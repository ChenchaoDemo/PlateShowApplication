package com.eparking.jniplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eparking.R;
import com.eparking.adapter.CameraAdapter;
import com.eparking.adapter.PlateRecognizeAdapter;
import com.eparking.adapter.inter.OnItemChildClickListener;
import com.eparking.adapter.utils.SharedPreferencesHelper;
import com.eparking.databinding.ActivityMainBinding;
import com.eparking.permission.PermissionHelper;
import com.eparking.permission.PermissionInterface;
import com.eparking.permission.PermissionUtil;
import com.kernal.api.PlateCameraApi;
import com.kernal.bean.CameraTime;
import com.kernal.bean.RecResultEx;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

public class MainActivity extends AppCompatActivity implements PermissionInterface{
    private PermissionHelper permissionHelper;
    private ActivityMainBinding binding;
    private List<RecResultEx> recResultExes = new ArrayList<>();
    private PlateRecognizeAdapter plateRecognizeAdapter;
    private Handler handler = new Handler();
    private String cameraIp1;

    private LibVLC libVLC;
    private long starTime;

    private RecyclerView recyclerView;
    private List<String> cameraDevices = new ArrayList<>();
    private CameraAdapter cameraAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        permissionHelper = new PermissionHelper(this, this);
        recyclerView =binding.recyclerView;
        cameraDevices = new ArrayList<>();
        cameraAdapter = new CameraAdapter(cameraDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cameraAdapter);
        binding.startSearch.setOnClickListener(v -> {
            scanAndIdentifyCameras();
        });
        binding.stopSearch.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this,"停止",Toast.LENGTH_SHORT).show();

        });
        binding.connect.setOnClickListener(v -> {
            setNodePlayer(binding.nodePlay,binding.inputEdit.getText().toString().trim());
        });

        // 初始化 VLC
        ArrayList<String> options = new ArrayList<>();
        libVLC = new LibVLC(this, options);

        cameraAdapter.setCameraClickListener(new CameraAdapter.OnCameraClickListener() {
            @Override
            public void onCameraClick(String ipAddress) {
                Log.d("CameraClick", "Clicked camera IP: " + ipAddress);
                setNodePlayer(binding.nodePlay,ipAddress);
            }
        });
    }

    protected void scanAndIdentifyCameras() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ipAddress = dhcpInfo.ipAddress;
        binding.myIp.setText(Formatter.formatIpAddress(ipAddress));
        // 获取网关地址
        int gateway = dhcpInfo.gateway;
        String gateIp = Formatter.formatIpAddress(gateway);
        binding.wgIp.setText(gateIp);
        cameraDevices.clear();
        // 扫描范围
        new Thread(() -> {
            for (int i = 2; i < 255; i++) {
                String itemIp=gateIp.substring(0,gateIp.length()-1)+i;
                try {
                    InetAddress inetAddress = InetAddress.getByName(itemIp);
                    if (inetAddress.isReachable(1000)) { // 100ms 超时
                        Log.d("连接ip", "成功: "+inetAddress.getHostAddress());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 在这里更新UI
                                binding.nowIp.setText(inetAddress.getHostAddress());
                            }
                        });
                        List<String> cameraDevice = isCameraDevice(inetAddress.getHostAddress());
                        for(String item:cameraDevice){
                            cameraDevices.add(item);
                        }
                        // 在这里更新摄像头列表
                        handler.post(() -> {
                            // 通知适配器更新
                            cameraAdapter.notifyDataSetChanged();
                        });
                    }
                } catch (Exception e) {
                    Log.d("连接ip", "失败: "+itemIp,e);
                }
            }
        }).start();
    }

    private static List<String> isCameraDevice(String ip) {
        List<String> list=new ArrayList<>();
        String[] urlsHttpCheck = {
                "http://" + ip + "/",
                "http://" + ip + "/stream",
                "http://" + ip + "/video",
        };
        for (String url : urlsHttpCheck) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000); // 1秒超时
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("连接ip 成功流", url);
                    list.add(url);
                }
            } catch (Exception e) {
                Log.e("CameraCheckError", "Error checking " + url, e);
            }
        }

        String[] urlsRtspCheck = {
                "rtsp://" + ip + "/",
                "rtsp://" + ip + "/stream",
                "rtsp://" + ip + "/stream1",
                "rtsp://" + ip + "/stream2",
                "rtsp://" + ip + "/mjpegstream",
                "rtsp://" + ip + "/h264_substream"
        };
        for (String url : urlsRtspCheck) {
            try {
                URLConnection  connection = new URL(url).openConnection();
                connection.connect();
                int responseCode = connection.getReadTimeout();
                list.add(url);
            } catch (Exception e) {
                Log.e("CameraCheckError", "Error checking " + url, e);
            }
        }
        return list;
    }



    private void setNodePlayer(NodePlayerView nodePlayerView,String ipAddress){
        //设置渲染器类型
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        //设置视频画面缩放模式
        /*
             ScaleToFill,      // 充满：拉伸内容以填充整个视图（可能会改变宽高比）
             ScaleAspectFit,   // 等比例适应：按比例缩放内容以适应视图，保持原始宽高比
             ScaleAspectFill   // 等比例填充：按比例缩放内容以填充视图，可能会裁剪部分内容以保持宽高比
         */
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        NodePlayer nodePlayer = new NodePlayer(this);
        //设置播放视图
        nodePlayer.setPlayerView(nodePlayerView);
        //设置RTSP流使用的传输协议,支持的模式有:
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);//设置传输
//        nodePlayer.setInputUrl("rtsp://"+"192.168.1.98"+"/stream1");
        nodePlayer.setVideoEnable(true);//设置视频启用
        nodePlayer.setBufferTime(100);//设置缓冲时间
        nodePlayer.setMaxBufferTime(200);//设置最大缓冲时间



        nodePlayer.setInputUrl(ipAddress);
        nodePlayer.start();

    }



    @Override
    protected void onStart() {
        super.onStart();
        recResultExes.clear();
        permissionHelper.requestPermissions();
    }

    @Override
    public int getPermissionsRequestCode() {
        return 1000;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
    }

    @Override
    public void requestPermissionsSuccess() {

    }

    @Override
    public void requestPermissionsFail() {

    }


    @Override
    public void onShowRationale(String[] deniedShowRationalePermissions) {
        PermissionUtil.requestPermissions(this, deniedShowRationalePermissions, getPermissionsRequestCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.requestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * camera的ip为172.16.10.210的识别
     */


    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (libVLC != null) {
            libVLC.release();
        }
    }


}
