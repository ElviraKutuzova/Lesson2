package geekbrains.java2;

import geekbrains.java2.auth.AuthService;
import geekbrains.java2.auth.BaseAuthService;
import geekbrains.java2.client.ClientHandler;
import geekbrains.java2.client.Command.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkServer {
    private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final AuthService authService;


    public NetworkServer(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Сервер был успешно запущен на порту " + port);

            authService.start();
            while (true){
                System.out.println("Ожидание клиентского подключения...");

                Socket clientSocket = null;
                clientSocket = serverSocket.accept();

                System.out.println("Клиент подключился!");
                createClientHandler(clientSocket);

            }

        }catch(IOException e){
            System.out.println("Ошибка при работе сервера!");
            e.printStackTrace();
        }finally{
            authService.stop();
        }
    }

    private void createClientHandler(Socket clientSocket){
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.run();

    }

    public AuthService getAuthService() {
        return authService;
    }

    public /*synchronized*/ void broadcastMessage(Command message, ClientHandler owner) throws IOException{
        for(ClientHandler client : clients){
            if (client != owner) {
                client.sendMessage(message); ///???
            }
        }
    }

    public /*synchronized*/ void subscribe(ClientHandler clientHandler) throws IOException{
        clients.add(clientHandler);
        List<String> users = getAllUsernames();
        broadcastMessage(Command.updateUsersListCommand(users), null);
    }

    public /*synchronized*/ void unsubscribe(ClientHandler clientHandler) throws IOException{
        clients.remove(clientHandler);
        List<String> users = getAllUsernames();
        broadcastMessage(Command.updateUsersListCommand(users), null);
    }

    private List<String> getAllUsernames() {
        List<String> usernames = new LinkedList<>();
        for(ClientHandler clientHandler : clients){
            usernames.add(clientHandler.getNickname());
        }
        return usernames;
    }

    public void sendMessage(String receiver, Command commandMessage) throws IOException{
        for(ClientHandler client : clients){
            if(client.getNickname().equals(receiver)){
                client.sendMessage(commandMessage);
                break;
            }
        }
    }

    public boolean isNicknameBusy(String username){
        for(ClientHandler client : clients){
            if(client.getNickname().equals(username)){
                return true;
            }
        }
        return  false;
    }
}
