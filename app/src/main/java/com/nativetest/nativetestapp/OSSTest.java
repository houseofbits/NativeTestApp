package com.nativetest.nativetestapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import static android.content.Context.USB_SERVICE;

public class OSSTest {

    public MainActivity main;
    public UsbManager usbManager = null;
    public UsbDevice audioDevice = null;
    public UsbDeviceConnection usbDeviceConnection = null;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            boolean deviceOpen = openDevice(device);
                            if(!deviceOpen){
//                                main.addDebugString("UART Error re-opening device. Try to restart app");
                            }
                        }
                    }
                    else {
                   //     main.addDebugString("UART permission denied for device " + device);
                    }
                }
            }
        }
    };

    public OSSTest(MainActivity m){
        main = m;
    }

    public void connectDevice(){

        main.addDebugString("========== Connect to OSS device ===========");

        usbManager = (UsbManager) main.getSystemService(USB_SERVICE);

        for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            //if(usbDevice.getDeviceClass() == UsbConstants.USB_CLASS_AUDIO) {

                String name = usbDevice.getDeviceName();
                String productName = usbDevice.getProductName();
                String serialNumber = usbDevice.getSerialNumber();
                main.addDebugString("USB Audio device name: " + name+" class:"+usbDevice.getDeviceClass());
                main.addDebugString(" Product name: " + productName);
                main.addDebugString(" Serial number: " + serialNumber);

                if(audioDevice == null)audioDevice = usbDevice;
//                break;
            //}
        }
        if(audioDevice != null){
            if(usbManager.hasPermission(audioDevice)){
                openDevice(audioDevice);
            }else{
                PendingIntent mPermissionIntent;
                mPermissionIntent = PendingIntent.getBroadcast(this.main, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                main.registerReceiver(mUsbReceiver, filter);
                usbManager.requestPermission(audioDevice, mPermissionIntent);
            }
        }

    }

    public boolean openDevice(UsbDevice dev) {
        if(dev == null)return false;
        usbDeviceConnection = usbManager.openDevice(dev);
        if (usbDeviceConnection == null)return false;

        int devFd = usbDeviceConnection.getFileDescriptor();
        main.addDebugString("  Device is open: fd "+Integer.toString(usbDeviceConnection.getFileDescriptor()));
        if(devFd >= 0){

            int interfaceNum = dev.getInterfaceCount();
            for(int i=0; i<interfaceNum; i++){
                UsbInterface usbInterface = dev.getInterface(i);
                main.addDebugString(" Interface "+usbInterface.getName()+" class:"+usbInterface.getInterfaceClass());
                int endpoints = usbInterface.getEndpointCount();
                for (int a=0; a<endpoints; a++){
                    UsbEndpoint usbEndpoint = usbInterface.getEndpoint(a);

                    String inOut = ((usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT)?"USB_DIR_OUT":"USB_DIR_IN");
                    String typeStr = "";
                    switch(usbEndpoint.getType()){
                        case UsbConstants.USB_ENDPOINT_XFER_CONTROL:    // (endpoint zero)
                            typeStr = "USB_ENDPOINT_XFER_CONTROL";
                            break;
                        case UsbConstants.USB_ENDPOINT_XFER_ISOC:       // (isochronous endpoint)
                            typeStr = "USB_ENDPOINT_XFER_ISOC";
                            break;
                        case UsbConstants.USB_ENDPOINT_XFER_BULK:       // (bulk endpoint)
                            typeStr = "USB_ENDPOINT_XFER_BULK";
                            break;
                        case UsbConstants.USB_ENDPOINT_XFER_INT:        // (interrupt endpoint)
                            typeStr = "USB_ENDPOINT_XFER_INT";
                            break;
                    }

                    main.addDebugString("  Endpoint "+usbEndpoint.getEndpointNumber()+" "+inOut+" "+typeStr);
                }
            }

            connectOSSDeviceJNI(devFd, 5);

        }

        main.updateJNIDebugStrings();
        return true;
    }

    public native int connectOSSDeviceJNI(int devFd, int endpointNum);
}
