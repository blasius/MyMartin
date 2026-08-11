# Android Repair Requests — Build Guide

A guide for an AI agent implementing the **repair request** feature and its **full lifecycle** on the Android client, backed by the Martin Logistics Laravel API.

---

## 1. What this feature is

Drivers report vehicle faults at the yard; a workshop team (dispatchers, approvers, mechanics) works the request through inspection, approval, assignment, repair, and release. The Android app has **two audiences**:

| Role | What they do in the app |
|---|---|
| **Driver** | Create a repair request at the yard (with photos + parts), submit it, track status, get notified when the vehicle is released |
| **Mechanic** | See assigned tasks, view task detail, start work, complete work |

> **Server readiness (verified against `routes/api.php`):**
>
> - ✅ **Mechanic (workshop) mobile API exists today**: `/api/mobile/workshop/my-tasks`, `/tasks/{assignment}`, `/tasks/{assignment}/start`, `/tasks/{assignment}/complete` (§4).
> - ❌ **Driver-side mobile endpoints do NOT exist yet**: there is no `mobile/repair-requests*` route anywhere in `routes/api.php` (checked `api.php` and `web.php`). The full lifecycle currently exists only under `/api/portal/workshop/repair-requests`, which uses **session auth** and is not usable by the Android app. The driver endpoints in §6 **must be built server-side first** — this is a blocking prerequisite for the driver flow (list / create / submit / cancel / vehicles), though not for the mechanic flow.

---

## 2. Lifecycle / state machine

A `RepairRequest` moves through these statuses:

```
draft ──submit──> pending_approval ──approve(1st, logistics)──> pending_ops_approval ──approve(2nd, ops)──> approved
   │                   │                     └──────reject──────> draft                        │
   │                   │                                                                      │
   │                   └───────── submit without parts ──────────> approved ─────────────────┘
   │                                                                  │
   └──cancel──> cancelled                                         assignMechanic
                                                                      │
                                                                      v
                                                                 in_progress ──completeWork──> completed ──release──> released
```

Key rules (enforced by `RepairRequestService` in `app/Services/RepairRequestService.php`):

1. **Create → `draft`.** Reference auto-generated: `RR-YYYYMMDD-NNNN`.
2. **Submit → `pending_approval`** if any line item has a `part_id`, else straight to **`approved`**.
3. **Approval is two-level.** First level = Logistics Manager (or Admin) → `pending_ops_approval`. Second level = Operations Manager (or Admin) → `approved`. An `Approval` row is written each time (stage 1 or 2).
4. **Request approval requires a mechanic to be assigned first** (422 otherwise), and only works from `pending_approval`.
5. **Assign mechanic → `in_progress`** only when the request is `approved`; otherwise the assignment is just created and the request stays in its current state.
6. **Complete work:** an assignment goes `assigned → in_progress → completed`. When *all* assignments are completed, the request becomes `completed`.
7. **Release:** all assignments must be completed. Creates a `RepairRelease`, sets the vehicle status to `released_from_workshop`, updates `last_odometer`, sets request to `released`, notifies the driver.
8. **Reject** (either approval stage) sends the request back to `draft`.

Priority values: `low, medium, high, critical`. Types are free strings, e.g. `mechanical, electrical, body, tire, brake`.

---

## 3. Authentication

All mobile endpoints require a **Sanctum token** as a `Bearer` token.

```http
POST /api/mobile/auth/login
Content-Type: application/json

{
  "email": "driver@example.com",
  "password": "password"
}
```

Response (200):

```json
{
  "message": "Login successful.",
  "token": "1|abc123...",
  "user": { "id": 1, "name": "John Driver", "email": "john@example.com", "roles": ["driver"] }
}
```

Also available:
- `POST /api/mobile/auth/request-whatsapp-otp` — body `{ "identifier": "<whatsapp-number>" }`
- `POST /api/mobile/auth/verify-whatsapp-otp` — body `{ "identifier": "...", "code": "123456" }`
- `POST /api/mobile/auth/verify-firebase-phone` — body `{ "idToken": "...", "identifier": "<phone-number>" }`
- `POST /api/mobile/auth/logout` (authenticated)

Store the token in encrypted local storage; attach it to every request. On `401`, clear the session and route to login.

> **Note:** `/api/mobile/auth/login` is the login for the **mobile** client. Do **not** use `/api/login` from `MOBILE_SUPPORT_API.md` (older contract) — it is superseded by `mobile/auth/login` on this repo.

---

## 4. Existing mobile endpoints you can use today

All under `Authorization: Bearer <token>`.

### 4.1 Mechanic — my tasks

