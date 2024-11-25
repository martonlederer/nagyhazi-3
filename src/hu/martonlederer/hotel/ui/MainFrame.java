package hu.martonlederer.hotel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainFrame extends JFrame {
	private CalendarData data;
	
	// global components
	JLabel dateLabel;
	
	/**
	 * Főoldal konstruktora
	 */
	public MainFrame() {
		// frame konfig
		super("Hotel manager");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// setup calendar
		data = new CalendarData(
			LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())
		);
		
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
        add(scrollPane, BorderLayout.CENTER);

        // navigáció
        JPanel nav = new JPanel();
        
        dateLabel = new JLabel(data.getStartDate().toString());
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        JButton addBtn = new JButton("+ Add");
        prev.addActionListener(new DateNavBtnListener(-1));
        next.addActionListener(new DateNavBtnListener(1));
        
        nav.add(prev);
        nav.add(dateLabel);
        nav.add(next);
        nav.add(addBtn);
        add(nav, BorderLayout.SOUTH);
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
