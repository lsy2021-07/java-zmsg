package zcashTest;

import org.junit.Test;
import zcash.AddressManage;

import java.util.HashMap;

/**
 * @author yang
 */
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

    @Test
    public void getAllAddress() {
        String id = "getAllAddressTest";
        HashMap result = addressManage.getAllAddress(serviceIp, id);
        System.out.println(result);
    }
}