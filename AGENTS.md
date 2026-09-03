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
- Latest published release: `thor-v0.9.0`.
- Release source commit: `271d2a0fa3c9f55d69b20b5caf01169536478b72`.
- Release APK SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.
- v0.9.0 adds hardware-validated Hero Meeting direct manipulation, tap and whole-army transfers, player-installed Hero and creature visuals, complete in-game Adventure and File Options, and semantic anti-stuck gameplay dialogs. Save/load, treasure chest, Level Up, Arena, standard prompts, and battle-result layouts expose exact lower-screen actions and restore their native parent without stale input. It retains the complete v0.8.0 Expanded Adventure Map, quick-selection, touch-minimap, menu, gameplay-control, information-card, campaign, and Map Editor workflows.
- Latest hardware-validated source commit: `271d2a0fa3c9f55d69b20b5caf01169536478b72`.
- This checkpoint passed Android build and lint, explicit Thor installation and launch, and focused hardware validation of the full dialog and options flow. The final debug APK SHA-256 is `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The semantic anti-stuck gameplay dialogs, complete in-game Adventure and File Options, Hero Meeting direct troop manipulation, tap-based troop-slot transfers, whole-army deck, player-installed creature sprites and Hero portrait, safe unknown-menu fallback, haptic, owned/allied/enemy/neutral-labeled, clustered, zoomable, presentation-filtered, fog-safe Expanded Adventure Map, marker information card, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers system-respecting accepted-action haptics and silence for excluded inputs; owned, allied, enemy, and neutral individual labels at 2x and 4x with native authorization and live withdrawal or reclassification; focused-owned / owned / allied / enemy / neutral bounded truncation, edge and collision priority, and input transparency; deterministic filter-first clustering and counts at 1x and 2x; the individual 4x transition; same-tile offsets; relationship and kind badge presentation; focused-cluster highlighting; lower-only drill-down; cluster information suppression; bounded zoom and anchoring; transformed navigation and individual marker targeting; gesture cancellation; restoration; privacy-aware information; fog/ownership/alliance invalidation; owned native focus; safe non-owned navigation; exact Heroes/Towns and Back restoration; compact minimap; View World; dialogs; physical controls; upper touchscreen; mouse; and hotkeys.
- The latest hardware-validated source implementation is `271d2a0fa`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused dialog, options, layout, restoration, interaction, and regression checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.

## Next recommended planning point

- Select and propose the next focused slice before implementation. Remaining candidates include partial-stack splitting or richer lower-screen army management, artifact transfers, broader player-installed sprite use, configurable haptic choices, and configurable layouts; each remains deferred until its exact behavior and focused tests are approved.
- Retain the validated Hero Meeting direct manipulation, tap-based troop-slot and whole-army transfers, player-installed visuals, quick-selection lists, touch minimap, Editor pre-entry, File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, both campaign selectors, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
