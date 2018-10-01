package com.nativetest.nativetestapp;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    private SerialComunication serial = new SerialComunication(this);
    public ChannelData channelData = new ChannelData();
    private static final String TAG = "NativetestLog";
    public static final String savedPrefsName = "NativeTestAppSettings";
    private String debugData = "";
    public int editSelectedChannel = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        // Try debug libraries...
        try {
            System.loadLibrary("fmodD");
        }
        catch (UnsatisfiedLinkError e) { }

        // Try logging libraries...
        try {
            System.loadLibrary("fmodL");
        }
        catch (UnsatisfiedLinkError e) { }

        // Try release libraries...
        try {
            System.loadLibrary("fmod");
        }
        catch (UnsatisfiedLinkError e) { }

        System.loadLibrary("native-lib");
    }

    public void addDebugString(String str){
        this.debugData = this.debugData + str + "\n";
        MultiAutoCompleteTextView tv = findViewById(R.id.multiAutoCompleteTextView);
        tv.setText(this.debugData);
        Log.i(TAG, str);
    }
    public void updateJNIDebugStrings(){
        this.addDebugString(this.getDebugStringJNI());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch switch_button = (Switch) findViewById(R.id.switch_console);
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            MultiAutoCompleteTextView tv = findViewById(R.id.multiAutoCompleteTextView);
            if(isChecked){
                tv.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(View.INVISIBLE);
            }
            }
        });

        final Button closeChannelMixerButton = findViewById(R.id.closeChannelMixerButton);
        closeChannelMixerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            View channelMixer = findViewById(R.id.channelMixerView);
            if(channelMixer.getVisibility() == View.VISIBLE) {
                channelMixer.setVisibility(View.INVISIBLE);
                closeChannelMixerButton.setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.titleTextView)).setText(R.string.appTitle);
            }
            }
        });

        class DimmerBarChangeListener implements SeekBar.OnSeekBarChangeListener{
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                int idx = -1;
                float val = 0;
                switch (seekBar.getId()){
                    case R.id.dimmerBar1: idx = 0;  break;
                    case R.id.dimmerBar2: idx = 1;  break;
                    case R.id.dimmerBar3: idx = 2;  break;
                    case R.id.dimmerBar4: idx = 3;  break;
                    case R.id.dimmerBar5: idx = 4;  break;
                    case R.id.dimmerBar6: idx = 5;  break;
                    case R.id.dimmerBar7: idx = 6;  break;
                    case R.id.dimmerBar8: idx = 7;  break;

                    case R.id.seekChanAL:
                        val = (float)seekBar.getProgress() / 100.0f;
                        ((TextView)findViewById(R.id.textChanAL)).setText(Float.toString(val));
                        channelData.channels[editSelectedChannel].audioALeft = val;
                        channelData.setChannelValue(idx);
                        break;
                    case R.id.seekChanAR:
                        val = (float)seekBar.getProgress() / 100.0f;
                        ((TextView)findViewById(R.id.textChanAR)).setText(Float.toString(val));
                        channelData.channels[editSelectedChannel].audioARight = val;
                        channelData.setChannelValue(idx);
                        break;
                    case R.id.seekChanBL:
                        val = (float)seekBar.getProgress() / 100.0f;
                        ((TextView)findViewById(R.id.textChanBL)).setText(Float.toString(val));
                        channelData.channels[editSelectedChannel].audioBLeft = val;
                        channelData.setChannelValue(idx);
                        break;
                    case R.id.seekChanBR:
                        val = (float)seekBar.getProgress() / 100.0f;
                        ((TextView)findViewById(R.id.textChanBR)).setText(Float.toString(val));
                        channelData.channels[editSelectedChannel].audioBRight = val;
                        channelData.setChannelValue(idx);
                        break;
                }
                if(idx >= 0)channelData.setChannelValue(idx, progress);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  }
        }

        DimmerBarChangeListener dimmerBarChangeListener = new DimmerBarChangeListener();

        ((SeekBar) findViewById(R.id.dimmerBar1)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar2)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar3)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar4)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar5)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar6)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar7)).setOnSeekBarChangeListener(dimmerBarChangeListener);
        ((SeekBar) findViewById(R.id.dimmerBar8)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        this.addDebugString("Starting Native Test App");

        serial.connectDevice();

        checkController();

        org.fmod.FMOD.init(this);

        createFMODJNI();
        channelData.loadSoundJNI("file:///android_asset/voice_1.wav",0);
        channelData.loadSoundJNI("file:///android_asset/voice_2.wav",1);
        channelData.loadSoundJNI("file:///android_asset/voice_3.wav",2);
        channelData.loadSoundJNI("file:///android_asset/voice_4.wav",3);
        channelData.loadSoundJNI("file:///android_asset/voice_5.wav",4);
        channelData.loadSoundJNI("file:///android_asset/voice_6.wav",5);
        channelData.loadSoundJNI("file:///android_asset/voice_7.wav",6);
        channelData.loadSoundJNI("file:///android_asset/voice_8.wav",7);

        new MyTask().execute("test");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadAppPreferences();

        for(int i=0; i<8; i++){
            int resID = getResources().getIdentifier("playCheck"+Integer.toString(i+1), "id", getPackageName());
            ((CheckBox) findViewById(resID)).setChecked(channelData.isPlaying(i));
        }

        this.updateJNIDebugStrings();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void checkController(){
        for(int deviceId: InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(deviceId);
            if(isController(device)) {
                String controllerName = device.getName();
                this.addDebugString("Input device found: " + controllerName);
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        saveAppPreferences();
    }
    public void setAxisChange(int index, float value){
        switch(index){
            case 0:
                SeekBar bar1 = (SeekBar) findViewById(R.id.dimmerBar1);
                bar1.setProgress((int)(255.0f * value));
                //channelData.setChannelValue(0, val);
                break;
            case 1:
                SeekBar bar2 = (SeekBar) findViewById(R.id.dimmerBar2);
                bar2.setProgress((int)(255.0f * value));
                break;
            case 2:
                SeekBar bar3 = (SeekBar) findViewById(R.id.dimmerBar3);
                bar3.setProgress((int)(255.0f * value));
                break;
            case 3:
                SeekBar bar4 = (SeekBar) findViewById(R.id.dimmerBar4);
                bar4.setProgress((int)(255.0f * value));
                break;
        }
    }

    //1 - X AXIS
    //2 - Y AXIS
    //3 - Z AXIS
    //4 - X ROTATION

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if(!isController(event.getDevice())){
            return super.dispatchGenericMotionEvent(event);
        }

//        String debug = "---------------- motion ------------------";
//        int i = MotionEvent.AXIS_X;
//        debug = debug + "\n" + MotionEvent.axisToString(i) + " = " + event.getAxisValue(i);
//        i = MotionEvent.AXIS_Y;
//        debug = debug + "\n" + MotionEvent.axisToString(i) + " = " + event.getAxisValue(i);
//        i = MotionEvent.AXIS_Z;
//        debug = debug + "\n" + MotionEvent.axisToString(i) + " = " + event.getAxisValue(i);
//        i = MotionEvent.AXIS_RX;
//        debug = debug + "\n" + MotionEvent.axisToString(i) + " = " + event.getAxisValue(i);
//        writeControllerDebugText(debug);


        float axis_0 = event.getAxisValue(MotionEvent.AXIS_X);  //value -1:1
        axis_0 = (axis_0 + 1.0f) * 0.5f;                        //value 0:1
        setAxisChange(0, axis_0);

        float axis_1 = event.getAxisValue(MotionEvent.AXIS_Y);
        axis_1 = (axis_1 + 1.0f) * 0.5f;
        setAxisChange(1, axis_1);

        float axis_2 = event.getAxisValue(MotionEvent.AXIS_Z);
        axis_2 = (axis_2 + 1.0f) * 0.5f;
        setAxisChange(2, axis_2);

        float axis_3 = event.getAxisValue(MotionEvent.AXIS_RX);
        axis_3 = (axis_3 + 1.0f) * 0.5f;
        setAxisChange(3, axis_3);


        //int historySize = event.getHistorySize();
        //for (int i = 0; i < historySize; i++) {
            // Process the event at historical position i
            //this.addDebugString("JOYSTICKMOVE "+event.getHistoricalAxisValue(MotionEvent.AXIS_Y,i)+"  "+event.getHistoricalAxisValue(MotionEvent.AXIS_Z,i));
           // processJoystickInput(event,  i);
       // }
        // Process current position
        //this.addDebugString("JOYSTICKMOVE "+event.getAxisValue(MotionEvent.AXIS_Y)+" "+event.getAxisValue(MotionEvent.AXIS_Z));

        return true;
    }
    /*
    private void processJoystickInput(MotionEvent event, int historyPos) {

        InputDevice mInputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);
        }

        // Update the ship object based on the new x and y values
    }
    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }
*/
    private boolean isController(InputDevice device) {
        return ((device.getSources() & InputDevice.SOURCE_CLASS_JOYSTICK) == InputDevice.SOURCE_CLASS_JOYSTICK)
                && (((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                || (device.getKeyboardType() != InputDevice.KEYBOARD_TYPE_ALPHABETIC));
    }

    public void writeControllerDebugText(String str){
        MultiAutoCompleteTextView tv = findViewById(R.id.controllerDebugText);
        tv.setText(str);
        Log.i(TAG, str);
    }


    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            while(true){
                serial.writeDataFrame(channelData);
                try {
                    Thread.sleep(1000/30);
                } catch (InterruptedException e) {
                    return "";
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // do something with result
        }
    }

    public void loadAppPreferences(){
        SharedPreferences settings = getSharedPreferences(savedPrefsName, 0);
        channelData.loadSettings(settings);
    }
    public void saveAppPreferences(){
        SharedPreferences settings = getSharedPreferences(savedPrefsName, 0);
        SharedPreferences.Editor editor = settings.edit();
        channelData.saveSettings(editor);
        editor.commit();
    }

    public void onClick(View view){
        String tag = getResources().getResourceEntryName(view.getId());
        if(tag.indexOf("channelButton") >= 0){
            int idx = Integer.parseInt(tag.substring(tag.length() - 1));
            if(idx > 0 && idx <=8){
                initChannelMixer(idx-1);
            }
        }
        if(tag.indexOf("playCheck") >= 0){
            int idx = Integer.parseInt(tag.substring(tag.length() - 1));
            if(idx > 0 && idx <=8){
                channelData.playStop(idx-1, ((CheckBox) view).isChecked());
            }
        }
    }

    public void initChannelMixer(int index){
        editSelectedChannel = index;

        ChannelData.Channel ch = channelData.channels[index];

        ((TextView)findViewById(R.id.textChanAL)).setText(Float.toString(ch.audioALeft));
        ((TextView)findViewById(R.id.textChanAR)).setText(Float.toString(ch.audioARight));
        ((TextView)findViewById(R.id.textChanBL)).setText(Float.toString(ch.audioBLeft));
        ((TextView)findViewById(R.id.textChanBR)).setText(Float.toString(ch.audioBRight));

        ((TextView)findViewById(R.id.titleTextView)).setText("Edit channel "+Integer.toString(index+1));
        findViewById(R.id.channelMixerView).setVisibility(View.VISIBLE);
        findViewById(R.id.closeChannelMixerButton).setVisibility(View.VISIBLE);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getDebugStringJNI();
    public native int createFMODJNI();
    public native int CRC8JNI(int data[], int len);
}
