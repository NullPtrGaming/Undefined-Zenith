// Class for storing basic data about the location, lifespan, and type of special effects / particles 
package game.entity;

import game.TextureLoader;

public class Effect {

	public static final int EFFECT_EXPLOSION = 0; // particle/effect types, more to be added? 
	
	private static int[] effectTextureList = new int[2]; 
	
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
	public void pollDuration() {
		duration--; 
	}
	
	// textures 
	public static void loadTextures (TextureLoader textureLoader) {
		effectTextureList[0] = textureLoader.loadTexture("res/Explosion.png"); 
		effectTextureList[1] = textureLoader.loadTexture("res/Trail.png"); 
	}
	public static int[] getTextures () {
		return effectTextureList; 
	}
}
