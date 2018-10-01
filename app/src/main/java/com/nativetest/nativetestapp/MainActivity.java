package com.nativetest.nativetestapp;

import android.os.AsyncTask;
import android.os.Bundle;
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

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity extends AppCompatActivity {

    private SerialComunication serial = new SerialComunication(this);
    public ChannelData channelData = new ChannelData();
    private static final String TAG = "NativetestLog";

    private String debugData = "";

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

        Button sendButton = findViewById(R.id.uartSendButton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //serial.writeDataFrame(channelData);
//                if(channelData.isPlayingSoundJNI(0))channelData.stopSoundJNI(0);
//                else channelData.playSoundJNI(0);
            }
        });

        class DimmerBarChangeListener implements SeekBar.OnSeekBarChangeListener{
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                int idx = -1;
                switch (seekBar.getId()){
                    case R.id.dimmerBar1: idx = 0;  break;
                    case R.id.dimmerBar2: idx = 1;  break;
                    case R.id.dimmerBar3: idx = 2;  break;
                    case R.id.dimmerBar4: idx = 3;  break;
                    case R.id.dimmerBar5: idx = 4;  break;
                    case R.id.dimmerBar6: idx = 5;  break;
                    case R.id.dimmerBar7: idx = 6;  break;
                    case R.id.dimmerBar8: idx = 7;  break;
                }
                if(idx >= 0)channelData.setChannelValue(idx, progress);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  }
        }

        class PlaySoundCheckBoxListener implements View.OnClickListener{
            @Override
            public void onClick(View v){
                int idx = -1;
                switch (v.getId()){
                    case R.id.playCheck1: idx = 0; break;
                    case R.id.playCheck2: idx = 1; break;
                    case R.id.playCheck3: idx = 2; break;
                    case R.id.playCheck4: idx = 3; break;
                    case R.id.playCheck5: idx = 4; break;
                    case R.id.playCheck6: idx = 5; break;
                    case R.id.playCheck7: idx = 6; break;
                    case R.id.playCheck8: idx = 7; break;
                }
                if(idx >= 0)channelData.playStop(idx, ((CheckBox) v).isChecked());
            }
        }

        DimmerBarChangeListener dimmerBarChangeListener = new DimmerBarChangeListener();
        PlaySoundCheckBoxListener playSoundCheckBoxListener = new PlaySoundCheckBoxListener();

        findViewById(R.id.playCheck1).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar1)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck2).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar2)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck3).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar3)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck4).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar4)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck5).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar5)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck6).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar6)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck7).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar7)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        findViewById(R.id.playCheck8).setOnClickListener(playSoundCheckBoxListener);
        ((SeekBar) findViewById(R.id.dimmerBar8)).setOnSeekBarChangeListener(dimmerBarChangeListener);

        this.addDebugString("Starting Native Test App");

///////////////////////////////////////////////////////////////////////
// Debug
//        channelData.val0 = 1;
//        channelData.val1 = 2;
//        channelData.val2 = 3;
//        channelData.val3 = 4;
//        serial.writeDataFrame(channelData);

//        int[] crcInData = {255,122,54};
//        int crc_val = CRC8JNI(crcInData, 3);
//        this.addDebugString("CRC = "+Integer.toString(crc_val));

//        byte out = bytetestJNI((byte)60);
//        this.addDebugString("int 60 = "+Byte.toString(out));
//        out = bytetestJNI((byte)128);
//        this.addDebugString("int 128 = "+Byte.toString(out));
//        out = bytetestJNI((byte)255);
//        this.addDebugString("int 255 = "+Byte.toString(out));
//        out = bytetestJNI((byte)185);
//        this.addDebugString("int 185 = "+Byte.toString(out));

//        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
//        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
//
//            String name = usbDevice.getDeviceName();
//            String productName = usbDevice.getProductName();
//            String serialNumber = usbDevice.getSerialNumber();
//
//            this.addDebugString("USB Device name: "+name);
//            this.addDebugString(" Product name: "+productName);
//            this.addDebugString(" Serial number: "+serialNumber);
//        }

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

        this.updateJNIDebugStrings();

        new MyTask().execute("test");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getDebugStringJNI();
    public native int createFMODJNI();
    public native int CRC8JNI(int data[], int len);
}
