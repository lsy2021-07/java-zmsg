package Test;

import org.junit.Test;
import zcash.ZcashNet;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ZcashNetTest {
    String serviceIp = "8.219.9.193";
    private ZcashNet zcashNet = new ZcashNet();
    String id = "zcashNetTest";
    @Test
    public void sendMessage() {
        HashMap result_success = zcashNet.sendMessage(serviceIp,
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                "0.001","hello test 8.29",id);
        System.out.println("成功："+result_success);
    }

    @Test
    public void getBalance() {
        String address = "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp";
        System.out.println("该地址金额数："+zcashNet.getBalance(serviceIp, address, id));
    }

    @Test
    public void checkMessage() {
        HashMap result = zcashNet.checkMessage(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp");
        System.out.println(result);
    }

    @Test
    public void getReceivehistory() {
        HashMap result = zcashNet.GetReceiveHistory(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp");
        System.out.println(result);
    }
}