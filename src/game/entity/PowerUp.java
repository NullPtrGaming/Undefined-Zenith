// represents the game powerups, they do not extend Entity but will have collision boxes for the Player only 

package game.entity;

import java.awt.geom.Rectangle2D; 

public class PowerUp {

	private int x; 
	private int y; 
	private int type; 
	private Rectangle2D.Float collisionBox; 
	
	public PowerUp (int x, int y, int type) { 
		this.x = x; 
		this.y = y; 
		this.type = type; 
	} 
	
}