```
GET /api/mobile/workshop/my-tasks
```

Returns a JSON array (no envelope):

```json
[
  {
    "id": 5,
    "repair_request_id": 12,
    "reference": "RR-20260619-0001",
    "vehicle_plate": "CJH 241 GP",
    "instructions": "Inspect front brake pads.",
    "status": "assigned",
    "duration": null,
    "assigned_at": "2026-06-19T09:00:00.000000Z",
    "started_at": null,
    "completed_at": null,
    "completed_note": null
  }
]
```

`status` ∈ `assigned | in_progress | completed`. `duration` is a human string like `"1h 25m"` (null until started).

### 4.2 Mechanic — task detail

```
GET /api/mobile/workshop/tasks/{assignment}
```

Returns 403 unless the assignment belongs to the authenticated mechanic.

```json
{
  "assignment": {
    "id": 5,
    "instructions": "Inspect front brake pads.",
    "status": "in_progress",
    "duration": "25m",
    "assigned_at": "...", "started_at": "...", "completed_at": null, "completed_note": null
  },
  "repair_request": {
    "reference": "RR-20260619-0001",
    "type": "brake",
    "priority": "high",
    "description": "Squealing front brakes.",
    "status": "in_progress",
    "vehicle": { "id": 3, "plate_number": "CJH 241 GP", "status": "in_workshop" },
    "driver": { "id": 9, "name": "John Driver", "phone": "+27820000000" },
    "items": [
      { "description": "Front brake pads", "part": { "name": "Brake Pad Set", "sku": "BRK-PAD-001" }, "estimated_quantity": 1.0, "estimated_unit_price": 450.0 }
    ]
  }
}
```

### 4.3 Mechanic — start work

```
POST /api/mobile/workshop/tasks/{assignment}/start
```

422 if not currently `assigned`. Response: `{ "message": "Work started.", "assignment": { "id", "status", "started_at" } }`.

### 4.4 Mechanic — complete work

```
POST /api/mobile/workshop/tasks/{assignment}/complete
Content-Type: application/json

{ "completed_note": "Replaced pads and rotor." }
```

422 if not `in_progress`. Response: `{ "message": "Work completed.", "assignment": { "id", "status", "completed_at" } }`.

### 4.5 FCM token registration

```
POST /api/mobile/fcm-token
Content-Type: application/json

{ "token": "<firebase-messaging-token>" }
```

Call this after login and refresh the token on every Firebase `onNewToken` callback. Required for push notifications (§7).

---

## 5. Backend contract you must reference (portal routes)

The full lifecycle endpoints already exist under the **portal** prefix (`/api/portal/workshop/repair-requests`, session auth). The Android agent should model the driver-side API on these. The important one is `store`, which **already supports mobile-style submission** (lat/lng + geofence) — it is reused unchanged by a mobile wrapper.

| Method | Route | Notes |
|---|---|---|
| GET | `repair-requests` | List, filters: `status`, `vehicle_id`, `priority`, `search`, `per_page` |
| POST | `repair-requests` | Create (see body below) |
| GET | `repair-requests/{id}` | Full detail incl. items, approvals, assignments, release |
| POST | `repair-requests/{id}/submit` | draft → pending_approval / approved |
| POST | `repair-requests/{id}/request-approval` | requires assigned mechanic |
| POST | `repair-requests/{id}/approve` | body `{ "comment"?: string }`; role decides stage |
| POST | `repair-requests/{id}/reject` | body `{ "comment"?: string }` |
| POST | `repair-requests/{id}/assign-mechanic` | body `{ "mechanic_id", "instructions"? }` |
| POST | `repair-requests/{id}/reassign-mechanic` | same body; forces request to `in_progress` |
| POST | `repair-requests/start-work/{assignmentId}` | |
| POST | `repair-requests/complete-work/{assignmentId}` | body `{ "completed_note"? }` |
| POST | `repair-requests/{id}/use-parts` | body `{ "warehouse_id", "part_id", "quantity" }` — decrements stock, writes StockMovement |
| POST | `repair-requests/{id}/update-item` | body `{ "id", "actual_quantity"?, "actual_unit_price"? }` |
| POST | `repair-requests/{id}/release` | body `{ "checklist_completed": bool, "unresolved_issues"?, "odometer_at_release"? }` |
| POST | `repair-requests/{id}/cancel` | |

**Create body** (`POST repair-requests`):

