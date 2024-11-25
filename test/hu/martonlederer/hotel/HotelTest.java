package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.martonlederer.hotel.Hotel.NoAvailableRoomsException;

class HotelTest {
	Hotel hotel;
	Room room;

	@BeforeEach
	void setup() {
		room = new Room("Duplex", 1000, 2, 2, List.of());
		hotel = new Hotel(
			"Tihany Hotel",
			"Ez egy példa hotel, ami Tihanyban van",
			HotelRating.Comfortable,
			"8236 Tihany, Rév utca 2.",
			Set.of(room),
			Set.of()
		);
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
}
