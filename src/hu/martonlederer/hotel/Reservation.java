package hu.martonlederer.hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
	private Customer customer;
	private Room room;
	private LocalDate from;
	private LocalDate to;
	private List<ExtraService> extras;
	
	/**
	 * Új foglalás konstruktora
	 * @param customer Ügyfél példány
	 * @param room Szoba példány
	 * @param from Bejelentkezés dátuma
	 * @param to Kijelentkezés dátuma
	 * @param extras Opcionális extra szolgáltatások
	 */
	public Reservation(Customer customer, Room room, LocalDate from, LocalDate to, List<ExtraService> extras) {
		if (!to.isAfter(from)) {
			throw new IllegalArgumentException("Checkout time cannot be before checkin time");
		}
		
		this.customer = customer;
		this.room = room;
		this.from = from;
		this.to = to;
		this.extras = new ArrayList<>(extras);
	}
	
	/**
	 * Foglalás éjszakáinak lekérdezése
	 * @return Éjszakák száma ehhez a foglaláshoz
	 */
	public long getNightsCount() {
		return ChronoUnit.DAYS.between(from, to);
	}
	
	/**
	 * Foglalás teljes árának lekérdezése (extrákkal együtt)
	 * @return A foglalás teljes ára
	 */
	public int getTotalPrice() {		
		int price = (int) getNightsCount() * room.getPrice();
		
		// add prices for extras
		price += extras.stream()
			.map(ExtraService::getPrice)
			.reduce(0, (sum, curr) -> sum + curr);
		
		return price;
	}
	
	/**
	 * Az ügyfél példány lekérdezése
	 * @return A szobát lefoglaló ügyfél példánya
	 */
	public Customer getCustomer() {
		return customer;
	}
	
	/**
	 * A szoba példány lekérdezése
	 * @return A lefoglalt szobatípus példánya
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * A bejelentkezés dátumának lekérdezése
	 * @return Bejelentkezési dátum
	 */
	public LocalDate getCheckinDate() {
		return from;
	}
	
	/**
	 * A kijelentkezés dátumának lekérdezése
	 * @return Kijelentkezési dátum
	 */
	public LocalDate getCheckoutDate() {
		return to;
	}
	
	/**
	 * Extra szolgáltatások lekérdezése
	 * @return Extra szolgáltatások listája
	 */
	public List<ExtraService> getExtras() {
		return extras;
	}
}
