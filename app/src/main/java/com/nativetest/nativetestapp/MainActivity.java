package com.nativetest.nativetestapp;

import android.os.Bundle;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MenuItem;

import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;

import com.nativetest.nativetestapp.driver.UsbSerialDriver;
import com.nativetest.nativetestapp.driver.UsbSerialPort;
import com.nativetest.nativetestapp.driver.UsbSerialProber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity extends AppCompatActivity {

    private SerialComunication serial = new SerialComunication(this);
    public ChannelData channelData = new ChannelData();
//    private UsbManager mUsbManager;
//    private UsbSerialPort serialPort = null;
    private static final String TAG = "NativetestLog";

//    private ExecutorService fixedThreadPool;
//    private PlayerThread playerThread;
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

//    class PlayerThread implements Runnable {
//        @Override
//        public void run() {
//
//        }
//    }

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
                serial.writeDataFrame(channelData);
            }
        });

        class DimmerBarChangeListener implements SeekBar.OnSeekBarChangeListener {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()){
                    case R.id.dimmerBar1:
                        channelData.val0 = progress;
                        break;
                    case R.id.dimmerBar2:
                        channelData.val1 = progress;
                        break;
                    case R.id.dimmerBar3:
                        channelData.val2 = progress;
                        break;
                    case R.id.dimmerBar4:
                        channelData.val3 = progress;
                        break;
                    case R.id.dimmerBar5:
                        channelData.val4 = progress;
                        break;
                    case R.id.dimmerBar6:
                        channelData.val5 = progress;
                        break;
                    case R.id.dimmerBar7:
                        channelData.val6 = progress;
                        break;
                    case R.id.dimmerBar8:
                        channelData.val7 = progress;
                        break;
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  }
        }
        DimmerBarChangeListener dimmerBarChangeListener = new DimmerBarChangeListener();

        SeekBar bar1 = (SeekBar) findViewById(R.id.dimmerBar1);
        bar1.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar2 = (SeekBar) findViewById(R.id.dimmerBar2);
        bar2.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar3 = (SeekBar) findViewById(R.id.dimmerBar3);
        bar3.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar4 = (SeekBar) findViewById(R.id.dimmerBar4);
        bar4.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar5 = (SeekBar) findViewById(R.id.dimmerBar5);
        bar5.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar6 = (SeekBar) findViewById(R.id.dimmerBar6);
        bar6.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar7 = (SeekBar) findViewById(R.id.dimmerBar7);
        bar7.setOnSeekBarChangeListener(dimmerBarChangeListener);

        SeekBar bar8 = (SeekBar) findViewById(R.id.dimmerBar8);
        bar8.setOnSeekBarChangeListener(dimmerBarChangeListener);

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
// ???
//        mThread = new Thread(this, "Example Main");
//        mThread.start();

//        createFMODJNI();
//        loadSoundJNI("file:///android_asset/test1.wav",0);
//        loadSoundJNI("file:///android_asset/test2.wav",1);
//        loadSoundJNI("file:///android_asset/test3.wav",2);
//        loadSoundJNI("file:///android_asset/test4.wav",3);
//        loadSoundJNI("file:///android_asset/test5.wav",4);
//        loadSoundJNI("file:///android_asset/test6.wav",5);
//        loadSoundJNI("file:///android_asset/test7.wav",6);
//        loadSoundJNI("file:///android_asset/test8.wav",7);

        this.updateJNIDebugStrings();

        //fixedThreadPool = Executors.newFixedThreadPool(1);
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
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if(!isController(event.getDevice())){
            return super.dispatchGenericMotionEvent(event);
        }

//        writeControllerDebugText("0: "+event.getAxisValue(MotionEvent.AXIS_X));
//        writeControllerDebugText("1: "+event.getAxisValue(MotionEvent.AXIS_Y));
//        writeControllerDebugText("2: "+event.getAxisValue(MotionEvent.AXIS_Z));

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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getDebugStringJNI();
    public native int loadSoundJNI(String filename, int index);
    public native int createFMODJNI();
    public native int CRC8JNI(int data[], int len);
    public native byte bytetestJNI(byte in);

}
