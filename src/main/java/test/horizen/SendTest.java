package test.horizen; 

import horizen.AddressManage;
import horizen.Send;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/** 
* Send Tester. 
* 
* @author <Authors name> 
* @since <pre>10月 24, 2022</pre> 
* @version 1.0 
*/ 
public class SendTest {
    String serviceIp = "8.219.9.193";
    private Send send = new Send();
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: stringToHexString(String s) 
* 
*/ 
@Test
public void testStringToHexString() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String id) 
* 
*/ 
@Test
public void testSendMessage() throws Exception {
    String id = "sendMessageTest";
    String sender = "zcJvmw9ZmH7CVxavbE2q88qJbipD5WD6G4Xk2DoTjPsf8zmkJtr9MxZkLsyumTyr67DSKad5S4CBWsfUfYjTsYd9t39BXNn";
    String receiver = "zcT9KxyzFFvCho8PArnDkWCjTN8Y7Mfm3NRQvDLMW1nNTvHfqTRSuNW9TUNYBkan8yEwWfmxg5Qhr89GothKWBgLBXwjBNa";
    Date date =  new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String memo = "Hello Test"+formatter.format(date).toString();
    HashMap result = send.sendMessage(serviceIp,sender,receiver,"0.0002",memo,id);
    System.out.println(result);
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
    HashMap result = send.getBalance(serviceIp,address,id);
    System.out.println(result);
}


} 
