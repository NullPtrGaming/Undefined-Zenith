// Represents a game entity. Used as a base for players, enemies, etc. 

package game.entity;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float; 
import game.GameLogic;
import game.Input;
import game.TextureLoader; 

public class Entity {
	
	public static final int MAX_X = 256; 
	public static final int MAX_Y = 144; 
	public static final float BOX_WIDTH = (float)1/16; 
	public static final float BOX_HEIGHT = (float)1/9; 
	
	// attack types, for different Entities 
	public static final int ATTACK_PHYSICAL = 0; 
	public static final int ATTACK_PROJECTILE = 1; 
	
	// texture list 
	private static int[] entityTextureList = new int[4]; 
	private static int[] entityTextureList1 = new int[4]; 
	private int texture; 
	
	// known enemy positions 
	private int lastPlayerX = 0; 
	private int lastPlayerY = 0; 
	
	// position variables 
	private int x; 
	private int y; 
	
	// last position change variables 
	private int lastXMove = 0; 
	private int lastYMove = 0; 
	
	// status variables 
	private int health; 
	private int damage; 
	private int speed; 
	private int direction; 
	private int cooldown; 
	private boolean rotatable; // determines if the texture can be rotated 
	private int attackType = ATTACK_PHYSICAL; 
	private double extraMovementX = 0; 
	private double extraMovementY = 0; 
	
	private long cooldownTimer; // for determining cooldown times 
	private long moveCooldownTimer; 
	
	// collision box 
	Rectangle2D collisionBox; 
	
	// Constructor, initializes basic entity values 
	public Entity (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, int texture) {
		this.x = x; 
		this.y = y; 
		this.health = health;  
		this.damage = damage; 
		this.speed = speed; 
		this.cooldown = cooldown; 
		this.rotatable = rotatable; 
		this.texture = texture; 
		collisionBox = new Rectangle2D.Float(); 
		setAttackType(type); 
		cooldownTimer = GameLogic.getTime(); 
		moveCooldownTimer = GameLogic.getTime(); 
		updateCollisionBox(); 
	}
	
	// class-based textures 
	public static void loadTextures (TextureLoader textureLoader) {
		entityTextureList[0] = textureLoader.loadTexture("res/Enemies/Searchlight.png"); 
		entityTextureList[1] = textureLoader.loadTexture("res/Enemies/Alien Ship.png"); 
		entityTextureList[2] = textureLoader.loadTexture("res/Enemies/Alien Ship Fast.png"); 
		entityTextureList[3] = textureLoader.loadTexture("res/Enemies/Searchlight Angry.png"); 
		
		entityTextureList1[0] = textureLoader.loadTexture("res/Enemies/Alien Robot.png"); 
		entityTextureList1[1] = textureLoader.loadTexture("res/Enemies/Alien.png"); 
		entityTextureList1[2] = textureLoader.loadTexture("res/Enemies/Alien Fast.png"); 
		entityTextureList1[3] = textureLoader.loadTexture("res/Enemies/Alien Angry.png"); 
	}
	public static int[] getTextures () {
		if (GameLogic.getLevelType() == 1) 
			return entityTextureList1; 
		return entityTextureList; 
	}
	
	// return specific texture for Entity 
	public int getTexture () {
		return texture; 
	}
	
	// returns position/status variables 
	public int getX () {
		return x; 
	}
	public int getY () { 
		return y; 
	}
	public int getSpeed () {
		return speed; 
	}
	public int getDirection() {
		if (GameLogic.getLevelType() == 1) {
			if (direction == 3 || direction == 1)
				return 5; 
			else 
				return 0; 
		} 
		return direction; 
	}
	public int getDirectionTrue() {
		return direction; 
	}
	public int getHealth () { 
		return health; 
	}
	public int getDamage () { 
		return damage; 
	}
	public int getCooldown () {
		return cooldown; 
	}
	public long getCooldownTimer () { 
		return cooldownTimer; 
	}
	
	public void setX (int x) {
		this.x = x; 
		updateCollisionBox(); 
	}
	public void setY (int y) {
		this.y = y; 
		updateCollisionBox(); 
	}
	// changes speed by given value 
	public void accelerate (int acceleration) {
		speed += acceleration; 
	}
	// Changes health by the given value 
	public void healthModify (int h) {
		health += h;  
		if (health < 0) 
			health = 0; 
		if (this == GameLogic.getEntity(0, true) && h < 0) {
			GameLogic.addDamageFrames(20); 
		}
		if (h <= 0) 
			GameLogic.playSound(4); 
		if (this.equals(GameLogic.getEntity(0, true))) 
			GameLogic.updateDifficulty(true); 
	}
	// health setting method, only to be used for initialization 
	public void setHealth (int h) {
		health = h; 
	}
	// same thing for damage 
	public void setDamage (int d) {
		damage = d; 
	}
	
