package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class GetTransaction extends ZcashProxy{
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
}
