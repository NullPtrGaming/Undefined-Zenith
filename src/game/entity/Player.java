// Represents a main (user-controlled) player of the game 
package game.entity;

import game.*; 

public class Player extends Entity {

	private boolean[] keyStates; 
	private boolean isPrimary = false; 
	private int score; 
	
	private int acceleration = 2; // currently testing 
	private int tempSpeed = 0; 
	
	// Constructor, use Entity constructor but allows the Player to reference key states 
	public Player (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, boolean isPrimary, boolean[] keyStates) {
		super(x, y, health, damage, speed, cooldown, type, rotatable); 
		this.keyStates = keyStates; 
		this.isPrimary = isPrimary; 
		score = 0; 
	}
	
	// Overrides the entity movement, utilizes key states 
	public void pollMovement () {
		if (GameLogic.getState() > 1 && isPrimary) { 
			if (keyStates[Input.ATTACK]) 
				pollAttack(); 
			if (keyStates[Input.UP]) 
				move(0, getSpeed()); 
			if (keyStates[Input.DOWN]) 
				move(0, -1*getSpeed()); 
			if (keyStates[Input.LEFT])
				move(-1*getSpeed(), 0); 
			if (keyStates[Input.RIGHT])
				move(getSpeed(), 0); 
			if (keyStates[Input.ESCAPE] && GameLogic.getMenuCooldownState())
				GameLogic.setState(1); 
		}
		else { 
			if (!GameLogic.isRebinding() && GameLogic.getState() == 1 && keyStates[Input.ESCAPE] && GameLogic.getMenuCooldownState()) 
				GameLogic.setState(2); 
			if (keyStates[Input.UP] && GameLogic.getMenuCooldownState())
				GameLogic.getInput().changeSelection(false); 
			if (keyStates[Input.DOWN] && GameLogic.getMenuCooldownState()) 
				GameLogic.getInput().changeSelection(true); 
			if (keyStates[Input.SELECT] && GameLogic.getMenuCooldownState())
				GameLogic.getInput().select(); 
		}
	}
	
	// adds to score 
	public void scoreAdd (int add) {
		score += add; 
	}
	public int getScore () { 
		return score; 
	}
	
	// accelerates - not use normal speed variable (testing) 
	public void accelerate (boolean isNegative) {
		if (isNegative) 
			tempSpeed -= acceleration; 
		else 
			tempSpeed += acceleration; 
	}
	
}
