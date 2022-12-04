package horizen;

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

import static java.lang.Thread.sleep;

public class HorizenNet extends BlockChainNet {
    String servicePort = "8242";
    public HorizenNet(){

    }
    /** 发送消息模块 **/
    /**
     * 使用horizen发起交易,发送信息
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

                    /*获取opid*/
                    _con(ip,servicePort);
                    JSONObject opid = _sendRequest("z_sendmany",paramArray,id);
                    /*获取opid*/

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
        return mapResult;
    }

    /**
     * 获取对应地址的金额
     * @param ip：服务器地址
     * @param address：horizen地址
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

    /** 接受消息模块 **/

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

    @Override
    public HashMap<String, Object> getReceiveDetail(String ip, String txid, String id) {
        return null;
    }

    public HashMap<String, Object> getReceiveHistory(String ip, String address, Integer minconf, String id){
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
        return mapResult;
    }

    /** 地址管理模块 **/
    /**
     * 如果用户有对应账户则生成新地址，否则创建新的账户后生成新地址
     * @param ip：地址
     * @param id：用户id
     * @return 生成的地址
     */
    public HashMap<String, Object> generateAddress(String ip, String id){
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip,servicePort);

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
            _con(ip,servicePort);
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

    /** 发送历史模块 **/
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

    /** 获取交易详情模块 **/
    public HashMap<String, Object> getSendDetail(String ip, String opid, String id) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            List<String> list = new ArrayList<>();
            list.add(opid);
            _con(ip,servicePort);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(list);
            JSONObject response1 = _sendRequest("z_getoperationstatus", paramArray1, id);
            JSONArray jsonArray1 = response1.getJSONArray("data");

            List<JSONObject> resJsonList = new ArrayList<JSONObject>();
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

    public HashMap<String, Object> getTransactionDetailsWithTx(String ip, String address, String txid, String id) {
        HashMap mapResult = new HashMap<String,Object>();
        try {
            _con(ip,servicePort);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            HashMap receive_by_address = getReceiveHistory(ip,address,id);
            JSONObject resJsonObject = new JSONObject();
            JSONArray receiveArray = (JSONArray) receive_by_address.get("data");
            for (int i=0; i < receiveArray.size(); i++){
                JSONObject jsonObject = receiveArray.getJSONObject(i);
                String reciveTxid = jsonObject.getString("txid");
                if (reciveTxid.equals(txid)){
                    resJsonObject.put("memo",jsonObject.getString("memo"));
                    resJsonObject.put("txid",txid);
                    resJsonObject.put("amount",jsonObject.getString("amount"));
                    resJsonObject.put("time",jsonObject.getString("time"));
                    if(jsonObject.getString("sendAddress").equals("")){
                        resJsonObject.put("sendAddress","****************");
                    }else{
                        resJsonObject.put("sendAddress", jsonObject.getString("sendAddress"));
                    }
                }
            }
            mapResult.put("status","ok");
            mapResult.put("data",resJsonObject);
        }catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        return mapResult;

    }
}
