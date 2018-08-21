package com.androidex.lockaxial.androidexdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.lockaxial.utils.ChangeTool;
import com.androidex.lockaxial.utils.SerialPortUtils;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/7/30.
 */

public class KeyBoardActivity extends Activity {
    private TextView inputText;
    private SerialPortUtils serialPortUtils;
    private String path = "/dev/ttyS4";
    private int baudrate = 9600;
    private SerialPort serialPort;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            byte data[] = (byte[]) msg.obj;
            String hexMessage = ChangeTool.ByteArrToHex(data);
            inputText.append(getCode(hexMessage));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        inputText = findViewById(R.id.inputtext);
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                Message message = Message.obtain();
                message.obj = buffer;
                message.what = 0x01;
                mHandler.sendMessage(message);
            }
        });
        serialPort = serialPortUtils.openSerialPort(path,baudrate);
        if(serialPort == null){
            showToast("按键板串口打开异常");
            this.finish();
        }else{
            showMsg("按键板口打开成功");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serialPort!=null){
            serialPortUtils.closeSerialPort();
        }
    }

    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(KeyBoardActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMsg(String msg){
        Log.i("xiao_",msg);
    }


    private String getCode(String c){
        String code = "";
        if(c.equals("31-")){
            code = "1";
        }else if(c.equals("32-")){
            code = "2";
        }else if(c.equals("33-")){
            code = "3";
        }else if(c.equals("34-")){
            code = "4";
        }else if(c.equals("35-")){
            code = "5";
        }else if(c.equals("36-")){
            code = "6";
        }else if(c.equals("37-")){
            code = "7";
        }else if(c.equals("38-")){
            code = "8";
        }else if(c.equals("39-")){
            code = "9";
        }else if(c.equals("30-")){
            code = "0";
        }else if(c.equals("2A-")){
            code = "*";
        }else if(c.equals("23-")){
            code = "#";
        }else if(c.equals("70-")){
            showToast("F1");
        }else if(c.equals("71-")){
            showToast("F2");
        }else if(c.equals("72-")){
            showToast("F3");
        }else if(c.equals("73-")){
            showToast("F4");
        }else if(c.equals("74-")){
            showToast("F5");
        }else if(c.equals("75-")){
            showToast("F6");
        }else if(c.equals("76-")){
            showToast("F7");
        }
        return code;
    }
}
