package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import hu.martonlederer.hotel.Hotel;

public class DayReservationList extends JDialog {
	private LocalDate date;
	private Hotel hotel;
	
	private JPanel formPanel;

	/**
	 * Napi foglalás lista dialógus konstruktora
	 * @param parent Szülő frame
	 * @param date Az adott nap dátuma
	 */
	public DayReservationList(JFrame parent, LocalDate date, Hotel hotel) {
		super(parent, "Reservations on " + date.toString(), true);
		this.date = date;
		this.hotel = hotel;
		
		setSize(new Dimension(400, 600));
		setLocationRelativeTo(parent);
		initComponents();
	}
	
	/**
	 * Komponensek hozzáadása
	 */
	public void initComponents() {
		setLayout(new BorderLayout());
		
		formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        
        JButton addBtn = new JButton("+ Add");
        addBtn.addActionListener((e) -> {
        	JFrame parent = (JFrame) this.getOwner();
        	ReservationDialog reservationForm = new ReservationDialog(parent, null, hotel, date);
        	
        	reservationForm.setVisible(true);
        	
        	try {
				Hotel.save(hotel);
			} catch (IOException e1) {
				System.out.println("Failed to save hotel file");
			}
        });
        
        
        add(addBtn, BorderLayout.SOUTH);
	}
}
