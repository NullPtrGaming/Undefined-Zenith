// represents a non-moving, collision-detecting obstacle the size of an Entity 

package game.entity;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import game.GameLogic;
import game.TextureLoader;

public class Obstacle {
	private static int[] obstacleTextureList = new int[3]; 
	private static int[] obstacleTextureList1 = new int[3]; 
	
	private int x; 
	private int y; 
	private int texture; 
	Rectangle2D collisionBox = new Rectangle2D.Float(); 
	
	// simple constructor, self-explanatory 
	public Obstacle (int x, int y, int texture) {
		this.x = x; 
		this.y = y; 
		this.texture = texture; 
		updateCollisionBox(); 
	}
	// self-explanatory methods 
	public int getX () {
		return x; 
	}
	public int getY () {
		return y; 
	}
	public int getTexture () {
		return texture; 
	}
	public Rectangle2D getCollisionBox () {
		return collisionBox; 
	}
	// updates collisions 
	private void updateCollisionBox () {
		collisionBox.setRect((float)x/Entity.MAX_X, (float)y/Entity.MAX_Y, Entity.BOX_WIDTH, Entity.BOX_HEIGHT); 
	}
	// gets/loads textures 
	public static void loadTextures (TextureLoader tl) {
		obstacleTextureList[0] = tl.loadTexture("res/Obstacles/Asteroid.png"); 
		obstacleTextureList[1] = tl.loadTexture("res/Obstacles/Space Station.png"); 
		obstacleTextureList[2] = tl.loadTexture("res/Obstacles/Planet.png"); 
		
		obstacleTextureList1[0] = obstacleTextureList[0]; 
	}
	public static int[] getTextures () {
		if (GameLogic.getLevelType() == 1) {
			return obstacleTextureList1; 
		}
		return obstacleTextureList; 
	}
}
