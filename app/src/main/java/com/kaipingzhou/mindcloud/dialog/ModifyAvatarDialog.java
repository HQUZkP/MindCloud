package com.kaipingzhou.mindcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.kaipingzhou.mindcloud.R;

/**
 * Created by 周开平 on 2017/3/27 16:50.
 * qq 275557625@qq.com
 * 作用：
 */
public class ModifyAvatarDialog extends Dialog implements View.OnClickListener {

    private LayoutInflater factory;

    private Button mImg;

    private Button mPhone;

    private Button mCancel;

    private Context context;

    public ModifyAvatarDialog(Context context) {
        super(context);
        factory = LayoutInflater.from(context);
        this.context = context;
    }

    public ModifyAvatarDialog(Context context, int theme) {
        super(context, theme);
        factory = LayoutInflater.from(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(factory.inflate(R.layout.update_photo_choose_dialog, null));
        mImg = (Button) this.findViewById(R.id.btn_choose_img);
        mPhone = (Button) this.findViewById(R.id.btn_choose_phone);
        mCancel = (Button) this.findViewById(R.id.btn_choose_cancel);
        mImg.setOnClickListener(this);
        mPhone.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_img:
                doGoToImg();
                break;
            case R.id.btn_choose_phone:
                doGoToPhone();
                break;
            case R.id.btn_choose_cancel:
                dismiss();
                break;
        }
    }


    public void doGoToImg() {

    }

    public void doGoToPhone() {

    }
}

