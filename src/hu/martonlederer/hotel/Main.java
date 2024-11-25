package hu.martonlederer.hotel;

import java.io.IOException;

import hu.martonlederer.hotel.ui.*;

public class Main {
	public static void main(String[] args) {
		// show main frame
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
		
		// controller
		Hotel hotel;
		
		// check for saved data
		if (!Hotel.hasSavedData()) {
			HotelDialog dialog = new HotelDialog(frame, null);
			dialog.setVisible(true);
			
			hotel = dialog.getHotel();
		} else {
			try {
				hotel = Hotel.load();
			} catch (IOException e) {
				System.out.println("Failed to load hotel file");
				System.exit(0);
			}
		}
		
		
	}
}
