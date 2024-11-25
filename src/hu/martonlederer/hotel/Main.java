package hu.martonlederer.hotel;

import hu.martonlederer.hotel.ui.*;

public class Main {
	public static void main(String[] args) {
		// show main frame
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
		
		// check for saved data
		if (!Hotel.hasSavedData()) {
			HotelDialog dialog = new HotelDialog(frame, null);
			dialog.setVisible(true);
		}
	}
}
