import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONObject;

public class RecallForm {

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://test:Shunine8@8.219.9.193:8232/");
        HttpURLConnection con = (HttpURLConnection)url.openConnection(); //建立连接

        /*建立发送头,发送请求*/
        con.setRequestMethod("POST");
        con.setRequestProperty("host","AuthServiceProxy/0.1");
        con.setRequestProperty("User-Agent","AuthServiceProxy/0.1");
        con.setRequestProperty("Content-type","application/json");
        con.setRequestProperty("Authorization", "Basic "+ Base64.getEncoder().encodeToString(url.getUserInfo().getBytes(StandardCharsets.UTF_8)));
        con.setDoOutput(true);
        /*建立发送头*/

        /*建立输入数据格式*/
        String jsonInputString = new JSONObject()
                .put("version", "1.1")
                .put("method", "getinfo")
                .toString();

        /*建立输入数据格式*/

        /*写入输入数据*/
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        /*写入输入数据*/

        /*读返回数据*/
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        /*读返回数据*/

    }
}
