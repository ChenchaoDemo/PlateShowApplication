package com.kernal.api;


import com.kernal.bean.CameraTime;
import com.kernal.bean.RecResultEx;

public class PlateCameraApi {
    static {
        System.loadLibrary("androidwty");
    }

    private final long nativeHandle;

    public PlateCameraApi() {
        nativeHandle = nativeInit();
    }

    public void initPlateCameraSDK(String ip, int port) {
        initPlateCameraSDK(nativeHandle, ip, port);
    }

    public void unInitPlateCameraSDK() {
        unInitPlateCameraSDK(nativeHandle);
    }

    /**
     * CLIENT_LPRC_SetTrigger: 手动触发识别（抓取一帧识别）
     */
    public void setTrigger() {
        setTrigger(nativeHandle);
    }
    /**
     * setDataTime: 设置相机时间
     */
    public void setDateTime(CameraTime time) { setDateTime(nativeHandle, time); }
    private native long nativeInit();

    private native void initPlateCameraSDK(long nativeHandle, String ip, int port);

    private native void unInitPlateCameraSDK(long nativeHandle);
    private native void setTrigger(long nativeHandle);
    private native void setDateTime(long nativeHandle, CameraTime time);
    private void onInitPlateCameraSDK(int initPlateCameraSuccess) {
        if (null != onInitPlateCameraListener) {
            onInitPlateCameraListener.onInitPlateCamera(initPlateCameraSuccess);
        }
    }

    private void onGetDataListener(RecResultEx recResultEx, long dwUser) {
        if (null != onGetDataListener) {
            onGetDataListener.onGetDataListener(recResultEx, dwUser);
        }
    }

    private void onConnectStatusListener(String chWTYIP, int status) {
        if (null != onConnectStatusListener) {
            onConnectStatusListener.onConnectStatusListener(chWTYIP, status);
        }
    }


    private InitPlateCameraListener onInitPlateCameraListener;
    private OnConnectStatusListener onConnectStatusListener;
    private OnGetDataListener onGetDataListener;

    public void setOnInitPlateCameraListener(InitPlateCameraListener onInitPlateCameraListener) {
        this.onInitPlateCameraListener = onInitPlateCameraListener;
    }

    public void setOnConnectStatusListener(OnConnectStatusListener onConnectStatusListener) {
        this.onConnectStatusListener = onConnectStatusListener;
    }

    public void setOnGetDataListener(OnGetDataListener onGetDataListener) {
        this.onGetDataListener = onGetDataListener;
    }

    public interface InitPlateCameraListener {
        void onInitPlateCamera(int initPlateCameraSuccess);
    }

    public interface OnGetDataListener {
        void onGetDataListener(RecResultEx recResultEx, long dwUser);
    }

    public interface OnConnectStatusListener {
        void onConnectStatusListener(String chWTYIP, int status);
    }
}
