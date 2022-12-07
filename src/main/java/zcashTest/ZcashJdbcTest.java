package zcashTest;

import org.junit.Test;
import zcash.ZcashJdbc;

import java.util.List;


public class ZcashJdbcTest {

    @Test
    public void checkByTxid() {
        ZcashJdbc jdbc = new ZcashJdbc();
        String txid = "dafe695e7b5ae3a7d12d39233007745a7a8c2e44b3b249629599bb6681c69c03";
        List<List<String>> result = jdbc.checkByTxid(txid);
        System.out.println(result);
    }

    @Test
    public void checkByAdd1() {
        ZcashJdbc jdbc = new ZcashJdbc();
        String address = "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg";
        List<List<String>> result = jdbc.checkByAdd1(address);
        System.out.println(result);
    }

    @Test
    public void checkByAdd2() {
        ZcashJdbc jdbc = new ZcashJdbc();
        String address = "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg";
        List<List<String>> result = jdbc.checkByAdd1(address);
        System.out.println(result);
    }

    @Test
    public void checkByAdd1_txid() {
        ZcashJdbc jdbc = new ZcashJdbc();
        String address = "zs1w9sk86zx980lu0e30zm8t9xp3cjadhwnel70qyhtzmawuppjq66kmmmkv409eg90sk4jxdymjwg";
        List<String> result = jdbc.checkByAdd1_txid(address);
        System.out.println(result);
    }
}