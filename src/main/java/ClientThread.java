import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by user1 on 15.12.2015.
 */
public class ClientThread extends Thread {

    private final ClientThread[] clientThreads;
    private final Socket clientSocket;
    private PrintStream out;
    private DataInputStream in;
    private int maxClientCount;

    public ClientThread(ClientThread[] clientThreads, Socket clientSocket) {
        this.clientThreads = clientThreads;
        this.clientSocket = clientSocket;
        maxClientCount = clientThreads.length;
    }

    @Override
    public void run() {
        try {
            out = new PrintStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            //Присваиваем имя клиенту
            String name;
            while (true){
                out.println("Enter your name!");
                name = in.readLine().trim();
                if(name.indexOf("@") == -1){
                    break;
                }
                else {
                    out.println("The name must not contain character @");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
