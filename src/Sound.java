import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

	private Clip clip;
	
	public Sound(String file){
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			clip = AudioSystem.getClip();
			clip.open(ais);
			//clip.start();
			/*if(Loop){
				clip.loop(-1);
			} else {
				clip.close();
			}*/
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void play(){
		clip.setFramePosition(0);  // Must always rewind!
        clip.start();
	}
	
	public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
	
	public void stop(){
        clip.stop();
    }
}
