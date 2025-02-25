import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("New client connected. Handled by thread: "+Thread.currentThread().getName());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)){

            String requestLine = in.readLine();
            if(requestLine != null && requestLine.startsWith("GET")) {
                handleRequest(requestLine, out);
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//run

    private void handleRequest(String requestLine, PrintWriter out) {
        String[] tokens = requestLine.split(" ");
        String requestedFile = tokens.length > 1 ? tokens[1].substring(1):"index.html";

        File file = new File(requestedFile);
        if(file.exists() && !file.isDirectory()) {
            sendResponse(200, "OK", file, out);
        } else {
            send404Response(out);
        }
    }//handleRequest

    private void sendResponse(int statusCode, String msg, File file, PrintWriter out) {
        try(BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            out.println("HTTP/1.1 " + statusCode + " " + msg);
            out.println("Content-Type: text/html");
            out.println();
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }//sendResponse

    private void send404Response(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>404 - File Not Found</h1></body></html>");
    }//send404Response


}
