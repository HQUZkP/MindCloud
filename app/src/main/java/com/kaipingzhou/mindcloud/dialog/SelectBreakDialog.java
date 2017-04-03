package com.kaipingzhou.mindcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.kaipingzhou.mindcloud.R;

/**
 * Created by 周开平 on 2017/4/3 22:16.
 * qq 275557625@qq.com
 * 作用：选择跳转到知乎还是百思不得姐的dialog
 */

public class SelectBreakDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private LayoutInflater factory;

    private Button btn_baisinov;
    private Button btn_zhihu;
    private Button btn_cancel;

    public SelectBreakDialog(Context context) {
        super(context);
        factory = LayoutInflater.from(context);
        this.context = context;
    }

    public SelectBreakDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        factory = LayoutInflater.from(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(factory.inflate(R.layout.select_break_dialog, null));

        btn_baisinov = (Button) this.findViewById(R.id.btn_baisinov);
        btn_zhihu = (Button) this.findViewById(R.id.btn_zhihu);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        btn_baisinov.setOnClickListener(this);
        btn_zhihu.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_baisinov:
                //百思不得姐
                gotoBaisinov();
//                this.dismiss();
                break;
            case R.id.btn_zhihu:
                //知乎
                gotoZhihu();
//                this.dismiss();
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }

    /**
     * 跳转到知乎，让实例去实现
     */
    public void gotoZhihu() {

    }

    /**
     * 跳转到百思不得姐，让实例去实现
     */
    public void gotoBaisinov() {

    }
}
