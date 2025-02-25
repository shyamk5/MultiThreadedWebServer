import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedWebServer {

    private static final int PORT = 8088;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port "+PORT);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
