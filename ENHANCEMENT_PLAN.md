# BrainOps Weather – Enhancement Plan

This document outlines how to evolve the Breezy Weather codebase (Android/Kotlin) into a BrainOps-aware, multi-device weather command experience while keeping the repo private and fit for rapid iteration.

## Objectives
- Deliver a responsive, production-grade weather app for phone/tablet/PC form factors (mobile-first; consider desktop via Android emulator or future web/desktop shell).
- Integrate BrainOps systems for auth, alerts, and tasking while retaining offline/forecast strength.
- Keep upstream weather capabilities (multi-provider, widgets, notifications) and add BrainOps operational context (jobs/crews/sites/alerts).

## Integration Surfaces
1) **Auth & Identity**
   - Use BrainOps auth tokens (OIDC/JWT) as primary; allow local fallback for offline.
   - Store tenant/org and user role; ensure headers `X-API-Key` and `x-tenant-id` for BrainOps APIs.

2) **BrainOps Data/Alerts**
   - Pull operational entities (jobs, crews, sites, assets) from BrainOps backend.
   - Subscribe to Command Center alerts/notifications and map to weather impacts.
   - Expose quick actions: “Delay job”, “Notify crew”, “Create task”, “Send customer notice”.

3) **Weather Intelligence**
   - Keep multi-source weather ingestion; add an internal proxy endpoint to normalize providers.
   - Compute impact overlays: lightning/precip/wind/heat risk by site coordinates; show per-job risk badges.
   - Cache forecasts locally for offline continuity; respect user-selected provider priority.

4) **UI/UX (Card-first)**
   - Card grid/stacks for: current conditions, hourly, daily, radar/tiles, alerts, job/crew impacts.
   - Mobile: swipeable stacks; Tablet: 2–3 column adaptive grid; Desktop: wide dashboard layout.
   - Add “Ops” pane: list of affected jobs/crews with CTA chips (call, message, delay, create ticket).
   - Notifications: actionable notifications (snooze/ack) tied to BrainOps tasks.

5) **Offline & Perf**
   - Keep local DB/cache for forecasts and settings.
   - Guard network calls with retries/backoff; surface offline banners.
   - Lazy-load heavy layers (radar/tiles) and use placeholders/skeletons.

6) **Security & Privacy**
   - Do not log tokens; redact PII in logs.
   - Respect private repo; avoid upstream telemetry defaults.
   - Ensure HTTPS-only endpoints; pin to BrainOps domains where possible.

## Delivery Phases
**Phase 0 – Baseline sanity (short)**
- Build & run current app; confirm weather providers and notifications work.
- Add feature flags for BrainOps integrations (off by default).

**Phase 1 – Auth + Config**
- Add BrainOps auth client (OIDC/JWT) + secure storage.
- Add settings screen for BrainOps endpoints/api-key/tenant.
- Thread auth headers through the API client layer.

**Phase 2 – Cards & Layout**
- Introduce card primitives and responsive grid/stacks.
- Rebuild home screen with cards (current/hourly/daily/alerts).
- Add “Ops Impact” card pulling mock data; wire to feature flag.

**Phase 3 – BrainOps Data**
- Call BrainOps backend for jobs/crews/sites; map to cards.
- Add alert → task/notification actions hitting Command Center/BrainOps endpoints.

**Phase 4 – Weather Intelligence**
- Risk scoring per site (wind/precip/lightning/heat).
- Overlay risk on jobs/crews; add filter/sort by risk/severity.

**Phase 5 – Polish & QA**
- A11y pass (labels, contrast, touch targets).
- Perf pass (caching, batched calls).
- Regression and smoke tests.

## Environment & Secrets (to be set)
- `BRAINOPS_API_BASE` – BrainOps backend base URL
- `BRAINOPS_COMMAND_CENTER_URL` – Command Center/alerts/tasks
- `BRAINOPS_API_KEY` – API key (use X-API-Key)
- `BRAINOPS_TENANT_ID` – default tenant (or derive post-auth)
- Weather provider keys (as per Breezy Weather defaults; keep in local .env not committed)

## Technical Notes
- Keep repo private; do not fork upstream publicly.
- Avoid global fetch monkey-patching; centralize API clients.
- Make BrainOps integration feature-flagged to avoid blocking weather core while iterating.

## Done Definition (per phase)
- Build passes; no lint errors.
- Authenticated calls include tenant + API key.
- Cards render with real data (weather + BrainOps where enabled).
- Offline experience degrades gracefully.

## Open Questions
- Confirm BrainOps auth flow for mobile (PKCE vs direct token).
- Preferred map/radar tile source and license.
- Notification transport (FCM/APNS equivalents) and routing for BrainOps alerts.
