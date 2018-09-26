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

//<permissions>
//        <feature name="android.hardware.usb.host"/>

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NativetestLog";

    private String debugData = "";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public void addDebugString(String str){
        this.debugData = this.debugData + str + "\n";
        MultiAutoCompleteTextView tv = findViewById(R.id.multiAutoCompleteTextView);
        tv.setText(this.debugData);
        Log.i(TAG, str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.addDebugString("Starting Native Test App");

        stringFromJNI();
        doSomeWorkJNI();
        this.addDebugString(this.getDebugStringJNI());

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String getDebugStringJNI();
    public native String doSomeWorkJNI();
}
