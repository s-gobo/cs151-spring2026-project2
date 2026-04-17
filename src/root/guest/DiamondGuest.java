package guest;

public class DiamondGuest extends Guest {
    public DiamondGuest(String name) {
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
        
        amount *= 0.8; // diamond discount
        System.out.println("Diamond discount applied!");

		// now let's affect our balance
		if (currentReservation != null) {
			currentReservation.addCharge(amount, reason);
		} else {
			outstandingBalance += amount;
		}

		System.out.println("Charge amount: $" + amount + "[" + reason + "]");
	}
}
