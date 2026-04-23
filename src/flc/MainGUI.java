package flc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainGUI extends JFrame {

    private BookingSystem sys;
    private JTextArea outputArea;

    public MainGUI(BookingSystem sys) {
        this.sys = sys;
        setTitle("Furzefield Leisure Centre - Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        // ── Sidebar buttons ──────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 5, 5));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebar.setBackground(new Color(50, 80, 120));

        String[] btnLabels = {
            "Timetable by Day",
            "Timetable by Exercise",
            "Make Booking",
            "Change Booking",
            "Add Review",
            "My Bookings",
            "Attendance Report",
            "Income Report"
        };

        for (String label : btnLabels) {
            JButton btn = new JButton(label);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.addActionListener(e -> handleAction(label));
            sidebar.add(btn);
        }

        // ── Output area ──────────────────────────────────────────
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setMargin(new Insets(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(outputArea);

        // ── Layout ───────────────────────────────────────────────
        JLabel title = new JLabel("  Furzefield Leisure Centre", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setOpaque(true);
        title.setBackground(new Color(30, 50, 90));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(title, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
    }

    private void handleAction(String action) {
        switch (action) {
            case "Timetable by Day"       -> showTimetableByDay();
            case "Timetable by Exercise"  -> showTimetableByExercise();
            case "Make Booking"           -> makeBooking();
            case "Change Booking"         -> changeBooking();
            case "Add Review"             -> addReview();
            case "My Bookings"            -> showMyBookings();
            case "Attendance Report"      -> outputArea.setText(sys.generateAttendanceReport());
            case "Income Report"          -> outputArea.setText(sys.generateIncomeReport());
        }
    }

    // ── Timetable by Day ────────────────────────────────────────
    private void showTimetableByDay() {
        String[] days = {"Saturday", "Sunday"};
        String day = (String) JOptionPane.showInputDialog(this,
                "Select day:", "Timetable by Day",
                JOptionPane.PLAIN_MESSAGE, null, days, days[0]);
        if (day == null) return;

        List<Lesson> list = sys.getLessonsByDay(day);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Timetable for ").append(day).append(" ===\n\n");
        sb.append(String.format("%-8s %-5s %-11s %-10s %-8s %-6s%n",
                "ID", "Week", "Time", "Exercise", "Price", "Spaces"));
        sb.append("-".repeat(55)).append("\n");
        for (Lesson l : list) {
            sb.append(String.format("%-8s %-5d %-11s %-10s £%-7.2f %-6d%n",
                    l.getLessonId(), l.getWeekNumber(), l.getTimeSlot(),
                    l.getExercise().getName(), l.getExercise().getPrice(),
                    4 - l.getBookingCount()));
        }
        outputArea.setText(sb.toString());
    }

    // ── Timetable by Exercise ────────────────────────────────────
    private void showTimetableByExercise() {
        String[] names = sys.getExercises().stream()
                .map(Exercise::getName).toArray(String[]::new);
        String ex = (String) JOptionPane.showInputDialog(this,
                "Select exercise:", "Timetable by Exercise",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
        if (ex == null) return;

        List<Lesson> list = sys.getLessonsByExercise(ex);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Timetable for ").append(ex).append(" ===\n\n");
        sb.append(String.format("%-8s %-5s %-10s %-11s %-8s %-6s%n",
                "ID", "Week", "Day", "Time", "Price", "Spaces"));
        sb.append("-".repeat(55)).append("\n");
        for (Lesson l : list) {
            sb.append(String.format("%-8s %-5d %-10s %-11s £%-7.2f %-6d%n",
                    l.getLessonId(), l.getWeekNumber(), l.getDay(),
                    l.getTimeSlot(), l.getExercise().getPrice(),
                    4 - l.getBookingCount()));
        }
        outputArea.setText(sb.toString());
    }

    // ── Make Booking ─────────────────────────────────────────────
    private void makeBooking() {
        Member member = selectMember();
        if (member == null) return;
        Lesson lesson = selectLesson("Select Lesson to Book");
        if (lesson == null) return;

        String result = sys.makeBooking(member, lesson);
        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed! ID: " + result.substring(3));
            outputArea.setText("Booked: " + member.getName() + " -> " + lesson);
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Change Booking ───────────────────────────────────────────
    private void changeBooking() {
        Member member = selectMember();
        if (member == null) return;

        List<Booking> myBookings = sys.getBookingsForMember(member);
        if (myBookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found for this member.");
            return;
        }
        String[] bookingLabels = myBookings.stream()
                .map(b -> b.getBookingId() + " - " + b.getLesson())
                .toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this,
                "Select booking to change:", "Change Booking",
                JOptionPane.PLAIN_MESSAGE, null, bookingLabels, bookingLabels[0]);
        if (chosen == null) return;
        Booking selectedBooking = myBookings.get(java.util.Arrays.asList(bookingLabels).indexOf(chosen));
        Lesson fromLesson = selectedBooking.getLesson();

        Lesson toLesson = selectLesson("Select New Lesson");
        if (toLesson == null) return;

        String result = sys.changeBooking(member, fromLesson, toLesson);
        if (result.startsWith("OK")) {
            JOptionPane.showMessageDialog(this,
                    "Booking changed! New ID: " + result.substring(3));
            outputArea.setText("Changed: " + member.getName() + " from " + fromLesson + "\n  to " + toLesson);
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Add Review ───────────────────────────────────────────────
    private void addReview() {
        Member member = selectMember();
        if (member == null) return;
        Lesson lesson = selectLesson("Select Attended Lesson");
        if (lesson == null) return;

        String[] ratings = {"1 - Very Dissatisfied", "2 - Dissatisfied",
                            "3 - Ok", "4 - Satisfied", "5 - Very Satisfied"};
        String ratingStr = (String) JOptionPane.showInputDialog(this,
                "Select rating:", "Rating", JOptionPane.PLAIN_MESSAGE, null, ratings, ratings[2]);
        if (ratingStr == null) return;
        int rating = Integer.parseInt(ratingStr.substring(0, 1));

        String comment = JOptionPane.showInputDialog(this, "Enter your comment:");
        if (comment == null) comment = "";

        String result = sys.addReview(member, lesson, rating, comment);
        if (result.equals("OK")) {
            JOptionPane.showMessageDialog(this, "Review submitted. Thank you!");
            outputArea.setText("Review added for " + lesson.getExercise().getName()
                    + " by " + member.getName() + " - Rating: " + rating + "/5");
        } else {
            JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── My Bookings ──────────────────────────────────────────────
    private void showMyBookings() {
        Member member = selectMember();
        if (member == null) return;
        List<Booking> list = sys.getBookingsForMember(member);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Bookings for ").append(member.getName()).append(" ===\n\n");
        if (list.isEmpty()) {
            sb.append("No bookings found.");
        } else {
            for (Booking b : list)
                sb.append(b).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    // ── Helpers ──────────────────────────────────────────────────
    private Member selectMember() {
        List<Member> members = sys.getMembers();
        String[] labels = members.stream().map(Member::toString).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this,
                "Select member:", "Member", JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
        if (chosen == null) return null;
        int idx = java.util.Arrays.asList(labels).indexOf(chosen);
        return members.get(idx);
    }

    private Lesson selectLesson(String title) {
        List<Lesson> lessons = sys.getLessons();
        String[] labels = lessons.stream().map(Lesson::toString).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this,
                "Select lesson:", title, JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
        if (chosen == null) return null;
        int idx = java.util.Arrays.asList(labels).indexOf(chosen);
        return lessons.get(idx);
    }

    // ── Main ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        BookingSystem sys = new BookingSystem();
        DataLoader.load(sys);
        SwingUtilities.invokeLater(() -> new MainGUI(sys).setVisible(true));
    }
}