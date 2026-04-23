package flc;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private static final int MAX_CAPACITY = 4;

    private String lessonId;
    private Exercise exercise;
    private String day;           // "Saturday" or "Sunday"
    private TimeSlot timeSlot;
    private int weekNumber;
    private List<Booking> bookings;
    private List<Review> reviews;

    public Lesson(String lessonId, Exercise exercise, String day, TimeSlot timeSlot, int weekNumber) {
        this.lessonId = lessonId;
        this.exercise = exercise;
        this.day = day;
        this.timeSlot = timeSlot;
        this.weekNumber = weekNumber;
        this.bookings = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getLessonId() { return lessonId; }
    public Exercise getExercise() { return exercise; }
    public String getDay() { return day; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public int getWeekNumber() { return weekNumber; }
    public List<Booking> getBookings() { return bookings; }
    public List<Review> getReviews() { return reviews; }

    public boolean hasSpace() { return bookings.size() < MAX_CAPACITY; }
    public int getBookingCount() { return bookings.size(); }

    public boolean isMemberBooked(Member member) {
        for (Booking b : bookings)
            if (b.getMember().getMemberId().equals(member.getMemberId())) return true;
        return false;
    }

    public boolean addBooking(Booking booking) {
        if (!hasSpace()) return false;
        bookings.add(booking);
        return true;
    }

    public boolean removeBooking(Member member) {
        return bookings.removeIf(b -> b.getMember().getMemberId().equals(member.getMemberId()));
    }

    public void addReview(Review review) { reviews.add(review); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        double sum = 0;
        for (Review r : reviews) sum += r.getRating();
        return sum / reviews.size();
    }

    public double getTotalIncome() {
        return bookings.size() * exercise.getPrice();
    }

    @Override
    public String toString() {
        return String.format("Week%d %s %s %s (£%.2f) [%d/4]",
                weekNumber, day, timeSlot, exercise.getName(),
                exercise.getPrice(), bookings.size());
    }
}