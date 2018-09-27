package com.nativetest.nativetestapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.view.InputDevice;
import android.view.InputDevice.MotionRange;
import android.view.MotionEvent;

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity extends AppCompatActivity implements View.OnGenericMotionListener {

    private static final String TAG = "NativetestLog";

//    private ExecutorService fixedThreadPool;
//    private PlayerThread playerThread;
    private String debugData = "";

    // Used to load the 'native-lib' library on application startup.
    static {
        /*
         * To simplify our examples we try to load all possible FMOD
         * libraries, the Android.mk will copy in the correct ones
         * for each example. For real products you would just load
         * 'fmod' and if you use the FMOD Studio tool you would also
         * load 'fmodstudio'.
         */

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        initController();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.switch_console) {
            MultiAutoCompleteTextView tv = findViewById(R.id.multiAutoCompleteTextView);
            if(item.isChecked()){
                tv.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(View.INVISIBLE);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initController(){

        for(int deviceId: InputDevice.getDeviceIds()) {

            InputDevice device = InputDevice.getDevice(deviceId);

            String controllerName = device.getName();

            this.addDebugString("Controller: "+controllerName);

        }

        //this.addGenericMotionListener(this);
    }
    @Override
    public boolean onGenericMotion (View view, MotionEvent motionEvent) {

        return true;
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getDebugStringJNI();
    public native int loadSoundJNI(String filename, int index);
    public native int createFMODJNI();

}
