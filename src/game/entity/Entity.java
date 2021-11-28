// Represents a game entity. Used as a base for players, enemies, etc. 

package game.entity;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float; 
import game.GameLogic;
import game.Input; 

public class Entity {
	
	public static final int MAX_X = 256; 
	public static final int MAX_Y = 144; 
	public static final float BOX_WIDTH = (float)1/16; 
	public static final float BOX_HEIGHT = (float)1/9; 
	
	// attack types, for different Entities 
	public static final int ATTACK_PHYSICAL = 0; 
	public static final int ATTACK_PROJECTILE = 1; 
		
	
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
		cooldownTimer = GameLogic.getTime() + cooldown; 
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
	
	public void setX (int x) {
		this.x = x; 
	}
	public void setY (int y) {
		this.y = y; 
	}
	// Changes health by the given value 
	public void healthModify (int h) {
		health += h;  
	}
	// sets attack type through variables 
	public void setAttackType (int type) {
		attackType = type; 
	}
	
	// gets the Rectangle2D used for collision detection 
	public Rectangle2D getCollisionBox () {
		return collisionBox; 
	}
	
	// sets the facing direction, used for texture rendering 
	public void setDirection (int x, int y) {
		if (y > 0)
			direction = Input.UP; 
		else if (y < 0)
			direction = Input.DOWN; 
		if (x > 0) 
			direction = Input.RIGHT; 
		else if (x < 0) 
			direction = Input.LEFT; 
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
		if (this.x <= MAX_X-16 && this.y <= MAX_Y-16 && this.x >= -1*MAX_X && this.y >= -1*MAX_Y) { 
			this.x += x; 
			this.y += y; 
			updateCollisionBox(); 
			Entity e = GameLogic.testEntityIntersect(this); 
			if (e != null) 
				while (e.getCollisionBox().intersects(this.getCollisionBox())) { // in progress - rework involves better collisions teleporting to edges of colliding objects 
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
		move(0, 0); 
	}
	
	// generates projectiles and deals damage respectively 
	public void pollAttack () {
		if (cooldownTimer - GameLogic.getTime() <= 0) {
			if (attackType == ATTACK_PHYSICAL) 
				GameLogic.testEntityIntersect(this); 
			else if (attackType == ATTACK_PROJECTILE) 
				genProjectile(); 
			cooldownTimer = GameLogic.getTime() + cooldown; 
		}
		
	}  
	
	// Generates a Projectile at the location of the Entity in their direction 
	public void genProjectile () {
		GameLogic.createProjectile(x+8, y+8, speed*3, direction, damage, false, this); 
	}
}

