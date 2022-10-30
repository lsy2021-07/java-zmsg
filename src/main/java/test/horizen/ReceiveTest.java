package test.horizen; 

import horizen.Receive;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.HashMap;

/** 
* Receive Tester. 
* 
* @author <Authors name> 
* @since <pre>10月 29, 2022</pre> 
* @version 1.0 
*/ 
public class ReceiveTest {
    String serviceIp = "8.219.9.193";
    Receive receive = new Receive();
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: checkMessage(String ip, String address) 
* 
*/ 
@Test
public void testCheckMessage() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    HashMap result =  receive.checkMessage(serviceIp,address);
    System.out.println(result);
} 

/** 
* 
* Method: checkMessage(String ip, String address, Double amount) 
* 
*/ 
@Test
public void testCheckMessageWithAmount() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    Double amount = 1.0;
    HashMap result =  receive.checkMessage(serviceIp,address,amount);
    System.out.println(result);

}

/**
* 
* Method: GetReceiveHistory(String ip, String address) 
* 
*/ 
@Test
public void testGetReceiveHistory() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
//    Double amount = 1.0;
    HashMap result =  receive.getReceiveHistory(serviceIp,address);
    System.out.println(result);
} 

/** 
* 
* Method: GetReceiveHistory(String ip, String address, Double amount) 
* 
*/ 
@Test
public void testGetReceiveWithAmount() throws Exception {
//TODO: Test goes here...
} 


} 
