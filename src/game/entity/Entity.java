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
	private static int[] entityTextureList = new int[2]; 
	
	// known enemy positions 
	private int lastPlayerX = 0; 
	private int lastPlayerY = 0; 
	
	// position variables 
	private int x; 
	private int y; 
	
	// status variables 
	private int health; 
	private int damage; 
	private int speed; 
	private int direction; 
	private int cooldown; 
	private boolean rotatable; // determines if the texture can be rotated 
	private int attackType = ATTACK_PHYSICAL; 
	
	private long cooldownTimer; // for determining cooldown times 
	private long moveCooldownTimer; 
	
	// collision box 
	Rectangle2D collisionBox; 
	
	// Constructor, initializes basic entity values 
	public Entity (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable) {
		this.x = x; 
		this.y = y; 
		this.health = health;  
		this.damage = damage; 
		this.speed = speed; 
		this.cooldown = cooldown; 
		this.rotatable = rotatable; 
		collisionBox = new Rectangle2D.Float((float)x/MAX_X, (float)y/MAX_Y, BOX_WIDTH, BOX_HEIGHT); 
		setAttackType(type); 
		cooldownTimer = GameLogic.getTime(); 
		moveCooldownTimer = GameLogic.getTime(); 
	}
	
	// class-based textures 
	public static void loadTextures (TextureLoader textureLoader) {
		entityTextureList[0] = textureLoader.loadTexture("res/Enemies/Searchlight.png"); 
		entityTextureList[1] = textureLoader.loadTexture("res/Enemies/Alien.png"); 
	}
	public static int[] getTextures () {
		return entityTextureList; 
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
	}
	public void setY (int y) {
		this.y = y; 
	}
	// changes speed by given value 
	public void accelerate (int acceleration) {
		speed += acceleration; 
	}
	// Changes health by the given value 
	public void healthModify (int h) {
		health += h;  
		if (this == GameLogic.getEntity(0, true) && h < 0) {
			//GameLogic.addDamageFrames(20); 
		}
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
		if (this != GameLogic.getEntity(0, true) && this.getCollisionBox().intersects(GameLogic.getMainPlayer().getCollisionBox())) {
			if ((GameLogic.getTime() - this.getCooldownTimer() >= this.getCooldown())) {
				GameLogic.getMainPlayer().healthModify(-this.getDamage());
				GameLogic.newEffect(GameLogic.getMainPlayer().getX(), GameLogic.getMainPlayer().getY(), 0, 20); 
				GameLogic.startShake(3); 
				this.cooldownReset(); 
			}
			return; 
		} 
		if (this != GameLogic.getEntity(0, true)) { 
			if (Math.abs(this.x - lastPlayerX) <= 5) 
				this.x -= x; 
			if (Math.abs(this.y - lastPlayerY) <= 5) 
				this.y -= y; 
		} 
		if (this.x <= MAX_X-16 && this.y <= MAX_Y-16 && this.x >= -1*MAX_X && this.y >= -1*MAX_Y) { 
			this.x += x; 
			this.y += y; 
			updateCollisionBox(); 
			Entity e = GameLogic.testEntityIntersect(this); 
			while (e != null) {
				while (e.getCollisionBox().intersects(this.getCollisionBox())) { // in progress - rework involves better collisions teleporting to edges of colliding objects 
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
					if (x > 0) { 
						this.x -= 1; 
					} 
					else if (x < 0){
						this.x += 1; 
					}
					if (y > 0) { 
						this.y -= 1; 
					}
					else if (y < 0){
						this.y += 1; 
					}
					updateCollisionBox(); 
					e.updateCollisionBox(); 
				}
			updateCollisionBox(); 
			e = GameLogic.testEntityIntersect(this); 
			}
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
		setDirection(x, y); 
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
	
	// generates projectiles and deals damage respectively 
	public void pollAttack () {
		if (GameLogic.getTime() - cooldownTimer >= cooldown*2) { 
			cooldownTimer = GameLogic.getTime(); 
			if (attackType == ATTACK_PROJECTILE) 
				genProjectile(); 
		}
	}
	public int getAttackType () {
		return attackType; 
	}
	
	// Generates a Projectile at the location of the Entity in their direction 
	public void genProjectile () {
		GameLogic.createProjectile(x+8, y+8, speed*3, direction, damage, false, this); 
	}
}

