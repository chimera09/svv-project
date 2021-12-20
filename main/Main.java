package main;

import webserver.WebServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        RunWebServer serverSwitch = new RunWebServer();
        Thread serverThread = new Thread(serverSwitch);
        serverThread.start();
    }
}
