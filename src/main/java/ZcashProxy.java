import com.alibaba.fastjson.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

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


    public ZcashProxy(String serviceIp, int  servicePort){
        //TODO:参数完善
        this.serviceIp = serviceIp;
        this.servicePort = servicePort;
    }

    public static void main(String[] args) {
        String serviceIp = "8.219.9.193";
        int servicePort = 8232;
        ZcashProxy connectionTest = new ZcashProxy(serviceIp, servicePort);
        HashMap result = connectionTest.sendMessage("http://test:Shunine8@8.219.9.193:8232/",
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "0.001","hello test","1");
        System.out.println(result);
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


    public HashMap<String,String> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String idNumber) {

        HashMap mapResult = new HashMap<String,Object>();
        try {
            URL url = new URL(ip);
            //建立连接
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //设置头部
            /*建立发送头,发送请求*/
            con.setRequestMethod("POST");
            con.setRequestProperty("host","AuthServiceProxy/0.1");
            con.setRequestProperty("User-Agent","AuthServiceProxy/0.1");
            con.setRequestProperty("Content-type","application/json");
            con.setRequestProperty("Authorization", "Basic "+ Base64.getEncoder().encodeToString(url.getUserInfo().getBytes(StandardCharsets.UTF_8)));
            // 设置是否向 HttpUrlConnection 输出，对于post请求，参数要放在 http 正文内，因此需要设为true，默认为false。
            con.setDoOutput(true);
            /*建立发送头*/


            /*建立输入数据格式*/
            JSONObject params = new JSONObject();
            params.put("address", receiverAddress);
            params.put("amount", amount);
            String messageHex = stringToHexString(message);
            params.put("memo", messageHex);

            JSONArray paramArray1 = new JSONArray();
            paramArray1.put(params);
            JSONArray paramArray = new JSONArray();
            paramArray.put(senderAddress);
            paramArray.put(paramArray1);


            String jsonInputString = new JSONObject()
                    .put("version", "1.1")
                    .put("method", "z_sendmany")
                    .put("params:", paramArray)
                    .put("id", idNumber)
                    .toString();
            System.out.println(jsonInputString);
            /*建立输入数据格式*/

            // 写入参数到请求中
            OutputStream out = con.getOutputStream();
            out.write(jsonInputString.getBytes());
            out.flush();
            out.close();

            //读取相应信息
            // 从连接中读取响应信息
            String msg = "";
            int code = con.getResponseCode();
            // 返回服务器对于HTTP请求的返回信息
            if(code == 200) {
                // 返回的data字段
                com.alibaba.fastjson.JSONObject jsonData = new com.alibaba.fastjson.JSONObject();
                mapResult.put("status","success");

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    msg += line + "\n";
                }
                // 转换为json格式
                com.alibaba.fastjson.JSONObject jsonResult = JSON.parseObject(msg);
                jsonData.put("operationid",jsonResult.get("result"));
                mapResult.put("data",jsonData);
                reader.close();

            }else{
                mapResult.put("status","error");
                com.alibaba.fastjson.JSONObject jsonData = new com.alibaba.fastjson.JSONObject();
                //TODO:获取错误信息

                mapResult.put("data",jsonData);
            }

            //  断开连接
            con.disconnect();
            // 处理结果

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return mapResult;
    }

}
