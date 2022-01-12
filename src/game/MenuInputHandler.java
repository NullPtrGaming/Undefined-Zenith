// handles input in menus (Player objects do this in game [state 2]) 

package game;

import static org.lwjgl.glfw.GLFW.*; 

public class MenuInputHandler {

	private boolean[] keyStates; 
	
	private int selectedButton = 0; 
	
	// constructor, sets keystates 
	public MenuInputHandler (boolean keyStates[]) {
		this.keyStates = keyStates; 
	}
	
	// returns the selected button (for rendering) 
	public int getSelection () {
		return selectedButton; 
	}
	// changes selected button 
	public void changeSelection(boolean isIncrease) {
		if (isIncrease) 
			selectedButton++; 
		else 
			selectedButton--; 
		if (selectedButton >= GameLogic.numButtons())
			selectedButton = 0; 
		else if (selectedButton < 0) 
			selectedButton = GameLogic.numButtons()-1; 
	}
	// calls other methods based on the selected button 
	public void select () {
		if (GameLogic.getState() == 0 && !GameLogic.getOptionState()) 
			switch (selectedButton) {
		 	case 0: {
		 		GameLogic.setState(2); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 1: {
		 		GameLogic.toggleOptions(); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 2: {
		 		glfwSetWindowShouldClose(GameLogic.getWindow(), true); 
		 	}
		 	break; 
			}
		else if (GameLogic.getState() == 1 && !GameLogic.getOptionState() && !GameLogic.getWasGameOver()) 
			switch (selectedButton) {
		 	case 0: {
		 		GameLogic.setState(2); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 1: {
		 		GameLogic.toggleOptions(); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 2: {
		 		GameLogic.setState(0); 
		 		selectedButton = 0; 
		 	}
		 	break; 
			}
		else if (GameLogic.getOptionState()) {
			switch (selectedButton) {
			case 0: {
				GameLogic.toggleOptions(); 
				selectedButton = 0; 
			}
			break; 
			case 1: {
				GameLogic.setKeysInit(); 
				GameSaver.saveGame(); 
			}
			break; 
			case 2: {
				int[] keybinds = Input.getKeybinds(); 
				keybinds[Input.SELECT] = keybinds[Input.ATTACK]; 
				selectedButton = 0; 
			}
			break; 
			case 3: {
				GameLogic.toggleFullscreen(); 
				selectedButton = 0; 
			}
			break; 
			}
		}
		else if (GameLogic.getWasGameOver()) {
			switch (selectedButton) {
			case 0: {
				GameLogic.postGameOver(); 
			}
			break; 
			}
		}
		for (int i=0; i<keyStates.length; i++) 
			keyStates[i] = false; 
	}
}
