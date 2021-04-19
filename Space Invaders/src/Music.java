import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Music {
	AudioInputStream music;
	Clip clip;
	public Music(File file) {
		try {
			//Clip load
			this.music = AudioSystem.getAudioInputStream(file);
			this.clip = AudioSystem.getClip();
			this.clip.open(this.music);
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	//If we want background music to be played over and over while the game is running:
	public void playBackgroundMusic() {
		try {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e){e.printStackTrace();}
	}
	//If we want to play music o be played while the game is running
	public void playMusic() {
		try {
			clip.stop(); //In case clip was playing
			clip.setMicrosecondPosition(0); //Reset initial sample frame (sample frame 0 = microsecond 0)
			clip.start(); //Start playing the clip
		}
		catch(Exception e){e.printStackTrace();}
	}
}