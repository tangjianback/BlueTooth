package com.tj.bluetooth;
import android.bluetooth.*;
import android.util.Log;

import java.util.UUID;

public class ListenerThread extends Thread {
    private String TAG = "ListenerThread";
    private BluetoothServerSocket serverSocket = null;
    private BluetoothSocket socket = null;

    private BluetoothAdapter bTAdapter = null;
    private UUID BT_UUID = null;

    ListenerThread(UUID BT_UUID,BluetoothAdapter bTAdapter){

        super();
        this.BT_UUID = BT_UUID;

        this.bTAdapter = bTAdapter;
    }


    @Override
    public void run() {
        try {
            serverSocket = bTAdapter.listenUsingRfcommWithServiceRecord(
                    "huawei_1", BT_UUID);
            while (true) {
                //线程阻塞，等待别的设备连接

                socket = serverSocket.accept();


                ConnectThread connectThread = new ConnectThread(socket, false);

                connectThread.start();

                Log.d(TAG, "run: "+"the girl get the  request successful !!");

            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
