package com.tj.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.widget.Button;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener {
    private final static int REQUEST_ENABLE_BT = 1;
    private String TAG  = "MainActivity";
    private boolean is_boy = false;

    private BluetoothAdapter mBluetoothAdapter = null;


    private BluetoothDevice Device = null;
    private java.util.UUID BT_UUID = UUID.fromString("b10e4ce8-7506-4efc-b18c-b6c2bc35e811");

    private String remote_device_address = "7C:7D:3D:86:2C:09";
    //private final BroadcastReceiver pinBlueReceiver = new PinBlueReceiver(new PinBlueCallBack());

    private final BroadcastReceiver pinBlueReceiver = new PinBlueReceiver(new PinBlueCallBack());

    private final BroadcastReceiver mReceiver_for_device =  new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();


            if(BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG,"可配对设备:"+device.getName() + "\n" + device.getAddress());
            }




        }
    };

    private final BroadcastReceiver mReceiver_for_start =  new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent){


            Log.d(TAG, "onReceive: "+"start to find  some device");

        }
    };
    private final BroadcastReceiver mReceiver_for_finish =  new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent){

            Log.d(TAG, "onReceive: "+"finish discover");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applypermission();

        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);


        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver_for_device, filter1);

        IntentFilter filter2 = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver_for_start, filter2);

        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver_for_finish, filter3);

        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pinBlueReceiver,filter4);
        registerReceiver(pinBlueReceiver,filter5);
        Log.d(TAG, "onCreate: "+BT_UUID);


    }


    /**
     * 配对（配对成功与失败通过广播返回）
     * @param device
     */
    public void pin(BluetoothDevice device){
        if (device == null){
            Log.e(TAG, "bond device null");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Log.e(TAG, "Bluetooth not enable!");
            return;
        }
        //配对之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to bond:" + device.getName());
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                Log.d(TAG, "pin: "+"bond return value is "+returnValue);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "attemp to bond fail!");
            }
        }
    }
    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     * @param device
     */
    public void cancelPinBule(BluetoothDevice device){
        if (device == null){
            Log.d(TAG, "cancel bond device null");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "Bluetooth not enable!");
            return;
        }
        //判断设备是否配对，没有配对就不用取消了
        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attemp to cancel bond:" + device.getName());
            try {
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
                Log.d(TAG, "cancelPinBule: return value is "+returnValue);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "attemp to cancel bond fail!");
            }
        }
    }
    /**
     * 连接蓝牙设备
     */
    private void connectDevice(BluetoothDevice device) {

        //text_state.setText(getResources().getString(R.string.connecting));

        try {
            //创建Socket
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(BT_UUID);

            //启动连接线程(主动去连接)
            Thread connectThread = new ConnectThread(socket, true);

            connectThread.start();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            ///start scan
            case R.id.button1:
                //startDiscovery();
                android.widget.Toast.makeText(MainActivity.this," opening bluetooth",android.widget.Toast.LENGTH_SHORT).show();


                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                break;
            ///start broadcast
            case R.id.button2:
                //startAdvertising();

                android.widget.Toast.makeText(MainActivity.this," scan!!",android.widget.Toast.LENGTH_SHORT).show();

                if(mBluetoothAdapter == null || !mBluetoothAdapter.startDiscovery())
                {
                    Log.d(TAG, "onClick: open scan failed!!");
                }

                //Log.d(TAG, "onClick: "+_bluetooth.getBondedDevices().toString());
                break;


            case R.id.button3:

                if(!is_boy)
                {
                    android.widget.Toast.makeText(MainActivity.this," sorry you can not pair!",android.widget.Toast.LENGTH_SHORT).show();
                    break;
                }

                android.widget.Toast.makeText(MainActivity.this," begin to pair!!",android.widget.Toast.LENGTH_SHORT).show();

                Device =  mBluetoothAdapter.getRemoteDevice(remote_device_address);

                pin(Device);
//                try{
//                    ClsUtils.createBond(Device.getClass(),Device);
//                }catch(Exception e)
//                {
//                    e.printStackTrace();
//                }
                break;
            case R.id.button4:
                // 如果是主动的连接一方。
                if(is_boy)
                {
                    android.widget.Toast.makeText(MainActivity.this," begin to connect!!",android.widget.Toast.LENGTH_SHORT).show();
                    connectDevice(Device);
                }
                // 如果是被动监听的一方。
                else {
                    android.widget.Toast.makeText(MainActivity.this, " begin to connect!!", android.widget.Toast.LENGTH_SHORT).show();
                    Thread listen_thread = new ListenerThread(BT_UUID, mBluetoothAdapter);
                    listen_thread.start();

                }
                break;
            case R.id.button5:
                android.widget.Toast.makeText(MainActivity.this, " i am the boy!!1", android.widget.Toast.LENGTH_SHORT).show();
                is_boy = true;
                break;









            default:
                break;
        }

    }
    public void applypermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //检查是否已经给了权限
            int checkpermission = androidx.core.content.ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkpermission != android.content.pm.PackageManager.PERMISSION_GRANTED) {//没有给权限
                Log.e("permission", "动态申请");
                //参数分别是当前活动，权限字符串数组，requestcode
                androidx.core.app.ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.widget.Toast.makeText(MainActivity.this, "已授权", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            android.widget.Toast.makeText(MainActivity.this, "拒绝授权", android.widget.Toast.LENGTH_SHORT).show();
        }

    }



}
