package com.kay.exviewpager.sample;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Kay on 16/1/13.
 */
public abstract class BasePagerAdapter<T> extends PagerAdapter {

    private List<T> mList;

    public BasePagerAdapter(List<T> list) {
        mList = list;
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
