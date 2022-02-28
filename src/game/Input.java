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
	
	private static int[] keybinds = {GLFW_KEY_W, GLFW_KEY_S, GLFW_KEY_A, GLFW_KEY_D, GLFW_KEY_LEFT, GLFW_KEY_DOWN, GLFW_KEY_ESCAPE}; 
	// array for storing different bindings for different keys 
	
	private static int lastKey; 
	private static boolean lastAttackKeyState; 
	
	// Constructor, sets up GLFW key callback 
	public Input (long w, boolean isWASD, boolean[] keyStates) {
		glfwSetKeyCallback(w, (window, key, scancode, action, mods) -> {
			if (key == keybinds[ATTACK]) 
				keyStates[ATTACK] = (glfwGetKey(window, keybinds[ATTACK]) == GLFW_PRESS); 
			if (key == keybinds[UP]) 
				keyStates[UP] = (glfwGetKey(window, keybinds[UP]) == GLFW_PRESS); 
			if (key == keybinds[DOWN])
				keyStates[DOWN] = (glfwGetKey(window, keybinds[DOWN]) == GLFW_PRESS); 
			if (key == keybinds[LEFT])
				keyStates[LEFT] = (glfwGetKey(window, keybinds[LEFT]) == GLFW_PRESS); 
			if (key == keybinds[RIGHT])
				keyStates[RIGHT] = (glfwGetKey(window, keybinds[RIGHT]) == GLFW_PRESS); 
			if (key == keybinds[SELECT]) 
				keyStates[SELECT] = (glfwGetKey(window, keybinds[SELECT]) == GLFW_PRESS); 
			if (key == keybinds[SELECT]) 
				keyStates[SELECT] = (glfwGetKey(window, keybinds[SELECT]) == GLFW_PRESS);
			if (key == keybinds[ESCAPE]) 
				keyStates[ESCAPE] = (glfwGetKey(window, keybinds[ESCAPE]) == GLFW_PRESS);
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

