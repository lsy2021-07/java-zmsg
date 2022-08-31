package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class AddressManage extends ZcashProxy {
    public AddressManage(){
        super();
    }

    /**
     * 如果用户有对应账户则生成新地址，否则创建新的账户后生成新地址
     * @param ip：地址
     * @param id：用户id
     * @param accountNumber：账户号码
     * @return 生成的地址
     */
    public HashMap<String, Object> generateAddress(String ip, String id, Integer accountNumber){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip);

            if(accountNumber == null) {
                /*创建新帐户*/
                JSONArray paramArray = new JSONArray();
                JSONObject response = _sendRequest(ip, "z_getnewaccount",paramArray,id);
                /*创建新帐户*/

                /*创建账户成功*/
                int account = (int) response.getJSONObject("data").get("account");
                /*建立在新建账户下获取新地址输入数据格式*/
                JSONArray paramArray_address = new JSONArray();
                paramArray_address.add(account);
                this.con.disconnect();

                _con(ip);
                JSONObject response_address = _sendRequest(ip, "z_getaddressforaccount",paramArray_address,id);
                /*创建账户成功*/

                JSONObject jsonData = new JSONObject();
                String address = (String) response_address.getJSONObject("data").get("address");
                jsonData.put("address", address);
                jsonData.put("account",account);
                mapResult.put("status", "success");
                mapResult.put("data", jsonData);

            }else {
                /*建立在新建账户下获取新地址输入数据格式*/
                JSONArray paramArray_address = new JSONArray();
                paramArray_address.add(accountNumber);
                /*建立在新建账户下获取新地址输入数据格式*/

                // 获取反馈数据
                JSONObject getAddressResponse = _sendRequest(ip,"z_getaddressforaccount", paramArray_address,id);
                JSONObject jsonData = new JSONObject();
                String address = (String) getAddressResponse.getJSONObject("data").get("address");
                jsonData.put("address", address);
                mapResult.put("status", "success");
                mapResult.put("data", jsonData);
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