	// sets attack type through variables 
	public void setAttackType (int type) {
		attackType = type; 
	}
	// resets attack/action cooldown 
	public void cooldownReset () {
		cooldownTimer = GameLogic.getTime(); 
	}
	public void setCooldown (int cooldown) {
		this.cooldown = cooldown; 
	}
	public void setCooldownTimer (long time) {
		cooldownTimer = time; 
	}
	public boolean isCooldown () { 
		return (GameLogic.getTime() - cooldownTimer >= cooldown*2); 
	} 
	
	// gets the Rectangle2D used for collision detection 
	public Rectangle2D getCollisionBox () {
		return collisionBox; 
	}
	
	// sets the facing direction, used for texture rendering 
	public void setDirection (int x, int y) {
			if (this.y - GameLogic.getMainPlayer().getY()*16 >= this.x - GameLogic.getMainPlayer().getX()*9 && Math.abs(this.y - GameLogic.getMainPlayer().getY()) > 5 && Math.abs(this.y - lastPlayerY) > 5) {
				if (y > 0)
					direction = Input.UP; 
				else if (y < 0)
					direction = Input.DOWN; 
			} 
			else if (Math.abs(this.x - GameLogic.getMainPlayer().getX()) > 5 && Math.abs(this.x - lastPlayerX) > 5) { 
				if (x > 0) 
					direction = Input.RIGHT; 
				else if (x < 0) 
					direction = Input.LEFT; 
			}
	}
	// alternate form using an existing direction 
	public void setDirection (int direction) {
		this.direction = direction; 
	}
	
	// sets the bounds of the collision rectangle 
	private void updateCollisionBox () {
		collisionBox.setRect((float)x/MAX_X, (float)y/MAX_Y, BOX_WIDTH, BOX_HEIGHT); 
	}
	
	// movement method, calls collision logic 
	public void move (int x, int y) {
		if (this != GameLogic.getEntity(0, true) && this.getCollisionBox().intersects(GameLogic.getMainPlayer().getCollisionBox())) { // checks initial collisions with Player 
			if ((GameLogic.getTime() - this.getCooldownTimer() >= this.getCooldown())) {
				GameLogic.getMainPlayer().healthModify(-this.getDamage());
				GameLogic.newEffect(GameLogic.getMainPlayer().getX(), GameLogic.getMainPlayer().getY(), 0, 20); 
				GameLogic.startShake(3); 
				this.cooldownReset(); 
			}
			return; 
		} 
		if (this != GameLogic.getEntity(0, true)) { // collisions - prevents things from going inside Player 
			if (Math.abs(this.x - lastPlayerX) <= 5) 
				this.x -= x; 
			if (Math.abs(this.y - lastPlayerY) <= 5) 
				this.y -= y; 
		} 
		if (this.x <= MAX_X-16 && this.y <= MAX_Y-16 && this.x >= -1*MAX_X && this.y >= -1*MAX_Y) { 
			int tempX = Math.abs(x), tempY = Math.abs(y); 
			while (tempX > 0 || tempY > 0) {
				updateCollisionBox(); 
				Entity e = GameLogic.testEntityIntersect(this); 
				if (this != GameLogic.getBoss() && GameLogic.isBoss() && this.collisionBox.intersects(GameLogic.getBoss().getCollisionBox())) { // boss damage 
					e = GameLogic.getBoss(); 
					if (GameLogic.getBoss().isCooldown()) { 
						this.healthModify(-GameLogic.getBoss().getDamage()/2); 
						GameLogic.newEffect((int)this.x, (int)this.y, 0, 20); 
						GameLogic.startShake(2); 
					} 
				}
				if (e != null) {
					if (e.getCollisionBox().intersects(this.getCollisionBox())) { 
						if (this != GameLogic.getEntity(0, true) && !this.getCollisionBox().intersects(GameLogic.getMainPlayer().getCollisionBox())) { 
							if (lastPlayerY > y) {
								this.y += speed; 
							} 
							else {
								this.y -= speed; 
							} 
						}
						else if (this == GameLogic.getEntity(0, true) && (GameLogic.getTime() - e.getCooldownTimer() >= e.getCooldown())) {
							healthModify(-e.getDamage()); 
							GameLogic.newEffect(GameLogic.getMainPlayer().getX(), GameLogic.getMainPlayer().getY(), 0, 20); 
							GameLogic.startShake(2); 
							e.cooldownReset(); 
						} 
						Entity tempE = e; 
						while (e == tempE) { 
							if (e.getX() > this.x) 
								this.x--; 
							else 
								this.x++; 
							if (e.getY() > this.y)
								this.y--; 
							else
								this.y++; 
							updateCollisionBox(); 
							e = GameLogic.testEntityIntersect(this); 
						}
					}
				}
				if (this != GameLogic.getBoss()) {
					Obstacle tempO = GameLogic.testObstacleIntersect(this); 
					Obstacle o = tempO; 
					if (this != GameLogic.getEntity(0, true))
						while (tempO != null && o == tempO) { 
							if (o.getX() > this.x) 
								this.x--; 
							else 
								this.x++; 
							if (o.getY() > this.y)
								this.y--; 
							else
								this.y++; 
							updateCollisionBox(); 
							o = GameLogic.testObstacleIntersect(this); 
						} 
					else {
						if (tempO != null && o == tempO) { 
							undoMove(); 
							return; 
						} 
					}
				}
				if (tempX > 0) {
					if (x > 0) {
						this.x++; 
						lastXMove = 1; 
					}
					else {
						this.x--; 
						lastXMove = -1; 
					} 
					tempX--; 
				}
				else {
					lastXMove = 0; 
				}
				if (tempY > 0) {
					if (y > 0) {
						this.y++; 
						lastYMove = 1; 
					}
					else {
						this.y--; 
						lastYMove = -1; 
					} 
					tempY--; 
				}
				else {
					lastYMove = 0; 
				}
				updateCollisionBox();
			}
		if (this.x > 240)
			this.x = -256; 
		else if (this.x < -1*MAX_X)
			this.x = 240; 
		else if (this.y > 128)
			this.y = -144; 
		else if (this.y < -1*MAX_Y)
			this.y = 128; 
		updateCollisionBox(); 
		setDirection((int)x, (int)y); 
	}
	}

