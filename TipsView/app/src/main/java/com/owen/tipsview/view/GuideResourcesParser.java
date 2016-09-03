package com.owen.tipsview.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.XmlResourceParser;


public class GuideResourcesParser {
    public static final String TAG_GUIDERESOURCES = "guideresources";
    public static final String TAG_RESOURCES = "resources";

    public static final String ATTR_ID = "id";
    public static final String ATTR_TEXT = "text";
    public static final String ATTR_SUBTEXT = "subtext";
    public static final String ATTR_IMAGE = "image";
    private XmlPullParser mXpp;
    private GuideResources guideResources;

    public GuideResourcesParser() {

    }

    public GuideResources fromXml(InputStream in)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        mXpp = factory.newPullParser();
        mXpp.setInput(new InputStreamReader(in));

        return parse();
    }

    public GuideResources fromXmlResource(XmlResourceParser in)
            throws XmlPullParserException, IOException {
        mXpp = in;

        return parse();
    }

    public GuideResources parse() throws XmlPullParserException, IOException {

        guideResources = new GuideResources();

        int eventType = mXpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = mXpp.getName();

            if (eventType == XmlPullParser.START_TAG) {
                if (tag.equals(TAG_GUIDERESOURCES)) {

                } else if (tag.equals(TAG_RESOURCES)) {
                    addMimeTypeStart();

                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (tag.equals(TAG_GUIDERESOURCES)) {

                }
            }

            eventType = mXpp.next();
        }

        return guideResources;
    }

    private void addMimeTypeStart() {
        String id = mXpp.getAttributeValue(null, ATTR_ID);
        String text = mXpp.getAttributeValue(null, ATTR_TEXT);
        String subtext = mXpp.getAttributeValue(null, ATTR_SUBTEXT);
        String imageId = mXpp.getAttributeValue(null, ATTR_IMAGE);
        guideResources.put(id, text, imageId, subtext);

    }
}
