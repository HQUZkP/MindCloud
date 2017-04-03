package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;

public class SplashActivity extends Activity {

    public static final String START_LOGIN = "start_login";
    public static final String LOGINED_BEFORE = "logined_before";

    private RelativeLayout rl_activity_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化UI
        initUI();
        //初始化动画
        initAnimation();
    }

    private void initAnimation() {
        //旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3000);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(3000);

        AnimationSet set = new AnimationSet(false);
        //添加三个动画没有先后的顺序，便于同时播放
        set.addAnimation(rotateAnimation);
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);
        //添加动画
        rl_activity_splash.startAnimation(set);

        set.setAnimationListener(new MyAnimationListener());
    }

    class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            LogUtil.e("动画开始播放了");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            LogUtil.e("动画结束播放了");
            if (PermissionUtils.isGrantExternalRW(SplashActivity.this, 1)) {
                toLoginOrGuide();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            LogUtil.e("动画重复播放了");
        }
    }

    /**
     * 请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toLoginOrGuide();
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

    private void toLoginOrGuide() {
        String isLoginedBefore = CacheUtils.getString(SplashActivity.this, LOGINED_BEFORE, 0);
        if (isLoginedBefore.equals("0")) {
            //判断是否进入过登录页面
            boolean isStartLogin = CacheUtils.getBoolean(SplashActivity.this, START_LOGIN);
            if (isStartLogin) {
                //如果进入过登录页面，直接进入登录页面
                //跳转到登录页面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                //如果没有进入过登录页面，进入引导页面
                Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                startActivity(intent);
            }
        } else {
            //以前登陆过，直接登录
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }

        //关闭当前页面
        finish();
    }

    private void initUI() {
        rl_activity_splash = (RelativeLayout) findViewById(R.id.rl_activity_splash);
    }
}
