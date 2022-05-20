// Represents a main (user-controlled) player of the game 
package game.entity;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;

import game.*; 

public class Player extends Entity {

	private boolean[] keyStates; 
	private boolean isPrimary = false; 
	private int score; 
	private int originalCooldown; 
	private int originalHealth; 
	private long powerUpTimer = 0; 
	private int powerUpDuration = 0; 
	private int texture = 0; 
	private int texture1 = 0; 
	
	private int acceleration = 2; // currently testing 
	private int tempSpeed = 0; 
	
	private Rectangle2D attackCollisionBox; // only for physical attack 
	
	private static int[] playerTextureArray = new int[4]; 
	private static ArrayList<Integer> playerTypeTextureList = new ArrayList<Integer> (); 
	
	// Constructor, use Entity constructor but allows the Player to reference key states 
	public Player (int x, int y, int health, int damage, int speed, int cooldown, int type, int texture, boolean rotatable, boolean isPrimary, boolean[] keyStates) {
		super(x, y, health, damage, speed, cooldown, type, rotatable, texture); 
		this.keyStates = keyStates; 
		this.isPrimary = isPrimary; 
		this.originalCooldown = getCooldown(); 
		this.originalHealth = getHealth(); 
		this.texture = texture; 
		score = 0; 
		attackCollisionBox = new Rectangle2D.Float (); 
	}
	
	// textures 
	public static void loadTextures (TextureLoader textureLoader) {
		playerTextureArray[0] = textureLoader.loadTexture("res/Characters/Jay/Jay Spaceship.png"); 
		playerTextureArray[1] = textureLoader.loadTexture("res/Characters/Iona/Iona Spaceship.png"); 
		playerTextureArray[2] = textureLoader.loadTexture("res/Characters/Jay/Jay.png"); 
		playerTextureArray[3] = textureLoader.loadTexture("res/Characters/Iona/Iona.png"); 
	}
	public static int[] getTextures () {
		return playerTextureArray; 
	}
	
	// Overrides the entity movement, utilizes key states 
	public void pollMovement () {
		if (GameLogic.getState() > 1 && (isPrimary || GameLogic.isCharacterPlayer2(this))) { 
			Entity e = GameLogic.testEntityIntersect(this); // this is the cause of the startup death bug 
			if (e != null && GameLogic.getTime() - e.getCooldownTimer() >= e.getCooldown()) { 
				healthModify(-e.getDamage()); 
				GameLogic.newEffect(this.getX(), this.getY(), 0, 20); 
				GameLogic.startShake(3); 
				e.cooldownReset(); 
			}
			rapidFirePoll(); 
			if (getHealth() <= 0) { // death checking 
				GameLogic.setState(1); 
				setCooldown(originalCooldown); 
				GameLogic.gameOver(); 
			} 
			if (keyStates[Input.ATTACK]) {
				if (getAttackType() == Entity.ATTACK_PROJECTILE)
					pollAttack(); 
				else { 
					pollAttackPhysical(); 
				}
			}
			if (keyStates[Input.UP]) 
				move(0, getSpeed()); 
			if (keyStates[Input.DOWN]) 
				move(0, -1*getSpeed()); 
			if (keyStates[Input.LEFT])
				move(-1*getSpeed(), 0); 
			if (keyStates[Input.RIGHT])
				move(getSpeed(), 0); 
			if (keyStates[Input.ESCAPE] && GameLogic.getMenuCooldownState())
				GameLogic.setState(1); 
			if (!keyStates[Input.ESCAPE] && Math.random() >= 0.9) 
				genProjectileTrail(); 
			if (this.getAttackType() == Entity.ATTACK_PHYSICAL) 
				Input.resetAttackStatePoll(); 
		}
		else { 
			if (!GameLogic.isRebinding() && GameLogic.getState() == 1 && keyStates[Input.ESCAPE] && GameLogic.getMenuCooldownState()) 
				GameLogic.setState(2); 
			if (keyStates[Input.UP] && GameLogic.getMenuCooldownState())
				GameLogic.getInput().changeSelection(false); 
			if (keyStates[Input.DOWN] && GameLogic.getMenuCooldownState()) 
				GameLogic.getInput().changeSelection(true); 
			if (keyStates[Input.SELECT] && GameLogic.getMenuCooldownState())
				GameLogic.getInput().select(); 
		}
	}
	
