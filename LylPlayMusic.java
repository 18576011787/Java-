package com.lyl;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;

/**
 *	落子声播放类
 */

public class LylPlayMusic {
	
//	public static void main(String[] args) {
//		new LylPlayMusic().Play();
//	}
	
    //播放音频文件
    public void Play(){
    	
    	try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/music/Stone3.wav"));
            AudioFormat aif = ais.getFormat();
            SourceDataLine sdl = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,aif);
            sdl = (SourceDataLine)AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
           
            //play
            int nByte = 0;
            byte[] buffer = new byte[128];
            while(nByte != -1){
                nByte = ais.read(buffer,0,128);
                if(nByte >= 0){
                    int oByte = sdl.write(buffer, 0, nByte);
                }
            }
            sdl.stop();
        }catch(UnsupportedAudioFileException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
