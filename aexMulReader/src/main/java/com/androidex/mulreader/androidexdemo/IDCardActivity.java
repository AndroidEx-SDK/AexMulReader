package com.androidex.lockaxial.androidexdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.lockaxial.utils.AppConfig;
import com.androidex.lockaxial.utils.ChangeTool;
import com.androidex.lockaxial.utils.CopyFileToSD;
import com.androidex.lockaxial.utils.NationDeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ivsign.android.IDCReader.IDCReaderSDK;
import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/7/19.
 */

public class IDCardActivity extends Activity{
    private ImageView headImage;
    private TextView name,sex,nation,birthday,address,idNumber,outfit,availability;

    private String path = "/dev/ttyS0";
    private int baudrate = 115200;
    private SerialPort serialPort = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private boolean isRun = true;
    private int Readflage = -99;
    byte[] recData = new byte[1500];
    String[] decodeInfo = new String[10];
    private class ThreadRun implements Runnable{
        @Override
        public void run() {
            while(isRun){
                try{
                    Thread.sleep(1000);
                    if(isRun){
                        ReadCard();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    private Context mContext;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what!=0){
                return;
            }
            try {
                if(Readflage > 0) {
                    name.setText(decodeInfo[0]);
                    sex.setText(decodeInfo[1]);
                    nation.setText(decodeInfo[2]);
                    birthday.setText(decodeInfo[3]);
                    address.setText(decodeInfo[4]);
                    idNumber.setText(decodeInfo[5]);
                    outfit.setText(decodeInfo[6]);
                    availability.setText(decodeInfo[7]+"--"+decodeInfo[8]);
                    showMsg("不知道什么东西的数据："+decodeInfo[9]);
                    if (Readflage == 1) {
                        FileInputStream fis = new FileInputStream(
                                Environment.getExternalStorageDirectory()
                                        + "/wltlib/zp.bmp");
                        Bitmap bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
                        headImage.setImageBitmap(bmp);
                    } else {
                        showToast("照片解码失败，请检查路径");
                        showMsg("照片解码失败，请检查路径"+ AppConfig.RootFile);
                        headImage.setImageBitmap(BitmapFactory.decodeResource(
                                getResources(), R.drawable.face));
                    }
                }else{
                    headImage.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.face));
                    if (Readflage == -2) {
                        showMsg("连接异常");
                    }
                    if (Readflage == -3) {
                        showMsg("无卡或卡片已读过");
                    }
                    if (Readflage == -4) {
                        showMsg("无卡或卡片已读过");
                    }
                    if (Readflage == -5) {
                        showMsg("读卡失败");
                    }
                    if (Readflage == -99) {
                        showMsg("操作异常");
                    }
                }
                Thread.sleep(100);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                showMsg("读取数据异常！");
                headImage.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.face));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                showMsg("读取数据异常！");
                headImage.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.face));
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        mContext = this;
        CopyFileToSD cFileToSD = new CopyFileToSD();
        cFileToSD.initDB(this);
        initView();
        openSerialPort();
        new Thread(new ThreadRun()).start();
    }

    private void ReadCard(){
        try {
            if ((mInputStream == null) || (mInputStream == null)) {
                Readflage = -2;// 连接异常
                return;
            }
            mOutputStream.write(ChangeTool.HexToByteArr(DevConfig.IDCARD_FIND));
            Thread.sleep(200);
            int datalen = mInputStream.read(recData);
            if (recData[9] == -97) {
                mOutputStream.write(ChangeTool.HexToByteArr(DevConfig.IDCARD_SELT));
                Thread.sleep(200);
                datalen = mInputStream.read(recData);
                if (recData[9] == -112) {
                    mOutputStream.write(ChangeTool.HexToByteArr(DevConfig.IDCARD_READ));
                    Thread.sleep(1000);
                    byte[] tempData = new byte[1500];

                    //写完命令后，保存回数据到tempData
                    if (mInputStream.available() > 0) {
                        datalen = mInputStream.read(tempData);
                    } else {
                        Thread.sleep(500);
                        if (mInputStream.available() > 0) {
                            datalen = mInputStream.read(tempData);
                        }
                    }


                    int flag = 0;
                    if (datalen < 1294) {
                        for (int i = 0; i < datalen; i++, flag++) {
                            recData[flag] = tempData[i];
                        }
                        Thread.sleep(1000);
                        if (mInputStream.available() > 0) {
                            datalen = mInputStream.read(tempData);
                        } else {
                            Thread.sleep(500);
                            if (mInputStream.available() > 0) {
                                datalen = mInputStream.read(tempData);
                            }
                        }
                        for (int i = 0; i < datalen; i++, flag++) {
                            recData[flag] = tempData[i];
                        }

                    } else {
                        for (int i = 0; i < datalen; i++, flag++) {
                            recData[flag] = tempData[i];
                        }
                    }
                    tempData = null;
                    if(flag == 1295 || flag == 1297 || flag == 2321){
                        if (recData[9] == -112) {

                            byte[] dataBuf = new byte[256];
                            for (int i = 0; i < 256; i++) {
                                dataBuf[i] = recData[14 + i];
                            }
                            String TmpStr = new String(dataBuf, "UTF16-LE");
                            TmpStr = new String(TmpStr.getBytes("UTF-8"));
                            decodeInfo[0] = TmpStr.substring(0, 15);
                            decodeInfo[1] = TmpStr.substring(15, 16);
                            decodeInfo[2] = TmpStr.substring(16, 18);
                            decodeInfo[3] = TmpStr.substring(18, 26);
                            decodeInfo[4] = TmpStr.substring(26, 61);
                            decodeInfo[5] = TmpStr.substring(61, 79);
                            decodeInfo[6] = TmpStr.substring(79, 94);
                            decodeInfo[7] = TmpStr.substring(94, 102);
                            decodeInfo[8] = TmpStr.substring(102, 110);
                            decodeInfo[9] = TmpStr.substring(110, 128);
                            if (decodeInfo[1].equals("1"))
                                decodeInfo[1] = "男";
                            else
                                decodeInfo[1] = "女";
                            try {
                                int code = Integer.parseInt(decodeInfo[2]
                                        .toString());
                                decodeInfo[2] = NationDeal.decodeNation(code);
                            } catch (Exception e) {
                                decodeInfo[2] = "";
                            }

                            // 照片解码
                            try {
                                int ret = IDCReaderSDK.Init();
                                if (ret == 0){
                                    byte[] datawlt = new byte[1384];
                                    byte[] byLicData = { (byte) 0x05,
                                            (byte) 0x00, (byte) 0x01,
                                            (byte) 0x00, (byte) 0x5B,
                                            (byte) 0x03, (byte) 0x33,
                                            (byte) 0x01, (byte) 0x5A,
                                            (byte) 0xB3, (byte) 0x1E,
                                            (byte) 0x00 };
                                    for (int i = 0; i < 1295; i++) {
                                        datawlt[i] = recData[i];
                                    }
                                    int t = IDCReaderSDK.unpack(datawlt,
                                            byLicData);
                                    if (t == 1) {
                                        Readflage = 1;// 读卡成功
                                    } else {
                                        Readflage = 6;// 照片解码异常
                                    }
                                } else {
                                    Readflage = 6;// 照片解码异常
                                }
                            } catch (Exception e) {
                                Readflage = 6;// 照片解码异常
                            }
                            mHandler.sendEmptyMessage(0);
                        } else {
                            Readflage = -5;// 读卡失败！
                        }
                    } else {
                        Readflage = -5;// 读卡失败
                    }
                } else {
                    Readflage = -4;// 选卡失败
                }
            } else {
                Readflage = -3;// 寻卡失败
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            Readflage = -99;// 读取数据异常
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Readflage = -99;// 读取数据异常
        }
    }

    private void openSerialPort(){
        try{
            serialPort = new SerialPort(new File(path),baudrate,0);
            mInputStream = serialPort.getInputStream();
            mOutputStream = serialPort.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
            showToast("串口打开异常，请稍后重试");
            this.finish();
        }
    }

    private void initView(){
        headImage = findViewById(R.id.head_image);
        name = findViewById(R.id.name_);
        sex = findViewById(R.id.sex_);
        nation = findViewById(R.id.nation_);
        birthday = findViewById(R.id.birthday_);
        address = findViewById(R.id.address_);
        idNumber = findViewById(R.id.idcard_);
        outfit = findViewById(R.id.outfit_);
        availability = findViewById(R.id.availability_);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
        try{
            if(mInputStream!=null){
                mInputStream.close();
            }
            if(mOutputStream!=null){
                mOutputStream.close();
            }
            if(serialPort!=null){
                serialPort.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMsg(String msg){
        Log.i("xiao_",msg);
    }

}
