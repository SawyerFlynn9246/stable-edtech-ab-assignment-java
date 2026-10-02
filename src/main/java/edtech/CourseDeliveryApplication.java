package edtech;

import java.time.LocalDate;

public final class CourseDeliveryApplication {
    public static void main(String[] args) throws Exception {
        String user = args.length > 0 ? args[0] : "learner-42";
        String course = args.length > 1 ? args[1] : "algebra-101";
        var assignment = new ExperimentService(new InfraiClient()).assign(user, course, LocalDate.now().plusDays(14));
        System.out.printf("user=%s course=%s variant=%s deadline=%s overdue=%s%n", assignment.userId(), assignment.courseId(), assignment.variant(), assignment.deadline(), assignment.overdue());
    }
}
