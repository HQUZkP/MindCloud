package com.kaipingzhou.mindcloud.game2048;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kaipingzhou.mindcloud.utils.CacheUtils;

/**
 * Created by 周开平 on 2017/3/31 16:30.
 * qq 275557625@qq.com
 * 作用：卡片类
 */

public class Card extends FrameLayout {

    private Context context;

    public Card(Context context) {
        super(context);

        this.context = context;

        LayoutParams layoutParams = null;

        background = new View(getContext());
        layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(10, 10, 0, 0);
        background.setBackgroundColor(0x33ffffff);
        addView(background, layoutParams);

        label = new TextView(getContext());
        label.setTextSize(20);
        label.setGravity(Gravity.CENTER);

        layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(10, 10, 0, 0);
        addView(label, layoutParams);

        setNum(0);
    }


    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        String maxNum = CacheUtils.getString(context, "maxNum", 0);
        int maxNumInt = Integer.parseInt(maxNum);

        if (num > maxNumInt) {
            CacheUtils.putString(context, "maxNum", num + "");
        }

        if (num <= 0) {
            label.setText("");
        } else {
            label.setText(num + "");
        }

        switch (num) {
            case 0:
                label.setBackgroundColor(0x00000000);
                label.setText("");
                break;
            case 2:
                label.setBackgroundColor(0xffeee4da);
                label.setText("学癌");
                break;
            case 4:
                label.setBackgroundColor(0xffede0c8);
                label.setText("学糕");
                break;
            case 8:
                label.setBackgroundColor(0xfff2b179);
                label.setText("学酥");
                break;
            case 16:
                label.setBackgroundColor(0xfff59563);
                label.setText("学水");
                break;
            case 32:
                label.setBackgroundColor(0xfff67c5f);
                label.setText("学残");
                break;
            case 64:
                label.setBackgroundColor(0xfff65e3b);
                label.setText("学渣");
                break;
            case 128:
                label.setBackgroundColor(0xffedcf72);
                label.setText("学弱");
                break;
            case 256:
                label.setBackgroundColor(0xffedcc61);
                label.setText("学民");
                break;
            case 512:
                label.setBackgroundColor(0xffedc850);
                label.setText("学屌");
                break;
            case 1024:
                label.setBackgroundColor(0xffedc53f);
                label.setText("学痞");
                break;
            case 2048:
                label.setBackgroundColor(0xffedc22e);
                label.setText("学霸");
                break;
            case 4096:
                label.setBackgroundColor(0xffedc22e);
                label.setText("学神");
                break;
            case 8172:
                label.setBackgroundColor(0xffedc22e);
                label.setText("学鬼");
                break;
            case 16344:
                label.setBackgroundColor(0xffedc22e);
                label.setText("学魔");
                break;
            default:
                label.setBackgroundColor(0xff3c3a32);
                break;
        }
    }

    public boolean equals(Card card) {
        return getNum() == card.getNum();
    }

    protected Card clone() {
        Card card = new Card(getContext());
        card.setNum(getNum());
        return card;
    }

    public TextView getLabel() {
        return label;
    }

    private TextView label;
    private View background;
}

