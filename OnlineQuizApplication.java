import java.sql.*;
import java.util.*;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Quiz Menu ---");
            System.out.println("1. Take Quiz");
            System.out.println("2. Add Question");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    takeQuiz();
                    break;
                case 2:
                    addQuestion();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static void takeQuiz() {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions");

            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correctOption").charAt(0)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int score = 0;
        for (Question q : questions) {
            System.out.println("\n" + q.question);
            System.out.println("A. " + q.optionA);
            System.out.println("B. " + q.optionB);
            System.out.println("C. " + q.optionC);
            System.out.println("D. " + q.optionD);
            System.out.print("Your answer (A/B/C/D): ");
            char answer = sc.nextLine().toUpperCase().charAt(0);

            if (answer == q.correctOption) {
                score++;
            }
        }

        System.out.println("\nQuiz Finished. Your Score: " + score + "/" + questions.size());
    }

    static void addQuestion() {
        System.out.print("Enter question: ");
        String question = sc.nextLine();
        System.out.print("Option A: ");
        String a = sc.nextLine();
        System.out.print("Option B: ");
        String b = sc.nextLine();
        System.out.print("Option C: ");
        String c = sc.nextLine();
        System.out.print("Option D: ");
        String d = sc.nextLine();
        System.out.print("Correct Option (A/B/C/D): ");
        char correct = sc.nextLine().toUpperCase().charAt(0);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO questions (question, optionA, optionB, optionC, optionD, correctOption) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, question);
            ps.setString(2, a);
            ps.setString(3, b);
            ps.setString(4, c);
            ps.setString(5, d);
            ps.setString(6, String.valueOf(correct));
            ps.executeUpdate();

            System.out.println("Question added.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// ========== Question Class ==========
class Question {
    int id;
    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    char correctOption;

    public Question(int id, String question, String optionA, String optionB, String optionC, String optionD, char correctOption) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }
}

// ========== DBConnection Class ==========
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String USER = "root"; // change to your MySQL username
    private static final String PASSWORD = ""; // change to your MySQL password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
