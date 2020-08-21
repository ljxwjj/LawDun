package com.yunfa365.lawservice.app.ui.activity.common;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yunfa365.lawservice.app.R;
import com.zhihu.matisse.internal.utils.Platform;
import com.zhihu.matisse.listener.OnFragmentInteractionListener;

import java.util.ArrayList;

public class PhotoPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, OnFragmentInteractionListener {

    ViewPager photoViewPage;

    PreviewPagerAdapter mAdapter;

    TextView text;

    String[] images;

    int index;

    protected int mPreviousPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);
        if (Platform.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        photoViewPage = findViewById(R.id.photoViewPage);
        text = findViewById(R.id.text);

        images = getIntent().getStringArrayExtra("images");
        index = getIntent().getIntExtra("index", 0);
        init();
    }

    void init() {
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager());
        photoViewPage.setAdapter(mAdapter);
        photoViewPage.setCurrentItem(index);
        photoViewPage.addOnPageChangeListener(this);
        text.setText(String.format("%d/%d", index + 1, images.length));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((PreviewItemFragment)mAdapter.instantiateItem(photoViewPage, mPreviousPos)).resetView();
        }
        mPreviousPos = position;
        text.setText(String.format("%d/%d", position + 1, images.length));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick() {
        finish();
    }

    public class PreviewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> mItems = new ArrayList<>();

        public PreviewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String item = images[position];
            return PreviewItemFragment.newInstance(item);
        }

        @Override
        public int getCount() {
            return images.length;
        }


    }

}
