package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.utils.CropImage;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;
import com.kaipingzhou.mindcloud.view.CropImageView;

/**
 * Created by 周开平 on 2017/3/27 22:11.
 * qq 275557625@qq.com
 * 作用：头像裁剪Activity
 */

public class CropImageActivity extends Activity implements View.OnClickListener {
    public static final int SHOW_PROGRESS = 2000;

    public static final int REMOVE_PROGRESS = 2001;

    private CropImageView mImageView;
    private Bitmap mBitmap;
    private CropImage mCrop;

    private Button mSave;
    private Button mCancel, rotateLeft, rotateRight;

    private String mPath = "CropImageActivity";

    private String TAG = "";

    public int screenWidth = 0;
    public int screenHeight = 0;

    private ProgressBar mProgressBar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case REMOVE_PROGRESS:
                    mHandler.removeMessages(SHOW_PROGRESS);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        //初始化UI
        initUI();
        //初始化监听事件
        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
        mSave = (Button) findViewById(R.id.gl_modify_avatar_save);
        mCancel = (Button) findViewById(R.id.gl_modify_avatar_cancel);
        rotateLeft = (Button) findViewById(R.id.gl_modify_avatar_rotate_left);
        rotateRight = (Button) findViewById(R.id.gl_modify_avatar_rotate_right);

        if (PermissionUtils.isGrantExternalRW(CropImageActivity.this, 2)){
            findImage();
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                }
            });
        }
        addProgressbar();
    }

    /**
     * 找到相应的图片做头像
     */
    private void findImage() {
        getWindowWH();
        mPath = getIntent().getStringExtra("imgPath");
        LogUtil.e("得到的图片的路径是 = " + mPath);

        try {
            mBitmap = createBitmap(mPath, screenWidth, screenHeight);
            if (mBitmap == null) {
                Toast.makeText(CropImageActivity.this, "没有找到图片", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                resetImageView(mBitmap);
            }
        } catch (Exception e) {
            Toast.makeText(CropImageActivity.this, "没有找到图片", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 获取屏幕的高和宽
     */
    private void getWindowWH() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void resetImageView(Bitmap bitmap) {
        mImageView.clear();
        mImageView.setImageBitmap(bitmap);
        mImageView.setImageBitmapResetBase(bitmap, true);
        mCrop = new CropImage(this, mImageView, mHandler);
        mCrop.crop(bitmap);
    }

    protected void addProgressbar() {
        mProgressBar = new ProgressBar(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addContentView(mProgressBar, params);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gl_modify_avatar_cancel://取消
                finish();
                break;
            case R.id.gl_modify_avatar_save://保存
                if (PermissionUtils.isGrantExternalRW(CropImageActivity.this, 1)){
                    saveImage();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.gl_modify_avatar_rotate_left://向左旋转
                mCrop.startRotate(270.f);
                break;
            case R.id.gl_modify_avatar_rotate_right://向右旋转
                mCrop.startRotate(90.f);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findImage();
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
     * 处理并保存图片
     */
    private void saveImage() {
        String imgPath = mCrop.saveToLocal(mCrop.cropAndSave());
        LogUtil.e("截取后图片的路径是 = " + imgPath);
        Intent intent = new Intent();
        intent.putExtra("imgPath", imgPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    public Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图片
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBitmap != null) {
            mBitmap = null;
        }
    }
}
