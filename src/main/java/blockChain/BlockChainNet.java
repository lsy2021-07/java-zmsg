package blockChain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import horizen.HorizenNet;
import zcash.ZcashNet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlockChainNet {
    HttpURLConnection con;
    List<String> ipSet = new ArrayList<>(Arrays.asList("8.219.9.193"));  //服务器ip集合

    public BlockChainNet(){

    }
    public BlockChainNet setBlockChainNet(String type){
        if(type == "zcash"){
            ZcashNet zcashNet = new ZcashNet();
            return zcashNet;
        }else if(type == "horizen"){
            HorizenNet horizenNet = new HorizenNet();
            return horizenNet;
        }else {
            return null;
        }
    }

    /** 基础连接模块 **/
    /**
     * 配置基础连接信息
     * @param serviceIp:服务器IP
     * @throws IOException：错误
     */
    public void _con(String serviceIp,String servicePort) throws IOException {
        // 服务端口
        if (!ipSet.contains(serviceIp)){
            throw new IOException("{err_msg:\"ip地址错误\"}");
        }
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
    public JSONObject _sendRequest(String method, JSONArray paramArray, String id) throws IOException {
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
        this.con.disconnect(); // 断开连接
        /*从连接中读取响应信息*/
        return result;
    }
    /** 基础连接模块 **/

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

    /** 判断是否要relpy to **/
    public List<String>  getReplyTo(String memo_address){
        List<String> result = new ArrayList<String>();
        int index = memo_address.lastIndexOf("Reply-To:");
        String memo="",sendAddress="";
        if (index==-1){
            memo = memo_address;
            result.add(memo);
            result.add(sendAddress);
        }
        else{
            memo = memo_address.substring(0,index);
            sendAddress = memo_address.substring(index+9);
            result.add(memo);
            result.add(sendAddress);
        }
        return result;
    }
    /** 判断是否要relpy to **/

    /**
     * 地址管理
     **/
    public HashMap<String, Object> getAllAddress(String ip, String id) {
        return null;
    }

    public HashMap<String, Object> generateAddress(String ip, String id) {
        return null;
    }

    /**
     * 发送消息
     **/
    public HashMap<String, Object> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String id) {
        return null;
    }

    public HashMap<String, Object> getBalance(String ip, String address, String id) {
        return null;
    }

    /**
     * 发送历史记录
     **/
    public HashMap<String, Object> getSendHistory(String ip, String address, String id) {
        return null;
    }

    /**
     * 接受历史记录
     **/
    public HashMap<String, Object> getReceiveHistory(String ip, String address, String id) {
        return null;
    }

    public HashMap<String, Object> getReceiveDetail(String ip, String txid, String id) {
        return null;
    }

    public HashMap<String, Object> getSendDetail(String ip, String opid, String id) {
        return null;
    }
}
