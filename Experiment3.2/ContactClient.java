package Experiment3s;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ContactClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private JFrame frame;
    private JTextArea resultTextArea;
    private JComboBox<String> commandComboBox;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    public ContactClient() {
        frame = new JFrame("Contact Management Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);
        frame.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        commandComboBox = new JComboBox<>(new String[]{"GET_CONTACTS", "ADD_CONTACT", "UPDATE_CONTACT", "DELETE_CONTACT", "VIEW_CONTACT"});
        commandComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCommand = (String) commandComboBox.getSelectedItem();
                if (selectedCommand != null) {
                    performCommand(selectedCommand);
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Name: "));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email: "));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Phone: "));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitContact();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteContact();
            }
        });

        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllContacts();
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateContact();
            }
        });

        inputPanel.add(submitButton);
        inputPanel.add(deleteButton);
        inputPanel.add(showAllButton);
        inputPanel.add(updateButton);

        frame.add(commandComboBox, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void performCommand(String command) {
        try {
            out.writeObject(command);

            switch (command) {
                case "GET_CONTACTS":
                    List<Contact> contacts = (List<Contact>) in.readObject();
                    displayContacts(contacts);
                    break;
                case "ADD_CONTACT":
                case "UPDATE_CONTACT":
                case "DELETE_CONTACT":
                case "VIEW_CONTACT":
                    // Handle other commands as needed
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void submitContact() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
            Contact newContact = new Contact(name, email, phone);
            try {
                out.writeObject("ADD_CONTACT");
                out.writeObject(newContact);
                displayContact(newContact);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
        }
    }

    private void deleteContact() {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            try {
                out.writeObject("DELETE_CONTACT");
                out.writeObject(name);
                JOptionPane.showMessageDialog(frame, "Contact deleted.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a name for deletion.");
        }
    }

    private void showAllContacts() {
        try {
            out.writeObject("GET_CONTACTS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateContact() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (!name.isEmpty()) {
            try {
                Contact updatedContact = new Contact(name, email, phone);
                out.writeObject("UPDATE_CONTACT");
                out.writeObject(updatedContact);
                out.writeObject(name);
                JOptionPane.showMessageDialog(frame, "Contact updated.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please enter a name for updating.");
        }
    }

    private void displayContact(Contact contact) {
        resultTextArea.append("Submitted Contact Information:\n");
        resultTextArea.append("Name: " + contact.getName() + "\n");
        resultTextArea.append("Email: " + contact.getAddress() + "\n");
        resultTextArea.append("Phone: " + contact.getPhone() + "\n");
        resultTextArea.append("----------------------------\n");
    }

    private void displayContacts(List<Contact> contacts) {
        resultTextArea.setText("Contacts:\n");
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            resultTextArea.append("Index: " + i + "\n");
            resultTextArea.append(contact.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ContactClient();
            }
        });

        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
