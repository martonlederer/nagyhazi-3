package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HotelRatingTest {
	@Test
	void testStarsText() {
		HotelRating rating = HotelRating.Excellent;
		
		assertTrue("☆☆☆☆".equals(rating.getStars()));
	}
	
	@Test
	void testFromStars() {
		String stars = "☆☆";
		
		assertEquals(HotelRating.Budget, HotelRating.fromStars(stars));
	}
	
	@Test
	void testFromStarsInvalid() {
		String stars = "☆☆☆☆☆☆";

		assertThrows(
			IllegalArgumentException.class,
			() -> HotelRating.fromStars(stars)
		);
	}
}
