// Houses main game loop, updates states and calls rendering methods and logic 

package game;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import game.entity.Button;
import game.entity.Effect;
import game.entity.Entity;
import game.entity.Player;
import game.entity.PowerUp;
import game.entity.Projectile;

public class GameLoop {
	
	private long window; // Window ID  
	private int[] textureList = new int[8]; 

	// Constructor, initializes GLFW and other important stuff - recommended code adapted from main LWJGL documentation 
	public GameLoop () { 
		initialize(); 
		loop(); 
		glfwFreeCallbacks(window); 
		glfwDestroyWindow(window); 
		glfwTerminate(); 
		glfwSetErrorCallback(null).free(); 
	}
	
	// Method to initialize requirements and important stuff 
	public void initialize () { 
		GLFWErrorCallback.createPrint(System.err).set(); 
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW"); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); 
		window = glfwCreateWindow(512, 288, "Undefined Zenith", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		centerWindow(); 
		glfwMakeContextCurrent(window); 
		glfwSwapInterval(1); 
		GameLogic.gameInit(window); 
		glfwShowWindow(window); // At this point, we are visible 
	}
	
	// Method handling the main game loop itself, each frame with event handling 
	public void loop () {
		GL.createCapabilities(); 
		glClearColor(0, 0, 0, 0); 
		glEnable(GL_TEXTURE_2D); 
		glDisable(GL_DEPTH_TEST); 
		glEnable(GL_BLEND);
		loadTextures(); 
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); 
		Renderer render = new Renderer(window, textureList); 
		
		while ( !glfwWindowShouldClose(window) ) { // this is the loop 
			// to be continued... 
			//FPSCounter.StartCounter(); 
			
			render.render(); 
			
			glfwSwapBuffers(window); 
			glfwPollEvents(); 
			
			GameLogic.updateTime(); 
			
			if (GameLogic.getState() == 2) 
				GameLogic.updateEntities(); 
			else {
				GameLogic.getEntity(GameLogic.PRIMARY_PLAYER, true).pollMovement(); 
			}
			
			//FPSCounter.StopAndPost(); 
		}
	}
	
	// Self-explanatory method - centers window on the monitor 
	public void centerWindow () {
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
	}
	
	// loads textures using the TextureLoader 
	public void loadTextures () { 
		TextureLoader textureLoader = new TextureLoader(); 
		textureLoader.loadIcon("res/Icon.png", window); // loads the game icon 
		textureList[0] = textureLoader.loadTexture("res/Background.png"); 
		textureList[1] = textureLoader.loadTexture("res/e.png"); 
		textureList[2] = textureLoader.loadTexture("res/Numbers.png"); 
		Projectile.loadTextures(textureLoader); 
		Entity.loadTextures(textureLoader); 
		Player.loadTextures(textureLoader); 
		Effect.loadTextures(textureLoader); 
		Button.loadTextures(textureLoader); 
		PowerUp.loadTextures(textureLoader); 
	}
	
	// gets the window handle 
	public long getWindow () {
		return window; 
	}
	
}
