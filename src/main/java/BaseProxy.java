import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;


public class BaseProxy {
    String serviceUrl;
    URL url;
    HttpPost conn;
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
                    conn = new HttpPost("http://www.example.com");

//         python   ??  if service_port is None: service_port = network

//                    conf.put();



//                    System.out.println(data);
                }
                fd.close();
            } catch (FileNotFoundException e) {

            }

        }


    }

//    public _call(String service_name, )

}


