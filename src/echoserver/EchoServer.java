package echoserver;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;


public class EchoServer extends Thread{

private List<ClientHandler> clientHandlers= new ArrayList();
  private static boolean keepRunning = true;
  private static ServerSocket serverSocket;
  public static ArrayList<String> names;
  private static EchoServer instance = null;
  private static final Properties properties = Utils.initProperties("server.properties");    
    
  public EchoServer() {

        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");
        String logFile = properties.getProperty("logFile");
        clientHandlers = new ArrayList();
        

        Utils.setLogFile(logFile, EchoServer.class.getName());

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started");
        if (keepRunning == true) {
            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(ip, port));
                keepRunning = true;
                do {
                    Socket socket = serverSocket.accept(); //Important Blocking call
                    Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                    ClientHandler clientHandler = new ClientHandler(socket, this);
                    clientHandler.start();
                    clientHandlers.add(clientHandler);

                } while (keepRunning);
            } catch (IOException ex) {
                Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                keepRunning = false;
                Utils.closeLogger(EchoServer.class.getName());
            }
        }
    }
    
    
  
  
   
  public static void stopServer() {
    keepRunning = false;
  }

  
  
  public void addHandler(ClientHandler ch)
  {
      clientHandlers.add(ch);
      names.add(ch.clientName);
  }
  
  
  public void removeHandler(ClientHandler ch)
  {
     clientHandlers.remove(ch);
     updateOnlineList();
  }
  
  public void sendAll(String msg, ClientHandler ch) {
        if (ch.getClientName()!= null) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.send(ProtocolStrings.MESSAGE + ch.getClientName() + msg.substring(msg.lastIndexOf("#")));

                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, msg + " " + ch.getClientName() + " sent to " + clientHandler.getClientName() + " messege :" + msg.substring(msg.lastIndexOf("#") + 1));

            }
        }
        else
        Logger.getLogger(EchoServer.class.getName()).log(Level.WARNING, "no name found");
    }

    public void sendOne(String msg, ClientHandler ch, ArrayList<String> namesForSending) {
        if (ch.getClientName() != null) {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.getClientName();
                if (namesForSending.contains(clientHandler.getClientName())) {
                    clientHandler.send(ProtocolStrings.MESSAGE + ch.getClientName() + msg.substring(msg.lastIndexOf("#")));

                    Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, msg + " " + ch.getClientName() + " sent: " + msg.substring(msg.lastIndexOf("#")));
                }

            }
        }
        else
        Logger.getLogger(EchoServer.class.getName()).log(Level.WARNING, "no name found");
    }

    public void send(String msg, ClientHandler ch) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != ch) {
                clientHandler.send(ProtocolStrings.MESSAGE + ch.getClientName() + msg.toUpperCase());
            }
        }
    }

     public void updateOnlineList() {
        String onlineList = ProtocolStrings.ONLINE;

        for (int i = 0; i < clientHandlers.size(); i++) {
            String user = clientHandlers.get(i).getClientName();
            if (i == clientHandlers.size() - 1) {
                onlineList += user;
            }
            else {
                onlineList += user + ",";
            }

        }

        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send(onlineList);
        }

    }
    
    public void sendOnlineList(ClientHandler name) {
        String onlineList = ProtocolStrings.ONLINE;

        

        for (int i = 0; i < clientHandlers.size(); i++) {
            String user = clientHandlers.get(i).getClientName();
            if (i == clientHandlers.size() - 1) {
                onlineList += user;
            }
            else {
                onlineList += user + ",";
            }

        }
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, name.getClientName() + " logged in");

        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.send(onlineList);
        }

    }

   static public  ArrayList<String> getClientNames() {
       
        return names;
    }
    
  public static EchoServer getInstance() {
       if (instance == null) {
           instance = new EchoServer();
        }
        return instance;
    } 

  public static void main(String[] args) {
    EchoServer server = EchoServer.getInstance();
    
  
}
}