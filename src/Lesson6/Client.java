package Lesson6;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String SERVER_ADD = "localhost";
    private final int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client() throws IOException {
        try{
            openConnection();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void openConnection() throws IOException{
        new Thread(new Runnable() {
            Thread inputThread = null;
            @Override
            public void run() {
                try{
                    socket = new Socket(SERVER_ADD, SERVER_PORT);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Соединение успешно");
                    inputThread = runInputThread(in);
                    runOutputLoop(out);
                }catch (IOException e){
                    e.printStackTrace();
                }
                finally {
                    if (inputThread != null) inputThread.interrupt();
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    private void runOutputLoop(DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            String message = scanner.next();
            out.writeUTF(message);
            if (message.equals("/end")){
                break;
            }
        }
    }
    private Thread runInputThread(DataInputStream in) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = in.readUTF();
                    System.out.println("Server: " + message);
                } catch (IOException e) {
                    System.out.println("Соединение закрыто");
                    break;
                }
            }
        });
        thread.start();
        return thread;
    }

    public static void main(String[] args) throws IOException {

        new Client();
    }
}
