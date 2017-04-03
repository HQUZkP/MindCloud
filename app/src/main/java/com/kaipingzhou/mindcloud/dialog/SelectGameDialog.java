package com.kaipingzhou.mindcloud.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.game2048.Game2048Activity;
import com.kaipingzhou.mindcloud.gamesokoban.GameTXZActivity;
import com.kaipingzhou.mindcloud.utils.CacheUtils;

/**
 * Created by 周开平 on 2017/3/31 21:42.
 * qq 275557625@qq.com
 * 作用：选在2048还是推箱子的小游戏dialog
 */

public class SelectGameDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private LayoutInflater factory;

    private Button btn_2048;
    private Button btn_txz;
    private Button btn_cancel;

    private Intent intent;

    public SelectGameDialog(Context context) {
        super(context);
        factory = LayoutInflater.from(context);
        this.context = context;
    }

    public SelectGameDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        factory = LayoutInflater.from(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(factory.inflate(R.layout.select_game_dialog, null));
        btn_2048 = (Button) this.findViewById(R.id.btn_2048);
        btn_txz = (Button) this.findViewById(R.id.btn_txz);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

        btn_2048.setOnClickListener(this);
        btn_txz.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_2048:
                CacheUtils.putString(context, "maxNum", 0 + "");
                intent = new Intent(context, Game2048Activity.class);
                context.startActivity(intent);
                this.dismiss();
                break;
            case R.id.btn_txz:
                intent = new Intent(context, GameTXZActivity.class);
                context.startActivity(intent);
                this.dismiss();
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }
}
