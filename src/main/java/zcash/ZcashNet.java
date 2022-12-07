package zcash;

import blockChain.BlockChainNet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.Object;

import static java.lang.Thread.sleep;

public class ZcashNet extends BlockChainNet {
    String servicePort = "8232";
//    ZcashJdbc _jdbc;
    public ZcashNet(){

    }
    public String _opidTotxid(String ip, String opid, String id) {
        String txid = null;
        try{
            _con(ip,servicePort);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            List<String> list=new ArrayList<>();
            list.add(opid);
            paramArray.add(list);
            JSONObject response = _sendRequest("z_getoperationstatus", paramArray, id);
            JSONObject jsonData = JSON.parseObject(String.valueOf(response.getJSONArray("data").getJSONObject(0)));
            txid = (String) JSON.parseObject(String.valueOf(jsonData.get("result"))).get("txid");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return txid;
    }

    /** 发送模块 **/
    /**
     * 使用zcash发起交易,发送信息
     * @param ip:IP地址
     * @param senderAddress:发送方地址
     * @param receiverAddress：接收方地址
     * @param amount：金额数量
     * @param message：信息
     * @param id：请求的id_number
     * @return HashMap
     */
    public HashMap<String, Object> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String id) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip,servicePort);
            JSONObject jsonData = new JSONObject();
            /*判断金额数量*/
            HashMap resultMoney = getBalance(ip,senderAddress,id);
            if(resultMoney.get("status")=="ok"){
                JSONObject amountAddressJson = (JSONObject) resultMoney.get("data");
                BigDecimal amount_address = (BigDecimal)amountAddressJson.get("balance");
//                System.out.println(amount_address);
                /*判断金额数量*/
                // 金额数量符合
                if(amount_address.floatValue() >=  0.00101){
                    /*建立输入数据格式*/
                    JSONObject params = new JSONObject();
                    params.put("address", receiverAddress);
                    params.put("amount", amount);
                    String messageHex = stringToHexString(message);
                    params.put("memo", messageHex);

                    JSONArray paramArray1 = new JSONArray();
                    paramArray1.add(params);
                    JSONArray paramArray = new JSONArray();
                    paramArray.add(senderAddress);
                    paramArray.add(paramArray1);
                    /*建立输入数据格式*/

                    // 获取opid
                    _con(ip,servicePort);
                    JSONObject opid = _sendRequest("z_sendmany",paramArray,id);

                    /*查看操作id的状态*/
                    _con(ip,servicePort);
                    List<String> opidList=new ArrayList<>();
                    opidList.add((String) opid.get("data"));

                    JSONArray paramOpid = new JSONArray();
                    paramOpid.add(opidList);
                    JSONObject response = _sendRequest("z_getoperationstatus",paramOpid,id);
                    String status = (String)response.getJSONArray("data").getJSONObject(0).get("status");

                    //执行状态
                    while(status.equals("executing")){
                        _con(ip,servicePort);
                        response = _sendRequest("z_getoperationstatus",paramOpid,id);
                        status = (String)response.getJSONArray("data").getJSONObject(0).get("status");
                        if(status.equals( "success")){
                            mapResult.put("status","ok");
                            jsonData.put("opid",opid.get("data"));
                            String _txid =_opidTotxid(ip, opid.getString("data"), id);

                            Object[] obj = {_txid, senderAddress, receiverAddress};
                            ZcashJdbc.insert(obj);
                            mapResult.put("data",jsonData);
                        }else if(status.equals("failed")){
                            mapResult.put("status","error");
                            jsonData.put("err_code",response.getJSONArray("data").getJSONObject(0).getJSONObject("error").get("code"));
                            jsonData.put("err_msg",response.getJSONArray("data").getJSONObject(0).getJSONObject("error").get("message"));
                            mapResult.put("data",jsonData);
                        }
                        else if(status.equals("executing")){
                            sleep(1);
                        }
                    }

                    // 成功状态
                    if(status.equals( "success")) {
                        mapResult.put("status","ok");
                        jsonData.put("opid",opid.get("data"));
                        mapResult.put("data",jsonData);
                    }
                    else{
                        mapResult.put("status","error");
                        jsonData.put("err_code",response.getJSONArray("data").getJSONObject(0).getJSONObject("error").get("code"));
                        jsonData.put("err_msg",response.getJSONArray("data").getJSONObject(0).getJSONObject("error").get("message"));
                        mapResult.put("data",jsonData);
                    }
                }else{
                    mapResult.put("status","error");
                    jsonData.put("err_msg","金额数量不足");
                    mapResult.put("data",jsonData);
                }
            }else{
                mapResult = resultMoney;
            }
        } catch (IOException | InterruptedException e) {
            mapResult.put("status","error");
            //TODO:有些错误没有message
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
//        finally {
//            this.con.disconnect();
//        }
        return mapResult;
    }

    /**
     * 获取对应地址的金额
     * @param ip：服务器地址
     * @param address：zcash地址
     * @param id：用户标识符
     * @return 金额
     */
    public HashMap<String, Object> getBalance(String ip,String address,String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip,servicePort);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest("z_getbalance",paramArray,id);
            JSONObject jsonData = new JSONObject();
            jsonData.put("balance",response.get("data"));
            mapResult.put("status","ok");
            mapResult.put("data",jsonData);

        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        return mapResult;
    }
    /** 发送模块 */


    /** 接受模块 **/
    /**
     * 用于查询对应地址的信息
     * @param ip:服务器ip
     * @param address:查询接收消息的地址
     * @return:查询结果
     */
