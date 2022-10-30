package horizen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import horizen.GetTransactionDetails;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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

    public HashMap<String, Object> checkMessage(String ip, String address) {
        return checkMessage(ip, address, 0.0);
    }
    /**
     * check message about a specified address
     * @param ip：ip
     * @param address：地址
     * @param amount: 金额数量
     * @return
     */
    public HashMap<String, Object> checkMessage(String ip, String address, Double amount) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            paramArray.add(amount);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest("z_listreceivedbyaddress",paramArray);
            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(response.get("data")) );

            for (int i=0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("memo",hex_decode(String.valueOf(jsonObject.get("memo"))));
                String time = String.valueOf (((JSONObject) new GetTransactionDetails().getTransaction(ip, String.valueOf(jsonObject.get("txid"))).get("data")).get("time"));
                jsonObject.put("time",unixtimeToData(time));
            }
            mapResult.put("status","ok");
            mapResult.put("data",jsonArray);
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

    public HashMap<String, Object> getReceiveHistory(String ip, String address) {
        return getReceiveHistory(ip, address, 0.0);
    }
    /**
     * 获取对应地址接受历史
     * @param ip:ip
     * @param address:地址
     * @param amount：金额
     * @return
     */
    public HashMap<String, Object> getReceiveHistory(String ip, String address,Double amount){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            paramArray.add(amount);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest("z_listreceivedbyaddress",paramArray);

            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(response.get("data")) );

            for (int i=0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String time = String.valueOf(jsonObject.get("blocktime"));
                jsonObject.put("memo",hex_decode(String.valueOf(jsonObject.get("memo"))));
                JSONArray jsonArray1 = JSON.parseObject(String.valueOf(new GetTransactionDetails().getTransaction(ip, String.valueOf(jsonObject.get("txid"))).get("data"))).getJSONArray("outputs");

                if (!address.equals(jsonArray1.getJSONObject(0).get("address"))){
                    jsonObject.put("sender",jsonArray1.getJSONObject(0).get("address"));
                }
                else if (jsonArray1.size() > 1 && !address.equals(jsonArray1.getJSONObject(1).get("address"))){
                    jsonObject.put("sender",jsonArray1.getJSONObject(1).get("address"));
                }
                else{
                    jsonObject.put("sender","");
                }
                jsonObject.put("time",unixtimeToData(time));
            }
            mapResult.put("status","ok");
            mapResult.put("data",jsonArray);
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
