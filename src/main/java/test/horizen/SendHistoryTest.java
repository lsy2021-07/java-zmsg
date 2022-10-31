package test.horizen; 

import horizen.Receive;
import horizen.Send;
import horizen.SendHistory;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.HashMap;

/** 
* SendHistory Tester. 
* 
* @author <Authors name> 
* @since <pre>10月 30, 2022</pre> 
* @version 1.0 
*/ 
public class SendHistoryTest {
    String serviceIp = "8.219.9.193";
    SendHistory sendHistory = new SendHistory();
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getSendHistory(String ip, String address) 
* 
*/ 
@Test
public void testGetSendHistory() throws Exception { 
    String address = "zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    String id = "testGetSendHistory";
    HashMap result = sendHistory.getSendHistory(serviceIp,address,id);
    System.out.println(result);
} 

} 
