package com.eparking.adapter;

/**
 * @name： PlateShowApplication
 * @author： wsc
 * @createTime： 2024年9月30日, 0030 14:26:51
 * @version： 1.0
 * @description：
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.CameraViewHolder> {
    private List<String> cameraDevices;
    public OnCameraClickListener listener;
    public interface OnCameraClickListener {
        void onCameraClick(String ipAddress);
    }

    public void setCameraClickListener(OnCameraClickListener listener){
        this.listener=listener;
    }


    public CameraAdapter(List<String> cameraDevices) {
        this.cameraDevices = cameraDevices;
    }

    @NonNull
    @Override
    public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CameraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraViewHolder holder, int position) {
        holder.textView.setText(cameraDevices.get(position));
        String ipAddress = cameraDevices.get(position);
        holder.textView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCameraClick(ipAddress); // 调用接口方法
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraDevices.size();
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        CameraViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
