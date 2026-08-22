# AYN Thor development backlog

This file is the maintained backlog for Thor-specific work. Update the status and acceptance criteria here when a milestone changes; do not discard deferred items when implementing an earlier milestone.

Status values: `planned`, `in progress`, `blocked`, `done`, `deferred`.

## Agreed product decisions

- The lower display will become context-sensitive.
- Milestone 1 is controls-only.
- The visual design will be inspired by the Heroes II interface: stone or parchment surfaces, gold bevels, and game-appropriate typography. The APK must not bundle proprietary game artwork; any future use of original sprites must come from the player's installed assets.
- The 1240 x 1080 layout will reserve a clearly separated information region for later milestones. In Milestone 1 this region may show only the current context title and decorative framing.
- Confirm, Cancel, and Game Menu will not remain permanently visible. Each context will show only the controls useful in that context.
- Physical controller support and the upper-screen game remain independent of the lower-screen UI.

## Milestone 1: context-sensitive command deck

Status: `done`

Goal: replace the fixed 12-button deck with Heroes II-styled layouts selected by the active game context.

### Planned contexts

1. Main menu and its submenus.
2. General dialogs and list selection.
3. Adventure map.
4. Hero screen.
5. Castle screen.
6. Battle, included as the primary stress test for context switching.
7. Safe fallback layout for unclassified screens.

### Technical plan

1. Define stable Thor context and action identifiers that do not depend on Java labels.
2. Add a small, thread-safe C++ to Android state bridge for the active context and enabled-action mask.
3. Track temporary overlays with a context stack so closing a dialog restores the underlying layout.
4. Deliver context updates to the Android main thread without moving focus away from SDL.
5. Refactor the lower-screen view into data-driven layouts rather than hardcoded button coordinates.
6. Build the Heroes II-inspired visual primitives without shipping proprietary assets.
7. Continue using the proven key-event path for actions that already have hotkeys.
8. Show disabled actions as visibly unavailable and suppress actions that are unsafe in the current context.
9. Retain a fallback navigation deck if native state is temporarily unavailable.

### Reserved layout

- Reserve roughly the upper quarter of the lower display for a future information panel.
- Use the remaining space for large contextual controls.
- Do not populate the reserved panel with live hero, castle, kingdom, or battle data in Milestone 1.

### Acceptance criteria

- The layout changes correctly between every supported context.
- Dialogs override and then restore the previous context reliably.
- No lower-screen touch steals focus from the upper SDL window.
- No unavailable action is emitted.
- Button press and release states cannot remain stuck after a context change, panel toggle, sleep, or app pause.
- The fallback layout remains usable when a context is unknown.
- Both panels recover after display disable/enable and app resume.
- The existing Vita-style physical controller mapping remains unchanged.
- Android build and lint pass, followed by testing on the connected Thor.

### Current validation state

- Android build and lint pass.
- Installed successfully on a connected AYN Thor.
- Main Menu to Dialog transition verified from a lower-screen action.
- Dialog to Main Menu restoration verified.
- Battle context and its eight-action layout verified in a live Battle Only match.
- Adventure Map, Hero, and Castle contexts verified by the user on the Thor.
- Battle Spell, Wait/Defend, Turn Order, and Options actions verified by the user.
- Nested Battle confirmation correctly switches to Dialog and restores Battle after cancellation.
- Automatic battle resolution was traced to the standard `auto resolve battles` setting and resolved by selecting Manual mode.
- Dynamic measured-text fitting was verified from a live lower-display capture; long labels retain inset from the inner button frame across the shared renderer.

## Milestone 2: semantic actions

Status: `in progress`

- Replace fragile simulated hotkeys where appropriate with a Java to native semantic-action queue.
- Process actions on the game/SDL thread; never mutate engine state directly from the Android UI thread.
- Add availability rules for context-specific operations such as battle wait/defend, recruitment, and army management.
- Preserve configurable keyboard hotkeys independently of lower-screen semantic controls.

### Battle-first implementation

