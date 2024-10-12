package com.eparking.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eparking.R;
import com.eparking.adapter.holder.BaseViewHolder;
import com.eparking.adapter.utils.SharedPreferencesHelper;
import com.eparking.jniplate.MainActivity;
import com.eparking.myglide.ImageGlide;

import com.kernal.bean.RecResultEx;

import java.util.Collection;
import java.util.List;

public class PlateRecognizeAdapter extends RecyclerArrayAdapter<RecResultEx> {
    private ImageGlide imageGlide;
    private int mLayoutId;
    private Context mContext;

    public PlateRecognizeAdapter(Context context, int layoutId, List<RecResultEx> recResultExes) {
        super(context, recResultExes);
        this.mContext = context;
        this.mLayoutId = layoutId;
        imageGlide = ImageGlide.getInstance(context.getApplicationContext());
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(parent, mLayoutId);
    }

    public class MyViewHolder extends BaseViewHolder<RecResultEx> {
        private TextView sample_text;
        private ImageView sample_imageView;
        private Button btn_startRecognize;
        private Button btn_stopRecognize;
        private Button setTrigger;
        private EditText editText;

        public MyViewHolder(ViewGroup parent, int res) {
            super(parent, res);
            sample_text = getView(R.id.sample_text);
            sample_imageView = getView(R.id.sample_imageView);
            btn_startRecognize = getView(R.id.startRecognize);
            btn_stopRecognize = getView(R.id.stopRecognize);
            editText = getView(R.id.cameraIp);
            setTrigger=getView(R.id.setTrigger);

            btn_startRecognize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isIP(editText.getText().toString())) {
                        // Save the IP address to SharedPreferences
                        //初始化 ExoPlayer
                        SharedPreferencesHelper.putString(mContext, "cameraIp" + getDataPosition(), editText.getText().toString());
                        getOnItemChildClickListener().onItemChildClick(v, getDataPosition(),editText.getText().toString().trim());
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.ip_title), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btn_stopRecognize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnItemChildClickListener().onItemChildClick(v, getDataPosition(),editText.getText().toString().trim());
                }
            });
            setTrigger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnItemChildClickListener().onItemChildClick(v, getDataPosition(),editText.getText().toString().trim());
                }
            });
        }

        @Override
        public void setData(RecResultEx data, List<Object> payloads) {
            if(!"".equals(data.plateColor)||!"".equals(data.plateLicense))
            sample_text.setText(data.plateColor + "," + data.plateLicense);
            imageGlide.load(data.pFullImage.pBuffer, sample_imageView);
            editText.setText(SharedPreferencesHelper.getString(mContext, "cameraIp" + getDataPosition(), ""));
        }

    }

    private boolean isIP(String ip) {
        String Ip = ip.replaceAll(" ", "");
        if (Ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] str = Ip.split("\\.");
            if (Integer.parseInt(str[0]) < 225)
                if (Integer.parseInt(str[1]) < 225)
                    if (Integer.parseInt(str[2]) < 225)
                        if (Integer.parseInt(str[3]) < 225)
                            return true;

        }
        return false;
    }
}
