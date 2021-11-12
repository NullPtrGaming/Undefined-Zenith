// Represents a main (user-controlled) player of the game 
package game.entity;

import game.*; 

public class Player extends Entity {

	private boolean[] keyStates; 
	private boolean isPrimary = false; 
	
	// Constructor, use Entity constructor but allows the Player to reference key states 
	public Player (int x, int y, int health, int damage, int speed, int cooldown, int type, boolean rotatable, boolean isPrimary, boolean[] keyStates) {
		super(x, y, health, damage, speed, cooldown, type, rotatable); 
		this.keyStates = keyStates; 
		this.isPrimary = isPrimary; 
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
			if (keyStates[Input.SELECT] && GameLogic.getMenuCooldownState())
				GameLogic.setState(1); 
		}
		else { 
			if (keyStates[Input.SELECT] && GameLogic.getMenuCooldownState())
				GameLogic.setState(2); 
		}
	}
	
	
}
