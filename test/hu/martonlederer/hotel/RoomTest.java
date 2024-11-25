package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class RoomTest {
	@Test
	void testInvalidPrice() {
		assertThrows(
			IllegalArgumentException.class,
			() -> new Room(
				"Szobanev",
				-1514,
				2,
				120,
				List.of("Légkondi")
			)
		);
	}
	
	@Test
	void testInvalidCapacity() {
		assertThrows(
			IllegalArgumentException.class,
			() -> new Room(
				"Szobanev",
				45300,
				0,
				120,
				List.of("Légkondi")
			)
		);
	}
	
	@Test
	void testInvalidCount() {
		assertThrows(
			IllegalArgumentException.class,
			() -> new Room(
				"Szobanev",
				74532,
				1,
				0,
				List.of("Légkondi")
			)
		);
	}
}
