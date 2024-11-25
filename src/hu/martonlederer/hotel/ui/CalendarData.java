package hu.martonlederer.hotel.ui;

import java.time.LocalDate;

import javax.swing.table.AbstractTableModel;

public class CalendarData extends AbstractTableModel {
	public LocalDate startDate;
	
	private String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	private String[][] data = new String[6][7];
	
	public CalendarData(LocalDate startDate) {
		this.startDate = startDate;
		calculateCalendar();
	}
	
	/**
	 * Kitölti a naptárat az aktuális hónaphoz
	 */
	private void calculateCalendar() {
		int startDayOfWeek = startDate.getDayOfWeek().getValue() - 2;
		int daysInMonth = startDate.lengthOfMonth();
		
		for (int row = 0, day = 1; row < data.length; row++) {
			for (int col = 0; col < data[row].length; col++) {
				data[row][col] = (row != 0 || col > startDayOfWeek) && day <= daysInMonth ? Integer.toString(day++) : "";
			}
		}
	}
	
	/**
	 * Start date frissítése a naptárhoz
	 * @param date Új dátum
	 */
	public void setStartDate(LocalDate date) {
		startDate = date;
		calculateCalendar();
		fireTableDataChanged();
	}
	
	/**
	 * Start date lekérdezése
	 * @return Az aktuálisan mutatott hónap első napja
	 */
	public LocalDate getStartDate() {
		return startDate;
	}
	
	@Override
	public int getColumnCount() {
		return days.length;
	}
	
	@Override
	public int getRowCount() {
		return data.length;
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		return data[row][column];
	}
	
	@Override
	public String getColumnName(int column) {
		return days[column];
	}
}
