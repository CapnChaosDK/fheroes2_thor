# AYN Thor dual-screen build

This branch adds an Android companion command deck for the AYN Thor's lower display. The regular fheroes2 SDL window remains on the display where the game was launched, while the other active display shows touch controls for common game actions.

The APK is arm64-only, matching both the Snapdragon 865 and Snapdragon 8 Gen 2 Thor models.

The fork uses the package ID `org.fheroes2.thor`, allowing it to be installed alongside the official `org.fheroes2` app without a signing-key conflict. Android keeps their imported assets, settings, and saves separate.

The game uses a 960 x 540 logical canvas scaled exactly 2x to the Thor top panel's 1920 x 1080 native resolution. This fills the 16:9 panel while keeping interface elements readable on the six-inch display, and overrides stale 4:3 resolution values from earlier builds.

The original 4:3 main-menu artwork and its buttons are scaled to the full 16:9 canvas in the Thor build. This removes the decorative side borders without changing the aspect ratio of the adventure map or other gameplay screens.

The implementation uses Android's public multi-display APIs. It does not hardcode the Thor's model name or a display ID, so IDs changing after a reboot or after toggling a panel does not break it. On a normal single-display Android device, the game behaves exactly like the upstream build.

## Lower-screen controls

The Heroes II-styled command deck follows the active game context. Its upper section is reserved for a future information panel; the current milestone provides controls only.

| Context | Available actions |
| --- | --- |
| Main menu | New Game, Load Game, Settings, High Scores, Credits, Quit |
| Dialog or fallback | Directional navigation, Confirm, Cancel |
| Adventure map | Next Hero, Next Town, Move, Action, Spell, End Turn, Adventure, File, Puzzle, Kingdom, View World, Dig |
| Hero | Previous, Next, Dismiss, Upgrade, Split Half, Split One, Join, Swap, Close |
| Castle | Previous, Next, Well, Market, Mage Guild, Shipyard, Thieves Guild, Tavern, Build, merge to Hero/Garrison, Exit |
| Battle | Spell, Wait/Defend, Auto, Quick Combat, Retreat, Surrender, Options, Turn Order |

Context is published by the native game engine and polled by the Android Presentation. Temporary message dialogs override the current layout and restore the underlying context when they close.

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
3. Open the `android` directory in Android Studio and build the `app` module, or run `android\\gradlew.bat assembleDebug` after configuring `ANDROID_HOME`/`local.properties`.
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

## Current scope

This first version provides a useful independent touch surface while leaving upstream rendering unchanged. Moving live engine widgets such as the radar, hero list, or kingdom status onto the lower panel will require a native engine-to-Android state/rendering bridge and is a separate follow-up milestone.

The maintained implementation plan and deferred feature list are in [AYN_THOR_BACKLOG.md](AYN_THOR_BACKLOG.md).
