package horizen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Receive extends HorizenProxy{

    public static String hex_decode(String s_hex) {
        int index = 0;
        for (int i = s_hex.length() - 1; i >= 0; i--) {
            if (s_hex.charAt(i)!='0'){
                index = i;
                break;
            }
        }
        s_hex = s_hex.substring(0, index + 1);
        if (s_hex.equals("f6") || s_hex.length()%2 != 0){
            return "";
        }
        StringBuilder s_acsii = new StringBuilder();
        for (int i = 0; i < s_hex.length(); i+=2){
            String str = s_hex.substring(i, i+2);
            s_acsii.append((char)Integer.parseInt(str,16));
        }
        return s_acsii.toString();
    }

    public String unixtimeToData(String time){
        time += "000";   // python time stamp to java time stamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  simpleDateFormat.format(new Date(new Long(time)));
    }

    /**
     * 获取对应地址接受历史
     * @param ip:ip
     * @param address:地址
     * @param amount：金额
     * @return
     */
    /**  接收历史记录 **/
    public HashMap<String, Object> getReceiveHistory(String ip, String address, String id){
        return getReceiveHistory(ip, address, 1,id);
    }
    public HashMap<String, Object> getReceiveHistory(String ip, String address, Integer minconf, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            paramArray.add(minconf);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest("z_listreceivedbyaddress",paramArray,id);

            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(response.get("data")) );
            List<JSONObject> resJsonList = new ArrayList<JSONObject>();
            for (int i=0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String time = String.valueOf (((JSONObject) new GetTransactionDetails().getTransaction(ip, String.valueOf(jsonObject.get("txid")), id).get("data")).get("time"));
                String memo_address = hex_decode(String.valueOf(jsonObject.get("memo")));
                String txid = jsonObject.getString("txid");
                String amount = jsonObject.getString("amount");
                int index = memo_address.lastIndexOf("reply to:");
                String memo="",sendAddress="";

                if (index==-1){
                    memo = memo_address;
                }
                else{
                    memo = memo_address.substring(0,index);
                    sendAddress = memo_address.substring(index+9);
                }
                JSONObject _jsonObject = new JSONObject();
                _jsonObject.put("txid",txid);
                _jsonObject.put("memo",memo);
                _jsonObject.put("amount",amount);
                _jsonObject.put("sendAddress",sendAddress);
                _jsonObject.put("time",unixtimeToData(time));
                resJsonList.add(_jsonObject);
            }
            Collections.sort(resJsonList, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String x = (String) a.get("time");
                    String y = (String) b.get("time");
                    return -x.compareTo(y);
                }
            });
            JSONArray resJsonArray = JSONArray.parseArray(resJsonList.toString());
            mapResult.put("status","ok");
            mapResult.put("data",resJsonArray);
        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        finally {
            this.con.disconnect();
        }
        return mapResult;
    }


    public HashMap<String, Object> Op_getReceiveHistory(String ip, String address, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            /*建立输入数据格式*/

            JSONObject response = _sendRequest("z_listoperationids", paramArray, id);
            JSONArray jsonArray = response.getJSONArray("data");
            List<String> list = new ArrayList<>();
            for (int i=0; i < jsonArray.size(); i++) {
                String opid = (String) jsonArray.get(i);
                list.add(opid);
            }

            _con(ip);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest("z_getoperationstatus", paramArray1, id);
            JSONArray jsonArray1 = response1.getJSONArray("data");

            List<JSONObject> resJsonList = new ArrayList<JSONObject>();

            for (int i=0; i < jsonArray1.size(); i++) {
                JSONObject _jsonObject = jsonArray1.getJSONObject(i);

                String time = unixtimeToData(_jsonObject.getString("creation_time"));
                String opid = _jsonObject.getString("id");
                _jsonObject = _jsonObject.getJSONObject("params");
                String receiver = _jsonObject.getJSONArray("amounts").getJSONObject(0).getString("address");
                if (address.equals(receiver)){
                    String sender = _jsonObject.getString("fromaddress");
                    _jsonObject = _jsonObject.getJSONArray("amounts").getJSONObject(0);
                    String memo = hex_decode(_jsonObject.getString("memo"));
                    JSONObject resJsonObject = new JSONObject();
                    resJsonObject.put("senderAddress",sender);
                    resJsonObject.put("receiverAddress",receiver);
                    resJsonObject.put("memo",memo);
                    resJsonObject.put("time",time);
                    resJsonObject.put("opid",opid);

                    resJsonList.add(resJsonObject);
                }
            }
            Collections.sort(resJsonList, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String x = (String) a.get("time");
                    String y = (String) b.get("time");
                    return -x.compareTo(y);
                }
            });
            JSONArray resJsonArray = JSONArray.parseArray(resJsonList.toString());
            mapResult.put("status","ok");
            mapResult.put("data",resJsonArray);
        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        finally {
            this.con.disconnect();
        }
        return mapResult;
    }


}
