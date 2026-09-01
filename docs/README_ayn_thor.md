# AYN Thor dual-screen build

This branch adds an Android companion command deck for the AYN Thor's lower display. The regular fheroes2 SDL window remains on the display where the game was launched, while the other active display shows touch controls for common game actions.

The APK is arm64-only, matching both the Snapdragon 865 and Snapdragon 8 Gen 2 Thor models.

The fork uses the package ID `org.fheroes2.thor`, allowing it to be installed alongside the official `org.fheroes2` app without a signing-key conflict. Android keeps their imported assets, settings, and saves separate.

The game uses a 960 x 540 logical canvas scaled exactly 2x to the Thor top panel's 1920 x 1080 native resolution. This fills the 16:9 panel while keeping interface elements readable on the six-inch display, and overrides stale 4:3 resolution values from earlier builds.

The original 4:3 main-menu artwork and its buttons are scaled to the full 16:9 canvas in the Thor build. This removes the decorative side borders without changing the aspect ratio of the adventure map or other gameplay screens.

The implementation uses Android's public multi-display APIs. It does not hardcode the Thor's model name or a display ID, so IDs changing after a reboot or after toggling a panel does not break it. On a normal single-display Android device, the game behaves exactly like the upstream build.

## Lower-screen controls

The Heroes II-styled command deck follows the active game context. Its upper information panel shows game-visible summaries for the Adventure Map, Hero, Castle/Town, Battle, Scenario Setup, and Battle Only Setup contexts.

| Context | Available actions |
| --- | --- |
| Main menu | New Game, Load Game, Settings, High Scores, Credits, Editor, Quit |
| New Game menus | Standard, Campaign, Multiplayer, Battle Only, Settings, campaign type, Hot Seat player count, Back |
| Load Game | Standard, Campaign, Hot Seat, Back; unavailable categories are muted |
| Scenario setup | Select Map, player navigation and assignment, faction, handicap, five difficulty levels, Start, Back |
| Battle Only setup | Select Attacker, Select Defender, previous/next terrain, Defender Control, Reset, Start, Exit |
| High Scores | Campaign and Exit from Standard; Standard and Exit from Campaign; Campaign follows installed-asset availability |
| Succession Wars campaign | Non-interactive intro state, then Roland, Archibald, Back |
| Price of Loyalty campaigns | Price of Loyalty, Voyage Home, Wizard's Isle, Descendants, Back |
| Game Settings | Language, Graphics, Audio, Hot Keys, Cursor Type, Interface Type, Text Support, Okay / Back |
| Map Editor menus | New Map, Load Map, Main Menu; From Scratch or Random; four map sizes and Back |
| Map Editor | File Options, System Options |
| Editor File Options | New Map, Load Map, Start Map, Save Map, Main Menu, Quit, Auto Playtest, Cancel |
| Editor System Options | Language, Graphics, Audio, Hot Keys, Animation, Passability, Interface Type, Cursor Type, Scroll Speed, Okay / Back |
| Dialog or fallback | Confirm, Cancel |
| Adventure map | Next Hero, Next Town, Heroes, Towns, Move, Action, Spell, End Turn, Adventure, File, Puzzle, Kingdom, View World, Dig |
| Adventure hero/town lists | Native kingdom order, compact status, current-focus highlight, paging, direct selection, Back |
| Hero | Previous, Next, Dismiss, Split Half, Split One, Join, Close |
| Castle | Previous, Next, Well, Market, Mage Guild, Shipyard, Thieves Guild, Tavern, Build, merge to Hero/Garrison, Upgrade, Exit |
| Battle | Spell, Wait/Defend, Auto, Quick Combat, Retreat, Surrender, Options, Turn Order |

Context is published by the native game engine and polled by the Android Presentation. Temporary message dialogs override the current layout and restore the underlying context when they close.

Scenario Setup player editing follows the native game rules. Standard games retain exactly one human position. Hot Seat uses a two-step Select/Swap flow so the chosen number of human players cannot change. Faction and handicap actions are muted when the selected map or player does not allow them.

Battle Only Setup shows both sides, defender control, terrain, occupied army slots, and readiness. Hero selection uses the existing upper-screen selector, Start is muted when either active army is invalid, and Start transitions directly to the Battle deck.

High Scores opens the Standard table first. The lower deck switches both displays between Standard and Campaign when the complete Succession Wars campaign assets are installed, restores the active table after a help dialog, and exits directly to Main Menu.

Selecting the original Succession Wars campaign shows a non-interactive lower-screen state while the intro runs, then exposes Roland, Archibald, and Back beside the animated upper selector. Back returns to New Game without residual selector audio, and stale intro or selector taps are cleared during context changes.

Selecting the Price of Loyalty expansion exposes all four campaigns and Back on the lower display while preserving the upper selector's hover animations. Missing-video behavior remains compatible with the engine, and selection or cancellation clears queued actions, audio, and palette state before the next screen.

