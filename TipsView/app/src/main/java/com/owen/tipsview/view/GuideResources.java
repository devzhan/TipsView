package com.owen.tipsview.view;

import java.util.HashMap;
import java.util.Map;

public class GuideResources {

    private Map<String, G_Resources> guideResources;

    public GuideResources() {
        guideResources = new HashMap<String, G_Resources>();
    }

    public void put(String id, String text, String image, String subtext) {
        G_Resources resources = new G_Resources();
        resources.setImageId(image);
        resources.setText(text);
        resources.setSubText(subtext);
        guideResources.put(id, resources);
    }

    public Map<String, G_Resources> get() {
        return guideResources;
    }

    public class G_Resources {
        private String text;
        private String subText;
        private String imageId;

        public String getSubText() {

            return subText;
        }

        public void setSubText(String subText) {
            this.subText = subText;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }
    }

}
