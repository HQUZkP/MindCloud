package com.kaipingzhou.mindcloud.controller.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.activity.SelectPictureActivity;
import com.kaipingzhou.mindcloud.controller.adapter.GalleryAdapter;
import com.kaipingzhou.mindcloud.controller.fragment.domin.BucketItem;
import com.kaipingzhou.mindcloud.controller.fragment.domin.GridItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周开平 on 2017/3/27 20:19.
 * qq 275557625@qq.com
 * 作用：
 */

public class BucketsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, null);

        String[] projection = new String[]{MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID};

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, " + MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        final List<GridItem> buckets = new ArrayList<GridItem>();
        BucketItem lastBucket = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (lastBucket == null || !lastBucket.name.equals(cursor.getString(1))) {
                        lastBucket = new BucketItem(cursor.getString(1), cursor.getString(0), "", cursor.getInt(2));
                        buckets.add(lastBucket);
                    } else {
                        lastBucket.images++;
                    }
                    cursor.moveToNext();
                }
            }
            //关闭游标
            cursor.close();
        }

        if (buckets.isEmpty()) {
            Toast.makeText(getActivity(), "未发现任何图片", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            GridView gridView = (GridView) view.findViewById(R.id.gridView);
            gridView.setAdapter(new GalleryAdapter(getActivity(), buckets));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((SelectPictureActivity) getActivity()).showBucket(((BucketItem) buckets.get(position)).id);
                }
            });
        }
        return view;
    }




}
