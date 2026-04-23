package flc;

public class Booking {
    private String bookingId;
    private Member member;
    private Lesson lesson;

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
    }

    public String getBookingId() { return bookingId; }
    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }

    @Override
    public String toString() {
        return bookingId + ": " + member.getName() + " -> " + lesson;
    }
}