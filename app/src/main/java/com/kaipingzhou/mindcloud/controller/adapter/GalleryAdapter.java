package com.kaipingzhou.mindcloud.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.fragment.BucketsFragment;
import com.kaipingzhou.mindcloud.controller.fragment.domin.BucketItem;
import com.kaipingzhou.mindcloud.controller.fragment.domin.GridItem;
import com.kaipingzhou.mindcloud.utils.ImageLoader;

import java.util.List;


/**
 * Created by 周开平 on 2017/3/27 20:45.
 * qq 275557625@qq.com
 * 作用：
 */
public class GalleryAdapter extends BaseAdapter {
    private final Context context;
    private final List<GridItem> items;
    private final ImageLoader imageLoader;
    private final LayoutInflater mInflater;

    public GalleryAdapter(final Context context, final List<GridItem> buckets) {
        this.items = buckets;
        this.imageLoader = new ImageLoader();
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).hashCode();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (items.get(0) instanceof BucketItem) { // show buckets
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.bucketitem, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BucketItem bi = (BucketItem) items.get(position);
            holder.text.setText(bi.images > 1 ?
                    bi.name + " - " + context.getString(R.string.images, bi.images) : bi.name);
            imageLoader.DisplayImage(bi.path, holder.icon);
            return convertView;
        } else { // show images in a bucket
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = (ImageView) mInflater.inflate(R.layout.imageitem, null);
            } else {
                imageView = (ImageView) convertView;
            }
            imageLoader.DisplayImage(items.get(position).path, imageView);
            return imageView;
        }
    }

    private static class ViewHolder {
        private ImageView icon;
        private TextView text;
    }
}
