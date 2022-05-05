// Houses main game logic methods and stores main game data 

package game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*; 
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.EXTOffset;

import game.entity.*; 

public class GameLogic {
	public static final int SPRITE_OFFSET = 16; 
	public static final int PRIMARY_PLAYER = 0; 
	public static final boolean PLAYER_TRUE = true; 
	public static final int MENU_COOLDOWN = 250; 
	public static final int ENEMY_COOLDOWN = 5000; 
	public static final int MAX_ENTITIES = 16; 
	public static final int[] POSITION_NODE_ARRAY = {240, 110}; 
	
	private static int gameState = 0; // THE GAME'S MAIN STATE VARIABLE - 0=TITLE, 1=MENU, 2=RUNNING 
	private static long gameTime; 
	private static long pauseTime; 
	private static long pauseTimer; 
	private static long menuCooldownTimer; 
	private static long enemyCooldownTimer; 
	private static long gameWindow; 
	private static boolean isFullscreen = false; 
	private static int highScore = 0; 
	
	private static Player[] playerList; // Stores all game players - referenced by indices 
	private static ArrayList<Entity> entityList; 
	private static ArrayList<Projectile> projectileList; 
	private static ArrayList<PowerUp> powerUpList; 
	private static ArrayList<Obstacle> obstacleList; 
	private static Boss currentBoss = null; 
	private static boolean isBoss = false; 
	private static int bossCounter = 15; // counts enemies before boss generation // temporarily small for testing 
	private static int bossCounterTemp = 0; 
	private static int deadEnemyCount = 0; 
	private static ArrayList<Player> playerTypeList; // not the player list, this is for types of players (characters) 
	private static int currentPlayerIndex = 0; 
	private static Entity[] physicalEnemyTypeList = {
			new Entity(0, 0, 25, 10, 1, 500, Entity.ATTACK_PHYSICAL, true, 1), 
			new Entity(0, 0, 5, 10, 2, 300, Entity.ATTACK_PHYSICAL, true, 2) // moves faster 
	}; 
	private static Entity[] projEnemyTypeList = {
			new Entity(0, 0, 15, 10, 1, 500, Entity.ATTACK_PROJECTILE, true, 0), 
			new Entity(0, 0, 5, 10, 1, 200, Entity.ATTACK_PROJECTILE, true, 3) // fires faster 
	}; 
	private static Boss[] bossTypeList = {
			new Boss(-16, 16, 300, 10, 1, 750, Entity.ATTACK_PROJECTILE, true, 0, Boss.DEFAULT_BOSS_W, Boss.DEFAULT_BOSS_H, 0), 
			new Boss(-16, 16, 250, 15, 1, 1200, Entity.ATTACK_PROJECTILE, true, 0, Boss.DEFAULT_BOSS_W, Boss.DEFAULT_BOSS_H, 1) 
	}; 
	
	private static int tempScore = 0; 
	private static int tempScoreHealth = 0; 
	private static double enemyHealthMod = 1; 
	private static double enemyDamageMod = 1; 
	private static double enemyGenMod = 1; 
	private static double enemySpeedMod = 1; 
	
	private static boolean[] keyStates = new boolean[7]; 
	private static int[] newKeys = new int[7]; 
	private static int newKeysIndex = 0; 
	private static boolean rebinding = false; 
	private static int reboundButton = -1; 
	private static boolean rebindSuccess = false; 
	
	private static ArrayList<Button> titleButtonList; // Stores buttons for menus - alternate game states 
	private static ArrayList<Button> menuButtonList; 
	private static ArrayList<Button> optionsButtonList; 
	private static ArrayList<Button> gameOverButtonList; 
	private static ArrayList<Button> rebindButtonList; 
	private static ArrayList<Button> currentButtonList; 
	private static int menuIndex = 0; 
	private static boolean wasGameOver = false; 
	
	private static MenuInputHandler input; 
	private static Input keyPressHandler; 
	
	private static ArrayList<Effect> effectList = new ArrayList<Effect> (); 
	private static int[] shakeOffsetCoordinates = new int[2]; 
	private static int shakeFrames = 0; 
	private static int maxShake; 
	private static int damageFrames = 0; 
	private static boolean physicalAttackState = false; 
	
