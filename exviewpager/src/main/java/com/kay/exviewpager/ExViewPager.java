package com.kay.exviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 16/1/25.
 */
public class ExViewPager extends ViewPager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int SCROLL_WHAT = 0;
    public static final int DEFAULT_INTERVAL = 4000;
    private static final boolean DEFAULT_BOUNDARY_CASHING = true;

    private int orientation;
    private boolean mBoundaryCaching = DEFAULT_BOUNDARY_CASHING;
    private boolean isCycle = false;
    private LoopPagerAdapterWrapper mAdapterWrapper;
    private List<OnPageChangeListener> mOuterPageChangeListeners;

    private float swipeScrollFactor = 1.0f;
    private float autoScrollFactor = 2.4f;

    private boolean handleTouch = true;
    private boolean isAutoScroll = false;
    private boolean stopScrollWhenTouch = true;
    private boolean isStopByTouch = false;
    private long interval = DEFAULT_INTERVAL;

    private Context mContext;
    private Handler mHandler;
    private CustomScroller mScroller;

    public ExViewPager(Context context) {
        this(context, null);
    }

    public ExViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        changeScroller();
        super.addOnPageChangeListener(mOnPageChangeListener);
        mHandler = new ViewPagerHandler(this);
    }

    public void setOrientation(int orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            if (orientation == HORIZONTAL) {
                setPageTransformer(true, null);
            } else {
                setPageTransformer(true, new VerticalPageTransformer());
            }
            requestLayout();
        }
    }

    public void setHandleTouch(boolean handleTouch) {
        this.handleTouch = handleTouch;
    }

    /*
     * Begin of cycle related method
     */

    /**
     * If set to true, the boundary views (i.e. first and last) will never be destroyed
     * This may help to prevent "blinking" of some views
     *
     * @param flag
     */
    public void setBoundaryCaching(boolean flag) {
        mBoundaryCaching = flag;
        if (mAdapterWrapper != null) {
            mAdapterWrapper.setBoundaryCaching(flag);
        }
    }

    /**
     * Works only if data has more than 1 element.
     * This method will clear all the PageChangeListeners added before.
     * @param isCycle
     */
    public void setCycle(boolean isCycle) {
        if (this.isCycle != isCycle) {
            if (mOuterPageChangeListeners != null) mOuterPageChangeListeners.clear();
            clearOnPageChangeListeners();
            super.addOnPageChangeListener(mOnPageChangeListener);

            if (getAdapter() != null) {
                PagerAdapter realAdapter = getAdapter();
                setAdapter(realAdapter);
            }
            this.isCycle = isCycle;
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter == null) return;
        if (!isCycle || (adapter.getCount() <= 1)) {
            isCycle = false;
            mAdapterWrapper = null;
            super.setAdapter(adapter);
        } else {
            mAdapterWrapper = new LoopPagerAdapterWrapper(adapter);
            mAdapterWrapper.setBoundaryCaching(mBoundaryCaching);
            super.setAdapter(mAdapterWrapper);
            setCurrentItem(0, false);
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        if (!isCycle) {
            return super.getAdapter();
        } else {
            return mAdapterWrapper == null ? null : mAdapterWrapper.getRealAdapter();
        }
    }

    @Override
    public int getCurrentItem() {
        if (!isCycle) {
            return super.getCurrentItem();
        } else {
            return mAdapterWrapper == null ? 0 : mAdapterWrapper.toRealPosition(super.getCurrentItem());
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (!isCycle) {
            super.setCurrentItem(item, smoothScroll);
        } else {
            if (mAdapterWrapper == null) return;
            int realItem = mAdapterWrapper.toInnerPosition(item);
            super.setCurrentItem(realItem, smoothScroll);
        }
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        if (!isCycle) {
            super.setOnPageChangeListener(listener);
        } else {
            if (mOuterPageChangeListeners == null) {
                mOuterPageChangeListeners = new ArrayList<>();
            }
            mOuterPageChangeListeners.add(listener);
        }
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (!isCycle) {
            super.addOnPageChangeListener(listener);
        } else {
            if (mOuterPageChangeListeners == null) {
                mOuterPageChangeListeners = new ArrayList<>();
            }
            mOuterPageChangeListeners.add(listener);
        }
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mAdapterWrapper == null) return;
            int realPosition = mAdapterWrapper.toRealPosition(position);
            if (positionOffset == 0 && mPreviousOffset == 0
                    && (position == 0 || position == mAdapterWrapper.getCount() - 1)) {
                setCurrentItem(realPosition, false);
            }
            mPreviousOffset = positionOffset;
            if (mOuterPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOuterPageChangeListeners) {
                    if (realPosition != mAdapterWrapper.getRealCount() - 1) {
                        listener.onPageScrolled(realPosition,
                                positionOffset, positionOffsetPixels);
                    } else {
                        if (positionOffset > .5) {
                            listener.onPageScrolled(0, 0, 0);
                        } else {
                            listener.onPageScrolled(realPosition, 0, 0);
                        }
                    }
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mAdapterWrapper == null) return;
            int realPosition = mAdapterWrapper.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListeners != null) {
                    for (OnPageChangeListener listener : mOuterPageChangeListeners) {
                        listener.onPageSelected(realPosition);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mAdapterWrapper == null) return;
            int position = ExViewPager.super.getCurrentItem();
            int realPosition = mAdapterWrapper.toRealPosition(position);
            if (state == ViewPager.SCROLL_STATE_IDLE
                    && (position == 0 || position == mAdapterWrapper.getCount() - 1)) {
                setCurrentItem(realPosition, false);
            }
            if (mOuterPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOuterPageChangeListeners) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }
    };

    /*
     * End of cycle related method
     */
    private void changeScroller() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new CustomScroller(mContext, new Interpolator() {
                public float getInterpolation(float t) {
                    t -= 1.0f;
                    return t * t * t + 1.0f;
                }
            });
            scroller.set(this, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setAutoScrollFactor(float autoScrollFactor) {
        this.autoScrollFactor = autoScrollFactor;
    }

    public void setSwipeScrollFactor(float swipeScrollFactor) {
        this.swipeScrollFactor = swipeScrollFactor;
    }

    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage(interval);
    }

    public void stopAutoScroll() {
        isAutoScroll = false;
        mHandler.removeMessages(SCROLL_WHAT);
    }

    private void sendScrollMessage(long interval) {
        mHandler.removeMessages(SCROLL_WHAT);
        mHandler.sendEmptyMessageDelayed(SCROLL_WHAT, interval);
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void scrollOnce() {
        if (getAdapter() == null || getAdapter().getCount() <= 1) return;
        int position = super.getCurrentItem();
        int nextItem = (position + 1) % (super.getAdapter().getCount());
        super.setCurrentItem(nextItem);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (handleTouch && stopScrollWhenTouch) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
                startAutoScroll();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!handleTouch) return false;
        if (orientation == VERTICAL) {
            boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
            swapXY(ev); // return touch coordinates to original reference frame for any child views
            return intercepted;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        ev.setLocation(newX, newY);
        return ev;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!handleTouch) return false;
        if (orientation == VERTICAL) {
            return super.onTouchEvent(swapXY(ev));
        } else {
            return super.onTouchEvent(ev);
        }
    }

    public static class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(android.view.View page, float position) {
            if (position > -1 && position < 1) {
                page.setAlpha(1);
                // Counteract the default slide transition
                float positionX = page.getWidth() * (-position);
                page.setTranslationX(positionX);

                float positionY = position * page.getHeight();
                page.setTranslationY(positionY);
            } else {
                page.setAlpha(0);
            }
        }
    }

    private static class CustomScroller extends Scroller {

        private float factor = 1;

        public CustomScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void setFactor(float factor) {
            this.factor = factor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (duration * factor));
        }
    }

    private static class ViewPagerHandler extends Handler {
        private final WeakReference<ExViewPager> mExViewPager;

        public ViewPagerHandler(ExViewPager viewPager) {
            mExViewPager = new WeakReference<ExViewPager>(viewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    ExViewPager pager = mExViewPager.get();
                    if (pager != null) {
                        pager.mScroller.setFactor(pager.autoScrollFactor);
                        pager.scrollOnce();
                        pager.mScroller.setFactor(pager.swipeScrollFactor);
                        pager.sendScrollMessage(pager.interval + pager.mScroller.getDuration());
                    }
                    break;
            }
        }
    }
}
