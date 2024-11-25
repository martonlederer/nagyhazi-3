package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hu.martonlederer.hotel.Hotel;
import hu.martonlederer.hotel.HotelRating;

public class HotelDialog extends JDialog {
	private GridBagConstraints gbc;
	private JPanel formPanel;
	
	private Hotel hotel;
	
	private JTextField nameField;
	private JTextField descriptionField;
	private JTextField locationField;
	
	private JComboBox rating;
	
	/**
	 * Hotel dialógus konstruktora
	 * @param parent Szülő frame
	 * @param hotel Opcionális létező hotel objektum (null, ha a hotelt most állítjuk be)
	 */
	public HotelDialog(JFrame parent, Hotel hotel) {
		super(parent, hotel == null ? "Setup hotel" : "Edit hotel");
		this.hotel = hotel;
		setSize(new Dimension(400, 600));
		setLocationRelativeTo(parent);
		
		if (hotel == null) {
			addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                dispose();
	                parent.dispose();
	                System.exit(0);
	            }
	        });
		}
		
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        nameField = new JTextField(this.hotel != null ? hotel.getName() : null);
        descriptionField = new JTextField(this.hotel != null ? hotel.getDescription() : null);
        locationField = new JTextField(this.hotel != null ? hotel.getLocation() : null);
        
        rating = new JComboBox(Arrays.stream(HotelRating.values()).map(HotelRating::getStars).toArray());
        
        addWithLabel("Hotel name:", nameField);
        addWithLabel("Hotel description:", descriptionField);
        addWithLabel("Rating:", rating);
        
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