Game Settings shows the current language, cursor, interface, and text-support states. Direct toggles refresh both displays immediately, unavailable language selection is muted, and the existing Language, Graphics, Audio, and Hot Keys dialogs restore the Settings deck when they close.

The Adventure information panel includes an engine-owned minimap for clamped upper-screen viewport navigation. Heroes and Towns open revisioned lower-screen lists in native kingdom order; selection is revalidated and applied on the SDL thread through the existing focus paths, then returns directly to Adventure.

The Editor entry hierarchy mirrors New Map, Load Map, From Scratch, Random, map-size selection, and exact Back/Main Menu behavior. Creating or loading a map enters a dedicated Map Editor deck. File Options delegates saving, loading, playtesting, confirmations, and transitions to the existing editor logic.

Editor System Options shows the current language, animation, passability, cursor, interface, and scroll-speed states. Direct changes retain the editor's existing redraw, rebuild, and persistence paths; child dialogs temporarily use the Dialog deck and restore System Options once when they close.

If battles open directly in automatic or quick resolution, open **Settings**, select **Battles**, and cycle the option until it reads **Manual**. This is the standard fheroes2 `auto resolve battles` setting and is independent of the Thor command deck.

## Physical controls

The built-in controls use the same mapping as the PlayStation Vita port:

| Thor control | Action |
| --- | --- |
| Left analog stick | Move pointer |
| Right analog stick | Scroll map |
| A (bottom face button) | Left mouse button |
| B (right face button) | Right mouse button |
| X (left face button) | End turn |
| Y (top face button) | Open spellbook |
| D-pad left | Next hero |
| D-pad right | Next castle |
| D-pad down | Revisit current object |
| R1 | Accelerate pointer while held |
| Select / Back | System menu |
| Start | Enter |

The mapping follows SDL's A/B/X/Y controller convention. If the Thor system settings offer Xbox and Nintendo button modes, use Xbox mode so the physical positions match the table.

Hotkeys keep their normal context-dependent behavior. For example, `C` opens the campaign choice from the appropriate main menu and casts a spell on the adventure map.

## Build on Windows

1. Install Android Studio, including Android SDK 35 and the NDK requested by the project.
2. Run `script\\android\\install_packages.bat` from the repository root. This downloads the pinned SDL2 and native dependencies.
3. Map the repository to a short temporary drive before compiling. The NDK generates deeply nested object paths and can otherwise fail at a `*.cflags.tmp` file with `No such file or directory` because of the Windows path-length limit:

   ```powershell
   # Run from the repository root.
   cmd /d /c subst R: "%CD%"
   Push-Location R:\android
   .\gradlew.bat --no-daemon :app:assembleDebug :app:lintDebug
   Pop-Location
   cmd /d /c subst R: /d
   ```

   Configure `ANDROID_HOME`, `JAVA_HOME`, or `local.properties` as usual before running Gradle. If `R:` is already occupied, use another unused drive letter consistently.
4. Install `android\\app\\build\\outputs\\apk\\debug\\app-debug.apk` on the Thor with `adb install -r <apk>`.
5. Enable both Thor panels before launching fheroes2. Launch the game on the top panel; the command deck will open on the other active display.

The original Heroes of Might and Magic II assets are not included. Use the in-app toolset to import assets from a legally owned copy, as described in [the Android user guide](README_android.md).

## Diagnostics

Display setup messages use the `fheroes2-thor` log tag:

```text
adb logcat -s fheroes2-thor fheroes2
```

If the lower panel stays blank, verify that Android exposes it to applications:

```text
adb shell dumpsys display
```

The lower display should be active and should appear either in the presentation display category or in the active display list. The controller also responds to displays being enabled, disabled, or re-created while the game is running.

## Development validation workflow

Build and lint checks are automated. After installing and explicitly launching a candidate, hardware behavior is normally validated manually by the user from a short focused checklist. Extended ADB-driven navigation and repeated screenshot capture are reserved for requested automation or diagnosis of a specific failure.

## Current scope

The latest hardware-validated source checkpoint is `a5ae1e58f`. It provides the clustered, zoomable, presentation-filtered, fog-safe Expanded Adventure Map with privacy-aware marker information on top of semantic controls, read-only information cards, navigable game menus, both campaign selectors, the complete Editor workflow, touch-minimap viewport control, and hero and castle quick-selection lists. Its APK SHA-256 is `64F3707B04B2439A2AC9E873811BDD311B7B89514428EEA688E65EFFB1331242`. The latest published prerelease remains [`thor-v0.7.0`](https://github.com/CapnChaosDK/fheroes2_thor/releases/tag/thor-v0.7.0), sourced from `4e396a814`.

The next focused feature planning point is collision-avoiding labels for individually resolved owned markers at 2x and 4x. The initial slice should use only names already authorized in the owned selection snapshot; clusters, 1x markers, and non-owned markers remain unlabeled. Broader authorized non-owned labels, drag-and-drop army management, configurable layouts, and haptics remain separate later milestones.

The maintained implementation plan and deferred feature list are in [AYN_THOR_BACKLOG.md](AYN_THOR_BACKLOG.md).
