// represents a button, to be placed in a menu array with a specific function 

package game.entity;

import game.TextureLoader;

public class Button {
	
	public static final int STANDARD_BUTTON_HEIGHT = 32; 
	public static final int STANDARD_BUTTON_WIDTH = 32; 
	
	// textures 
	private static int[] buttonTextureList = new int[8]; 
	
	private int x; 
	private int y; 
	private int w; 
	private int h; 
	private String name; 
	private int texture; 
	private boolean isSelected = false; 
	
	// constructor, allows for most customization 
	public Button (int x, int y, int w, int h, String name, int texture) {
		this.x = x; 
		this.y = y; 
		this.w = w; 
		this.h = h; 
		this.texture = texture; 
		this.name = name; 
	}
	
	// also constructor, uses standard sizes 
	public Button (int x, int y, String name, int texture) { 
		this.x = x; 
		this.y = y; 
		this.w = STANDARD_BUTTON_WIDTH; 
		this.h = STANDARD_BUTTON_HEIGHT; 
		this.texture = texture; 
		this.name = name; 
	}
	
	// textures 
	public static void loadTextures(TextureLoader textureLoader) {
		buttonTextureList[0] = textureLoader.loadTexture("res/Play.png"); 
		buttonTextureList[1] = textureLoader.loadTexture("res/Gear.png"); 
		buttonTextureList[2] = textureLoader.loadTexture("res/X.png"); 
	}
	public static int[] getTextures () {
		return buttonTextureList; 
	}
	
	public int getTexture () {
		return texture; 
	}
	
	// gets x position
	public int getX () {
		return x; 
	}
	// y 
	public int getY () {
		return y; 
	}
	
	// gets width 
	public int getWidth () {
		return w; 
	}
	// height 
	public int getHeight () {
		return h; 
	}
	
}