- Stable Battle action identifiers are shared by Java and C++.
- A bounded, thread-safe native queue accepts one-shot commands from the Android main thread and is consumed only on the SDL/game thread.
- Context changes clear queued commands, and current native availability masks reject stale or unavailable commands.
- Battle buttons are visually muted and ignore touch when unavailable. Spell, Retreat, and Surrender availability is derived from current engine state; all Battle actions are disabled during AI turns and spell-target selection.
- The prior SDL key-event path remains the fallback if the native bridge is not loaded. Adventure Map, Hero, Castle, menu, dialog, and fallback layouts continue using it during incremental migration.
- Android build and lint pass. The candidate APK was installed successfully and opened the command deck on display 4.

### Battle-first device validation

Status: `done`

1. In a manual Battle Only match, verify Spell, Wait / Defend, Options, and Turn Order still act once per tap.
2. Open and cancel the Auto and Quick Combat confirmations; verify Dialog context restores to Battle.
3. Verify Retreat and Surrender act normally when available and appear muted when the engine disallows them.
4. During an AI turn and while choosing a spell target, verify all command-deck Battle buttons are muted and ignore touches.
5. Verify physical controls and configurable keyboard hotkeys remain unchanged.

All five checks passed on the connected AYN Thor.

### Adventure Map implementation

- All twelve Adventure Map controls now use stable semantic action identifiers and execute through the native game-thread queue.
- Enabled states mirror current engine state: Next Hero requires a movable hero, Next Town requires a castle, Spell requires a focused hero able to cast, and Move requires a plotted path.
- Action is available for a revisitable object or focused castle, while Dig requires hero focus. Menu and summary actions remain generally available.
- During automatic hero movement every other command is muted; tapping Move again requests a stop.
- Android build and lint pass. The candidate APK was installed successfully and opened the command deck on display 4.

### Adventure Map device validation

Status: `in progress`

1. Verify Next Hero and Next Town select the next available owned object and mute when none exists.
2. Verify Spell, Adventure, File, Puzzle, Kingdom, and View World each open once per tap and restore the Adventure Map layout after closing.
3. Plot a hero path and tap Move; while the hero travels, verify every other button is muted and tapping Move stops movement.
4. Verify Action works on a revisitable object or focused castle and is muted when no default action is possible.
5. Verify Dig is available with hero focus, End Turn behaves normally, and physical controls remain unchanged.

Initial device result: checks 1, 3, 4, and 5 passed. Check 2 exposed stacked menu openings because several full-screen Adventure dialogs did not publish the temporary Dialog context. The modal entry points now publish Dialog directly, clearing queued Adventure actions and restoring the map context on exit; the corrected APK is installed for a focused check 2 retest.

### Windows build note

- Always compile the Android native target through a short temporary drive mapping such as `R:`. Building from the full repository path can exceed the Windows NDK path limit and fail while creating a deeply nested `*.cflags.tmp` file.
- The repeatable `subst` procedure and cleanup command are documented in `README_ayn_thor.md`.

## Milestone 3: information panel

Status: `deferred`

- Selected hero portrait, movement, mana, primary stats, army, and artifacts summary.
- Selected castle summary and available creature growth.
- Kingdom resources, player color, and current date.
- Battle turn order and selected-unit details.
- Explicit privacy/fog-of-war rules so the lower screen never exposes hidden information.

## Milestone 4: interactive second-screen tools

Status: `deferred`

- Touch radar or minimap with upper-screen viewport control.
- Hero and castle quick-selection lists.
- Optional drag-and-drop army management after semantic actions are proven safe.
- Configurable layouts, button sizing, left/right-handed modes, and controls-only battery-saving mode.
- Optional haptics and configurable long-press actions.

## Milestone 5: native visual integration

Status: `deferred`

- Investigate rendering engine-owned widgets or player-installed Heroes II sprites on the lower display.
- Prefer a native rendering/state bridge over duplicating gameplay logic in Java.
- Measure memory, battery, frame pacing, and lifecycle behavior before enabling continuous second-screen rendering.

## Validation backlog

Status: `planned`

- Context transition matrix covering main menu, nested dialogs, adventure map, hero, castle, battle, and editor paths.
- Touch cancellation, multi-touch rejection, rapid context changes, screen sleep, rotation lock, and panel hot-plug tests.
- Verify Snapdragon 865 and Snapdragon 8 Gen 2 Thor variants where hardware is available.
- Document ADB diagnostics and capture context/action transitions under the `fheroes2-thor` log tag.
