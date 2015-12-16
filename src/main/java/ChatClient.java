import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by user1 on 15.12.2015.
 */
public class ChatClient {
    //класс для приема и отправки сообщений
    public static class ClientAccess extends Observable {
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        public static final int PORT = 2222;
        private static String serverAddres = "localhost";
        private static Socket socket;
        private static OutputStream out;


        public ClientAccess(String server, int port) {
            try {
                socket = new Socket(server,port);

                out = socket.getOutputStream();

                Thread receivingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line;
                            while ((line = reader.readLine())!= null){
                                notifyObservers(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                receivingThread.start();


            } catch (IOException e) {
                notifyObservers(e);
            }
        }

        private static final String CRLF = "\r\n";

        public void send(String message){
            // Отправляем сообщение
            try{
                out.write((message+CRLF).getBytes());
                out.flush();

            }catch (RuntimeException e){
            } catch (IOException e) {
                notifyObservers(e);
            }
            System.out.println("sended " + message);
        }
        public void close(){
            try {
                socket.close();
            } catch (IOException e) {
                notifyObservers(e);
            }
        }
    }
    //GUI клиента для Desktop
    static class ChatFrame extends JFrame implements Observer {
        private JTextArea textArea;
        private JTextField textField;
        private ClientAccess access;
        private JButton sendButton;

        public ChatFrame(ClientAccess access) throws HeadlessException {
            this.access = access;
            access.addObserver(this);
            buildFrame();

        }
        private void buildFrame(){
            textArea = new JTextArea(20,50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea),BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box,BorderLayout.SOUTH);
            textField = new JTextField();
            sendButton = new JButton("Send");

            box.add(textField);
            box.add(sendButton);

            ActionListener sendListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = textField.getText();
                    if(message != null && message.trim().length() > 0){
                        access.send(message);
                    }
                    textField.selectAll();
                    textField.requestFocus();
                    textField.setText("");
                }
            };
            textField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    access.close();
               //     super.windowClosing(e);
                }
            });


        }


        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
            System.out.println("updated " + arg.toString());

        }
    }

    public static void main(String[] args) {
        ClientAccess access = new ClientAccess(ClientAccess.serverAddres,ClientAccess.PORT);
        JFrame frame = new ChatFrame(access);
        frame.setTitle("MyChatApp connected to " + ClientAccess.PORT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }

}
