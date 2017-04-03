package com.kaipingzhou.mindcloud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.activity.CropImageActivity;
import com.kaipingzhou.mindcloud.view.CropImageView;
import com.kaipingzhou.mindcloud.view.HighlightView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 周开平 on 2017/3/27 22:23.
 * qq 275557625@qq.com
 * 作用：裁剪图片
 */

public class CropImage {
    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();

    public static final File FILE_LOCAL = new File(FILE_SDCARD, "智慧云/files");

    public boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    public boolean mSaving; // Whether the "save" button is already clicked.
    public HighlightView mCrop;

    private Context mContext;
    private Handler mHandler;
    private CropImageView mImageView;
    private Bitmap mBitmap;

    public CropImage(Context context, CropImageView imageView, Handler handler) {
        mContext = context;
        mImageView = imageView;
        mImageView.setCropImage(this);
        mHandler = handler;
    }

    /**
     * 图片裁剪
     */
    public void crop(Bitmap bm) {
        mBitmap = bm;
        startFaceDetection();
    }


    public void startRotate(float d) {
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        final float degrees = d;
        showProgressDialog(mContext.getResources().getString(R.string.gl_wait), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            Matrix m = new Matrix();
                            m.setRotate(degrees);
                            Bitmap tb = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);
                            mBitmap = tb;
                            mImageView.resetView(tb);
                            if (mImageView.mHighlightViews.size() > 0) {
                                mCrop = mImageView.mHighlightViews.get(0);
                                mCrop.setFocus(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, mHandler);
    }

    private void startFaceDetection() {
        if (((Activity) mContext).isFinishing()) {
            return;
        }
        showProgressDialog(mContext.getResources().getString(R.string.gl_wait), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                final Bitmap b = mBitmap;
                mHandler.post(new Runnable() {
                    public void run() {
                        if (b != mBitmap && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1.0f) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    /**
     * 裁剪并保存
     *
     * @return
     */
    public Bitmap cropAndSave() {
        final Bitmap bmp = onSaveClicked(mBitmap);
        mImageView.mHighlightViews.clear();
        return bmp;
    }

    /**
     * 裁剪并保存
     *
     * @return
     */
    public Bitmap cropAndSave(Bitmap bm) {
        final Bitmap bmp = onSaveClicked(bm);
        mImageView.mHighlightViews.clear();
        return bmp;
    }

    /**
     * 取消裁剪
     */
    public void cropCancel() {
        mImageView.mHighlightViews.clear();
        mImageView.invalidate();
    }

    private Bitmap onSaveClicked(Bitmap bm) {
        if (mSaving)
            return bm;

        if (mCrop == null) {
            return bm;
        }

        mSaving = true;

        Rect r = mCrop.getCropRect();
        int width = 200;//dr.width(); // modify by yc
        int height = 200;//dr.height();
        Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(bm, r, dstRect, null);
        }

        return croppedImage;
    }

    /**
     * 压缩图片至指定大小
     *
     * @param bgimage
     * @param newWidth  新的宽度
     * @param newHeight 新的高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();

        if (width <= newHeight || height <= newHeight){
            return bgimage;
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public String saveToLocal(Bitmap bm) {
        String path = FILE_LOCAL + "/icon4.jpg";
        try {
            File file = new File(path);
            //如果文件已存在则先删除原先保存的头像图片
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            //将图片压缩至200*200
            bm = zoomImage(bm, 200, 200);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    private void showProgressDialog(String msg, Runnable job, Handler handler) {
        new Thread(new BackgroundJob(msg, job, handler)).start();
    }

    Runnable mRunFaceDetection = new Runnable() {
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {
            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, false, true);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // CR: sentences!
            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, false, true);
            mImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {
            if (mBitmap == null) {
                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {
                mScale = 256.0F / mBitmap.getWidth(); // CR: F => f (or change
                // all f to F).
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            return faceBitmap;
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                public void run() {
                    mWaitingToPick = mNumFaces > 1;
                    makeDefault();
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() > 0) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }

                    if (mNumFaces > 1) {
                    }
                }
            });
        }
    };

    class BackgroundJob implements Runnable {
        private String message;
        private Runnable mJob;
        private Handler mHandler;

        public BackgroundJob(String m, Runnable job, Handler handler) {
            message = m;
            mJob = job;
            mHandler = handler;
        }

        public void run() {
            final CountDownLatch latch = new CountDownLatch(1);
            mHandler.post(new Runnable() {
                public void run() {
                    try {
                        mHandler.sendMessage(mHandler.obtainMessage(CropImageActivity.SHOW_PROGRESS));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                mJob.run();
            } finally {
                mHandler.sendMessage(mHandler.obtainMessage(CropImageActivity.REMOVE_PROGRESS));
            }
        }
    }
}
