// takes input 

package game;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import game.entity.Entity;

public class Input {
	
	// Universal key identifier indices 
	public static final int UP = 0; 
	public static final int DOWN = 1; 
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int ATTACK = 4; 
	public static final int SELECT = 5; // for menu navigation 
	public static final int ESCAPE = 6; 
	
	private static int[] keybinds = {GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_V, GLFW_KEY_B, GLFW_KEY_ESCAPE}; // player 1 
	private static int[] keybinds1 = {GLFW_KEY_UP, GLFW_KEY_DOWN, GLFW_KEY_LEFT, GLFW_KEY_RIGHT, GLFW_KEY_RIGHT_CONTROL, GLFW_KEY_RIGHT_SHIFT, GLFW_KEY_ESCAPE}; // player 2 
	// array for storing different bindings for different keys 
	
	private static int lastKey; 
	private static boolean lastAttackKeyState; 
	
	// Constructor, sets up GLFW key callback 
	public Input (long w, boolean isWASD, boolean[] keyStates, boolean[] keyStates1) {
		glfwSetKeyCallback(w, (window, key, scancode, action, mods) -> {
			for (int i=0; i<7; i++) {
				if (key == keybinds[i])
					keyStates[i] = (glfwGetKey(window, keybinds[i]) == GLFW_PRESS);  
			}
			for (int i=0; i<7; i++) {
				if (key == keybinds1[i])
					keyStates1[i] = (glfwGetKey(window, keybinds1[i]) == GLFW_PRESS);  
			}
			lastKey = key; 
			lastAttackKeyState = keyStates[ATTACK]; 
			GameLogic.setKeysPoll(); 
			});
	}
	
	// returns the keybinds array for modification 
	public static int[] getKeybinds () {
		return keybinds; 
	}
	
	// returns the last pressed key 
	public static int getLastKey () {
		return lastKey; 
	}
	// resets the attack indicator if the key is released 
	public static void resetAttackStatePoll () { 
		GameLogic.displayPhysicalAttack(lastAttackKeyState); 
	}
}

