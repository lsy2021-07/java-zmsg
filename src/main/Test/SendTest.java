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

        // 发送消息成功
        HashMap result_success = send.sendMessage(serviceIp,
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp",
                "0.001","hello test 8.29","1");
        System.out.println("成功："+result_success);

        // 发送消息失败
        HashMap result_fail = send.sendMessage(serviceIp,
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "u1eqghjwucgt3facqlwzzcjrtp56g54l7g5ar8gxnldxpdj80h9hrspa8j39qtt704h3edlpatkzvp8nwyh90h244mhnjdpsklaxkh9ljp04qt7rewrfuentewdj9wxyyxje2cdach2t4xrj7jw0v7ajc0ps4yyapcnqyqapwplname7d2yw2xx2ynukgltwdsmv90zpqsqxlry7uvj9d",
                "0.001","hello test 8.29","1");
        System.out.println("失败："+result_fail);

        // 金额不足
        HashMap result2 = send.sendMessage(serviceIp,
                "u1eqghjwucgt3facqlwzzcjrtp56g54l7g5ar8gxnldxpdj80h9hrspa8j39qtt704h3edlpatkzvp8nwyh90h244mhnjdpsklaxkh9ljp04qt7rewrfuentewdj9wxyyxje2cdach2t4xrj7jw0v7ajc0ps4yyapcnqyqapwplname7d2yw2xx2ynukgltwdsmv90zpqsqxlry7uvj9d",
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",
                "0.001","hello test 8.29","1");
        System.out.println("失败："+result2);


    }

    @Test
    public void getBalance() {
        String address = "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp";
        String id = "sendTest";
        System.out.println("该地址金额数："+send.getBalance(serviceIp, address, id));
    }
}