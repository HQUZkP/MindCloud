package com.kaipingzhou.mindcloud.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.kaipingzhou.mindcloud.controller.fragment.BucketsFragment;
import com.kaipingzhou.mindcloud.controller.fragment.ImagesFragment;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by 周开平 on 2017/3/27 20:17.
 * qq 275557625@qq.com
 * 作用：
 */
public class SelectPictureActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        Fragment newFragment = new BucketsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(android.R.id.content, newFragment);

        transaction.commit();
    }

    public void showBucket(final int bucketId)
    {
        Bundle b = new Bundle();
        b.putInt("bucket", bucketId);
        Fragment f = new ImagesFragment();
        f.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, f).addToBackStack(null).commit();
    }

    public void imageSelected(final String imgPath, final String imgTaken, final long imageSize)
    {
        Intent result = new Intent();
        result.putExtra("imgPath", imgPath);
        result.putExtra("dateTaken", imgTaken);
        result.putExtra("imageSize", imageSize);
        setResult(RESULT_OK, result);
        finish();
    }
}
