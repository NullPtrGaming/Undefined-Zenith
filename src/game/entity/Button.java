// represents a button, to be placed in a menu array with a specific function 

package game.entity;

public class Button {
	
	public static final int STANDARD_BUTTON_HEIGHT = 32; 
	public static final int STANDARD_BUTTON_WIDTH = 192; 
	
	private int x; 
	private int y; 
	private int w; 
	private int h; 
	private String name; 
	private boolean isSelected = false; 
	
	// constructor, allows for most customization 
	public Button (int x, int y, int w, int h, String name) {
		this.x = x; 
		this.y = y; 
		this.w = w; 
		this.h = h; 
		this.name = name; 
	}
	
	// also constructor, uses standard sizes 
	public Button (int x, int y, String name) {
		this.x = x; 
		this.y = y; 
		this.w = STANDARD_BUTTON_WIDTH; 
		this.h = STANDARD_BUTTON_HEIGHT; 
		this.name = name; 
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
