package edtech;

public final class ExperimentServiceTest {
    public static void main(String[] args) throws Exception {
        String first = ExperimentService.stableBucket("learner-42", "course-delivery");
        String second = ExperimentService.stableBucket("learner-42", "course-delivery");
        if (!first.equals(second)) throw new AssertionError("bucket changed for the same learner");
        if (first.equals(ExperimentService.stableBucket("learner-43", "course-delivery"))) {
            System.out.println("different learners may share a bucket");
        }
        System.out.println("stable assignment: " + first);
    }
}
