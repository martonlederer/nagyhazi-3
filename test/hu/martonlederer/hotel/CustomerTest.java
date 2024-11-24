package hu.martonlederer.hotel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerTest {
	String name = "John Doe";
	String email = "email@email.com";
	String num = "+36704563290";
	int existingPoints = 4225;
	Customer customer;
	
	@BeforeEach
	void setup() {
		customer = new Customer(name, email, num, existingPoints);
	}

	@Test
	void testNewCustomer() {
		customer = new Customer(name, email, num);
		
		assertTrue(customer.getName().equals(name));
		assertTrue(customer.getPhoneNumber().equals(num));
		assertTrue(customer.getEmail().equals(email));
		assertEquals(customer.getPoints(), 0, 0);
	}

	@Test
	void testExistingCustomer() {
		assertTrue(customer.getName().equals(name));
		assertTrue(customer.getPhoneNumber().equals(num));
		assertTrue(customer.getEmail().equals(email));
		assertEquals(customer.getPoints(), existingPoints, 0);
	}
	
	@Test
	void testAddPoints() {
		int addCount = 45;
		customer.addPoints(addCount);
		
		assertEquals(customer.getPoints(), existingPoints + addCount, 0);
	}
	
	@Test
	void testMinusPoints() {
		customer.addPoints(-1 * (existingPoints + 1));
		
		assertEquals(customer.getPoints(), 0, 0);
	}
}
