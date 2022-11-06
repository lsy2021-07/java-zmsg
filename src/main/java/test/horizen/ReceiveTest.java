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
* Method: GetReceiveHistory(String ip, String address) 
* 
*/ 
@Test
public void testGetReceiveHistory() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    String id = "testGetReceiveHistory";
    HashMap result =  receive.getReceiveHistory(serviceIp,address,id);
    System.out.println(result);
}

@Test
public void testOp_getReceiveHistory() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    String id = "testOp_getReceiveHistory";
    HashMap result =  receive.Op_getReceiveHistory(serviceIp,address,id);
    System.out.println(result);
}


} 
