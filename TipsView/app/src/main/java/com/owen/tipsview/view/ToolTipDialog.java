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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.owen.tipsview.R;
import com.owen.tipsview.view.exception.NoOverflowMenuRuntimeException;
import com.owen.tipsview.view.exception.NoTitleViewRuntimeException;
import com.owen.tipsview.view.exception.ViewNotFoundRuntimeException;


public class ToolTipDialog extends Dialog implements View.OnClickListener {

    public static final String ACTION_BAR_TITLE = "action_bar_title";
    public static final String ID = "id";
    public static final String ANDROID = "android";
    public static final String ACTION_BAR = "action_bar";
    public static final String ACTION_MENU_VIEW = "ActionMenuView";
    public static final String OVERFLOW_MENU_BUTTON = "OverflowMenuButton";

    private DisplayMetrics metrics;
    private int window_W;
    private int window_H;

    public ToolTipDialog(final Context context, final int theme) {

        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tooltips_dialog);
        metrics = context.getResources().getDisplayMetrics();
        window_W = metrics.widthPixels;
        window_H = metrics.heightPixels;
        getWindow().setLayout(window_W, window_H);
    }

    @TargetApi(11)
    private static View findActionBarOverflowMenuView(final Activity activity) {
        final ViewGroup decorView = (ViewGroup) activity.getWindow()
                .getDecorView();

        final int actionBarViewResId = Resources.getSystem().getIdentifier(
                ACTION_BAR, ID, ANDROID);
        final ViewGroup actionBarView = (ViewGroup) decorView
                .findViewById(actionBarViewResId);

        ViewGroup actionMenuView = null;
        int actionBarViewChildCount = actionBarView.getChildCount();
        for (int i = 0; i < actionBarViewChildCount; ++i) {
            if (actionBarView.getChildAt(i).getClass().getSimpleName()
                    .equals(ACTION_MENU_VIEW)) {
                actionMenuView = (ViewGroup) actionBarView.getChildAt(i);
            }
        }

        if (actionMenuView == null) {
            throw new NoOverflowMenuRuntimeException();
        }

        int actionMenuChildCount = actionMenuView.getChildCount();
        View overflowMenuButton = null;
        for (int i = 0; i < actionMenuChildCount; ++i) {
            if (actionMenuView.getChildAt(i).getClass().getSimpleName()
                    .equals(OVERFLOW_MENU_BUTTON)) {
                overflowMenuButton = actionMenuView.getChildAt(i);
            }
        }

        if (overflowMenuButton == null) {
            throw new NoOverflowMenuRuntimeException();
        }

        return overflowMenuButton;
    }

    /**
     * Shows a {@link ToolTipView} based on given {@link ToolTip} at the proper
     * location relative to given {@link View}.
     *
     * @param toolTip
     *            the ToolTip to show.
     * @param view
     *            the View to position the ToolTipView relative to.
     *
     * @return the ToolTipView that was created.
     */
    public ToolTipView showToolTipForView(final ToolTip toolTip, final View view) {
        final ToolTipView toolTipView = new ToolTipView(getContext());
        toolTipView.setToolTip(toolTip, view);
        ((RelativeLayout) findViewById(R.id.rl_guide)).addView(toolTipView);
        toolTipView.setOnClickListener(this);
        return toolTipView;
    }

    /**
     * **EXPERIMENTAL**</p> Shows a {@link ToolTipView} based on given
     * {@link ToolTip} at the proper location relative to the {@link View} with
     * given resource id.</p>NOTE: This method will throw a
     * choose to ignore this by catching the ViewNotFoundRuntimeException.
     *
     * @param activity
     *            the Activity which holds the ActionBar.
     * @param toolTip
     *            the ToolTip to show.
     * @param resId
     *            the resource id of the View to position the ToolTipView
     *            relative to.
     * @return the ToolTipView that was created.
     */
    public ToolTipView showToolTipForViewResId(final Activity activity,
            final ToolTip toolTip, final int resId) {
        final ToolTipView toolTipView = new ToolTipView(getContext());
        final View decorView = activity.getWindow().getDecorView();
        final View view = decorView.findViewById(resId);

        if (view == null) {
            throw new ViewNotFoundRuntimeException();
        }

        toolTipView.setToolTip(toolTip, view);
        return toolTipView;
    }

    /**
     * **EXPERIMENTAL**</p> Shows a {@link ToolTipView} based on given
     * {@link ToolTip} at the proper location relative to the {@link ActionBar}
     * home {@link View}.
     *
     * @param activity
     *            the Activity which holds the ActionBar.
     * @param toolTip
     *            the ToolTip to show.
     * @return the ToolTipView that was created.
     */
    @TargetApi(11)
    public ToolTipView showToolTipForActionBarHome(final Activity activity,
            final ToolTip toolTip) {
        final int homeResId = android.R.id.home;
        return showToolTipForViewResId(activity, toolTip, homeResId);
    }

    /**
     * **EXPERIMENTAL**</p>
     *
     * Shows a {@link ToolTipView} based on given {@link ToolTip} at the proper
     * location relative to the {@link ActionBar} title {@link View}.</p>NOTE:
     * View is not found. You can choose to ignore this by catching the
     * NoTitleViewRuntimeException.</p>NOTE: This method uses an internal API to
     * find the View. It MAY cause your application to crash in future Android
     * versions.
     *
     * @param activity
     *            the Activity which holds the ActionBar.
     * @param toolTip
     *            the ToolTip to show.
     * @return the ToolTipView that was created.
     */
    @TargetApi(11)
    public ToolTipView showToolTipForActionBarTitle(final Activity activity,
            final ToolTip toolTip) {
        final int titleResId = Resources.getSystem().getIdentifier(
                ACTION_BAR_TITLE, ID, ANDROID);
        if (titleResId == 0) {
            throw new NoTitleViewRuntimeException();
        }
        return showToolTipForViewResId(activity, toolTip, titleResId);
    }

    /**
     * **EXPERIMENTAL**</p> Shows a {@link ToolTipView} based on given
     * {@link ToolTip} at the proper location relative to the overflow menu
     * button.</p>NOTE: This method will throw a
     * {@link NoOverflowMenuRuntimeException} if the overflow menu is not found.
     * This happens when there simply is no overflow menu button, or the menu
     * isn't initialised yet. You can choose to ignore this by catching the
     * NoOverflowMenuRuntimeException.</p>NOTE: This method uses an internal API
     * to find the View. It MAY cause your application to crash in future
     * Android versions.
     *
     * @param activity
     *            the Activity which holds the ActionBar.
     * @param toolTip
     *            the ToolTip to show.
     * @return the ToolTipView that was created.
     */
    @TargetApi(11)
    public ToolTipView showToolTipForActionBarOverflowMenu(
            final Activity activity, final ToolTip toolTip) {
        return showToolTipForView(toolTip,
                findActionBarOverflowMenuView(activity));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            dismiss();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
