package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetTransactionDetails extends ZcashProxy{
    public HashMap<String, Object> getTransaction(String ip, String txid) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(txid);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"gettransaction",paramArray);

            JSONObject jsonData = JSON.parseObject(String.valueOf(response.get("data")));

            mapResult.put("status","ok");
            mapResult.put("data",jsonData);

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

    public HashMap<String, Object> getZtransaction(String ip, String txid) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(txid);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"z_viewtransaction",paramArray);

//            System.out.println(response);

            JSONObject jsonData = JSON.parseObject(String.valueOf(response.get("data")));

            mapResult.put("status","ok");
            mapResult.put("data",jsonData);

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


    public HashMap<String, Object> getTransactionDetails(String ip, String opid) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            List<String> list = new ArrayList<>();
            list.add(opid);
            _con(ip);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest(ip,"z_getoperationstatus", paramArray1);
            JSONArray jsonArray1 = response1.getJSONArray("data");

            List<JSONObject> resJsonList = new ArrayList<JSONObject>();
//                System.out.println(jsonArray.get(i));
            JSONObject _jsonObject = jsonArray1.getJSONObject(0);

            String txid = _jsonObject.getJSONObject("result").getString("txid");

            String time = unixtimeToData(_jsonObject.getString("creation_time"));
            String _opid = _jsonObject.getString("id");
            _jsonObject = _jsonObject.getJSONObject("params");
            String _fromaddress = _jsonObject.getString("fromaddress");

            int _minconf = _jsonObject.getInteger("minconf");

            _jsonObject = _jsonObject.getJSONArray("amounts").getJSONObject(0);
            String _address = _jsonObject.getString("address");
            String _amount = _jsonObject.getString("amount");
            String memo = hex_decode(_jsonObject.getString("memo"));
            JSONObject resJsonObject = new JSONObject();

            resJsonObject.put("senderAddress",_fromaddress);
            resJsonObject.put("receiverAddress",_address);
            resJsonObject.put("memo",memo);
            resJsonObject.put("time",time);
            resJsonObject.put("opid",opid);
            resJsonObject.put("txid",txid);
            resJsonObject.put("minconf",_minconf);
            resJsonObject.put("amount",_amount);

            mapResult.put("status","ok");
            mapResult.put("data",resJsonObject);
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
