package test.horizen; 

import horizen.GetTransactionDetails;
import horizen.Receive;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.HashMap;

/** 
* GetTransactionDetails Tester. 
* 
* @author <Authors name> 
* @since <pre>10月 30, 2022</pre> 
* @version 1.0 
*/ 
public class GetTransactionDetailsTest {
    String serviceIp = "8.219.9.193";
    GetTransactionDetails getTransactionDetails = new GetTransactionDetails();
@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getTransaction(String ip, String txid) 
* 
*/ 
@Test
public void testGetTransaction() throws Exception {
    String txid = "5cd3bc81fc78ce6b5bcb34ecaf4a58ad750dde16bf267409a303f2f6d8a16e7a";
    String id  = "testGetTransaction";
    HashMap result = getTransactionDetails.getTransaction(serviceIp,txid,id);
    System.out.println(result);
} 

/** 
* 
* Method: getZtransaction(String ip, String txid) 
* 
*/ 
@Test
public void testGetZtransaction() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getTransactionDetails(String ip, String opid) 
* 
*/ 
@Test
public void testGetTransactionDetails() throws Exception {
    String opid = "opid-04a05510-6edf-4734-95cd-d97c43dfa7a6";
    String id  = "testGetTransactionDetails";
    HashMap result = getTransactionDetails.getTransactionDetails(serviceIp,opid,id);
    System.out.println(result);

} 


} 
