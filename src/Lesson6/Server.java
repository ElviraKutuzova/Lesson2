package Lesson6;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        new Server().start();
    }
    public void start() throws IOException{
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        Thread inputThread = null;
        try{
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидается подключение...");
            clientSocket = serverSocket.accept();
            System.out.println("Клиент подключился");
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            inputThread = runInputThread(in);
            runOutputLoop(out);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (serverSocket != null) serverSocket.close();
            if(clientSocket != null) clientSocket.close();
            if(inputThread != null) inputThread.interrupt();
        }

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
                    System.out.println("Client: " + message);
                } catch (IOException e) {
                    System.out.println("Соединение закрыто");
                    break;
                }
            }
        });
        thread.start();
        return thread;
    }
}
