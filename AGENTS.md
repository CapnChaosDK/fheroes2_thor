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
- Latest published release: `thor-v0.10.0`.
- Release source commit: `9a7c595f276e9db4210e8c8589a9885020ccac15`.
- Release APK SHA-256: `E3DC75FF2B8A512E05BA36BBBCCE75B993E14A30540D2ECBFBD4D2DFA35441F6`.
- v0.10.0 integrates official upstream through `d778cb44b`, precise Hero Meeting stack splitting, whole-artifact transfers and swapping, semantic necromancy control, and complete in-game Adventure Options. Graphics, Audio, Interface, Language, Hot Keys, and Resolution expose dedicated lower-screen actions and live values with exact nested restoration. It adds Thor default-branch Android CI and strengthened identifier and action-mask collision checks while retaining every validated v0.9.0 workflow.
- Latest hardware-validated source commit: `d9079c1d1039fbf4fb76c23f1fad7b998ebdcb34`.
- Individual Hero Meeting artifact transfers and swaps are hardware-validated on 2026-09-07. Android build, app/isotools lint, native request regression tests, identifier parity, explicit Thor installation/launch, and all focused hardware checks passed. Development APK SHA-256: `35A526DF4CE4D6327BFC6A689200AE2061DB919E4481FD92F9F9D1F621E66DCA`. This development checkpoint is newer than the published v0.10.0 release.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- Precise Hero Meeting stack splitting, direct troop manipulation, tap-based troop-slot transfers, whole-army controls, player-installed creature sprites and Hero portrait, semantic anti-stuck gameplay dialogs, complete in-game Adventure and File Options, safe unknown-menu fallback, haptic, owned/allied/enemy/neutral-labeled, clustered, zoomable, presentation-filtered, fog-safe Expanded Adventure Map, marker information card, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers system-respecting accepted-action haptics and silence for excluded inputs; owned, allied, enemy, and neutral individual labels at 2x and 4x with native authorization and live withdrawal or reclassification; focused-owned / owned / allied / enemy / neutral bounded truncation, edge and collision priority, and input transparency; deterministic filter-first clustering and counts at 1x and 2x; the individual 4x transition; same-tile offsets; relationship and kind badge presentation; focused-cluster highlighting; lower-only drill-down; cluster information suppression; bounded zoom and anchoring; transformed navigation and individual marker targeting; gesture cancellation; restoration; privacy-aware information; fog/ownership/alliance invalidation; owned native focus; safe non-owned navigation; exact Heroes/Towns and Back restoration; compact minimap; View World; dialogs; physical controls; upper touchscreen; mouse; and hotkeys.
- The latest hardware-validated source implementation is `d9079c1d1039fbf4fb76c23f1fad7b998ebdcb34`.
- Show Artifacts / Show Army, individual empty-slot moves and occupied-slot swaps, distinct scroll spells, spellbook protection, assembled sets, stats/scouting, cancellation, nested restoration, lifecycle, and all troop/whole-artifact/input regressions passed on the Thor. The validated APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `35A526DF4CE4D6327BFC6A689200AE2061DB919E4481FD92F9F9D1F621E66DCA`.

## Next recommended planning point

- Individual Hero Meeting artifact manipulation is complete and hardware-validated. Recommend player-installed artifact artwork as the next focused proposal, reusing the existing bounded visual bridge to improve slot recognition. No next slice is approved yet. Artifact drag-and-drop, same-bag rearrangement, multi-slot army redistribution, broader player-installed visuals, configurable haptics, and configurable layouts remain deferred alternatives.
- Retain the validated precise stack splitting, Hero Meeting direct manipulation, tap-based troop-slot and whole-army transfers, player-installed visuals, quick-selection lists, touch minimap, Editor pre-entry, File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, both campaign selectors, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
      .\gradlew.bat --no-daemon :app:assembleDebug :app:lintDebug :isotools:lint
  }
  finally {
      Set-Location C:\
      subst.exe R: /d
  }
  ```

- APK: `android/app/build/outputs/apk/debug/app-debug.apk`.
- Package/activity: `org.fheroes2.thor/org.fheroes2.GameActivity`. Process name for `pidof` and logs: `org.fheroes2.thor:GameActivityProcess`.
- The last device endpoint was `192.168.68.62:33045`, advertised as `adb-42d0284-qC5yYd._adb-tls-connect._tcp`, but wireless ADB ports can change. Run `adb devices` and `adb mdns services` before install or launch. If a daemon started inside the restricted sandbox returns Windows socket error 10013, restart the local ADB server with approved network access before reconnecting.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
