package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationTest {
	Customer customer;
	Room room;
	
	@BeforeEach
	void setup() {
		customer = new Customer(
			"John Doe",
			"test@test.hu",
			"+36708459312"
		);
		room = new Room(
			"Duplex",
			64000,
			2,
			85,
			List.of("Légkondi", "Minibár", "Seeview")
		);
	}

	@Test
	void testNightsCount() {
		// should be 14 nights
		Reservation res = new Reservation(
			customer,
			room,
			LocalDate.parse("2024-05-10"),
			LocalDate.parse("2024-05-24"),
			List.of()
		);
		
		assertEquals(14, res.getNightsCount());
	}
	
	@Test
	void testTotalPriceNoExtras() {
		// price for the room only (no extras)
		Reservation res = new Reservation(
			customer,
			room,
			LocalDate.parse("2024-07-17"),
			LocalDate.parse("2024-07-21"),
			List.of()
		);
		
		assertEquals(256000, res.getTotalPrice());
	}
	
	@Test
	void testTotalPriceWithExtras() {
		// price for the room + the extras
		Reservation res = new Reservation(
			customer,
			room,
			LocalDate.parse("2023-11-05"),
			LocalDate.parse("2023-11-07"),
			List.of(
				new ExtraService("Virág", 4500),
				new ExtraService("Torta", 3200)
			)
		);
		
		assertEquals(135700, res.getTotalPrice());
	}
}
