/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package echoserver;

import echoclient.EchoClient;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author Bogdan
 */
public class ClientHandler extends Thread {
 public Scanner input;
 public PrintWriter writer;
 public Socket socket;
 public String target;
 public String text;
 public String clientName;
    EchoServer server;
    
    
    public ClientHandler(Socket socket, EchoServer server) throws IOException
    {
        
    input = new Scanner(socket.getInputStream());
    writer = new PrintWriter(socket.getOutputStream(), true);
    this.socket=socket;
    this.server=server;
    
  }
    
    public void send(String message)
    {
        writer.println(message);
    }
    
    
    
    @Override
    public void run()
    {
        String message = input.nextLine(); //IMPORTANT blocking call
        //Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
        while (!message.equals(ProtocolStrings.CLOSE))
        {
            if (message.startsWith(ProtocolStrings.CONNECT))

            {
                clientName = message.substring(8);
                server.sendOnlineList(this);

            }

            else if (message.startsWith(ProtocolStrings.SEND))
            { 

                InputStream is = new ByteArrayInputStream(message.getBytes());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = "";
                String[] words = null;
                try
                {
                    while ((line = br.readLine()) != null)
                    {

                        words = line.split("#");

                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (words[1].equals("*"))
                {
                    server.sendAll(message, this);
                }
                else if (words[1].contains(","))
                {
                    line = "";
                    String names[] = null;
                    is = new ByteArrayInputStream(words[1].getBytes());
                    br = new BufferedReader(new InputStreamReader(is));
                    try
                    {
                        while ((line = br.readLine()) != null)
                        {

                            names = line.split(", ");

                        }
                        ArrayList<String> nameListForSending = new ArrayList();
                        for (int i = 0; i < names.length; i++)
                        {
                            nameListForSending.add(names[i]);

                        }
                        server.sendOne(message, this, nameListForSending);

                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                else
                {
                    ArrayList<String> nameListForSending = new ArrayList();
                    nameListForSending.add(words[1]);
                    server.sendOne(message, this, nameListForSending);
                }

            }

            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
            message = input.nextLine(); //IMPORTANT blocking call
        }

        try
        {
            writer.println(ProtocolStrings.CLOSE);//Echo the stop message back to the client for a nice closedown
            server.removeHandler(this);
            socket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, clientName + " has closed a Connection");
    }


    public String getClientName() {
        return clientName;
    }

   
    
    
}
