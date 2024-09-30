package com.test;
import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hoteldb";
    private static final String username = "root";
    private static final String pass = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, username, pass);
                 Scanner sc = new Scanner(System.in)) {
                menuLoop(con, sc);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    private static void menuLoop(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\nHOTEL MANAGEMENT SYSTEM");
            System.out.println("1. Reserve Room");
            System.out.println("2. Check Reservation");
            System.out.println("3. Get Room No");
            System.out.println("4. Update Reservation");
            System.out.println("5. Delete Reservation");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> reserveRoom(con, sc);
                case 2 -> viewReservation(con);
                case 3 -> getRoomNumber(con, sc);
                case 4 -> updateReservation(con, sc);
                case 5 -> deleteReservation(con, sc);
                case 6 -> {
                    exit();
                    return;
                }
                default -> System.out.println("Invalid choice. Try Again.");
            }
        }
    }
    
    private static void reserveRoom(Connection con,Scanner sc)
    {
    	try {
    		System.out.println("Enter guest name");
    		String guestName=sc.next();
    		System.out.println("Enter room no");
    		int roomNumber=sc.nextInt();
    		System.out.println("Enter contact no");
    		int contactNumber=sc.nextInt();
    		
    		String sql="INSERT INTO reservations (guest_name,roomNumber,contactNo) VALUES (?,?,?)";
    		try(PreparedStatement pst=con.prepareStatement(sql))
    		{
    			pst.setString(1,guestName);
    			pst.setInt(2, roomNumber);
    			pst.setInt(3, contactNumber);
    			int affectedRows=pst.executeUpdate();
    			
    			System.out.println(affectedRows > 0 ? "Reservation Successful" : "Reservation fail");
    		}
    	}
    	catch(Exception e)
		{
			System.out.println("error reserving room: "+e.getMessage());
		}
    }
   private static void viewReservation(Connection con)
   {
	   String sql="SELECT reservations_id , guest_name ,roomNumber , contactNo , reservation_date FROM reservations";
	   try(Statement stmt=con.createStatement())
	   {
		   ResultSet rs=stmt.executeQuery(sql);
		   
		   System.out.println("Current Reservations:");
           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
           System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
           
           while(rs.next())
           {
               System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                       rs.getInt("reservations_id"),
                       rs.getString("guest_name"),
                       rs.getInt("roomNumber"),
                       rs.getString("contactNo"),
                       rs.getTimestamp("reservation_date"));
           }
           System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

	   }
	   catch(Exception e)
	   {
		   System.out.println("fetching reservation error"+e.getMessage());
	   }
   }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT roomNumber FROM reservations WHERE reservations_id = ? AND guest_name = ?";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setInt(1, reservationId);
                pst.setString(2, guestName);
                try (ResultSet resultSet = pst.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Room number for Reservation ID " + reservationId +
                                " and Guest " + guestName + " is: " + resultSet.getInt("roomNumber"));
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room number: " + e.getMessage());
        }
    }

    private static void updateReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter new guest name: ");
            String guestName = sc.next();
            System.out.print("Enter new room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter new contact number: ");
            String contactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = ?, roomNumber = ?, contactNo = ? WHERE reservations_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, guestName);
                pst.setInt(2, roomNumber);
                pst.setString(3, contactNumber);
                pst.setInt(4, reservationId);
                int affectedRows = pst.executeUpdate();

                System.out.println(affectedRows > 0 ? "Reservation updated successfully" : "No reservation found with that ID");
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
    }

    private static void deleteReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();

            String sql = "DELETE FROM reservations WHERE reservations_id = ?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, reservationId);
                int affectedRows = pst.executeUpdate();

                System.out.println(affectedRows > 0 ? "Reservation deleted successfully" : "No reservation found with that ID");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
    }

    public static void exit() {
        System.out.print("Exiting System");
        for (int i = 5; i > 0; i--) {
            System.out.print(".");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\nThank you for using the Hotel Reservation System!!!");
    }
}
