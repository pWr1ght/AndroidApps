package com.example.yelptutorial;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class parseRestaurant {


    public static String modifySrc(String src) {
        int srcLength = src.length() - 1;
        while (srcLength != 0) {
            if (src.charAt(srcLength) == '/') {
                src = src.substring(0, srcLength + 1) + "o.jpg";
                break;
            }
            srcLength -= 1;
        }
//        String searchableString = src;
//        String keyword = "select=";
//
//
//        int indexAfterSelect = searchableString.indexOf(keyword) + keyword.length();
//        int lengthOfUrl = searchableString.length();
//        String serialSection = searchableString.substring(indexAfterSelect, lengthOfUrl);
//        src = "https://s3-media2.fl.yelpcdn.com/bphoto/" + serialSection + "/o.jpg";

        return src;
    }


    public static List<String> parsePicture(String html) throws IOException {
        Document doc = Jsoup.connect(html).get();
        Elements images = doc.select("div.photo-box > img");
        List<String> photos = new ArrayList<>();
        for (Element image : images) {
            String src = "";
            src = image.attr("src");
            if(src != null && !src.equals("")) {
                photos.add(modifySrc(src));
            }
        }
        return photos;
    }
}

