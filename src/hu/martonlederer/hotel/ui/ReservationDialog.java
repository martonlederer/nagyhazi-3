package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hu.martonlederer.hotel.Customer;
import hu.martonlederer.hotel.ExtraService;
import hu.martonlederer.hotel.Hotel;
import hu.martonlederer.hotel.Hotel.NoAvailableRoomsException;
import hu.martonlederer.hotel.Reservation;
import hu.martonlederer.hotel.Room;

public class ReservationDialog extends JDialog {
	private GridBagConstraints gbc;
	private JPanel formPanel;

	private JTextField nameField;
	private JTextField emailField;
	private JTextField phoneNumberField;
	
	private JComboBox existingCustomers;
	private JComboBox roomType;
	
	private JSpinner fromDate;
	private JSpinner toDate;
	
	private JLabel priceSumLabel;
	private JLabel pointsEarnedLabel;
	
	private Reservation reservation;
	private Hotel hotel;
	
	private List<ExtraService> addedExtras;
	
	/**
	 * Foglalás dialógus konstruktora
	 * @param parent Szülő frame
	 * @param reservation Opcionális foglalás (null is lehet, ha a foglalást éppen hozzáadjuk)
	 */
	public ReservationDialog(JFrame parent, Reservation reservation, Hotel hotel) {
		super(parent, reservation == null ? "Add reservation" : "Edit reservation", true);
		this.reservation = reservation;
		this.hotel = hotel;
		
		// manually add extras, so we don't change the reservation reference value
		this.addedExtras = new ArrayList<>();
		
		if (reservation != null)
			for (ExtraService extra : reservation.getExtras())
				this.addedExtras.add(extra);
		
		setSize(new Dimension(400, 600));
		setLocationRelativeTo(parent);
		initComponents();
	}
	
	/**
	 * A dialógus elemeinek létrehozása
	 */
	private void initComponents() {
		setLayout(new BorderLayout());
		
		formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
		
		Customer customer = reservation != null ? reservation.getCustomer() : null;
		emailField = new JTextField(customer != null ? customer.getEmail() : null);
		phoneNumberField = new JTextField(customer != null ? customer.getPhoneNumber() : null);
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		existingCustomers = new JComboBox(
			hotel.getCustomers().stream()
				.map(Customer::getName)
				.toArray(String[]::new)
		);
		
		if (customer != null) {
			existingCustomers.setSelectedItem(customer.getName());
		}
		
		nameField = new JTextField(customer != null ? customer.getName() : null);
		nameField.setPreferredSize(new Dimension(200, 25));
		nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            	updateTotalPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTotalPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
		
		existingCustomers.addActionListener((e) -> {
			nameField.setText((String) existingCustomers.getSelectedItem());
		});

		namePanel.add(nameField);
		namePanel.add(existingCustomers);

		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        addWithLabel("Customer name:", namePanel);
        addWithLabel("Customer email:", emailField);
        addWithLabel("Customer phone number:", phoneNumberField);
        
        roomType = new JComboBox(
        	hotel.getRooms().stream()
        		.map(Room::getName)
        		.toArray(String[]::new)
        );
        
        if (reservation != null) {
        	roomType.setSelectedItem(reservation.getRoom().getName());
        }
        
        addWithLabel("Room type:", roomType);
        
        fromDate = new JSpinner(new SpinnerDateModel(
            reservation != null ? getDate(reservation.getCheckinDate()) : new Date(),
            new Date(),
            null,
            java.util.Calendar.DAY_OF_MONTH
        ));
        toDate = new JSpinner(new SpinnerDateModel(
            getDate(
            	reservation != null ? reservation.getCheckoutDate() : LocalDate.now().plusDays(1)
            ),
            new Date(),
            null,
            java.util.Calendar.DAY_OF_MONTH
        ));
        
        // date fixer
        fromDate.addChangeListener((e) -> {
        	LocalDate selectedDate = getLocalDate((Date) fromDate.getValue());
        	LocalDate otherDate = getLocalDate((Date) toDate.getValue());
        	
        	if (!selectedDate.isBefore(otherDate)) {
	        	selectedDate = otherDate.minusDays(1);
	        	fromDate.setValue(getDate(selectedDate));
        	}
        	updateTotalPrice();
        });
        toDate.addChangeListener((e) -> {
        	LocalDate selectedDate = getLocalDate((Date) toDate.getValue());
        	LocalDate otherDate = getLocalDate((Date) fromDate.getValue());
        	
        	if (!selectedDate.isAfter(otherDate)) {
	        	selectedDate = otherDate.plusDays(1);
	        	toDate.setValue(getDate(selectedDate));
        	}
        	updateTotalPrice();
        });
        
        addWithLabel("From:", fromDate);
        addWithLabel("To:", toDate);
                
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        for (ExtraService extra : hotel.getExtras()) {
	        JCheckBox checkbox = new JCheckBox(extra.getName());

	        checkbox.setSelected(reservation != null && reservation.getExtras().contains(extra));
	        checkbox.addChangeListener((e) -> {
	        	if (checkbox.isSelected() && !addedExtras.contains(extra)) {
	        		addedExtras.add(extra);
	        	} else if (!checkbox.isSelected() && addedExtras.contains(extra)) {
	        		addedExtras.remove(extra);
	        	}
	        	updateTotalPrice();
	        });
	        panel.add(checkbox);
        }

        addWithLabel("Extra services:", panel);
        
        priceSumLabel = new JLabel("Summary: HUF");
        formPanel.add(priceSumLabel, gbc);
		gbc.gridy++;
		
        pointsEarnedLabel = new JLabel("Points earned: 0");
        formPanel.add(pointsEarnedLabel, gbc);
		gbc.gridy++;

		add(formPanel, BorderLayout.NORTH);
		
		JPanel btnsPanel = new JPanel(new BorderLayout());
		
		if (reservation != null) {
			JButton deleteBtn = new JButton("Delete");
			deleteBtn.addActionListener((e) -> {
				hotel.removeReservation(reservation);
				dispose();
			});
			btnsPanel.add(deleteBtn, BorderLayout.NORTH);
		}

		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener((e) -> save());
		btnsPanel.add(saveBtn, BorderLayout.SOUTH);
		
		add(btnsPanel, BorderLayout.SOUTH);
		updateTotalPrice();
	}
	
