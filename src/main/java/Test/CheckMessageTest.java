package Test;

import org.junit.Test;
import zcash.GetTransaction;
import zcash.Receive;

import java.util.HashMap;

public class CheckMessageTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void generateAddress() {

        String serviceIp = "8.219.9.193";
        HashMap result = new Receive().checkMessage(serviceIp,
                "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg");
//        HashMap result = new GetTransaction().getTransaction2(serviceIp,
//                "31238f18cd98c63f1fcbce7aadc89ba031321dd4e6821b58873dc2dafebd5311");;
        System.out.println(result);

    }
}
