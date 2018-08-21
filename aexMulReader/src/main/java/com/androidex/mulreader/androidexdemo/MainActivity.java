package com.androidex.lockaxial.androidexdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidex.lockaxial.utils.ChangeTool;
import com.androidex.lockaxial.utils.SerialPortUtils;

import android_serialport_api.SerialPort;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFingerprint(View v){
        startActivity(new Intent(this,FingerprintActivity.class));
    }

    public void onIdCard(View v){
        startActivity(new Intent(this,IDCardActivity.class));
    }

    public void onNfc(View v){
        startActivity(new Intent(this,NFCCardActivity.class));
    }

    public void onMagnetic(View v){startActivity(new Intent(this,MagneticActivity.class));}

    public void onKeyBoard(View v){startActivity(new Intent(this,KeyBoardActivity.class));}

}
