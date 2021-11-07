package tests.webserver;

import java.net.Socket;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import webserver.WebServer;

public class TestWebServer {
    private WebServer webserver;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    @Before
    public void setUp() {
        serverSocket = null;

        try {
			serverSocket = new ServerSocket(10008);
			System.out.println("Connection Socket Created");
			try {
				System.out.println("Waiting for Connection");
                clientSocket = serverSocket.accept();
				webserver = new WebServer(clientSocket);
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: 10008.");
			System.exit(1);
		} finally {
			try {
                System.out.println("Closing sockets for tests");
				serverSocket.close();
                clientSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 10008.");
				System.exit(1);
			}
		}
	}

    @Test
    public void testGetContentType() {
        assertTrue("Content type function did not return text/html", webserver.getContentType("a.html").equals("text/html"));
        assertTrue("Content type function did not return other/css", webserver.getContentType("a.css").equals("other/css"));
        assertTrue("Content type function did not return text/plain", webserver.getContentType("asdasdasd").equals("text/plain"));
    }

    @Test
    public void testParseWhiteSpaces() {
        assertTrue("%20 was not replaced with space", webserver.parseWhiteSpaces("test%20whitespace").equals("test whitespace"));
        assertTrue("Error when string doesn't contain %20", webserver.parseWhiteSpaces("testwhitespace").equals("testwhitespace"));
        assertTrue("Error when space is present in string", webserver.parseWhiteSpaces("test whitespace").equals("test whitespace"));
    }

    @Test
    public void testReadFileData() throws IOException {
        File WEB_ROOT = new File("C:/Users/rares/Projects/College/SVV/svv-project/TestSite");
        File file = new File(WEB_ROOT, "a.html");
        int fileLength = (int) file.length();
        assertNotNull("File was not found", webserver.readFileData(file, fileLength));
    }
}
