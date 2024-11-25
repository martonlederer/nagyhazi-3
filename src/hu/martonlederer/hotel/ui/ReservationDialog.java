package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import hu.martonlederer.hotel.Customer;
import hu.martonlederer.hotel.Reservation;

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
	
	private Reservation reservation;
	
	/**
	 * Foglalás dialógus konstruktora
	 * @param parent Szülő frame
	 * @param reservation Opcionális foglalás (null is lehet, ha a foglalást éppen hozzáadjuk)
	 */
	public ReservationDialog(JFrame parent, Reservation reservation) {
		super(parent, reservation == null ? "Add reservation" : "Edit reservation", true);
		this.reservation = reservation;
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

		// TODO
        String[] customers = { "", "Marton Lederer", "John Doe" };
		existingCustomers = new JComboBox(customers);
		nameField = new JTextField(customer != null ? customer.getName() : null);
		nameField.setPreferredSize(new Dimension(200, 25));
		namePanel.add(nameField);
		namePanel.add(existingCustomers);

		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        addWithLabel("Customer name:", namePanel);
        addWithLabel("Customer email:", emailField);
        addWithLabel("Customer phone number:", phoneNumberField);
        
        // TODO
        String[] roomTypes = { "Duplex", "Premium" };
        roomType = new JComboBox(roomTypes);
        addWithLabel("Room type:", roomType);
        
        SpinnerDateModel fromSpinnerModel = new SpinnerDateModel(
        	new Date(),
        	new Date(),
        	null,
        	java.util.Calendar.DAY_OF_MONTH
        );
        fromDate = new JSpinner(fromSpinnerModel);
        SpinnerDateModel toSpinnerModel = new SpinnerDateModel(
            new Date(),
           	new Date(),
           	null,
           	java.util.Calendar.DAY_OF_MONTH
        );
        toDate = new JSpinner(toSpinnerModel);
        
        addWithLabel("From:", fromDate);
        addWithLabel("To:", toDate);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // TODO:
        JCheckBox checkbox1 = new JCheckBox("Option 1");
        JCheckBox checkbox2 = new JCheckBox("Option 2");
        JCheckBox checkbox3 = new JCheckBox("Option 3");

        panel.add(checkbox1);
        panel.add(checkbox2);
        panel.add(checkbox3);
        
        addWithLabel("Extra services:", panel);
		
		add(formPanel, BorderLayout.NORTH);
		
		JButton saveBtn = new JButton("Save");
		add(saveBtn, BorderLayout.SOUTH);
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
}
