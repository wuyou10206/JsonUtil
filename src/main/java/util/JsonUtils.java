package util;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by zhaowei on 2017/6/5.
 */
public class JsonUtils {
    private static String equalsStr = "";
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    static {
        PropertyConfigurator.configure("log4j.properties");
    }

    /**
     * 判断两个JSONArray是否相等,默认对两个JSONArray按照字符串进行字典排序,该方法会递归调用
     * @param array1 JSONArray
     * @param array2 JSONArray
     * @throws JSONException
     */
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
        array1 = sort(array1);
        array2 = sort(array2);
        for(int i=0;i<array1.length();i++){
            String str1 = array1.getString(i);
            String str2 = array2.getString(i);
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

    /**
     * 将JSONArray转换为list集合，并按照字符串字典进行排序。
     * @param array 一个JSONArray
     * @return 按照字典排序后的ArrayList
     * @throws JSONException
     */
    private static List<String> toList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<String>();
        for(int i=0;i<array.length();i++){
            String str = array.getString(i);
            if(str.startsWith("[")){
                list.add(array.getJSONArray(i).toString());
            }else if(str.startsWith("{")){
                list.add(array.getJSONObject(i).toString());
            }else{
                list.add(array.getString(i));
            }
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 判断两个JSONObject是否相等,该方法会递归调用
     * @param json1 JSONObject
     * @param json2 JSONObject
     * @throws JSONException
     */
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
                        JSONArray array2 = json2.getJSONArray(key);
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

    /**
     * 初始化equalsStr的值
     */
    private static void init(){
        equalsStr="";
    }

    /**
     * 得到比较结果，true为相等，false为不相等
     * @return
     */
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

    /**
     * 对外提供的判断两个JSONObject是否想等的方法,如果json中包含数组,数组则按字典排序进行排序比较
     * @param json1 第一个JSONObject
     * @param json2 第一个JSONObject
     * @return 返回true则为相等，false为不相等
     * @throws JSONException
     */
    public static boolean equalsJson(JSONObject json1,JSONObject json2) throws JSONException {
        init();
        logger.info("需要比较的两个JSONObject为：json1={},json2={}",json1.toString(),json2.toString());
        equals(json1,json2);
        return getJsonEqualsResult();
    }

    /**
     * 对外提供的判断两个JSONArray是否想等的方法,数组则按字典排序进行排序比较
     * @param json1 第一个JSONArray
     * @param json2 第一个JSONArray
     * @return 返回true则为相等，false为不相等
     * @throws JSONException
     */
    public static boolean equalsJsonArray(JSONArray json1,JSONArray json2) throws JSONException {
        init();
        logger.info("需要比较的两个JSONArray为：array1={},array2={}",json1.toString(),json2.toString());
        equals(json1,json2);
        return getJsonEqualsResult();
    }

    /**
     * 去除JSONArray中key为null的key
     * @param array JSONArray
     * @throws JSONException
     */
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

    /**
     * 去除JSONObject中key为null的key
     * @param json JSONObject
     * @throws JSONException
     */
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

    /**
     * 对JSONArray进行字典排序,对包含JSONObject则对key进行字典排序，递归调用
     * @param json JSONArray
     * @return 排序后的JSONArray
     * @throws JSONException
     */
    private static JSONArray sort(JSONArray json) throws JSONException {
        JSONArray sortJson = new JSONArray();
        List<Object> list = new ArrayList<Object>();
        for(int i=0;i<json.length();i++){
            String str = json.getString(i);
            if(str.startsWith("[")){
                JSONArray array = sort(json.getJSONArray(i));
                list.add(array);
            }else if(str.startsWith("{")){
                JSONObject jsonObject = sort(json.getJSONObject(i));
                list.add(jsonObject);
            }else{
                list.add(json.getString(i));
            }
        }
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for(Object obj:list){
            if(obj instanceof JSONArray){
                sortJson.put((JSONArray)obj);
            }else if(obj instanceof JSONObject){
                sortJson.put((JSONObject)obj);
            }else{
                sortJson.put(obj.toString());
            }
        }
        System.out.println(sortJson.toString());
        return sortJson;
    }

    /**
     * 对JSONObject中的key进行字典排序，递归调用
     * @param json JSONObject
     * @return 排序后的JSONObject
     * @throws JSONException
     */
    private static JSONObject sort(JSONObject json) throws JSONException {
        JSONObject sortJson = new JSONObject(new LinkedHashMap());
        Iterator<String> it = json.keys();
        List<String> keys = new ArrayList<String>();
        while(it.hasNext()){
            String key = it.next();
            keys.add(key);
        }
        Collections.sort(keys);
        for(String key:keys){
            String value = json.getString(key);
            if(value.startsWith("[")){
                JSONArray array = sort(json.getJSONArray(key));
                sortJson.put(key,array);
            }else if(value.startsWith("{")){
                JSONObject jsonObject = sort(json.getJSONObject(key));
                sortJson.put(key,jsonObject);
            }else{
                sortJson.put(key,json.getString(key));
            }

        }
        return sortJson;
    }

    /**
     * 对外提供的对json进行排序的方法
     * @param json 符合json规范的字符串
     * @return 排序后的json字符串
     * @throws JSONException
     */
    public static String sort(String json) throws JSONException {
        String str = null;
        if(json.startsWith("{")){
            str = sort(new JSONObject(json)).toString();
        }else if(json.startsWith("[")){
            str = sort(new JSONArray(json)).toString();
        }else{
            logger.info("传入的json格式错误");
        }
        return str;
    }

    public static void main(String[] args) throws JSONException {
        JSONArray j1 = new JSONArray("[[1,2,3],[4,3],[6]]");
        JSONArray j2 = new JSONArray("[[1,2,3],[3,4],[6]]");
        init();
        equals(j1,j2);
        System.out.println(getJsonEqualsResult());
    }
}
