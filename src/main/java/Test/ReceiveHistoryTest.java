package Test;

import org.junit.Test;
import zcash.ReceiveHistory;

import java.util.HashMap;

public class ReceiveHistoryTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void ReceiveHistoryTest() {

        HashMap result = new ReceiveHistory().GetReceiveHistory(serviceIp,
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg",0.0);

        System.out.println(result);
//        String a="pap";

    }
}
