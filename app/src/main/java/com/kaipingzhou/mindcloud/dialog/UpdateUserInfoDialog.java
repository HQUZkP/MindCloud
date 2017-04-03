package com.kaipingzhou.mindcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.bean.UserInfo;
import com.kaipingzhou.mindcloud.controller.activity.LoginActivity;
import com.kaipingzhou.mindcloud.utils.CacheUtils;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.LogUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 周开平 on 2017/3/28 22:21.
 * qq 275557625@qq.com
 * 作用：
 */

public class UpdateUserInfoDialog extends Dialog implements View.OnClickListener {

    private EditText etLoginid;
    private EditText etName;
    private EditText etAcademy;
    private EditText etGrade;
    private EditText etSpecialty;
    private EditText etClass;
    private Button btnConfirm;
    private Button btnCancle;

    private Context context;

    private LayoutInflater factory;

    private UserInfo userInfo = null;


    public UpdateUserInfoDialog(Context context) {
        super(context);
        factory = LayoutInflater.from(context);
        this.context = context;
    }

    public UpdateUserInfoDialog(Context context, int themeResId) {
        super(context, themeResId);
        factory = LayoutInflater.from(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(factory.inflate(R.layout.update_userinfo_dialog, null));

        etLoginid = (EditText) this.findViewById(R.id.et_loginid);
        etName = (EditText) this.findViewById(R.id.et_name);
        etAcademy = (EditText) this.findViewById(R.id.et_academy);
        etGrade = (EditText) this.findViewById(R.id.et_grade);
        etSpecialty = (EditText) this.findViewById(R.id.et_specialty);
        etClass = (EditText) this.findViewById(R.id.et_class);
        btnConfirm = (Button) this.findViewById(R.id.btn_confirm);
        btnCancle = (Button) this.findViewById(R.id.btn_cancle);

        String userId = CacheUtils.getString(context, LoginActivity.USER_ID, 0);
        etLoginid.setText(userId);

        btnConfirm.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm://提交输入的信息到服务器，并且同步数据
                //获取用户输入的数据
                submitDataToService();
                break;
            case R.id.btn_cancle://取消
                dismiss();
                break;
        }
    }

    /**
     * 提交输入的信息到服务器，并且同步数据
     */
    public void submitDataToService() {
        userInfo = new UserInfo();
        String loginId = etLoginid.getText().toString();
        String userName = etName.getText().toString();
        String userAcademy = etAcademy.getText().toString();
        String userGrade = etGrade.getText().toString();
        String userSpecialty = etSpecialty.getText().toString();
        String userClass = etClass.getText().toString();

        userInfo.setStudentId(loginId);
        userInfo.setStudentName(userName);
        userInfo.setStudentAcademy(userAcademy);
        userInfo.setStednetGrade(userGrade);
        userInfo.setStudentSpecialty(userSpecialty);
        userInfo.setStudentClass(userClass);

        LogUtil.e(userInfo.toString());

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();

        RequestBody requestBody = formEncodingBuilder
                .add("StudentId", loginId)
                .add("StudentName", userName)
                .add("StudentAcademy", userAcademy)
                .add("StednetGrade", userGrade)
                .add("StudentSpecialty", userSpecialty)
                .add("StudentClass", userClass)
                .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder
                .url(Constants.BASE_URL + "DoUploadUserInfo")
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
                if (res.equals(Constants.UPLOAD_USERINFO_SUCCESS)) {
                    getOwnerActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getOwnerActivity().getApplicationContext(), "更新个人信息成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (res.equals(Constants.UPLOAD_USERINFO_FAILED)) {
                    getOwnerActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getOwnerActivity().getApplicationContext(), "更新个人信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        //隐藏dialog
        this.dismiss();
    }
}