//    public HashMap<String, java.lang.Object> checkMessage(String ip, String address) {
//        return checkMessage(ip, address, 0.0);
//    }
//
//    public HashMap<String, java.lang.Object> checkMessage(String ip, String address, Double amount) {
//        HashMap mapResult = new HashMap<String, java.lang.Object>();
//        try{
//            _con(ip);
//            /*建立输入数据格式*/
//            JSONArray paramArray = new JSONArray();
//            paramArray.add(address);
//            paramArray.add(amount);
//            /*建立输入数据格式*/
//
//            JSONObject response = _sendRequest("z_listreceivedbyaddress",paramArray);
//
//            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(response.get("data")) );
//
//            for (int i=0; i < jsonArray.size(); i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                jsonObject.put("memo",hex_decode(String.valueOf(jsonObject.get("memo"))));
//                String time = String.valueOf (((JSONObject) new GetTransactionDetails().getTransaction(ip, String.valueOf(jsonObject.get("txid"))).get("data")).get("time"));
//                jsonObject.put("time",unixtimeToData(time));
//            }
//            mapResult.put("status","ok");
//            mapResult.put("data",jsonArray);
//        } catch (IOException e) {
//            mapResult.put("status","error");
//            JSONObject jsonData = JSON.parseObject(e.getMessage());
//            mapResult.put("data",jsonData);
//            e.printStackTrace();
//        }
//        finally {
//            this.con.disconnect();
//        }
//        return mapResult;
//    }
    /** 接受模块 **/

    /**  发送历史记录 **/
    public HashMap<String, Object> getSendHistory(String ip, String address, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip,servicePort);
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

            _con(ip,servicePort);

            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest("z_getoperationstatus", paramArray1, id);
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
        return mapResult;
    }

    /**
     * 利用数据库返回发送历史数据
     * @param ip 服务器地址
     * @param address 发送方地址
     * @param id 用户标识符
     * @return 该地址下发送历史
     */
    public HashMap<String, Object> getSendHistoryByTxid(String ip, String address, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            ZcashJdbc jdbc = new ZcashJdbc();
            List<List<String>> txidList = jdbc.checkByAdd1(address);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < txidList.size(); i++){
                List<String> txidWithAddress = txidList.get(i);

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
                List<String> memo_address = getReplyTo(memo);
                jsonResult.put("memo",memo_address.get(0));

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
    /**  发送历史记录  **/

    /**  地址管理   **/
    /**
     * 生成地址
     * @param ip：服务器Ip
     * @param id：用户标识符
     * @param accountNumber：用户对应账户
     * @return 该用户所有地址
     */
    public HashMap<String, Object> generateAddress(String ip, String id, Integer accountNumber){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip,servicePort);

            if(accountNumber == null) {
                /*创建新帐户*/
                JSONArray paramArray = new JSONArray();
                JSONObject response = _sendRequest("z_getnewaccount",paramArray,id);
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

                _con(ip,servicePort);
                JSONObject response_address = _sendRequest("z_getaddressforaccount",paramArray_address,id);
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
                JSONObject getAddressResponse = _sendRequest("z_getaddressforaccount", paramArray_address,id);
                JSONObject jsonData = new JSONObject();
                String address = (String) getAddressResponse.getJSONObject("data").get("address");
                jsonData.put("address", address);
                mapResult.put("status", "ok");
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
    public HashMap<String, Object> generateAddress(String ip, String id){
        return null;
    }

    /**
     * 获取该服务器的所有地址
     * @param ip：服务器Ip
     * @param id：用户标识符
     * @return 地址列表
     */
    public HashMap<String,Object> getAllAddress(String ip,String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip,servicePort);
            //建立发送数据格式
            JSONArray paramArray = new JSONArray();
            //存储的数据
            JSONArray jsonData = new JSONArray();
            // 获取反回数据
            JSONObject getAllAddressResponse = _sendRequest("listaddresses",paramArray,id);
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
    /**  产生新地址  **/


    /**  接收历史记录 **/
    public HashMap<String, Object> getReceiveHistory(String ip, String address, String id){
        return getReceiveHistory(ip, address, 1,id);
    }
    public HashMap<String, Object> getReceiveHistory(String ip, String address, Integer minconf, String  id){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip,servicePort);
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
                String time = unixtimeToData(String.valueOf(jsonObject.get("blocktime")));
                String memo_address = hex_decode(String.valueOf(jsonObject.get("memo")));
                String txid = jsonObject.getString("txid");
                List<String> memoWithAddress = getReplyTo(memo_address);
                JSONObject _jsonObject = new JSONObject();
                _jsonObject.put("txid",txid);
                _jsonObject.put("memo",memoWithAddress.get(0));
                _jsonObject.put("sendAddress",memoWithAddress.get(1));
                _jsonObject.put("time",time);
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
        return mapResult;
    }
    /**  接收历史记录 **/

    /** 接受详情**/
    public HashMap<String, Object> getReceiveDetail(String ip, String txid, String id) {
        HashMap mapResult = new HashMap<java.lang.String, java.lang.Object>();
        try{
            _con(ip,servicePort);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(txid);
            JSONObject response1 = _sendRequest("z_viewtransaction", paramArray1,id);
            JSONObject jsonData = response1.getJSONObject("data");

            JSONObject jsonResult = new JSONObject();
            jsonResult.put("txid",jsonData.get("txid"));
            jsonResult.put("sendAddress",((JSONObject)jsonData.getJSONArray("spends").get(0)).get("address"));
            jsonResult.put("receiveAddress",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("address"));
            jsonResult.put("amount",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("value"));
            String memo = (String) ((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("memoStr");
            jsonResult.put("memo",memo);


            mapResult.put("status","ok");
            mapResult.put("data",jsonResult);
        }catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
//        finally {
//            this.con.disconnect();
//        }
        return mapResult;
    }
    /** 接受详情**/


    /** 交易记录  **/
    public HashMap<String, Object> getSendDetail(String ip, String opid, String id) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            List<String> list = new ArrayList<>();
            list.add(opid);
            _con(ip,servicePort);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest("z_getoperationstatus", paramArray1,id);
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
        return mapResult;

    }
    /** 交易记录  **/
}
