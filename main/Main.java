package main;

import webserver.WebServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(10008);
            System.out.println("Connection Socket Created");
            try {
                while (true) {
                    System.out.println("Waiting for Connection");
                    new WebServer(serverSocket.accept());
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }
}
