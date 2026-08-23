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

Status: `Castle/Town candidate installed; device validation pending`

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

Status: `done`

1. Verify Next Hero and Next Town select the next available owned object and mute when none exists.
2. Verify Spell, Adventure, File, Puzzle, Kingdom, and View World each open once per tap and restore the Adventure Map layout after closing.
3. Plot a hero path and tap Move; while the hero travels, verify every other button is muted and tapping Move stops movement.
4. Verify Action works on a revisitable object or focused castle and is muted when no default action is possible.
5. Verify Dig is available with hero focus, End Turn behaves normally, and physical controls remain unchanged.

Checks 1, 3, 4, and 5 passed on the initial device run. Check 2 exposed stacked menu openings because several full-screen Adventure dialogs did not publish the temporary Dialog context. The modal entry points now publish Dialog directly, clearing queued Adventure actions and restoring the map context on exit. The focused check 2 retest passed: each menu opens once and one exit restores the Adventure Map deck.

### Next semantic-action slice

Status: `completed`

- Hero navigation, dismiss, selected-troop operations, and close now use stable semantic actions consumed in the Hero dialog on the game thread.
- Previous and Next follow the native Hero dialog's availability. Dismiss follows the native restrictions and retains its confirmation.
- Split Half, Split One, and Join operate on the troop selected on the upper screen and are muted until their exact engine conditions are satisfied.
- Swap is intentionally muted in a single-hero view because no second army exists; army exchange belongs to the later meeting or Castle context.
- The key-event fallback remains available if the native bridge is not loaded.
- Android build and lint pass. The candidate APK was installed successfully and opened the command deck on display 4.

### Hero device validation

Status: `passed`

1. With at least two heroes, verify Previous and Next switch once per tap; verify Close returns to the Adventure Map deck.
2. Tap Dismiss, cancel its confirmation, and verify the Hero deck restores once without reopening.
3. Select a stack containing at least two creatures with a free army slot. Verify Split One and Split Half enable and perform the requested split.
4. Select either of two matching stacks and verify Join enables and merges them. Verify these army buttons mute again when no stack is selected.
5. Confirm Upgrade is absent from the general Hero deck, Swap remains muted, and physical controls are unchanged.

Checks 1 through 4 passed on the Thor. Upgrade was removed from the general Hero deck because creature upgrades are only available while visiting a town. Upgrade is now part of the Castle/Town semantic slice. Swap remains reserved for a later two-army exchange context.

### Castle/Town semantic controls

Status: `completed`

- Previous, Next, Well, Market, Mage Guild, Shipyard, Thieves, Tavern, Build, To Hero, To Garrison, Upgrade, and Exit now use stable semantic actions consumed by the Castle dialog on the game thread.
- Navigation follows the native town buttons. Building actions are enabled only when the corresponding building exists; Build works for either a town tent or castle construction screen.
- Army transfers require a visiting hero and a non-empty source army. Upgrade requires selecting an eligible garrison or visiting-hero stack, the matching upgraded dwelling, and sufficient funds.
- Opening a building or construction sub-screen temporarily switches the command deck to Dialog, preventing queued Castle actions from firing after that sub-screen closes.
- Android build and lint pass through the required short `R:` drive mapping. The candidate installed successfully on the paired Thor and opened the command deck on display 4.

### Castle/Town device validation

Status: `passed`

1. With at least two towns, verify Previous and Next switch once per tap; verify Exit returns to the Adventure Map deck.
2. Verify built-building buttons open the correct screen once and one close returns to the Castle deck. Verify unbuilt-building buttons are muted.
3. With a visiting hero and troops in both armies, test To Hero and To Garrison and verify the army bars redraw correctly.
4. Select an upgradeable stack in either the garrison or visiting hero army. Verify Upgrade enables only when the upgraded dwelling exists and funds are sufficient, then upgrades the selected stack.
5. Verify Build opens the construction screen in a castle and the town-upgrade screen from a tent. Confirm physical controls remain unchanged.

All five Castle/Town checks passed on the Thor.

### Windows build note

- Always compile the Android native target through a short temporary drive mapping such as `R:`. Building from the full repository path can exceed the Windows NDK path limit and fail while creating a deeply nested `*.cflags.tmp` file.
- The repeatable `subst` procedure and cleanup command are documented in `README_ayn_thor.md`.

## Milestone 3: information panel

Status: `Castle/Town information candidate installed; device validation pending`

### Snapshot bridge

- A versioned, revisioned information snapshot now carries read-only game-visible data from the game thread through JNI to the Android presentation.
- The existing 100 ms context poll requests a snapshot by revision. Native code returns no payload when values are unchanged, and Android redraws only for a new revision.
- Context transitions publish an empty snapshot before changing screens, preventing stale information from another context or hot-seat player from remaining visible.
- The first slice uses text and generated stone, parchment-brown, and gold UI primitives; it does not bundle proprietary Heroes II artwork.

### Adventure Map information card

