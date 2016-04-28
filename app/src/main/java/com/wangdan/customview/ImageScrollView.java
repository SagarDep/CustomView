package com.wangdan.customview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * 水平滚动的scrollview，里面包含一排自动滚动的图片
 *
 * @author blueberry
 */
public class ImageScrollView extends HorizontalScrollView {

    private static final String TAG = ImageScrollView.class.getSimpleName();
    private Handler handler = new Handler(Looper.getMainLooper());
    private int scrollSpeed = 3;// 滚动速度,默认15
    private int scrollCount = Integer.MAX_VALUE - 1;// 滚动完整的次数，默认为最大
    private int divide = 0;// 每个图片之间的间隔
    // private Timer timer;
    // private ScrollTimerTask timerTask;

    private int mScreenWitdh = 0;

    private Context mContext;
    private LinearLayout mContainer;// HorizontalScrollView下唯一一个子布局
    private List<Bitmap> listBitmap = new ArrayList<Bitmap>();// 设置的图片资源,全局变量

    public ImageScrollView(Context context) {
        super(context);
    }

    public ImageScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 获得屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;
        this.setFocusable(false);
        this.setClickable(false);
        this.setSmoothScrollingEnabled(true);
    }

    public ImageScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) this.getChildAt(0);// 得到本View下第一个子view，也是唯一一个
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bringToFront(this);
        super.onDraw(canvas);
    }

    /**
     * 使view显示在最上层
     *
     * @param view
     */
    private void bringToFront(View view) {
        view.getParent().requestLayout();
        view.bringToFront();
    }

    /**
     * 按次数显示滚动图片
     *
     * @param count
     * @param list
     */
    public synchronized void setImageContent(int count, List<Bitmap> list) {
        Log.i(TAG, "setImageCount:" + list.size());
        setImageData(list);
        scrollCount = count;
    }

    /**
     * 按时间显示滚动图片
     *
     * @param durion
     * @param list
     */
    public synchronized void setImageContent(long durion, List<Bitmap> list) {
        setImageData(list);
        handler.postDelayed(hideView, durion);
    }

    /**
     * 深拷贝，保存图片数据资源
     *
     * @param list
     */
    private void setImageData(List<Bitmap> list) {
        listBitmap = new ArrayList< Bitmap >();
        if (list == null) {
            list = listBitmap;
        }
        doWorkChangeBitmap(list);
    }

    /**
     * 显示滚动图片
     *
     * @param list
     */
    public void setImageContent(List<Bitmap> list) {
        Log.i(TAG, "setImageContent---list.size:" + list.size());
        mContainer.removeAllViews();
        if (list == null || list.size() <= 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
                return;
            }
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setImageBitmap(list.get(i));
            if (i == 0) {
                setMargins(imageView, mScreenWitdh, 0, divide, 0);// 第一个图片距离左边全屏宽度
            } else if (i == list.size() - 1) {
                setMargins(imageView, divide, 0, mScreenWitdh, 0);// 最后一个图片距离右边全屏宽度
            } else {
                setMargins(imageView, divide, 0, divide, 0);
            }
            mContainer.addView(imageView);
        }
        startImageScroll();// 开始滚动

    }

    Runnable hideView = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "hideView");
            stopScroll();
            setImageScrollViewHide();
        }

    };

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            Log.i(TAG, "onWindowVisibilityChanged");
        }
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * 设置图片之间的间隔
     *
     * @param imageDivide
     */
    public void setImageViewDivide(int imageDivide) {
        divide = imageDivide;
    }

    /**
     * 设置图片的滚动速度
     *
     * @param speed
     */
    public void setScrollSpeed(int speed) {
        scrollSpeed = speed;
    }

    /**
     * 开始滚动
     */
    private void startImageScroll() {
        stopScroll();
        this.smoothScrollTo(0, getScrollY());// 滚动前置到最左位置
        handler.postDelayed(smoothAction, 100);
    }

    Runnable smoothAction = new Runnable() {
        @Override
        public void run() {
            ImageScrollView.this.smoothScrollBy(scrollSpeed, ImageScrollView.this.getScrollY());
            Log.i(TAG, "isScrollRight:" + isScrollRight());
            if (isScrollRight())// 判断是否要继续显示
            {
                scrollContinue();
            } else {
                handler.postDelayed(smoothAction, 5);
            }
            ImageScrollView.this.postInvalidate();
        }
    };

    /**
     * 停止滚动
     */
    public void stopScroll() {
        handler.removeCallbacks(smoothAction);
    }

    /**
     * 判断是否滚动到最右边
     *
     * @return
     */
    private Boolean isScrollRight() {
        View view = (View) this.getChildAt(this.getChildCount() - 1);
        int subViewWidth = view.getRight();
        int x = this.getScrollX();
        if (subViewWidth - x - this.getWidth() == 0)
            return true;
        else
            return false;
    }

    /**
     * 继续下一次的显示与滚动
     */
    private void scrollContinue() {
        stopScroll();
        handler.post(scrollCountAction);

    }

    // 滚动结束时的回调
    public IScrollFinishCallBack mCallback;

    public interface IScrollFinishCallBack {
        void scrollFinish();
    }

    public void setOnScrollFinishListener(IScrollFinishCallBack callback) {
        mCallback = callback;
    }

    private Runnable scrollCountAction = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "scrollCount:" + scrollCount);
            if (scrollCount > 0) {
                stopScroll();
                setImageContent(listBitmap);
            } else {
                // 通知OSD刷新
                Log.i(TAG, "scrollCountAction");
                stopScroll();
                setImageScrollViewHide();
                mCallback.scrollFinish();
                // GifDecodeShowFrame.getInstance().setFirstCallback( true );//
                // 重置GIF解析回调
                // AdMsgManeger.getInstance().notifyUpdataAD( AD_Type.AD_OSD
                // );// 业务需求：展示完成通知协议栈重新获取广告
            }
            scrollCount--;
        }
    };

    /**
     * 先清空图片，再隐藏
     */
    public void setImageScrollViewHide() {
        this.mContainer.removeAllViews();
        this.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置view的margin属性
     *
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void setMargins(View view, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, right, bottom);
        view.setLayoutParams(lp);
    }

    private Runnable changeBitmapRunable = new Runnable() {
        @Override
        public void run() {
            handler.post(scrollCountAction);
        }
    };

    /**
     * 工作线程，改变位图
     *
     * @param
     */
    private void doWorkChangeBitmap(final List<Bitmap> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Bitmap bm : list) {
                    listBitmap.add(changeAlpahBitmap(bm));
                }
                handler.removeCallbacks(changeBitmapRunable);
                handler.postAtFrontOfQueue(changeBitmapRunable);
            }
        }).start();
    }

    /**
     * 业务需求：将除白色以外的区域变成透明
     *
     * @param bitmap
     * @return
     */
    private synchronized Bitmap changeAlpahBitmap(Bitmap bitmap) {
        Log.i(TAG, "changeAlpahBitmap...");
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        int len = bitmapHeight * bitmapWidth;
        int[] imageARGB = new int[len];
        int[] newImage = new int[len];

        Bitmap bm = bitmap.copy(Config.ARGB_8888, true);// 拷贝一个副本，使其位图可编辑

        int red;
        int greed;
        int blue;
        int alpha;
        int pix;

        bitmap.getPixels(imageARGB, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < bitmapHeight; i++) {
            for (int j = 0; j < bitmapWidth; j++) {
                if (imageARGB[i * bitmapWidth + j] != 0) {
                    alpha = ((imageARGB[i * bitmapWidth + j] & 0xff000000) >> 24);
                    red = ((imageARGB[i * bitmapWidth + j] & 0x00ff0000) >> 16);
                    greed = ((imageARGB[i * bitmapWidth + j] & 0x0000ff00) >> 8);
                    blue = ((imageARGB[i * bitmapWidth + j] & 0x000000ff));
                    if (!(red == 255 && greed == 255 & blue == 255)) {
                        alpha = 0;
                        red = 0;
                        greed = 0;
                        blue = 0;
                    }
                    pix = (alpha << 24) | (red << 16) | (greed << 8) | blue;
                    newImage[i * bitmapWidth + j] = pix;
                } else {
                    newImage[i * bitmapWidth + j] = imageARGB[i * bitmapWidth + j];
                }
            }
        }

        bm.setPixels(newImage, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);// 设置新的像素
        return bm;

    }

}
