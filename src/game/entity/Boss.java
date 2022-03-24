// represents game bosses (big Entities that are harder to destroy) 

package game.entity;

import java.awt.geom.Rectangle2D;

import game.GameLogic;
import game.TextureLoader;

public class Boss extends Entity {
	
	public static final float DEFAULT_BOSS_W = (float)32/256; 
	public static final float DEFAULT_BOSS_H = (float)32/144; 
	public static final int[] BOSS_NODES = {
		-16, 16, 200, 110 	
	}; 
	
	private float w; 
	private float h; 
	private int attackPattern; 
	private int spCooldown; 
	private long spCooldownTimer; 
	private int moveCooldown; 
	private Rectangle2D collisionBox = new Rectangle2D.Float(); 
	
	private static int[] bossTextureList = new int[4]; 
	
	public Boss (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, int texture, float w, float h, int ap) { 
		super(x, y, health, damage, speed, cooldown, type, rotatable, texture); 
		this.w = w; 
		this.h = h; 
		this.attackPattern = ap; 
		spCooldown = cooldown * 4; 
		spCooldownTimer = GameLogic.getTime(); 
		moveCooldown = (int)(cooldown*3.5); 
		updateCollisionBox(); 
	}
	
	public static void loadTextures (TextureLoader tl) {
		bossTextureList[0] = tl.loadTexture("res/Enemies/Bosses/477 Drone Fighter.png"); 
	}
	public static int[] getTextures () {
		return bossTextureList; 
	}

	public float getW () { // basic accessors 
		return w; 
	}
	public float getH () {
		return h; 
	}
	
	public void pollMovement () {
		for (int i=0; i<GameLogic.numProjectiles(); i++) {
			if (GameLogic.getProjectile(i).getOwner() == GameLogic.getEntity(0, true) && collisionBox.intersects(GameLogic.getProjectile(i).getCollisionBox())) {
				healthModify(-GameLogic.getProjectile(i).getDamage()); 
				GameLogic.newEffect(GameLogic.getProjectile(i).getX(), GameLogic.getProjectile(i).getY(), 0, 20); 
				GameLogic.startShake(2); 
				GameLogic.removeProjectile(i); 
				GameLogic.getMainPlayer().scoreAdd(100); 
			}
		}
		if (GameLogic.getMainPlayer().getAttackType() == ATTACK_PHYSICAL && GameLogic.getMainPlayer().getAttackCollisions().intersects(collisionBox)) { 
			healthModify(-GameLogic.getMainPlayer().getDamage()); 
			GameLogic.newEffect(getX(), getY(), 0, 20); 
			GameLogic.startShake(2); 
		} 
		if (isCooldown()) {
			pollAttack(); 
			setCooldownTimer(GameLogic.getTime()); 
		}
		move(); 
		updateCollisionBox(); 
	}
	public void pollAttack () { // actual attack method 
		for (int i=0; i<4; i++) {
			GameLogic.createProjectile(getX()+16, getY()+16, GameLogic.getMainPlayer().getSpeed()*3, i, getDamage(), false, this); 
		}
	}
	
	public void move() { 
		if (GameLogic.getTime() - getMoveCooldown() >= moveCooldown) { 
			setMoveCooldown(GameLogic.getTime());  
			if (Math.random() >= 0.9) {
				int[] coords = {GameLogic.getMainPlayer().getX(), GameLogic.getMainPlayer().getY()}; 
				setTargetCoords(coords); 
			}
			else {
				double targetNode = Math.random(); 
				if (targetNode <= 0.2) {
					int[] coords = {BOSS_NODES[0], BOSS_NODES[1]}; 
					setTargetCoords(coords); 
				}
				else if (targetNode <= 0.4) {
					int[] coords = {BOSS_NODES[2], BOSS_NODES[3]}; 
					setTargetCoords(coords); 
				}
				else if (targetNode <= 0.6) {
					int[] coords = {-BOSS_NODES[2], -BOSS_NODES[3]}; 
					setTargetCoords(coords); 
				}
				else if (targetNode <= 0.8) {
					int[] coords = {-BOSS_NODES[2], BOSS_NODES[3]}; 
					setTargetCoords(coords); 
				}
				else {
					int[] coords = {BOSS_NODES[2], -BOSS_NODES[3]}; 
					setTargetCoords(coords); 
				}
			} 
		} 
		if (getTargetCoords()[0] > getX()) {
			if (getTargetCoords()[1] > getY()) 
				move(getSpeed(), getSpeed()); 
			else 
				move(getSpeed(), -getSpeed()); 
		} 
		else {
			if (getTargetCoords()[1] > getY()) 
				move(-getSpeed(), getSpeed()); 
			else 
				move(-getSpeed(), -getSpeed()); 
		} 
	} 
	
	public void specialAttack () {
		
	}
	
	public void updateCollisionBox () { // overrides to Entity collisions 
		collisionBox.setRect((float)getX()/MAX_X, (float)getY()/MAX_Y, (float)32/MAX_X, (float)32/MAX_Y); 
	}
	public Rectangle2D getCollisionBox() {
		return collisionBox; 
	}
	
	public Boss copy () { 
		return new Boss(getX(), getY(), getHealth(), getDamage(), getSpeed(), getCooldown(), getAttackType(), true, getTexture(), w, h, attackPattern); 
	}
}
