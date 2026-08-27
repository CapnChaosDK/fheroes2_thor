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
- Latest hardware-validated development commit: `188f36fc3ff1fa086b68e9b311245ab667b0c910`.
- The development checkpoint adds the High Scores Standard/Campaign workflow on top of the validated Battle Only setup. Its final debug APK SHA-256 is `3EF381E02C09A8E566BD83900E18F1461D2EACAB6E8D34EA3D5F7B61643A887A`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The working tree is clean at this handoff. There are no known uncommitted source changes to preserve.
- Scenario Setup player editing, Battle Only setup, and High Scores are complete and hardware-validated. High Scores covers synchronized Standard/Campaign switching, installed-asset availability, direct Exit, help-dialog restoration, rapid taps, and physical, touchscreen, mouse, and keyboard regressions.
- Validated implementation commit: `188f36fc3ff1fa086b68e9b311245ab667b0c910`.
- The validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned.

## Next recommended planning point

- Plan the Campaign selection workflow. Compatible Succession Wars campaign assets are now installed and the High Scores test confirmed that the engine detects them.
- Inspect `Game::NewSuccessionWarsCampaign()` and `Game::NewPriceOfLoyaltyCampaign()` in `src/fheroes2/game/game_newgame.cpp`: the upper screen uses animated/video-backed Roland/Archibald and four-campaign expansion selectors, with missing-video fallbacks and distinct asset requirements.
- Determine which expansion assets are installed before including the expansion selector in the first slice. Prefer the smallest fully hardware-testable slice; do not assume Price of Loyalty assets from the confirmed Succession Wars assets.
- Propose lower-screen behavior, modal/animation handling, Back or exit semantics, enabled-state rules, and focused manual acceptance tests, then wait for user approval before implementation.
- Retain the validated New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
