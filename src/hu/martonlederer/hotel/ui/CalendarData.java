package hu.martonlederer.hotel.ui;

import java.time.LocalDate;

import javax.swing.table.AbstractTableModel;

public class CalendarData extends AbstractTableModel {
	public LocalDate startDate;
	
	private String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	
	public CalendarData(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Start date frissítése a naptárhoz
	 * @param date Új dátum
	 */
	public void setStartDate(LocalDate date) {
		startDate = date;
	}
	
	/**
	 * Start date lekérdezése
	 * @return Az aktuálisan mutatott hónap első napja
	 */
	public LocalDate getStartDate() {
		return startDate;
	}
	
	@Override
	public int getColumnCount() { return 7; }
	
	@Override
	public int getRowCount() { return 5; }
	
	@Override
	public Object getValueAt(int row, int column) {
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		return days[column];
	}
}
