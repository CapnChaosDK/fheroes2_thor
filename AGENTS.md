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
- Latest published release: `thor-v0.6.0`.
- Release source commit: `92621eaeb1942c2d40432b09079bfdd1d9c31dfb`.
- Release APK SHA-256: `FF4FB232AF0BEE4968E3D334A52FA45D08C77E649182C3FA1CE8553DEFDB4AA5`.
- v0.6.0 contains the hardware-validated hero and castle quick-selection lists, touch minimap viewport control, live Editor map information, and every previously validated menu, gameplay-control, information-card, campaign, and complete Map Editor workflow.
- Latest hardware-validated source commit: `5967dcb76241b66d3fca3e0e63b528ad4c7f3e98`.
- This development checkpoint extends the Expanded Adventure Map with engine-filtered owned, allied, enemy, and neutral hero and settlement markers. Native code owns world-object discovery, current-player relations, fog visibility, positions, marker categories, and selectability; Android renders only the revisioned visible snapshot. Owned markers remain selectable, while visible non-owned markers are informational and their taps retain safe viewport navigation without focus, route, dialog, or movement side effects. Its final debug APK SHA-256 is `368BC26928C542AE43C622ED4D4982F22680E9D719CE750E20A7E81A707FE0F1`.
- The maintained implementation history, validation results, next work, and deferred features are in `docs/AYN_THOR_BACKLOG.md`.

## Worktree handoff

- The fog-aware Expanded Adventure Map, hero and castle quick-selection lists, touch minimap viewport control, Editor pre-entry, in-map File Options, System Options, Map Specifications, Editor Tools, live map information, Game Settings, Scenario Setup player editing, Battle Only setup, High Scores, and both campaign selectors are complete and hardware-validated.
- Expanded-map validation now covers owned/allied/enemy/neutral relationship colors, hero/town shapes, fog-driven appearance and removal without stale state, owned native focus, informational non-owned taps without gameplay side effects, overlap handling, drag/rapid-input/multitouch safety, Heroes/Towns restoration, Back behavior, and compact-minimap, View World, physical-control, and upper-screen regressions. Earlier validation covers native list ordering, status, paging, and every previously validated Editor, menu, gameplay, dialog, touchscreen, mouse, and hotkey workflow.
- The latest hardware-validated source implementation is `5967dcb76`.
- Build and lint passed, the candidate installed and launched explicitly on the Thor, all focused hardware checks passed, and the validated debug APK remains at `android/app/build/outputs/apk/debug/app-debug.apk` when build outputs have not been cleaned. SHA-256: `368BC26928C542AE43C622ED4D4982F22680E9D719CE750E20A7E81A707FE0F1`.

## Next recommended planning point

- The next focused feature planning point is a fog-safe, read-only information card for markers on the Expanded Adventure Map.
- Inspect the existing engine quick-info visibility rules, especially enemy hero identity and army-detail restrictions, then propose the exact fields and touch gesture. Native code must decide every published field and immediately withdraw information when its marker is no longer visible; Android must not infer or retain hidden details.
- Preserve owned-marker focus selection and safe non-owned viewport navigation. Labels, clustering, zoom, sprites, configurable filters, and army-management interactions remain deferred unless explicitly approved as separate slices.
- Propose the focused behavior and manual acceptance tests, then wait for user approval before implementation.
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
