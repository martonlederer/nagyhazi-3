package hu.martonlederer.hotel;

public enum HotelRating {
	Basic(1),
	Budget(2),
	Comfortable(3),
	Excellent(4),
	Luxury(5);
	
	private int stars;
	
	/**
	 * Privát konstruktor, ami beállítja, hogy az aktuális
	 * enum érték hány csillagnak felel meg
	 * @param stars Csillag szám
	 */
	private HotelRating(int stars) {
		this.stars = stars;
	}
	
	/**
	 * Csillagok rajzolása
	 * @return Csillag karakterek
	 */
	public String getStars() {
		return "☆".repeat(stars);
	}
}
