# JsonUtil
Json的工具类，目前只提供比较两个JsonObject是否相等，比较两个JsonArray是否相等

该工程下目前只有一个工具类：JsonEqualsUtils。
  该工具提供了如下方法：
  
    1、equalsJson(JSONObject json1,JSONObject json2)
      返回值：boolean
      用途：比较两个JsonObject对象是否相等，该Json中可以包含多层Json以及Json数组，如果是字符串请先转成JsonObject进行比较。该方法在比较时不考虑key的顺序。
           例如：json1={"A1":"aa,bb","B1":[1,2,3]} json2={"B1":[3,2,1],"A1":"aa,bb"}经过该方法比较认为是相等的。
           
    2、equalsJsonArray(JSONArray json1,JSONArray json2)
      返回值：boolean
      用途：比较两个JsonArray对象是否相等，如果是字符串请先转成JsonArray进行比较。该方法在比较时元素的顺序。如下Json应当选择该方法比较。
           例如：json1=[[2,3],[5]] json2=[[5],[3,2]] 经过该方法比较认为是相等的。
           
    3、delKeyByValueIsNull(JSONObject json)
      返回值：null
      用途：去除Json中的null，该方法会将json中值为null的key去除。
           例如：json={"A":"123","B":{"B01":null,"B02","456"},"C":null}  
           经过处理后为： {"A":"123","B":{"B02","456"}}
