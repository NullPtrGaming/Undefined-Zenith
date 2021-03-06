// Loads textures 

package game;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.opengl.GL11.*; 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL12;

public class TextureLoader {
	
	private static final int BYTES_PER_PIXEL = 4; 
	
	public TextureLoader () {
		
	}

	public int loadTexture (String path) { 
		
		BufferedImage image; 
		
		try {
             image = ImageIO.read(new File(path)); // loads textures in Eclipse 
        } catch (Exception e) { // loads textures if Eclipse loading fails 
             try {
            	 image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(path)); 
             } catch (Exception i) {
            	 image = null; // texture loading failed 
             }
        }
		
		int[] pixels = new int[image.getWidth() * image.getHeight()]; 
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth()); 
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); 
        
        for(int y = image.getHeight() - 1; y >= 0; y--){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x]; 
                imageBuffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                imageBuffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                imageBuffer.put((byte) (pixel & 0xFF));               // Blue component
                imageBuffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        
        imageBuffer.flip(); 
        
        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);  
        
        //Return the texture ID so we can bind it later again
        return textureID;
	}
	
	// loads the window icon 
	public void loadIcon (String path, long window) { 
		BufferedImage image; 
		
		try {
             image = ImageIO.read(new File(path)); 
        } catch (Exception e) {
        	try {
        		image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(path)); 
            } catch (Exception i) {
            	image = null; 
            }
        }
		
		int[] pixels = new int[image.getWidth() * image.getHeight()]; 
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth()); 
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); 
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x]; 
                imageBuffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                imageBuffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                imageBuffer.put((byte) (pixel & 0xFF));               // Blue component
                imageBuffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        
        imageBuffer.flip(); 
        
        // loads the texture as the icon 
    	GLFWImage iconImage = GLFWImage.malloc();
    	iconImage.set(64, 64, imageBuffer);
    	GLFWImage.Buffer images = GLFWImage.malloc(1);
    	images.put(0, iconImage);
    	glfwSetWindowIcon(window, images);
	}
	
	// loads texture from input stream 
	public int loadTexture (InputStream is) {
		BufferedImage image; 
		
		try {
			image = ImageIO.read(is); // loads textures in zip 
        } catch (Exception e) { 
        	image = null; // texture loading failed 
        }
		
		int[] pixels = new int[image.getWidth() * image.getHeight()]; 
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth()); 
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); 
        
        for(int y = image.getHeight() - 1; y >= 0; y--){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x]; 
                imageBuffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                imageBuffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                imageBuffer.put((byte) (pixel & 0xFF));               // Blue component
                imageBuffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        
        imageBuffer.flip(); 
        
        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);  
        
        //Return the texture ID so we can bind it later again
        return textureID;
	}
	
}
