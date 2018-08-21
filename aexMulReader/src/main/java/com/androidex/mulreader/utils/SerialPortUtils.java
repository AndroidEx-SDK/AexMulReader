package com.androidex.lockaxial.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android_serialport_api.SerialPort;


public class SerialPortUtils {

    private final String TAG = "SerialPortUtils";
    //private String path = "/dev/ttyS6";
    //private int baudrate = 19200;
    public boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    public ChangeTool changeTool = new ChangeTool();


    /**
     * 打开串口
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort(String path,int baudrate){
        try {
                serialPort = new SerialPort(new File(path),baudrate,0);
                this.serialPortStatus = true;
                threadStatus = false; //线程状态

                //获取打开的串口中的输入输出流，以便于串口数据的收发
                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();

                new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口");
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常："+e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    public boolean sendSerialPort(byte data[]){
        try{
            if(data!=null && data.length>0){
                outputStream.write(data);
                //outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }



    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                //64   1024
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    int size; //读取数据的大小
                    size = inputStream.read(buffer);
                    if (size > 0){
                        onDataReceiveListener.onDataReceive(buffer,size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

}
