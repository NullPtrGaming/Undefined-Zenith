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
	
	// Constructor, sets up GLFW key callback 
	public Input (long w, boolean isWASD, boolean[] keyStates) {
		if (isWASD) {
			glfwSetKeyCallback(w, (window, key, scancode, action, mods) -> {
				if (key == GLFW_KEY_SPACE) 
					keyStates[ATTACK] = (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS); 
				if (key == GLFW_KEY_W) 
					keyStates[UP] = (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS); 
				if (key == GLFW_KEY_S)
					keyStates[DOWN] = (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS); 
				if (key == GLFW_KEY_A)
					keyStates[LEFT] = (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS); 
				if (key == GLFW_KEY_D)
					keyStates[RIGHT] = (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS); 
				if (key == GLFW_KEY_ENTER) 
					keyStates[SELECT] = (glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS); 
			});
		}
		else 
			glfwSetKeyCallback(w, (window, key, scancode, action, mods) -> {
				if (key == GLFW_KEY_SPACE) 
					keyStates[ATTACK] = (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS); 
				if (key == GLFW_KEY_UP) 
					keyStates[UP] = (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS); 
				if (key == GLFW_KEY_DOWN)
					keyStates[DOWN] = (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS); 
				if (key == GLFW_KEY_LEFT)
					keyStates[LEFT] = (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS); 
				if (key == GLFW_KEY_RIGHT)
					keyStates[RIGHT] = (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS); 
				if (key == GLFW_KEY_ENTER) 
					keyStates[SELECT] = (glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS);
			});
	}
}

