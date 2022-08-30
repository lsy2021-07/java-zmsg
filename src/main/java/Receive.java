import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import org.omg.CORBA.Object;

import java.io.IOException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

public class Receive extends ZcashProxy{

    // test
    /*
        String serviceIp = "8.219.9.193";
        Receive Message = new Receive();
        HashMap result = Message.checkMessage(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                0.0);
        System.out.println(result);
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

    public String unixtimeToData(String time){
        time += "000";   // python time stamp to java time stamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  simpleDateFormat.format(new Date(new Long(time)));
    }

    public HashMap<String, Object> checkMessage(String ip, String address) {
        return checkMessage(ip, address, 0.0);
    }

    public HashMap<String, Object> checkMessage(String ip, String address, Double amount) {
        HashMap mapResult = new HashMap<String,Object>();
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
                String time = String.valueOf (((JSONObject) new GetTransaction().getTransaction(ip, String.valueOf(jsonObject.get("txid"))).get("data")).get("time"));
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

    //test
    /*
           HashMap result = Message.getTransaction(serviceIp,
        "5f703ef2c7768d48efa8b7ae3499eb623c96fb0e83294d840a9cb17a2c55be43");
     */

}
