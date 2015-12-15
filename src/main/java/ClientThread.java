import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by user1 on 15.12.2015.
 */
public class ClientThread extends Thread {

    private  ClientThread[] threads;
    private  Socket clientSocket;
    private PrintStream out;
    private DataInputStream in;
    private int maxClientCount;
    private String clientName;

    public ClientThread(ClientThread[] clientThreads, Socket clientSocket) {
        this.threads = clientThreads;
        this.clientSocket = clientSocket;
        maxClientCount = clientThreads.length;
    }

    @Override
    public void run() {
        try {
            out = new PrintStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            //Считываем имя клиента из in
            String name;
            while (true){
                out.println("Enter your name!");
                name = in.readLine().trim();
                System.out.println(name);
                if(name != null&&name.indexOf("@") == -1){
                    break;
                }
                else {
                    out.println("The name must not contain character @");
                }
            }
            //Инициализируем имя клиента в синхронизированном блоке
            synchronized (this){
                for(int i = 0; i < maxClientCount; i++){
                    if(threads[i] != null && threads[i] == this){
                        threads[i].clientName = name;
                    }
                }
            }
            for(int i = 0; i < maxClientCount; i++){
                if(threads[i] != null && threads[i] != this){
                    threads[i].out.println(clientName + " connected to conversation");
                }
            }

            String message;
            //диалог...
            while (true){
                message = in.readLine().trim();
                if(message.equals("/exit"))break;
                String splitedMessage[] = message.split(" ",2);
                if(message!=null && message.startsWith("@") && splitedMessage.length == 2){

                    String messageWithoutName = splitedMessage[1];
                    String senderName = splitedMessage[0];

                    synchronized (this){
                        for(int i = 0; i < maxClientCount; i++){
                            if(threads[i] != null && threads[i] != this
                            && threads[i].clientName != null
                            && threads[i].clientName.equals(senderName))
                            {
                                threads[i].out.println("private:<" + clientName + ">" +messageWithoutName);
                            }
                        }
                    }
                }
                else {
                    synchronized (this){
                        for(int i = 0; i < maxClientCount; i++){
                            if(threads[i] != null && threads[i] != this){
                                threads[i].out.println("<" + clientName + ">" + message.trim());
                            }
                        }
                    }
                }
            }

            //Оповещаем всех о выходе участника чата
            synchronized (this){
                for(int i = 0; i < maxClientCount; i++){
                    if(threads[i] != null && threads[i] !=this){
                        threads[i].out.println(clientName + " has left the conversation!");
                    }
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                this.out.close();
                this.clientSocket.close();
                this.in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
