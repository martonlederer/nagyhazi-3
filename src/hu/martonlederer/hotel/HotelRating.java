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
	
	/**
	 * Csillagok visszaadása számban
	 * @return Számmal való kifejezés
	 */
	public int getStarsCount() {
		return stars;
	}
	
	/**
	 * Érték visszaadása csillagokból
	 * @param stars Csillagok string
	 * @return Enum értéke
	 */
	public static HotelRating fromStars(String stars) {
		int starCount = (int) stars.chars()
			.filter(ch -> ch == '☆')
            .count();

		for (HotelRating rating : HotelRating.values()) {
            if (rating.stars == starCount)
                return rating;
        }

        throw new IllegalArgumentException("No rating with " + stars + " stars found");
	}
}
