// Houses main rendering loop 

package game;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.opengl.GL11.*;

import game.entity.Boss;
import game.entity.Button;
import game.entity.Effect;
import game.entity.Entity;
import game.entity.Player;
import game.entity.PowerUp;
import game.entity.Projectile; 

public class Renderer { 
	
	private int[] dimensions; 
	private long window; 
	private float[] vertexArray; 
	private int[] textureList; 
	private int[] projectileTextureList; 
	private int[] entityTextureList; 
	private int[] playerTextureList; 
	private int[] effectTextureList; 
	private int[] buttonTextureList; 
	private int[] powerUpTextureList; 
	private int[] bossTextureList; 
	private ArrayList<Effect> effectList; 
	private int[] shakeArray; 
	
	// Constructor, initializes important access/data 
	public Renderer (long window, int[] textures) { 
		this.window = window; 
		dimensions = new int[2]; 
		textureList = textures; 
		projectileTextureList = Projectile.getTextures(); 
		entityTextureList = Entity.getTextures(); 
		playerTextureList = Player.getTextures(); 
		effectTextureList = Effect.getTextures(); 
		buttonTextureList = Button.getTextures(); 
		powerUpTextureList = PowerUp.getTextures(); 
		bossTextureList = Boss.getTextures(); 
		effectList = GameLogic.getEffects(); 
		shakeArray = GameLogic.getShake(); 
	} 
	
