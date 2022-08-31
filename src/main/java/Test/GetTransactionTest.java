package Test;

import org.junit.Test;

import java.util.HashMap;

public class GetTransactionTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void generateAddress() {

        String serviceIp = "8.219.9.193";
//        HashMap result = new GetTransaction().getTransaction(serviceIp,
//                "5f703ef2c7768d48efa8b7ae3499eb623c96fb0e83294d840a9cb17a2c55be43");;
        HashMap result = new GetTransaction().getTransaction(serviceIp,
                "31238f18cd98c63f1fcbce7aadc89ba031321dd4e6821b58873dc2dafebd5311");;
        System.out.println(result);
    }
}