	/**
	 * Hozzáad a dialógushoz a megadott címkével
	 * @param label A címke szövege
	 * @param component Az elem amit hozzáadunk a dialógushoz
	 */
	private void addWithLabel(String label, Component component) {
		formPanel.add(new JLabel(label), gbc);
		gbc.gridy++;
		formPanel.add(component, gbc);
		gbc.gridy++;
	}
	
	/**
	 * Segédfunkció LocalDate számításához Date-ből
	 * @param date Eredeti dátum
	 * @return LocalDate érték
	 */
	private LocalDate getLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	/**
	 * Segédfunkció Date számításához LocalDate-ből
	 * @param localDate Eredeti dátum
	 * @return Date érték
	 */
	private Date getDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Visszaadja a foglalást az aktuális adatok alapján
	 * @return A generált foglalás
	 */
	private Reservation getReservation() {
		Room room = null;
		
		for (Room r : hotel.getRooms())
			if (r.getName().equals((String) roomType.getSelectedItem()))
				room = r;
		
		if (room == null) return null;
		
		Customer c = hotel.findCustomerByName(nameField.getText());
		
		if (c == null) {
			c = new Customer(
				nameField.getText(),
				emailField.getText(),
				phoneNumberField.getText()
			);
		}
		
		return new Reservation(
			c,
			room,
			getLocalDate((Date) fromDate.getValue()),
			getLocalDate((Date) toDate.getValue()),
			addedExtras
		);
	}
	
	/**
	 * Frissíti a teljes árat
	 */
	private void updateTotalPrice() {
		Reservation predicted = getReservation();
		
		if (predicted == null) return;

		priceSumLabel.setText(
			"Summary: HUF" + Integer.toString(predicted.getTotalPrice()) +
			" (" + Integer.toString(predicted.getDiscountedNights()) + " free nights for points)"
		);
		pointsEarnedLabel.setText("Points earned: " + Long.toString(predicted.getNightsCount()));
	}
	
	/**
	 * Elmenti a foglalást és bezárja a dialógust
	 */
	public void save() {
		Reservation newReservation = getReservation();
		
		if (reservation != null) {
			hotel.removeReservation(reservation);
		}
		
		try {
			hotel.addReservation(newReservation);
			dispose();
		} catch (NoAvailableRoomsException e) {
			JOptionPane.showMessageDialog(
				this,
		         "No available rooms for this date!",
		        "Error",
		        JOptionPane.ERROR_MESSAGE
		    );
		}
	}
}
