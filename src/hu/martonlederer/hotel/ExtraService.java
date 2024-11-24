package hu.martonlederer.hotel;

public class ExtraService {
	private String name;
	private int price;
	
	/**
	 * Extra szolgáltatás konstruktora
	 * @param name Extra szolgáltatás neve
	 * @param price Extra szolgáltatás ára egyszeri alkalomra
	 */
	public ExtraService(String name, int price) {
		if (price <= 0) {
			throw new IllegalArgumentException("Extra service price has to be >0");
		}

		this.name = name;
		this.price = price;
	}
	
	/**
	 * Extra szolgáltatás nevének lekérdezése
	 * @return A szolgáltatás neve
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Extra szolgáltatás árának lekérdezése
	 * @return A szolgáltatás ára
	 */
	public int getPrice() {
		return price;
	}
}
