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
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
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
 * Created by 周开平 on 2017/3/25 19:19.
 * qq 275557625@qq.com
 * 作用：
 */
public class RigisterActivity extends Activity {

    private EditText etLoginid;
    private EditText etLoginpsd;
    private EditText etConfirmpsd;
    private Button btnRigister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rigister);

        //初始化UI
        initUI();
        //初始化监听器
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        btnRigister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.isGrantExternalRW(RigisterActivity.this, 1)){
                    Rigister();
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
                    Rigister();
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
     * 注册
     */
    private void Rigister() {
        final String loginid = etLoginid.getText().toString();
        final String loginpsd = etLoginpsd.getText().toString();

        LogUtil.e("loginid==" + loginid + " loginpsd==" + loginpsd);

        if (loginid.length() == 10) {
            if (loginpsd.equals(etConfirmpsd.getText().toString())) {
                FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

                RequestBody requestBody = formEncodingBuilder.add("username", loginid).add("password", loginpsd).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder
                        .url(Constants.BASE_URL + "Rigister")
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
                        if (res.equals(Constants.RIGISTER_SUCCESS)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RigisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // 给启动页面返回数据
                            Intent intent = new Intent();

                            intent.putExtra("loginid", loginid);
                            intent.putExtra("loginpsd", loginpsd);

                            // 设置返回的结果码
                            setResult(RESULT_OK, intent);
                            //关闭当前界面
                            finish();

                        } else if (res.equals(Constants.RIGISTER_FAILED)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RigisterActivity.this, "该账户已被注册", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "请输入10位学号", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        etLoginid = (EditText) findViewById(R.id.et_loginid);
        etLoginpsd = (EditText) findViewById(R.id.et_loginpsd);
        etConfirmpsd = (EditText) findViewById(R.id.et_confirmpsd);
        btnRigister = (Button) findViewById(R.id.btn_rigister);
    }
}
