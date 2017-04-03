package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.kaipingzhou.mindcloud.utils.MD5Encoder;
import com.kaipingzhou.mindcloud.utils.PermissionUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 周开平 on 2017/3/25 16:15.
 * qq 275557625@qq.com
 * 作用：登陆注册页面
 */
public class LoginActivity extends Activity {

    public static final String USER_ID = "user_id";
    public static final String USER_PSD = "user_psd";

    private EditText etLoginid;
    private EditText etLoginpsd;
    private TextView tvRigister;
    private TextView tvUpdatePsd;
    private Button btnLogin;
    private String loginid;
    private String loginpsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_rigister);

        //初始化UI
        initUI();
        //初始化监听器
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        tvRigister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RigisterActivity.class);

                startActivityForResult(intent, 1);
            }
        });

        tvUpdatePsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UpdatePsdActivity.class);

                startActivityForResult(intent, 2);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginid = etLoginid.getText().toString();
                loginpsd = etLoginpsd.getText().toString();

                if (PermissionUtils.isGrantExternalRW(LoginActivity.this, 1)) {
                    Login(loginid, loginpsd);
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
     * 登陆
     * @param loginid
     * @param loginpsd
     */
    private void Login(final String loginid, final String loginpsd) {
        if (!loginid.equals("")) {
            if (!loginpsd.equals("")) {
                Request.Builder builder = new Request.Builder();
                Request request = builder
                        .get()
                        .url(Constants.BASE_URL + "login?username=" + loginid + "&password=" + loginpsd)
                        .build();

                final OkHttpClient okHttpClient = new OkHttpClient();

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
                        if (res.equals(Constants.LOGIN_SUCCESS)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                            CacheUtils.putString(LoginActivity.this, SplashActivity.LOGINED_BEFORE, "yes");

                            //保存登录id及经MD5算法加密过的密码
                            CacheUtils.putString(LoginActivity.this, LoginActivity.USER_ID, loginid);
                            try {
                                CacheUtils.putString(LoginActivity.this, LoginActivity.USER_PSD, MD5Encoder.encode(loginpsd));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();

                        } else if (res.equals(Constants.LOGIN_FAILED)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "账户不存在或密码错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "用户名为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Login(loginid, loginpsd);
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
        etLoginpsd = (EditText) findViewById(R.id.et_loginpsd);
        tvUpdatePsd = (TextView) findViewById(R.id.tv_update_psd);
        tvRigister = (TextView) findViewById(R.id.tv_rigister);
        btnLogin = (Button) findViewById(R.id.btn_login);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String loginid = data.getStringExtra("loginid");
                String loginpsd = data.getStringExtra("loginpsd");
                etLoginid.setText(loginid);
                etLoginpsd.setText(loginpsd);
            } else if (requestCode == 2) {
                String loginid = data.getStringExtra("loginid");
                String loginpsd = data.getStringExtra("loginpsd");
                etLoginid.setText(loginid);
                etLoginpsd.setText(loginpsd);
            }
        }
    }
}
