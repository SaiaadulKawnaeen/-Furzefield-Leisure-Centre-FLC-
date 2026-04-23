package flc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingSystem {

    private List<Member> members;
    private List<Exercise> exercises;
    private List<Lesson> lessons;
    private List<Booking> allBookings;
    private int bookingCounter = 1;

    public BookingSystem() {
        members = new ArrayList<>();
        exercises = new ArrayList<>();
        lessons = new ArrayList<>();
        allBookings = new ArrayList<>();
    }

    // ── Registration ────────────────────────────────────────────
    public void addMember(Member m) { members.add(m); }
    public void addExercise(Exercise e) { exercises.add(e); }
    public void addLesson(Lesson l) { lessons.add(l); }

    public List<Member> getMembers() { return members; }
    public List<Exercise> getExercises() { return exercises; }
    public List<Lesson> getLessons() { return lessons; }

    // ── Timetable queries ────────────────────────────────────────
    public List<Lesson> getLessonsByDay(String day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons)
            if (l.getDay().equalsIgnoreCase(day)) result.add(l);
        return result;
    }

    public List<Lesson> getLessonsByExercise(String exerciseName) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons)
            if (l.getExercise().getName().equalsIgnoreCase(exerciseName)) result.add(l);
        return result;
    }

    // ── Booking ──────────────────────────────────────────────────
    public String makeBooking(Member member, Lesson lesson) {
        if (!lesson.hasSpace())
            return "FAIL: Lesson is full.";
        if (lesson.isMemberBooked(member))
            return "FAIL: Already booked for this lesson.";
        if (hasTimeConflict(member, lesson))
            return "FAIL: Time conflict with an existing booking.";

        String id = "B" + String.format("%03d", bookingCounter++);
        Booking booking = new Booking(id, member, lesson);
        lesson.addBooking(booking);
        allBookings.add(booking);
        return "OK:" + id;
    }

    private boolean hasTimeConflict(Member member, Lesson newLesson) {
        for (Booking b : allBookings) {
            if (!b.getMember().getMemberId().equals(member.getMemberId())) continue;
            Lesson existing = b.getLesson();
            if (existing.getWeekNumber() == newLesson.getWeekNumber()
                    && existing.getDay().equalsIgnoreCase(newLesson.getDay())
                    && existing.getTimeSlot() == newLesson.getTimeSlot()) {
                return true;
            }
        }
        return false;
    }

    // ── Change booking ───────────────────────────────────────────
    public String changeBooking(Member member, Lesson fromLesson, Lesson toLesson) {
        if (!fromLesson.isMemberBooked(member))
            return "FAIL: No booking found on source lesson.";
        if (!toLesson.hasSpace())
            return "FAIL: Target lesson is full.";
        if (toLesson.isMemberBooked(member))
            return "FAIL: Already booked on target lesson.";

        // Temporarily remove to avoid false conflict on same slot
        fromLesson.removeBooking(member);
        allBookings.removeIf(b ->
                b.getMember().getMemberId().equals(member.getMemberId())
                && b.getLesson().getLessonId().equals(fromLesson.getLessonId()));

        if (hasTimeConflict(member, toLesson)) {
            // Roll back
            String id = "B" + String.format("%03d", bookingCounter++);
            Booking rb = new Booking(id, member, fromLesson);
            fromLesson.addBooking(rb);
            allBookings.add(rb);
            return "FAIL: Time conflict with another booking.";
        }

        String id = "B" + String.format("%03d", bookingCounter++);
        Booking newBooking = new Booking(id, member, toLesson);
        toLesson.addBooking(newBooking);
        allBookings.add(newBooking);
        return "OK:" + id;
    }

    // ── Reviews ──────────────────────────────────────────────────
    public String addReview(Member member, Lesson lesson, int rating, String comment) {
        if (!lesson.isMemberBooked(member))
            return "FAIL: Member has not attended this lesson.";
        if (rating < 1 || rating > 5)
            return "FAIL: Rating must be 1-5.";
        Review review = new Review(member, lesson, rating, comment);
        lesson.addReview(review);
        return "OK";
    }

    // ── Reports ──────────────────────────────────────────────────
    public String generateAttendanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ATTENDANCE & RATING REPORT ===\n\n");
        sb.append(String.format("%-6s %-10s %-10s %-10s %-5s %-12s%n",
                "Week", "Day", "Time", "Exercise", "Booked", "Avg Rating"));
        sb.append("-".repeat(60)).append("\n");
        for (Lesson l : lessons) {
            String rating = l.getReviews().isEmpty() ? "N/A"
                    : String.format("%.1f", l.getAverageRating());
            sb.append(String.format("%-6d %-10s %-10s %-10s %-5d %-12s%n",
                    l.getWeekNumber(), l.getDay(), l.getTimeSlot(),
                    l.getExercise().getName(), l.getBookingCount(), rating));
        }
        return sb.toString();
    }

    public String generateIncomeReport() {
        Map<String, Double> incomeMap = new HashMap<>();
        for (Lesson l : lessons) {
            String name = l.getExercise().getName();
            incomeMap.put(name, incomeMap.getOrDefault(name, 0.0) + l.getTotalIncome());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== INCOME REPORT ===\n\n");
        sb.append(String.format("%-12s %s%n", "Exercise", "Total Income"));
        sb.append("-".repeat(30)).append("\n");

        String topExercise = null;
        double topIncome = -1;
        for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
            sb.append(String.format("%-12s £%.2f%n", entry.getKey(), entry.getValue()));
            if (entry.getValue() > topIncome) {
                topIncome = entry.getValue();
                topExercise = entry.getKey();
            }
        }
        sb.append("\nHighest income: ").append(topExercise)
          .append(" (£").append(String.format("%.2f", topIncome)).append(")\n");
        return sb.toString();
    }

    // ── Lookup helpers ───────────────────────────────────────────
    public Member findMember(String memberId) {
        for (Member m : members)
            if (m.getMemberId().equals(memberId)) return m;
        return null;
    }

    public Lesson findLesson(String lessonId) {
        for (Lesson l : lessons)
            if (l.getLessonId().equals(lessonId)) return l;
        return null;
    }

    public List<Booking> getBookingsForMember(Member member) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : allBookings)
            if (b.getMember().getMemberId().equals(member.getMemberId())) result.add(b);
        return result;
    }
}