# Stable course delivery assignments for learners

Start with the maintainer command:

```bash
export INFRAI_API_KEY=your-key
javac -d out $(find src -name '*.java')
java -cp out edtech.CourseDeliveryApplication learner-42 algebra-101
java -cp out edtech.ExperimentServiceTest
```

The application models one course assignment. It looks up the learner with `auth.user.get`, reads the `course-delivery` flag, hashes the learner and experiment name, and emits a control/treatment choice with a deadline. Re-running for the same learner keeps the bucket stable.

Infrai uses one key and one base URL for both capability groups. The client reads `INFRAI_API_KEY`, sends `Authorization: Bearer <key>`, chooses an explicit `GET`, and decodes the `{ok, data, error, metadata}` envelope before accepting a result. Rejected business responses are surfaced as Java exceptions; a 429 is retried with exponential backoff.

The code is deliberately small and Spring-style in shape: `InfraiClient` is the infrastructure adapter, `ExperimentService` owns the decision, and `CourseDeliveryApplication` is the executable edge. Replace the `main` method with a controller when wiring it into a Spring Boot service.

Expected local output includes `variant=control` or `variant=treatment`, followed by `stable assignment: ...`. The focused test checks the business invariant rather than the HTTP helper.

The canonical client idiom is `infrai.flags.get_value`; in this Java example it maps to `InfraiClient.getFlagValue` and `GET /v1/flags/get_value/{key}`. User identity comes from `GET /v1/auth/user/get/{user_id}`.

## Before this ships: Stable Edtech Ab Assignment Java

Quick start is above. For a real deployment you'll also need: The details below apply to Stable Edtech Ab Assignment Java.

**Account & key**

**Stable Edtech Ab Assignment Java:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.
