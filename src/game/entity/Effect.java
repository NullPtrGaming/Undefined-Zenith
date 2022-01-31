// Class for storing basic data about the location, lifespan, and type of special effects / particles 
package game.entity;

public class Effect {

	public static final int EFFECT_EXPLOSION = 0; // particle/effect types, more to be added? 
	
	private int x; 
	private int y; 
	private int type; 
	private int duration; // in frames 
	
	public Effect (int x, int y, int type, int duration) {
		this.x = x; 
		this.y = y; 
		this.type = type; 
		this.duration = duration; 
	}
	
	public int getX () {
		return x; 
	}
	public int getY() {
		return y; 
	}
	public int getType() {
		return type; 
	}
	public int getDuration() {
		return duration; 
	}
}