```json
{
  "vehicle_id": 3,
  "driver_id": 9,
  "type": "brake",
  "priority": "high",
  "description": "Squealing front brakes.",
  "latitude": -26.2041,
  "longitude": 28.0473,
  "items": [
    { "description": "Front brake pads", "part_id": 41, "estimated_quantity": 1, "estimated_unit_price": 450 }
  ],
  "photo_urls": ["https://.../brakes.jpg"]
}
```

Validation & behavior:
- `vehicle_id`, `type`, `priority`, `description` required. `priority` must be one of `low|medium|high|critical`.
- If **both** `latitude` and `longitude` are present, the server runs a **geofence check** against configured yards (`Place` type `yard`, default radius 50 m) and returns **422 "Vehicle is not inside the yard..."** if outside. This is how mobile submissions are distinguished from portal ones — mobile **must** send lat/lng.
- `driver_id` active-guard: a driver cannot have more than one request not in `released`/`cancelled` (422 otherwise).
- `items[].estimated_total` is computed server-side.
- `photo_urls` are plain URL strings. There is **no upload endpoint yet** — either upload to signed storage in the app and pass URLs, or add an upload endpoint (see §10).
- On success the response is the created request (with `vehicle` and `items.part`), status `draft`.

---

## 6. Gaps to add server-side (BLOCKING prerequisite for the driver flow)

These endpoints do **not** exist on the server as of this document. The agent should **not assume they are live** — either the backend team adds them first (recommended), or the Android work proceeds only for the mechanic side and the driver screens are built against the contract below and wired to real endpoints once deployed. Add them under `Route::prefix('mobile')->middleware('auth:sanctum')` in `routes/api.php`, using `RepairRequestService`. A new `Api\Mobile\MobileRepairRequestController` should:

- `POST /api/mobile/repair-requests` — **delegate to the same `RepairRequestController::store` logic** (share a service method, or call the existing controller) so geofence + active-guard + reference generation stay identical. Auto-set `driver_id` to the authenticated user when it is a driver. Only drivers/mechanics assigned to the vehicle may create.
- `GET /api/mobile/repair-requests` — list requests where `driver_id = auth user` (or `vehicle` assigned to them), newest first, with `vehicle`, `items`, status.
- `GET /api/mobile/repair-requests/{id}` — detail for the owner only (403 otherwise), with `items.part`, `approvals`, `assignments.mechanic`, `release`.
- `POST /api/mobile/repair-requests/{id}/submit` — driver submits a draft.
- `POST /api/mobile/repair-requests/{id}/cancel` — driver cancels a draft / pending request.
- `GET /api/mobile/repair-requests/vehicles` — the driver's assignable vehicles (`id`, `plate_number`, `status`, `current_driver_id`), modeled on the portal `vehicles()`/`searchVehicles()` methods.

After adding them, update this guide's §4 with the exact JSON shapes. Keep the response envelope consistent with the rest of the mobile API (plain array for lists, object for single resources).

---

## 7. Push notifications (FCM)

Notifications are sent through `FcmChannel`. Payloads arrive in the Android `data` map — **all values are strings**, so parse ints carefully.

| `type` | Sent to | `data` keys | Navigate to |
|---|---|---|---|
| `mechanic_assigned` | mechanic | `type`, `repair_request_id`, `assignment_id`, `reference` | Task detail screen |
| `repair_released` | driver | `type`, `repair_request_id`, `reference` | Repair detail screen |
| `repair_created` | driver (portal-created only) | `type`, `repair_request_id`, `reference` | Repair detail screen |

Register a `FirebaseMessagingService`; in `onMessageReceived` route on `data.type` and launch the corresponding screen with the `repair_request_id` / `assignment_id`.

---

## 8. Android architecture (recommended)

There is no Android code in this repo yet, so pick a standard stack and keep it consistent:

- **Language:** Kotlin
- **Networking:** Retrofit + OkHttp + kotlinx.serialization (or Moshi)
- **Async:** Coroutines / Flow
- **DI:** Hilt
- **Storage:** Room for a local cache (repair list, task list); DataStore (or EncryptedSharedPreferences) for the token
- **Images:** capture photos with the camera intent, then upload via the app's storage layer and store the returned URL (there is no server upload endpoint yet — see §10)

Suggested package layout:

```
com.martin.logistics/
├── data/
│   ├── remote/        # Retrofit services, DTOs
│   ├── local/         # Room entities, DAOs
│   └── repository/    # RepairRequestRepository, WorkshopRepository, AuthRepository
├── domain/            # models (RepairRequest, RepairItem, RepairAssignment), state machines
├── ui/
│   ├── auth/
│   ├── repairs/       # driver: list, create, detail
│   ├── workshop/      # mechanic: tasks, task detail
│   └── notifications/ # deep-link handling
└── di/
```

