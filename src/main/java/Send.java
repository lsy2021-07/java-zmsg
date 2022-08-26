import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.omg.CORBA.Object;
import java.util.HashMap;

public class Send extends ZcashProxy{
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
     * @param idNumber：请求的id_number
     * @return HashMap
     */
    public HashMap<String, Object> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String idNumber) {
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
        /*建立输入数据格式*/

        JSONObject response = sendRequest(ip,"z_sendmany",paramArray,idNumber);
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

}