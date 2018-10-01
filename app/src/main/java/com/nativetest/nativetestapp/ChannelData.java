package com.nativetest.nativetestapp;

import android.content.SharedPreferences;

public class ChannelData {
    public class Channel{
        public Channel(float al, float ar, float bl, float br){
            audioALeft = al;
            audioARight = ar;
            audioBLeft = bl;
            audioBRight = br;
        }
        float getAudioALeft(){ return audioALeft * ((float)value / 256.0f);}
        float getAudioARight(){ return audioARight * ((float)value / 256.0f);}
        float getAudioBLeft(){ return audioBLeft * ((float)value / 256.0f);}
        float getAudioBRight(){ return audioBRight * ((float)value / 256.0f);}

        public int value = 0;
        float audioALeft = 0;
        float audioARight = 0;
        float audioBLeft = 0;
        float audioBRight = 0;
        boolean isPlaying = false;
    };
    public Channel[] channels = {
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 1.0f, 0.0f, 0.0f),
            new Channel(1.0f, 1.0f, 0.0f, 0.0f),
            new Channel(0.0f, 1.0f, 0.0f, 0.0f),
            new Channel(0.0f, 1.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f)
    };
    public void setChannelValue(int index){
        setChannelValue(index, channels[index].value);
    }
    public void setChannelValue(int index, int value){
        if(index < 0)return;
        if(index > channels.length)return;
        channels[index].value = value;
        float fval = (float)value / 256.0f;
        soundChannelMixJNI(index,
                channels[index].audioALeft * fval,
                channels[index].audioARight * fval,
                channels[index].audioBLeft * fval,
                channels[index].audioBRight * fval
                );
    }
    public void playStop(int index, boolean play){
        if(play){
            playSoundJNI(index);
            setChannelValue(index);
            channels[index].isPlaying = true;
        }else{
            stopSoundJNI(index);
            channels[index].isPlaying = false;
        }
    }
    public boolean isPlaying(int index){
        return isPlayingSoundJNI(index);
    }
    public void loadSettings(SharedPreferences settings){
        for(int i=0; i<8; i++){
            channels[i].audioALeft = settings.getFloat("ch"+Integer.toString(i)+"ALeft", channels[i].audioALeft);
            channels[i].audioARight = settings.getFloat("ch"+Integer.toString(i)+"ARight", channels[i].audioARight);
            channels[i].audioBLeft = settings.getFloat("ch"+Integer.toString(i)+"BLeft", channels[i].audioBLeft);
            channels[i].audioBRight = settings.getFloat("ch"+Integer.toString(i)+"BRight", channels[i].audioBRight);
            channels[i].isPlaying = settings.getBoolean("ch"+Integer.toString(i)+"Playing", channels[i].isPlaying);
            playStop(i, channels[i].isPlaying);
        }
    }
    public void saveSettings(SharedPreferences.Editor editor){
        for(int i=0; i<8; i++){
            editor.putFloat("ch"+Integer.toString(i)+"ALeft", channels[i].audioALeft);
            editor.putFloat("ch"+Integer.toString(i)+"ARight", channels[i].audioARight);
            editor.putFloat("ch"+Integer.toString(i)+"BLeft", channels[i].audioBLeft);
            editor.putFloat("ch"+Integer.toString(i)+"BRight", channels[i].audioBRight);
            editor.putBoolean("ch"+Integer.toString(i)+"Playing", channels[i].isPlaying);
        }
    }

    public native int loadSoundJNI(String filename, int index);
    private native int playSoundJNI(int index);
    private native int stopSoundJNI(int index);
    private native int soundChannelMixJNI(int index, float Al, float Ar, float Bl, float Br);
    private native boolean isPlayingSoundJNI(int index);
}
