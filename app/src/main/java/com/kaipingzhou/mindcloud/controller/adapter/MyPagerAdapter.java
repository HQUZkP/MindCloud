package com.kaipingzhou.mindcloud.controller.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 周开平 on 2017/3/25 16:33.
 * qq 275557625@qq.com
 * 作用：MainActivityPagerAdapter
 */

public class MyPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<ImageView> mImageViews;

    public MyPagerAdapter(Context context, ArrayList<ImageView> imageViews) {
        this.mContext = context;
        this.mImageViews = imageViews;
    }

    /**
     * 返回数据的总个数
     *
     * @return
     */
    @Override
    public int getCount() {
        return mImageViews == null ? 0 : mImageViews.size();
    }

    /**
     * 作用getView
     *
     * @param container viewpager
     * @param position  要创建页面的位置
     * @return 返回和当前页面有关系的值
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = mImageViews.get(position);
        //添加到容器中
        container.addView(imageView);
        return imageView;
    }

    /**
     * 判断
     *
     * @param view   当前创建的视图
     * @param object 上面instantiateItem返回的值
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 销毁页面
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
