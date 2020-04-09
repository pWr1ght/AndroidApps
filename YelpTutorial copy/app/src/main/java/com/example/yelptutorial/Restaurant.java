package com.example.yelptutorial;

import com.yelp.fusion.client.models.Category;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String name;
    private String mUrl;
    private String picUrl;
    private List<String> pictures;
    private String message;
    private int curPic;
    private int iLast;

    public int getiLast() {
        return iLast;
    }

    public void setiLast(int iLast) {
        this.iLast = iLast;
    }

    public int getCurPic() {
        return curPic;
    }


    public Restaurant() {
        name="";
        mUrl="";
        picUrl = "";
        message="";
        pictures = new ArrayList<>();
        curPic=0;
        iLast=0;
    }


    public Restaurant(String name, String mUrl) {
        this.name = name;
        setmUrl(mUrl);
        curPic = 0;
    }

    public void incCurPic() {
        curPic++;
    }

    public void decCurPic() {

        curPic--;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
        setPicUrl(mUrl);
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = mUrl.replace("/biz/", "/biz_photos/");
        this.picUrl += "?tab=food";
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String convertCatToString(ArrayList<Category> cat) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < cat.size(); i++) {
            str.append(cat.get(i).getTitle());
            if (i != cat.size()-1) {
                str.append(", ");
            }
        }
        return String.valueOf(str);
    }
}
