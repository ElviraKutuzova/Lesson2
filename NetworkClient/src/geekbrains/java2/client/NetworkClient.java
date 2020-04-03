package geekbrains.java2.client;

import geekbrains.java2.client.controller.ClientController;

import java.io.IOException;

public class NetworkClient {
    public static void main(String[] args) {
        try {
            ClientController clientController = new ClientController("localhost", 8189); //создаем контроллер и туда передаем хост и порт
            clientController.runApplication(); //вызываем метод старта

        } catch (IOException e) {
            System.err.println("Failed to connect to server! Please, check you network settings");
//
        }
    }
}