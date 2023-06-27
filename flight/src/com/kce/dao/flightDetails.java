package com.kce.dao;
import com.kce.bean.BusDetails;
import com.kce.util.DButil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

class InvalidDateException extends Exception {
	public InvalidDateException(String message) {
		super(message);
	}
}

public class flightDetails {
	static Scanner sc = new Scanner(System.in);

	public static void insert(int a) {
		try {
			BusDetails[] arr = new flightDetails[a];
			for (int i = 0; i < a; i++) {
				System.out.println("Enter flight id: ");
				int id = sc.nextInt();
				System.out.println("Enter the source: ");
				String source = sc.next();
				System.out.println("Enter the destination: ");
				String destination = sc.next();
				System.out.println("Enter the seat to be available: ");
				int seatsAvailable = sc.nextInt();
				System.out.println("Enter the date (DD/MM/YYYY): ");
				String dateStr = sc.next();
				System.out.println("Enter the price for single Seat: ");
				double priceOfSingleSeat = sc.nextDouble();

				LocalDate date = validateAndParseDate(dateStr);

				arr[i] = new flightDetails(id, source, destination, seatsAvailable, dateStr, priceOfSingleSeat);
			}

			Connection con = FMutil.getConnection();
			PreparedStatement stmt = con.prepareStatement("insert into bus values(?,?,?,?,?,?)");
			for (int i = 0; i < a; i++) {
				stmt.setInt(1, arr[i].getflightId());
				stmt.setString(2, arr[i].getSource());
				stmt.setString(3, arr[i].getDestination());
				stmt.setInt(4, arr[i].getSeatsAvailable());
				stmt.setString(5, arr[i].getDate());
				stmt.setDouble(6, arr[i].getPriceOfSingleSeat());
				stmt.executeUpdate();
			}

			System.out.println("Data collected successfully");
		} catch (SQLException e) {
			System.out.println(e);
		} catch (InvalidDateException e) {
			System.out.println(e.getMessage());
		}
	}

	private static LocalDate validateAndParseDate(String dateStr) throws InvalidDateException {
		try {
			return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (DateTimeParseException e) {
			throw new InvalidDateException("Invalid date format. Please enter the date in the format DD/MM/YYYY.");
		}
	}

	public static void update() {
		try {
			Connection con = FMutil.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE flight SET source = ? WHERE flightId = ?");
			System.out.println("Enter flight id: ");
			int id = sc.nextInt();
			System.out.println("Enter the new source: ");
			String source = sc.next();
			stmt.setString(1, source);
			stmt.setInt(2, id);
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Update successful");
			} else {
				System.out.println("No records found for the given bus id");
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void delete() {
		try {
			Connection con = FMutil.getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM bus WHERE flightId = ?");
			System.out.println("Enter flight id: ");
			int id = sc.nextInt();
			stmt.setInt(1, id);
			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Delete successful");
			} else {
				System.out.println("No records found for the given flight id");
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	public static void displayTable() {
		try {
			Connection con = FMutil.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM flight");
			ResultSet rs = stmt.executeQuery();

			System.out.println("flight Details:");
			System.out.println("---------------------------------------------------------------------------------------------------------------------------");
			System.out.printf("| %-20s | %-20s | %-20s | %-20s | %-20s | %-20s |%n", "Bus Id", "Source", "Destination","SeatsAvailable","Date","PriceOfSingleSeat ");

			while (rs.next()) {

				System.out.printf("| %-20s | %-20s | %-20s | %-20s | %-20s | %-20s |%n",rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getDouble(6));
			}

			rs.close();
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	public static double getSeatPrice(int flightId) {
		try {
			Connection con = FMutil.getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT priceOfSingleSeat FROM bus WHERE busId = ?");
			stmt.setInt(1, flightId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				double seatPrice = rs.getDouble("priceOfSingleSeat");
				return seatPrice;
			} else {
				throw new IllegalArgumentException("No records found for the given flight id");
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
