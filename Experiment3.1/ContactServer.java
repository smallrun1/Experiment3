package Experiment3f;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ContactServer {
    private static List<Contact> contacts = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());

                while (true) {
                    String command = (String) in.readObject();
                    if (command.equals("EXIT")) {
                        break;
                    } else if (command.equals("GET_CONTACTS")) {
                        out.writeObject(contacts);
                    } else if (command.equals("ADD_CONTACT")) {
                        Contact newContact = (Contact) in.readObject();
                        contacts.add(newContact);
                    } else if (command.equals("UPDATE_CONTACT")) {
                        Contact updatedContact = (Contact) in.readObject();
                        int contactIndex = Integer.parseInt((String) in.readObject());
                        contacts.set(contactIndex, updatedContact);
                    } else if (command.equals("DELETE_CONTACT")) {
                        int contactIndex = Integer.parseInt((String) in.readObject());
                        contacts.remove(contactIndex);
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
