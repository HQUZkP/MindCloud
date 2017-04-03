package com.kaipingzhou.mindcloud.controller.fragment.domin;

/**
 * Created by 周开平 on 2017/3/27 21:08.
 * qq 275557625@qq.com
 * 作用：
 */

public class GridItem {
    public final String name;
    public final String path;
    public final String imageTaken;
    public final long imageSize;

    public GridItem(final String n, final String p, final String imageTaken, final long imageSize) {
        name = n;
        path = p;
        this.imageTaken = imageTaken;
        this.imageSize = imageSize;
    }
}
