package com.kaipingzhou.mindcloud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 周开平 on 2017/3/26 22:01.
 * qq 275557625@qq.com
 * 作用：
 */

public class FocusTextView extends TextView {
    /**
     * 在代码中调用 new的时候调用
     *
     * @param context
     */
    public FocusTextView(Context context) {
        super(context, null);
    }

    /**
     * 系统调用 在布局文件中调用这个构造方法
     *
     * @param context
     * @param attrs
     */
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    /**
     * 系统调用 在布局文件中调用这个构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写获取焦点的方法
     *
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
