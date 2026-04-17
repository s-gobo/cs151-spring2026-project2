package guest;

public class PlatinumGuest extends Guest {
    public PlatinumGuest(String name) {
        super(name, "Platinum");
    }
    
    // First our charge function
	@Override
	public void addCharge(double amount, String reason) {
		// check first if the amount is invalid
		if (amount <= 0) {
			System.out.println("Invalid charge amount!!!");
			return;
		}
        
        amount *= 0.9; // platinum discount
        System.out.println("Platinum discount applied!");

		// now let's affect our balance
		if (currentReservation != null) {
			currentReservation.addCharge(amount, reason);
		} else {
			outstandingBalance += amount;
		}

		System.out.println("Charge amount: $" + amount + "[" + reason + "]");
	}
}
