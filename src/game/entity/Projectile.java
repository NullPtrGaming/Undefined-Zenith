// Represents a projectile, a smaller entity used in attacks for determining accuracy 
package game.entity;

import java.awt.geom.Rectangle2D;

import game.TextureLoader; 
import game.GameLogic;
import game.Input;

public class Projectile extends Entity {

	// projectile-specific data 
	
	public static final float PROJ_BOX_WIDTH = (float)1/64; 
	public static final float PROJ_BOX_HEIGHT = (float)1/36; 
	
	private static int[] projectileTextureList = new int[2]; 
	
	private boolean isPlayerFriendly; 
	private Entity owner; 
	
	// constructor, sets projectile-specific data 
	public Projectile (int x, int y, int speed, int direction, int damage, boolean friendly, Entity owner) {
		super(x, y, 1, damage, speed, -1, Entity.ATTACK_PHYSICAL, true); 
		setDirection(direction); 
		isPlayerFriendly = friendly; 
		this.owner = owner; 
		updateCollisionBox(); 
	}
	
	// texture stuff 
	public static void loadTextures (TextureLoader textureLoader) {
		projectileTextureList[0] = textureLoader.loadTexture("res/Projectiles/Friendly Projectile.png"); 
		projectileTextureList[1] = textureLoader.loadTexture("res/Projectiles/Enemy Projectile.png"); 
	}
	public static int[] getTextures () {
		return projectileTextureList; 
	}
	
	// important so projectiles don't damage their creators 
	public Entity getOwner () {
		return owner; 
	}
	
	// projectiles use different collision boxes 
	private void updateCollisionBox () {
		collisionBox.setRect((float)getX()/MAX_X, (float)getY()/MAX_Y, PROJ_BOX_WIDTH, PROJ_BOX_HEIGHT); 
	}
	
	// overrides the pollmovement, allows for projectile direction 
	public void pollMovement () { 
		switch (getDirection()) {
		case Input.UP: 
			move(0, getSpeed()); 
			break; 
		case Input.LEFT: 
			move(-1*getSpeed(), 0); 
			break; 
		case Input.DOWN: 
			move(0, -1*getSpeed()); 
			break; 
		default: 
			move(getSpeed(), 0); 
		}
	}
	
	// overrides the move method to fix projectile-specific bugs 
	public void move (int x, int y) {
		setX(getX() + x); 
		setY(getY() + y); 
		updateCollisionBox(); 
		GameLogic.testEntityIntersect(this); 
		updateCollisionBox(); 
	}
}
