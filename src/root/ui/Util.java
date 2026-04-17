package ui;

import java.util.List;
import java.util.Scanner;

public class Util {
    public static final int MAXIMUM_INSTANCES = 100;

    private Scanner scanner;

    public Util(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine() {
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("EXIT")) {
            System.out.println("Shutting down...");
            System.exit(0);
        }
        return input;
    }

    public int readInt() {
        return Integer.parseInt(readLine());
    }

    public static void showError(String message) {
        System.out.println("ERROR: " + message);
    }

    // For selecting hotels, reservations, etc
    public <T> T selectFromList(String title, String prompt, List<T> items) {
        System.out.println("\n--- " + title + " ---");
        if (items.isEmpty()) {
            showError("No items found.");
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
        System.out.print(prompt);
        try {
            int index = readInt() - 1;
            if (index < 0 || index >= items.size()) {
                showError("Invalid selection.");
                return null;
            }
            return items.get(index);
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return null;
        }
    }

    // For displaying lists of hotels, reservations, etc
    public <T> void displayList(String title, List<T> items, String emptyMsg) {
        if (items.isEmpty()) {
            System.out.println(emptyMsg);
            return;
        }
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
    }

    // Various menus
    public int showMainMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("1. Guest Menu");
        System.out.println("2. Employee Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        try {
            return readInt();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    public int showGuestIdentificationMenu() {
        System.out.println("\n--- Guest Identification ---");
        System.out.println("1. New Guest");
        System.out.println("2. Returning Guest");
        System.out.print("Choose: ");
        try {
            return readInt();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    public int showGuestMenu(String hotelName) {
        System.out.println("\n===== " + hotelName + " - Guest Menu =====");
        System.out.println("1. Book a Room");
        System.out.println("2. View Reservations");
        System.out.println("3. Leave Rating");
        System.out.println("4. Cancel Reservation");
        System.out.println("5. View Hotel Rating");
        System.out.println("6. Back");
        System.out.print("Choose an option: ");
        try {
            return readInt();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    public int showEmployeeMenu(String hotelName) {
        System.out.println("\n===== " + hotelName + " - Employee Menu =====");
        System.out.println("1. Add a Room");
        System.out.println("2. Remove a Room");
        System.out.println("3. View Reservations");
        System.out.println("4. View All Rooms");
        System.out.println("5. Process Payment");
        System.out.println("6. Check In");
        System.out.println("7. Check Out");
        System.out.println("8. Back");
        System.out.print("Choose an option: ");
        try {
            return readInt();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    public String showRoomTypeMenu() {
        System.out.println("  1. Single Room");
        System.out.println("  2. Double Room");
        System.out.println("  3. Suite Room");
        System.out.print("Choose room type: ");
        try {
            switch (readInt()) {
                case 1:
                    return "Single Room";
                case 2:
                    return "Double Room";
                case 3:
                    return "Suite Room";
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return null;
        }
    }

    public String showMembershipMenu() {
        System.out.println("Membership level:");
        System.out.println("  1. PLatinum ( 10% discount) ");
        System.out.println("  2. Diamond ( 20% discount) ");
        System.out.println("  3. Normal (no discount)");
        System.out.print("Choose membership level: ");
        try {
            switch (readInt()) {
                case 1:
                    return "Platinum";
                case 2:
                    return "Diamond";
                case 3:
                    return "Normal";
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return null;
        }
    }

    public int showPaymentMenu() {
        System.out.println("\nPayment method:");
        System.out.println("  1. Pay by Card");
        System.out.println("  2. Pay at Hotel");
        System.out.print("Choose: ");
        try {
            return readInt();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return -1;
        }
    }

    public void close() {
        scanner.close();
    }
}
