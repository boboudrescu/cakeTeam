/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */







import echoclient.EchoClient;
import echoclient.EchoListener;
import echoserver.EchoServer;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import shared.ProtocolStrings;

/**
 * @author Lars Mortensen
 * runs on server from azure
 */
public class TestClient 
{

    String msg;

    public TestClient()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
               // EchoServer.main(null);
            }
        }).start();
    }


    @Before
    public void setUp()
    {
    }

    @Test
    public void sendConnectProtocol() throws IOException, InterruptedException
    {
        EchoClient client = new EchoClient();
        EchoListener listener = new EchoListener()
        {

            @Override
            public void messageArrived(String data)
            {
                msg = data;
            }
        };
        client.registerEchoListener(listener);

        client.connect("137.135.56.173", 9090);
       
        client.send(ProtocolStrings.CONNECT + "Test");
        Thread.sleep(500);
        Assert.assertTrue(msg.startsWith(ProtocolStrings.ONLINE));
        Thread.sleep(50);

        client.send(ProtocolStrings.CLOSE);
        
    }

    @Test
    public void sendSendProtocol() throws IOException, InterruptedException
    {
        EchoClient client = new EchoClient();
        EchoListener listener = new EchoListener()
        {

            @Override
            public void messageArrived(String data)
            {
                msg = data;
            }
        };
        client.registerEchoListener(listener);

        client.connect("137.135.56.173", 9090);
       
        client.send(ProtocolStrings.CONNECT + "Test");
        Thread.sleep(100);

        client.send(ProtocolStrings.SEND + "*#disconnected");
        Thread.sleep(100);
       
        client.send(ProtocolStrings.CLOSE);
        Thread.sleep(500);
    }

    @Test
    public void sendCloseProtocol() throws IOException, InterruptedException
    {
        EchoClient client = new EchoClient();
        EchoListener listener = new EchoListener()
        {

            @Override
            public void messageArrived(String data)
            {
                msg = data;
            }
        };
        client.registerEchoListener(listener);

        client.connect("137.135.56.173", 9090);
      
        client.send(ProtocolStrings.CONNECT + "Test");
        Thread.sleep(100);

        client.send(ProtocolStrings.CLOSE);

        Thread.sleep(100);
       
        Thread.sleep(500);
    }

}
