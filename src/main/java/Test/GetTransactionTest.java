package Test;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import zcash.GetTransaction;

import java.util.HashMap;

public class GetTransactionTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void generateAddress() {

        String serviceIp = "8.219.9.193";
        HashMap result = new GetTransaction().getZtransaction(serviceIp,
                "0bc1460cc4b94e452f5fe511ce0d3248d5b681ef6909fe0b83a87c9518c2f47d");
//        HashMap result = new GetTransaction().getTransaction2(serviceIp,
//                "31238f18cd98c63f1fcbce7aadc89ba031321dd4e6821b58873dc2dafebd5311");;
        System.out.println(result);

    }
}
