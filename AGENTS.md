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
- Latest hardware-validated development commit: `f35c7ffc4b6e77b9bbec402d46fba15698f1fb34`.
- The development checkpoint adds the Succession Wars Roland/Archibald campaign selector on top of the validated Battle Only setup and High Scores workflow. Its final debug APK SHA-256 is `587803916FEB3DD1951BE7D5EA91925E30C014875E4AF3692BE00161079395AA`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The working tree is clean at this handoff. There are no known uncommitted source changes to preserve.
- Scenario Setup player editing, Battle Only setup, High Scores, and the Succession Wars selector are complete and hardware-validated. The selector covers non-interactive intro synchronization, Roland/Archibald selection, Back without residual audio, intro skipping, rapid taps, and physical, touchscreen, mouse, and keyboard regressions.
- Validated implementation commit: `f35c7ffc4b6e77b9bbec402d46fba15698f1fb34`.
- The validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned.

## Next recommended planning point

- Plan the Price of Loyalty four-campaign selector in `Game::NewPriceOfLoyaltyCampaign()` in `src/fheroes2/game/game_newgame.cpp`.
- The Thor contains all 24 required `CAMP1_01.HXC` through `CAMP4_04.HXC` maps and all four `IVYPOL.SMK`, `IVYVOY.SMK`, `IVYWIZ.SMK`, and `IVYDES.SMK` selector videos. Still verify the engine's `X_LOADCM` and `X_IVY` resource gate and the enabled Expansion entry before implementation.
- Account for its four hover-triggered animations, per-video missing behavior, static background restoration, audio/palette cleanup, and the current `NEW_GAME` fallback/exit behavior.
- Propose lower-screen behavior, modal/animation handling, Back or exit semantics, enabled-state rules, and focused manual acceptance tests, then wait for user approval before implementation.
- Retain the validated New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, Succession Wars selector, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
