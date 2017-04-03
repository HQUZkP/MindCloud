package com.kaipingzhou.mindcloud.controller.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.activity.AnsOnlineActivity;
import com.kaipingzhou.mindcloud.controller.activity.BreakActivity;
import com.kaipingzhou.mindcloud.controller.activity.PersonalActivity;
import com.kaipingzhou.mindcloud.utils.Constants;
import com.kaipingzhou.mindcloud.utils.DensityUtil;
import com.kaipingzhou.mindcloud.view.ImageInfo;
import com.kaipingzhou.mindcloud.dialog.SelectBreakDialog;
import com.kaipingzhou.mindcloud.dialog.SelectGameDialog;

/**
 * 自定义适配器
 */
public class MainActivityPagerAdapter extends PagerAdapter {
    Vibrator vibrator;
    ArrayList<ImageInfo> data;
    Activity activity;
    LayoutParams params;

    public MainActivityPagerAdapter(Activity activity, ArrayList<ImageInfo> data) {
        this.activity = activity;
        this.data = data;
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int index) {
        View view = activity.getLayoutInflater().inflate(R.layout.grid, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        //设置gridview的列数以及其内部item的水平间距和垂直间距
        gridView.setNumColumns(2);
        gridView.setVerticalSpacing(DensityUtil.dip2px(activity, 5));
        gridView.setHorizontalSpacing(DensityUtil.dip2px(activity, 5));

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = LayoutInflater.from(activity).inflate(R.layout.grid_item, null);

                    viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
                    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.msg);

                    viewHolder.imageView.setImageResource((data.get(position)).imageId);

                    viewHolder.relativeLayout.setBackgroundResource((data.get(position)).bgId);
                    viewHolder.relativeLayout.getBackground().setAlpha(225);

                    viewHolder.textView.setText((data.get(position)).imageMsg);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                return convertView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(activity, AnsOnlineActivity.class);
                        activity.startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(activity, PersonalActivity.class);
                        activity.startActivity(intent);
                        break;
                    case 2:
                        SelectBreakDialog selectBreakDialog = new SelectBreakDialog(activity, R.style.dialog_select){
                            @Override
                            public void gotoBaisinov() {
                                this.dismiss();
                                Intent i = new Intent(activity, BreakActivity.class);
                                i.putExtra("url", Constants.BAI_SI_BU_DE_JIE);
                                activity.startActivity(i);
                            }

                            @Override
                            public void gotoZhihu() {
                                this.dismiss();
                                Intent i = new Intent(activity, BreakActivity.class);
                                i.putExtra("url", Constants.ZHI_HU);
                                activity.startActivity(i);
                            }
                        };

                        selectBreakDialog.show();
                        break;
                    case 3:
                        SelectGameDialog selectGameDialog = new SelectGameDialog(activity, R.style.dialog_select);
                        selectGameDialog.show();
                        break;
                    case 4:
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(), "小的们正在努力开发中...", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });

        ((ViewPager) container).addView(view);

        return view;
    }

    static class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView imageView;
        TextView textView;
    }
}
