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
import java.util.stream.Stream;

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
	
	public static String savePath = "hotel.json";
	
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
	 * A hotel nevének lekérdezése
	 * @return A hotel neve
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * A hotel leírásának lekérdezése
	 * @return A hotel leírása
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * A hotel minősítésének lekérdezése
	 * @return A hotel minősítése
	 */
	public HotelRating getRating() {
		return rating;
	}
	
	/**
	 * A hotel helyének lekérdezése
	 * @return A hotel helye
	 */
	public String getLocation() {
		return location;
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
				if (reservation.getRoom() != room) return false;
				
				return !(to.isBefore(reservation.getCheckinDate().plusDays(1)) &&
					from.isAfter(reservation.getCheckoutDate().minusDays(1)));
			})
			.count();
		
		return room.getCount() - (int) takenRooms;
	}
	
	/**
	 * Segédfunkció foglalt szobák filterezéséhez
	 * @param date Megadott dátum, ami alapján válogat a funkció
	 * @return Foglalt szobák stream
	 */
	private Stream<Reservation> streamTakenRooms(LocalDate date) {
		return reservations.stream()
			.filter(
				(reservation) -> date.isAfter(reservation.getCheckinDate().minusDays(1)) &&
					date.isBefore(reservation.getCheckoutDate().plusDays(1))
			);
	}
	
	/**
	 * Hány szoba van összesen a hotelben
	 * @return Szobaszám
	 */
	public int getTotalRoomCount() {
		return rooms.stream()
			.map(Room::getCount)
			.reduce(0, (sum, curr) -> sum + curr);
	}
	
	/**
	 * Hány elérhető szoba van egy adott napon
	 * @param room Szobatípus (ha null, akkor minden szobát néz a rendszer)
	 * @param date A megadott nap
	 * @return Elérhető szobák száma
	 */
	public int getAvailability(Room room, LocalDate date) {
		int roomCount = room != null ? room.getCount() : getTotalRoomCount();
		
		long takenRooms = streamTakenRooms(date)
			.filter((reservation) ->
				(reservation.getRoom() == room || room == null) &&
				!date.isEqual(reservation.getCheckinDate()) &&
				!date.isEqual(reservation.getCheckoutDate())
			)
			.count();
		
		return roomCount - (int) takenRooms;
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
		File file = new File(savePath);
		
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
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
			.create();
		String data = gson.toJson(hotel);
		
		// save json
		BufferedWriter writer = new BufferedWriter(new FileWriter(savePath));
		
		writer.write(data);
		writer.close();
	}
	
	/**
	 * Hotel adatainak betöltése
	 * @throws IOException 
	 * @return A betöltött hotel
	 */
	public static Hotel load() throws IOException {
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
			.create();
		
		// read json string
		BufferedReader reader = new BufferedReader(new FileReader(savePath));
		
		Hotel hotel = gson.fromJson(reader, Hotel.class);
		reader.close();
		
		return hotel;
	}
	
	/**
	 * Foglalások lekérdezése
	 * @return Foglalások a hotelhez
	 */
	public List<Reservation> getReservations() {
		return reservations;
	}
	
	/**
	 * Ügyfelek lekérdezése
	 * @return Ügyfelek a hotelhez
	 */
	public Set<Customer> getCustomers() {
		return customers;
	}
	
	/**
	 * Szobák lekérdezése
	 * @return Összes szoba kategória
	 */
	public Set<Room> getRooms() {
		return rooms;
	}
	
	/**
	 * Extra szolgáltatások lekérdezése
	 * @return Extrák a hotelhez
	 */
	public Set<ExtraService> getExtras() {
		return extras;
	}
	
	/**
	 * Foglalt szobák listázása egy adott napra
	 * @param date Megadott nap, amire a lefoglalt szobákat listázzuk
	 * @return Lefoglalt szobák
	 */
	public List<Reservation> getReservationsForDay(LocalDate date) {
		return streamTakenRooms(date).toList();
	}
	
	/**
	 * Megkeres egy ügyfelet a neve szerint
	 * @param name Ügyfél neve
	 * @return Ügyfél vagy null
	 */
	public Customer findCustomerByName(String name) {
		for (Customer c : customers) {
			if (c.getName().equals(name)) return c;
		}
		
		return null;
	}
}
