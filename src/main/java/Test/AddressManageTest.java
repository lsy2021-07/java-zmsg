package Test;

import org.junit.Test;
import zcash.AddressManage;

import java.util.HashMap;

import static org.junit.Assert.*;

public class AddressManageTest {
    String serviceIp = "8.219.9.193";
    private AddressManage addressManage = new AddressManage();

    @Test
    public void generateAddress() {
        String id = "AddressManageTest";
        Integer accountNumber = 13;
        HashMap result = addressManage.generateAddress(serviceIp, id,accountNumber);
        System.out.println(result);
    }
}