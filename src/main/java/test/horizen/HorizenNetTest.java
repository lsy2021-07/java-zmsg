package test.horizen; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import horizen.HorizenNet;

import java.util.HashMap;

/** 
* horizenNet Tester. 
* 
* @author <Authors name> 
* @since <pre>11月 1, 2022</pre> 
* @version 1.0 
*/ 
public class HorizenNetTest {
    String serviceIp = "8.219.9.193";
    private HorizenNet horizenNet = new HorizenNet();
    String id = "horizenNetTest";

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

@Test
public void testSendMessage() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getBalance(String ip, String address, String id) 
* 
*/ 
@Test
public void testGetBalance() throws Exception {
    String id = "getBalanceTest";
    String address = "zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    String recevierAddress = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    HashMap result = horizenNet.getBalance(serviceIp,address,id);
    System.out.println(result);
} 

/** 
* 
* Method: getReceiveHistory(String ip, String address, String id) 
* 
*/ 
@Test
public void testGetReceiveHistoryForIpAddressId() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    String id = "testGetReceiveHistory";
    HashMap result =  horizenNet.getReceiveHistory(serviceIp,address,id);
    System.out.println(result);
} 

/** 
* 
* Method: getReceiveHistory(String ip, String address, Integer minconf, String id) 
* 
*/ 
@Test
public void testGetReceiveHistoryForIpAddressMinconfId() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: generateAddress(String ip, String id) 
* 
*/ 
@Test
public void testGenerateAddress() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getAllAddress(String ip, String id) 
* 
*/ 
@Test
public void testGetAllAddress() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getSendHistory(String ip, String address, String id) 
* 
*/ 
@Test
public void testGetSendHistory() throws Exception { 
//TODO: Test goes here... 
} 


} 
