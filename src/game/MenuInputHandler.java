// handles input in menus (Player objects do this in game [state 2]) 

package game;

import static org.lwjgl.glfw.GLFW.*; 

public class MenuInputHandler {

	private boolean[] keyStates; 
	
	private int selectedButton = 0; 
	private int currentCharacter; 
	private int currentCharacter1; 
	
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
		if (GameLogic.getState() == 0 && GameLogic.getMenuIndex() == 0) 
			switch (selectedButton) {
		 	case 0: {
		 		GameLogic.setMenu(5); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 1: {
		 		GameLogic.setMenu(2); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 2: {
		 		glfwSetWindowShouldClose(GameLogic.getWindow(), true); 
		 		
		 	}
		 	break; 
			}
		else if (GameLogic.getState() == 1 && GameLogic.getMenuIndex() == 1) 
			switch (selectedButton) {
		 	case 0: {
		 		GameLogic.setState(2); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 1: {
		 		GameLogic.setMenu(2); 
		 		selectedButton = 0; 
		 	}
		 	break; 
		 	case 2: {
		 		GameLogic.setState(0); 
		 		selectedButton = 0; 
		 	}
		 	break; 
			}
		else if (GameLogic.getMenuIndex() == 2) {
			switch (selectedButton) {
			case 0: {
				GameLogic.setMenu(GameLogic.getState()); 
				selectedButton = 0; 
			}
			break; 
			case 1: {
				//GameLogic.setKeysInit(); 
				GameLogic.setRebindCharacter(0); 
				GameLogic.setMenu(3); 
				GameSaver.saveGame(); 
			}
			break; 
			case 2: {
				GameLogic.setRebindCharacter(1); 
				GameLogic.setMenu(3); 
				GameSaver.saveGame(); 
			}
			break; 
			case 3: {
				int[] keybinds = Input.getKeybinds(); 
				int[] keybinds1 = Input.getKeybinds1(); 
				keybinds[Input.SELECT] = keybinds[Input.ATTACK]; 
				keybinds1[Input.SELECT] = keybinds1[Input.ATTACK]; 
				selectedButton = 0; 
			}
			break; 
			case 4: {
				GameLogic.toggleFullscreen(); 
				selectedButton = 0; 
			}
			break; 
			case 5: {
				currentCharacter = GameLogic.getPlayerIndex(); 
				currentCharacter1 = GameLogic.getPlayer2Index(); 
				if (currentCharacter < GameLogic.numCharacters()-1) {
					currentCharacter++;  
				} 
				else {
					currentCharacter = 0; 
				}
				if (currentCharacter == currentCharacter1) { // swaps the characters if they are about to be the same 
					GameLogic.setPlayer2(GameLogic.getPlayerIndex()); 
				}
				GameLogic.setMainPlayer(currentCharacter); 
			} 
			break; 
			case 6: {
				currentCharacter = GameLogic.getPlayerIndex(); 
				currentCharacter1 = GameLogic.getPlayer2Index(); 
				if (currentCharacter1 < GameLogic.numCharacters()-1) {
					currentCharacter1++;  
				} 
				else {
					currentCharacter1 = 0; 
				}
				if (currentCharacter == currentCharacter1) { // increments again if the characters are the same 
					if (currentCharacter1 < GameLogic.numCharacters()-1) {
						currentCharacter1++;  
					} 
					else {
						currentCharacter1 = 0; 
					}
				}
				GameLogic.setPlayer2(currentCharacter1); 
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
		else if (GameLogic.getMenuIndex() == 3) {
			switch (selectedButton) {
			case 7: { 
				GameLogic.setMenu(2); 
				selectedButton = 0; 
			} 
				break; 
			default: {
				if (!GameLogic.isRebinding()) {
					GameLogic.setKeys(selectedButton); 
				}
			}
			break; 
			}
		} 
		else if (GameLogic.getMenuIndex() == 5) {
			switch (selectedButton) {
			case 0: {
				GameLogic.setTwoPlayer(false); 
				GameLogic.setPlayer2(-1); 
				GameLogic.setState(2);  
			}
			break; 
			case 1: {
				GameLogic.setTwoPlayer(true); 
				GameLogic.setPlayer2(GameLogic.getPlayer2Index()); 
				GameLogic.setState(2); 
			}
			break; 
			}
		}
		for (int i=0; i<keyStates.length; i++) 
			keyStates[i] = false; 
		selectedButton = 0; 
	}
}
