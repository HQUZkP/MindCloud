package com.kaipingzhou.mindcloud.controller.fragment.domin;

/**
 * Created by 周开平 on 2017/3/27 21:08.
 * qq 275557625@qq.com
 * 作用：
 */

public class BucketItem extends GridItem {

    public final int id;
    public int images = 1;

    /**
     * Creates a new BucketItem
     *
     * @param n the name of the bucket
     * @param p the absolute path to the bucket
     * @param i the bucket ID
     */
    public BucketItem(final String n, final String p, final String taken, int i) {
        super(n, p, taken, 0);
        id = i;
    }
}