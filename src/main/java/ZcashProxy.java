import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ZcashProxy {
    HttpURLConnection con;
    List<String> ipSet = new ArrayList<>(Arrays.asList("8.219.9.193"));  //服务器ip集合

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

//    public JSONObject _call(String method, JSONArray params) throws IOException {
//        JSONObject jsonInput = new JSONObject();
//        jsonInput.put("version", "1.1");
//        jsonInput.put("method", method);
//        jsonInput.put("params", params);
//
//        String jsonInputString = jsonInput.toJSONString();
//
//        OutputStream outputStream = this.con.getOutputStream();
//        outputStream.write(jsonInputString.getBytes());
//        outputStream.flush();
//        outputStream.close();
//        //return this.con.getResponseCode();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(this.con.getInputStream()));
//        StringBuilder msg = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            msg.append(line);
//        }
//        // 转换为json格式
//        reader.close();
//        String response = msg.toString();
//        return JSON.parseObject(response).getJSONObject("result");
//    }

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

//    public HashMap<String, String> GetInfo(String ip, String receiverAddress) {
//        HashMap mapResult = new HashMap<String, Object>();
//        try {
//            /*建立连接*/
//            _con(ip);
//            /*建立连接*/
//
//            /*建立数据格式*/
//            JSONArray params = new JSONArray();
//            //TODO 自定义参数
//            /*建立数据格式*/
//
//            /*发送并得到响应*/
//            JSONObject response = _call("getinfo", params);
//            /*发送并得到响应*/
//
//            mapResult.put("status", "ok");
//            mapResult.put("data", response);
//
//            /*处理数据*/
//            //TODO
//            /*处理数据*/
//        } catch (IOException e) {
//            e.printStackTrace();
//            mapResult.put("status", "error");
//            mapResult.put("data", e.getMessage());
//        }
//
//        return mapResult;
//    }

}