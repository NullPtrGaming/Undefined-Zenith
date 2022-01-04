// Houses main game logic methods and stores main game data 

package game;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*; 
import org.lwjgl.opengl.GL11;

import game.entity.*; 

public class GameLogic {
	public static final int SPRITE_OFFSET = 16; 
	public static final int PRIMARY_PLAYER = 0; 
	public static final boolean PLAYER_TRUE = true; 
	public static final int MENU_COOLDOWN = 250; 
	public static final int ENEMY_COOLDOWN = 5000; 
	public static final int MAX_ENTITIES = 16; 
	
	private static int gameState = 0; // THE GAME'S MAIN STATE VARIABLE - 0=TITLE, 1=MENU, 2=RUNNING 
	private static long gameTime; 
	private static long pauseTime; 
	private static long pauseTimer; 
	private static long menuCooldownTimer; 
	private static long enemyCooldownTimer; 
	private static long gameWindow; 
	private static boolean isFullscreen = false; 
	
	private static Player[] playerList; // Stores all game players - referenced by indices 
	private static ArrayList<Entity> entityList; 
	private static ArrayList<Projectile> projectileList; 
	private static boolean[] keyStates = new boolean[7]; 
	private static int[] newKeys = new int[7]; 
	private static int newKeysIndex = 0; 
	private static boolean rebinding = false; 
	
	private static ArrayList<Button> titleButtonList; // Stores buttons for menus - alternate game states 
	private static ArrayList<Button> menuButtonList; 
	private static ArrayList<Button> optionsButtonList; 
	private static ArrayList<Button> tempButtonList = null; 
	private static boolean isOptions = false; 
	
	private static MenuInputHandler input; 
	private static Input keyPressHandler; 
	
	// Initializes game logic, including key states and the entity lists 
	public static void gameInit (long window) {  
		gameWindow = window; 
		menuCooldownTimer = System.currentTimeMillis() + MENU_COOLDOWN; 
		enemyCooldownTimer = System.currentTimeMillis(); 
		pauseTimer = System.currentTimeMillis(); 
		playerList = new Player[4]; 
		entityList = new ArrayList<Entity> (); 
		projectileList = new ArrayList<Projectile> (); 
		titleButtonList = new ArrayList<Button> (); 
		menuButtonList = new ArrayList<Button> (); 
		optionsButtonList = new ArrayList<Button> (); 
		playerList[PRIMARY_PLAYER] = new Player(0, 0, 1, 10, 2, 500, Entity.ATTACK_PROJECTILE, true, true, keyStates); 
		keyPressHandler = new Input(window, false, keyStates); // Key callbacks set 
		input = new MenuInputHandler(keyStates); 
		initMenus(); 
		GameSaver.loadGame(GameSaver.getExpectedSaveLocation()); 
	} 
	
	// fullscreen toggle 
	public static void toggleFullscreen () {
		long monitor = GLFW.glfwGetPrimaryMonitor(); 
		GLFWVidMode monitorMode = GLFW.glfwGetVideoMode(monitor); 
		if (isFullscreen) { // sets windowed 
			GLFW.glfwSetWindowMonitor(gameWindow, 0, (monitorMode.width() - 512) / 2, (monitorMode.height() - 288) / 2, 512, 288, monitorMode.refreshRate()); 
			isFullscreen = false; 
		}
		else { // sets fullscreen 
			GLFW.glfwSetWindowMonitor(gameWindow, monitor, 0, 0, monitorMode.width(), monitorMode.height(), monitorMode.refreshRate()); 
			isFullscreen = true; 
		}
		GLFW.glfwSwapInterval(1); 
	}
	public static boolean getFullscreenState() {
		return isFullscreen; 
	}
	
	// sets button lists with buttons 
	public static void initMenus () {
		titleButtonList.add(new Button(-96, 0, "Play")); 
		titleButtonList.add(new Button(-96, -48, "Settings")); 
		titleButtonList.add(new Button(-96, -96, "Exit")); 
		
		menuButtonList.add(new Button(-96, 48, "Resume")); 
		menuButtonList.add(new Button(-96, -16, "Settings")); 
		menuButtonList.add(new Button(-96, -80, "Save and Exit")); 
		
		optionsButtonList.add(new Button(-96, 96, "Exit")); 
		optionsButtonList.add(new Button(-96, 60, "Rebind Keys")); 
		optionsButtonList.add(new Button(-96, 24, "Select = Attack")); 
		optionsButtonList.add(new Button(-96, -12, "Fullscreen")); 
	} 
	
