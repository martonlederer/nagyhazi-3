package hu.martonlederer.hotel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Hotel {
	private String name;
	private String description;
	private HotelRating rating;
	private String location;
	
	private Set<Room> rooms;
	private Set<ExtraService> extras;
	private Set<Customer> customers;
	
	private List<Reservation> reservations;
	
	/**
	 * Hotel példány konstruktora
	 * @param name Hotel neve
	 * @param description Hotel rövid leírása
	 * @param rating Hotel minősítése
	 * @param location Hotel fizikai helye
	 * @param rooms Az összes szobatípus ebben a hotelben
	 * @param extras A hotelben elérhető extrák
	 * @param customers Létező ügyfelek
	 * @param reservations Létező foglalások
	 */
	public Hotel(
		String name,
		String description,
		HotelRating rating,
		String location,
		Set<Room> rooms,
		Set<ExtraService> extras,
		Set<Customer> customers,
		List<Reservation> reservations
	) {
		this.name = name;
		this.description = description;
		this.rating = rating;
		this.location = location;
		
		this.rooms = new HashSet<>(rooms);
		this.extras = new HashSet<>(extras);
		
		this.customers = new HashSet<>(customers);
		this.reservations = new ArrayList<>(reservations);
	}
	
	/**
	 * Hotel példány konstruktora (setuphoz)
	 * @param name Hotel neve
	 * @param description Hotel rövid leírása
	 * @param rating Hotel minősítése
	 * @param location Hotel fizikai helye
	 * @param rooms Az összes szobatípus ebben a hotelben
	 * @param extras A hotelben elérhető extrák
	 */
	public Hotel(
		String name,
		String description,
		HotelRating rating,
		String location,
		Set<Room> rooms,
		Set<ExtraService> extras
	) {
		this(name, description, rating, location, rooms, extras, Set.of(), List.of());
	}
	
	/**
	 * Hány elérhető szoba van egy adott szobatípusból egy időszakra
	 * @param room Szobatípus
	 * @param from Időszak eleje
	 * @param to Időszak vége
	 * @return Elérhető szobák száma
	 */
	public int getAvailableRoomsOf(Room room, LocalDate from, LocalDate to) {
		long takenRooms = reservations.stream()
			.filter((reservation) -> {
				// if the room is not of the given type, no checks are needed
				if (reservation.getRoom().getName().equals(room.getName())) return false;

				if (from.isBefore(reservation.getCheckinDate()))
					return to.isBefore(reservation.getCheckoutDate()) || to.isEqual(reservation.getCheckoutDate());
				if (to.isAfter(reservation.getCheckoutDate()))
					return from.isAfter(reservation.getCheckinDate()) || from.isEqual(reservation.getCheckinDate());
				
				return false;
			})
			.count();
		
		return room.getCount() - (int) takenRooms;
	}
	
	/**
	 * Hány elérhető szoba van egy adott napon
	 * @param room Szobatípus
	 * @param date A megadott nap
	 * @return Elérhető szobák száma
	 */
	public int getAvailability(Room room, LocalDate date) {
		long takenRooms = reservations.stream()
			.filter(
				(reservation) -> reservation.getRoom().getName().equals(room.getName()) && 
					date.isAfter(reservation.getCheckinDate()) &&
					date.isBefore(reservation.getCheckoutDate())
			)
			.count();
		
		return room.getCount() - (int) takenRooms;
	}
	
	public class NoAvailableRoomsException extends Exception {
	    public NoAvailableRoomsException(String message) {
	        super(message);
	    }
	}
	
	/**
	 * Foglalás hozzáadása
	 * @param r Új foglalás példánya
	 */
	public void addReservation(Reservation r) throws NoAvailableRoomsException {
		// get if room is available for the dates
		if (getAvailableRoomsOf(r.getRoom(), r.getCheckinDate(), r.getCheckoutDate()) == 0) {
			throw new NoAvailableRoomsException("Nincs szabad szoba az adott időpontra");
		}
		
		// add points
		r.getCustomer().addPoints((int) r.getNightsCount());
		
		// add customer
		if (!customers.contains(r.getCustomer())) {
			customers.add(r.getCustomer());
		}
		
		// add reservation
		reservations.add(r);
	}
	
	/**
	 * Foglalás törlése
	 * @param r Törlendő foglalás
	 */
	public void removeReservation(Reservation r) {
		// pontok törlése
		int pointsToRemove = (int) r.getNightsCount();
		
		r.getCustomer().addPoints(-pointsToRemove);
		reservations.remove(r);
	}
	
	/**
	 * Visszaadja, hogy van e elmentett hotel
	 * adatfájl
	 * @return Boolean ami jelzi, hogy létezik e
	 * hotel fájl
	 */
	public static boolean hasSavedData() {
		File file = new File("hotel.json");
		
		return file.exists();
	}
	
	/**
	 * Hotel adatainak mentése
	 * @param hotel Az elmentendő hotel
	 * @throws IOException
	 */
	public static void save(Hotel hotel) throws IOException {
		// build json
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();
		String data = gson.toJson(hotel);
		
		// save json
		BufferedWriter writer = new BufferedWriter(new FileWriter("hotel.json"));
		
		writer.write(data);
	}
	
	/**
	 * Hotel adatainak betöltése
	 * @throws IOException 
	 * @return A betöltött hotel
	 */
	public static Hotel load() throws IOException {
		Gson gson = new Gson();
		
		// read json string
		BufferedReader reader = new BufferedReader(new FileReader("hotel.json"));
		
		return gson.fromJson(reader, Hotel.class);
	}
}
