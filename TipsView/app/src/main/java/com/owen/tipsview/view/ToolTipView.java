/*
 * Copyright 2013 Niek Haarman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.owen.tipsview.view;

import java.util.ArrayList;
import java.util.Collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.owen.tipsview.R;

/**
 * A ViewGroup to visualize ToolTips. Use
 * ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
public class ToolTipView extends LinearLayout implements
        ViewTreeObserver.OnPreDrawListener {

    public static final String TRANSLATION_Y_COMPAT = "translationY";
    public static final String TRANSLATION_X_COMPAT = "translationX";
    public static final String SCALE_X_COMPAT = "scaleX";
    public static final String SCALE_Y_COMPAT = "scaleY";
    public static final String ALPHA_COMPAT = "alpha";

    private RelativeLayout mTopPointerView;
    private View mTopFrame;
    private ViewGroup mContentHolder;
    private TextView mToolTipTV;
    private TextView mSubToolTipTV;
    private TextView mKnowToolTipTV;
    private View mBottomFrame;
    private RelativeLayout mBottomPointerView;
    private ProgressBar pro_top;
    private ProgressBar pro_down;
    private ImageView img_top;
    private ImageView img_down;
    private View mShadowView;

    private ToolTip mToolTip;
    private View mView;

    private Handler mHandler;

    private boolean mDimensionsKnown;
    private int mRelativeMasterViewY;

    private int mRelativeMasterViewX;
    private int mWidth;

    private OnToolTipViewClickedListener mListener;

    public ToolTipView(final Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.tooltip, this, true);

        mTopPointerView = (RelativeLayout) findViewById(R.id.tooltip__up);
        mTopFrame = findViewById(R.id.tooltip_topframe);
        mContentHolder = (ViewGroup) findViewById(R.id.tooltip_contentholder);
        mToolTipTV = (TextView) findViewById(R.id.tooltip_contenttv);
        mSubToolTipTV = (TextView) findViewById(R.id.tooltip_subcontenttv);
        mKnowToolTipTV = (TextView) findViewById(R.id.tooltip_knowcontenttv);
        mBottomFrame = findViewById(R.id.tooltip_bottomframe);
        mBottomPointerView = (RelativeLayout) findViewById(R.id.tooltip__down);
        pro_top = (ProgressBar) findViewById(R.id.pro_top);
        pro_down = (ProgressBar) findViewById(R.id.pro_down);
        img_top = (ImageView) findViewById(R.id.img_top);
        img_down = (ImageView) findViewById(R.id.img_down);
        pro_top.setProgress(0);
        pro_down.setProgress(0);
        mShadowView = findViewById(R.id.tooltip_shadow);

        getViewTreeObserver().addOnPreDrawListener(this);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                if (bundle.getBoolean("showBelow")) {
                    pro_top.setProgress(bundle.getInt("progress"));
                } else {
                    pro_down.setProgress(bundle.getInt("progress"));
                }
            }
        };
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        mDimensionsKnown = true;

        // mWidth = mContentHolder.getWidth();

        int a = mToolTipTV.getText().length();
        int b = mSubToolTipTV.getText().length();
        if (b > 11) {
            b = 11;
        }

        Paint pFont = mToolTipTV.getPaint();
        Paint pSubFont = mSubToolTipTV.getPaint();
        Rect rect = new Rect();
        Rect subRect = new Rect();
        pFont.getTextBounds(mToolTipTV.getText().toString(), 0, a, rect);
        pSubFont.getTextBounds(mSubToolTipTV.getText().toString(), 0, b,
                subRect);

        int x = rect.width();
        int y = subRect.width();

        int max = x;
        if (x < y) {
            max = y;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = max * 3 / 2;
        setLayoutParams(layoutParams);
        mWidth = max * 3 / 2;

        if (mToolTip != null) {
            applyToolTipPosition();
        }
        return true;
    }

    public void setToolTip(final ToolTip toolTip, final View view) {
        mToolTip = toolTip;
        mView = view;

        if (mToolTip.getText() != null) {
            mToolTipTV.setText(mToolTip.getText());
        } else if (mToolTip.getTextResId() != 0) {
            mToolTipTV.setText(mToolTip.getTextResId());
        } else {
            mToolTipTV.setVisibility(View.GONE);
        }

        if (mToolTip.getSubText() != null) {
            mSubToolTipTV.setText(mToolTip.getSubText());
        } else if (mToolTip.getSubTextResId() != 0) {
            mSubToolTipTV.setText(mToolTip.getSubTextResId());
        } else {
            mSubToolTipTV.setVisibility(View.GONE);
        }

        if (mToolTip.getTypeface() != null) {
            mToolTipTV.setTypeface(mToolTip.getTypeface());
        }

        if (mToolTip.getSubTypeface() != null) {
            mSubToolTipTV.setTypeface(mToolTip.getSubTypeface());
        }

        if (mToolTip.getTextColor() != 0) {
            mToolTipTV.setTextColor(mToolTip.getTextColor());
            mKnowToolTipTV.setTextColor(mToolTip.getTextColor());
        }
        if (mToolTip.getSubTextColor() != 0) {
            mSubToolTipTV.setTextColor(mToolTip.getSubTextColor());
            mKnowToolTipTV.setTextColor(mToolTip.getSubTextColor());
        }

        mKnowToolTipTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                if (mListener != null) {
                    mListener.onToolTipViewClicked(ToolTipView.this);
                }
            }
        });

        if (mToolTip.getColor() != 0) {
            setColor(mToolTip.getColor());
        }

        if (mToolTip.getContentView() != null) {
            setContentView(mToolTip.getContentView());
        }

        if (!mToolTip.shouldShowShadow()) {
            mShadowView.setVisibility(View.GONE);
        }

        if (mDimensionsKnown) {
            applyToolTipPosition();
        }
    }

    private void applyToolTipPosition() {
        final int[] masterViewScreenPosition = new int[2];
        mView.getLocationInWindow(masterViewScreenPosition);
        Log.i("TAG","x==="+masterViewScreenPosition[0]+"===y==="+masterViewScreenPosition[1]);

        final Rect viewDisplayFrame = new Rect();
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int[] parentViewScreenPosition = new int[2];
        ((View) getParent()).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        mRelativeMasterViewX = masterViewScreenPosition[0]
                - parentViewScreenPosition[0];
        mRelativeMasterViewY = masterViewScreenPosition[1]
                - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX
                + masterViewWidth / 2;

        int toolTipViewAboveY = mRelativeMasterViewY - getHeight();
        int toolTipViewBelowY = Math.max(0, mRelativeMasterViewY
                + masterViewHeight);

        int toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - mWidth;
        }

        setX((float) toolTipViewX);
        setPointerCenterX(relativeMasterViewCenterX);

        final boolean showBelow = toolTipViewAboveY < 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ViewHelper.setAlpha(mTopPointerView, showBelow ? 1 : 0);
            ViewHelper.setAlpha(mBottomPointerView, showBelow ? 0 : 1);

            ViewHelper.setAlpha(img_top, showBelow ? 1 : 0);
            ViewHelper.setAlpha(img_down, showBelow ? 0 : 1);

            ViewHelper.setAlpha(pro_top, showBelow ? 1 : 0);
            ViewHelper.setAlpha(pro_down, showBelow ? 0 : 1);
        } else {
            mTopPointerView.setVisibility(showBelow ? VISIBLE : GONE);
            mBottomPointerView.setVisibility(showBelow ? GONE : VISIBLE);
            pro_top.setVisibility(showBelow ? VISIBLE : GONE);
            pro_down.setVisibility(showBelow ? GONE : VISIBLE);
            img_top.setVisibility(showBelow ? VISIBLE : GONE);
            img_down.setVisibility(showBelow ? GONE : VISIBLE);

        }

        int toolTipViewY;
        if (showBelow) {
            toolTipViewY = toolTipViewBelowY - 10;
        } else {
            toolTipViewY = toolTipViewAboveY + 10;
        }
        setY((float) toolTipViewY);

        if (mToolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            // ViewHelper.setTranslationY(mContentHolder, toolTipViewY);
            // ViewHelper.setTranslationX(mContentHolder, toolTipViewX);
            Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            // 设置动画时间
            alphaAnimation.setDuration(200);
            mTopPointerView.startAnimation(alphaAnimation);
            mBottomPointerView.startAnimation(alphaAnimation);
            alphaAnimation.setDuration(700);
            mContentHolder.startAnimation(alphaAnimation);
            mBottomFrame.startAnimation(alphaAnimation);
            mTopFrame.startAnimation(alphaAnimation);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int progress = 0; progress < 100; progress++) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("progress", progress);
                        bundle.putBoolean("showBelow", showBelow);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            Collection<Animator> animators = new ArrayList<>(5);

            if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_Y_COMPAT,
                        mRelativeMasterViewY + mView.getHeight() / 2
                                - getHeight() / 2, toolTipViewY));
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_X_COMPAT,
                        mRelativeMasterViewX + mView.getWidth() / 2 - mWidth
                                / 2, toolTipViewX));
            } else if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_TOP) {
                animators.add(ObjectAnimator.ofFloat(this,
                        TRANSLATION_Y_COMPAT, 0, toolTipViewY));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 0, 1));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 0, 1));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 0, 1));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                animatorSet.addListener(new AppearanceAnimatorListener(
                        toolTipViewX, toolTipViewY));
            }

            animatorSet.start();
        }

    }

    public void setPointerCenterX(final int pointerCenterX) {
        int pointerWidth = Math.max(mTopPointerView.getMeasuredWidth(),
                mBottomPointerView.getMeasuredWidth());

        ViewHelper.setX(mTopPointerView, pointerCenterX - pointerWidth / 2
                - (int) getX());
        ViewHelper.setX(mBottomPointerView, pointerCenterX - pointerWidth / 2
                - (int) getX());
    }

    public void setOnToolTipViewClickedListener(
            final OnToolTipViewClickedListener listener) {
        mListener = listener;
    }

    public void setColor(final int color) {
        // mTopPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mTopFrame.getBackground().setColorFilter(color,
                PorterDuff.Mode.MULTIPLY);
        // mBottomPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mBottomFrame.getBackground().setColorFilter(color,
                PorterDuff.Mode.MULTIPLY);
        mContentHolder.setBackgroundColor(color);
    }

    private void setContentView(final View view) {
        mContentHolder.removeAllViews();
        mContentHolder.addView(view);
    }

    public void remove() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            setX(params.leftMargin);
            setY(params.topMargin);
            params.leftMargin = 0;
            params.topMargin = 0;
            setLayoutParams(params);
        }

        if (mToolTip.getAnimationType() == ToolTip.AnimationType.NONE) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(this);
            }
        } else {
            Collection<Animator> animators = new ArrayList<>(5);
            if (mToolTip.getAnimationType() == ToolTip.AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_Y_COMPAT,
                        (int) getY(), mRelativeMasterViewY + mView.getHeight()
                                / 2 - getHeight() / 2));
                animators.add(ObjectAnimator.ofInt(this, TRANSLATION_X_COMPAT,
                        (int) getX(), mRelativeMasterViewX + mView.getWidth()
                                / 2 - mWidth / 2));
            } else {
                animators.add(ObjectAnimator.ofFloat(this,
                        TRANSLATION_Y_COMPAT, getY(), 0));
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 1, 0));
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 1, 0));

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 1, 0));

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.addListener(new DisappearanceAnimatorListener());
            animatorSet.start();
        }
    }

    /**
     * Convenience method for getting X.
     */
    @SuppressLint("NewApi")
    @Override
    public float getX() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getX();
        } else {
            result = ViewHelper.getX(this);
        }
        return result;
    }

    /**
     * Convenience method for setting X.
     */
    @SuppressLint("NewApi")
    @Override
    public void setX(final float x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setX(x);
        } else {
            ViewHelper.setX(this, x);
        }
    }

    /**
     * Convenience method for getting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public float getY() {
        float result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result = super.getY();
        } else {
            result = ViewHelper.getY(this);
        }
        return result;
    }

    /**
     * Convenience method for setting Y.
     */
    @SuppressLint("NewApi")
    @Override
    public void setY(final float y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setY(y);
        } else {
            ViewHelper.setY(this, y);
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param context
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param context
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param context
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param context
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public interface OnToolTipViewClickedListener {
        void onToolTipViewClicked(ToolTipView toolTipView);
    }

    private class AppearanceAnimatorListener extends AnimatorListenerAdapter {

        private final float mToolTipViewX;
        private final float mToolTipViewY;

        AppearanceAnimatorListener(final float fToolTipViewX,
                final float fToolTipViewY) {
            mToolTipViewX = fToolTipViewX;
            mToolTipViewY = fToolTipViewY;
        }

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        @SuppressLint("NewApi")
        public void onAnimationEnd(final Animator animation) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            params.leftMargin = (int) mToolTipViewX;
            params.topMargin = (int) mToolTipViewY;
            setX(0);
            setY(0);
            setLayoutParams(params);
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }

    private class DisappearanceAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationStart(final Animator animation) {
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            if (getParent() != null) {
                ((ViewManager) getParent()).removeView(ToolTipView.this);
            }
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
        }

        @Override
        public void onAnimationRepeat(final Animator animation) {
        }
    }
}
