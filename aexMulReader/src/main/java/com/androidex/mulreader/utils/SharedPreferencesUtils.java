package com.androidex.lockaxial.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/7/18.
 */

public class SharedPreferencesUtils {
    private static SharedPreferencesUtils utils;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesUtils(Context context){
        sharedPreferences = context.getSharedPreferences("ANDROIDEX_SHARE",Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtils getInstance(Context context){
        if(utils == null){
            utils = new SharedPreferencesUtils(context);
        }

        return utils;
    }

    public void putFingerprint(String number,String name){
        sharedPreferences.edit().putString(number, name).commit();
        putKey(number);
    }

    public String getFingerprint(String number){
        return sharedPreferences.getString(number,"");
    }

    private void putKey(String number){
        String key = sharedPreferences.getString("key","");
        key = key+number+"-";
        sharedPreferences.edit().putString("key",key).commit();
    }

    public String[] getAllNumber(){
        String keys = sharedPreferences.getString("key","");
        return keys.split("-");
    }

    public void deleteFingerprint(String number){
        sharedPreferences.edit().remove(number).commit();
        String key = sharedPreferences.getString("key","");
        key = key.replace(number+"-","");
        sharedPreferences.edit().putString("key",key).commit();
    }

    public void deleteAll(){
        sharedPreferences.edit().clear().commit();
    }
}
