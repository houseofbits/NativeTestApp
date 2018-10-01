package com.nativetest.nativetestapp;

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
        }
        else stopSoundJNI(index);
    }

    public native int loadSoundJNI(String filename, int index);
    public native int playSoundJNI(int index);
    public native int stopSoundJNI(int index);
    public native int soundChannelMixJNI(int index, float Al, float Ar, float Bl, float Br);
    public native boolean isPlayingSoundJNI(int index);
}
