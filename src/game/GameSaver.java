// class for writing to save files and reading them for game reloading 

package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import game.entity.Player;

public class GameSaver {
	
	// returns a string with the expected save location (cross platform) 
	public static String getExpectedSaveLocation () {
		//File saveDir = new File(System.getProperty("user.home")+System.getProperty("file.separator")+"AppData"+System.getProperty("file.separator")+"Roaming"+System.getProperty("file.separator")+"UndefinedZenith"); 
		File saveDir = new File(System.getProperty("user.home")+System.getProperty("file.separator")+"Saved Games"+System.getProperty("file.separator")+"UndefinedZenith"); 
		saveDir.mkdir(); 
		//return System.getProperty("user.home")+System.getProperty("file.separator")+"AppData"+System.getProperty("file.separator")+"Roaming"+System.getProperty("file.separator")+"UndefinedZenith"+System.getProperty("file.separator")+"UndefinedZenithSave.txt"; 
		return System.getProperty("user.home")+System.getProperty("file.separator")+"Saved Games"+System.getProperty("file.separator")+"UndefinedZenith"+System.getProperty("file.separator")+"UndefinedZenithSave.txt"; 
	}
	
	// self-explanatory, saves game 
	public static void saveGame () {
		File saveFile = new File ("UndefinedZenithSave.txt"); 
		try { 
			saveFile.createNewFile(); 
			FileWriter saveWriter = new FileWriter("UndefinedZenithSave.txt"); 
			saveWriter.write("Score: " + GameLogic.getMainPlayer().getScore() + "\n"); // saves score 
			saveWriter.write("Keybinds: "); 
			for (int i=0; i<Input.getKeybinds().length; i++)
				saveWriter.write(Input.getKeybinds()[i] + " "); // saves key binds 
			saveWriter.write("\nFullscreen: " + GameLogic.getFullscreenState()); // saves fullscreen state 
			saveWriter.write("\nPosition: " + GameLogic.getMainPlayer().getX() + " " + GameLogic.getMainPlayer().getY() + " " + GameLogic.getMainPlayer().getDirection()); // saves player position (might not be a thing) 
			
			saveWriter.write("\n"); // extra new line in save file because why not? 
			saveWriter.close(); // testing 
		} 
		catch (IOException e) { 
			System.out.println("nope no saving the game (trying another thing)"); 
			saveGame(getExpectedSaveLocation()); 
		}  
	}
	// also saves game but with a specific path 
	public static void saveGame (String path) { 
		File saveFile = new File (path); 
		try { 
			saveFile.createNewFile(); 
			FileWriter saveWriter = new FileWriter(path); 
			saveWriter.write("Score: " + GameLogic.getMainPlayer().getScore() + "\n"); // saves score 
			saveWriter.write("Keybinds: "); 
			for (int i=0; i<Input.getKeybinds().length; i++)
				saveWriter.write(Input.getKeybinds()[i] + " "); // saves key binds 
			saveWriter.write("\nFullscreen: " + GameLogic.getFullscreenState()); // saves fullscreen state 
			saveWriter.write("\nPosition: " + GameLogic.getMainPlayer().getX() + " " + GameLogic.getMainPlayer().getY() + " " + GameLogic.getMainPlayer().getDirection()); // saves player position (might not be a thing) 
			
			saveWriter.write("\n"); // extra new line in save file because why not? 
			saveWriter.close(); // testing 
		} 
		catch (IOException e) { 
			System.out.println("nope no saving the game"); 
			System.out.println(getExpectedSaveLocation()); 
		} 
	}
	
	// loads most saved game values back into variables 
	public static void loadGame () {
		try {
			File saveFile = new File("UndefinedZenithSave.txt"); 
			Scanner saveReader = new Scanner(saveFile); 
			while (!saveReader.hasNextInt()) 
				saveReader.next(); 
			GameLogic.getMainPlayer().scoreAdd(saveReader.nextInt()); // loads score 
			while (!saveReader.hasNextInt()) 
				saveReader.next();
			for (int i=0; i<Input.getKeybinds().length; i++) 
				Input.getKeybinds()[i] = saveReader.nextInt(); // loads key binds 
			while (!saveReader.hasNextBoolean()) 
				saveReader.next();
			if (saveReader.nextBoolean()) 
				GameLogic.toggleFullscreen(); // loads fullscreen state 
			while (!saveReader.hasNextInt()) 
				saveReader.next();
			GameLogic.getMainPlayer().move(saveReader.nextInt(), saveReader.nextInt()); // loads player position 
			GameLogic.getMainPlayer().setDirection(saveReader.nextInt()); // loads player direction (again this might be disabled later) 
			saveReader.close(); 
		} 
		catch (Exception e) { 
			loadGame(getExpectedSaveLocation()); 
		} 
	}
	// for the workaround 
	public static void loadGame(String path) {
		try {
			File saveFile = new File(path); 
			Scanner saveReader = new Scanner(saveFile); 
			while (!saveReader.hasNextInt()) 
				saveReader.next(); 
			GameLogic.getMainPlayer().scoreAdd(saveReader.nextInt()); // loads score 
			while (!saveReader.hasNextInt()) 
				saveReader.next();
			for (int i=0; i<Input.getKeybinds().length; i++) 
				Input.getKeybinds()[i] = saveReader.nextInt(); // loads key binds 
			while (!saveReader.hasNextBoolean()) 
				saveReader.next();
			if (saveReader.nextBoolean()) 
				GameLogic.toggleFullscreen(); // loads fullscreen state 
			while (!saveReader.hasNextInt()) 
				saveReader.next();
			GameLogic.getMainPlayer().move(saveReader.nextInt(), saveReader.nextInt()); // loads player position 
			GameLogic.getMainPlayer().setDirection(saveReader.nextInt()); // loads player direction (again this might be disabled later) 
			saveReader.close(); 
		} 
		catch (Exception e) { 
			saveGame(); 
		} 
	}
}

