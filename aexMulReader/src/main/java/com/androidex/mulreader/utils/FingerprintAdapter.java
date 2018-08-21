package com.androidex.lockaxial.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.androidex.lockaxial.androidexdemo.R;

import java.util.List;

/**
 * Created by Administrator on 2018/7/18.
 */

public class FingerprintAdapter extends BaseAdapter {
    private List<FingerprintBean> data;
    private LayoutInflater inflater;
    private Context context;
    public static final String DELETE_ACTION = "com.xiao.FINGERPRINT_DELETE_ACTION";

    public FingerprintAdapter(Context context,List<FingerprintBean> data){
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.activity_fingerprint_item, viewGroup, false);
            holder = new ViewHolder();
            holder.number = view.findViewById(R.id.number_);
            holder.name = view.findViewById(R.id.name_);
            holder.delete = view.findViewById(R.id.delete_);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        String number = data.get(i).number;
        holder.number.setText(number);

        String name =SharedPreferencesUtils.getInstance(context).getFingerprint(number);
        if(name == null || name.length()<=0){
            name = "未知用户";
        }
        holder.name.setText(name);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDeleteMessage(data.get(i).number);
                Log.i("xiao_","删除："+data.get(i).number);
            }
        });
        return view;
    }

    private class ViewHolder{
        TextView number;
        TextView name;
        Button delete;
    }

    private void sendDeleteMessage(String number){
        Intent i = new Intent(DELETE_ACTION);
        i.putExtra("number",number);
        context.sendBroadcast(i);
    }
}
