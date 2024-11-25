package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import hu.martonlederer.hotel.Hotel;

public class MainFrame extends JFrame {
	private CalendarModel data;
	private Hotel hotel;
	
	// global components
	JLabel dateLabel;
	
	/**
	 * Főoldal konstruktora
	 */
	public MainFrame() {
		// frame konfig
		super("Reservations overview");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// setup calendar
		data = new CalendarModel(
			LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
		);
		hotel = null;
		
		// adjuk hozzá az elemeket
		initComponents();
	}
	
	/**
	 * A frame elemeinek létrehozása
	 */
	private void initComponents() {
        setLayout(new BorderLayout());
        
        // naptár
        JTable calendar = new JTable(data);
        JScrollPane scrollPane = new JScrollPane(calendar);
        
        calendar.setFillsViewportHeight(true);
        calendar.getTableHeader().setReorderingAllowed(false);
        calendar.setRowHeight(100);
        calendar.setCellSelectionEnabled(true);
        calendar.setRowSelectionAllowed(false);
        calendar.setColumnSelectionAllowed(false);
        calendar.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                component.setFont(new Font("Arial", Font.BOLD, 18));
                ((JComponent) component).setBorder(new LineBorder(Color.WHITE, 10));

                setHorizontalAlignment(SwingConstants.RIGHT);
                setVerticalAlignment(SwingConstants.BOTTOM);

                //setBackground(new Color(255, 255, 204));

                return component;
            }
        });
        calendar.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseClicked(MouseEvent e) {
                int row = calendar.rowAtPoint(e.getPoint());
                int column = calendar.columnAtPoint(e.getPoint());

                if (row >= 0 && column >= 0) {
                    Object cellValue = calendar.getValueAt(row, column);
                    System.out.println(cellValue);
                }
            }
        });
                        
        add(scrollPane, BorderLayout.CENTER);

        // navigáció
        JPanel nav = new JPanel();
        
        JButton editHotelBtn = new JButton("Edit hotel");
        dateLabel = new JLabel(data.getStartDate().toString());
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        JButton addBtn = new JButton("+ Add");

        prev.addActionListener(new DateNavBtnListener(-1));
        next.addActionListener(new DateNavBtnListener(1));
        editHotelBtn.addActionListener((e) -> editHotel());
        addBtn.addActionListener((e) -> {
        	ReservationDialog dialog = new ReservationDialog(this, null);
        	dialog.setVisible(true);
        });
        
        nav.add(editHotelBtn);
        nav.add(prev);
        nav.add(dateLabel);
        nav.add(next);
        nav.add(addBtn);
        add(nav, BorderLayout.SOUTH);
	}
	
	/**
	 * A hotel példány frissítése
	 * @param hotel Új hotel példány
	 */
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	/**
	 * A hotel szerkesztésének meghívása (előhozza a dialógust)
	 */
	private void editHotel() {
    	HotelDialog dialog = new HotelDialog(this, hotel);
		dialog.setVisible(true);
		
		hotel = dialog.getHotel();
		
		try {
			Hotel.save(hotel);
		} catch (IOException e) {
			System.out.println("Failed to save hotel file");
		}
    }
	
	final class DateNavBtnListener implements ActionListener {	
		private long val;

		/**
		 * Következő/előző hónap listener
		 * @param val Az érték amit a listener hozzáad az aktuális hónaphoz
		 */
		public DateNavBtnListener(long val) {
			this.val = val;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			data.setStartDate(data.getStartDate().plusMonths(val));
			dateLabel.setText(data.getStartDate().toString());
		}
	}
}
