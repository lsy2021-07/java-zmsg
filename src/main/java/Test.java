import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        String serviceIp = "8.219.9.193";

        /*发送消息测试*/
        Send Message = new Send();
        HashMap result = Message.sendMessage(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "0.001","hello test 8.26","1");
        System.out.println(result);
        /*发送消息测试*/

    }
    public Test(){

    }

    
}
