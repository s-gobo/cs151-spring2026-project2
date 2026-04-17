package ui;

import exception.InvalidDateRangeException;
import exception.OverCapacityException;
import exception.RoomUnavailableException;
import guest.DiamondGuest;
import guest.Guest;
import guest.PlatinumGuest;
import guest.Rating;
import guest.Reservation;
import hotel.Employee;
import hotel.Hotel;
import room.DoubleRoom;
import room.Room;
import room.SingleRoom;
import room.SuiteRoom;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HotelSystemUI {
    private List<Hotel> hotels;
    private Util util;

    public HotelSystemUI(List<Hotel> hotels) {
        this.hotels = hotels;
        this.util = new Util(new Scanner(System.in));
    }

    // ----------------------Main menu----------------------
    public void start() {
        System.out.println("Welcome to the Hotel Management System!\n");
        boolean running = true;

        while (running) {
            int choice = util.showMainMenu();
            if (choice == -1)
                continue;

            switch (choice) {
                case 1:
                    guestMenu();
                    break;
                case 2:
                    employeeLogin();
                    break;
                case 3:
                    System.out.println("Shutting down...");
                    running = false;
                    break;
                default:
                    Util.showError("Invalid option.");
                    break;
            }
        }
        util.close();
    }

    // ----------------------Guest menu----------------------
    private void guestMenu() {
        Hotel hotel = util.selectFromList("Select a Hotel", "Choose a hotel: ", hotels);
        if (hotel == null)
            return;
        // Guest Login
        int guestType = util.showGuestIdentificationMenu();
        if (guestType == -1)
            return;

        Guest guest;
        if (guestType == 1) {
            guest = createNewGuest(hotel);
            if (guest == null)
                return;
        } else if (guestType == 2) {
            System.out.print("Enter your Guest ID: ");
            String guestId = util.readLine();
            guest = hotel.findGuestById(guestId);
            if (guest == null) {
                Util.showError("Guest not found.");
                return;
            }
            System.out.println("Welcome back, " + guest.getName() + "!");
        } else {
            Util.showError("Invalid option.");
            return;
        }

        boolean running = true;
        while (running) {
            int choice = util.showGuestMenu(hotel.getName());
            if (choice == -1)
                continue;

            switch (choice) {
                case 1:
                    handleGuestBooking(hotel, guest);
                    break;
                case 2: // View Reservations
                    util.displayList("Your Reservations", guest.getReservations(), "No active reservations.");
                    break;
                case 3:
                    handleLeaveRating(hotel, guest);
                    break;
                case 4:
                    handleCancelReservation(hotel, guest);
                    break;
                case 5: // View Hotel Rating
                    System.out.println("Average rating: " + hotel.getAverageRating() + "/5");
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    Util.showError("Invalid option.");
                    break;
            }
        }
    }

    // ----------------------Employee menu----------------------
    private void employeeMenu(Employee employee) {
        Hotel hotel = employee.getAssignedHotel();
        boolean running = true;

        while (running) {
            int choice = util.showEmployeeMenu(hotel.getName());
            if (choice == -1)
                continue;

            switch (choice) {
                case 1:
                    addRoom(hotel);
                    break;
                case 2:
                    removeRoom(hotel);
                    break;
                case 3: // View All Reservations
                    util.displayList(hotel.getName() + "-All Reservations", hotel.getReservations(),
                            "No reservations found.");
                    break;
                case 4: // View All Rooms
                    util.displayList(hotel.getName() + "-All Rooms", hotel.getRooms(), "No rooms found.");
                    break;
                case 5:
                    processPayment(hotel);
                    break;
                case 6:
                    handleCheckInOut(hotel, employee, true);
                    break;
                case 7:
                    handleCheckInOut(hotel, employee, false);
                    break;
                case 8:
                    running = false;
                    break;
                default:
                    Util.showError("Invalid option.");
                    break;
            }
        }
    }

    // ----------------------Employee login----------------------

    private void employeeLogin() {
        System.out.println("\n--- Employee Login ---");
        System.out.print("Enter Employee ID: ");
        String employeeId = util.readLine();

        Employee found = null;
        for (Hotel hotel : hotels) {
            found = hotel.findEmployeeById(employeeId);
            if (found != null)
                break;
        }

        if (found == null) {
            Util.showError("Employee not found.");
            return;
        }

        System.out.println("Welcome, " + found.getName() + " (" + found.getRole() + ")");

        employeeMenu(found);
    }

    // ----------------------Employee actions----------------------

    private void addRoom(Hotel hotel) {
        try {
            System.out.println("\n--- Add a Room ---");
            String roomType = util.showRoomTypeMenu();
            if (roomType == null)
                return;

            System.out.print("Base price per night: $");
            double price = Double.parseDouble(util.readLine());

            Room room;
            switch (roomType) {
                case "Single Room":
                    room = new SingleRoom(price);
                    break;
                case "Double Room":
                    room = new DoubleRoom(price);
                    break;
                case "Suite Room":
                    room = new SuiteRoom(price);
                    break;
                default:
                    return;
            }

            hotel.addRoom(room);
            System.out.println("Room added: " + room.getRoomId() + " (" + room.getRoomType() + ")");
        } catch (NumberFormatException e) {
            Util.showError("Please enter a valid number.");
        }
    }

    private void removeRoom(Hotel hotel) {
        Room room = util.selectFromList("Remove a Room", "Select room to remove: ", hotel.getRooms());
        if (room != null) {
            hotel.removeRoom(room);
            System.out.println("Room " + room.getRoomId() + " removed.");
        }
    }

    private void processPayment(Hotel hotel) {
        System.out.println("\n--- Process Payment ---");
        System.out.print("Enter reservation ID: ");
        String id = util.readLine();

        Reservation found = hotel.findReservationById(id);
        if (found == null) {
            Util.showError("Reservation not found.");
            return;
        }

        double balance = found.getOutstandingBalance();
        if (balance <= 0) {
            System.out.println("No outstanding balance for this reservation.");
            return;
        }

        found.processPayment(balance);
        System.out.println("Payment of $" + balance + " processed. Balance: $" + found.getOutstandingBalance());
    }

    private void handleCheckInOut(Hotel hotel, Employee employee, boolean checkIn) {
        String title = checkIn ? "Check In" : "Check Out";
        Reservation r = util.selectFromList(title, "Select reservation number: ", hotel.getReservations());
        if (r != null) {
            if (checkIn)
                employee.checkInGuest(r);
            else
                employee.checkOutGuest(r);
        }
    }

    // ----------------------Guest actions----------------------
    private void handleGuestBooking(Hotel hotel, Guest guest) {
        try {
            System.out.println("\n--- Book a Room ---");
            System.out.println("Room type:");
            String roomType = util.showRoomTypeMenu();
            if (roomType == null)
                return;

            System.out.print("Number of guests: ");
            int guestCount;
            try {
                guestCount = util.readInt();
            } catch (NumberFormatException e) {
                Util.showError("Please enter a valid number.");
                return;
            }
            if (guestCount < 1) {
                Util.showError("Guest count must be at least 1.");
                return;
            }

            System.out.print("Check-in date (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(util.readLine());

            System.out.print("Check-out date (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(util.readLine());

            Reservation reservation = guest.reserveRoom(hotel, roomType, guestCount, checkIn, checkOut);

            if (reservation != null) {
                System.out.println("Booking complete! " + reservation);

                int payChoice = util.showPaymentMenu();
                if (payChoice == -1)
                    return;

                if (payChoice == 1) {
                    reservation.processPayment(reservation.getOutstandingBalance());
                    System.out.println("Payment processed. Balance: $" + reservation.getOutstandingBalance());
                } else if (payChoice == 2) {
                    System.out.println("Payment will be collected at the hotel.");
                } else {
                    Util.showError("Invalid option.");
                }
            }

        } catch (InvalidDateRangeException e) {
            Util.showError("Invalid date range: " + e.getMessage());
        } catch (RoomUnavailableException e) {
            Util.showError("Room unavailable: " + e.getMessage());
        } catch (OverCapacityException e) {
            Util.showError("Over capacity: " + e.getMessage());
        } catch (DateTimeParseException e) {
            Util.showError("Invalid date format. Please use YYYY-MM-DD.");
        } catch (NumberFormatException e) {
            Util.showError("Please enter a valid number.");
        }
    }

    private void handleLeaveRating(Hotel hotel, Guest guest) {
        List<Reservation> ratable = new java.util.ArrayList<>();
        // Find reservations that are checked out and not rated
        for (Reservation r : guest.getReservations()) {
            if (r.isCheckedOut() && !r.isRated())
                ratable.add(r);
        }
        Reservation toRate = util.selectFromList("Rate a Reservation", "Select reservation to rate: ", ratable);
        if (toRate == null)
            return;
        System.out.print("Stars (1-5): ");
        try {
            int stars = util.readInt();
            if (stars < 1 || stars > 5) {
                Util.showError("Stars must be between 1 and 5.");
                return;
            }
            hotel.addRating(new Rating(guest.getName(), stars));
            toRate.setRated(true);
            System.out.println("Thank you for your rating!");
        } catch (NumberFormatException e) {
            Util.showError("Please enter a valid number.");
        }
    }

    private void handleCancelReservation(Hotel hotel, Guest guest) {
        Reservation toCancel = util.selectFromList("Cancel a Reservation", "Select reservation to cancel: ",
                guest.getReservations());
        if (toCancel != null) {
            guest.cancelReservation(toCancel);
            hotel.removeReservation(toCancel);
        }
    }

    private Guest createNewGuest(Hotel hotel) {
        System.out.print("Guest name: ");
        String name = util.readLine();
        if (name.isEmpty()) {
            Util.showError("Name cannot be empty.");
            return null;
        }

        String membership = util.showMembershipMenu();
        if (membership == null)
            return null;
        
        Guest guest = switch (membership) {
            case "Platinum" -> new PlatinumGuest(name);
            case "Diamond" -> new DiamondGuest(name);
            default -> new Guest(name, membership);
        };

        hotel.registerGuest(guest);
        System.out.println("Guest registered: " + guest.getGuestId());
        return guest;
    }
}
