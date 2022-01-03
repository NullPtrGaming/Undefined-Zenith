// Houses main rendering loop 

package game;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL12;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.opengl.GL11.*;

import game.entity.Button;
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
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		renderBackground(); // outside of state check 
		
		if (GameLogic.getState() != 0) {
			for (int i=0; i<4; i++) { 
				glBindTexture(GL_TEXTURE_2D, textureList[i]); 
				glBegin(GL_TRIANGLES);
				vertexArray = getVertexArray(GameLogic.getEntity(i, true)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getEntity(i, true).getDirection()); 
				glEnd(); 
			} 
			
			glBindTexture(GL_TEXTURE_2D, textureList[1]); 
			for (int j=0; j<GameLogic.numEntities(); j++) {
				glBegin(GL_TRIANGLES);
				vertexArray = getVertexArray(GameLogic.getEntity(j, false)); 
				if (vertexArray != null)  
					renderVertices(GameLogic.getEntity(j, false).getDirection()); 
				glEnd(); 
			}
			glBindTexture(GL_TEXTURE_2D, textureList[2]); 
			for (int k=0; k<GameLogic.numProjectiles(); k++) { 
				glBegin(GL_TRIANGLES); 
				vertexArray = getVertexArray(GameLogic.getProjectile(k)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getProjectile(k).getDirection()); 
				glEnd(); 
			}
		}
			glBindTexture(GL_TEXTURE_2D, textureList[4]); 
			for (int i=0; i<GameLogic.numButtons(); i++) {
				glBegin(GL_TRIANGLES); 
				if (GameLogic.getInput().getSelection() == i && GameLogic.getButton(i) != null) {
					Button tempButton = GameLogic.getButton(i); 
					vertexArray = vertexArrayButtons(new Button(tempButton.getX()-4, tempButton.getY()-4, tempButton.getWidth()+8, tempButton.getHeight()+8, null)); 
					renderVertices(1); 
				}
				vertexArray = vertexArrayButtons(GameLogic.getButton(i)); 
				if (vertexArray != null) 
					renderVertices(1); 
				glEnd(); 
		} 
	} 
	
		// Returns an array of vertices for rendering a single tile entity 
		public float[] getVertexArray(Entity entity) {  
			//glfwSetWindowSizeCallback(window, glfwWindowSizeCallback ); // testing 
			float[] vertexArray = new float[4];  
			if (entity == null) // important check 
				return null; 
			int x = entity.getX(), y = entity.getY(); 
			getWindowSize(); 
			
			if (entity.getCooldown() == -1) {  
				vertexArray[0] = (float)x/(Entity.MAX_X); 
				vertexArray[1] = (float)y/(Entity.MAX_Y); 
				vertexArray[2] = (float)x/(Entity.MAX_X) + Projectile.PROJ_BOX_WIDTH; 
				vertexArray[3] = (float)y/(Entity.MAX_Y) + Projectile.PROJ_BOX_HEIGHT; 
			} 
			else {
				vertexArray[0] = (float)x/Entity.MAX_X; 
				vertexArray[1] = (float)y/Entity.MAX_Y;
				vertexArray[2] = (float)x/Entity.MAX_X + Entity.BOX_WIDTH; 
				vertexArray[3] = (float)y/Entity.MAX_Y + Entity.BOX_HEIGHT;
			}
			
			return vertexArray; 
		}
		// same thing but for button 
		public float[] vertexArrayButtons (Button button) {
			float[] vertexArray = new float[4]; 
			if (button == null) // important check 
			return null; 
			int x = button.getX(), y = button.getY(); 
			vertexArray[0] = (float)x/Entity.MAX_X; 
			vertexArray[1] = (float)y/Entity.MAX_Y;
			vertexArray[2] = (float)x/Entity.MAX_X + (float)button.getWidth()/256; 
			vertexArray[3] = (float)y/Entity.MAX_Y + (float)button.getHeight()/144; 
			return vertexArray; 
		}
		
		// OpenGL side of render code 
		public void renderVertices (int direction) { 
			if (direction == 3) {
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[0], vertexArray[1]);
				glTexCoord2f(1, 1);
				glVertex2f(vertexArray[2], vertexArray[1]);
				glTexCoord2f(0, 1);
				glVertex2f(vertexArray[2], vertexArray[3]);
				glTexCoord2f(0, 1); 
				glVertex2f(vertexArray[2], vertexArray[3]);
				glTexCoord2f(0, 0);
				glVertex2f(vertexArray[0], vertexArray[3]);
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[0], vertexArray[1]);
			}
			else if (direction == 2) {
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[2], vertexArray[3]);
				glTexCoord2f(1, 1);
				glVertex2f(vertexArray[0], vertexArray[3]);
				glTexCoord2f(0, 1);
				glVertex2f(vertexArray[0], vertexArray[1]);
				glTexCoord2f(0, 1); 
				glVertex2f(vertexArray[0], vertexArray[1]);
				glTexCoord2f(0, 0);
				glVertex2f(vertexArray[2], vertexArray[1]);
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[2], vertexArray[3]);
			}
			else if (direction == 1) {
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[0], vertexArray[3]);
				glTexCoord2f(1, 1);
				glVertex2f(vertexArray[0], vertexArray[1]);
				glTexCoord2f(0, 1);
				glVertex2f(vertexArray[2], vertexArray[1]);
				glTexCoord2f(0, 1); 
				glVertex2f(vertexArray[2], vertexArray[1]);
				glTexCoord2f(0, 0);
				glVertex2f(vertexArray[2], vertexArray[3]);
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[0], vertexArray[3]);
			}
			else {
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[2], vertexArray[1]);
				glTexCoord2f(1, 1);
				glVertex2f(vertexArray[2], vertexArray[3]);
				glTexCoord2f(0, 1);
				glVertex2f(vertexArray[0], vertexArray[3]);
				glTexCoord2f(0, 1); 
				glVertex2f(vertexArray[0], vertexArray[3]);
				glTexCoord2f(0, 0);
				glVertex2f(vertexArray[0], vertexArray[1]);
				glTexCoord2f(1, 0);
				glVertex2f(vertexArray[2], vertexArray[1]);
			}
		}
		
		// specifically renders the background 
		public void renderBackground () {
			glBindTexture(GL_TEXTURE_2D, textureList[3]); 
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // background uses repeat filter 
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			
			glBegin(GL_TRIANGLES); 
			glTexCoord2f(18, 0);
			glVertex2f(-1f, -1f);
			glTexCoord2f(18, 32);
			glVertex2f(1f, -1f);
			glTexCoord2f(0, 32);
			glVertex2f(1f, 1f);
			glTexCoord2f(0, 32); 
			glVertex2f(1f, 1f);
			glTexCoord2f(0, 0);
			glVertex2f(-1f, 1f);
			glTexCoord2f(18, 0);
			glVertex2f(-1f, -1f); 
			glEnd(); 
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); // switch back to standard filter 
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE); 
		}
		
		// Returns the window's size in (width, height) format in a 2 position array 
		public int[] getWindowSize () {  
			IntBuffer w = BufferUtils.createIntBuffer(1); 
			IntBuffer h = BufferUtils.createIntBuffer(1); 
			GLFW.glfwGetWindowSize(window, w, h); 
			dimensions[0] = w.get(0); 
			dimensions[1] = h.get(0); 
			if (dimensions[0] < dimensions[1] * 16 / 9)
				glViewport(0, (dimensions[1]-dimensions[0]*9/16)/2, dimensions[0], dimensions[0] * 9 / 16); 
			else 
				glViewport((dimensions[0]-dimensions[1]*16/9)/2, 0, dimensions[1] * 16 / 9, dimensions[1]);
			return dimensions; 
		}

}
