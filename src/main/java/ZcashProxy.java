import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.omg.CORBA.Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZcashProxy {
    String serviceIp;
    int servicePort;

    public ZcashProxy(){

    }
    public ZcashProxy(String serviceIp, int  servicePort){
        //TODO:参数完善
        this.serviceIp = serviceIp;
        this.servicePort = servicePort;
    }

    public static void main(String[] args) {
        String serviceIp = "8.219.9.193";
        int servicePort = 8232;
        ZcashProxy connectionTest = new ZcashProxy(serviceIp, servicePort);

        /*sendMessage测试*/
//        HashMap result_sendMessage = connectionTest.sendMessage("http://test:Shunine8@8.219.9.193:8232/",
//                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
//                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
//                "0.001","hello test 8.25","1");
//        System.out.println("sendMessage:"+result_sendMessage);
        /*sendMessage测试*/

        /*generateAddress测试*/
        HashMap result_generateAddress = connectionTest.generateAddress("http://test:Shunine8@8.219.9.193:8232/","1",null);
        System.out.println("generateAddress:"+result_generateAddress);
        /*generateAddress测试*/

//        //opid
//        JSONArray opid = new JSONArray();
//        opid.put("oopid-04f5e151-0ca9-4a40-83b1-6c506a3b7f38");
//        JSONArray opid2 = new JSONArray();
//        opid2.put(opid);
//
//        String jsonInputString = new JSONObject()
//                .put("version", "1.1")
//                .put("method", "z_getoperationstatus")
//                .put("params", opid2)
//                .put("id", "1")
//                .toString();
//        HashMap result_opid = new HashMap();
//        result = connectionTest.sendRequest("http://test:Shunine8@8.219.9.193:8232/",jsonInputString);
//        System.out.println(result_opid);
    }

    /**
     * 字符串转十六进制
     * @param s
     * @return str
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
     * 十六进制转字符串
     * @param s
     * @return s
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 使用zcash发起交易
     * @param ip:IP地址
     * @param senderAddress:发送方地址
     * @param receiverAddress：接收方地址
     * @param amount：金额数量
     * @param message：信息
     * @param idNumber：请求的id_number
     * @return HashMap
     */

    public HashMap<String,Object> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String idNumber) {
        HashMap mapResult = new HashMap<String,Object>();

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

        JSONObject jsonInput = new JSONObject();
        jsonInput.put("version", "1.1");
        jsonInput.put("method", "z_sendmany");
        jsonInput.put("params", paramArray);
        jsonInput.put("id", idNumber);
        String jsonInputString = jsonInput.toJSONString();
        /*建立输入数据格式*/

        JSONObject response = sendRequest(ip,jsonInputString);
        if(response.get("status") == "success"){
            JSONObject jsonData = new JSONObject();
            jsonData.put("operationid",response.get("data"));
            mapResult.put("status","success");
            mapResult.put("data",jsonData);
        }else{
            JSONObject jsonData = new JSONObject();
            jsonData.put("error",response.get("data"));
            mapResult.put("status","error");
            mapResult.put("data",jsonData);
        }
        return mapResult;
    }

    /**
     * 向服务器发送请求
     * @param ip：地址
     * @param jsonInputString：请求数据
     * @return result：服务器处理结果
     */
    public JSONObject sendRequest(String ip, String jsonInputString){
        JSONObject result = new JSONObject();
        try {
            URL url = new URL(ip);
            //建立连接
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //设置头部
            /*建立发送头,发送请求*/
            con.setRequestMethod("POST");
            con.setRequestProperty("host", "AuthServiceProxy/0.1");
            con.setRequestProperty("User-Agent", "AuthServiceProxy/0.1");
            // 如何编码
            con.setRequestProperty("Content-type", "application/json");
            // HTTP授权的授权证书
            con.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(url.getUserInfo().getBytes(StandardCharsets.UTF_8)));
            // 设置是否向 HttpUrlConnection 输出，对于post请求，参数要放在 http 正文内，因此需要设为true，默认为false。
            con.setDoOutput(true);
            /*建立发送头*/

            // 写入参数到请求中
            OutputStream out = con.getOutputStream();
            out.write(jsonInputString.getBytes());
            out.flush();
            out.close();

            // 从连接中读取响应信息
            String msg = "";
            int code = con.getResponseCode();
            // 返回服务器对于HTTP请求的返回信息
            if(code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    msg += line + "\n";
                }
                // 转换为json格式
                JSONObject jsonResult = JSONObject.parseObject(msg);
                if(jsonResult.get("result") == null ){
                    result.put("status","error");
                    result.put("data",jsonResult.get("error"));
                }else {
                    result.put("status","success");
                    result.put("data",jsonResult.get("result"));
                }
                reader.close();
            }else{
               // TODO:HTTP连接错误处理
                JSONObject jsonData = new JSONObject();
                jsonData.put("message","连接失败");
                result.put("status","error");
                result.put("data",jsonData);
            }
            //  断开连接
            con.disconnect();
            // 处理结果
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 如果用户有对应账户则生成新地址，否则创建新的账户后生成新地址
     * @param ip：地址
     * @param idNumber：用户id
     * @param accountNumber：账户号码
     * @return 生成的地址
     */
    public HashMap<String, Object> generateAddress(String ip, String idNumber, Integer accountNumber){
        HashMap mapResult = new HashMap<String,Object>();

        if(accountNumber == null) {
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("version", "1.1");
            jsonInput.put("method", "z_getnewaccount");
            jsonInput.put("params", paramArray);
            jsonInput.put("id", idNumber);
            String jsonInputString = jsonInput.toJSONString();
            /*建立输入数据格式*/

            JSONObject response = sendRequest(ip, jsonInputString);
            // 创建账户成功
            if (response.get("status") == "success") {
                int account = (int) response.getJSONObject("data").get("account");
                /*建立在新建账户下获取新地址输入数据格式*/
                JSONArray paramArray_address = new JSONArray();
                paramArray_address.add(account);
                JSONObject jsonInput_address = new JSONObject();
                jsonInput_address.put("version", "1.1");
                jsonInput_address.put("method", "z_getaddressforaccount");
                jsonInput_address.put("params", paramArray_address);
                jsonInput_address.put("id", idNumber);
                String jsonGetAddressString = jsonInput_address.toJSONString();
                System.out.println("jsonGetAddressString:" + jsonGetAddressString);
                /*建立在新建账户下获取新地址输入数据格式*/

                // 获取反馈数据
                JSONObject getAddressResponse = sendRequest(ip, jsonGetAddressString);

                if (getAddressResponse.get("status") == "success") {
                    JSONObject jsonData = new JSONObject();
                    String address = (String) getAddressResponse.getJSONObject("data").get("address");
                    jsonData.put("address", address);
                    jsonData.put("account",account);
                    mapResult.put("status", "success");
                    mapResult.put("data", jsonData);

                } else {
                    JSONObject jsonData = new JSONObject();
                    jsonData.put("error", response.get("data"));
                    mapResult.put("status", "error");
                    mapResult.put("data", jsonData);
                }

            } else {
                JSONObject jsonData = new JSONObject();
                jsonData.put("error", response.get("data"));
                mapResult.put("status", "error");
                mapResult.put("data", jsonData);
            }
        }else {
            /*建立在新建账户下获取新地址输入数据格式*/
            JSONArray paramArray_address = new JSONArray();
            paramArray_address.add(accountNumber);
            JSONObject jsonInput_address = new JSONObject();
            jsonInput_address.put("version", "1.1");
            jsonInput_address.put("method", "z_getaddressforaccount");
            jsonInput_address.put("params", paramArray_address);
            jsonInput_address.put("id", idNumber);
            String jsonGetAddressString = jsonInput_address.toJSONString();
            System.out.println("jsonGetAddressString:" + jsonGetAddressString);
            /*建立在新建账户下获取新地址输入数据格式*/

            // 获取反馈数据
            JSONObject getAddressResponse = sendRequest(ip, jsonGetAddressString);

            if (getAddressResponse.get("status") == "success") {
                JSONObject jsonData = new JSONObject();
                String address = (String) getAddressResponse.getJSONObject("data").get("address");
                jsonData.put("address", address);
                mapResult.put("status", "success");
                mapResult.put("data", jsonData);

            } else {
                JSONObject jsonData = new JSONObject();
                jsonData.put("error", getAddressResponse.get("data"));
                mapResult.put("status", "error");
                mapResult.put("data", jsonData);
            }
        }
        return mapResult;
    }

}
