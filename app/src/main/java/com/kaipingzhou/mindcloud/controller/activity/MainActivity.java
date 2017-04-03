package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.view.ImageInfo;
import com.kaipingzhou.mindcloud.controller.adapter.MainActivityPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 周开平 on 2017/3/26 14:23.
 * qq 275557625@qq.com
 * 作用：
 */
public class MainActivity extends Activity {


    ArrayList<ImageInfo> data; // 菜单数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        initData();
        //初始化UI
        initUI();
        //添加监听事件
        initListener();

    }

    /**
     * 添加监听事件
     */
    private void initListener() {

    }

    /**
     * 初始化UI
     */
    private void initUI() {
        ViewPager vpager = (ViewPager) findViewById(R.id.vPager);
        vpager.setAdapter(new MainActivityPagerAdapter(MainActivity.this, data));
        vpager.setPageMargin(50);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        data = new ArrayList<ImageInfo>();
        data.add(new ImageInfo("在线答题", R.drawable.icon1, R.drawable.icon_bg1));
        data.add(new ImageInfo("个人中心", R.drawable.icon2, R.drawable.icon_bg1));
        data.add(new ImageInfo("课间休息", R.drawable.icon3, R.drawable.icon_bg1));
        data.add(new ImageInfo("小游戏", R.drawable.icon4, R.drawable.icon_bg2));
        data.add(new ImageInfo("更多功能", R.drawable.icon5, R.drawable.icon_bg2));
    }
}
