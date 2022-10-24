package horizen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.*;

public class AddressManage extends HorizenProxy {
    public AddressManage(){
        super();
    }

    /**
     * 如果用户有对应账户则生成新地址，否则创建新的账户后生成新地址
     * @param ip：地址
     * @param id：用户id
     * @return 生成的地址
     */
    public HashMap<String, Object> generateAddress(String ip, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip);

            /*创建新地址*/
            JSONArray paramArray = new JSONArray();
            JSONObject response = _sendRequest( "z_getnewaddress",paramArray,id);
            /*创建新地址*/
            JSONObject jsonData = new JSONObject();
            if(response.getJSONObject("error") == null){
                String address = (String) response.get("data");
                jsonData.put("address", address);
                mapResult.put("status", "ok");
                mapResult.put("data",jsonData);
            }else{
                mapResult.put("status","error");
                mapResult.put("data",response.getJSONObject("error"));
            }

        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        return mapResult;
    }

    /**
     * 查看对应ip的全部地址
     * @param ip：ip地址
     * @return mapResult
     */
    public HashMap<String,Object> getAllAddress(String ip,String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip);

            /*获取地址*/
            JSONArray paramArray = new JSONArray();
            JSONObject response = _sendRequest( "z_listaddresses",paramArray,id);
            /*获取地址*/

            JSONObject jsonData = new JSONObject();
            if(response.getJSONObject("error") == null){
                JSONArray addressList = response.getJSONArray("data");
                jsonData.put("addresses",addressList);
                mapResult.put("status", "ok");
                mapResult.put("data", jsonData);
            }else{
                mapResult.put("status","error");
                mapResult.put("data",response.getJSONObject("error"));
            }

        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        return mapResult;
    }
}
