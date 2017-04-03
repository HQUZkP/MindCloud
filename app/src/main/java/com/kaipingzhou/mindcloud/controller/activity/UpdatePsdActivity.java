package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.kaipingzhou.mindcloud.utils.MD5Encoder;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 周开平 on 2017/3/26 21:36.
 * qq 275557625@qq.com
 * 作用：
 */
public class UpdatePsdActivity extends Activity {

    private EditText etLoginid;
    private EditText etOldloginpsd;
    private EditText etNewloginpsd;
    private EditText etConfirmnewpsd;
    private Button btnUdate;

    private String loginId;
    private String oldLoginPsd;
    private String newLoginPsd;
    private String confirmNewPsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_psd);
        //初始化UI
        initUI();
        //初始化监听事件
        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        //修改密码的逻辑
        btnUdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginId = etLoginid.getText().toString();
                oldLoginPsd = etOldloginpsd.getText().toString();
                newLoginPsd = etNewloginpsd.getText().toString();
                confirmNewPsd = etConfirmnewpsd.getText().toString();

                if (PermissionUtils.isGrantExternalRW(UpdatePsdActivity.this)) {
                    updatePsd();
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
     * 修改密码
     */
    private void updatePsd() {
        String cacheOldPsd = CacheUtils.getString(UpdatePsdActivity.this, LoginActivity.USER_PSD, 0);
        if (!cacheOldPsd.equals("")) {
            if (!loginId.equals("")) {
                try {
                    if (!oldLoginPsd.equals("") && cacheOldPsd.equals(MD5Encoder.encode(oldLoginPsd))) {
                        if (!newLoginPsd.equals("")) {
                            if (!confirmNewPsd.equals("")) {
                                if (newLoginPsd.equals(confirmNewPsd)) {

                                    FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                                    RequestBody requestBody = formEncodingBuilder.add("username", loginId).add("password", newLoginPsd).build();

                                    Request.Builder builder = new Request.Builder();
                                    Request request = builder
                                            .url(Constants.BASE_URL + "updateLoginPsd")
                                            .post(requestBody)
                                            .build();

                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    //回调
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
                                            if (res.equals(Constants.UPDATE_PSD_SUCCESS)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(UpdatePsdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                //保存新密码
                                                CacheUtils.putString(UpdatePsdActivity.this, LoginActivity.USER_PSD, newLoginPsd);
                                            } else if (res.equals(Constants.UPDATE_PSD_FAILED)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(UpdatePsdActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "请确认新密码", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入新密码", Toast.LENGTH_SHORT).show();
                        }
                    } else if (oldLoginPsd.equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入旧密码", Toast.LENGTH_SHORT).show();
                    } else if (!cacheOldPsd.equals(MD5Encoder.encode(oldLoginPsd))) {
                        Toast.makeText(getApplicationContext(), "您输入的旧密码有误", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请输入学号", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!loginId.equals("")) {
                try {
                    if (!oldLoginPsd.equals("")) {
                        if (!newLoginPsd.equals("")) {
                            if (!confirmNewPsd.equals("")) {
                                if (newLoginPsd.equals(confirmNewPsd)) {

                                    FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                                    RequestBody requestBody = formEncodingBuilder
                                            .add("username", loginId)
                                            .add("password", oldLoginPsd)
                                            .add("newpassword", newLoginPsd)
                                            .build();

                                    Request.Builder builder = new Request.Builder();
                                    Request request = builder
                                            .url(Constants.BASE_URL + "updateLoginPsd")
                                            .post(requestBody)
                                            .build();

                                    OkHttpClient okHttpClient = new OkHttpClient();
                                    //回调
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
                                            if (res.equals(Constants.UPDATE_PSD_SUCCESS)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(UpdatePsdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                //保存新密码
                                                CacheUtils.putString(UpdatePsdActivity.this, LoginActivity.USER_PSD, newLoginPsd);
                                                // 给启动页面返回数据
                                                Intent intent = new Intent();

                                                intent.putExtra("loginid", loginId);
                                                intent.putExtra("loginpsd", newLoginPsd);

                                                // 设置返回的结果码
                                                setResult(RESULT_OK, intent);
                                                //关闭当前界面
                                                finish();
                                            } else if (res.equals(Constants.UPDATE_PSD_FAILED)) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(UpdatePsdActivity.this, "账户不存在或旧密码有误", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "请确认新密码", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入新密码", Toast.LENGTH_SHORT).show();
                        }
                    } else if (oldLoginPsd.equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入旧密码", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请输入学号", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updatePsd();
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
     * 初始化UI
     */
    private void initUI() {
        etLoginid = (EditText) findViewById(R.id.et_loginid);
        etOldloginpsd = (EditText) findViewById(R.id.et_oldloginpsd);
        etNewloginpsd = (EditText) findViewById(R.id.et_newloginpsd);
        etConfirmnewpsd = (EditText) findViewById(R.id.et_confirmnewpsd);
        btnUdate = (Button) findViewById(R.id.btn_udate);

        Intent intent = getIntent();
        String loginid = intent.getStringExtra("loginid");
        etLoginid.setText(loginid);
    }
}
