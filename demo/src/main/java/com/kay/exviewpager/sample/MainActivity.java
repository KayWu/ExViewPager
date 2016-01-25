package com.kay.exviewpager.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kay.exviewpager.ExViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExViewPager mVertical;
    private ExViewPager mHorizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVertical.startAutoScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVertical.stopAutoScroll();
    }

    private void initViews() {
        mVertical = (ExViewPager) findViewById(R.id.vertical);
        mVertical.setOrientation(ExViewPager.VERTICAL);
        mVertical.setHandleTouch(false);
        mVertical.setCycle(true);
        mVertical.setInterval(3000);
        mVertical.setAdapter(new BasePagerAdapter<String>(generateString()) {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView view = new TextView(container.getContext());
                view.setGravity(Gravity.CENTER);
                view.setText(getItem(position));
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                view.setTextColor(Color.BLACK);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                view.setLayoutParams(params);
                container.addView(view);
                return view;
            }
        });

        mHorizontal = (ExViewPager) findViewById(R.id.horizontal);
        mHorizontal.setHandleTouch(true);
        mHorizontal.setCycle(true);
        mHorizontal.setAdapter(new BasePagerAdapter<Integer>(generateColor()) {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = new View(container.getContext());
                view.setBackgroundColor(getItem(position));
                container.addView(view);
                return view;
            }
        });

    }

    private List<String> generateString() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        return list;
    }

    private List<Integer> generateColor() {
        List<Integer> list = new ArrayList<>();
        list.add(Color.BLUE);
        list.add(Color.YELLOW);
        list.add(Color.CYAN);
        return list;
    }

}
