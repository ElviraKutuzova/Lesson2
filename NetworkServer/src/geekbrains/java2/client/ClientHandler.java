package geekbrains.java2.client;

import geekbrains.java2.NetworkServer;
import geekbrains.java2.TimeoutBlock;
import geekbrains.java2.client.Command.Command;
import geekbrains.java2.client.Command.CommandType;
import geekbrains.java2.client.Command.command.AuthCommand;
import geekbrains.java2.client.Command.command.BroadcastMessageCommand;
import geekbrains.java2.client.Command.command.PrivateMessageCommand;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private final NetworkServer networkServer;
    private final Socket clientSocket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String nickname;

    public ClientHandler(NetworkServer networkServer, Socket socket){
        this.networkServer = networkServer;
        this.clientSocket = socket;
//        timeOutThread = new Thread(timeOutThread);
    }

    public void run(){
        doHandle(clientSocket);
    }



    private void doHandle(Socket socket){
        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            new Thread(()->{
                try {
                    authentication();
                    readMessages();
                }catch(IOException e){
                    System.out.println("Соединение с клиентом " + nickname + " было закрыто!");
                }finally {
                    closeConnection();
                }
            }).start();

        }catch(IOException e){
            e.printStackTrace();
        }
    }



    private void closeConnection() {

        try {
            networkServer.unsubscribe(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException{
        while (true){
           Command command = readCommand();
            if(command == null){
                continue;
            }
            switch (command.getType()){
                case END:
                    System.out.println("Received 'END' command" );
                    return;
                case PRIVATE_MESSAGE:{
                    PrivateMessageCommand commandData = (PrivateMessageCommand) command.getData();
                    String receiver = commandData.getReceiver();
                    String message = commandData.getMessage();
                    networkServer.sendMessage(receiver, Command.messageCommand(nickname, message));
                    break;
                }
                case BROADCAST_MESSAGE:{
                    BroadcastMessageCommand commandData = (BroadcastMessageCommand) command.getData();
                    String message = commandData.getMessage();
                    networkServer.broadcastMessage(Command.messageCommand(nickname, message), this);
                    break;
                }
                default:
                    System.err.println("Unknown type of command: " + command.getType());
            }
//            String message = in.readUTF();
//            System.out.printf("От %s: %s%n", nickname, message);
//            if("/end".equals(message)){
//                return;
//            }
//            networkServer.broadcastMessage(nickname + ": " + message, this);
        }
    }

    private Command readCommand()throws IOException{
        try {
            return (Command) in.readObject();
        }catch (ClassNotFoundException e){
            String errorMessage = "Unknown type of object from client!";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

//    public static boolean taskComplete = false;
////    TimeOut timeOut = new TimeOut();
////    Thread timeOutThread = new Thread();
//
//    public static class TimeOut implements Runnable{
//        public void run() {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(taskComplete ==false) {
//                System.out.println("Timed Out");
//                return;
//            }
//
//        }
//
//    }
//    private void authenticationTime() throws IOException {
//        TimeOut timeOut = new TimeOut();
//        Thread timeOutThread = new Thread(timeOutThread);
//        timeOutThread.start();
//        authentication();
//        taskComplete =true;
//        while(true) {//do all other stuff }
//        }
//    }

    private void authentication() throws IOException{

                    while (true) {
                        Command command = readCommand();
                        if (command == null) {
                            continue;
                        }
                        if (command.getType() == CommandType.AUTH) {
                            boolean successfulAuth = processAuthCommand(command);
                            if (successfulAuth) {
                                return;
                            }
                        } else {
                            System.err.println("Unknown type of command for auth process: " + command.getType());
                        }
                    }

                                //            String message = in.readUTF();
//            if(message.startsWith("/auth")){
//                String[] messageParts = message.split("\\s+", 3);
//                String login = messageParts[1];
//                String password = messageParts[2];
//                String  username = networkServer.getAuthService().getUsernameByLoginAndPassword(login, password);
//                if(username == null){
//                    sendMessage("Учетная запись по данному логину и паролю отсутствует");
//                }else{
//                    nickname = username;
//                    networkServer.broadcastMessage(nickname + "  зашел в чат!", this);
//                    sendMessage("/auth" + nickname);
//                    networkServer.subscribe(this);
//                    break;
//                }


    }

    private boolean processAuthCommand(Command command) throws IOException{
        AuthCommand commandData = (AuthCommand) command.getData();
        String login = commandData.getLogin();
        String password = commandData.getPassword();
        String username = networkServer.getAuthService().getUsernameByLoginAndPassword(login, password);
        if( username == null){
            Command authErrorCommand = Command.authErrorCommand("Отсутствует учетная запись по данному логину и паролю!");
            sendMessage(authErrorCommand);
            return false;
        } else if(networkServer.isNicknameBusy(username)){
            Command authErrorCommand = Command.authErrorCommand("Данный пользователь уже авторизован!");
            sendMessage(authErrorCommand);
            return false;
        }
        else{
            nickname = username;
            String message = nickname + "зашел в чат!";
            networkServer.broadcastMessage(Command.messageCommand(null, message), this);
            commandData.setUsername(nickname);
            sendMessage(command);
            networkServer.subscribe(this);
            return true;
        }
    }

    public void sendMessage(Command command) throws IOException{
        out.writeObject(command);
    }

    public String getNickname() {return nickname;}
}
