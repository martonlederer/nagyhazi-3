package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import hu.martonlederer.hotel.Hotel;
import hu.martonlederer.hotel.Reservation;

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
		
		JPanel reservationsPanel = new JPanel();
		reservationsPanel.setLayout(new BoxLayout(reservationsPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(reservationsPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
		
		addReservationsToUi(reservationsPanel);
        
        JButton addBtn = new JButton("+ Add");
        addBtn.addActionListener((e) -> {
        	JFrame parent = (JFrame) this.getOwner();
        	ReservationDialog reservationForm = new ReservationDialog(parent, null, hotel, date);
        	
        	reservationForm.setVisible(true);
        	reservationsPanel.removeAll();
        	reservationsPanel.revalidate();
	    	reservationsPanel.repaint();
        	addReservationsToUi(reservationsPanel);
        	
        	try {
				Hotel.save(hotel);
			} catch (IOException err) {
				System.out.println("Failed to save hotel file");
			}
        });
        
        add(addBtn, BorderLayout.SOUTH);
	}
	
	/**
	 * Foglalások betöltése a UI-ba
	 * @param reservationsPanel A JPanel, ahová betöltjük a foglalásokat
	 */
	private void addReservationsToUi(JPanel reservationsPanel) {
		for (Reservation reservation : hotel.getReservationsForDay(date)) {
			JPanel reservationWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		    JButton reservationBtn = new JButton(getReservationTitle(reservation));
		    JButton deleteBtn = new JButton("x");
		    
		    reservationBtn.setHorizontalAlignment(SwingConstants.LEFT);
		
		    reservationBtn.addActionListener((e) -> {
		    	JFrame parent = (JFrame) this.getOwner();
		    	ReservationDialog dialog = new ReservationDialog(parent, reservation, hotel);
	        	dialog.setVisible(true);
	        	
	        	// get updated reservation
	        	Reservation updatedReservation = dialog.getReservation();
	        	
	        	if (updatedReservation != null) {
	        		reservationBtn.setText(getReservationTitle(updatedReservation));
	        	}
	        	
	        	try {
					Hotel.save(hotel);
				} catch (IOException e1) {
					System.out.println("Failed to save hotel file");
				}
		    	
	        	// remove reservation if not on this day
	        	if (updatedReservation == null || updatedReservation.getCheckinDate().isAfter(date) || updatedReservation.getCheckoutDate().isBefore(date)) {
			    	reservationsPanel.remove(reservationWrapper);
			    	reservationsPanel.revalidate();
			    	reservationsPanel.repaint();
	        	}
		    });
		    deleteBtn.addActionListener((e) -> {
		    	int response = JOptionPane.showConfirmDialog(
		    		null,
		    		"Are you sure you want to delete this reservation?",
		    		"Delete reservation",
		    		JOptionPane.YES_NO_OPTION
		    	);
		    	
		    	if (response != JOptionPane.YES_OPTION) return;
		    	hotel.removeReservation(reservation);
		    	reservationsPanel.remove(reservationWrapper);
		    	reservationsPanel.revalidate();
		    	reservationsPanel.repaint();
		    	
		    	try {
					Hotel.save(hotel);
				} catch (IOException err) {
					System.out.println("Failed to save hotel file");
				}
		    });
		    
		    reservationWrapper.add(reservationBtn, BorderLayout.CENTER);
		    reservationWrapper.add(deleteBtn, BorderLayout.EAST);

		    reservationsPanel.add(reservationWrapper);
		}
	}
	
	/**
	 * Visszaadja a foglalás gomb szövegét
	 * @param r A foglalás
	 * @return A szöveg amit a gombon kell mutatni
	 */
	private String getReservationTitle(Reservation r) {
		return r.getCustomer().getName() +
		    " (" +
		    r.getCheckinDate().toString() +
		    " - " +
		    r.getCheckoutDate().toString() +
		    ")";
	}
}
