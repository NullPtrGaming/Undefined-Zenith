// Houses main rendering loop 

package game;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;

import game.entity.Entity;
import game.entity.Projectile; 

public class Renderer { 
	
	private int[] dimensions; 
	private long window; 
	private float[] vertexArray; 
	private int[] textureList; 
	
	// Constructor, initializes important access/data 
	public Renderer (long window, int[] textures) {
		this.window = window; 
		dimensions = new int[2]; 
		textureList = textures; 
	}
	
	// Rendering method, called every frame 
	public void render () { 
		getWindowSize(); 
		
		glBindTexture(GL_TEXTURE_2D, textureList[0]); 
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glBegin(GL_TRIANGLES); 
		
		if (GameLogic.getState() == 2) {
			for (int i=0; i<4; i++) { 
				vertexArray = getVertexArray(GameLogic.getEntity(i, true)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getEntity(i, true).getDirection()); 
			} 
			
			for (int j=0; j<GameLogic.numEntities(); j++) {
				vertexArray = getVertexArray(GameLogic.getEntity(j, false)); 
				if (vertexArray != null)  
					renderVertices(GameLogic.getEntity(j, false).getDirection()); 
			}
			for (int k=0; k<GameLogic.numProjectiles(); k++) {
				vertexArray = getVertexArray(GameLogic.getProjectile(k)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getProjectile(k).getDirection()); 
			}
		}
		else {
			for (int i=0; i<GameLogic.numButtons(); i++) {
				vertexArray = getVertexArray(GameLogic.getProjectile(i)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getButton(i).getDirection()); 
			}
		}
		glEnd(); 
	} 
	
		// Returns an array of vertices for rendering a single tile entity 
		public float[] getVertexArray(Entity entity) {  
			//glfwSetWindowSizeCallback(window, glfwWindowSizeCallback ); // testing 
			float[] vertexArray = new float[8];  
			if (entity == null) // important check 
				return null; 
			int x = entity.getX(), y = entity.getY(); 
			getWindowSize(); 
			
			if (entity.getCooldown() == -1) { 
				vertexArray[0] = (float)x/(Entity.MAX_X); // experimental/unstable code, to be fixed later 
				vertexArray[1] = (float)y/(Entity.MAX_Y);
				vertexArray[2] = (float)x/(Entity.MAX_X) + Projectile.PROJ_BOX_WIDTH; 
				vertexArray[3] = (float)y/(Entity.MAX_Y);
				vertexArray[4] = (float)x/(Entity.MAX_X) + Projectile.PROJ_BOX_WIDTH; 
				vertexArray[5] = (float)y/(Entity.MAX_Y) + Projectile.PROJ_BOX_HEIGHT; 
				vertexArray[6] = (float)x/(Entity.MAX_X);
				vertexArray[7] = (float)y/(Entity.MAX_Y) + Projectile.PROJ_BOX_HEIGHT; 
			} 
			else {
				vertexArray[0] = (float)x/Entity.MAX_X; // experimental/unstable code, to be fixed later 
				vertexArray[1] = (float)y/Entity.MAX_Y;
				vertexArray[2] = (float)x/Entity.MAX_X + Entity.BOX_WIDTH; 
				vertexArray[3] = (float)y/Entity.MAX_Y;
				vertexArray[4] = (float)x/Entity.MAX_X + Entity.BOX_WIDTH;
				vertexArray[5] = (float)y/Entity.MAX_Y + Entity.BOX_HEIGHT;
				vertexArray[6] = (float)x/Entity.MAX_X;
				vertexArray[7] = (float)y/Entity.MAX_Y + Entity.BOX_HEIGHT;
			}
			
			return vertexArray; 
		}
		
		// OpenGL side of render code 
		public void renderVertices (int direction) {
			glTexCoord2f(1, 0);
			glVertex2f(vertexArray[0], vertexArray[1]);
			glTexCoord2f(1, 1);
			glVertex2f(vertexArray[2], vertexArray[3]);
			glTexCoord2f(0, 1);
			glVertex2f(vertexArray[4], vertexArray[5]);
			glTexCoord2f(0, 1); 
			glVertex2f(vertexArray[4], vertexArray[5]);
			glTexCoord2f(0, 0);
			glVertex2f(vertexArray[6], vertexArray[7]);
			glTexCoord2f(1, 0);
			glVertex2f(vertexArray[0], vertexArray[1]);
		}
		
		
		// Returns the window's size in (width, height) format in a 2 position array 
		public int[] getWindowSize () {  
			IntBuffer w = BufferUtils.createIntBuffer(1); 
			IntBuffer h = BufferUtils.createIntBuffer(1); 
			GLFW.glfwGetWindowSize(window, w, h); 
			dimensions[0] = w.get(0); 
			dimensions[1] = h.get(0); 
			glViewport(0, 0, dimensions[0], dimensions[1]); 
			return dimensions; 
		}

}

