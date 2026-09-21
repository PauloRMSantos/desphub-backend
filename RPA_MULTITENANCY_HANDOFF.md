# Task — wire per-request gov.br session (multi-tenant) into the RPA query call

> Handoff from the RPA project. Self-contained: read top to bottom, then implement.

## Background

The DespHub RPA microservice (`POST /api/detran/queries`) previously relied on a
**single global gov.br token** injected via `POST /api/detran/session/token`. That
is a multi-tenancy bug: with more than one office (escritório) connected, office
B's token overwrites office A's inside the RPA, so a query for A would run under
B's session — wrong data / cross-tenant leak (also an LGPD problem).

**The RPA was fixed to be multi-tenant.** Its query endpoint now accepts the
caller's gov.br session **per request**, so the RPA keeps no shared state and two
offices never collide. This task wires the backend to use that path.

## New RPA contract (already deployed)

`POST /api/detran/queries` now accepts an optional `session` object:

```json
{
  "plate": "IDX1756",
  "renavam": "00561040575",
  "types": ["REGISTRATION", "DEBTS", "RESTRICTIONS", "LICENSING"],
  "session": {
    "bearer": "<the office's gov.br JWT>",
    "userId": "<the office's X-User-Id, base64 CPF>"
  }
}
```

- When `session` is present, the RPA uses **exactly those credentials** for that
  one request and stores nothing globally.
- When `session` is omitted, the RPA falls back to the old global token. We want
  the backend to **always send `session`** so the system is multi-tenant.

Consequence: the backend must now **persist each office's `{bearer, userId}`** so
it can attach them to every query. Today `GovbrSession` stores only
`connected`/`expiresAt` — the actual token lives only inside the RPA. That must
change.

## Changes to make (this repo: `desphub/backend`)

### 1. Persist the session per office (encrypted at rest)

`models/GovbrSession.java` — add two columns holding the captured session:

```java
@Column(name = "bearer", columnDefinition = "text")
@Convert(converter = EncryptedStringConverter.class)
private String bearer;

@Column(name = "user_id")
@Convert(converter = EncryptedStringConverter.class)
private String userId;
```

- Add a JPA `AttributeConverter<String,String>` (`EncryptedStringConverter`) that
  encrypts on write / decrypts on read. Use Spring Security Crypto
  (`org.springframework.security.crypto.encrypt.Encryptors.text(secret, salt)`)
  or AES-GCM. The key comes from config/env (e.g. `desphub.crypto.secret`), never
  hard-coded.
- **These are sensitive credentials.** Encrypt at rest, never log them, keep the
  DB column access tight.

Add a Flyway migration (e.g. `V7__govbr_session_token.sql`):

```sql
ALTER TABLE govbr_session ADD COLUMN bearer  text;
ALTER TABLE govbr_session ADD COLUMN user_id text;
```
(Columns are encrypted at the application layer, so `text` is fine.)

### 2. Save the session when the extension delivers it

`services/GovbrSessionService.java`, in `relay(pairingToken, bearer, userId)` —
when persisting the `GovbrSession`, also store the credentials:

```java
session.setConnected(true);
session.setExpiresAt(expiresAt);
session.setBearer(bearer);     // NEW — encrypted by the converter
session.setUserId(userId);     // NEW
sessionRepository.save(session);
```

Keep pushing to the RPA via `RpaSessionClient.pushSession(...)` **only if you also
keep single-tenant/manual as a fallback**. In the pure multi-tenant design the
per-request `session` makes `POST /session/token` unnecessary — you may drop that
relay call. Recommended: drop it and rely on per-request `session`.

### 3. Send the office's session on every query

`dtos/detran/VehicleQueryRequest.java` — add the session:

```java
public record VehicleQueryRequest(
        String plate,
        String renavam,
        List<String> types,
        SessionCredentials session   // NEW
) {
    public record SessionCredentials(String bearer, String userId) {}
}
```

`integrations/RpaDetranClient.java` — take the office's session and put it in the
body:

```java
public VehicleQueryResponse query(String plate, String renavam,
                                  String bearer, String userId) {
    var session = new VehicleQueryRequest.SessionCredentials(bearer, userId);
    return http.post()
            .uri("/api/detran/queries")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new VehicleQueryRequest(plate, renavam, DEFAULT_TYPES, session))
            .exchange(/* unchanged */);
}
```

### 4. Resolve the current office's session before querying

In the service that calls `RpaDetranClient.query(...)` (the vehicle-query use
case): load the current office's `GovbrSession`, and:

- If it does not exist / `connected == false` / expired → return a clear
  "reconnect gov.br" error to the frontend (don't call the RPA).
- Otherwise pass `session.getBearer()` and `session.getUserId()` into the client.

The office id is already available from the authenticated user (you use
`currentUser.requireOfficeId()` elsewhere).

## Security / LGPD

- `bearer` + `userId` are sensitive session credentials → **encrypt at rest**,
  **never log**, short-lived (the JWT `exp` is already tracked in `expiresAt`).
- The RPA masks the owner CPF on output (`ANONYMIZE_PII`) — unchanged.

## Acceptance criteria

1. Two offices connected at once, each queries its own vehicle → each gets its own
   correct data (no collision). This is the whole point.
2. A query for an office whose session is missing/expired → clean "reconnect"
   response, RPA not called with someone else's token.
3. `bearer`/`userId` are encrypted in the DB and never appear in logs.
4. Existing single-office flow still works.

## Notes

- Prod config reminder (unrelated but worth checking): `rpa.base-url` must be
  `http://desphub-rpa:8080` in production (the Docker service name), not
  `localhost:8090`; and `rpa.session-token-apikey` must equal the RPA's
  `SESSION_TOKEN_APIKEY` **if** you keep the single-tenant relay.
- The RPA-side contract is documented in the RPA repo `docs/INTEGRATION.md` (§2.1)
  and `docs/COURIER.md`.
