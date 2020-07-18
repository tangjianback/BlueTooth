package com.tj.bluetooth;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public class ConnectThread extends Thread {
    private String TAG = "ConnectThread";
    private BluetoothSocket socket = null;
    private Boolean activeConnect = null;
    ConnectThread(BluetoothSocket socket,Boolean activeConnect)
    {
        super();
        this.activeConnect = activeConnect;
        this.socket = socket;
    }
    @Override
    public void run()
    {
        //如果是主动连接 则调用连接方法
        try{
            if (activeConnect) {
                socket.connect();
                Log.d(TAG, "run: "+"the boy is connect the girl successful!!!");

            }
        }catch (java.io.IOException e)
        {
            e.printStackTrace();
            Log.d(TAG, "run: "+"connect failled");
            return ;
        }
        ///两个连接会用下面的连接socket!

        ///如果是boy的话
        try{
            if(activeConnect) {
                OutputStream os = socket.getOutputStream();
                for(int i = 0;i<=10;i++)
                {
                    os.write(("i had a crush on you when we were young!"+i).getBytes());
                    Log.d(TAG, "run: "+"i am the boy ,i am already said i love you!"+i);
                    os.flush();
                    try{
                        Thread.sleep(10000);
                    }catch(java.lang.InterruptedException e){
                        e.printStackTrace();
                    }

                }

                os.close();

            }
            ///如果是girl 的话，那么就循环的监听这个inputstream是否可以接受数据。
            else{

                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[1024];

                ///如果得到的数据数目是大于0的那么就读取咯
                int read_len;
                while((read_len = is.read(buffer))>0){

                    final byte[] data = new byte[read_len];

                    System.arraycopy(buffer, 0, data, 0, read_len);

                    Log.d(TAG, "run: "+"i am the girl, i get the message you leave me:"+new String(data));

                }
                is.close();


            }

        }catch (java.io.IOException e)
        {
            e.printStackTrace();
        }

        try{
            socket.close();
        }catch (java.io.IOException e)
        {
            e.printStackTrace();
        }





    }
}
