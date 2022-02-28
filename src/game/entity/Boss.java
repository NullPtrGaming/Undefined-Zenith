// represents game bosses (big Entities that are harder to destroy) 

package game.entity;

public class Boss extends Entity {
	
	
	
	public Boss (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, int texture) { 
		super(x, y, health, damage, speed, cooldown, type, rotatable, texture); 
	}

}
