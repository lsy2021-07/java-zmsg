package zcashTest;

import org.junit.Test;
import zcash.SendHistory;

import java.util.HashMap;

public class SendHistoryTest {
    String serviceIp = "8.219.9.193";
    String id = "SendHistoryTest";
    @Test
    public void sendHistoryTest() {

//        HashMap result = new Receive().checkMessage(serviceIp,
//                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp");
        HashMap result = new SendHistory().GetSendHistory(serviceIp, "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp");
//        HashMap result = new Receive().GetReceiveHistory(serviceIp,
//                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg");
        System.out.println(result);
    }

    @Test
    public void getSendHistory() {
        String address = "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg";
        HashMap result = new SendHistory().getSendHistoryByTxid(serviceIp,address,id );
        System.out.println(result);
    }
}

