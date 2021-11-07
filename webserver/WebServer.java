package webserver;

import java.net.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringTokenizer;
import java.util.Date;
import java.io.*;

public class WebServer extends Thread {
	protected Socket clientSocket;
	//had to use the full path because the app was being launched from a different folder when running tests
	//replace with File("./TestSite")
	private static final File WEB_ROOT = new File("C:/Users/rares/Projects/College/SVV/svv-project/TestSite");
	private static final String DEFAULT_PAGE = "a.html";
	private static final String NOT_FOUND = "404.html";
	private static String foundPath = null;

	public String getContentType(String fileRequested) {
		if(fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
			return "text/html";
		if(fileRequested.endsWith(".css"))
			return "other/css";
		return "text/plain";
	}

	private static void searchFileTree(String fileRequested) throws IOException {
		Path root = Paths.get(WEB_ROOT.toString());

		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.toString().contains(fileRequested))
					foundPath = file.toString();
				return FileVisitResult.CONTINUE;
			}
		});
		
	}

	public String parseWhiteSpaces(String fileRequested) {
		if (fileRequested.contains("%20")) {
			fileRequested = fileRequested.replace("%20", " ");
		}

		return fileRequested;
	}

	private void fileNotFound(PrintWriter out, OutputStream dataOut) throws IOException {
		File file = new File(WEB_ROOT, NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println();
		out.flush();
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
	}

	public byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];

		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return fileData;
	}

	public static void main(String[] args) throws IOException {
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

	public WebServer(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public void run() {
		System.out.println("New Communication Thread Started");
		PrintWriter out = null;
		BufferedReader in = null;
		BufferedOutputStream dataOut = null;
		String fileRequested = null;

		try {
			out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			dataOut = new BufferedOutputStream(clientSocket.getOutputStream());

			String inputLine = in.readLine();

			StringTokenizer tokens = new StringTokenizer(inputLine);
			String method = tokens.nextToken().toUpperCase();
			fileRequested = parseWhiteSpaces(tokens.nextToken().toLowerCase());

			if (method.equals("GET")) {
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_PAGE;
				}

				File file = new File(WEB_ROOT, fileRequested);
				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);

				byte[] fileData = readFileData(file, fileLength);

				out.println("HTTP/1.1 200 OK");
				out.println("Server: Java HTTP Server");
				out.println("Date: " + new Date());
				out.println("Content-type: " + content);
				out.println("Content-length: " + fileLength);
				out.println();
				out.flush();

				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (FileNotFoundException fnfe) {
			try {
				fileNotFound(out, dataOut);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");
			System.exit(1);
		}
	}
}