package test.blockChain;

import blockChain.BlockChainNet;
import horizen.HorizenNet;
import org.junit.Test;
import zcash.ZcashNet;

import java.util.HashMap;

public class BlockChainNetTest {
    String serviceIp = "8.219.9.193";
    String id = "horizenNetTest";
    @Test
    public void setBlockChainNet() {
    }

    @Test
    public void getAllAddress() {
       BlockChainNet Net = new ZcashNet();
//       BlockChainNet Net = new HorizenNet();
       HashMap result = Net.getAllAddress(serviceIp, id);
       System.out.println(result);

    }

    @Test
    public void generateAddress() {
    }

    @Test
    public void sendMessage() {
    }

    @Test
    public void getBalance() {
    }

    @Test
    public void getSendHistory() {
    }

    @Test
    public void getReceiveHistory() {
    }

    @Test
    public void getReceiveDetail() {
        BlockChainNet Net = new ZcashNet();
        String txid = "26207398d65f4c2e0cbcda8d6a2c9fc746b2ff26568ed37016aa6b3ca531601d";
        HashMap result = Net.getReceiveDetail(serviceIp,txid, id);
        System.out.println(result);
    }
}