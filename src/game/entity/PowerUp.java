// represents the game powerups, they do not extend Entity but will have collision boxes for the Player only 

package game.entity;

import java.awt.geom.Rectangle2D;

import game.GameLogic;
import game.TextureLoader; 

public class PowerUp {
	
	private static int[] textureArray = new int[3]; 

	private int x; 
	private int y; 
	private int type; 
	private Rectangle2D.Float collisionBox = new Rectangle2D.Float(); 
	
	public PowerUp (int x, int y, int type) { 
		this.x = x; 
		this.y = y; 
		this.type = type; 
		updateCollisionBox(); 
	} 
	
	public static void loadTextures (TextureLoader textureLoader) {
		textureArray[0] = textureLoader.loadTexture("res/PowerUps/Heart.png"); 
		textureArray[1] = textureLoader.loadTexture("res/PowerUps/RapidFire.png"); 
		textureArray[2] = textureLoader.loadTexture("res/PowerUps/GoldHeart.png"); 
	}
	public static int[] getTextures () {
		return textureArray; 
	}
	
	public int getX () {
		return x; 
	}
	public int getY () {
		return y; 
	}
	public int getType () {
		return type; 
	}
	public Rectangle2D getCollisionBox () {
		return collisionBox; 
	}
	private void updateCollisionBox () {
		collisionBox.setRect((float)x/Entity.MAX_X, (float)y/Entity.MAX_Y, Entity.BOX_WIDTH, Entity.BOX_HEIGHT); 
	}
	
	// does the PowerUp action based on type 
	public void doAction (Player p) {
		switch (type) {
		case 0: {
			p.healthModify(10); 
		}
		break; 
		case 1: {
			p.rapidFireStart(2, 10000); 
		}
		break; 
		case 2: {
			if (GameLogic.getMainPlayer().getHealth() < GameLogic.getMainPlayer().getOriginalHealth()) { 
				GameLogic.getMainPlayer().healthModify(GameLogic.getMainPlayer().getOriginalHealth()-GameLogic.getMainPlayer().getHealth()); 
			} 
			if (GameLogic.getPlayer2().getHealth() < GameLogic.getPlayer2().getOriginalHealth()) { 
				GameLogic.getPlayer2().healthModify(GameLogic.getPlayer2().getOriginalHealth()-GameLogic.getPlayer2().getHealth()); 
			} 
			GameLogic.toggleEntities(); 
			GameLogic.newArea(); 
		}
		}
	}
	
}
