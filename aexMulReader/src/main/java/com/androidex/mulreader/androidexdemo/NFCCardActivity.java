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
 * Created by Administrator on 2018/7/20.
 */

public class NFCCardActivity extends Activity{
    private SerialPortUtils serialPortUtils;
    private String path = "/dev/ttyS0";
    private int baudrate = 115200;
    private SerialPort serialPort;
    private TextView nfcNumber;
    private boolean isRun = true;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x01){
                byte[] recData = (byte[]) msg.obj;
                String hexMessage = ChangeTool.ByteArrToHex(recData);
                if(recData!=null && recData.length>=9 && recData[9] != -97){
                    showMsg(hexMessage);
                    String[] arrryHex = hexMessage.split("-");
                    if(arrryHex.length>=14){
                        String cardNumber = arrryHex[10]+arrryHex[11]+arrryHex[12]+arrryHex[13];
                        nfcNumber.setText(cardNumber);
                    }
                }
            }
        }
    };

    private class ThreadRun implements Runnable{
        @Override
        public void run() {
            while(isRun){
                try{
                    Thread.sleep(1000);
                    if(isRun){
                        ReadNFCCard();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        nfcNumber =findViewById(R.id.nfc_number);
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
            showToast("指纹串口打开异常");
            this.finish();
        }else{
            showMsg("指纹串口打开成功");
        }
        new Thread(new ThreadRun()).start();
    }

    private void ReadNFCCard(){
        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(DevConfig.NFC_FIND));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        if(serialPort != null){
            serialPortUtils.closeSerialPort();
        }
    }

    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NFCCardActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMsg(String msg){
        Log.i("xiao_",msg);
    }
}
