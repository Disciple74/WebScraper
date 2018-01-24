package com.company;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;

public class WebScraper {

    public static void main(String[] args) {
	// write your code here
        try {
            String header = null;
            if (args.length == 0){
                header = readHeader(System.in);
            } else {
                FileInputStream file = new FileInputStream(args[0]);
                header = readHeader(file);
                file.close();
            }
            System.out.println("Header: \n" + header);
            String answer = sendRequest(header);
            System.out.println("Server's response: \n");
            System.out.write(answer.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.getCause().printStackTrace();
        }
    }

    public static String readHeader(InputStream stream) throws IOException {
        byte[] buffer = new byte[64*1024];
        int length = stream.read(buffer);
        String result = new String(buffer, 0, length);
        return result;
    }

    public static String sendRequest(String header) throws Exception{
        String host = null;
        int port = 0;
        try{
            host = getHost(header);
            port = getPort(host);
            host = getHostWithoutPort(host);
        }catch (Exception e) {
            throw new Exception("Не удалось получить адрес сервера!", e);
        }
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            System.out.println("Socket's created: " + host + " : " + port);
            socket.getOutputStream().write(header.getBytes());
            System.out.println("Header is sent!");
        } catch (Exception e) {
            throw new Exception("Ошибка при отправке запроса: " + e.getMessage(), e);
        }
        String res = null;
        try {
            InputStreamReader stream = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(stream);
            StringBuffer strb = new StringBuffer();
            int ch = bufferedReader.read();
            while (ch != -1) {
                strb.append((char) ch);
                ch = bufferedReader.read();
            }
            res = strb.toString();
        } catch (Exception e) {
            throw new Exception("Ошибка при чтении с сервера.", e);
        }
        socket.close();
        return res;
    }

    private static String getHost(String header) throws ParseException {
        final String host = "Host: ";
        final String normalEnd = "\n";
        final String msEnd = "\r\n";

        int s = header.indexOf(host, 0);
        if (s < 0) {
            return "localhost";
        }
        s += host.length();
        int e = header.indexOf(normalEnd, s);
        e = (e > 0) ? e : header.indexOf(msEnd, s);
        if (e < 0) {
            throw new ParseException(
                    "В заголовке запроса не найдено " +
                            "закрывающих символов после пункта Host.",
                    0);
        }
        String res = header.substring(s, e).trim();
        return res;
    }

    private static int getPort(String hostWithPort) {
        int port = hostWithPort.indexOf(":", 0);
        port = (port < 0) ? 80 : Integer.parseInt(hostWithPort
                .substring(port + 1));
        return port;
    }

    private static String getHostWithoutPort(String hostWithPort) {
        int portPosition = hostWithPort.indexOf(":", 0);
        if (portPosition < 0) {
            return hostWithPort;
        } else {
            return hostWithPort.substring(0, portPosition);
        }
    }
}

