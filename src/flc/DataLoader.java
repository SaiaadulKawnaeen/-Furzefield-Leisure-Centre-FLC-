package flc;

/**
 * Populates the BookingSystem with all seed data:
 * 10 members, 5 exercises, 8 weekends (48 lessons), 20+ reviews.
 */
public class DataLoader {

    public static void load(BookingSystem sys) {
        // ── Exercises (price fixed per exercise type) ────────────
        Exercise yoga     = new Exercise("Yoga",     8.00);
        Exercise zumba    = new Exercise("Zumba",   10.00);
        Exercise aquacise = new Exercise("Aquacise", 9.00);
        Exercise boxFit   = new Exercise("BoxFit",  12.00);
        Exercise bodyBlitz= new Exercise("BodyBlitz",11.00);

        sys.addExercise(yoga);
        sys.addExercise(zumba);
        sys.addExercise(aquacise);
        sys.addExercise(boxFit);
        sys.addExercise(bodyBlitz);

        // ── Members ──────────────────────────────────────────────
        Member[] members = {
            new Member("M01","Alice"),
            new Member("M02","Bob"),
            new Member("M03","Carol"),
            new Member("M04","David"),
            new Member("M05","Emma"),
            new Member("M06","Frank"),
            new Member("M07","Grace"),
            new Member("M08","Henry"),
            new Member("M09","Isla"),
            new Member("M10","Jack")
        };
        for (Member m : members) sys.addMember(m);

        // ── 8 Weekends × 2 days × 3 slots = 48 lessons ──────────
        // Layout per weekend:
        //   Saturday:   Morning=Yoga  Afternoon=Zumba   Evening=BoxFit
        //   Sunday:     Morning=Aquacise Afternoon=BodyBlitz Evening=Yoga
        // We rotate exercises slightly each weekend to create variety.
        Exercise[][] satExercises = {
            {yoga, zumba, boxFit},
            {zumba, aquacise, bodyBlitz},
            {aquacise, boxFit, yoga},
            {boxFit, bodyBlitz, zumba},
            {bodyBlitz, yoga, aquacise},
            {yoga, boxFit, bodyBlitz},
            {zumba, bodyBlitz, yoga},
            {aquacise, yoga, zumba}
        };
        Exercise[][] sunExercises = {
            {aquacise, bodyBlitz, yoga},
            {yoga, boxFit, zumba},
            {bodyBlitz, yoga, boxFit},
            {zumba, yoga, aquacise},
            {boxFit, zumba, bodyBlitz},
            {bodyBlitz, aquacise, yoga},
            {aquacise, boxFit, zumba},
            {yoga, zumba, boxFit}
        };

        TimeSlot[] slots = TimeSlot.values(); // MORNING, AFTERNOON, EVENING
        int lessonNum = 1;
        Lesson[][] allLessons = new Lesson[8][6]; // [week][0..5] sat0,sat1,sat2,sun0,sun1,sun2

        for (int w = 1; w <= 8; w++) {
            for (int s = 0; s < 3; s++) {
                String lid = "L" + String.format("%03d", lessonNum++);
                Lesson l = new Lesson(lid, satExercises[w-1][s], "Saturday", slots[s], w);
                sys.addLesson(l);
                allLessons[w-1][s] = l;
            }
            for (int s = 0; s < 3; s++) {
                String lid = "L" + String.format("%03d", lessonNum++);
                Lesson l = new Lesson(lid, sunExercises[w-1][s], "Sunday", slots[s], w);
                sys.addLesson(l);
                allLessons[w-1][s+3] = l;
            }
        }

        // ── Bookings ─────────────────────────────────────────────
        // Week 1
        sys.makeBooking(members[0], allLessons[0][0]); // Alice  - Sat Morn Yoga
        sys.makeBooking(members[1], allLessons[0][0]); // Bob    - Sat Morn Yoga
        sys.makeBooking(members[2], allLessons[0][1]); // Carol  - Sat Aft Zumba
        sys.makeBooking(members[3], allLessons[0][1]); // David  - Sat Aft Zumba
        sys.makeBooking(members[4], allLessons[0][2]); // Emma   - Sat Eve BoxFit
        sys.makeBooking(members[5], allLessons[0][3]); // Frank  - Sun Morn Aquacise
        sys.makeBooking(members[6], allLessons[0][3]); // Grace  - Sun Morn Aquacise
        sys.makeBooking(members[7], allLessons[0][4]); // Henry  - Sun Aft BodyBlitz
        sys.makeBooking(members[8], allLessons[0][5]); // Isla   - Sun Eve Yoga
        sys.makeBooking(members[9], allLessons[0][5]); // Jack   - Sun Eve Yoga

        // Week 2
        sys.makeBooking(members[0], allLessons[1][0]); // Alice  - Sat Morn Zumba
        sys.makeBooking(members[1], allLessons[1][1]); // Bob    - Sat Aft Aquacise
        sys.makeBooking(members[2], allLessons[1][2]); // Carol  - Sat Eve BodyBlitz
        sys.makeBooking(members[3], allLessons[1][3]); // David  - Sun Morn Yoga
        sys.makeBooking(members[4], allLessons[1][4]); // Emma   - Sun Aft BoxFit
        sys.makeBooking(members[5], allLessons[1][5]); // Frank  - Sun Eve Zumba

        // Week 3
        sys.makeBooking(members[6], allLessons[2][0]); // Grace  - Sat Morn Aquacise
        sys.makeBooking(members[7], allLessons[2][1]); // Henry  - Sat Aft BoxFit
        sys.makeBooking(members[8], allLessons[2][2]); // Isla   - Sat Eve Yoga
        sys.makeBooking(members[9], allLessons[2][3]); // Jack   - Sun Morn BodyBlitz
        sys.makeBooking(members[0], allLessons[2][4]); // Alice  - Sun Aft Yoga
        sys.makeBooking(members[1], allLessons[2][5]); // Bob    - Sun Eve BoxFit

        // Week 4
        sys.makeBooking(members[2], allLessons[3][0]); // Carol  - Sat Morn BoxFit
        sys.makeBooking(members[3], allLessons[3][1]); // David  - Sat Aft BodyBlitz
        sys.makeBooking(members[4], allLessons[3][2]); // Emma   - Sat Eve Zumba
        sys.makeBooking(members[5], allLessons[3][3]); // Frank  - Sun Morn Zumba
        sys.makeBooking(members[6], allLessons[3][4]); // Grace  - Sun Aft Yoga
        sys.makeBooking(members[7], allLessons[3][5]); // Henry  - Sun Eve Aquacise

        // ── Reviews (20+) ────────────────────────────────────────
        sys.addReview(members[0], allLessons[0][0], 5, "Loved the morning Yoga session!");
        sys.addReview(members[1], allLessons[0][0], 4, "Great instructor, very calming.");
        sys.addReview(members[2], allLessons[0][1], 3, "Zumba was ok, a bit crowded.");
        sys.addReview(members[3], allLessons[0][1], 4, "Fun and energetic class.");
        sys.addReview(members[4], allLessons[0][2], 5, "BoxFit was intense, loved it!");
        sys.addReview(members[5], allLessons[0][3], 2, "Aquacise was too slow for me.");
        sys.addReview(members[6], allLessons[0][3], 4, "Nice and refreshing Aquacise.");
        sys.addReview(members[7], allLessons[0][4], 5, "BodyBlitz really challenged me.");
        sys.addReview(members[8], allLessons[0][5], 3, "Evening Yoga was relaxing.");
        sys.addReview(members[9], allLessons[0][5], 5, "Perfect end to the weekend.");

        sys.addReview(members[0], allLessons[1][0], 4, "Zumba on Saturday morning - great energy.");
        sys.addReview(members[1], allLessons[1][1], 3, "Aquacise was decent.");
        sys.addReview(members[2], allLessons[1][2], 5, "BodyBlitz Saturday evening - superb!");
        sys.addReview(members[3], allLessons[1][3], 4, "Sunday Yoga was peaceful.");
        sys.addReview(members[4], allLessons[1][4], 5, "BoxFit Sunday afternoon was brilliant.");
        sys.addReview(members[5], allLessons[1][5], 2, "Zumba wasn't my thing.");

        sys.addReview(members[6], allLessons[2][0], 4, "Aquacise was refreshing this week.");
        sys.addReview(members[7], allLessons[2][1], 5, "BoxFit afternoon - pushed to the limit!");
        sys.addReview(members[8], allLessons[2][2], 3, "Yoga was good but room was warm.");
        sys.addReview(members[9], allLessons[2][3], 4, "BodyBlitz Sunday morning - tough but great.");
        sys.addReview(members[0], allLessons[2][4], 5, "Yoga again - still loving it.");
        sys.addReview(members[2], allLessons[3][0], 4, "BoxFit on Saturday - great workout.");
        sys.addReview(members[3], allLessons[3][1], 3, "BodyBlitz was ok, instructor was late.");
    }
}