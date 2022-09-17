package Test;

import org.junit.Test;
import zcash.GetTransactionDetails;

import java.util.HashMap;

public class GetTransactionDetailsTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void generateAddress() {

        String serviceIp = "8.219.9.193";
        HashMap result = new GetTransactionDetails().getTransactionDetails(serviceIp,
                "opid-a075e2c2-c66c-4746-b7fc-4c2f79dcbf0e");
//        HashMap result = new GetTransaction().getTransaction2(serviceIp,
//                "31238f18cd98c63f1fcbce7aadc89ba031321dd4e6821b58873dc2dafebd5311");;
        System.out.println(result);

    }
}
