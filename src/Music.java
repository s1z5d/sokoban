import java.io.File;

import javafx.scene.media.*;
import javafx.util.Duration;

public class Music{
	Media musicFile;
	MediaPlayer musicPlayer;
	
	public Music(String file){
		this.musicFile = new Media(new File(file).toURI().toString());
		this.musicPlayer = new MediaPlayer(musicFile);
	}
	
	public void playMusic(boolean loop, boolean musicSwitch){
		//set volumn
		musicPlayer.setVolume(0.5);
		if(musicSwitch){
			musicPlayer.play();
			//loop and repeat the music if loop if true
			if(loop){
				 musicPlayer.setOnEndOfMedia(new Runnable() {    
				        public void run() {
				        musicPlayer.seek(Duration.ZERO); 
				        }
				 });  
			}
		}
		if(musicSwitch == false){
			musicPlayer.stop();
		}
		
	}
	
	public MediaPlayer getPlayer(){
		return this.musicPlayer;
	}
}

