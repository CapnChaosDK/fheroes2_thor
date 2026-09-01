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
- Latest hardware-validated source commit: `60cf8fe2ddd40bf616250d740a01250257cb8a8f`.
- This development checkpoint adds context-aware Android `CLOCK_TICK` feedback for accepted lower-touch owned-marker focus, cluster drill-down, completed zoom-level transitions, and filter changes. Navigation, panning, bounds, rejected and cancelled gestures, long-press information, and non-lower-touch controls remain silent; Android's touch-feedback setting is respected and cluster drill-down cannot double-trigger through zoom. All six focused haptic checks passed with labels, clustering, filters, privacy, information, navigation, restoration, and established controls unchanged. Its final debug APK SHA-256 is `C42BCD784DC45F7626ECC9EBD49D636D249DFBE77D401156794244205C9322FF`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The haptic, labeled, clustered, zoomable, presentation-filtered, fog-safe Expanded Adventure Map, marker information card, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers system-respecting accepted-action haptics and silence for excluded inputs; owned-only individual labels at 2x and 4x; bounded truncation, edge and collision avoidance, focused-label priority, and input transparency; deterministic filter-first clustering and counts at 1x and 2x; the individual 4x transition; same-tile offsets; relationship and kind badge presentation; focused-cluster highlighting; lower-only drill-down; cluster information suppression; bounded zoom and anchoring; transformed navigation and individual marker targeting; gesture cancellation; restoration; privacy-aware information; fog/ownership/alliance invalidation; owned native focus; safe non-owned navigation; exact Heroes/Towns and Back restoration; compact minimap; View World; dialogs; physical controls; upper touchscreen; mouse; and hotkeys.
- The latest hardware-validated source implementation is `60cf8fe2d`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused hardware checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `C42BCD784DC45F7626ECC9EBD49D636D249DFBE77D401156794244205C9322FF`.

## Next recommended planning point

- The next recommended planning slice is privacy-safe allied-marker labels on the Expanded Adventure Map. Limit the first slice to individually resolved allied heroes and settlements at 2x and 4x; clusters, 1x markers, enemy markers, and neutral markers remain unlabeled.
- Native code must remain the sole authorization source and publish an allied label only while the object is visible and currently allied. Fog return, removal, ownership or alliance changes, turn/player/context changes, or snapshot replacement must withdraw it without Android retaining hidden text.
- Reuse the validated screen-space label sizing, truncation, edge/collision avoidance, and input transparency. Preserve focused-owned priority, then place remaining owned labels before allied labels in stable snapshot order. Allied labels must not alter marker hit testing, information tiers, focus, navigation, clustering, filters, or haptics.
- Before implementation, present the exact allied-label behavior and a focused manual checklist covering 1x/cluster suppression, allied-only authorization and live withdrawal, deterministic owned-first collision handling, zoom/filter transitions, input transparency, and established control regressions; then wait for user approval.
- Broader enemy or neutral labels, sprites, army-management interactions, configurable haptic choices, and configurable layouts remain deferred separately.
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
- The last device endpoint was `192.168.68.58:35751`, advertised as `adb-42d0284-qC5yYd._adb-tls-connect._tcp`, but wireless ADB ports can change. Run `adb devices` and `adb mdns services` before install or launch. If a daemon started inside the restricted sandbox returns Windows socket error 10013, restart the local ADB server with approved network access before reconnecting.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
