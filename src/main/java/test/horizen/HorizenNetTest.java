package test.horizen; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import horizen.HorizenNet;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    String sender = "zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    String receiver = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    Date date =  new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String memo = "Hello Test "+formatter.format(date).toString()+"reply to:zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    HashMap result = horizenNet.sendMessage(serviceIp,sender,receiver,"0.0002",memo,id);
    System.out.println(result);

} 

/** 
* 
* Method: getBalance(String ip, String address, String id) 
* 
*/ 
@Test
public void testGetBalance() throws Exception {
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
    HashMap result =  horizenNet.getReceiveHistory(serviceIp,address,id);
    System.out.println(result);
} 

/** 
* 
* Method: getReceiveHistory(String ip, String address, Integer minconf, String id) 
* 
*/ 
@Test
public void testGetReceiveHistory() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    String id = "testGetReceiveHistory";
    HashMap result =  horizenNet.getReceiveHistory(serviceIp,address,id);
    System.out.println(result);
} 

/** 
* 
* Method: generateAddress(String ip, String id) 
* 
*/ 
@Test
public void testGenerateAddress() throws Exception {
    String id = "GenerateAddressTest";
    HashMap result = horizenNet.generateAddress(serviceIp, id);
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
    HashMap result = horizenNet.getAllAddress(serviceIp, id);
    System.out.println(result);
} 

/** 
* 
* Method: getSendHistory(String ip, String address, String id) 
* 
*/ 
@Test
public void testGetSendHistory() throws Exception {
    String address = "zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    String id = "testGetSendHistory";
    HashMap result = horizenNet.getSendHistory(serviceIp,address,id);
    System.out.println(result);
}

@Test
public void testGetTransactionDetailWithTx() throws Exception {
    String address = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    String txid = "0a980f67a1c1101e63c79a32f1bf53b91e0cccd2e4def76f75c69da6368d24cd";
    String id = "testGetReceiveHistory";
    HashMap result =  horizenNet.getTransactionDetailsWithTx(serviceIp,address,txid,id);
    System.out.println(result);
}
} 
