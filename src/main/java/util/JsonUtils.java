package util;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaowei on 2017/6/5.
 */
public class JsonUtils {
    private static String equalsStr = "";
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    static {
        PropertyConfigurator.configure("log4j.properties");
    }

    private static void equals(JSONArray array1, JSONArray array2) throws JSONException {
        if(array1.toString().equals(array2.toString())){
            equalsStr+=true;
            return;
        }
        if(array1.length()!=array2.length()){
            equalsStr+=false;
            logger.info("[false,array1.length={},array2.length={}]",array1.length(),array2.length());
            logger.debug("array1={},array2={}",array1.toString(),array2.toString());
            return;
        }
        List<String> list1 = toList(array1);
        List<String> list2 = toList(array2);
        for(int i=0;i<array1.length();i++){
            String str1 = list1.get(i);
            String str2 = list2.get(i);
            if(str1.startsWith("[")){
                if(str2.startsWith("[")){
                    equals(new JSONArray(str1),new JSONArray(str2));
                }else{
                    equalsStr+=false;
                    logger.info("[false,第{}个元素,str1={},str2={}]",i,str1,str2);
                    logger.debug("array1={},array2={}",array1.toString(),array2.toString());
                }
            }else if(str1.startsWith("{")){
                if(str2.startsWith("{")){
                    equals(new JSONObject(str1),new JSONObject(str2));
                }else{
                    equalsStr+=false;
                    logger.info("[false,第{}个元素,str1={},str2={}]",i,str1,str2);
                    logger.debug("array1={},array2={}",array1.toString(),array2.toString());
                }

            }else{
                boolean b = str1.equals(str2);
                equalsStr+=b;
                if(!b) {
                    logger.info("[{},第{}个元素,str1={},str2={}]", b, i, str1, str2);
                    logger.debug("array1={},array2={}", array1.toString(), array2.toString());
                }
            }
        }

    }
    private static List<String> toList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<String>();
        for(int i=0;i<array.length();i++){
            list.add(array.getString(i));
        }
        Collections.sort(list);
        return list;
    }
    private static void equals(JSONObject json1, JSONObject json2) throws JSONException {
        if (json1.length() != json2.length()) {
            equalsStr+=false;
            logger.info("[false,json1.length={},json2.length={}]",json1.length(),json2.length());
            logger.debug("json1={},json2={}",json1.toString(),json2.toString());
            return;
        }
        String str1 = json1.toString();
        String str2 = json2.toString();
        if (str1.equals(str2)) {
            equalsStr+=true;
            return;
        }

        Iterator<String> iterator = json1.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (json2.has(key)) {
                String value1 = json1.getString(key);
                String value2 = json2.getString(key);
                if (value1.startsWith("[")) {
                    if (value2.startsWith("[")) {
                        JSONArray array1 = json1.getJSONArray(key);
                        JSONArray array2 = json1.getJSONArray(key);
                        equals(array1,array2);
                    } else {
                        equalsStr+=false;
                        logger.info("[false,key={},value1={},value2={}]",key,value1,value2);
                        logger.debug("json1={},json2={}",json1.toString(),json2.toString());
                    }

                }else if(value1.startsWith("{")){
                    if(value2.startsWith("{")){
                        equals(json1.getJSONObject(key),json2.getJSONObject(key));
                    }else{
                        equalsStr+=false;
                        logger.info("[false,key={},value1={},value2={}]",key,value1,value2);
                        logger.debug("json1={},json2={}",json1.toString(),json2.toString());
                    }
                }else{
                    boolean b = value1.equals(value2);
                    equalsStr+=b;
                    if(!b) {
                        logger.info("[{},key={},value1={},value2={}]",b, key, value1, value2);
                        logger.debug("json1={},json2={}", json1.toString(), json2.toString());
                    }
                }
            } else {
                equalsStr+=false;
                logger.info("[false,json2中没有key={}]",key);
                logger.debug("json1={},json2={}",json1.toString(),json2.toString());
            }
        }
    }
    private static void init(){
        equalsStr="";
    }
    private static boolean getJsonEqualsResult(){
        logger.info("equalsStr="+equalsStr);
        if(equalsStr.equals("")){
            equalsStr="";
            return false;
        }else{
            int index = equalsStr.indexOf("false");
            equalsStr="";
            return index==-1;
        }
    }
    public static boolean equalsJson(JSONObject json1,JSONObject json2) throws JSONException {
        init();
        equals(json1,json2);
        return getJsonEqualsResult();
    }
    public static boolean equalsJsonArray(JSONArray json1,JSONArray json2) throws JSONException {
        init();
        equals(json1,json2);
        return getJsonEqualsResult();
    }
    public static void delKeyByValueIsNull(JSONArray array) throws JSONException {
        for(int i=0;i<array.length();i++){
            String str = array.getString(i);
            if(str.startsWith("[")){
                delKeyByValueIsNull(array.getJSONArray(i));
            }else if(str.startsWith("{")){
                delKeyByValueIsNull(array.getJSONObject(i));
            }
        }
    }
    public static void delKeyByValueIsNull(JSONObject json) throws JSONException {
        Iterator<String> it = json.keys();
        while(it.hasNext()){
            String key = it.next();
            String value = json.getString(key);
            if (value.startsWith("[")) {
                delKeyByValueIsNull(json.getJSONArray(key));
            }else if(value.startsWith("{")){
                delKeyByValueIsNull(json.getJSONObject(key));
            }else{
                Object valueObj = json.get(key);
                if(valueObj.equals(null)){
                    it.remove();
                }
            }
        }
    }
    public static void main(String[] args) throws JSONException {
        JSONArray j1 = new JSONArray("[[1,2,3],[4,3],[6]]");
        JSONArray j2 = new JSONArray("[[1,2,3],[3,4],[6]]");
        init();
        equals(j1,j2);
        System.out.println(getJsonEqualsResult());
    }
}
