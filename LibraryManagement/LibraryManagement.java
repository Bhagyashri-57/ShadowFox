import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class LibraryManagement {

    static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        createTables();

        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Search Book");
            System.out.println("7. Add User");
            System.out.println("8. View Users");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addBook(sc);
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    issueBook(sc);
                    break;
                case 4:
                    returnBook(sc);
                    break;
                case 5:
                    deleteBook(sc);
                    break;
                case 6:
                    searchBook(sc);
                    break;
                case 7:
                    addUser(sc);
                    break;
                case 8:
                    viewUsers();
                    break;
                case 9:
                    System.out.println("Exiting Library System...");
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    static void createTables() {
        String booksTable = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "status TEXT NOT NULL)";

        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {

            stmt.execute(booksTable);
            stmt.execute(usersTable);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void addBook(Scanner sc) {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();

        String sql = "INSERT INTO books(title, author, status) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, "Available");

            pstmt.executeUpdate();
            System.out.println("Book Added Successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void viewBooks() {
        String sql = "SELECT * FROM books";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n----- BOOK LIST -----");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Title: " + rs.getString("title") +
                        " | Author: " + rs.getString("author") +
                        " | Status: " + rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void issueBook(Scanner sc) {
        System.out.print("Enter Book ID to Issue: ");
        int id = sc.nextInt();

        String sql = "UPDATE books SET status = 'Issued' WHERE id = ? AND status = 'Available'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("Book Issued Successfully!");
            else
                System.out.println("Book not available or invalid ID.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void returnBook(Scanner sc) {
        System.out.print("Enter Book ID to Return: ");
        int id = sc.nextInt();

        String sql = "UPDATE books SET status = 'Available' WHERE id = ? AND status = 'Issued'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("Book Returned Successfully!");
            else
                System.out.println("Invalid return request.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void deleteBook(Scanner sc) {
        System.out.print("Enter Book ID to Delete: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("Book Deleted Successfully!");
            else
                System.out.println("Book ID not found.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void searchBook(Scanner sc) {
        System.out.print("Enter Book Title to Search: ");
        String title = sc.nextLine();

        String sql = "SELECT * FROM books WHERE title LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n----- SEARCH RESULT -----");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Title: " + rs.getString("title") +
                        " | Author: " + rs.getString("author") +
                        " | Status: " + rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void addUser(Scanner sc) {
        System.out.print("Enter User Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);

            pstmt.executeUpdate();
            System.out.println("User Added Successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void viewUsers() {
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n----- USER LIST -----");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") +
                        " | Email: " + rs.getString("email")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
