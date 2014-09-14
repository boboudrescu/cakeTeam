package echoclient;

import echoserver.EchoServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;

public class EchoClient extends Thread implements EchoListener {

    public List<EchoListener> listeners = new ArrayList();
    public Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;

    public boolean connect(String address, int port) {
        try {
            this.port = port;
            serverAddress = InetAddress.getByName(address);
            socket = new Socket(serverAddress, port);
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
            start();
            return true;
        } catch (IOException ex) {
            return false;
           
        }
    }

    public void send(String msg) {
        output.println(msg);
    }

  
    @Override
    public void run() {
        String msg = input.nextLine();
        while (!msg.equals("CLOSE#"))
        {
            notifyListeners(msg);
            msg = input.nextLine();
            Logger.getLogger(EchoClient.class.getName()).log(Level.INFO, "Client received message :" + msg);

        }
        try
        {notifyListeners(msg);
            stop();
            socket.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void registerEchoListener(EchoListener l) {
        listeners.add(l);
    }

    public void unRegisterEchoListener(EchoListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String msg) {
         for (EchoListener echoListener : listeners)
        {
            echoListener.messageArrived(msg);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 9090;
        String ip = "localhost";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        
        
        
        
    }

    @Override
    public void messageArrived(String data) {
        System.out.println(data);
    }

}
