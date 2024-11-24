package hu.martonlederer.hotel;

import java.util.ArrayList;
import java.util.List;

public class Room {
	private String name;
	private int price;
	private int capacity;
	private int count;
	private List<String> facilities;
	
	/**
	 * Room osztály konstruktor
	 * @param name Szoba típus neve
	 * @param price Szoba ára egy éjszakára
	 * @param capacity Maximum férőhely a szobához
	 * @param count Megadja, hány darab ilyen szoba van a hotelben
	 * @param facilities Szoba felszereltésge
	 */
	public Room(String name, int price, int capacity, int count, List<String> facilities) {
		if (price <= 0) {
			throw new IllegalArgumentException("Extra service price has to be >0");
		}
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity has to be >0");
		}
		if (count <= 0) {
			throw new IllegalArgumentException("There has to be at least on of each room type");
		}

		this.name = name;
		this.price = price;
		this.capacity = capacity;
		this.count = count;
		this.facilities = new ArrayList<>(facilities);
	}
	
	/**
	 * Szobatípus nevének lekérdezése
	 * @return Szobatípus neve
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Szobatípus árának lekérdezése
	 * @return A szoba ára egy éjszakára
	 */
	public int getPrice() {
		return price;
	}
	
	/**
	 * Szobatípus férőhelyének lekérdezése
	 * @return A szoba maximum férőhelye
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Szobatípus számának lekérdezése
	 * @return Hány ilyen szoba van a hotelben
	 */
	public int getCount() {
		return count;
	}
}
