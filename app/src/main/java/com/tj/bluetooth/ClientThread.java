package com.tj.bluetooth;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClientThread extends Thread{
    private String message;
    private BluetoothDevice device;
    ClientThread(String message,BluetoothDevice device)
    {
        this.message = message;
        this.device = device;
    }
    @Override
    public void run()
    {
        //device.createRfcommSocketToServiceRecord()
       // final BluetoothSocket socket = (BluetoothSocket) device.getClass().getDeclaredMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
    }
}
