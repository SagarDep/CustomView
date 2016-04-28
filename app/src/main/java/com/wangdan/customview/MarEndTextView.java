package com.wangdan.customview;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarEndTextView extends TextView {
    public MarEndTextView(Context con) {
        super(con);
    }

    public MarEndTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarEndTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * view状态改变时候发生的回调
     */
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (this.isSelected()) {
            setEllipsize(TruncateAt.MARQUEE);
        } else {
            setEllipsize(TruncateAt.END);
        }
    }


}
