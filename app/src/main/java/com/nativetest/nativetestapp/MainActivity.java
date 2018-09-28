package com.nativetest.nativetestapp;

import android.os.Bundle;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MenuItem;

import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.List;

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NativetestLog";

//    private ExecutorService fixedThreadPool;
//    private PlayerThread playerThread;
    private String debugData = "";

    private UartDevice mDevice = null;

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

        this.addDebugString("Starting Native Test App");

        //https://github.com/mik3y/usb-serial-for-android/tree/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver
        UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);

        for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {

            String name = usbDevice.getDeviceName();
            String productName = usbDevice.getProductName();
            String serialNumber = usbDevice.getSerialNumber();

            this.addDebugString("USB Device name: "+name);
            this.addDebugString(" Product name: "+productName);
            this.addDebugString(" Serial number: "+serialNumber);
        }

        checkController();

        //UART Devices
        try{
            PeripheralManager manager = PeripheralManager.getInstance();
        }catch(Exception i){
            this.addDebugString(i.getMessage());
        }
        //this.addDebugString(manager.toString());
//        List<String> deviceList = manager.getUartDeviceList();
//        if(deviceList.isEmpty()){
//            this.addDebugString("No UART port available on this device.");
//        }else{
//            for (int i=0; i<deviceList.size(); i++){
//                this.addDebugString("UART Device: " + deviceList.get(i));
//            }
//        }

        org.fmod.FMOD.init(this);

//        mThread = new Thread(this, "Example Main");
//        mThread.start();
//
//        setStateCreate();

        createFMODJNI();

        loadSoundJNI("file:///android_asset/test1.wav",0);
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

        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                this.addDebugString("Unable to close UART device: "+ e);
            }
        }
    }
/*
    public void openUARTDevice(String devicename) throws IOException {
        // Attempt to access the UART device
        try{
            PeripheralManager manager = PeripheralManager.getInstance();
            mDevice = manager.openUartDevice(devicename);
        }catch (IOException e){
            this.addDebugString("Unable to access UART device: "+e);
        }
        if(mDevice != null) {
            mDevice.setBaudrate(115200);
            mDevice.setDataSize(8);
            mDevice.setParity(UartDevice.PARITY_NONE);
            mDevice.setStopBits(1);
            mDevice.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);
        }
    }
    public void writeUARTData(byte data[]) throws IOException {
        if(mDevice != null){
            mDevice.write(data, data.length);
        }
    }
    */

    public void checkController(){
        for(int deviceId: InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(deviceId);
            String controllerName = device.getName();
            this.addDebugString("Controller: "+controllerName+" is constroller "+isController(device));
        }
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if(!isController(event.getDevice())){
        //if((event.getSource()&InputDeviceCompat.SOURCE_JOYSTICK)!=InputDeviceCompat.SOURCE_JOYSTICK ){
            return super.dispatchGenericMotionEvent(event);
        }

        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            // Process the event at historical position i
            this.addDebugString("JOYSTICKMOVE "+event.getHistoricalAxisValue(MotionEvent.AXIS_Y,i)+"  "+event.getHistoricalAxisValue(MotionEvent.AXIS_Z,i));
            processJoystickInput(event,  i);
        }
        // Process current position
        this.addDebugString("JOYSTICKMOVE "+event.getAxisValue(MotionEvent.AXIS_Y)+" "+event.getAxisValue(MotionEvent.AXIS_Z));

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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getDebugStringJNI();
    public native int loadSoundJNI(String filename, int index);
    public native int createFMODJNI();

}
