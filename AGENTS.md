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
- Latest published release: `thor-v0.5.0`.
- Release source commit: `601eb7ef5139a9922426417e27b1c5f60e061db3`.
- Release APK SHA-256: `24BE278EA3FBE2CC447FB2BDDEF9B21220CD5F97A020C87668796ECD25F1B200`.
- v0.5.0 contains the hardware-validated Battle Only setup, High Scores, both campaign selectors, Game Settings, and complete Map Editor workflow: pre-entry, map creation/loading, File Options, System Options, transactional Map Specifications, and all nine Editor Tools.
- Latest hardware-validated source commit: `8706addb5`.
- This checkpoint adds touch minimap viewport control for the Adventure Map and base Map Editor on top of every previously validated menu, gameplay-control, information-card, and Editor workflow. Its final debug APK SHA-256 is `5259ED2DD2E2EE109ACC4CFC9AAC773BEF4CA9633A13638565685888583BC31C`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- Touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Editor validation covers entry and map creation/loading, exact parent restoration, File Options and its confirmations/transitions, all ten System Options actions, transactional Map Specifications including player, victory/loss, alliance, child-editor, Okay/Back, and history behavior, all nine native tool modes, terrain and brush selection, object selectors and placement, Detail Edit/Move/Copy, erase filters, Magnify, live Undo/Redo availability, map metadata, tile and object descriptions, live information refresh, touch-minimap radar fidelity and clamped viewport navigation, persisted configuration, language availability, rapid taps and drags, multitouch rejection, help, and physical, touchscreen, mouse, hotkey, and established-menu regressions.
- The latest hardware-validated source implementation is `8706addb5`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused hardware checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `5259ED2DD2E2EE109ACC4CFC9AAC773BEF4CA9633A13638565685888583BC31C`.

## Next recommended planning point

- The next focused feature planning point is hero and castle quick-selection lists.
- Inspect the existing native focus selection, kingdom-owned hero and castle collections, information snapshots, and dual-screen input bridge, then plan a focused selection slice without duplicating engine-owned focus, availability, ordering, or rendering logic.
- Propose the focused slice and manual acceptance tests, then wait for user approval before implementation.
- Retain the validated touch minimap, Editor pre-entry, File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, both campaign selectors, semantic gameplay controls, information cards, modal restoration, and all physical controls without regression.

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
- The last device endpoint was `192.168.68.81:46343`, but wireless ADB ports can change. Run `adb devices` before install or launch.
- Launch the explicit game activity after installation; do not use `monkey`, which can open the asset Toolset instead.
- The lower Android display was display ID 4. Its last SurfaceFlinger physical ID was `4630946482288158084`; re-check after panel toggles or reboot.

## Git and upstream safety

- `origin`: `https://github.com/CapnChaosDK/fheroes2_thor.git` (fetch and push).
- `upstream`: `https://github.com/ihhub/fheroes2.git` (fetch only); its push URL is deliberately disabled.
- Never push to upstream. Keep Thor-specific changes focused and guarded by `TARGET_AYN_THOR` where practical.
- Preserve published validated history; do not rewrite the branch without explicit user approval.
- Follow `docs/AYN_THOR_UPSTREAM_LOG.md` and the backlog for future upstream integrations.
