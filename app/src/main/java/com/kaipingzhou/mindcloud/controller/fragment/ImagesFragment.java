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

import com.kaipingzhou.mindcloud.R;
import com.kaipingzhou.mindcloud.controller.activity.SelectPictureActivity;
import com.kaipingzhou.mindcloud.controller.adapter.GalleryAdapter;
import com.kaipingzhou.mindcloud.controller.fragment.domin.GridItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周开平 on 2017/3/27 20:59.
 * qq 275557625@qq.com
 * 作用：
 */

public class ImagesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, null);

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.SIZE},
                MediaStore.Images.Media.BUCKET_ID + " = ?",
                new String[]{String.valueOf(getArguments().getInt("bucket"))},
                MediaStore.Images.Media.DATE_MODIFIED + " ASC");

        final List<GridItem> images = new ArrayList<GridItem>(cursor.getCount());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    images.add(new GridItem(cursor.getString(1), cursor.getString(0), cursor.getString(2), cursor.getLong(3)));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        GridView grid = (GridView) view.findViewById(R.id.gridView);
        grid.setAdapter(new GalleryAdapter(getActivity(), images));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SelectPictureActivity) getActivity()).imageSelected(images.get(position).path, images.get(position).imageTaken, images.get(position).imageSize);
            }
        });

        return view;
    }
}
