package zcash;

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

public class ZcashNet {

    HttpURLConnection con;
    List<String> ipSet = new ArrayList<>(Arrays.asList("8.219.9.193"));  //服务器ip集合

    public ZcashNet(){

    }

    /** 基础连接模块 **/
    /**
     * 配置基础连接信息
     * @param serviceIp:服务器IP
     * @throws IOException：错误
     */
    public void _con(String serviceIp) throws IOException {
        // 服务端口
        if (!ipSet.contains(serviceIp)){
            throw new IOException("{err_msg:\"ip地址错误\"}");
        }
        int servicePort = 8232;
        URL url = new URL("http://test:Shunine8@" + serviceIp + ":" + servicePort + "/");
        this.con = (HttpURLConnection) url.openConnection(); //建立连接
        this.con.setRequestMethod("POST");
        this.con.setRequestProperty("User-Agent", "AuthServiceProxy/0.1");
        this.con.setRequestProperty("Content-type", "application/json");
        this.con.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(url.getUserInfo().getBytes(StandardCharsets.UTF_8)));
        // 设置是否向 HttpUrlConnection 输出，对于post请求，参数要放在 http 正文内，因此需要设为true，默认为false。
        this.con.setDoOutput(true);
    }

    /**
     * 向服务器发起请求，接受返回信息
     * @param serviceIp：服务器Ip
     * @param method：调用方法名
     * @param paramArray：参数列表
     * @param id：用户标识
     * @return result(JSONObject):服务器结果
     */
    public JSONObject _sendRequest(String serviceIp, String method, JSONArray paramArray, String id) throws IOException {
        JSONObject result = new JSONObject();

        /*请求参数*/
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("version", "1.1");
        jsonInput.put("method", method);
        jsonInput.put("params", paramArray);
        jsonInput.put("id", id);
        String jsonInputString = jsonInput.toJSONString();
//        System.out.println(jsonInputString);
        /*写入参数到请求中*/

        OutputStream out = this.con.getOutputStream();
        out.write(jsonInputString.getBytes());
        out.flush();
        out.close();

        /*从连接中读取响应信息*/
        int code = this.con.getResponseCode();
        // 返回服务器对于HTTP请求的返回信息
        if (code == 200) {
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // 转换为json格式
            JSONObject jsonResult = JSONObject.parseObject(response.toString());
            if (jsonResult.get("result") == null) {
                throw new IOException("{err_code:"+"000"+",err_msg:\"missing JSON-RPC result\"}" );
            }
            result.put("data", jsonResult.get("result"));
            reader.close();
        } else {
            // TODO:HTTP连接错误处理
            throw new IOException("{err_code:"+String.valueOf(code)+"," + "err_msg:\"missing HTTP response from server\"}");
        }

        /*从连接中读取响应信息*/
        return result;
    }
    // TODO:以下用于测试，到时候删除
    public JSONObject _sendRequest(String serviceIp, String method, JSONArray paramArray) throws IOException {
        JSONObject result = new JSONObject();

        /*请求参数*/
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("version", "1.1");
        jsonInput.put("method", method);
        jsonInput.put("params", paramArray);
        String jsonInputString = jsonInput.toJSONString();
//        System.out.println(jsonInputString);

        /*写入参数到请求中*/
        OutputStream out = this.con.getOutputStream();
        out.write(jsonInputString.getBytes());
        out.flush();
        out.close();

        /*从连接中读取响应信息*/
        int code = this.con.getResponseCode();
        // 返回服务器对于HTTP请求的返回信息
        if (code == 200) {
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // 转换为json格式


            JSONObject jsonResult = JSONObject.parseObject(response.toString());

            if (jsonResult.get("result") == null) {
                throw new IOException("{err_code:"+"000"+",err_msg:\"missing JSON-RPC result\"}" );
            }
            result.put("data", jsonResult.get("result"));
            reader.close();
        } else {
            // TODO:HTTP连接错误处理
            throw new IOException("{err_code:"+String.valueOf(code)+"," + "err_msg:\"missing HTTP response from server\"}");
        }

        /*从连接中读取响应信息*/
        return result;
    }

    /** 基础连接模块 **/

    /** 发送模块 **/
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
    /** 发送模块 */

    /** 接受模块 **/

    /**
     * 将十六进制转化为字符串
     * @param s_hex:十六进制
     * @return：字符串
     */
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

    /**
     * 将时间形式转化为"yyyy-MM-dd HH:mm:ss"
     * @param time:区块链时间
     * @return：解码后时间
     */
    public String unixtimeToData(String time){
        time += "000";   // python time stamp to java time stamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return  simpleDateFormat.format(new Date(new Long(time)));
    }

    /**
     * 用于查询对应地址的信息
     * @param ip:服务器ip
     * @param address:查询接收消息的地址
     * @return:查询结果
     */
    public HashMap<String, java.lang.Object> checkMessage(String ip, String address) {
        return checkMessage(ip, address, 0.0);
    }

    public HashMap<String, java.lang.Object> checkMessage(String ip, String address, Double amount) {
        HashMap mapResult = new HashMap<String, java.lang.Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            paramArray.add(amount);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"z_listreceivedbyaddress",paramArray);

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

    /** 接受模块 **/



    /**  发送历史记录 **/
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
    /**  发送历史记录  **/

    /**  产生新地址   **/
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
    /**  产生新地址  **/


    /**  接收历史记录 **/
    public HashMap<String, Object> GetReceiveHistory(String ip, String address){
        return GetReceiveHistory(ip, address, 1);
    }
    public HashMap<String, Object> GetReceiveHistory(String ip, String address, Integer minconf){
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            paramArray.add(address);
            paramArray.add(minconf);
            /*建立输入数据格式*/

            JSONObject response = _sendRequest(ip,"z_listreceivedbyaddress",paramArray);

            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(response.get("data")) );
            List<JSONObject> resJsonList = new ArrayList<JSONObject>();
            for (int i=0; i < jsonArray.size(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String time = unixtimeToData(String.valueOf(jsonObject.get("blocktime")));
                String memo_address = hex_decode(String.valueOf(jsonObject.get("memo")));
                String txid = jsonObject.getString("txid");
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
                _jsonObject.put("sendAddress",sendAddress);
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
        finally {
            this.con.disconnect();
        }
        return mapResult;
    }
    /**  接收历史记录 **/

    /** 接受详情**/
    public HashMap<String, Object> getReceiveDetail(String ip, String txid) {
        HashMap mapResult = new HashMap<java.lang.String, java.lang.Object>();
        try{
            _con(ip);
            JSONArray paramArray1 = new JSONArray();
            paramArray1.add(txid);
            JSONObject response1 = _sendRequest(ip,"z_viewtransaction", paramArray1);
            JSONObject jsonData = response1.getJSONObject("data");

            JSONObject jsonResult = new JSONObject();
            jsonResult.put("txid",jsonData.get("txid"));
            jsonResult.put("sendAddress",((JSONObject)jsonData.getJSONArray("spends").get(0)).get("address"));
            jsonResult.put("receiveAddress",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("address"));
            jsonResult.put("amount",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("value"));
            jsonResult.put("memo",((JSONObject)jsonData.getJSONArray("outputs").get(0)).get("memoStr"));


            mapResult.put("status","ok");
            mapResult.put("data",jsonResult);
        }catch (IOException e) {
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
    /**  接收历史记录 **/


    /** 交易记录  **/

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
    /** 交易记录  **/
}
