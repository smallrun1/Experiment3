package Experiment3s.Database;

import Experiment3s.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactDatabase {
    private static List<Contact> contacts = new ArrayList<>();

    public static List<Contact> getAllContacts() {
        return contacts;
    }

    public static void addContact(Contact contact) {
        contacts.add(contact);
    }

    public static void updateContact(String contactIndex, Contact updatedContact) {
        contacts.set(Integer.parseInt(contactIndex), updatedContact);
    }

    public static void deleteContact(String contactIndex) {
        contacts.remove(contactIndex);
    }
}
