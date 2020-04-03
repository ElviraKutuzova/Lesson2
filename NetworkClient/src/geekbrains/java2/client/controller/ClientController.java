package geekbrains.java2.client.controller;


import geekbrains.java2.client.model.NetworkService;
import geekbrains.java2.client.view.AuthDialog;
import geekbrains.java2.client.view.ClientChat;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

import static geekbrains.java2.client.Command.Command.*;
//говорит view как отобразить данные

public class ClientController {
    private final NetworkService networkService;
    private final AuthDialog authDialog;
    private final ClientChat clientChat;
    private String nickname;

    public ClientController(String serverHost, int serverPort) { //инициализируем основные компоненты с которыми мы будем работать
        this.networkService = new NetworkService(serverHost, serverPort);
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
    }

    public void runApplication() throws IOException {
        connectToServer(); //пробуем подключится к серверу, подключаемся и запускаем поток на чтение
        runAuthProcess();
    }
    private void runAuthProcess(){
        networkService.setSuccessfulAuthEvent(new AuthEvent() {
            @Override
            public void authIsSuccessful(String nickname) {

                ClientController.this.setUserName(nickname); //передаем контроллеру никнайм
                clientChat.setTitle(nickname); //передаем имя в Title
                ClientController.this.openChat(); //открываем чат
            }
        });
            authDialog.setVisible(true);
//
    }

    private void openChat(){

        authDialog.dispose(); //уничтожаем диалоговое окно в котором мы вводили логин и пароль(при успешной аутентификации)
        networkService.setMessageHandler(new MessageHandler(){
            @Override
            public void handle(String message){clientChat.appendMessage(message);}
        });
//        networkService.setMessageHandler(clientChat::appendMessage);
        clientChat.setVisible(true);
    }

    private void setUserName(String nickname) {this.nickname = nickname;}

    private void connectToServer() throws IOException{
        try{
            networkService.connect(this);
        }catch (IOException e){
            System.err.println("Failed to establish server connection");
            throw e;
        }
    }

    public void sendAuthMessage(String login, String pass) throws IOException{
        networkService.sendCommand(authCommand(login, pass));
    }

    public void sendMessageToAllUsers(String message) {
        try {
            networkService.sendCommand(broadcastMessageCommand(message));
        } catch (IOException e) {
            clientChat.showError("Failed to send message");
//            JOptionPane.showMessageDialog(null, "Failed to sed message!");
            e.printStackTrace();
        }
    }

    public void shutdown(){networkService.close();}


    public String getUsername() {
        return nickname;
    }

    public void sendPrivateMessage(String username, String message){
        try{
            networkService.sendCommand(privateMessageCommand(username, message));
        }catch (IOException e){
            showErrorMessage(e.getMessage());
        }
    }

    public void showErrorMessage(String errorMessage) {
        if(clientChat.isActive()){
            clientChat.showError(errorMessage);
        }
        else if (authDialog.isActive()){
            authDialog.showError(errorMessage);
        }
        System.err.println(errorMessage);
    }

    public void updateUsersList(List<String> users){
        users.remove(nickname);
        users.add(0,"All");
        clientChat.updateUsers(users);
    }


//    public void shutdown() {
//        networkService.close();
//    }


}
