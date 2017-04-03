package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.adapter.MyPagerAdapter;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.DensityUtil;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;

import java.util.ArrayList;

/**
 * Created by 周开平 on 2017/3/25 16:07.
 * qq 275557625@qq.com
 * 作用：
 */
public class GuideActivity extends Activity {

    private ViewPager viewPager;
    private Button btn_start_main;
    private LinearLayout ll_point_group;
    private ImageView iv_red_point;

    private ArrayList<ImageView> imageViews;

    /**
     * 两点间的间距
     */
    int leftmax;

    private int widthdpi;

    private int[] guideImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gudie);
        //初始化UI
        initUI();
        //初始化数据
        initData();
        //初始化监听器
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());

        //设置按钮的点击事件
        btn_start_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.isGrantExternalRW(GuideActivity.this, 1)){
                    toMain();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toMain();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 进入主界面
     */
    private void toMain() {
        //保存曾经进入过登陆页面
        CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_LOGIN, true);
        //跳转到登陆页面
        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
        startActivity(intent);
        //关闭引导页面
        finish();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //准备数据
        guideImages = new int[]{
                R.drawable.guide_1,
                R.drawable.guide_2,
                R.drawable.guide_3,
                R.drawable.guide_4,
        };

        //单位转换
        widthdpi = DensityUtil.dip2px(this, 10);

        imageViews = new ArrayList<>();
        for (int i = 0; i < guideImages.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(guideImages[i]);
            //添加到集合中
            imageViews.add(imageView);

            //创建点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            /**
             * 单位是像素
             * 把单位当成dp转成对应的像素
             */
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthdpi, widthdpi);
            if (i != 0) {
                params.leftMargin = 10;
            }
            point.setLayoutParams(params);
            //添加到线性布局中
            ll_point_group.addView(point);
        }

        //设置viewpager的适配器
        viewPager.setAdapter(new MyPagerAdapter(getApplicationContext(), imageViews));
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_start_main = (Button) findViewById(R.id.btn_start_main);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGlobalLayout() {
            //执行不止一次
            iv_red_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            leftmax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();

            viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        }

        class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

            /**
             * 当页面滑动了回调这个方法
             *
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                屏幕滑动的距离： 屏幕宽 = 屏幕滑动的百分比
//                两点间移动的距离：间距 = 屏幕滑动的距离： 屏幕宽 = 屏幕滑动百分比（已知）
//
//                两点间移动的距离 = 屏幕滑动百分比 * 间距
//                两点间滑动距离对应的坐标 = 原来的起始位置 +  两点间移动的距离
                int leftmargin = (int) (position * leftmax + positionOffset * leftmax);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
                params.leftMargin = leftmargin;
                iv_red_point.setLayoutParams(params);
            }

            /**
             * 当页面被选中的时候调用这个方法
             *
             * @param position
             */
            @Override
            public void onPageSelected(int position) {
                if (position == imageViews.size() - 1) {
                    //最后一个页面
                    btn_start_main.setVisibility(View.VISIBLE);
                } else {
                    btn_start_main.setVisibility(View.GONE);
                }
            }

            /**
             * 当viewpager页面滑动状态发生变化的时候调用这个方法
             *
             * @param state
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        }
    }
}
