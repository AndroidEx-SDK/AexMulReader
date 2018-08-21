package com.androidex.lockaxial.androidexdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/7/23.
 */

public class MagneticActivity extends Activity {
    private TextView number1,number2;
    private String cardNumber,cardIMEI;
    private int flag = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic);
        number1 = findViewById(R.id.magnetic_number_1);
        number2 = findViewById(R.id.magnetic_number_2);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_SEMICOLON){
            cardNumber = "";
            flag = 1;
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_EQUALS){
            cardIMEI = "";
            flag = 2;
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            number1.setText("卡号："+cardNumber);
            number2.setText("唯一码："+cardIMEI);
            return true;
        }
        if(flag==1){
            cardNumber = cardNumber+getInputText(keyCode);
        }else{
            cardIMEI = cardIMEI+getInputText(keyCode);
        }
        return true;
    }


    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MagneticActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

//    ；6217007200067453723=28012205181000100？
//    ；6216602000000123271=28022202000015600？
//    ；6216602000000123271=28022202000015600？


    private void showMsg(String msg){
        Log.i("xiao_",msg);
    }

    private String getInputText(int keyCode){
        if(keyCode == KeyEvent.KEYCODE_0){
            return "0";
        }else if(keyCode == KeyEvent.KEYCODE_1){
            return "1";
        }else if(keyCode == KeyEvent.KEYCODE_2){
            return "2";
        }else if(keyCode == KeyEvent.KEYCODE_3){
            return "3";
        }else if(keyCode == KeyEvent.KEYCODE_4){
            return "4";
        }else if(keyCode == KeyEvent.KEYCODE_5){
            return "5";
        }else if(keyCode == KeyEvent.KEYCODE_6){
            return "6";
        }else if(keyCode == KeyEvent.KEYCODE_7){
            return "7";
        }else if(keyCode == KeyEvent.KEYCODE_8){
            return "8";
        }else if(keyCode == KeyEvent.KEYCODE_9){
            return "9";
        }
        return "";
    }

}
