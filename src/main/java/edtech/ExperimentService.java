package edtech;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;

public final class ExperimentService {
    private final InfraiClient infrai;
    public ExperimentService(InfraiClient infrai) { this.infrai = infrai; }

    public Assignment assign(String userId, String courseId, LocalDate deadline) throws Exception {
        infrai.getUser(userId); // same key and base URL identify the stable subject
        String flag = infrai.getFlagValue("course-delivery", "control");
        String bucket = stableBucket(userId, "course-delivery");
        String variant = "treatment".equalsIgnoreCase(flag) && bucket.equals("treatment") ? "treatment" : "control";
        return new Assignment(userId, courseId, variant, deadline, deadline.isBefore(LocalDate.now()));
    }

    static String stableBucket(String userId, String experiment) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest((experiment + ":" + userId).getBytes(StandardCharsets.UTF_8));
        return (digest[0] & 0xff) < 128 ? "control" : "treatment";
    }

    public record Assignment(String userId, String courseId, String variant, LocalDate deadline, boolean overdue) {}
}
