import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by user1 on 15.12.2015.
 */
public class ChatServer {

    private static final int port = 3333;
    private static Socket client;
    private static ServerSocket serverSocket;

    private static PrintStream out;


    public static final int maxClientsCount = 10;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(port);
            while (true){
                client = serverSocket.accept();
                System.out.println(client.hashCode());
                int clientsCount = maxClientsCount;
                for(int i = 0;i < clientsCount; i++){
                    if (threads[i] == null)
                        (threads[i] = new ClientThread(threads,client)).start();

                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("finish");


    }
}
