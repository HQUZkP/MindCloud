package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;
import com.kaipingzhou.mindcloud.dialog.ModifyAvatarDialog;
import com.kaipingzhou.mindcloud.dialog.UpdateUserInfoDialog;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;

/**
 * Created by 周开平 on 2017/3/26 23:26.
 * qq 275557625@qq.com
 * 作用：
 */
public class PersonalActivity extends Activity {

    private ImageView ivPersonalPhoto;
    private ImageView ivPersonalUpdate;
    private ImageView ivUpdatePsd;
    private TextView tvPersonalInfo;
    private Button btnPersonalLogout;
    /**
     * 用户名
     */
    private String uesrId;

    private static final int SELECT_IMAGE_CODE = 1;
    private static final int FLAG_TAKE_IMAGE = 2;
    private static final int FLAG_MODIFY_FINISH = 3;

    private static String localTempImageFileName = "";


    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    public static final File FILE_LOCAL = new File(FILE_SDCARD, Constants.APP_PATH);
    public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "files");
    private String userInfo = "";//用户信息
    private String imgPath = "";//头像路径
    private Intent intent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initUI();

        initData();

        initListener();
    }

    private void initListener() {
        btnPersonalLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.isGrantExternalRW(PersonalActivity.this, 5)) {
                    clearCache();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        ivUpdatePsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalActivity.this, UpdatePsdActivity.class);
                intent.putExtra("loginid", uesrId);
                startActivity(intent);
            }
        });

        ivPersonalUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.isGrantExternalRW(PersonalActivity.this, 4)) {
                    updatePersonalInfo();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        ivPersonalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.isGrantExternalRW(PersonalActivity.this, 3)) {
                    updatePhoto();
                } else {
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

    /**
     * 清除缓存的账号以及加密过的密码
     */
    private void clearCache() {
        //清除缓存的账号以及加密过的密码
        CacheUtils.clearSp(PersonalActivity.this, new String[]{LoginActivity.USER_ID, LoginActivity.USER_PSD});
        CacheUtils.clearSp(PersonalActivity.this, new String[]{SplashActivity.LOGINED_BEFORE});
        Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 更新个人信息
     */
    private void updatePersonalInfo() {
        LogUtil.e("更新个人信息开始了...");

        UpdateUserInfoDialog updateUserInfoDialog =
                new UpdateUserInfoDialog(PersonalActivity.this, R.style.dialog_select);

        updateUserInfoDialog.setOwnerActivity(PersonalActivity.this);

        AlignmentSpan span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);

        AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        String dTitle = "请选择图片";
        spannable.append(dTitle);

        spannable.setSpan(span, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_size, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        updateUserInfoDialog.setTitle(spannable);
        updateUserInfoDialog.show();

        updateUserInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //再次请求数据
                FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                RequestBody requestBody = formEncodingBuilder.add("username", uesrId).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder
                        .url(Constants.BASE_URL + "DownLoadUserInfo")
                        .post(requestBody)
                        .build();

                //回调
                OkHttpClient okHttpClient = new OkHttpClient();

                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        LogUtil.e("onFailure==" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        final String res = response.body().string();
                        LogUtil.e("onResponse==" + res);
                        //将服务器返回的用户信息及请求结果状态码转换为String数组
                        String resutf8 = URLDecoder.decode(res, "UTF-8");

                        LogUtil.e("onResponse==" + resutf8);

                        String[] strArray = null;
                        strArray = convertStrToArray(resutf8);
                        userInfo = "";
                        if (strArray[0].equals(Constants.DOWNLOAD_USER_INFO_SUCCESS)) {
                            for (int i = 1; i < strArray.length; i++) {
                                userInfo += strArray[i] + "\n";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvPersonalInfo.setText(userInfo);
//                                            Toast.makeText(getApplicationContext(), "个人信息更改成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (strArray[0].equals(Constants.DOWNLOAD_USER_INFO_FAILED)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvPersonalInfo.setText("请完善个人信息");
                                }
                            });
                        }
                    }
                });
            }
        });

        LogUtil.e("更新个人信息结束了...");
    }

    /**
     * 修改头像
     */
    public void updatePhoto() {
        LogUtil.e("修改头像开始了...");

        ModifyAvatarDialog modifyAvatarDialog = new ModifyAvatarDialog(PersonalActivity.this, R.style.dialog_select) {
            //选择本地相册
            @Override
            public void doGoToImg() {
                this.dismiss();
                Intent intent = new Intent(PersonalActivity.this, SelectPictureActivity.class);
                startActivityForResult(intent, SELECT_IMAGE_CODE);
            }

            //选择相机拍照
            @Override
            public void doGoToPhone() {
                this.dismiss();
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        localTempImageFileName = "";
                        localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";
                        File filePath = FILE_PIC_SCREENSHOT;
                        //如果目录不存在就创建相应的目录存放拍摄的照片
                        if (!filePath.exists()) {
                            filePath.mkdirs();
                        }

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(filePath, localTempImageFileName);
                        // localTempImgDir和localTempImageFileName是自己定义的名字
                        Uri uri = Uri.fromFile(file);

                        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        startActivityForResult(intent, FLAG_TAKE_IMAGE);

                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        AlignmentSpan span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);

        AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
        SpannableStringBuilder spannable = new SpannableStringBuilder();

        String dTitle = "请选择图片";
        spannable.append(dTitle);

        spannable.setSpan(span, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(span_size, 0, dTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        modifyAvatarDialog.setTitle(spannable);
        modifyAvatarDialog.show();

        LogUtil.e("修改头像结束了");
    }

    private void initData() {

        if (PermissionUtils.isGrantExternalRW(PersonalActivity.this, 1)) {
            getDataFromService();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDataFromService();
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
                    SyncPhoto(intent);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updatePhoto();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case 4:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updatePersonalInfo();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "您的手机暂不适配哦~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case 5:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    clearCache();
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
     * 从服务器获取数据
     */
    private void getDataFromService() {
        uesrId = CacheUtils.getString(PersonalActivity.this, LoginActivity.USER_ID, 0);
        btnPersonalLogout.setText("退出登录(" + uesrId + ")");

        //获取缓存的用户头像路径
        imgPath = CacheUtils.getString(getApplicationContext(), "imgPath", 0);
        File file = new File(imgPath);

        if (file.exists()) {
            //如果文件已存在则先使用原先保存的头像图片
            Bitmap icon = BitmapFactory.decodeFile(imgPath);
            ivPersonalPhoto.setImageBitmap(icon);
        } else {
            //本地数据被清空了，就去服务器加载用户头像，显示到界面，并且保存到本地
            final String imgPathOnWeb = uesrId + ".png";

            Request.Builder builder = new Request.Builder();
            Request request = builder
                    .get()
                    .url(Constants.BASE_URL + "files/" + imgPathOnWeb)
                    .build();

            //回调
            OkHttpClient okHttpClient = new OkHttpClient();

            Call call = okHttpClient.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    LogUtil.e("onFailure==" + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    LogUtil.e("onResponse==");

                    InputStream inputStream = response.body().byteStream();

                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    if (bitmap != null) {
                        LogUtil.e("bitmap: width=" + bitmap.getWidth() + " height=" + bitmap.getHeight());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //将请求的图片显示到UI中
                                ivPersonalPhoto.setImageBitmap(bitmap);
                            }
                        });

                        //将请求到的图片保存到本地，以供下次使用
                        imgPath = FILE_PIC_SCREENSHOT + "/icon4.jpg";
                        File file = new File(imgPath);
                        //如果文件已存在则先删除原先保存的头像图片
                        if (file.exists()) {
                            file.delete();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        fos.flush();
                        fos.close();
                        //保存图片后发送广播通知更新数据库
                        Uri uri = Uri.fromFile(file);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
                                ivPersonalPhoto.setImageBitmap(decodeResource);
                            }
                        });
                    }
                }
            });
        }

        //如果还没有信息就去服务器加载用户的当前信息，如果已经有信息了就不用去加载了
        if (userInfo.equals("")) {
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

            RequestBody requestBody = formEncodingBuilder.add("username", uesrId).build();

            Request.Builder builder = new Request.Builder();
            Request request = builder
                    .url(Constants.BASE_URL + "DownLoadUserInfo")
                    .post(requestBody)
                    .build();

            //回调
            OkHttpClient okHttpClient = new OkHttpClient();

            Call call = okHttpClient.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    LogUtil.e("onFailure==" + e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String res = response.body().string();
                    LogUtil.e("onResponse==" + res);
                    //将服务器返回的用户信息及请求结果状态码转换为String数组
                    String resutf8 = URLDecoder.decode(res, "UTF-8");

                    LogUtil.e("onResponse==" + resutf8);

                    String[] strArray = null;
                    strArray = convertStrToArray(resutf8);
                    userInfo = "";
                    if (strArray[0].equals(Constants.DOWNLOAD_USER_INFO_SUCCESS)) {
                        for (int i = 1; i < strArray.length; i++) {
                            userInfo += strArray[i] + "\n";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvPersonalInfo.setText(userInfo);
                            }
                        });
                    } else if (strArray[0].equals(Constants.DOWNLOAD_USER_INFO_FAILED)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvPersonalInfo.setText("请完善个人信息");
                            }
                        });
                    }
                }
            });
        }
    }

    //使用String的split 方法
    public static String[] convertStrToArray(String str) {
        String[] strArray = null;
        strArray = str.split(","); //拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    private void initUI() {
        ivPersonalPhoto = (ImageView) findViewById(R.id.iv_personal_photo);
        ivPersonalUpdate = (ImageView) findViewById(R.id.iv_personal_update);
        ivUpdatePsd = (ImageView) findViewById(R.id.iv_update_psd);
        tvPersonalInfo = (TextView) findViewById(R.id.tv_personal_info);
        btnPersonalLogout = (Button) findViewById(R.id.btn_personal_logout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_CODE) {
                //从手机相册选择图片
                if (data != null) {
                    String imgPath = data.getStringExtra("imgPath");

                    Intent intent = new Intent(this, CropImageActivity.class);
                    intent.putExtra("imgPath", imgPath);

                    startActivityForResult(intent, FLAG_MODIFY_FINISH);
                } else {
                    Toast.makeText(getApplicationContext(), "未发现图片", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == FLAG_TAKE_IMAGE) {
                //摄像头拍照
                File file = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);

                Intent intent = new Intent(this, CropImageActivity.class);
                intent.putExtra("imgPath", file.getAbsolutePath());

                startActivityForResult(intent, FLAG_MODIFY_FINISH);
            } else if (requestCode == FLAG_MODIFY_FINISH) {
                //处理结束
                if (data != null) {
                    intent = data;
                    if (PermissionUtils.isGrantExternalRW(PersonalActivity.this, 2)) {
                        SyncPhoto(intent);
                    }
                }
            }
        }
    }

    /**
     * 同步头像
     *
     * @param intent
     */
    private void SyncPhoto(Intent intent) {
        //获取裁剪后图片保存的路径
        imgPath = intent.getStringExtra("imgPath");

        CacheUtils.putString(getApplicationContext(), "imgPath", imgPath);

        Bitmap icon = BitmapFactory.decodeFile(imgPath);
        ivPersonalPhoto.setImageBitmap(icon);

        //将用户头像上传到服务器
        File file = new File(imgPath);

        MultipartBuilder multipartBuilder = new MultipartBuilder();
        RequestBody requestBody = multipartBuilder.type(MultipartBuilder.FORM)
                .addFormDataPart("username", uesrId)
                .addFormDataPart("Photo", uesrId + ".png", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder
                .url(Constants.BASE_URL + "UploadInfo")
                .post(requestBody)
                .build();

        //回调
        OkHttpClient okHttpClient = new OkHttpClient();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogUtil.e("onFailure==" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                LogUtil.e("onResponse==" + res);
                if (res.equals(Constants.UPLOAD_IMG_SUCCESS)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "头像已同步到服务器", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (res.equals(Constants.UPLOAD_IMG_FAILED)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "头像同步到服务器失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
