package flc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookingSystemTest {

    private BookingSystem sys;
    private Member alice, bob, carol;
    private Exercise yoga, zumba;
    private Lesson satMornYoga, satAftZumba, satMornYoga2;

    @BeforeEach
    void setUp() {
        sys = new BookingSystem();

        yoga  = new Exercise("Yoga",  8.00);
        zumba = new Exercise("Zumba", 10.00);
        sys.addExercise(yoga);
        sys.addExercise(zumba);

        alice = new Member("M01", "Alice");
        bob   = new Member("M02", "Bob");
        carol = new Member("M03", "Carol");
        sys.addMember(alice);
        sys.addMember(bob);
        sys.addMember(carol);

        // Week 1, Saturday: Morning=Yoga, Afternoon=Zumba
        satMornYoga  = new Lesson("L001", yoga,  "Saturday", TimeSlot.MORNING,   1);
        satAftZumba  = new Lesson("L002", zumba, "Saturday", TimeSlot.AFTERNOON, 1);
        satMornYoga2 = new Lesson("L003", yoga,  "Saturday", TimeSlot.MORNING,   2);
        sys.addLesson(satMornYoga);
        sys.addLesson(satAftZumba);
        sys.addLesson(satMornYoga2);
    }

    // ── Booking tests ────────────────────────────────────────────

    @Test
    void testSuccessfulBooking() {
        String result = sys.makeBooking(alice, satMornYoga);
        assertTrue(result.startsWith("OK"), "Booking should succeed");
        assertEquals(1, satMornYoga.getBookingCount());
        assertTrue(satMornYoga.isMemberBooked(alice));
    }

    @Test
    void testDuplicateBookingRejected() {
        sys.makeBooking(alice, satMornYoga);
        String result = sys.makeBooking(alice, satMornYoga);
        assertTrue(result.startsWith("FAIL"), "Duplicate booking should fail");
        assertEquals(1, satMornYoga.getBookingCount());
    }

    @Test
    void testCapacityEnforced() {
        Member m1 = new Member("T01", "T1");
        Member m2 = new Member("T02", "T2");
        Member m3 = new Member("T03", "T3");
        Member m4 = new Member("T04", "T4");
        Member m5 = new Member("T05", "T5");
        sys.makeBooking(m1, satMornYoga);
        sys.makeBooking(m2, satMornYoga);
        sys.makeBooking(m3, satMornYoga);
        sys.makeBooking(m4, satMornYoga);
        String result = sys.makeBooking(m5, satMornYoga);
        assertTrue(result.startsWith("FAIL"), "5th booking should fail - lesson full");
        assertEquals(4, satMornYoga.getBookingCount());
    }

    @Test
    void testTimeConflictRejected() {
        // Another lesson at same week/day/slot
        Lesson conflict = new Lesson("L004", zumba, "Saturday", TimeSlot.MORNING, 1);
        sys.addLesson(conflict);
        sys.makeBooking(alice, satMornYoga);
        String result = sys.makeBooking(alice, conflict);
        assertTrue(result.startsWith("FAIL"), "Time conflict should fail");
    }

    @Test
    void testNoConflictDifferentWeek() {
        sys.makeBooking(alice, satMornYoga);    // Week 1 Sat Morning
        String result = sys.makeBooking(alice, satMornYoga2); // Week 2 Sat Morning
        assertTrue(result.startsWith("OK"), "Different week - no conflict");
    }

    @Test
    void testNoConflictDifferentSlot() {
        sys.makeBooking(alice, satMornYoga);   // Saturday Morning
        String result = sys.makeBooking(alice, satAftZumba); // Saturday Afternoon
        assertTrue(result.startsWith("OK"), "Different time slot - no conflict");
    }

    // ── Change booking tests ─────────────────────────────────────

    @Test
    void testChangeBookingSuccess() {
        sys.makeBooking(alice, satMornYoga);
        String result = sys.changeBooking(alice, satMornYoga, satAftZumba);
        assertTrue(result.startsWith("OK"), "Change booking should succeed");
        assertFalse(satMornYoga.isMemberBooked(alice), "Old lesson should be vacated");
        assertTrue(satAftZumba.isMemberBooked(alice), "New lesson should be booked");
    }

    @Test
    void testChangeBookingToFullLesson() {
        Member m1 = new Member("T01","T1");
        Member m2 = new Member("T02","T2");
        Member m3 = new Member("T03","T3");
        Member m4 = new Member("T04","T4");
        sys.makeBooking(m1, satAftZumba);
        sys.makeBooking(m2, satAftZumba);
        sys.makeBooking(m3, satAftZumba);
        sys.makeBooking(m4, satAftZumba);

        sys.makeBooking(alice, satMornYoga);
        String result = sys.changeBooking(alice, satMornYoga, satAftZumba);
        assertTrue(result.startsWith("FAIL"), "Change to full lesson should fail");
        assertTrue(satMornYoga.isMemberBooked(alice), "Original booking should remain");
    }

    @Test
    void testChangeBookingNoExistingBooking() {
        String result = sys.changeBooking(alice, satMornYoga, satAftZumba);
        assertTrue(result.startsWith("FAIL"), "Should fail if no existing booking");
    }

    // ── Review tests ─────────────────────────────────────────────

    @Test
    void testValidReview() {
        sys.makeBooking(alice, satMornYoga);
        String result = sys.addReview(alice, satMornYoga, 5, "Great!");
        assertEquals("OK", result);
        assertEquals(1, satMornYoga.getReviews().size());
        assertEquals(5.0, satMornYoga.getAverageRating());
    }

    @Test
    void testReviewByNonAttendee() {
        String result = sys.addReview(alice, satMornYoga, 4, "Nice.");
        assertTrue(result.startsWith("FAIL"), "Non-attendee should not review");
    }

    @Test
    void testInvalidRating() {
        sys.makeBooking(alice, satMornYoga);
        String result = sys.addReview(alice, satMornYoga, 6, "Invalid rating");
        assertTrue(result.startsWith("FAIL"), "Invalid rating should fail");
    }

    @Test
    void testAverageRating() {
        sys.makeBooking(alice, satMornYoga);
        sys.makeBooking(bob, satMornYoga);
        sys.addReview(alice, satMornYoga, 4, "Good");
        sys.addReview(bob, satMornYoga, 2, "Not great");
        assertEquals(3.0, satMornYoga.getAverageRating(), 0.001);
    }

    @Test
    void testAverageRatingNoReviews() {
        assertEquals(0.0, satMornYoga.getAverageRating());
    }

    // ── Timetable query tests ────────────────────────────────────

    @Test
    void testGetLessonsByDay() {
        List<Lesson> sat = sys.getLessonsByDay("Saturday");
        assertEquals(3, sat.size());
        for (Lesson l : sat)
            assertEquals("Saturday", l.getDay());
    }

    @Test
    void testGetLessonsByExercise() {
        List<Lesson> yogaLessons = sys.getLessonsByExercise("Yoga");
        assertEquals(2, yogaLessons.size()); // L001 and L003
    }

    // ── Income report test ───────────────────────────────────────

    @Test
    void testIncomeCalculation() {
        sys.makeBooking(alice, satMornYoga); // £8
        sys.makeBooking(bob, satMornYoga);   // £8
        // 2 bookings × £8 = £16
        assertEquals(16.0, satMornYoga.getTotalIncome(), 0.001);
    }

    // ── Lesson space test ────────────────────────────────────────

    @Test
    void testLessonHasSpace() {
        assertTrue(satMornYoga.hasSpace());
        sys.makeBooking(alice, satMornYoga);
        sys.makeBooking(bob, satMornYoga);
        sys.makeBooking(carol, satMornYoga);
        Member m4 = new Member("T04","T4");
        sys.makeBooking(m4, satMornYoga);
        assertFalse(satMornYoga.hasSpace());
    }

}