	// is the options menu open? 
	public static boolean getOptionState () {
		return isOptions; 
	}
	// toggles the options menu 
	public static void toggleOptions () {
		if (!isOptions) {
			if (gameState == 0) {
				tempButtonList = titleButtonList; 
				titleButtonList = optionsButtonList; 
			}
			else if (gameState == 1) {
				tempButtonList = menuButtonList; 
				menuButtonList = optionsButtonList;
			}
			isOptions = true; 
		}
		else {
			if (gameState == 0) {
				titleButtonList = tempButtonList; 
			}
			else if (gameState == 1) {
				menuButtonList = tempButtonList; 
			}
			isOptions = false; 
		}
	}
	
	// gets the menu input handler 
	public static MenuInputHandler getInput () { 
		return input; 
	}
	// gets the overall input (key callback)  handler 
	public static Input getKeyCallbacks () {
		return keyPressHandler; 
	}
	
	// sets keys 
	public static void setKeysInit () { 
		newKeysIndex = 0; 
		rebinding = true; 
	}
	public static void setKeysPoll () { 
		if (rebinding && getMenuCooldownState()) { 
			boolean cancel = false; 
			for (int i=0; i<newKeys.length; i++) {
				cancel = (newKeys[i] == Input.getLastKey()); 
				if (cancel) 
					break; 
			}
			if (!cancel) {
				newKeys[newKeysIndex] = Input.getLastKey(); 
				newKeysIndex++; 
			} 
			if (newKeysIndex >= newKeys.length) { 
				int[] keybinds = Input.getKeybinds(); 
				for (int i=0; i<newKeys.length; i++) { 
					keybinds[i] = newKeys[i]; 
				}
				rebinding = false; 
			}
		}
	}
	public static boolean isRebinding () {
		return rebinding; 
	}
	
	// gets/sets the main game state - important 
	public static int getState () { 
		return gameState; 
	}
	public static void setState (int state) { 
		int lastState = gameState; 
		gameState = state; 
		if (state == 2)
			startTime(); 
		else 
			stopTime(); 
		if (state == 1 && lastState == 2) 
			GameSaver.saveGame(GameSaver.getExpectedSaveLocation()); 
	}
	
	// Updates all entities and player movements 
	public static void updateEntities () {
		playerList[PRIMARY_PLAYER].pollMovement(); 
		for (Entity e : entityList) {
			e.pollMovement(); 
		}
		for (Projectile p : projectileList) {
			p.pollMovement(); 
		}
		removeDeadEntities(); 
		newEntity(); 
	}
	
	// Enemy generator method - operates on cooldown system 
	public static void newEntity () {
		if (entityList.size() <= MAX_ENTITIES && gameTime - enemyCooldownTimer >= ENEMY_COOLDOWN) {
			enemyCooldownTimer = gameTime; 
			int[] coords; 
			for (int i=3; i>0; i--) {
				coords = genCoordinates(); 
				entityList.add(new Entity(coords[0], coords[1], 50, 10, 1, 1000, Entity.ATTACK_PHYSICAL, true)); 
				if (testEntityIntersect(entityList.get(entityList.size()-1)) == null && Math.abs(coords[0] - playerList[PRIMARY_PLAYER].getX()) > 16 && Math.abs(coords[1] - playerList[PRIMARY_PLAYER].getY()) > 16) 
					return; 
				else 
					entityList.remove(entityList.size()-1); 
			}
		}
	}
	
	// Generates a random coordinate pair in a 2-position int array 
	public static int[] genCoordinates () {
		int[] coords = new int[2]; 
		coords[0] = (int)(Math.random()*256); 
		if (Math.random() < 0.5) 
			coords[0] *= -1; 
		coords[1] = (int)(Math.random()*144); 
		if (Math.random() < 0.5) 
			coords[1] *= -1; 
		return coords; 
	}
		
