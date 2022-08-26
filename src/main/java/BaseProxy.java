//import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;


public class BaseProxy {
    String serviceUrl;
    URL url;
    HttpURLConnection __conn;
    int __id_count;
    String __auth_header;

    public BaseProxy(String network, String service_url, int service_port, String zcash_conf_file, String timeout) {
        if (network.equals("testnet")) {
            service_port = 18232;
        } else if (network.equals("mainnet")) {
            service_port = 8232;
        } else {

//            throw Exception; //指定异常
        }

        if (service_url.equals("")) {
            zcash_conf_file = "zcash.conf";
            try {
                File FileObj = new File(zcash_conf_file);
                Scanner fd = new Scanner(FileObj);
                HashMap<String,String> conf = new HashMap<>();
                conf.put("rpcuser","");
                while (fd.hasNextLine()) {
                    String line = fd.nextLine();
                    if (line.indexOf('#') != -1) {
                        line = line.substring(0, line.indexOf('#'));
                    }
                    if (line.indexOf('=') == -1) {
                        continue;
                    }
                    int index = line.indexOf('=');
                    String k = line.substring(0, index), v = line.substring(index + 1, line.length());
                    conf.put(k,v);
                    conf.put("rpcport",conf.getOrDefault("rpcport",String.valueOf(service_port)));
                    conf.put("rpcconnect",conf.getOrDefault("rpcconnect","localhost"));

                    if (conf.get("rpcpassword")==null){
                        throw new IllegalArgumentException();
                    }
                    service_url = "http://" + conf.get("rpcuser") + ":" + conf.get("rpcpassword") + "@" + conf.get("rpcport") + ":" + conf.get("rpcport");
                    serviceUrl = service_url;
                    url = new URL(service_url);

                    __conn = (HttpURLConnection) new URL("http://www.example.com").openConnection();

                    __id_count = 0;
                    __auth_header = Base64.getEncoder().encodeToString(url.getUserInfo().getBytes(StandardCharsets.UTF_8));


//                    connection.setRequestProperty("Authorization", "Basic "+encoded);
                }
                fd.close();
            } catch (FileNotFoundException | MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

//    public _call(String service_name){
//        JSONObject postdata = new JSONObject();
//        postdata.put("version","1.1");
//        postdata.put("method",service_name);
//        postdata.put("method",__id_count);
//
//        __conn.setRequestMethod("POST");
//        __conn.setRequestProperty("Host", url.getHost());
//        __conn.setRequestProperty("User-Agent", "AuthServiceProxy/0.1");
//        __conn.setRequestProperty("Authorization", "Basic "+ __auth_header);
//        __conn.setRequestProperty("Content-type", "application/json");
//
//
////        conn.set
//
//    }


}


