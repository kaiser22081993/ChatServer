import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by user1 on 15.12.2015.
 */
public class ClientThread extends Thread {

    private  ClientThread[] threads;
    private  Socket clientSocket = null;
    private PrintStream os = null;
    private DataInputStream is = null;
    private int maxClientsCount;
    private String clientName = null;
    public static final String CRLF = "\r\n";

    public ClientThread(Socket clientSocket, ClientThread[] clientThreads) {

        this.clientSocket = clientSocket;
        this.threads = clientThreads;
        maxClientsCount = clientThreads.length;
    }

    @Override
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;
        try {
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
            //Считываем имя клиента из is
            String name;
            while (true){
                os.println("Enter your name!" + CRLF);
                name = is.readLine().trim();
                System.out.println(name);
                if(name!=null && name.indexOf("@") == -1){
                    break;
                }
                else {
                    os.println("The name must not contain character @");
                }
            }
            os.println("Welcome: <" + name + "> !!!");
            //Инициализируем имя клиента в синхронизированном блоке
            synchronized (this){
                for(int i = 0; i < maxClientsCount; i++){
                    if(threads[i] != null && threads[i] == this){
                        threads[i].clientName = "@" + name;
                    }
                }
            }
            for(int i = 0; i < maxClientsCount; i++){
                if(threads[i] != null && threads[i] != this){
                    threads[i].os.println(clientName + " connected to conversation");
                }
            }

            String message;
            //диалог...
            while (true){
                message = is.readLine().trim();
                if(message.equals("/exit"))break;
                String splitedMessage[] = message.split(" ",2);
                if(message!=null && message.startsWith("@") && splitedMessage.length == 2){

                    String messageWithoutName = splitedMessage[1];
                    String senderName = splitedMessage[0];

                    synchronized (this){
                        for(int i = 0; i < maxClientsCount; i++){
                            if(threads[i] != null
                            && threads[i].clientName != null
                            && threads[i].clientName.equals(senderName))
                            {
                                threads[i].os.println("private:<" + clientName + ">" + messageWithoutName);
                            }
                        }
                    }
                }
                else {
                    synchronized (this){
                        for(int i = 0; i < maxClientsCount; i++){
                            if(threads[i] != null ){
                                threads[i].os.println("<" + clientName + ">" + message.trim());
                            }
                        }
                    }
                }
            }

            //Оповещаем всех о выходе участника чата
            synchronized (this){
                for(int i = 0; i < maxClientsCount; i++){
                    if(threads[i] != null && threads[i] !=this){
                        threads[i].os.println(clientName + " has left the conversation!");
                    }
                }
            }



        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                this.os.close();
                this.clientSocket.close();
                this.is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
