# AYN Thor fork handoff

## Project and user workflow

- This workspace is the AYN Thor dual-screen fork of fheroes2.
- Before implementing any new code slice, present the proposed behavior and focused acceptance tests, then wait for the user's approval.
- Keep deferred ideas and later slices in `docs/AYN_THOR_BACKLOG.md`; do not discard them when implementing an earlier slice.
- Build and install an approved candidate, give the user focused hardware tests, and wait for their results. Record passed validation before committing and pushing unless the user explicitly requests a different workflow.
- Preserve unrelated or pre-existing working-tree changes. Never reset or overwrite them.

## Current validated checkpoint

- Branch: `ayn-thor-dual-screen`.
- Latest published release: `thor-v0.3.0`.
- Release source commit: `e8c56affbb9581bebee357ddce6da16450afd744`.
- Release APK SHA-256: `A41913CD3CB50BCDD30BBB135E7204F3FCD028A49B7C054A5A8F0439D2FCBB07`.
- v0.3.0 contains hardware-validated navigable Load Game and Scenario Setup workflows. The main README includes the AYN Thor physical-control scheme.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- At this handoff, three source files contain pre-existing, uncommitted Player Setup scaffold changes. Preserve them:
  - `android/app/src/main/java/org/fheroes2/ThorSecondScreenPresentation.java`
  - `src/fheroes2/game/thor_ui.cpp`
  - `src/fheroes2/game/thor_ui.h`
- The scaffold adds semantic identifiers and lower-screen buttons for previous/next player, control type, previous/next faction, and handicap.
- It is incomplete and unvalidated: native Scenario Setup event processing, availability rules, upper-screen changes, information refresh, build/install, and device tests have not been completed.
- Inspect the diff before planning further work. Do not commit the scaffold merely because it exists.

## Next recommended planning point

- Finish planning the Scenario Setup Player Editing slice: player selection, Human/AI control, faction selection, and handicap.
- Confirm how these lower-screen actions map to the existing upper-screen `PlayersInfo` behavior, especially which choices are mutable for Standard versus Hot Seat games.
- Retain Start, Back, Select Map, difficulty controls, modal restoration, and all physical controls without regression.

## Android build and device workflow

- Always build through a short temporary drive mapping. The full Windows path can exceed the Android NDK path limit while creating `*.cflags.tmp` files.
- Known local tools:
  - Android SDK: `C:\Users\steen\AppData\Local\Android\Sdk`
  - JDK 17: `C:\Users\steen\AppData\Local\Temp\fheroes2-jdk17\jdk-17.0.20.1+1`
  - ADB: `C:\Users\steen\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- Repeatable build from the repository root:

  ```powershell
  $thorRoot = 'C:\Users\steen\Documents\ChatGPT\FHeroes'
  subst.exe R: $thorRoot
  try {
      Set-Location R:\android
      $env:JAVA_HOME = 'C:\Users\steen\AppData\Local\Temp\fheroes2-jdk17\jdk-17.0.20.1+1'
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
- The last device endpoint was `192.168.68.84:40669`, but wireless ADB ports can change. Run `adb devices` before install or launch.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
