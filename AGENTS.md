# AYN Thor fork handoff

## Project and user workflow

- This workspace is the AYN Thor dual-screen fork of fheroes2.
- Before implementing any new code slice, present the proposed behavior and focused acceptance tests, then wait for the user's approval.
- Keep deferred ideas and later slices in `docs/AYN_THOR_BACKLOG.md`; do not discard them when implementing an earlier slice.
- Build and lint an approved candidate, install and explicitly launch it on the Thor, then give the user a short set of focused hardware tests and wait for their results. The user prefers to perform device interaction manually.
- Keep automatic device testing to brief connection, installation, launch, log, and state checks. Do not run extended ADB-driven navigation, repeated screenshots, or exhaustive automated hardware interaction unless the user explicitly requests it or it is needed to diagnose a reported failure; these runs consume too many tokens.
- Record the user's passed validation before committing and pushing unless the user explicitly requests a different workflow.
- Preserve unrelated or pre-existing working-tree changes. Never reset or overwrite them.

## Current validated checkpoint

- Branch: `ayn-thor-dual-screen`.
- Latest published release: `thor-v0.4.0`.
- Release source commit: `503ef2318ca254329748ce443ee69271bcc2bf41`.
- Release APK SHA-256: `2520C1C55BC954DFA2E1F91FF37A7BE2BE75305FEEE6B1BA22D5E0FE8930D01F`.
- v0.4.0 contains the hardware-validated Scenario Setup player-editing workflow in addition to the navigable Load Game and Scenario Setup controls from v0.3.0.
- Latest hardware-validated development commit: `db739b8c6`.
- This checkpoint adds the Price of Loyalty selector with all four expansion campaigns and Back while preserving hover animations, missing-video behavior, audio/palette cleanup, and existing input paths. Its final debug APK SHA-256 is `6750BD33D7568AE384B6665BF0B553E277675D634176090E9A2BEC2B5D6E1539`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The hardware-validated Price of Loyalty selector is preserved in commit `db739b8c6`.
- Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated. Price of Loyalty validation covers all four matching first scenarios, hover animation switching/looping/static restoration, Back and Cancel cleanup, rapid taps, and physical, touchscreen, mouse, hotkey, and Original-selector regressions.
- The latest committed validated implementation is `db739b8c6`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `6750BD33D7568AE384B6665BF0B553E277675D634176090E9A2BEC2B5D6E1539`.

## Next recommended planning point

- The next feature planning point from the backlog is the top-level Game Settings dialog in `fheroes2::openGameSettings()` in `src/fheroes2/dialog/dialog_game_settings.cpp`; Editor menus follow later.
- Plan lower-screen access to Language, Graphics, Audio, Hot Keys, cursor type, interface type, text support, and Okay/Back. Account for nested-dialog context restoration, settings that update the current dialog in place, persisted configuration, language availability, and safe enabled states.
- Propose the focused slice and manual acceptance tests, then wait for user approval before implementation.
- Retain the validated New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, both campaign selectors, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
- The last device endpoint was `192.168.68.81:39585`, but wireless ADB ports can change. Run `adb devices` before install or launch.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
