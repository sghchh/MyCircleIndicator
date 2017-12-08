package com.example.as.mycircleindicator;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;
    private CircleIndicatorView mIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init()
    {
        // 初始化ViewPager 相关
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        mIndicatorView = (CircleIndicatorView) findViewById(R.id.indicator_view);
        // 关联ViewPager
        mIndicatorView.attachViewPager(mViewPager);
    }
}
