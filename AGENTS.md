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
- Latest published release: `thor-v0.8.0`.
- Release source commit: `457e4fe1066568820ed97da9bfd3c93bc1f341cb`.
- Release APK SHA-256: `49D1C4EFB5BE1F4C4674B4E9CF5D687E6292ABA126E807BB9A3431B6751EA3EA`.
- v0.8.0 contains the complete hardware-validated Expanded Adventure Map with deterministic clustering, native-authorized owned/allied/enemy/neutral labels, context-aware haptics, fog-safe relationship markers, privacy-aware information cards, filters, and bounded presentation zoom; it also adds the safe unknown-menu fallback and retains every previously validated quick-selection, touch-minimap, menu, gameplay-control, information-card, campaign, and complete Map Editor workflow.
- Latest hardware-validated source commit: `457e4fe1066568820ed97da9bfd3c93bc1f341cb`.
- This development checkpoint completes Expanded Adventure Map relationship labels with native-authorized neutral hero and town names at 2x and 4x. All six focused checks passed, including 1x and cluster suppression, fog withdrawal, recruitment and capture reclassification, focused-owned / owned / allied / enemy / neutral collision priority, dense and edge placement, filter and zoom transitions, input transparency, unchanged haptics and safe navigation, exact restoration, and established controls. Its final debug APK SHA-256 is `49D1C4EFB5BE1F4C4674B4E9CF5D687E6292ABA126E807BB9A3431B6751EA3EA`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The safe unknown-menu fallback, haptic, owned/allied/enemy/neutral-labeled, clustered, zoomable, presentation-filtered, fog-safe Expanded Adventure Map, marker information card, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers system-respecting accepted-action haptics and silence for excluded inputs; owned, allied, enemy, and neutral individual labels at 2x and 4x with native authorization and live withdrawal or reclassification; focused-owned / owned / allied / enemy / neutral bounded truncation, edge and collision priority, and input transparency; deterministic filter-first clustering and counts at 1x and 2x; the individual 4x transition; same-tile offsets; relationship and kind badge presentation; focused-cluster highlighting; lower-only drill-down; cluster information suppression; bounded zoom and anchoring; transformed navigation and individual marker targeting; gesture cancellation; restoration; privacy-aware information; fog/ownership/alliance invalidation; owned native focus; safe non-owned navigation; exact Heroes/Towns and Back restoration; compact minimap; View World; dialogs; physical controls; upper touchscreen; mouse; and hotkeys.
- The latest hardware-validated source implementation is `457e4fe10`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused hardware checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `49D1C4EFB5BE1F4C4674B4E9CF5D687E6292ABA126E807BB9A3431B6751EA3EA`.

## Next recommended planning point

- Select and propose the next focused slice before implementation. Remaining candidates include player-installed sprites, army-management interactions, configurable haptic choices, and configurable layouts; each remains deferred until its exact behavior and focused tests are approved.
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