---

## 9. Screens to build

### Driver

1. **Repair Request List** (`GET .../repair-requests`) — cards keyed by reference, plate, type badge, priority color, status chip. Filter tabs: Active / Completed / All. Empty state when none.
2. **Create Repair Request** — select vehicle (from `repair-requests/vehicles`), pick type + priority, description, capture/add photos, add line items (description + optional part + est. qty + est. unit price). **Geofence gating:** request a fresh location; the server enforces it, so surface the 422 "not inside the yard" message clearly. On success navigate to detail. Cancel resets to the list.
3. **Repair Request Detail** — header (reference, status, priority, vehicle), photos, description, line items (estimated vs actual), approval timeline (`approvals`), assignments (mechanic + status), release block (`release`), and driver actions **Submit** (only when `draft`) and **Cancel** (draft / pending). Show "Released" summary (odometer, unresolved issues, checklist).
4. **Status timeline** — derive step progress from `status`: Submitted → Approved → In workshop → Completed → Released.

### Mechanic

1. **My Tasks** (`GET .../workshop/my-tasks`) — sorted `assigned → in_progress → completed`. Show plate, reference, instructions, duration.
2. **Task Detail** (`GET .../workshop/tasks/{assignment}`) — vehicle + driver info, request description, parts/items, assignment status. Buttons: **Start Work** (only `assigned`), **Complete Work** (only `in_progress`, with optional `completed_note`).

### Shared

- **Push notification deep links** (§7) landing on repair/task detail.
- **401 handling** — global interceptor → clear token → login screen.
- **Error toasts** mapping §11 codes to friendly messages.

---

## 10. Server-side additions you may still need

1. **Driver mobile endpoints** (§6) — required.
2. **Photo upload endpoint** — currently `photo_urls` is just strings. Either the app uploads to storage (S3/local) directly and submits URLs, or add `POST /api/mobile/repair-requests/photos` (multipart) returning a URL. Recommend the upload endpoint for consistency.
3. **Parts lookup for mobile** — the create form benefits from a searchable parts list (`GET /api/mobile/parts?q=...`). Portal has parts under the workshop module; expose a small Sanctum-protected endpoint.

---

## 11. Error handling reference

| Status | Body | Meaning / action |
|---|---|---|
| 401 | `{ "message": "Unauthenticated." }` | Invalid/expired token → re-login |
| 403 | `{ "message": "This task is not assigned to you." }` / "Forbidden" | Not the owner → hide/disable action |
| 422 | `{ "message": "Vehicle is not inside the yard..." }` | Geofence failed → prompt driver to move into yard |
| 422 | `{ "message": "Driver already has an active repair request..." }` | Active-guard → show existing request |
| 422 | `{ "message": "A mechanic must be assigned before requesting approval." }` | Approval ordering rule |
| 422 | `{ "message": "Task is not in progress." }` / "not in assignable status" | Stale state → refetch list |
| 404 | `{ "message": "No query results for model..." }` | Deleted/cancelled → refresh list |

Always refetch the resource after any state-changing call rather than guessing the next status.

---

## 12. Suggested build order (checklist)

1. **Backend (blocking):** add the mobile driver repair-request controller + routes (§6). Test with curl: create with valid + out-of-yard coords. **Do not start the Android driver screens until these return 200/201.**
2. **Android auth:** login + token storage + 401 interceptor.
3. **Driver list + detail** read-only; verify against seeded data.
4. **Create flow** incl. photos (URL or upload endpoint) and line items; verify geofence error handling.
5. **Submit / Cancel** actions; verify status transitions.
6. **Mechanic tasks** list + detail + start + complete.
7. **FCM:** register token, handle the three `data.type` payloads with deep links.
8. **Offline polish:** cache last-known list in Room; queue create submissions if you want offline-first (note: geofence + active-guard are server-enforced, so a create that "succeeds offline" can still be rejected — surface the server response verbatim).

## 13. Verification flow (manual / curl)

1. `POST /api/mobile/auth/login` → token.
2. `POST /api/mobile/repair-requests` with valid yard coordinates → 201/200 `draft` (repeat with coordinates far from any yard → expect 422 geofence error).
3. `POST .../submit` → `pending_approval` (with parts) or `approved` (no parts).
4. `POST .../{id}/cancel` → `cancelled`.
5. As a mechanic: `GET /api/mobile/workshop/my-tasks` → task appears after `assign-mechanic`; `start` → `in_progress`; `complete` → `completed`.
6. After all assignments complete and `release` is called in portal, confirm the driver receives a `repair_released` push and the detail shows `status: released` with the release summary.
