package com.androidex.lockaxial.androidexdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.lockaxial.utils.ChangeTool;
import com.androidex.lockaxial.utils.FingerprintAdapter;
import com.androidex.lockaxial.utils.FingerprintBean;
import com.androidex.lockaxial.utils.SerialPortUtils;
import com.androidex.lockaxial.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/7/18.
 */

public class FingerprintActivity extends Activity {

    private LinearLayout addLayout;
    private EditText name;
    private EditText number;
    private Button startFingerprint;
    private EditText addMsg;

    private LinearLayout selectLayout;
    private ListView listView;
    private LinearLayout listLayout;
    private TextView nullError;
    private TextView mount_;

    private LinearLayout contrastLayout;
    private EditText contrastMsg;


    private SerialPortUtils serialPortUtils;
    private String path = "/dev/ttyS6";
    private int baudrate = 19200;
    private SerialPort serialPort;

    String strName;
    String strNumber;
    int fingerprintMount = 0;
    String deleteNumber = "";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x01){
                //收到消息
                byte data[] = (byte[]) msg.obj;
                String dexData = ChangeTool.ByteArrToHex(data);
                String arrayData[] = dexData.split("-");
                if(arrayData[1].equals(DevConfig.Fingerprint_CMD_1)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        addMsg.append("第一次指纹采集成功，请不要移动手指\n");
                        String strData = DevConfig.Fingerprint_CMD_2+getUserCode(Integer.valueOf(strNumber))+DevConfig.Power_1+DevConfig.Zero;
                        strData = DevConfig.START+strData+ChangeTool.getXor(strData)+DevConfig.END;
                        showMsg("第二次拾取指纹命令："+strData);
                        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(strData));
                        if(result){
                            addMsg.append("正在第二次采集指纹，请不要移动手指\n");
                        }
                    }else{
                        addMsg.append("第一次指纹采集失败,错误原因："+getResultMessage(arrayData[4])+"\n");
                    }
                }else if(arrayData[1].equals(DevConfig.Fingerprint_CMD_2)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        addMsg.append("第二次指纹采集成功，请不要移动手指\n");
                        String strData = DevConfig.Fingerprint_CMD_3+getUserCode(Integer.valueOf(strNumber))+DevConfig.Power_1+DevConfig.Zero;
                        strData = DevConfig.START+strData+ChangeTool.getXor(strData)+DevConfig.END;
                        showMsg("第三次拾取指纹命令："+strData);
                        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(strData));
                        if(result){
                            addMsg.append("正在第三次采集指纹，请不要移动手指\n");
                        }
                    }else{
                        addMsg.append("第二次指纹采集失败,错误原因："+getResultMessage(arrayData[4])+"\n");
                    }
                }else if(arrayData[1].equals(DevConfig.Fingerprint_CMD_3)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        addMsg.append("指纹采集成功\n");
                        SharedPreferencesUtils.getInstance(FingerprintActivity.this).putFingerprint(strNumber,strName);
                    }else{
                        addMsg.append("第三次指纹采集失败,错误原因："+getResultMessage(arrayData[4])+"\n");
                    }
                }else if(arrayData[1].equals(DevConfig.MOUNT_CMD)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        String hexMount = arrayData[2]+arrayData[3];
                        fingerprintMount = Integer.valueOf(hexMount,16);
                        showMsg("指纹总数："+fingerprintMount);
                        mount_.setText(fingerprintMount+"");
                    }else{
                        showToast("获取指纹总数失败，错误原因："+getResultMessage(arrayData[4]));
                        showMsg("获取指纹总数失败，错误原因："+getResultMessage(arrayData[4]));
                    }
                    showAllFingerPrint();
                }else if(arrayData[1].equals(DevConfig.DELETE_CMD_ALL)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        showToast("删除所有指纹成功");
                        SharedPreferencesUtils.getInstance(FingerprintActivity.this).deleteAll();
                    }else{
                        showToast("删除所有指纹失败，错误原因："+getResultMessage(arrayData[4]));
                        showMsg("删除所有指纹失败，错误原因："+getResultMessage(arrayData[4]));
                    }
                    selectAllFingerprint();
                }else if(arrayData[1].equals(DevConfig.DELETE_CMD_NUMBER)){
                    if(arrayData[4].equals(DevConfig.ACK_SUCCESS)){
                        showToast("删除指定指纹成功");
                        showToast("删除指定指纹成功");
                        SharedPreferencesUtils.getInstance(FingerprintActivity.this).deleteFingerprint(deleteNumber);
                    }else{
                        showToast("删除指定指纹失败，错误原因："+getResultMessage(arrayData[4]));
                        showMsg("删除指定指纹失败，错误原因："+getResultMessage(arrayData[4]));
                    }
                    selectAllFingerprint();
                }else if(arrayData[1].equals(DevConfig.CONTRAST_USER)){
                    String result = arrayData[4];
                    if(result.equals(DevConfig.Power_1) || result.equals(DevConfig.Power_2) ||result.equals(DevConfig.Power_3)){
                        String hexNumber = arrayData[2]+arrayData[3];
                        String number = Integer.valueOf(hexNumber,16)+"";
                        String name = SharedPreferencesUtils.getInstance(FingerprintActivity.this).getFingerprint(number);
                        contrastMsg.append("验证成功\n验证信息：\n编号："+number+"\n姓名："+name+"\n");
                        showMsg("验证成功\n验证信息：\n编号："+number+"\n姓名："+name+"\n");
                    }else if(result.equals(DevConfig.ACK_NOUSER)){
                        contrastMsg.append("验证失败，没有此用户\n");
                        showMsg("验证失败，没有此用户\n");
                    }else if(result.equals(DevConfig.ACK_TIMEOUT)){
                        contrastMsg.append("验证超时\n");
                        showMsg("验证超时\n");
                    }
                    startCheck();
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(FingerprintAdapter.DELETE_ACTION)){
                deleteNumber = intent.getStringExtra("number");
                String cmd =  DevConfig.DELETE_CMD_NUMBER+getUserCode(Integer.valueOf(deleteNumber))+DevConfig.Zero+DevConfig.Zero;
                cmd = cmd+ChangeTool.getXor(cmd);
                cmd = DevConfig.START+cmd+DevConfig.END;
                showMsg("删除指定用户命令："+cmd);
                boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(cmd));
                if(result){
                    showMsg("请求删除指定用户成功");
                }else{
                    showMsg("请求删除指定用户失败");
                }
            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
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
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FingerprintAdapter.DELETE_ACTION);
        registerReceiver(broadcastReceiver,filter);
    }

    private void initView(){
        addLayout = findViewById(R.id.add_layout);
        name = findViewById(R.id.name_);
        number = findViewById(R.id.number_);
        startFingerprint = findViewById(R.id.start_Fingerprint);
        addMsg = findViewById(R.id.add_msg);

        selectLayout = findViewById(R.id.select_layout);
        listView = findViewById(R.id.listview_);
        listLayout = findViewById(R.id.list_layout);
        nullError = findViewById(R.id.null_);
        mount_ = findViewById(R.id.mount_);

        contrastLayout = findViewById(R.id.contrast_layout);
        contrastMsg = findViewById(R.id.contrast_msg);


    }

    public void addFingerprint(View v){
        //触发指纹录入
        addLayout.setVisibility(View.VISIBLE);
        addMsg.setText("");
        selectLayout.setVisibility(View.GONE);
        contrastLayout.setVisibility(View.GONE);
    }

    private void showAllFingerPrint(){
        String keys[] = SharedPreferencesUtils.getInstance(this).getAllNumber();
        List<FingerprintBean> data = null;
        if(keys!=null && keys.length>0){
            data = new ArrayList<>();
            for(int i=0;i<keys.length;i++){
                if(keys[i].length()<=0){
                    break;
                }
                FingerprintBean bean = new FingerprintBean();
                bean.name = SharedPreferencesUtils.getInstance(this).getFingerprint(keys[i]);
                bean.number = keys[i];
                data.add(bean);
            }
        }

        if(data!=null && data.size()>0){
            listView.setAdapter(new FingerprintAdapter(this,data));
            listLayout.setVisibility(View.VISIBLE);
            nullError.setVisibility(View.GONE);
        }else{
            showMsg("隐藏列表，显示null");
            listLayout.setVisibility(View.GONE);
            nullError.setVisibility(View.VISIBLE);
        }
    }

    public void selectFingerprint(View v){
        //触发指纹查询
        selectLayout.setVisibility(View.VISIBLE);
        addLayout.setVisibility(View.GONE);
        contrastLayout.setVisibility(View.GONE);
        selectAllFingerprint();
    }

    private void selectAllFingerprint(){
        String cmd = DevConfig.MOUNT_CMD+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero;
        cmd = cmd+ChangeTool.getXor(cmd);
        cmd = DevConfig.START+cmd+DevConfig.END;
        showMsg("请求指纹总数命令："+cmd);
        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(cmd));
        if(result){
            showMsg("请求指纹总数成功");
        }else{
            showMsg("请求指纹总数失败");
        }
    }

    public void deleteAll(View v){
        if(fingerprintMount!=0){
            String cmd = DevConfig.DELETE_CMD_ALL+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero;
            cmd = cmd+ChangeTool.getXor(cmd);
            cmd = DevConfig.START+cmd+DevConfig.END;
            showMsg("请求删除所有指纹命令："+cmd);
            boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(cmd));
            if(result){
                showMsg("请求删除所有指纹成功");
            }else{
                showMsg("请求删除所有指纹失败");
            }
        }else{
            showToast("当前没有指纹可删除");
        }
    }

    public void checkFingerprint(View v){
        //触发指纹验证
        addLayout.setVisibility(View.GONE);
        selectLayout.setVisibility(View.GONE);
        contrastLayout.setVisibility(View.VISIBLE);
        contrastMsg.setText("");
        startCheck();
    }

    private void startCheck(){
        String cmd = DevConfig.CONTRAST_USER+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero+DevConfig.Zero;
        cmd = cmd+ChangeTool.getXor(cmd);
        cmd = DevConfig.START+cmd+DevConfig.END;
        showMsg("请求开启指纹验证命令："+cmd);
        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(cmd));
        if(result){
            showMsg("请求开启指纹验证成功");
        }else{
            showMsg("请求开启指纹验证失败");
        }
    }

    public void startFingerprint(View v){
        //指纹录入，触发开始
        strName = name.getText().toString().trim();
        strNumber = number.getText().toString().trim();
        if(strName == null || strName.length()<=0){
            showToast("请输入姓名");
            return;
        }
        if(strNumber == null || strNumber.length()<=0){
            showToast("请输入编号");
            return;
        }
        int intNumber = Integer.valueOf(strNumber);
        if(intNumber>65535){
            showToast("超出编号限制，请重新输入");
            return;
        }
        if(SharedPreferencesUtils.getInstance(FingerprintActivity.this).getFingerprint(strNumber).length()>0){
            showToast("该编号以被占用");
            addMsg.append("该编号被占用\n");
            return;
        }
        addMsg.append("信息录入：\n姓名："+strName+"\n编号："+strNumber+"\n");
        String strData = DevConfig.Fingerprint_CMD_1+getUserCode(intNumber)+DevConfig.Power_1+DevConfig.Zero;
        strData = DevConfig.START+strData+ChangeTool.getXor(strData)+DevConfig.END;
        showMsg("第一次拾取指纹命令："+strData);
        boolean result = serialPortUtils.sendSerialPort(ChangeTool.HexToByteArr(strData));
        if(result){
            addMsg.append("请按下手指.......\n");
        }else{
            addMsg.append("请稍后重试\n");
        }
    }

    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FingerprintActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMsg(String msg){
        Log.i("xiao_",msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if(serialPort!=null){
            serialPortUtils.closeSerialPort();
        }
    }

    private String getResultMessage(String code){
        if(code.equals(DevConfig.ACK_SUCCESS)){
            return "操作成功";
        }else if(code.equals(DevConfig.ACK_FAIL)){
            return "操作失败";
        }else if(code.equals(DevConfig.ACK_FULL)){
            return "指纹数据库已满";
        }else if(code.equals(DevConfig.ACK_NOUSER)){
            return "无此用户";
        }else if(code.equals(DevConfig.ACK_USER_OCCUPIED)){
            return "此ID用户已存在";
        }else if(code.equals(DevConfig.ACK_USER_EXIST)){
            return "用户已存在";
        }else if(code.equals(DevConfig.ACK_TIMEOUT)){
            return "采集超时";
        }
        return "未知错误";
    }

    private String getUserCode(int numer){
        String hexNumber = Integer.toHexString(numer);
        String userCode1 = "00";
        String userCode2 = "00";
        if(hexNumber.length() == 1){
            userCode1 = "00";
            userCode2 = "0"+hexNumber;
        }else if(hexNumber.length() == 2){
            userCode1 = "00";
            userCode2 = hexNumber;
        }else if(hexNumber.length() == 3){
            userCode1 = "0"+hexNumber.substring(0, 1);
            userCode2 = hexNumber.substring(1,3);
        }else if(hexNumber.length() == 4){
            userCode1 = hexNumber.substring(0, 2);
            userCode2 = hexNumber.substring(2,4);
        }
        return userCode1+userCode2;
    }
}
