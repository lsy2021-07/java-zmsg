package horizen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class HorizenProxy {
    HttpURLConnection con;
    List<String> ipSet = new ArrayList<>(Arrays.asList("8.219.9.193"));  //服务器ip集合

    public void _con(String serviceIp) throws IOException {
        // 服务端口
        if (!ipSet.contains(serviceIp)){
            throw new IOException("{err_msg:\"ip地址错误\"}");
        }
        int servicePort = 8242;
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
     * @param method：调用方法名
     * @param paramArray：参数列表
     * @param id：用户标识
     * @return result(JSONObject):服务器结果
     */
    public JSONObject _sendRequest( String method, JSONArray paramArray, String id) throws IOException {
        JSONObject result = new JSONObject();

        /*请求参数*/
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("version", "1.1");
        jsonInput.put("method", method);
        jsonInput.put("params", paramArray);
        jsonInput.put("id", id);
        String jsonInputString = jsonInput.toJSONString();
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


    public JSONObject _sendRequest( String method, JSONArray paramArray) throws IOException {
        JSONObject result = new JSONObject();

        /*请求参数*/
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("version", "1.1");
        jsonInput.put("method", method);
        jsonInput.put("params", paramArray);
        String jsonInputString = jsonInput.toJSONString();

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

    public static void main(String[] args) {
        String serviceIp = "8.219.9.193";
        HorizenProxy horizenProxy = new HorizenProxy();
        HashMap result = horizenProxy._opidTotxid(serviceIp, "opid-a075e2c2-c66c-4746-b7fc-4c2f79dcbf0e");
        System.out.println(result);
//        horizenProxy._opidTotxid(serviceIp, "opid-605e9f90-766d-4511-a24e-dda7ee507665");
    }

    public HashMap<String, Object> _opidTotxid(String ip, String opid) {
        HashMap mapResult = new HashMap<String,Object>();
        try{
            _con(ip);
            /*建立输入数据格式*/
            JSONArray paramArray = new JSONArray();
            List<String> list=new ArrayList<>();
            list.add(opid);
            paramArray.add(list);
            JSONObject response = _sendRequest("z_getoperationstatus",paramArray);
            JSONObject jsonData = JSON.parseObject(String.valueOf(response.getJSONArray("data").getJSONObject(0)));
            String txid = (String) JSON.parseObject(String.valueOf(jsonData.get("result"))).get("txid");
            mapResult.put("status","ok");
            mapResult.put("data",txid);
        } catch (IOException e) {
            mapResult.put("status","error");
            JSONObject jsonData = JSON.parseObject(e.getMessage());
            mapResult.put("data",jsonData);
            e.printStackTrace();
        }
        finally {
            this.con.disconnect();
        }
        System.out.println(mapResult);
        return mapResult;
    }

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


}