	// adds to score 
	public void scoreAdd (int add) {
		score += add; 
		GameLogic.setHighScore(score); 
		GameLogic.addTempScore(add); 
	}
	public int getScore () { 
		return score; 
	}
	
	// set direction override 
	public void setDirection (int x, int y) {
		if (y > 0)
			setDirection(Input.UP); 
		else if (y < 0)
			setDirection(Input.DOWN); 
		if (x > 0) 
			setDirection(Input.RIGHT); 
		else if (x < 0) 
			setDirection(Input.LEFT); 
	}
	
	// generates projectile trail 
	public void genProjectileTrail() { 
		int offsetX = 0; 
		int offsetY = 0; 
		if (getDirectionTrue() == Input.LEFT) 
			offsetX = 16; 
		else if (getDirectionTrue() == Input.RIGHT) 
			offsetX = -16; 
		else if (getDirectionTrue() == Input.UP) 
			offsetY = -16; 
		else 
			offsetY = 16; 
		GameLogic.newEffect(this.getX()+offsetX, this.getY()+offsetY, 1, 10); 
	}
	
	// power-up - changes the fire rate for a period of time 
	public void rapidFireStart (int modifier, int duration) {
		powerUpTimer = GameLogic.getTime(); 
		powerUpDuration = duration; 
		setCooldown(getCooldown()/modifier); 
	}
	public void rapidFirePoll () {
		if (getCooldown() != originalCooldown && GameLogic.getTime() - powerUpTimer >= powerUpDuration) {
			setCooldown(originalCooldown); 
		} 
	} 
	
	public void setAttackCollisions () { // sets attack collision rectangle, only used for physical attack 
		attackCollisionBox.setRect(((float)getX()-28)/MAX_X, ((float)getY()-28)/MAX_Y, (float)72/MAX_X, (float)72/MAX_Y); 
	}
	public Rectangle2D getAttackCollisions () {
		return attackCollisionBox; 
	}
	
	public void pollAttackPhysical () { 
		setAttackCollisions(); 
		if (GameLogic.getTime() - getCooldownTimer() >= getCooldown()*2) {
			setCooldownTimer(GameLogic.getTime()); 
			ArrayList<Entity> hitEnemies = GameLogic.testPhysicalAttackIntersect(); 
			if (GameLogic.isBoss() && GameLogic.getBoss().getCollisionBox().intersects(this.getAttackCollisions())) {
				hitEnemies.add(GameLogic.getBoss()); 
			}
			for (Entity e : hitEnemies) {
				e.healthModify(-this.getDamage()); 
				if (!e.equals(GameLogic.getBoss())) 
					GameLogic.newEffect(e.getX(), e.getY(), 0, 20); 
				else {
					Boss b = (Boss)e; 
					GameLogic.newEffect(e.getX()+(int)(b.getW()*128)-8, e.getY()+(int)(b.getH()*72)-8, 0, 20);
				} 
				GameLogic.startShake(2); 
			}
		}
	} 
	public boolean isAttacking () {
		return keyStates[Input.ATTACK]; 
	}
	
	// sets primary player status (for switching) 
	public void makePrimary (boolean primary) {
		isPrimary = primary; 
	}
	public boolean isPrimary () {
		return isPrimary; 
	}
	
	// sets which set of key states the Player uses 
	public void setKeystates (boolean[] keyStates) {
		this.keyStates = keyStates; 
	}
	
	public int getOriginalHealth () {
		return originalHealth; 
	}
	
	public int getTexture () { // for human textures 
		if (GameLogic.getLevelType() == 1) 
			return texture1; 
		return texture; 
	}
	public void setAltTexture (int texture) {
		texture1 = texture; 
	}
	public int getAltTexture () {
		return texture1; 
	}
	
	// accelerates - not use normal speed variable (testing) 
	public void accelerate (boolean isNegative) {
		if (isNegative) 
			tempSpeed -= acceleration; 
		else 
			tempSpeed += acceleration; 
	}
	
}
