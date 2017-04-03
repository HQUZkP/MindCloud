package com.kaipingzhou.mindcloud.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.kaipingzhou.mindcloud.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 周开平 on 2017/3/26 23:47.
 * qq 275557625@qq.com
 * 作用：
 */

public class MainActivityImageView extends ImageView {
    private Bitmap back;        //背景图片资源
    private Bitmap mBitmap;        //生成位图
    private double startX = 0;    //移动起始X坐标

    //构造函数中必须有context,attributeSet这两个	参数，否则父类无法调用
    public MainActivityImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //由于不是Activity子类，只能通过DisplayMetrics来获取屏幕信息
        DisplayMetrics dm = getResources().getDisplayMetrics();
        //屏幕宽度
        int screenWidth = dm.widthPixels;
        //屏幕高度
        int screenHeight = dm.heightPixels;

        back = BitmapFactory.decodeResource(context.getResources(), R.drawable.rootblock_default_bg);

        mBitmap = Bitmap.createScaledBitmap(back, screenWidth * 3, screenHeight, true);


        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (startX <= -80) {
                        startX = 0;
                    } else {
                        startX -= 0.09;
                    }
                }
                invalidate();
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 10);

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, (float) startX, 0, null);
    }
}

