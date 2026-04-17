package guest;

import exception.InvalidDateRangeException;
import exception.OverCapacityException;
import exception.RoomUnavailableException;
import hotel.Hotel;
import room.Room;
import ui.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Guest implements Chargeable {

	private static int nextGuestId = 1;
	private static int totalGuests = 0;

	private String guestId;
	private String name;
	protected String membershipLevel;
	protected double outstandingBalance;
	protected Reservation currentReservation;
	protected List<Reservation> reservations;

	// This is my constructor
	public Guest(String name, String membershipLevel) {
		if (totalGuests >= Util.MAXIMUM_INSTANCES) {
			throw new IllegalStateException("Maximum number of guests reached.");
		}
		totalGuests++;
		this.name = name;
		this.membershipLevel = membershipLevel;
		this.outstandingBalance = 0.0;
		this.guestId = generateGuestId();
		this.reservations = new ArrayList<>();
	}

	// Let's create our core methods

	// function to reserve a room
	public Reservation reserveRoom(Hotel hotel, String roomType, int guestCount, LocalDate checkIn,
			LocalDate checkOut) {

		// Ensure our date is in the right range
		if (checkOut.isBefore(checkIn)) {
			throw new InvalidDateRangeException("Invalid date range.");
		}

		// Let's make sure we have a positive guest count
		if (guestCount <= 0) {
			throw new IllegalArgumentException("Guest count must be positive.");
		}

		// Now, let's find our room
		Room room = hotel.findAvailableRoom(roomType, guestCount);

		if (room == null) {
			throw new RoomUnavailableException("No available room found.");
		}

		if (!room.canFitGuests(guestCount)) {
			throw new OverCapacityException("Too many guests for this room.");
		}

		// Now let's create our reservation
		Reservation reservation = new Reservation(this, room, checkIn, checkOut);
		reservation.confirmReservation();

		this.currentReservation = reservation;
		this.reservations.add(reservation);
		hotel.addReservation(reservation);

		System.out.println("Reservation successful! ID: " + reservation.getReservationId());
		room.markOccupied();

		return reservation;
	}

	public void cancelReservation(Reservation reservation) {
		if (reservation == null || !reservations.contains(reservation)) {
			System.out.println("Reservation not found.");
			return;
		}

		Room room = reservation.getRoom();
		reservation.cancelReservation();

		if (room != null) {
			room.markAvailable();
		}

		reservations.remove(reservation);

		if (reservation == currentReservation) {
			currentReservation = null;
		}

		System.out.println("Reservation cancelled.");
	}

	// Now let's override our methods which were are in the chargeable class

	// First our charge function
	@Override
	public void addCharge(double amount, String reason) {

		// check first if the amount is invalid
		if (amount <= 0) {
			System.out.println("Invalid charge amount!!!");
			return;
		}

		// now let's affect our balance
		if (currentReservation != null) {
			currentReservation.addCharge(amount, reason);
		} else {
			outstandingBalance += amount;
		}

		System.out.println("Charge amount: $" + amount + "[" + reason + "]");
	}

	// Next up our process payment method
	@Override
	public void processPayment(double amount) {

		// check first if the amount is invalid
		if (amount <= 0) {
			System.out.println("Invalid payment amount!!!");
			return;
		}

		// Now let's check if to ensure we don't overpay
		if (amount > outstandingBalance) {
			System.out.println("You are overpaying! Adjusting to full balance");
			amount = outstandingBalance;
		}

		outstandingBalance -= amount;
		System.out.println("Payment Processed: $" + amount);

	}

	// Final function to override, our getter method to get the outstanding balance
	@Override
	public double getOutstandingBalance() {
		return (outstandingBalance);
	}

	// Now let's move onto our getter methods
	public String getGuestId() {
		return guestId;
	}

	public String getName() {
		return name;
	}

	public String getMembershipLevel() {
		return membershipLevel;
	}

	public Reservation getCurrentReservation() {
		return currentReservation;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	// Now unlike the other getters, to generate the nextGuestId I will create a specific function
	public String generateGuestId() {
		return ("GUEST-" + (nextGuestId++));
	}

	// Finally, here is our unique toString
	@Override
	public String toString() {
		String message = ("Guest ID: " + guestId + " | Name: " + name + " | Membership: " +
				membershipLevel + " | Balance: $" + outstandingBalance);

		return (message);

	}

}
