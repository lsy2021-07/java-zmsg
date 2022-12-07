package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import zcashTest.ZcashJdbcTest;

import java.io.IOException;
import java.util.*;

public class SendHistory extends ZcashProxy {
//    public SendHistory(){
//        super();
//    }

    String servicePort = "8232";

    public HashMap<String, Object> GetSendHistory(String ip, String address){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"z_listoperationids", paramArray);
            JSONArray jsonArray = response.getJSONArray("data");
            List<String> list = new ArrayList<>();
            for (int i=0; i < jsonArray.size(); i++) {
                String opid = (String) jsonArray.get(i);
                list.add(opid);
            }

            _con(ip);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest(ip,"z_getoperationstatus", paramArray1);
            JSONArray jsonArray1 = response1.getJSONArray("data");

            List<JSONObject> resJsonList = new ArrayList<JSONObject>();

            for (int i=0; i < jsonArray1.size(); i++) {
//                System.out.println(jsonArray.get(i));
                JSONObject _jsonObject = jsonArray1.getJSONObject(i);

                String time = unixtimeToData(_jsonObject.getString("creation_time"));
                String opid = _jsonObject.getString("id");
                _jsonObject = _jsonObject.getJSONObject("params");
                String _fromaddress = _jsonObject.getString("fromaddress");
                if (address.equals(_fromaddress)){
                    _jsonObject = _jsonObject.getJSONArray("amounts").getJSONObject(0);
                    String _address = _jsonObject.getString("address");
                    String memo = hex_decode(_jsonObject.getString("memo"));
                    JSONObject resJsonObject = new JSONObject();
                    resJsonObject.put("senderAddress",_fromaddress);
                    resJsonObject.put("receiverAddress",_address);
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

    public HashMap<String, Object> getSendHistoryByTxid(String ip, String address, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            ZcashJdbc jdbc = new ZcashJdbc();
            List<List<String>> txidList = jdbc.checkByAdd1(address);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < txidList.size(); i++){
                List<String> txidWithAddress = txidList.get(i);
                System.out.println(txidWithAddress);

                _con(ip,servicePort);
                JSONArray paramArray1 = new JSONArray();
                paramArray1.add(txidWithAddress.get(0));
                JSONObject response1 = _sendRequest("z_viewtransaction", paramArray1,id);
                JSONObject jsonData = response1.getJSONObject("data");

                JSONObject jsonResult = new JSONObject();
                jsonResult.put("txid",txidWithAddress.get(0));
                jsonResult.put("sendAddress",txidWithAddress.get(1));
                jsonResult.put("receiveAddress",txidWithAddress.get(2));
                jsonResult.put("amount",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("value"));
                String memo = (String) ((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("memoStr");
                jsonResult.put("memo",memo);

                jsonArray.add(jsonResult);
            }
            mapResult.put("status","ok");
            mapResult.put("data",jsonArray);
        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        return mapResult;
    }

}
