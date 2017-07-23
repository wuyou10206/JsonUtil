# JsonUtil
Json的工具类，主要提供了如下功能：
<br/>
1、比较两个json字符串是否相等,此方法不考虑key的顺序。
  方法：boolean equals(String json1,String json2)
  例如：json1={"A1":"aa,bb","B1":[1,2,3]} json2={"B1":[3,2,1],"A1":"aa,bb"}经过该方法比较认为是相等的。
<hr/>
2、去除Json中的null，该方法会将json中值为null的key去除。
  方法：String delKeyByValueIsNull(String json)
  例如：json={"A":"123","B":{"B01":null,"B02","456"},"C":null}  经过处理后为： {"A":"123","B":{"B02","456"}}
<hr/>
3、对json字符串进行排序，根据key进行字典排序。
  方法：String sort(String json)
  例如：json={"C":"123","B":["2","1","3"],"D":"qwe"} 经过处理后为：  {"B":["1","2","3"],"C":"123","D","qwe"}
<hr/>
4、比较两个json字符串的key是否相等,此方法key的值。
  方法：boolean equalsByKey(String json1,String json2)
  例如：json1={"A1":"ddd,bb","B1":[1,2,3]} json2={"B1":[3,2,1],"A1":"aa,bb"}经过该方法比较认为是相等的。

