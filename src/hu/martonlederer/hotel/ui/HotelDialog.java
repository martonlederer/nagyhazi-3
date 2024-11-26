package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import hu.martonlederer.hotel.ExtraService;
import hu.martonlederer.hotel.Hotel;
import hu.martonlederer.hotel.HotelRating;
import hu.martonlederer.hotel.Room;

public class HotelDialog extends JDialog {
	private GridBagConstraints gbc;
	private JPanel formPanel;
	
	private Hotel hotel;
	
	private JTextField nameField;
	private JTextField descriptionField;
	private JTextField locationField;
	
	private JComboBox rating;
	
	private JTable roomTable;
    private JTable extrasTable;
    private DefaultTableModel roomTableModel;
    private DefaultTableModel extrasTableModel;
	
	/**
	 * Hotel dialógus konstruktora
	 * @param parent Szülő frame
	 * @param hotel Opcionális létező hotel objektum (null, ha a hotelt most állítjuk be)
	 */
	public HotelDialog(JFrame parent, Hotel hotel) {
		super(parent, hotel == null ? "Setup hotel" : "Edit hotel", true);
		this.hotel = hotel;
		setSize(new Dimension(400, 800));
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
        rating.setSelectedIndex(
        	hotel != null ? hotel.getRating().getStarsCount() - 1 : 0
        );
        
        addWithLabel("Hotel name:", nameField);
        addWithLabel("Hotel description:", descriptionField);
        addWithLabel("Rating:", rating);
        addWithLabel("Location:", locationField);
        
        roomTableModel = new DefaultTableModel(new Object[]{ "Category", "Price", "Capacity", "Features", "Count" }, 0);
        roomTable = new JTable(roomTableModel);
        populateRoomTable();

        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(roomTable.getTableHeader(), BorderLayout.NORTH);
        roomPanel.add(roomTable, BorderLayout.CENTER);
        addWithLabel("Rooms:", roomPanel);

        JButton addRoomBtn = new JButton("Add Room");
        addRoomBtn.addActionListener(e -> addRoom());
        formPanel.add(addRoomBtn, gbc);
        gbc.gridy++;

        extrasTableModel = new DefaultTableModel(new Object[]{ "Name", "Price" }, 0);
        extrasTable = new JTable(extrasTableModel);
        populateExtrasTable();

        JPanel extrasPanel = new JPanel(new BorderLayout());
        extrasPanel.add(extrasTable.getTableHeader(), BorderLayout.NORTH);
        extrasPanel.add(extrasTable, BorderLayout.CENTER);
        addWithLabel("Extras:", extrasPanel);

        JButton addExtraBtn = new JButton("Add Extra");
        addExtraBtn.addActionListener(e -> addExtra());
        formPanel.add(addExtraBtn, gbc);
        gbc.gridy++;
        
        add(formPanel, BorderLayout.NORTH);
        
        JPanel btnsPanel = new JPanel(new BorderLayout());
        
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener((e) -> saveHotel());
        btnsPanel.add(saveBtn, BorderLayout.NORTH);
        
        if (hotel != null) {
        	JButton deleteBtn = new JButton("Delete");
        	deleteBtn.addActionListener((e) -> {
        		int response = JOptionPane.showConfirmDialog(
			    	null,
			    	"Are you sure you want to delete the entire hotel?",
			   		"Delete hotel",
			   		JOptionPane.YES_NO_OPTION
			   	);
			    	
			    if (response != JOptionPane.YES_OPTION) return;

			    try {
					Files.deleteIfExists(Paths.get(Hotel.savePath));
					System.exit(0);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(
						this,
					    "Could not delete hotel!",
					    "Error",
					    JOptionPane.ERROR_MESSAGE
					);
				}
        	});
            btnsPanel.add(deleteBtn, BorderLayout.SOUTH);
        }

		add(btnsPanel, BorderLayout.SOUTH);
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
	 * Hotel aktuális értékének lekérdezése
	 * @return Hotel példány
	 */
	public Hotel getHotel() {
		return hotel;
	}
	
	/**
	 * Feltölti a szoba táblát a létező szobákkal, ha vannak
	 */
	private void populateRoomTable() {
		if (hotel == null) return;
        hotel.getRooms().forEach(room -> roomTableModel.addRow(new Object[]{
	        room.getName(),
	        room.getPrice(),
	        room.getCapacity(),
	        String.join(", ", room.getFacilities()),
	        room.getCount()
        }));
    }

	/**
	 * Feltölti az extrák táblát a létező extrákkal, ha vannak
	 */
    private void populateExtrasTable() {
        if (hotel == null) return;
        hotel.getExtras().forEach(extra -> extrasTableModel.addRow(new Object[]{
            extra.getName(),
            extra.getPrice()
        }));
    }
	
	/**
	 * Új szobát illeszt be a szoba táblába
	 */
	private void addRoom() {
        roomTableModel.addRow(new Object[]{ "Szoba", 50000, 2, "Légkondi", 100 });
    }

	/**
	 * Új extrát illeszt be az extrák táblába
	 */
    private void addExtra() {
        extrasTableModel.addRow(new Object[]{ "Extra", 8000 });
    }
	
	/**
	 * Új hotel példányt készít az adatokból, majd bezárja a dialógust
	 */
	private void saveHotel() {
		Set<Room> rooms = new HashSet<>();
		Set<ExtraService> extras = new HashSet<>();
		
		// parse rooms
		for (int i = 0; i < roomTableModel.getRowCount(); i++) {
            rooms.add(new Room(
            	(String) roomTableModel.getValueAt(i, 0),
            	(Integer) roomTableModel.getValueAt(i, 1),
            	(Integer) roomTableModel.getValueAt(i, 2),
            	(Integer) roomTableModel.getValueAt(i, 4),
            	List.of(((String) roomTableModel.getValueAt(i, 3)).split(", "))
            ));
        }
		
		// parse extras
		for (int i = 0; i < extrasTableModel.getRowCount(); i++) {
			extras.add(new ExtraService(
				(String) extrasTableModel.getValueAt(i, 0),
				(Integer) extrasTableModel.getValueAt(i, 1)
			));
		}
		
		// parse the rest and create the hotel
		hotel = new Hotel(
			nameField.getText(),
        	descriptionField.getText(),
        	HotelRating.fromStars((String) rating.getSelectedItem()),
       		locationField.getText(),
       		rooms,
        	extras,
       		hotel != null ? hotel.getCustomers() : Set.of(),
            hotel != null ? hotel.getReservations() : List.of()
        );
        dispose();
	}
}
