package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.martonlederer.hotel.Hotel.NoAvailableRoomsException;

class HotelTest {
	Hotel hotel;
	Room room;
	Room otherRoom;

	@BeforeEach
	void setup() throws IOException {
		Hotel.savePath = "test_hotel.json";
		room = new Room("Duplex", 1000, 2, 2, List.of());
		otherRoom = new Room("Another", 10000, 2, 2, List.of());
		hotel = new Hotel(
			"Tihany Hotel",
			"Ez egy példa hotel, ami Tihanyban van",
			HotelRating.Comfortable,
			"8236 Tihany, Rév utca 2.",
			Set.of(room, otherRoom),
			Set.of()
		);
		
		Files.deleteIfExists(Paths.get(Hotel.savePath));
	}

	@Test
	void testReservation() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-10"),
			LocalDate.parse("2024-05-13"),
			List.of()
		);
		hotel.addReservation(res);
		
		assertEquals(1, hotel.getReservations().size());
		assertTrue(hotel.getReservations().contains(res));
		assertEquals(3, res.getCustomer().getPoints());
	}
	
	@Test
	void testRemoveReservation() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-07-21"),
			LocalDate.parse("2024-07-28"),
			List.of()
		);
		hotel.addReservation(res);
		
		assertEquals(1, hotel.getReservations().size());

		hotel.removeReservation(res);
		
		assertEquals(0, hotel.getReservations().size());
		assertEquals(0, res.getCustomer().getPoints());
	}
	
	@Test
	void testAvailability() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-12"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		hotel.addReservation(res);
		
		assertEquals(1, hotel.getAvailability(room, LocalDate.parse("2024-05-13")));
		assertEquals(2, hotel.getAvailability(room, LocalDate.parse("2024-05-17")));
	}
	
	@Test
	void testTotalAvailability() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-12"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		hotel.addReservation(res);
		
		assertEquals(3, hotel.getAvailability(null, LocalDate.parse("2024-05-13")));
		assertEquals(4, hotel.getAvailability(null, LocalDate.parse("2024-05-17")));
	}
	
	@Test
	void testTotalRoomCount() {
		assertEquals(4, hotel.getTotalRoomCount());
	}
	
	@Test
	void testAvailableRoomsOf() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-12"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		hotel.addReservation(res);
		
		assertEquals(1, hotel.getAvailableRoomsOf(room, LocalDate.parse("2024-05-12"), LocalDate.parse("2024-05-16")));
		assertEquals(1, hotel.getAvailableRoomsOf(room, LocalDate.parse("2024-05-10"), LocalDate.parse("2024-05-14")));
		assertEquals(1, hotel.getAvailableRoomsOf(room, LocalDate.parse("2024-05-13"), LocalDate.parse("2024-05-18")));
		
		hotel.addReservation(res);
		
		assertEquals(0, hotel.getAvailableRoomsOf(room, LocalDate.parse("2024-05-12"), LocalDate.parse("2024-05-16")));
	}
	
	@Test
	void testReservationWithNoAvailability() throws NoAvailableRoomsException {
		Reservation res = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-12"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		hotel.addReservation(res);
		hotel.addReservation(res);
		
		assertThrows(
			NoAvailableRoomsException.class,
			() -> hotel.addReservation(res)
		);
	}
	
	@Test
	void testHasSavedData() throws IOException {
		assertFalse(Hotel.hasSavedData());
		
		FileWriter fw = new FileWriter(Hotel.savePath);
		fw.write("{}");
		fw.close();
		
		assertTrue(Hotel.hasSavedData());
	}
	
	@Test
	void testSave() throws IOException {
		Hotel.save(hotel);
		
		assertTrue(Hotel.hasSavedData());
	}
	
	@Test
	void testLoad() throws IOException {
		Hotel.save(hotel);
		Hotel other = Hotel.load();
		
		assertTrue(hotel.getName().equals(other.getName()));
		assertTrue(hotel.getDescription().equals(other.getDescription()));
		assertTrue(hotel.getDescription().equals(other.getDescription()));
		assertEquals(hotel.getRating(), other.getRating());
	}
	
	@Test
	void testReservationsForDay() throws NoAvailableRoomsException {
		Reservation res1 = new Reservation(
			new Customer("John Doe", "test@test.com", "+3614556789"),
			room,
			LocalDate.parse("2024-05-12"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		Reservation res2 = new Reservation(
			new Customer("Test Test", "test@test.com", "+3634556789"),
			room,
			LocalDate.parse("2024-05-14"),
			LocalDate.parse("2024-05-16"),
			List.of()
		);
		hotel.addReservation(res1);
		hotel.addReservation(res2);
		
		hotel.addReservation(new Reservation(
			new Customer("Other Test", "test@test.hu", "+35408931266"),
			otherRoom,
			LocalDate.parse("2024-05-16"),
			LocalDate.parse("2024-05-19"),
			List.of()
		));
		
		List<Reservation> reservationsForDay = hotel.getReservationsForDay(LocalDate.parse("2024-05-15"));
		
		assertEquals(2, reservationsForDay.size());
		assertTrue(reservationsForDay.contains(res1));
		assertTrue(reservationsForDay.contains(res2));
	}
	
	@Test
	void testFindCustomer() throws NoAvailableRoomsException {
		Customer customer = new Customer("Other Test", "test@test.hu", "+35408931266", 500);
		hotel.addReservation(new Reservation(
			customer,
			otherRoom,
			LocalDate.parse("2024-05-16"),
			LocalDate.parse("2024-05-19"),
			List.of()
		));
		
		assertEquals(customer, hotel.findCustomerByName(customer.getName()));
	}
}
