# AYN Thor dual-screen build

This branch adds an Android companion command deck for the AYN Thor's lower display. The regular fheroes2 SDL window remains on the display where the game was launched, while the other active display shows touch controls for common game actions.

The APK is arm64-only, matching both the Snapdragon 865 and Snapdragon 8 Gen 2 Thor models.

The implementation uses Android's public multi-display APIs. It does not hardcode the Thor's model name or a display ID, so IDs changing after a reboot or after toggling a panel does not break it. On a normal single-display Android device, the game behaves exactly like the upstream build.

## Lower-screen controls

| Button | fheroes2 keyboard action |
| --- | --- |
| Up, Down, Left, Right | Menu navigation or adventure-map scrolling |
| Confirm | Enter / open focus |
| Cancel | Escape / close dialog |
| Next Hero | `H` |
| Next Town | `T` |
| Move | `M` |
| Spell | `C` |
| Action | Space |
| End Turn | `E` |

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
