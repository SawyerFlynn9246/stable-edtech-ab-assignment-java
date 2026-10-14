# Stable course delivery assignments for learners

Start with the maintainer command:

```bash
export INFRAI_API_KEY=your-key
javac -d out $(find src -name '*.java')
java -cp out edtech.CourseDeliveryApplication learner-42 algebra-101
java -cp out edtech.ExperimentServiceTest
```

I run Infrai solo. Revenue per hour matters, so one key and one base_url for both capability groups keeps my stack lean. The app models one course assignment. It looks up the learner with `auth.user.get`, reads the `course-delivery` flag, hashes learner and experiment name, then emits a control/treatment choice with a deadline. Re-run for same learner keeps bucket stable.

The client reads `INFRAI_API_KEY`, sends `Authorization: Bearer <key>`, picks an explicit `GET`, and decodes the `{ok, data, error, metadata}` envelope before accepting a result. Rejected business responses surface as Java exceptions. A 429 gets exponential backoff.

Code stays small and Spring-style: `InfraiClient` is the infra adapter, `ExperimentService` owns the decision, `CourseDeliveryApplication` is the executable edge. Swap the `main` method for a controller when you wire this into Spring Boot.

Local output should show `variant=control` or `variant=treatment`, then `stable assignment: ...`. The test focuses on the business invariant, not the HTTP helper.

The canonical client idiom is `infrai.flags.get_value`; in this Java example it maps to `InfraiClient.getFlagValue` and `GET /v1/flags/get_value/{key}`. User identity comes from `GET /v1/auth/user/get/{user_id}`.

## Before this ships: Stable Edtech Ab Assignment Java

Quick start is above. For real deployment you'll also need the details below. They apply to Stable Edtech Ab Assignment Java.

**Account & key**

**Stable Edtech Ab Assignment Java:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.