- The reserved panel shows the focused hero or settlement name and type, current date, all seven kingdom resources, and hero movement and mana when a hero is focused.
- With no hero or settlement focused, it shows a neutral Kingdom/Adventure Map summary.
- Existing Adventure semantic controls, enabled states, physical controls, and fallback behavior are unchanged.
- Android build and lint pass through the required short `R:` mapping. The APK installed successfully and opened the command deck on display 4.

### Adventure information device validation

Status: `passed`

1. Focus two different heroes and verify name, movement, and mana update without flicker or stale values.
2. Move a hero or cast an Adventure spell and verify the corresponding value updates.
3. Focus a town or castle and verify its name and type replace the hero details.
4. Gain or spend a resource and verify the resource line and date remain readable inside the panel borders.
5. Open and close a nested dialog, then end the turn. Verify no stale information appears during transitions and all existing lower-screen and physical controls still work.

All five Adventure information checks passed on the Thor, including readability and context transitions.

### Hero information card

- The shared card now shows hero name, level and class, Attack, Defense, Power, Knowledge, movement, mana, morale, and luck while the Hero screen is active.
- A snapshot is published before the Hero dialog fade-in and refreshed when values change, preventing the previous hero's information from lingering when Previous or Next switches heroes.
- Existing Hero semantic controls and the validated Adventure card are unchanged. Portrait, army, and artifact rendering remain deferred because this text-only slice does not transfer proprietary artwork.
- Android build and lint pass through the required short `R:` mapping. The APK installed successfully and opened the command deck on display 4.

### Hero information device validation

Status: `passed`

1. Open a Hero and compare name, level/class, and all four primary skills with the upper screen.
2. Use Previous and Next repeatedly and verify every value switches once without showing stale information from the prior hero.
3. Compare movement and mana with the Adventure card, including a hero whose values are not full.
4. Verify positive, neutral, or negative morale and luck values match the upper Hero screen and all text remains inside the panel borders.
5. Close and reopen the Hero screen and verify the Adventure and Hero cards restore correctly while all lower-screen and physical controls remain unchanged.

All five Hero information checks passed on the Thor, including rapid hero switching, value accuracy, readability, and context restoration.

### Castle/Town information card

- The shared card now shows the settlement name, Town or Castle type, faction, and visiting hero while the Castle screen is active.
- Construction status distinguishes available construction, insufficient resources, construction already used today, missing requirements, and complete or otherwise blocked states.
- Dwelling levels I through VI show the current recruitable creature count and normal weekly growth in `current/+growth` form. Unbuilt dwellings show `--`; Well and upgraded Well growth bonuses are included.
- Existing Castle/Town semantic controls and the validated Adventure and Hero cards are unchanged.
- Android build and lint pass through the required short `R:` mapping. The APK installed successfully and opened the command deck on display 4.

### Castle/Town information device validation

Status: `candidate installed`

1. Open a Town and a Castle and compare the settlement name, type, and faction with the upper screen.
2. Move a visiting hero into and out of the settlement and verify the hero name appears and clears without stale information.
3. Compare dwelling levels I through VI with the upper screen: current recruitable counts, normal weekly growth, Well bonuses, and `--` for unbuilt dwellings.
4. Verify construction status changes correctly when construction is available, resources are insufficient, or construction has already been used that day.
5. Use Previous and Next, open and close a building or construction sub-dialog, and exit to the Adventure Map. Verify the correct card restores and all lower-screen and physical controls remain unchanged.

- Selected hero portrait, movement, mana, primary stats, army, and artifacts summary.
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
- Thor startup cursor fix passed: controller/touch initialization enabled the software cursor, but the later display initializer overwrote it from the saved cursor-emulation setting. Thor display initialization now always retains software cursor emulation, while other platforms continue honoring their saved preference. Cold-start cursor visibility, controller buttons, touchscreen input, and both displays passed on the Thor without toggling away from Follow Standard mode.
- Verify Snapdragon 865 and Snapdragon 8 Gen 2 Thor variants where hardware is available.
- Document ADB diagnostics and capture context/action transitions under the `fheroes2-thor` log tag.

## Fork maintenance and upstream synchronization

Status: `planned`

- Create or select a user-owned GitHub fork before publishing. The current `origin` points directly to the official `ihhub/fheroes2` repository for both fetch and push; change the final layout so `origin` is the maintained Thor fork and `upstream` is the official read-only source.
- Keep Thor-specific changes isolated behind `TARGET_AYN_THOR`, in focused commits and dedicated bridge/UI files where practical, to reduce conflicts with upstream gameplay work.
- Maintain a clean upstream-tracking branch and periodically integrate `upstream/master` into `ayn-thor-dual-screen` using a documented merge or rebase policy. Never rewrite a published validated branch without an explicit decision.
- Before each upstream integration, record the last validated Thor commit and create a recoverable tag or backup branch. Review upstream Android, SDL/controller, rendering, resolution, JNI, and dialog-event changes for overlap.
- After integration, run Android build and lint through the short `R:` path, inspect the Thor-specific diff, install on hardware, and repeat a compact dual-screen, controller, context-transition, and save/load smoke test.
- Add an integration log describing the upstream commit, conflicts and resolutions, APK checksum, device variant, and validation result. Consider CI for compile/lint once the fork remote is established.
