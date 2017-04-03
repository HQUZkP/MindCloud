package com.kaipingzhou.mindcloud.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.kaipingzhou.mindcloud.R;


/**
 * Created by 周开平 on 2017/3/28 15:04.
 * qq 275557625@qq.com
 * 作用：
 */
public class BreakActivity extends Activity {

    private ImageButton ib_back;
    private ImageButton ib_textsize;
    private WebView webview;
    private ProgressBar pb_status;

    private WebSettings webSettings;

    private int tempSize = 2;
    private int realSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);

        InitUI();

        initData();

        initListener();
    }

    private void initListener() {
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ib_textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeTextSizeDialog();
            }
        });
    }

    private void showChangeTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置文字大小");
        String[] items = {"超大字体", "大号字体", "正常字体", "小号字体", "超小字体"};
        builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempSize = which;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realSize = tempSize;
                changeTextSize(realSize);
            }
        });
        builder.show();
    }

    private void changeTextSize(int realSize) {
        switch (realSize) {
            case 0:
                //超大字体
                webSettings.setTextZoom(200);
                break;
            case 1:
                //大号字体
                webSettings.setTextZoom(150);
                break;
            case 2:
                //正常字体
                webSettings.setTextZoom(100);
                break;
            case 3:
                //小号字体
                webSettings.setTextZoom(70);
                break;
            case 4:
                //超小字体
                webSettings.setTextZoom(50);
                break;
        }
    }

    private void initData() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        //设置支持Javascript
        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置双击变大/小
        webSettings.setUseWideViewPort(true);
        //增加缩放按钮
        webSettings.setBuiltInZoomControls(true);

        //不允许当前网页跳转到系统其他的浏览器中
        webview.setWebViewClient(new WebViewClient() {
            //加载完成的时候完成回调
            @Override
            public void onPageFinished(WebView view, String url) {
                pb_status.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        webview.loadUrl(url);
    }

    private void InitUI() {
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_textsize = (ImageButton) findViewById(R.id.ib_textsize);
        webview = (WebView) findViewById(R.id.webview);
        pb_status = (ProgressBar) findViewById(R.id.pb_status);
    }
}
