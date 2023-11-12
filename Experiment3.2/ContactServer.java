package Experiment3s.Server;

import Experiment3s.Contact;
import Experiment3s.Database.ContactDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ContactServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("服务器启动. 等待客户端启动...");

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
                        List<Contact> contacts = ContactDatabase.getAllContacts();
                        out.writeObject(contacts);
                    } else if (command.equals("ADD_CONTACT")) {
                        Contact newContact = (Contact) in.readObject();
                        ContactDatabase.addContact(newContact);
                    } else if (command.equals("UPDATE_CONTACT")) {
                        Contact updatedContact = (Contact) in.readObject();
                        String contactName = (String) in.readObject(); // 接收联系人名字
                        ContactDatabase.updateContact(contactName, updatedContact); // 使用名字进行更新
                    } else if (command.equals("DELETE_CONTACT")) {
                        String contactName = (String) in.readObject(); // 接收联系人名字
                        ContactDatabase.deleteContact(contactName); // 使用名字进行删除
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