	private static int[] soundList = new int[8]; 
	
	private static int testNumber = 0; 
	
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
		gameOverButtonList = new ArrayList<Button> (); 
		rebindButtonList = new ArrayList<Button> (); 
		powerUpList = new ArrayList<PowerUp> (); 
		obstacleList = new ArrayList<Obstacle> (); 
		playerTypeList = new ArrayList<Player> (); 
		playerList[PRIMARY_PLAYER] = loadPlayer(); 
		keyPressHandler = new Input(window, false, keyStates); // Key callbacks set 
		input = new MenuInputHandler(keyStates); 
		initMenus(); 
		GameSaver.loadGame(GameSaver.getExpectedSaveLocation()); 
		setState(0); 
	} 
	
	// loads a Player for starting the game 
	public static Player loadPlayer () {
		playerTypeList.add(new Player(0, 0, 50, 10, 2, 250, Entity.ATTACK_PROJECTILE, Player.getTextures()[0], true, true, keyStates)); 
		playerTypeList.add(new Player(0, 0, 70, 10, 2, 250, Entity.ATTACK_PHYSICAL, Player.getTextures()[1], true, false, keyStates)); 
		Player player; 
		File playerDir = new File(System.getProperty("user.home")+System.getProperty("file.separator")+"Saved Games"+System.getProperty("file.separator")+"UndefinedZenith"+System.getProperty("file.separator")+"Players"); 
		File listDir[] = playerDir.listFiles(); 
		if (listDir != null) 
			for (int i=0; i<listDir.length; i++) {
				int[] stats = new int[5]; 
				int texture = 0; 
				try {
					InputStream is = new FileInputStream(listDir[i]); // initial loading 
					ZipFile file = new ZipFile(listDir[i]); 
					ZipInputStream z = new ZipInputStream(is); 
					
					ZipEntry ze = z.getNextEntry(); // the text file (Player.txt) 
					InputStream internalIS = file.getInputStream(ze); 
					Scanner scan = new Scanner(internalIS); 
					for (int j=0; j<5; j++) {
						while (!scan.hasNextInt()) 
							scan.next(); 
						stats[j] = scan.nextInt(); 
					} 
					
					ze = z.getNextEntry(); // the image (Spaceship.png) 
					internalIS = file.getInputStream(ze); 
					TextureLoader tl = new TextureLoader(); 
					texture = tl.loadTexture(internalIS); 
				} catch (IOException e) {
					continue; 
				}  
				playerTypeList.add(new Player(0, 0, stats[0], stats[1], stats[2], stats[3], stats[4], texture, true, false, keyStates)); 
			} 
		player = playerTypeList.get(0); 
		return player; 
	}
	
	// switches loaded player 
	public static void setMainPlayer (int index) {
		playerTypeList.get(currentPlayerIndex).makePrimary(false); 
		playerTypeList.get(index).makePrimary(true); 
		playerList[0] = playerTypeList.get(index); 
		currentPlayerIndex = index; 
	}
	public static int getPlayerIndex () {
		return currentPlayerIndex; 
	}
	public static int numCharacters () {
		return playerTypeList.size(); 
	}
	
	// gets effects 
	public static ArrayList<Effect> getEffects () { 
		return effectList; 
	}
	public static void newEffect (int x, int y, int type, int duration) {
		effectList.add(new Effect(x, y, type, duration)); 
	}
	
	// gets a specific sound id 
	public static int getSound (int index) {
		return soundList[index]; 
	}
	public static void playSound (int index) {
		AL10.alSourcePlay(soundList[index]); 
	}
	public static void stopSound (int index) {
		AL10.alSourceStop(soundList[index]); 
	}
	public static void playMusic (int index) { // specifically for music switching, stops all other musics 
		for (int i=0; i<3; i++) 
			stopSound(i); 
		playSound(index); 
	}
	public static void loadSounds() {
		String[] pathList = {"res/Audio/01 Spaceflight.ogg", "res/Audio/02 Zenith.ogg", "res/Audio/03 Undefined.ogg", "res/Audio/s_01 Laser.ogg", "res/Audio/s_02 Damage.ogg"}; 
		for (int i=0; i<pathList.length; i++) {
			int buffer = AL10.alGenBuffers(); 
			// load audio files 
			try (STBVorbisInfo info = STBVorbisInfo.malloc()) { 
	            // Copy to buffer
				ShortBuffer pcm = AudioLoader.readVorbis(pathList[i], 1048576, info); 
	            AL10.alBufferData(buffer, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate()); 
			} catch (Exception e) {
				continue; 
			} 
			soundList[i] = AL10.alGenSources(); 
			AL10.alSourcei(soundList[i], AL10.AL_BUFFER, buffer); 
			if (i <= 2) 
				AL10.alSourcei(soundList[i], AL10.AL_LOOPING, AL10.AL_TRUE); 
		}
	}
	
	// for debugging 
	public static int getTestNumber () {
		return testNumber; 
	}
	
	// power-ups 
	public static void newPowerUp (int x, int y) {
		int type = (int)(Math.random()*2); 
		powerUpList.add(new PowerUp(x, y, type)); 
	}
	public static PowerUp getPowerUp (int index) {
		return powerUpList.get(index); 
	}
	public static int numPowerUps () {
		return powerUpList.size(); 
	} 
	
	public static void pollPowerUps () {
		for (int i=0; i<numPowerUps(); i++) {
			if (powerUpList.get(i).getCollisionBox().intersects(getMainPlayer().getCollisionBox())) {
				powerUpList.get(i).doAction(); 
				newEffect(powerUpList.get(i).getX(), powerUpList.get(i).getY(), 1, 20); 
				powerUpList.remove(i); 
				i--; 
			}
		}
	}
	
	// obstacle handling 
	public static void newObstacle (int x, int y, int type) {
		obstacleList.add(new Obstacle(x, y, type)); 
	}
	public static Obstacle testObstacleIntersect (Entity e) {
		Obstacle target = null; 
		for (Obstacle o : obstacleList) {
			if (o.getCollisionBox().intersects(e.getCollisionBox())) {
				target = o; 
			}
		}
		return target; 
	}
	public static Obstacle getObstacle (int index) {
		return obstacleList.get(index); 
	}
	public static int numObstacles () {
		return obstacleList.size(); 
	}
	
	// initializes a new set of Obstacles after a Boss - resets all character positions 
	public static void newArea () {
		obstacleList = new ArrayList<Obstacle> (); 
		int numObstacles = (int)(Math.random()*4)+5; 
		for (int i=0; i<numObstacles; i++) { 
			int[] coords = genCoordinates(); 
			if (Math.abs(coords[0]) < 32 || Math.abs(coords[1]) < 32) 
				continue; 
			else 
				newObstacle(coords[0], coords[1], (int)(Math.random()*1)); 
		}
		for (Player p : playerTypeList) {
			p.setX(0); 
			p.setY(0); 
		}
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
		GLFW.glfwSwapInterval(monitorMode.refreshRate()/60); 
		GameSaver.saveGame(GameSaver.getExpectedSaveLocation()); 
	}
	public static boolean getFullscreenState() {
		return isFullscreen; 
	}
	
	// sets button lists with buttons 
	public static void initMenus () {
		titleButtonList.add(new Button(-16, 0, "PLAY", 1)); 
		titleButtonList.add(new Button(-16, -48, "SETTINGS", 2)); 
		titleButtonList.add(new Button(-16, -96, "EXIT", 3)); 
		
		menuButtonList.add(new Button(-16, 48, "RESUME", 1)); 
		menuButtonList.add(new Button(-16, -16, "SETTINGS", 2)); 
		menuButtonList.add(new Button(-16, -80, "EXIT", 3)); 
		
		optionsButtonList.add(new Button(-16, 96, "EXIT", 1)); 
		optionsButtonList.add(new Button(-16, 60, "REBIND KEYS", 4)); 
		optionsButtonList.add(new Button(-16, 24, "SELECT ATTACK", 6)); 
		optionsButtonList.add(new Button(-16, -12, "FULLSCREEN", 5)); 
		optionsButtonList.add(new Button(-16, -48, "CHARACTER", 7)); 
		
		gameOverButtonList.add(new Button(-16, 0, "GAME OVER", 3)); 
		
		String[] buttonNames = {"UP", "DOWN", "LEFT", "RIGHT", "ATTACK", "SELECT", "ESCAPE"}; 
		
		for (int i=1; i<8; i++) 
			rebindButtonList.add(new Button(-16, 108-(36*(i-1)), buttonNames[i-1], 4)); 
		rebindButtonList.add(new Button(-16, -144, "EXIT", 1)); 
		
		currentButtonList = titleButtonList; 
	} 
	
	// 0=title 1=pause 2=options 3=rebind 4=game over 
	public static void setMenu (int index) { 
		menuIndex = index; 
		switch (index) {
		case 0: 
			currentButtonList = titleButtonList; 
			break; 
		case 1: 
			currentButtonList = menuButtonList; 
			break; 
		case 2: 
			currentButtonList = optionsButtonList; 
			break; 
		case 3: 
			currentButtonList = rebindButtonList; 
			break; 
		case 4: 
			currentButtonList = gameOverButtonList; 
			break; 
		}
	}
	public static int getMenuIndex () {
		return menuIndex; 
	}
		
	// handles the player's death/game reset 
	public static void gameOver () {
		menuCooldownTimer = System.currentTimeMillis() + MENU_COOLDOWN; 
		setMenu(4); 
		wasGameOver = true; 
	}
	public static boolean getWasGameOver () {
		return wasGameOver; 
	}
	public static void postGameOver () {
		setState(0); 
		setMenu(0); 
		wasGameOver = false; 
	}
	
	// gets high score 
	public static int getHighScore () {
		return highScore; 
	}
	// updates the high score if the given score is higher 
	public static void setHighScore (int score) {
		if (score > highScore) 
			highScore = score; 
	}
	public static void addTempScore (int add) {
		tempScore += add; 
		tempScoreHealth += add; 
		if (shouldAddScoreHealth()) 
			getMainPlayer().healthModify(10); 
	}
	public static boolean shouldAddScoreHealth () {
		boolean result = tempScoreHealth >= 10000; 
		if (result) 
			tempScoreHealth = 0; 
		return result; 
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
	public static void setKeysPoll () { 
		if (rebinding && getMenuCooldownState()) {
			rebindSuccess = rebindKey(reboundButton, Input.getLastKey()); 
		}
	}
	public static void setKeys (int selected) {
		rebinding = true; 
		reboundButton = selected; 
	}
	// directly changes a key 
	public static boolean rebindKey (int index, int key) {
		int[] keybinds = Input.getKeybinds(); 
		boolean cancel = false; 
		for (int i=0; i<keybinds.length; i++) {
			if (keybinds[i] == key) 
				cancel = true; 
		}
		if (!cancel) 
			keybinds[index] = key; 
		rebinding = false; 
		return !cancel; 
	}
	
	public static boolean isRebinding () {
		return rebinding; 
	}
	public static boolean wasRebindSuccess () { 
		return rebindSuccess; 
	} 
	
	// gets/sets the main game state - important - 0=TITLE, 1=MENU, 2=RUNNING 
	public static int getState () { 
		return gameState; 
	}
	public static void setState (int state) { 
		int lastState = gameState; 
		gameState = state; 
		if (state == 2) {
			startTime(); 
			playMusic(2); 
		} 
		else 
			stopTime(); 
		if (lastState == 2 || lastState == 1) // game saving automatic after pausing or unpausing game 
			GameSaver.saveGame(GameSaver.getExpectedSaveLocation()); 
		if (state == 0) { // clears game upon exit to title 
			currentBoss = null; 
			setBossState(); 
			entityList = new ArrayList<Entity> (); 
			projectileList = new ArrayList<Projectile> (); 
			setMenu(0); 
			tempScore = 0; 
			deadEnemyCount = 0; 
			bossCounterTemp = 0; 
			if (wasGameOver) {// only for death reset 
				int i=0; 
				for (Player player : playerTypeList) { 
					playerTypeList.set(i, new Player(0, 0, player.getOriginalHealth(), player.getDamage(), player.getSpeed(), player.getCooldown(), player.getAttackType(), player.getTexture(), true, player.isPrimary(), keyStates)); 
					setMainPlayer(currentPlayerIndex); 
					i++; 
				} 
			} 
			playMusic(0); 
		}
		else if (state == 1) 
			setMenu(1); 
		if (lastState == 0) {
			newArea(); 
		}
	}
	
	// Updates all entities and player movements 
	public static void updateEntities () {
		playerList[PRIMARY_PLAYER].pollMovement(); 
		for (Entity e : entityList) {
			e.pollMovement(); 
		}
		if (currentBoss != null) 
			currentBoss.pollMovement(); 
		for (Projectile p : projectileList) {
			p.pollMovement(); 
		}
		pollPowerUps(); 
		removeDeadEntities(); 
		updateDifficulty(false); 
		newEntity(); 
	}
	
	// updates the game's difficulty modifiers 
	public static void updateDifficulty (boolean isDamage) {
		if (isDamage) {
			tempScore -= 300; 
		}
		double mod = 1; 
		try {
			mod = 4/(1+(Math.pow(Math.E, -0.15*(double)tempScore/10000))) - 1; 
		}
		catch (Exception e) {
			return; 
		}
		enemyHealthMod = mod; 
		enemyDamageMod = mod; 
		enemyGenMod = mod; 
		enemySpeedMod = mod; 
	}
	
	// Enemy generator method - operates on cooldown system 
	public static void newEntity () {
		if (!isBoss && entityList.size() <= MAX_ENTITIES && gameTime - enemyCooldownTimer >= (ENEMY_COOLDOWN/enemyGenMod)) {
			enemyCooldownTimer = gameTime; 
			int[] coords; 
			for (int i=3; i>0; i--) {
				coords = genCoordinates(); 
				Entity e; 
				if (Math.random() >= 0.4) { 
					e = physicalEnemyTypeList[(int)(Math.random()*physicalEnemyTypeList.length)].copy(); 
				} 
				else { 
					e = projEnemyTypeList[(int)(Math.random()*projEnemyTypeList.length)].copy(); 
				} 
				e.setX(coords[0]); 
				e.setY(coords[1]); 
				e.setHealth((int)(e.getHealth()*enemyHealthMod)); 
				e.setDamage((int)(e.getDamage()*enemyDamageMod)); 
				e.setCooldown((int)(e.getCooldown()/enemySpeedMod)); 
				entityList.add(e); 
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
		coords[0] = (int)(Math.random()*240); 
		if (Math.random() < 0.5) 
			coords[0] *= -1; 
		coords[1] = (int)(Math.random()*128); 
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
	public static void removeProjectile (int index) {
		projectileList.remove(index); 
	}
	public static void removeProjectile (Projectile p) {
		projectileList.remove(p); 
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
		if (gameState == 0 || gameState == 1)
			return currentButtonList.size(); 
		else 
			return -1; 
	}
	// returns the button referenced by index dependent on game state 
	public static Button getButton (int index) { 
		if (gameState == 0 || gameState == 1) 
			return currentButtonList.get(index); 
		else 
			return null; 
	}
	
	// generates boss 
	public static void genBoss () {
		entityList = new ArrayList<Entity> (); 
		currentBoss = bossTypeList[(int)(Math.random()*bossTypeList.length)].copy(); 
		setBossState(); 
		currentBoss.setHealth((int)(currentBoss.getHealth()*enemyHealthMod)); 
		currentBoss.setDamage((int)(currentBoss.getDamage()*enemyDamageMod)); 
		currentBoss.setCooldown((int)(currentBoss.getCooldown()/enemySpeedMod)); 
	}
	// gets current boss 
	public static Boss getBoss () {
		return currentBoss; 
	}
	// gets current boss state 
	public static boolean isBoss () {
		return isBoss; 
	}
	public static void setBossState () { 
		isBoss = (currentBoss != null); 
		if (isBoss) 
			playMusic(1); 
		else 
			playMusic(2); 
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
				if (GameLogic.testObstacleIntersect(p) != null) 
					GameLogic.removeProjectile(i); 
				if (!(p.getX() <= Entity.MAX_X-4 && p.getY() <= Entity.MAX_Y-4 && p.getX() >= -1*Entity.MAX_X && p.getY() >= -1*Entity.MAX_Y)) { // destroys projectiles that leave the screen 
					projectileList.remove(i); 
					i--; 
				}
				else if (!p.getOwner().equals(entity) && !p.equals(entity) && p.getCollisionBox().intersects(entity.getCollisionBox())) { // actually checks projectile collision 
					entity.healthModify(-1*p.getDamage()); 
					GameLogic.newEffect(entity.getX(), entity.getY(), 0, 20);
					startShake(2); 
					projectileList.remove(i); 
					i--; 
					if (p.getOwner() == (Entity)playerList[PRIMARY_PLAYER]) 
						playerList[PRIMARY_PLAYER].scoreAdd(100); 
				}
			}
		}
		return null; 
	}
	
	public static ArrayList<Entity> testPhysicalAttackIntersect () { // physical attack 
		ArrayList<Entity> hitEnemies = new ArrayList<Entity> (); 
		for (Entity e : entityList) {
			if (e.getCollisionBox().intersects(getMainPlayer().getAttackCollisions())) {
				hitEnemies.add(e); 
			}
		} 
		return hitEnemies; 
	}
	public static void displayPhysicalAttack (boolean value) { // displays the physical attack bubble thing 
		physicalAttackState = value; 
	}
	public static boolean getPhysicalAttackState () {
		return physicalAttackState; 
	}
	
	// removes Entities that have non-positive health values from the list (they are dead) 
	public static void removeDeadEntities () { 
		for (int i=0; i<entityList.size(); i++) {
			if (entityList.get(i).getHealth() <= 0) {
				if (Math.random() >= 0.9) 
					newPowerUp(entityList.get(i).getX(), entityList.get(i).getY()); 
				entityList.remove(i); 
				i--; 
				playerList[PRIMARY_PLAYER].scoreAdd(500); 
				startShake(3); 
				deadEnemyCount++; 
				if (bossCounterTemp < bossCounter) {
					bossCounterTemp++; 
				} 
				else {
					bossCounterTemp = 0; 
					genBoss(); 
				}
			}
		}
		if (isBoss() && currentBoss.getHealth() <= 0) { 
			powerUpList.add(new PowerUp(currentBoss.getX()+8, currentBoss.getY()+8, 2)); 
			currentBoss = null; 
			setBossState(); 
			getMainPlayer().scoreAdd(1000); 
		} 
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
	
	// randomizes the values in the shake offset array in a small range 
	public static void shakeScreen () {
		shakeOffsetCoordinates[0] = (int)(Math.random()*maxShake); 
		if (Math.random() >= 0.5) 
			shakeOffsetCoordinates[0] *= -1; 
		shakeOffsetCoordinates[1] = (int)(Math.random()*maxShake); 
		if (Math.random() >= 0.5) 
			shakeOffsetCoordinates[1] *= -1; 
	}
	public static void startShake(int max) { 
		maxShake = max; 
		shakeFrames = 20; 
	} 
	public static void pollShake () { 
		if (shakeFrames <= 0) 
			stopShake(); 
		else { 
			shakeScreen(); 
			shakeFrames--; 
		}
	}
	public static void stopShake () {
		shakeOffsetCoordinates[0] = 0; 
		shakeOffsetCoordinates[1] = 0; 
	}
	public static int[] getShake () { // for renderer 
		return shakeOffsetCoordinates; 
	}
	
	// adds damage (red screen) frames 
	public static void addDamageFrames (int f) { 
		damageFrames += f; 
	}
	public static int getDamageFrames () {
		return damageFrames; 
	}
}

