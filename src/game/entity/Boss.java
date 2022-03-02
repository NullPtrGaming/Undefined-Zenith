// represents game bosses (big Entities that are harder to destroy) 

package game.entity;

import game.GameLogic;
import game.TextureLoader;

public class Boss extends Entity {
	
	public static final float DEFAULT_BOSS_W = (float)32/256; 
	public static final float DEFAULT_BOSS_H = (float)32/144; 
	
	private float w; 
	private float h; 
	private int attackPattern; 
	private int spCooldown; 
	private long spCooldownTimer; 
	
	private static int[] bossTextureList = new int[4]; 
	
	public Boss (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, int texture, float w, float h, int ap) { 
		super(x, y, health, damage, speed, cooldown, type, rotatable, texture); 
		this.w = w; 
		this.h = h; 
		this.attackPattern = ap; 
		spCooldown = cooldown * 4; 
		spCooldownTimer = GameLogic.getTime(); 
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
		
	}
	public void pollAttack () { // actual attack method 
		
	}
	public void specialAttack () {
		
	}
	
	public Boss copy () { 
		return new Boss(getX(), getY(), getHealth(), getDamage(), getSpeed(), getCooldown(), getAttackType(), true, getTexture(), w, h, attackPattern); 
	}
}