	public void pollMovement () { 
		if (GameLogic.getTime() - moveCooldownTimer >= cooldown) { 
			moveCooldownTimer = GameLogic.getTime(); 
			if (Math.random() >= 0.5) {
				lastPlayerX = GameLogic.getMainPlayer().getX(); 
				lastPlayerY = GameLogic.getMainPlayer().getY(); 
			}
			else {
				if (Math.random() >= 0.5) 
					lastPlayerX = GameLogic.POSITION_NODE_ARRAY[0]; 
				else 
					lastPlayerX = -GameLogic.POSITION_NODE_ARRAY[0];
				if (Math.random() <= 0.5) 
					lastPlayerY = GameLogic.POSITION_NODE_ARRAY[1]; 
				else 
					lastPlayerY = -GameLogic.POSITION_NODE_ARRAY[1]; 
			} 
			if (Math.random() >= 0.9) { 
				lastPlayerX = this.x; 
				lastPlayerY = this.y; 
			} 
		} 
		
		if (lastPlayerX > x) {
			if (lastPlayerY > y)
				move(speed, speed); 
			else 
				move(speed, -speed); 
		} 
		else {
			if (lastPlayerY > y)
				move(-speed, speed); 
			else 
				move(-speed, -speed); 
		} 		
		pollAttack(); 
	}
	public long getMoveCooldown () {
		return moveCooldownTimer; 
	}
	public void setMoveCooldown (long time) {
		moveCooldownTimer = time; 
	}
	
	// resets last move 
	public void undoMove () {
		this.x -= lastXMove; 
		this.y -= lastYMove; 
		updateCollisionBox(); 
	}
	
	public int[] getTargetCoords () {
		int[] coords = {lastPlayerX, lastPlayerY}; 
		return coords; 
	}
	public void setTargetCoords (int[] coords) {
		lastPlayerX = coords[0]; 
		lastPlayerY = coords[1]; 
	}
	
	// generates projectiles and deals damage respectively 
	public void pollAttack () {
		if (GameLogic.getTime() - cooldownTimer >= cooldown*2) { 
			cooldownTimer = GameLogic.getTime(); 
			if (attackType == ATTACK_PROJECTILE) { 
				genProjectile(); 
				GameLogic.playSound(3); 
			}
		}
	}
	public int getAttackType () {
		return attackType; 
	}
	
	// Generates a Projectile at the location of the Entity in their direction 
	public void genProjectile () {
		GameLogic.createProjectile(x+8, y+8, speed*3, direction, damage, false, this); 
	}
	
	// returns a copy of the entity with same stats but "brand new" 
	public Entity copy () {
		return new Entity(0, 0, health, damage, speed, cooldown, attackType, rotatable, texture); 
	}
}

