/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import echoserver.EchoServer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Benchmade
 */
public class HttpServer
{

    static int port = 8888;
    static String ip = "100.69.134.68";
    static String publicFolder = "public/";

    public static void main(String[] args) throws Exception
    {
        
        if (args.length >= 2)
        {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        if (args.length == 3)
        {
            publicFolder = args[2];
        }
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(ip, port), 0);
        server.createContext("/welcome", new RequestHandler());
        server.createContext("/", new RequestForAFileHandler());
        server.createContext("/log", new Log());
        server.createContext("/online", new Online());

        server.setExecutor(null);
        server.start();

        EchoServer.getInstance();
    }

    static class RequestHandler implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            String response = "Welcome to my very first almost home made Web Server :-)";
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody()))
            {
                pw.print(response); 
            }
        }
    }

    static class RequestForAFileHandler implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
            Map<String, String> typeMap = new HashMap();
            typeMap.put("pdf", "application/pdf");
            typeMap.put("xlxs", "application/vnd.ms-excel");
            typeMap.put("jpg", "image/jpg");
            typeMap.put("html", "text/html");
            typeMap.put("htm", "text/html");
            typeMap.put("css", "text/css");
            typeMap.put("jar", "application/java-archive");
            typeMap.put("png", "image/jpg");
            typeMap.put("ico", "image/x-icon");
            typeMap.put("pdf", "application/pdf");
            typeMap.put("doc", "application/msword");
            typeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            File file;
            String fileName = he.getRequestURI().toString();
            InputStream is = new ByteArrayInputStream(fileName.getBytes());
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String line;
            String lastwords = "";
            String contentType = "";

            while ((line = bf.readLine()) != null)
            {
                if (line.equals("/") && line.length() == 1)
                {
                    contentType = "html";
                    lastwords = "index.html";
                    break;
                }

                String[] words = line.split("/");

                lastwords = words[words.length - 1];
                String[] types = line.split("\\.");
                contentType = types[types.length - 1];
            }

            file = new File(publicFolder + lastwords);
            byte[] bytesToSend = new byte[(int) file.length()];
            try
            {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            }
            catch (IOException ie)
            {
                file = new File(publicFolder + "404.html");
                bytesToSend = new byte[(int) file.length()];
                try
                {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(bytesToSend, 0, bytesToSend.length);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                he.sendResponseHeaders(404, bytesToSend.length);
                try (OutputStream os = he.getResponseBody())
                {
                    os.write(bytesToSend, 0, bytesToSend.length);
                }

            }

            String method = he.getRequestMethod();

            if (method.equals("GET"))
            {
            }
            else if (method.equals("POST"))
            {

                StringBuilder sb = new StringBuilder();
                Scanner scan = new Scanner(he.getRequestBody());
                while (scan.hasNext())
                {
                    sb.append("Request body, with Post-parameters: " + scan.nextLine());
                    sb.append("<br>");
                }

            }

            Headers h = he.getResponseHeaders();
            h.add("Content-type", typeMap.get(contentType));

            he.sendResponseHeaders(200, bytesToSend.length);

            try (OutputStream os = he.getResponseBody())
            {
                os.write(bytesToSend, 0, bytesToSend.length);
            }

        }
    }

    private static class Online implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {
           

            String response;
            List<String> listOfNames = EchoServer.getClientNames();

            StringBuilder sb = new StringBuilder();

            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Online users</title>\n");
            sb.append("<link rel=\"stylesheet\" href=\"style.css\">\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h1 style=\"center\">Show Number of online users</h1>\n");

            sb.append("<div>Number of users online: ").append(listOfNames.size()).append("</div>");
            sb.append("<table border=\"1\">\n");
            sb.append("<tr><th>Name of User</th>\n");

            for (String string : listOfNames)
            {
                sb.append("<tr><td>").append(string).append("</td></tr>\n");
            }
            sb.append("</table>");
            sb.append("<a class=\"btn\" href=\"index.html\">Back</a>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();

            he.getResponseHeaders();

            Headers h = he.getResponseHeaders();

            h.add("Content-Type", "text/html");

            he.sendResponseHeaders(200, response.length());

            try (PrintWriter pw = new PrintWriter(he.getResponseBody()))
            {

                pw.print(response);

            }

        }
    }

    private static class Log implements HttpHandler
    {

        @Override
        public void handle(HttpExchange he) throws IOException
        {

            LogReader fr = new LogReader();

            List<String> list = fr.readFromFile();
            String response;
            StringBuilder sb = new StringBuilder();

            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Chat log</title>\n");
            sb.append("<link rel=\"stylesheet\" href=\"style.css\">\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h1>Chat Log</h1>\n");

            sb.append("<table border=\"1\">\n");
            sb.append("<tr><th>Class</th><th>Event</th>\n");

            for (int i = 0; i < list.size(); i++)
            {
                sb.append("<tr><td>" + list.get(i++) + "</td>");
                sb.append("<td>" + list.get(i) + "</td></tr>");
            }

            sb.append("</table>");
            sb.append("<a class=\"btn\" href =\"/index.html\" > Back </a >");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();

            he.getResponseHeaders();

            Headers h = he.getResponseHeaders();

            h.add("Content-Type", "text/html");

            he.sendResponseHeaders(200, response.length());

            try (PrintWriter pw = new PrintWriter(he.getResponseBody()))
            {

                pw.print(response);

            }

        }
    }
}
