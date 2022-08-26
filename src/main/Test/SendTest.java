import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class SendTest {
    String serviceIp = "8.219.9.193";
    private Send send = new Send();

    @Test
    public void stringToHexString() {
    }

    @Test
    public void sendMessage() {
        HashMap result = send.sendMessage(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "0.001","hello test 8.26","1");
        System.out.println(result);
    }

    @Test
    public void getBalance() {
        String address = "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp";
        String id = "sendTest";
        System.out.println(send.getBalance(serviceIp, address, id));
    }
}