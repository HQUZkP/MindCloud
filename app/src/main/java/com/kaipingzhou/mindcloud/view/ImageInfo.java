package com.kaipingzhou.mindcloud.view;

/**
 * Created by 周开平 on 2017/3/26 23:55.
 * qq 275557625@qq.com
 * 作用：图像实体类
 */

public class ImageInfo {
    public String imageMsg;		//菜单标题
    public int imageId;			//logo图片资源
    public int bgId;			//背景图片资源

    public ImageInfo(String msg, int id1,int id2) {
        imageId = id1;
        imageMsg = msg;
        bgId = id2;
    }
}
