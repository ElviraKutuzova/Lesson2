package geekbrains.java2.client.model;

import geekbrains.java2.client.Command.Command;
import geekbrains.java2.client.Command.command.AuthCommand;
import geekbrains.java2.client.Command.command.ErrorCommand;
import geekbrains.java2.client.Command.command.MessageCommand;
import geekbrains.java2.client.Command.command.UpdateUsersListCommand;
import geekbrains.java2.client.controller.AuthEvent;
import geekbrains.java2.client.controller.ClientController;
import geekbrains.java2.client.controller.MessageHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

//import static sun.tools.jconsole.Messages.MESSAGE;
//вся логика распологается здесь
public class NetworkService {

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ClientController controller;
    private MessageHandler messageHandler;
//    private Consumer<String> messageHandler;
    private AuthEvent successfulAuthEvent;
    private String nickname;

    public NetworkService (String host, int port){
        this.port = port;
        this.host = host;
    }

    public void connect(ClientController controller) throws IOException{ //подключение к серверу
        this.controller = controller;
        socket = new Socket(host, port);
        in = new ObjectInputStream(socket.getInputStream()); //поток на чтение присваеваем в отдельные переменные
        out = new ObjectOutputStream(socket.getOutputStream());  //поток на запись
        runReadThread(); //запускаем отдельный поток в котором мы будем получать сообщения о нашего сервера
    }

    private void runReadThread(){ // получаем сообщения от сервера
        new Thread(() ->{
            while(true){
                try{
                    Command command = (Command) in.readObject();
                    switch (command.getType()){
                        case AUTH: { //проходим аунтификацию
                            AuthCommand commandData = (AuthCommand) command.getData();
                            nickname = commandData.getUsername();
                            successfulAuthEvent.authIsSuccessful(nickname); //получаем сообщение и далее его анализируем
                            break;
                        }
                        case MESSAGE:{
                            MessageCommand commandData = (MessageCommand) command.getData();
                            if(messageHandler != null){
                                String message = commandData.getMessage();
                                String username = commandData.getUsername();
                                if(username != null){
                                    message = username + ": " + message;
                                }
                                messageHandler.handle(message);
                            }
                            break;
                        }
                        case AUTH_ERROR:
                        case ERROR:{
                            ErrorCommand commandData = (ErrorCommand) command.getData();
                            controller.showErrorMessage(commandData.getErrorMessage());
                            break;
                        }
                        case UPDATE_USERS_LIST:{
                            UpdateUsersListCommand commandData = (UpdateUsersListCommand) command.getData();
                            List<String> users = commandData.getUsers();
                            controller.updateUsersList(users);
                            break;
                        }
                        default:
                            System.err.println("Unknown type of command: " + command.getType());
                    }
                    }catch (IOException e){
                    System.out.println("Поток чтения был прерван!");
                    return;

//                    String message = in.readUTF();
//                    if(message.startsWith("/auth")){
//                        String[] messageParts = message.split("\\s+", 2);
//                        nickname = messageParts[1];
//                        successfulAuthEvent.authIsSuccessful(nickname);
//                    }else if(messageHandler != null){
//                        messageHandler.accept(message);
//                    }
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendCommand(Command command) throws IOException{
        out.writeObject(command);
    }
    public void setMessageHandler(MessageHandler messageHandler){this.messageHandler = messageHandler;}

    public void setSuccessfulAuthEvent(AuthEvent successfulAuthEvent){
        this.successfulAuthEvent = successfulAuthEvent;
    }

//    public void sendAuthMessage(String login, String password) throws IOException{
//        out.writeUTF(String.format("/auth %s %s", login, password));
//    }
//    public void sendMessage(String message) throws IOException{
//        out.writeUTF(message);
//    }
//
//
//    public void setSuccessfulAuthEvent (AuthEvent successfulAuthEvent){
//        this.successfulAuthEvent = successfulAuthEvent;
//    }

    public void close(){
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