	// gets the main player 
	public static Player getMainPlayer () { 
		return playerList[PRIMARY_PLAYER]; 
	}
	
	// Returns the referenced entity - referenced by index and which array to get from (true identifies the Player array) 
	public static Entity getEntity (int index, boolean isPlayer) {
		if (isPlayer)
			return playerList[index]; 
		return entityList.get(index); 
	}
	// Gets the referenced Projectile 
	public static Projectile getProjectile (int index) {
		return projectileList.get(index); 
	}
	
	// Adds a new projectile to the projectile list 
	public static void createProjectile (int x, int y, int speed, int direction, int damage, boolean friendly, Entity owner) { 
		projectileList.add(new Projectile(x, y, speed, direction, damage, friendly, owner)); 
	} 
	
	// returns the number of game entities (does not count players) 
	public static int numEntities () {
		return entityList.size(); 
	}
	// same thing but projectiles 
	public static int numProjectiles () {
		return projectileList.size(); 
	}
	// returns the number of buttons (dependent on menu state) 
	public static int numButtons () { 
		if (gameState == 0)
			return titleButtonList.size(); 
		else if (gameState == 1)
			return menuButtonList.size(); 
		else 
			return -1; 
	}
	// returns the button referenced by index dependent on game state 
	public static Button getButton (int index) { 
		if (gameState == 0)
			return titleButtonList.get(index); 
		else if (gameState == 1) 
			return menuButtonList.get(index); 
		else 
			return null; 
	}
	// returns true if the game can enter a menu state (must have cooldown to prevent spamming) 
	public static boolean getMenuCooldownState () {
		if (menuCooldownTimer - System.currentTimeMillis() <= 0) {
			menuCooldownTimer = System.currentTimeMillis() + MENU_COOLDOWN; 
			return true; 
		}
		return false; 
	}
	
	// tests collisions between entities using their Rectangle2D collision boxes 
	public static Entity testEntityIntersect (Entity entity) {
		for (Entity e : entityList) {
			if (!e.equals(entity) && e.getCollisionBox().intersects(entity.getCollisionBox())) {
				return e; 
			}
		}
		if (entity.getCooldown() != -1) {
			for (int i=0; i<projectileList.size(); i++) {
				Projectile p = projectileList.get(i); 
				if (!(p.getX() <= Entity.MAX_X-4 && p.getY() <= Entity.MAX_Y-4 && p.getX() >= -1*Entity.MAX_X && p.getY() >= -1*Entity.MAX_Y)) { // destroys projectiles that leave the screen 
					projectileList.remove(i); 
					i--; 
				}
				else if (!p.getOwner().equals(entity) && !p.equals(entity) && p.getCollisionBox().intersects(entity.getCollisionBox())) { // actually checks projectile collision 
					entity.healthModify(-1*p.getDamage());
					projectileList.remove(i); 
					i--; 
					if (p.getOwner() == (Entity)playerList[PRIMARY_PLAYER]) 
						playerList[PRIMARY_PLAYER].scoreAdd(100); 
					System.out.println(playerList[PRIMARY_PLAYER].getScore()); 
				}
			}
		}
		return null; 
	}
	
	// removes Entities that have non-positive health values from the list (they are dead) 
	public static void removeDeadEntities () {
		for (int i=0; i<entityList.size(); i++) {
			if (entityList.get(i).getHealth() <= 0) {
				entityList.remove(i); 
				i--; 
				playerList[PRIMARY_PLAYER].scoreAdd(500); 
			}
		}
	}
	
	// Sets up screen for a new area - returns true if successful 
	public static boolean newArea (boolean move) {
		return move; 
	}
	
	// returns window handle but actually accessible to things 
	public static long getWindow () {
		return gameWindow; 
	}
	
	// important method, gets a game-specific time value 
	public static long getTime () { 
		return gameTime; 
	}
	// updates time 
	public static void updateTime () { 
		gameTime = System.currentTimeMillis() - pauseTime; 
	}
	public static void startTime () { // restarts the standard timer - game unpaused 
		pauseTime += (System.currentTimeMillis() - pauseTimer); 
	}
	public static void stopTime () { // pauses standard timer - game paused 
		pauseTimer = System.currentTimeMillis(); 
	}
	
}

