package com.falseapple.general.plugin;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import javax.sound.sampled.DataLine.Info;

public final class SoundPlay implements LineListener {
	
	private volatile boolean playCompleted = true;
	private Clip audioClip;

	public SoundPlay(URL in) {
		try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(in)) {
			final AudioFormat format = audioStream.getFormat();
			Info info = new Info(Clip.class, format);
			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
		} catch (Exception e) {
			e.printStackTrace();
			if (audioClip != null) {
				audioClip.removeLineListener(this);
				audioClip.close();
			}
		}
	}
	
	public SoundPlay(String audioFilePath) {
		try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath))) {
			final AudioFormat format = audioStream.getFormat();
			Info info = new Info(Clip.class, format);
			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
		} catch (Exception e) {
			e.printStackTrace();
			if (audioClip != null) {
				audioClip.removeLineListener(this);
				audioClip.close();
			}
		}
	}

	public boolean play() {
		try {
			if (audioClip.isOpen()) {
				if (playCompleted) {
					playCompleted = false;
					audioClip.start();
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP) {
			playCompleted = true;
			audioClip.stop();
			audioClip.setMicrosecondPosition(0);
		}
	}
}

