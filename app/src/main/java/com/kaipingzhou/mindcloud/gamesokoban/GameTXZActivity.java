package com.kaipingzhou.mindcloud.gamesokoban;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kaipingzhou.mindcloud.R;

/**
 * Created by 周开平 on 2017/4/1 20:05.
 * qq 275557625@qq.com
 * 作用：
 */

public class GameTXZActivity extends Activity implements View.OnClickListener {

    private Button btn_pre_level;
    private Button btn_next_level;
    private Button btn_undo;
    private Button btn_back_out;

    private GameView view = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_txz);

        initUI();

        initListener();
    }

    private void initUI() {
        btn_pre_level = (Button) findViewById(R.id.btn_pre_level);
        btn_next_level = (Button) findViewById(R.id.btn_next_level);
        btn_undo = (Button) findViewById(R.id.btn_undo);
        btn_back_out = (Button) findViewById(R.id.btn_back_out);

        view = (GameView) findViewById(R.id.gameview);

        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ViewGroup.LayoutParams viewLayoutParams = view.getLayoutParams();
        viewLayoutParams.width = screenWidth;
        viewLayoutParams.height = screenHeight - DensityUtil.dip2px(getApplicationContext(), 70);
        view.setLayoutParams(viewLayoutParams);
    }

    private void initListener() {
        btn_pre_level.setOnClickListener(this);
        btn_next_level.setOnClickListener(this);
        btn_undo.setOnClickListener(this);
        btn_back_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pre_level:
                //上一关
                this.view.priorGrade();
                break;
            case R.id.btn_next_level:
                //下一关
                this.view.nextGrade();
                break;
            case R.id.btn_undo:
                //撤销
                this.view.undo();
                break;
            case R.id.btn_back_out:
                //返回
                this.finish();
                break;
        }
    }


    public byte[][] getMap(int grade) {
        return MapFactory.getMap(grade);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //退出时保存游戏状态
        save();
    }

    public void save() {
        //退出时保存游戏状态
        //地图，关卡数
        byte[][] map = view.getMap();
        int row = map.length;
        int column = map[0].length;
        StringBuffer mapString = new StringBuffer();
        //mapString最终格式
        //行优先存储，两两之间逗号隔开
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                mapString.append(map[i][j]);
                mapString.append(",");
            }
        //最后多加了一个逗号，解析时注意
        SharedPreferences pre = getSharedPreferences("map", 0);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("manX", view.getManX());
        editor.putInt("manY", view.getManY());
        editor.putInt("grade", view.getGrade());
        editor.putInt("row", row);
        editor.putInt("column", column);
        editor.putString("mapString", mapString.toString());
        editor.commit();
    }
}
