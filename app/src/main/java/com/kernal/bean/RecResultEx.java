package com.kernal.bean;

public class RecResultEx {
    public String cameraIp="";/* 相机IP */
    public String plateColor="";/* 车牌颜色*/
    public String plateLicense="";/* 车牌号码*/
    public PlateLocation plateLocation=new PlateLocation();/* 车牌在图像中的坐标*/
    public CameraTime cameraTime=new CameraTime();/* 识别出车牌的时间 */
    public  int plateConfidence;/*车牌可信度*/
    public int recognitionTime;/* 识别耗时*/
    public int plateDirection;/* 车牌方向*/
    public int nCarLogo;/* 车标类型(参考CAR_LOGO)*/
    public int nCarModel;/* 车型类型(参考CAR_MODEL)*/
    public String reserved;/* 预留 */
    public PlateImageInformation pFullImage=new PlateImageInformation();/* 全景图像数据(注意：相机不传输，此处指针为空) */
    public PlateImageInformation pPlateImage=new PlateImageInformation();/* 车牌图像数据(注意：相机不传输，此处指针为空) */
    public RecResultEx(){

    }
}
