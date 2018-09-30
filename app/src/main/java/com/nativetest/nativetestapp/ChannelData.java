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
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f),
            new Channel(1.0f, 0.0f, 0.0f, 0.0f)
    };
}
