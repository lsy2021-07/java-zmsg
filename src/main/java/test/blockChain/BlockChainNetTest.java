package test.blockChain; 

import blockChain.BlockChainNet;
import horizen.HorizenNet;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import zcash.ZcashNet;

import java.util.HashMap;

/** 
* BlockChainNet Tester. 
* 
* @author <Authors name> 
* @since <pre>11月 1, 2022</pre> 
* @version 1.0 
*/ 
public class BlockChainNetTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: blockChainNet(String type) 
* 
*/ 
@Test
public void testBlockChainNet() throws Exception { 
    BlockChainNet blockChainNet = new BlockChainNet() {
        @Override
        public HashMap<String, Object> getAllAddress(String ip, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> generateAddress(String ip, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> sendMessage(String ip, String senderAddress, String receiverAddress, String amount, String message, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> getBalance(String ip, String address, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> getSendHistory(String ip, String address, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> getReceiveHistory(String ip, String address, String id) {
            return null;
        }

        @Override
        public HashMap<String, Object> getReceiveDetail(String ip, String txid, String id) {
            return null;
        }
    };

    String ip = "8.219.9.193";
    String id = "111";
    System.out.println(blockChainNet);
}


} 
