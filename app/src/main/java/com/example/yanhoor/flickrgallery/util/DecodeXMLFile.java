package com.example.yanhoor.flickrgallery.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by yanhoor on 2016/3/15.
 */
public class DecodeXMLFile {
    private static final String TAG="DecodeXMLFile";

    //解析xml文件获取full token即auth_token
    public String decodeFullTokenXml(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType=parser.next();
        String fullToken=null;

        while (eventType!= XmlPullParser.END_DOCUMENT){
            if (eventType==XmlPullParser.START_TAG&&"token".equals(parser.getName())){
                fullToken=parser.nextText();
            }
            eventType=parser.next();
        }

        Log.d(TAG,"Full token is "+fullToken);
        return fullToken;
    }
}
