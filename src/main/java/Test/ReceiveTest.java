package Test;

import org.junit.Test;

import java.util.HashMap;

public class ReceiveTest {
    String serviceIp = "8.219.9.193";

    @Test
    public void checkMessageTest() {
        String serviceIp = "8.219.9.193";
        HashMap result = new Receive().checkMessage(serviceIp,
                "zs1h4mx4nt5m3pdqtwg3x9mu9e7wgpuyj2qjp7jf7l0cnjeh0gcmmcsz5vp79w6s5vraza677fsvdp");
        System.out.println(result);
    }
}
