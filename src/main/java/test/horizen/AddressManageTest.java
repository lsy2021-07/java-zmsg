package test.horizen; 

import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import horizen.AddressManage;

import java.util.HashMap;

/** 
* AddressManage Tester. 
* 
* @author <Authors name> 
* @since <pre>10月 23, 2022</pre> 
* @version 1.0 
*/ 
public class AddressManageTest {
    String serviceIp = "8.219.9.193";
    private AddressManage addressManage = new AddressManage();

@Before()
public void before() throws Exception {

} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: generateAddress(String ip, String id, Integer accountNumber) 
* 
*/ 
@Test
public void testGenerateAddress() throws Exception {
    String id = "GenerateAddressTest";
    HashMap result = addressManage.generateAddress(serviceIp, id);
    System.out.println(result);
} 

/** 
* 
* Method: getAllAddress(String ip, String id) 
* 
*/ 
@Test
public void testGetAllAddress() throws Exception {
    String id = "GetAllAddressTest";
    HashMap result = addressManage.getAllAddress(serviceIp, id);
    System.out.println(result);
} 


} 
