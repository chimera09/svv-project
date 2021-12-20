package main;

import webserver.WebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class RunWebServer implements Runnable {
    private volatile ServerSocket serverSocket = null;
    private InetAddress ip;
    private int port = 10008;
    private String webRoot = null;

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public String getIPAddress() {
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ip.getHostAddress();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Connection Socket Created");
            try {
                while (true) {
                    System.out.println("Waiting for connection on port " + this.port);
                    if (webRoot == null)
                        new WebServer(serverSocket.accept());
                    else
                        new WebServer(serverSocket.accept(), webRoot);
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
            }
        } catch (
                IOException e) {
            System.err.println("Could not listen on port: " + this.port + ".");
            System.exit(1);
        }
    }

    public void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Could not close port: " + this.port + ".");
            System.exit(1);
        }
    }
}
