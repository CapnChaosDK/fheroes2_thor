# AYN Thor fork handoff

## Project and user workflow

- This workspace is the AYN Thor dual-screen fork of fheroes2.
- Before implementing any new code slice, present the proposed behavior and focused acceptance tests, then wait for the user's approval.
- Keep deferred ideas and later slices in `docs/AYN_THOR_BACKLOG.md`; do not discard them when implementing an earlier slice.
- Build and lint an approved candidate, install and explicitly launch it on the Thor, then give the user a short set of focused hardware tests and wait for their results. The user prefers to perform device interaction manually.
- Keep automatic device testing to brief connection, installation, launch, log, and state checks. Do not run extended ADB-driven navigation, repeated screenshots, or exhaustive automated hardware interaction unless the user explicitly requests it or it is needed to diagnose a reported failure; these runs consume too many tokens.
- Record the user's passed validation before committing and pushing unless the user explicitly requests a different workflow.
- Continue the structured round workflow the user explicitly endorsed: propose behavior and focused tests, implement with concise progress updates, build/lint, install and launch, hand over a short manual checklist, then record validation and commit.
- Preserve unrelated or pre-existing working-tree changes. Never reset or overwrite them.

## Current validated checkpoint

- Branch: `ayn-thor-dual-screen`.
- Latest published release: `thor-v0.7.0`.
- Release source commit: `4e396a814874312438f45d85246a746e30df95fb`.
- Release APK SHA-256: `BABA7499AAD2BAB8805131FD59C0040B4385AC9434F92DCBB2B913280DBCD663`.
- v0.7.0 contains the complete hardware-validated Expanded Adventure Map with fog-safe relationship markers, privacy-aware information cards, kind and relationship filters, and bounded presentation zoom, plus every previously validated quick-selection, touch-minimap, menu, gameplay-control, information-card, campaign, and complete Map Editor workflow.
- Latest hardware-validated source commit: `4e396a814874312438f45d85246a746e30df95fb`.
- This development checkpoint adds bounded 1x, 2x, and 4x presentation-only zoom to the Expanded Adventure Map. Android applies one shared transform to radar rendering, viewport outlines, visible filtered markers, hit testing, long-press inspection, and viewport requests while native fog-safe snapshots and gameplay state remain unchanged. Anchored pinch, clamped two-finger panning, safe cancellation, in-memory overview and list restoration, and map-size reset passed on hardware with all prior filter, privacy, information, focus, navigation, and control behavior unchanged. Its final debug APK SHA-256 is `BABA7499AAD2BAB8805131FD59C0040B4385AC9434F92DCBB2B913280DBCD663`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The zoomable, presentation-filtered, fog-safe Expanded Adventure Map, marker information card, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers bounded zoom levels and badges, anchored pinch and clamped two-finger panning, transformed viewport navigation and marker targeting, gesture cancellation safety, zoom and list restoration, map-size reset, kind and relationship filter cycling and restoration, hidden-marker input exclusion, information-card clearing, deterministic filtered overlaps, full and limited hero detail, settlement information tiers, fog/ownership/alliance invalidation without stale details, relationship colors and shapes, owned native focus, informational non-owned taps, long-press/tap/drag separation, exact Heroes/Towns and Back restoration, compact minimap, View World, dialogs, physical controls, upper touchscreen, mouse, and hotkeys.
- The latest hardware-validated source implementation is `4e396a814`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused hardware checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `BABA7499AAD2BAB8805131FD59C0040B4385AC9434F92DCBB2B913280DBCD663`.

## Next recommended planning point

- The next focused feature planning point is zoom-aware, presentation-only marker clustering for the Expanded Adventure Map, aimed at reducing density at 1x and 2x without exposing new native data or changing gameplay state.
- Plan deterministic cluster membership and thresholds, same-tile handling, count and relationship/kind presentation, the 4x transition, and local cluster-tap drill-down. Filters must apply before clustering, rendering and hit testing must share the final geometry, and long-press information must remain limited to individually resolved markers.
- Preserve the validated zoom transform and restoration, filters, information-card authorization, owned-marker focus, safe non-owned viewport navigation, privacy and invalidation rules, deterministic overlaps, list restoration, Back, and all existing controls. Labels, sprites, army-management interactions, haptics, and configurable layouts remain deferred separately.
- Propose the focused clustering behavior and manual acceptance tests, then wait for user approval before implementation.
- Retain the validated quick-selection lists, touch minimap, Editor pre-entry, File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, both campaign selectors, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

## Android build and device workflow

- Always build through a short temporary drive mapping. The full Windows path can exceed the Android NDK path limit while creating `*.cflags.tmp` files.
- Known local tools:
  - Android SDK: `C:\Users\steen\AppData\Local\Android\Sdk`
  - JDK 17: `C:\Users\steen\AppData\Local\Temp\fheroes2-jdk17-full\jdk-17.0.20.1+1`
  - ADB: `C:\Users\steen\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- Repeatable build from the repository root:

  ```powershell
  $thorRoot = 'C:\Users\steen\Documents\ChatGPT\FHeroes'
  subst.exe R: $thorRoot
  try {
      Set-Location R:\android
      $env:JAVA_HOME = 'C:\Users\steen\AppData\Local\Temp\fheroes2-jdk17-full\jdk-17.0.20.1+1'
      $env:ANDROID_HOME = 'C:\Users\steen\AppData\Local\Android\Sdk'
      .\gradlew.bat --no-daemon :app:assembleDebug :app:lintDebug
  }
  finally {
      Set-Location C:\
      subst.exe R: /d
  }
  ```

- APK: `android/app/build/outputs/apk/debug/app-debug.apk`.
- Package/activity: `org.fheroes2.thor/org.fheroes2.GameActivity`.
- The last device endpoint was `192.168.68.58:45947`, but wireless ADB ports can change. Run `adb devices` before install or launch. If a daemon started inside the restricted sandbox returns Windows socket error 10013, restart the local ADB server with approved network access before reconnecting.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
