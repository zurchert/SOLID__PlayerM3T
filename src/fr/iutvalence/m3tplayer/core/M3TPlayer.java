package fr.iutvalence.m3tplayer.core;

import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import fr.iutvalence.m3tplayer.core.enumerations.PlayerControl;

public class M3TPlayer{

	
	private int volume;
	
	
	private boolean randomPlaying;
	
	
	private Media currentMedia;

	
	private Library libraryM3TPlayer;
	
	
	private AdvancedPlayer audioPlayer;
	
	
	private boolean isPlaying;

	
	private int positionOfMusic;
	
	
	private Random randomGenerator;

	private boolean isPausing;
	
	private boolean isStoped;
	
	private int mediaId;
	
	
	public M3TPlayer() {
		this.libraryM3TPlayer = new Library();
		this.volume = 100;
		this.randomPlaying = false;
		this.isPlaying = false;
		this.isPausing = false;
		this.isStoped = true;
		this.positionOfMusic = 0;
		if(this.libraryM3TPlayer.isEmpty())
			this.currentMedia = null;
		else
			this.currentMedia = this.libraryM3TPlayer.getMedia(0);

		this.audioPlayer = null;
		this.randomGenerator = new Random();

	}
	
	
	public int getMediaId() {
		return mediaId;
	}


	
	public Library getLibrary() {
		return this.libraryM3TPlayer;
	}

	
	public void setLibrary(Library library) {
		this.libraryM3TPlayer = library;
	}

	
	public void setRandomPlaying(){
		
		int maxRandId = this.libraryM3TPlayer.getImportedMusicNumber();
		if (this.randomPlaying)
			this.randomPlaying = false;
		else {
			this.randomPlaying = true;
			this.currentMedia = this.libraryM3TPlayer.getMedia(this.randomGenerator.nextInt(maxRandId));			
		}
	}
	
	
	public void stop(){
		this.audioPlayer.stop();
		this.isPlaying = false;
		this.isStoped = true;
		this.isPausing = false;
	}
	
	
	public void changeVolume(){
		//FIXME all
		DataLine.Info info = null;
	    Clip clip;
		try {
			clip = (Clip) AudioSystem.getLine(info);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			double gain = .5D; // number between 0 and 1 (loudest)
		    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
		    gainControl.setValue(dB);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
	}

	
	public void changeMedia(PlayerControl control){
		int size = this.libraryM3TPlayer.getImportedMusicNumber();
		if (!this.randomPlaying){
			this.mediaId = 0;

			this.mediaId = this.libraryM3TPlayer.getMediaId(this.currentMedia);

			switch (control) {
				case PREVIOUS:
					this.mediaId -= 1;
					if (this.mediaId < 0)
						this.mediaId = size-1;
					break;
				case NEXT:
					this.mediaId += 1;
					if (this.mediaId >= size)
						this.mediaId = 0;
					break;
				default:
					break;
			}
			
			this.currentMedia = this.libraryM3TPlayer.getMedia(this.mediaId);
		}
		else{
			this.currentMedia = this.libraryM3TPlayer.getMedia(this.randomGenerator.nextInt(size));
		}
		
	}
	
	
	public Media getCurrentMedia() {
		return this.currentMedia;
	}

	public void setCurretnMedia(Media media){
		this.currentMedia = media;
	}
	
	
	public void playMedia(){
		try {
			this.audioPlayer = new AdvancedPlayer(this.currentMedia.getStream());
			   this.isPlaying = true;
			   this.isStoped = false;
			   this.audioPlayer.play(0, Integer.MAX_VALUE);
			   while (this.isPlaying){
			    this.changeMedia(PlayerControl.NEXT);
			    this.audioPlayer = new AdvancedPlayer(this.currentMedia.getStream());
			    this.audioPlayer.play(0, Integer.MAX_VALUE);
			  
			}
		} catch (JavaLayerException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			System.out.println("No media to play ! (the source does not exists)");
		}
	}

	
	public boolean isPlaying() {
		return this.isPlaying;
	}

	
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isPausing() {
		return this.isPausing;
	}

	
	public void setPausing(boolean isPausing) {
		this.isPausing = isPausing;
	}

	public boolean isStoped() {
		return this.isStoped;
	}

	
	public void setStoped(boolean isStoped) {
		this.isStoped = isStoped;
	}
}