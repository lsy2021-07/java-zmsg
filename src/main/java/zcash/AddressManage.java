package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.*;

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

                List<String> addressType = new ArrayList<String>();
                // 设置成sapling地址
                addressType.add("sapling");
                paramArray_address.add(addressType);

                _con(ip);
                JSONObject response_address = _sendRequest(ip, "z_getaddressforaccount",paramArray_address,id);
                /*创建账户成功*/

                JSONObject jsonData = new JSONObject();
                String address = (String) response_address.getJSONObject("data").get("address");
                jsonData.put("address", address);
                jsonData.put("account",account);
                mapResult.put("status", "ok");
                mapResult.put("data", jsonData);

            }else {
                /*建立在新建账户下获取新地址输入数据格式*/
                JSONArray paramArray_address = new JSONArray();
                paramArray_address.add(accountNumber);

                List<String> addressType = new ArrayList<String>();
                addressType.add("sapling");
                paramArray_address.add(addressType);

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

    /**
     * 查看对应ip的全部地址
     * @param ip：ip地址
     * @return mapResult
     */
    public HashMap<String,Object> getAllAddress(String ip,String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip);
            //建立发送数据格式
            JSONArray paramArray = new JSONArray();
            //存储的数据
            JSONArray jsonData = new JSONArray();
            // 获取反回数据
            JSONObject getAllAddressResponse = _sendRequest(ip,"listaddresses",paramArray,id);
            JSONArray allAddressArray = (JSONArray) getAllAddressResponse.get("data");

            /* 返回数据格式优化*/
            for(int i=0;i < allAddressArray.size();i++){
                JSONObject allAddressData = allAddressArray.getJSONObject(i);
                //存放不同的source的地址
                JSONObject sourceData = new JSONObject();
                //存放addressesList
                JSONArray jsonArray = new JSONArray();
                sourceData.put("source",allAddressData.get("source"));
                //存储键对
                Set<String> keys = allAddressData.keySet();
                Iterator<String> it = keys.iterator();
                while(it.hasNext()) {
                    String key = String.valueOf(it.next());
                    if(key.equals("source")==false){
                        // 判断是否是透明地址
                        if(key.equals("transparent")==false){
                            JSONArray sameAddressType = (JSONArray)allAddressData.get(key);
                            for (int j =0;j<sameAddressType.size();j++){
                                JSONArray addresslist = (JSONArray) sameAddressType.getJSONObject(j).get("addresses");
                                for (int k=0;k<addresslist.size();k++){
                                    // 判断是否是统一地址
                                    if(key.equals("unified")){
                                        JSONObject addressJson = new JSONObject();
                                        /* 获取地址 */
                                        String address = (String) ((JSONObject)addresslist.get(k)).get("address");
                                        addressJson.put("address",address);
                                        /* 获取地址 */

                                        /* 获取账号 */
                                        Integer account = (Integer) sameAddressType.getJSONObject(j).get("account");
                                        addressJson.put("account",account);
                                        /* 获取账号 */

//                                        /* 获取地址金额 */
//                                        _con(ip);
//                                        JSONArray addressNum = new JSONArray();
//                                        addressNum.add(address);
//                                        JSONObject balance = _sendRequest(ip,"z_getbalance",addressNum,id);
//                                        addressJson.put("amount",balance.get("data"));
//                                        /* 获取地址金额 */

                                        jsonArray.add(addressJson);
                                    }else {
                                        String address = (String) addresslist.get(k);
                                        JSONObject addressJson = new JSONObject();
                                        addressJson.put("address",address);

//                                        /* 获取地址金额 */
//                                        _con(ip);
//                                        JSONArray addressNum = new JSONArray();
//                                        addressNum.add(address);
//                                        JSONObject balance = _sendRequest(ip,"z_getbalance",addressNum,id);
//                                        addressJson.put("amount",balance.get("data"));
//                                        /* 获取地址金额 */
                                        jsonArray.add(addressJson);
                                    }

                                }

                            }
                        }else {
                            JSONObject sameAddressType = (JSONObject) allAddressData.get(key);
                            JSONArray addresslist = sameAddressType.getJSONArray("addresses");
                            for (int j =0;j<addresslist.size();j++){
                                String address = (String) addresslist.get(j);
                                JSONObject addressJson = new JSONObject();
                                addressJson.put("address",address);

//                                /* 获取地址金额 */
//                                _con(ip);
//                                JSONArray addressNum = new JSONArray();
//                                addressNum.add(address);
//                                JSONObject balance = _sendRequest(ip,"z_getbalance",addressNum,id);
//                                addressJson.put("amount",balance.get("data"));
//                                /* 获取地址金额 */
                                jsonArray.add(addressJson);
                            }

                        }

                    }

                }
                sourceData.put("addresses",jsonArray);
                jsonData.add(sourceData);
            }
            /* 返回数据格式优化*/

            mapResult.put("status", "ok");
            mapResult.put("data", jsonData);


        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }


        return mapResult;
    }
}
