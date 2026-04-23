package flc;

public class Review {
    private Member member;
    private Lesson lesson;
    private int rating;       // 1-5
    private String comment;

    public Review(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        this.member = member;
        this.lesson = lesson;
        this.rating = rating;
        this.comment = comment;
    }

    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return member.getName() + " rated " + lesson.getExercise().getName()
                + " " + rating + "/5: " + comment;
    }
}