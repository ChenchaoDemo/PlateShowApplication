package com.kernal.bean;

public class PlateImageInformation {
    public int nWidth;							/* 宽度					*/
    public int nHeight;						/* 高度					*/
    public int	nPitch;							/* 图像宽度的一行像素所占内存字节数*/
    public int	nLen;							/* 图像的长度			*/
    public String reserved;					/* 预留     			*/
    public byte[] pBuffer;						/* 图像内存的首地址(数据)		*/
}
