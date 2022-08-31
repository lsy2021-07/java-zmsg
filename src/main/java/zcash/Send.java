package zcash;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class Send extends ZcashProxy {
    public Send(){
        super();
    }

    /**
     * 字符串转十六进制
     * @param s:字符串转十六进制
     * @return str:十六进制
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


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
            _con(ip);
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
                    _con(ip);
                    JSONObject opid = _sendRequest(ip,"z_sendmany",paramArray,id);

                    /*查看操作id的状态*/
                    _con(ip);
                    List<String> opidList=new ArrayList<>();
                    opidList.add((String) opid.get("data"));

                    JSONArray paramOpid = new JSONArray();
                    paramOpid.add(opidList);
                    JSONObject response = _sendRequest(ip,"z_getoperationstatus",paramOpid,id);
                    String status = (String)response.getJSONArray("data").getJSONObject(0).get("status");

                    //执行状态
                    while(status.equals("executing")){
                        _con(ip);
                        response = _sendRequest(ip,"z_getoperationstatus",paramOpid,id);
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
//                    jsonData.put("err_code",);
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
        finally {
            this.con.disconnect();
        }
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
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"z_getbalance",paramArray,id);
            JSONObject jsonData = new JSONObject();
            jsonData.put("balance",response.get("data"));
            mapResult.put("status","ok");
            mapResult.put("data",jsonData);

        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        } finally {
            this.con.disconnect();
        }
        return mapResult;
    }

}
