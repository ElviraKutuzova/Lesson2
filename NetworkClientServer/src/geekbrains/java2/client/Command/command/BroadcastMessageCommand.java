package geekbrains.java2.client.Command.command;

import java.io.Serializable;

public class BroadcastMessageCommand implements Serializable {

    private final String message;

    public BroadcastMessageCommand(String message) {this.message = message;}

    public String getMessage(){return message;}
}
