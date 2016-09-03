package com.owen.tipsview.view;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;

import com.owen.tipsview.R;

import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by owen on 16/9/3.
 */
public class ToolTipManage {
    public static ToolTipManage toolTipManage;
    private ArrayList<View> views = new ArrayList<View>();
    private ArrayList<String> resourcesIds = new ArrayList<String>();
    private int curr_index = 0;

    public ToolTipManage(Context context) {
    }

    public static ToolTipManage getInstance(Context context) {
        if (toolTipManage == null) {
            synchronized (ToolTipManage.class) {
                if (toolTipManage == null) {
                    ToolTipManage temp = new ToolTipManage(context);
                    toolTipManage = temp;
                }
            }
        }
        return toolTipManage;
    }

    public void showGuide(Context mContext, ArrayList<View> views,
                          ArrayList<String> resourcesIds) {
        this.views = views;
        this.resourcesIds = resourcesIds;
        curr_index = 0;
        show(mContext, views.get(curr_index), resourcesIds.get(curr_index));

    }

    public static GuideResources.G_Resources getmayGuideResources(Context context, String id) {
        GuideResources guideResources;
        GuideResources.G_Resources gr = null;
        guideResources = getGuideResources(context);
        if (guideResources != null) {
            gr = guideResources.get().get(id);
        }
        return gr;
    }

    private static GuideResources getGuideResources(Context context) {
        GuideResources guideResources = null;
        GuideResourcesParser guideRp = new GuideResourcesParser();
        try {
            guideResources = guideRp.fromXmlResource(
                    context.getResources().getXml(R.xml.guide_resources));
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return guideResources;
    }

    void show(final Context mContext, View view, String id) {
        GuideResources.G_Resources gr = getmayGuideResources(mContext,
                id);
        if (gr == null)
            return;
        ToolTip toolTip = new ToolTip()
                .withText(gr.getText())
                .withTextColor(mContext.getResources().getColor(R.color.white))
                .withSubText(gr.getSubText())
                .withSubTextColor(
                        mContext.getResources().getColor(R.color.white))
                .withColor(
                        mContext.getResources().getColor(
                                R.color.common_title_background)).withShadow()
                .withAnimationType(ToolTip.AnimationType.NONE);

        ToolTipDialog toolTipDialog = new ToolTipDialog(mContext,
                R.style.guideDailog);
        toolTipDialog.showToolTipForView(toolTip, view);
        toolTipDialog.show();
        toolTipDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        curr_index++;
                        if (curr_index < views.size()) {
                            show(mContext, views.get(curr_index),
                                    resourcesIds.get(curr_index));
                        } else {
                            curr_index = 0;
                        }
                    }
                });
    }


}
