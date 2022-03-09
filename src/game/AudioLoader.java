// Provides some basic methods for loading audio I didn't want to put in GameLogic 

package game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.*; 
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class AudioLoader {

	public static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
	    try (MemoryStack stack = MemoryStack.stackPush()) {
	        
	    	ByteBuffer vorbis = ioResourceToByteBuffer(resource, bufferSize);
	        IntBuffer error = stack.mallocInt(1);
	        long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);

	        STBVorbis.stb_vorbis_get_info(decoder, info);

	        int channels = info.channels();

	        int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

	        ShortBuffer pcm = MemoryUtil.memAllocShort(lengthSamples);

	        pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
	        STBVorbis.stb_vorbis_close(decoder);

	        return pcm;
	      }
	    }
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (
                    InputStream source = AudioLoader.class.getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = BufferUtils.createByteBuffer(bufferSize); 

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }
}
