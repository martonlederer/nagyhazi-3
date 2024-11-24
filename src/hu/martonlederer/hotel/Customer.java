package hu.martonlederer.hotel;

public class Customer {
	private String name;
	private String email;
	private String phoneNumber;
	private int points;
	
	/**
	 * Új ügyfél konstruktora
	 * @param name Ügyfél teljes neve
	 * @param email Ügyfél email címe
	 * @param phoneNumber Ügyfél telefonszáma
	 */
	public Customer(String name, String email, String phoneNumber) {
		// meghívjuk a meglévő ügyfél constructor-t 0 ponttal
		this(name, email, phoneNumber, 0);
	}
	
	/**
	 * Meglévő ügyfél konstruktora (van már pontja)
	 * @param name Ügyfél teljes neve
	 * @param email Ügyfél email címe
	 * @param phoneNumber Ügyfél telefonszáma
	 * @param points Ügyfél aktuális pontszáma
	 */
	public Customer(String name, String email, String phoneNumber, int points) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.points = points;
	}
	
	/**
	 * Ügyfél nevének lekérdezése
	 * @return Az ügyfél teljes neve
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Ügyfél email címének lekérdezése
	 * @return Az ügyfél e-mail címe
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Ügyfél telefonszámának lekérdezése
	 * @return Az ügyfél telefonszáma
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * Ügyfél pontszámának lekérdezése
	 * @return Az ügyfél pontszáma az eddigi foglalásokból
	 */
	public int getPoints() {
		return points;
	}
	
	/**
	 * Pontszám hozzáadása az ügyfél meglévő pontjaihoz
	 * @param v Pontmennyiség ami hozzáadásra kerül
	 */
	public void addPoints(int v) {
		// nem lehet nullánál kisebb az ügyfél pontszáma
		if (this.points + v < 0) v = this.points;
		this.points += v;
	}
}