	// Rendering method, called every frame 
	public void render () { 
		getWindowSize(); 
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		renderBackground(); // outside of state check 
		
		if (GameLogic.getState() != 0) { 
			GameLogic.pollShake(); 
			
			// power ups 
			for (int i=0; i<GameLogic.numPowerUps(); i++) {
				PowerUp p = GameLogic.getPowerUp(i); 
				glBindTexture(GL_TEXTURE_2D, powerUpTextureList[p.getType()]); 
				glBegin(GL_TRIANGLES); 
				vertexArray[0] = (float)(p.getX()+shakeArray[0])/Entity.MAX_X; 
				vertexArray[1] = (float)(p.getY()+shakeArray[1])/Entity.MAX_Y; 
				vertexArray[2] = (float)(p.getX()+shakeArray[0])/Entity.MAX_X + Entity.BOX_WIDTH; 
				vertexArray[3] = (float)(p.getY()+shakeArray[1])/Entity.MAX_Y + Entity.BOX_HEIGHT; 
				renderVertices(0); 
				glEnd(); 
			}
			
			// players 
			for (int i=0; i<4; i++) { 
				glBindTexture(GL_TEXTURE_2D, GameLogic.getMainPlayer().getTexture()); 
				glBegin(GL_TRIANGLES);
				vertexArray = getVertexArray(GameLogic.getEntity(i, true)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getEntity(i, true).getDirection()); 
				glEnd(); 
			} 
			
			// boss 
			if (GameLogic.isBoss()) {
				glBindTexture(GL_TEXTURE_2D, bossTextureList[GameLogic.getBoss().getTexture()]); 
				glBegin(GL_TRIANGLES); 
				vertexArray = new float[4]; 
				vertexArray[0] = (float)(GameLogic.getBoss().getX()+shakeArray[0])/Entity.MAX_X; 
				vertexArray[1] = (float)(GameLogic.getBoss().getY()+shakeArray[1])/Entity.MAX_Y; 
				vertexArray[2] = (float)(GameLogic.getBoss().getX()+shakeArray[0])/Entity.MAX_X + GameLogic.getBoss().getW(); 
				vertexArray[3] = (float)(GameLogic.getBoss().getY()+shakeArray[1])/Entity.MAX_Y + GameLogic.getBoss().getH(); 
				renderVertices(GameLogic.getBoss().getDirection()); 
				glEnd(); 
			}
			
			// entities 
			glBindTexture(GL_TEXTURE_2D, textureList[5]); 
			for (int j=0; j<GameLogic.numEntities(); j++) {
				glBindTexture(GL_TEXTURE_2D, entityTextureList[GameLogic.getEntity(j, false).getTexture()]); 
				glBegin(GL_TRIANGLES);
				vertexArray = getVertexArray(GameLogic.getEntity(j, false)); 
				if (vertexArray != null)  
					renderVertices(GameLogic.getEntity(j, false).getDirection()); 
				glEnd(); 
			}
			// projectiles 
			glBindTexture(GL_TEXTURE_2D, textureList[2]); 
			for (int k=0; k<GameLogic.numProjectiles(); k++) { 
				if (GameLogic.getProjectile(k).getOwner() != GameLogic.getEntity(0, true)) 
					glBindTexture(GL_TEXTURE_2D, projectileTextureList[1]); 
				else 
					glBindTexture(GL_TEXTURE_2D, projectileTextureList[0]); 
				glBegin(GL_TRIANGLES); 
				vertexArray = getVertexArray(GameLogic.getProjectile(k)); 
				if (vertexArray != null) 
					renderVertices(GameLogic.getProjectile(k).getDirection()); 
				glEnd(); 
			}
			// physical attack forcefield 
			if (GameLogic.getPhysicalAttackState()) {
				glBindTexture(GL_TEXTURE_2D, textureList[7]); 
				glBegin(GL_TRIANGLES); 
				vertexArray = new float[4]; 
				vertexArray[0] = (float)(GameLogic.getMainPlayer().getX()-8)/Entity.MAX_X; 
				vertexArray[1] = (float)(GameLogic.getMainPlayer().getY()-8)/Entity.MAX_Y; 
				vertexArray[2] = (float)(GameLogic.getMainPlayer().getX()-8)/Entity.MAX_X + 0.125f; 
				vertexArray[3] = (float)(GameLogic.getMainPlayer().getY()-8)/Entity.MAX_Y + ((float)2/9); 
				renderVertices(0); 
				glEnd(); 
			}
			
			renderEffects(); 
			renderNumbers(); 
		}
		
		if (GameLogic.getState() == 0 && GameLogic.getMenuIndex() == 0) { 
			glBindTexture(GL_TEXTURE_2D, textureList[3]); 
			glBegin(GL_TRIANGLES); 
			vertexArray = new float[4]; 
			vertexArray[0] = (float)(-80)/Entity.MAX_X; 
			vertexArray[1] = (float)(40)/Entity.MAX_Y; 
			vertexArray[2] = (float)(-80)/Entity.MAX_X + 0.625f; 
			vertexArray[3] = (float)(40)/Entity.MAX_Y + 0.5f; 
			renderVertices(0); 
			glEnd(); 
		} 
		
			for (int i=0; i<GameLogic.numButtons(); i++) {
				glBindTexture(GL_TEXTURE_2D, buttonTextureList[GameLogic.getButton(i).getTexture()]); 
				if (GameLogic.getInput().getSelection() == i && GameLogic.getButton(i) != null) { 
					glBindTexture(GL_TEXTURE_2D, buttonTextureList[0]); 
					Button tempButton = GameLogic.getButton(i); 
					vertexArray = vertexArrayButtons(new Button(tempButton.getX()-32, tempButton.getY(), tempButton.getWidth(), tempButton.getHeight(), null, 0)); 
					glBegin(GL_TRIANGLES); 
					renderVertices(0); 
					glEnd(); 
				}
				int tempDirection = 0; 
				glBindTexture(GL_TEXTURE_2D, buttonTextureList[GameLogic.getButton(i).getTexture()]); 
				if (GameLogic.getButton(i).getTexture() == 7) 
					glBindTexture(GL_TEXTURE_2D, GameLogic.getMainPlayer().getTexture()); 
				if (GameLogic.getMenuIndex() == 3) { // cases for the rebinding menu - weird workarounds 
					switch (i) {
					case 0: {
						tempDirection = 2; 
						glBindTexture(GL_TEXTURE_2D, buttonTextureList[0]);
					}
					break; 
					case 1: {
						tempDirection = 3; 
						glBindTexture(GL_TEXTURE_2D, buttonTextureList[0]);
					}
					break; 
					case 2: {
						tempDirection = 1; 
						glBindTexture(GL_TEXTURE_2D, buttonTextureList[0]);
					}
					break; 
					case 3: {
						tempDirection = 0; 
						glBindTexture(GL_TEXTURE_2D, buttonTextureList[0]);
					}
					break; 
					case 4: {
						glBindTexture(GL_TEXTURE_2D, effectTextureList[0]); 
					}
					break; 
					case 5: {
						glBindTexture(GL_TEXTURE_2D, textureList[5]); 
					}
					break; 
					case 6: {
						glBindTexture(GL_TEXTURE_2D, buttonTextureList[2]); 
					}
					break; 
					}
					
				}
				glBegin(GL_TRIANGLES); 
				vertexArray = vertexArrayButtons(GameLogic.getButton(i)); 
				if (vertexArray != null) 
					renderVertices(tempDirection); 
				glEnd(); 
			} 
			if (GameLogic.getMenuIndex() == 3) {
				vertexArray = new float[4]; 
				vertexArray[0] = (float)-128/Entity.MAX_X; 
				vertexArray[1] = (float)108/Entity.MAX_Y; 
				vertexArray[2] = (float)-128/Entity.MAX_X + (float)Button.STANDARD_BUTTON_WIDTH/256; 
				vertexArray[3] = (float)108/Entity.MAX_Y + (float)Button.STANDARD_BUTTON_HEIGHT/144; 
				if (GameLogic.wasRebindSuccess()) 
					glBindTexture(GL_TEXTURE_2D, textureList[5]); 
				else 
					glBindTexture(GL_TEXTURE_2D, textureList[6]); 
				glBegin(GL_TRIANGLES); 
				renderVertices(0); 
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
				vertexArray[0] = (float)(x+shakeArray[0])/(Entity.MAX_X); 
				vertexArray[1] = (float)(y+shakeArray[1])/(Entity.MAX_Y); 
				vertexArray[2] = (float)(x+shakeArray[0])/(Entity.MAX_X) + Projectile.PROJ_BOX_WIDTH; 
				vertexArray[3] = (float)(y+shakeArray[1])/(Entity.MAX_Y) + Projectile.PROJ_BOX_HEIGHT; 
			} 
			else {
				vertexArray[0] = (float)(x+shakeArray[0])/Entity.MAX_X; 
				vertexArray[1] = (float)(y+shakeArray[1])/Entity.MAX_Y; 
				vertexArray[2] = (float)(x+shakeArray[0])/Entity.MAX_X + Entity.BOX_WIDTH; 
				vertexArray[3] = (float)(y+shakeArray[1])/Entity.MAX_Y + Entity.BOX_HEIGHT;
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
		
		// OpenGL side of render code - must be between begin and end calls 
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
			glBindTexture(GL_TEXTURE_2D, textureList[0]); 
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
		
		// renders the score and health on the screen's corners (overlays everything else) 
		public void renderNumbers () {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER); 
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER); 
			final float OFFSET_W = (float)1/32; 
			final float OFFSET_H = (float)1/18; 
			final int NUMBER_OFFSET = 8; 
			vertexArray = new float[4]; 
			glBindTexture(GL_TEXTURE_2D, textureList[2]); 
			int health = GameLogic.getMainPlayer().getHealth(); 
			int score = GameLogic.getMainPlayer().getScore(); 
			int x = Entity.MAX_X - 4; 
			final int y = 132; 
			if (health == 0) {
				x -= NUMBER_OFFSET; 
				vertexArray[0] = (float)x/(Entity.MAX_X); 
				vertexArray[1] = (float)y/(Entity.MAX_Y); 
				vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
				vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
				renderNumberVertices(0, true); 
			} 
			while (health > 0) {
				int tempDigit = health % 10; 
				health /= 10; 
				x -= NUMBER_OFFSET; 
				vertexArray[0] = (float)x/(Entity.MAX_X); 
				vertexArray[1] = (float)y/(Entity.MAX_Y); 
				vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
				vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
				renderNumberVertices(tempDigit, true); 
			}
			x -= NUMBER_OFFSET; 
			vertexArray[0] = (float)x/(Entity.MAX_X); 
			vertexArray[1] = (float)y/(Entity.MAX_Y); 
			vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
			vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
			renderNumberVertices(10, true); 
			x -= NUMBER_OFFSET; 
			if (score == 0) {
				x -= NUMBER_OFFSET; 
				vertexArray[0] = (float)x/(Entity.MAX_X); 
				vertexArray[1] = (float)y/(Entity.MAX_Y); 
				vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
				vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
				renderNumberVertices(0, false); 
			}
			while (score > 0) {
				int tempDigit = score % 10; 
				score /= 10; 
				x -= NUMBER_OFFSET; 
				vertexArray[0] = (float)x/(Entity.MAX_X); 
				vertexArray[1] = (float)y/(Entity.MAX_Y); 
				vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
				vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
				renderNumberVertices(tempDigit, false); 
			}
			x -= NUMBER_OFFSET; 
			vertexArray[0] = (float)x/(Entity.MAX_X); 
			vertexArray[1] = (float)y/(Entity.MAX_Y); 
			vertexArray[2] = (float)x/(Entity.MAX_X) + OFFSET_W; 
			vertexArray[3] = (float)y/(Entity.MAX_Y) + OFFSET_H; 
			renderNumberVertices(10, false); 
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); 
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE); 
		}
		public void renderNumberVertices (int digit, boolean isHealth) { 
			float[] textureArray = new float[4]; 
			textureArray[0] = (float)digit/11; 
			textureArray[1] = (float)(digit+1)/11; 
			if (isHealth) {
				textureArray[2] = 0.5f; 
				textureArray[3] = 1f; 
			}
			else {
				textureArray[2] = 0; 
				textureArray[3] = 0.5f; 
			}
			glBegin(GL_TRIANGLES); 
			glTexCoord2f(textureArray[1], textureArray[2]);
			glVertex2f(vertexArray[2], vertexArray[1]);
			glTexCoord2f(textureArray[1], textureArray[3]);
			glVertex2f(vertexArray[2], vertexArray[3]);
			glTexCoord2f(textureArray[0], textureArray[3]);
			glVertex2f(vertexArray[0], vertexArray[3]);
			glTexCoord2f(textureArray[0], textureArray[3]);
			glVertex2f(vertexArray[0], vertexArray[3]);
			glTexCoord2f(textureArray[0], textureArray[2]);
			glVertex2f(vertexArray[0], vertexArray[1]);
			glTexCoord2f(textureArray[1], textureArray[2]);
			glVertex2f(vertexArray[2], vertexArray[1]);
			glEnd(); 
		}
		
		// renders and polls Effects 
		public void renderEffects () {
			for (Effect e : effectList) {
				glBindTexture(GL_TEXTURE_2D, effectTextureList[e.getType()]); 
				vertexArray = new float[4]; 
				vertexArray[0] = (float)e.getX()/Entity.MAX_X; 
				vertexArray[1] = (float)e.getY()/Entity.MAX_Y;
				vertexArray[2] = (float)e.getX()/Entity.MAX_X + Entity.BOX_WIDTH; 
				vertexArray[3] = (float)e.getY()/Entity.MAX_Y + Entity.BOX_HEIGHT; 
				glBegin(GL_TRIANGLES); 
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
				glEnd(); 
				e.pollDuration(); 
			}
			for (int i=0; i<effectList.size(); i++) {
				if (effectList.get(i).getDuration() <= 0) 
					effectList.remove(i); 
			} 
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
		
		public long getWindow () {
			return window; 
		}
}
