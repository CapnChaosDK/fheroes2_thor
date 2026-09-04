# AYN Thor development backlog

This file is the maintained backlog for Thor-specific work. Update the status and acceptance criteria here when a milestone changes; do not discard deferred items when implementing an earlier milestone.

Status values: `planned`, `in progress`, `blocked`, `done`, `deferred`.

## Latest release checkpoint

- `thor-v0.9.0` was published on 2026-09-03 as a debug-signed AYN Thor prerelease.
- Release: https://github.com/CapnChaosDK/fheroes2_thor/releases/tag/thor-v0.9.0
- Source commit: `271d2a0fa3c9f55d69b20b5caf01169536478b72`.
- APK: `fheroes2-thor-v0.9.0-debug.apk`.
- APK SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.
- The release adds Hero Meeting direct manipulation, tap and whole-army transfers, player-installed Hero and creature visuals, complete in-game Adventure and File Options, and semantic anti-stuck gameplay dialogs with exact save/load, treasure, Level Up, Arena, standard-prompt, and battle-result controls. It retains every hardware-validated v0.8.0 map, menu, editor, campaign, information, and input workflow.

## Latest validated development checkpoint

- The precise Hero Meeting stack-splitting checkpoint at `b09695653a83e48c2779e0002f076710e14f68f4` is the latest hardware-validated source state. Prerelease `thor-v0.9.0` remains the latest published release.
- This checkpoint adds lower-screen long-press splitting into empty or matching slots across either army, exact One/Half/Max and ±1/±10 controls, live count previews, revision-bound native validation, safe cancellation, and explicit whole-army creature-type guidance without a misleading per-slot KEEP badge. It retains every validated v0.9.0 feature.
- Debug APK SHA-256: `884A70B1F60A3CFAE4F3DE217096D90C003ACBB72898B95C1AE162DA4E03C35F`.
- Android build and lint passed. The APK installed and launched explicitly on the Thor, and the user passed the complete six-check splitting/regression run plus the focused whole-army-guidance correction retest.

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
- Dialog deck simplification passed on the Thor: modal Dialog and safe Fallback show only centered Confirm and Cancel actions. A live lower-display capture confirmed that the redundant directional navigation deck is removed; Main Menu and planned navigable menu work remain unchanged.
- Battle context and its eight-action layout verified in a live Battle Only match.
- Adventure Map, Hero, and Castle contexts verified by the user on the Thor.
- Battle Spell, Wait/Defend, Turn Order, and Options actions verified by the user.
- Nested Battle confirmation correctly switches to Dialog and restores Battle after cancellation.
- Automatic battle resolution was traced to the standard `auto resolve battles` setting and resolved by selecting Manual mode.
- Dynamic measured-text fitting was verified from a live lower-display capture; long labels retain inset from the inner button frame across the shared renderer.

## Milestone 2: semantic actions

Status: `done`

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

Status: `in progress`

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

Status: `passed`

1. Open a Town and a Castle and compare the settlement name, type, and faction with the upper screen.
2. Move a visiting hero into and out of the settlement and verify the hero name appears and clears without stale information.
3. Compare dwelling levels I through VI with the upper screen: current recruitable counts, normal weekly growth, Well bonuses, and `--` for unbuilt dwellings.
4. Verify construction status changes correctly when construction is available, resources are insufficient, or construction has already been used that day.
5. Use Previous and Next, open and close a building or construction sub-dialog, and exit to the Adventure Map. Verify the correct card restores and all lower-screen and physical controls remain unchanged.

All five Castle/Town information checks passed on the Thor, including settlement and visiting-hero updates, dwelling availability and growth, construction status, context restoration, and control regression coverage.

### Battle information card

- The Battle card shows the round, acting army color, active creature stack count and name, Attack, Defense, total damage range, current speed, the wounded/front creature's hit points, aggregate surviving-stack hit points, remaining shots, and up to two active spell effects.
- The lower line shows the next five valid stacks using the battle engine's existing turn-order calculation, including each stack's current side, count, and creature name.
- Snapshots refresh at the start of a unit turn and after applied actions that can change the active stack or turn order. Unchanged snapshots do not redraw the Android presentation.
- Existing Battle semantic actions, enabled states, confirmation handling, and physical controls are unchanged. Target selection and damage prediction remain outside this read-only slice.
- Android build and lint pass through the required short `R:` mapping. The APK installed successfully and opened the command deck on display 4.

### Battle information device validation

Status: `passed`

1. Start a manual battle and compare the round, acting side, active stack count, and creature name with the upper screen.
2. Compare Attack, Defense, total damage range, speed, hit points, and remaining shots for melee and ranged stacks.
3. Compare the next-five-stack line with the upper turn-order display, then use Wait/Defend and advance to another round to verify ordering and round updates.
4. Take damage, lose a stack, fire a ranged attack, and apply a beneficial or harmful spell. Verify hit points, shots, effects, and turn order refresh without stale values.
5. Open and close Battle Options, spell selection, and a confirmation dialog. Verify the Battle card restores, all text remains inside its borders, and lower-screen and physical controls remain unchanged.

Checks 1, 3, and 4 passed on the Thor. Check 2 initially exposed ambiguous aggregate-only hit points; the corrected card shows both `UNIT HP current/full` and `STACK HP current/full`. Check 5 exposed that Battle Options did not enter the Dialog context, allowing repeated lower-screen actions to queue and stack settings windows. Battle Options now uses a scoped Dialog context, which clears queued Battle actions and restores the Battle context after one close.

Corrected checks 2 and 5 passed on the Thor. All five Battle information checks are validated, including active-stack accuracy, per-creature and aggregate hit points, turn-order refresh, spell and casualty updates, single-instance Options behavior, context restoration, and control regression coverage.

- Selected hero portrait, movement, mana, primary stats, army, and artifacts summary.
- Kingdom resources, player color, and current date.
- Explicit privacy/fog-of-war rules so the lower screen never exposes hidden information.

## Navigable menu workflows

Status: `in progress`

Goal: make the lower display follow menu hierarchy so choices can be completed directly instead of falling back to generic Dialog navigation.

### New Game first slice

- When New Game opens, replace the Main Menu deck with Standard, Campaign, Multiplayer, Battle Only, Settings, and Back.
- Campaign opens an available-assets-aware choice between Original and Expansion, plus Back.
- Multiplayer opens Hot Seat and Back. Hot Seat then opens 2, 3, 4, 5, and 6 Players plus Back.
- Back restores exactly one parent deck. Selecting an option emits exactly one action and clears stale queued actions when the menu state changes.
- Publish explicit native menu states and stable semantic actions rather than inferring submenus from Java labels or reusing overlapping keyboard shortcuts.
- Preserve upper-screen mouse, touchscreen, physical controller, configurable hotkeys, and text-support behavior.

### New Game implementation and validation

- Four explicit native states now represent New Game, Campaign, Multiplayer, and Hot Seat menus instead of inferring the visible submenu from enabled upper-screen buttons.
- Stable semantic actions cover Standard, Campaign, Multiplayer, Battle Only, Settings, Original and Expansion campaigns, Hot Seat, player counts 2 through 6, and Back.
- Back restores exactly one parent state: Hot Seat to Multiplayer, Campaign or Multiplayer to New Game, and New Game to Main Menu.
- Campaign availability follows installed assets. Expansion remains visible but muted when Price of Loyalty data is unavailable.
- Context changes clear stale queued taps, while the existing keyboard, mouse, touchscreen, and physical-controller paths remain available.
- Android build and lint pass through the required short `R:` mapping. The APK installed and cold-launched successfully on the Thor. Live lower-display checks verified the New Game, Multiplayer, and Hot Seat layouts and the complete Back chain from Hot Seat to Multiplayer to New Game to Main Menu. Campaign was correctly muted for the current asset set; selection actions and the asset-dependent Campaign path remain pending manual validation.
- User validation passed Standard, Battle Only, New Game Settings, all Hot Seat player counts, rapid-tap rejection, and physical, mouse, and touchscreen controls. Main Menu Settings initially exposed a missing temporary Dialog context; the correction scopes that settings window as Dialog and restores Main Menu on close. A live lower-display check and the focused user retest both passed Main Menu to Settings Dialog and one Cancel back to Main Menu. The New Game first slice is hardware-validated for the current asset configuration.

### Load Game implementation and validation

- Load Game is flattened to Standard, Campaign, Hot Seat, and Back on both displays; the redundant Multiplayer to Hot Seat intermediate menu has been removed.
- The four lower-screen choices use a balanced 2 by 2 Heroes II-styled layout.
- Standard, Campaign, and Hot Seat availability is derived before rendering from compatible saves and installed campaign assets. Empty categories remain visible but muted and ignore touch or hotkeys.
- Stable semantic Load Game actions execute on the game thread. Back returns directly to Main Menu, while closing a category's existing save browser restores the Load Game deck.
- Mouse, touchscreen, physical controller, configurable hotkeys, and text-support behavior remain available.
- Android build and lint pass through the required short `R:` mapping. The APK installed and cold-launched successfully on the Thor. Live lower-display checks verified the balanced Load Game layout, Standard enabled, Campaign and Hot Seat muted for the current save and asset set, Back restoration to Main Menu, Standard save-browser Dialog context, and exact Load Game restoration after one Cancel. Focused user validation passed the layout, muted-action rejection, Standard browser and restoration, Back, rapid-tap protection, and physical, mouse, and touchscreen controls. The Load Game slice is hardware-validated for the current save and asset configuration.

### Scenario Setup implementation and validation

- Standard and Hot Seat scenario setup expose Select Map, all five difficulty levels, Start, and Back directly on the lower display.
- The reserved information card shows the selected map, human-player count, current difficulty, and rating. Difficulty changes refresh both displays immediately.
- Select Map temporarily uses the modal Dialog deck and restores Scenario Setup exactly once after closing. Stable semantic actions run on the game thread and retain the existing mouse, touchscreen, physical-controller, hotkey, and text-support paths.
- Enabled-action masks now use context-local bit positions after stable action identifier 63. This preserves existing identifiers while allowing later menu contexts to grow without widening the JNI mask.
- Player editing remains the next Scenario Setup slice: previous/next player, control type, faction, and handicap controls will be planned separately after this controls-and-information slice is hardware-validated.
- Android build and lint passed through the required short `R:` mapping, and the APK installed successfully on the connected Thor. Live checks verified the Standard layout, information card, difficulty and rating refresh, action-mask handling at and beyond identifier 63, and Select Map cancellation restoring Scenario Setup exactly once.
- User hardware validation passed all five focused checks: layout and border readability; all five difficulty choices and immediate two-screen refresh; Select Map selection, cancellation, and non-stacking restoration; Back plus Hot Seat player-count context; and single-shot Start with physical-controller, upper-touchscreen, and mouse regression coverage.

### Scenario Setup player editing

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-23.

- Previous Player and Next Player cycle through the scenario's player positions, wrap at either end, highlight the selected upper-screen player, and refresh the lower information card.
- In Standard games, Set Human moves the sole human assignment to the selected human-capable position and carries its handicap, matching the existing upper-screen rules. It is unavailable for the current human and computer-only positions.
- In Hot Seat games, Select / Swap uses the existing two-position swap rules instead of independently toggling Human/AI. This preserves the chosen human-player count; selecting the same position cancels and invalid targets are muted.
- Previous Faction and Next Faction are available only for a player position whose scenario race is changeable. Handicap cycles None, Mild, and Severe only for a human-controlled position.
- The information card retains the map, human-player count, difficulty, and rating while adding the selected player's name, color, control, faction, and handicap. A pending Hot Seat swap identifies its source position.
- Existing Select Map, difficulty, Start, Back, modal restoration, mouse, touchscreen, physical-controller, hotkey, and text-support paths remain in place.
- Android build and lint pass through the required short `R:` mapping. The candidate APK installed successfully on the connected Thor and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; SHA-256: `2520C1C55BC954DFA2E1F91FF37A7BE2BE75305FEEE6B1BA22D5E0FE8930D01F`.
- All five focused checks passed on the Thor: wrapped player navigation and synchronized upper/lower selection; Standard human-position transfer and handicap retention; fixed-count Hot Seat swapping, cancellation, and invalid-target rejection; faction and handicap availability; and regression coverage for Select Map, difficulty, Start, Back, rapid taps, physical controls, upper touchscreen, and mouse.

#### Focused player-editing validation

1. Cycle players in both directions, including wraparound, and verify the upper highlight and lower information always identify the same player.
2. In Standard, transfer the human position between valid colors. Verify the current-human and computer-only targets are muted and the handicap follows the human assignment.
3. In Hot Seat, swap a human and flexible AI position with the two-step flow. Verify the human count remains fixed, selecting the source again cancels, and invalid targets ignore input.
4. Cycle both directions through factions on a changeable position and verify fixed-faction controls are muted. Cycle all three handicap states and verify AI positions cannot receive a handicap.
5. Exercise Select Map, all difficulties, Start, Back, rapid taps, physical controls, upper touchscreen, and mouse to confirm the previously validated Scenario Setup behavior remains intact.

### Battle Only setup

Status: `passed`; behavior and focused acceptance tests approved and hardware-validated on 2026-08-27.

- Add a dedicated Battle Only Setup context instead of showing the generic fallback deck.
- Show attacker, defender, defender control, terrain, occupied army slots, and army readiness in the lower information card.
- Expose Select Attacker, Select Defender, Previous Terrain, Next Terrain, Defender Control, Reset, Start, and Exit as stable semantic actions processed on the game thread.
- Hero selection temporarily uses the modal Dialog deck and restores Battle Only Setup exactly once. Defender Control is available only for a defending hero, and Start mirrors the validity of the active hero or monster armies.
- Reset restores the existing defaults, Start transitions to the validated Battle deck, and Exit returns to Main Menu. Existing upper-screen army/loadout editing, mouse, touchscreen, physical-controller, and hotkey paths remain available.
- Automated device testing exposed that Battle Options and a Battle confirmation could restore the Battle deck while leaving its information card empty. The settings-dialog restoration point and completed semantic actions that remain in the current turn now republish the active unit snapshot after returning from Dialog. Focused Options and confirmation restoration retests passed on the final rebuilt candidate.
- A fully native lower-screen loadout editor for army slots, primary and secondary skills, artifacts, spellbook, morale, and luck is deferred as a separate later slice; this first slice does not discard or duplicate those engine-owned editors.

#### Battle Only device validation

- The final candidate installed and launched successfully on the connected AYN Thor as `org.fheroes2.thor/org.fheroes2.GameActivity`; the command deck opened on display 4.
- Live two-display captures verified the initial Lord Kilburn versus Monsters setup, Random terrain, AI defender, occupied-stack counts, ready state, readable eight-button layout, and muted Defender Control for a monster defender.
- Lower-screen semantic actions changed terrain in both directions, opened the attacker selector, cancelled it through Dialog, restored Battle Only Setup exactly once, selected Sir Gallant as defender through the existing physical navigation path, toggled the defender to Human, and refreshed both screens immediately.
- Reset restored Lord Kilburn versus Monsters, Random terrain, the muted defender-control action, and ready state. Start launched exactly one match and transitioned to the existing Battle deck and information card. Exit returned directly from a checkpointed Battle Only Setup state to Main Menu.
- Battle Options testing exposed an empty information card after Dialog restoration. The corrected candidate republishes the current unit after the settings context guard closes. A cancelled Auto confirmation exposed the same general pattern; completed semantic Battle actions that remain in the current turn now republish the active-unit snapshot. Options cancellation and confirmation cancellation both passed focused retests with the full Battle card restored.
- Android build and lint pass through the required short `R:` mapping. Final candidate SHA-256: `63AD483B6067024CE18E92C963A28C51450F11102EE77126EEDDD79CC586FC94`.
- The user completed the remaining manual checks successfully: duplicate-hero rejection restored Battle Only Setup correctly; invalid and restored active armies muted and re-enabled Start; and rapid selector taps, physical controls, upper touchscreen, and mouse all passed without regression.

#### Focused Battle Only setup validation

1. Open Battle Only and verify attacker, defender, control type, terrain, occupied army slots, and readiness agree on both screens. Cycle terrain in both directions, including Random.
2. Open both hero selectors, cancel and confirm selections, and verify each modal restores Battle Only Setup once. Attempt to select the same hero for both sides and verify the existing error handling restores correctly.
3. Select a defending hero and toggle Human/AI. Reset to the default monster defender and verify Defender Control becomes muted.
4. Make either active army invalid with the upper editor and verify Start mutes; restore a valid army and verify Start enables. Confirm upper army and loadout editing leaves the information card synchronized.
5. Test Reset, Exit, rapid taps, and Start. Verify Start launches exactly one battle and switches to the validated Battle deck; confirm physical controls, mouse, and upper touchscreen remain intact.

### High Scores workflow

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- Standard and Campaign tables use dedicated lower-screen contexts. Main Menu entry continues to open Standard first.
- Standard exposes Campaign and Exit; Campaign exposes Standard and Exit. The lower title identifies the active table without duplicating the ten upper-screen score entries.
- Campaign availability mirrors the upper screen and requires the complete Succession Wars campaign files. The current Thor has compatible campaign assets installed, so both directions can be validated.
- Stable semantic actions execute on the game thread. Context changes clear queued taps, Exit returns directly to Main Menu, and High Scores help dialogs restore the active table and its enabled actions.
- Existing score loading, default entries, post-victory name entry, new-score highlighting, mouse, touchscreen, keyboard, hotkey, and physical-controller paths remain unchanged.
- Android build and lint pass through the required short `R:` mapping. The candidate APK installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; SHA-256: `3EF381E02C09A8E566BD83900E18F1461D2EACAB6E8D34EA3D5F7B61643A887A`.
- All five focused checks passed on the Thor: initial Standard synchronization, two-way Standard/Campaign switching with the installed assets, direct Exit from both variants, rapid-tap rejection, help-dialog restoration, and physical-controller, upper-touchscreen, mouse, and keyboard-close regression coverage.

#### Focused High Scores validation

1. Open High Scores and verify Standard appears on both screens, with Campaign and Exit on the lower display.
2. Switch Standard to Campaign and back to Standard. Verify both screens identify the same table after each single tap.
3. Use Exit from both variants and verify it returns directly to Main Menu exactly once.
4. Rapidly tap the table switch and Exit controls. Verify there are no stacked transitions, delayed actions, or incorrect restored contexts.
5. Verify upper touchscreen, mouse, physical controls, and keyboard close behavior remain intact. Open and close a High Scores help dialog and verify the correct lower deck restores.

### Succession Wars campaign selector

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- Selecting Original retains the existing Succession Wars intro video. While it plays, the lower display shows a dedicated non-interactive Campaign Intro state so Campaign-menu taps cannot queue into the selector.
- The animated Roland/Archibald selector exposes Roland, Archibald, and Back as stable semantic lower-screen actions. A campaign selection executes once and hands off to the existing campaign scenario screen.
- Back stops selector audio and returns to New Game. Context changes clear stale taps, while upper-screen mouse, touchscreen, physical-controller, configurable-hotkey, and text-support behavior remain available.
- If the selector video is missing, the existing warning and Roland fallback remain in place; the lower display uses Dialog during the warning and does not expose invalid campaign choices.
- The Price of Loyalty four-campaign selector remains a separate next slice. The connected Thor contains all 24 required `.HXC` maps and all four expansion selector videos, but its distinct hover-driven animations and four-way selection behavior require focused implementation and validation.
- Android build and lint pass through the required short `R:` mapping. The candidate APK installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; SHA-256: `587803916FEB3DD1951BE7D5EA91925E30C014875E4AF3692BE00161079395AA`.
- All five focused checks passed on the Thor: non-interactive intro synchronization; Roland and Archibald selection into their matching first scenarios; Back returning once to New Game without residual audio; intro skipping through physical, upper-touchscreen, mouse, and keyboard paths; and rapid-tap rejection without stacked or stale transitions.

#### Focused Succession Wars selector validation

1. Open New Game, Campaign, Original. Verify the intro plays normally and the lower display shows Campaign Intro without stale Campaign-menu actions.
2. Let the intro finish and verify the lower display changes to Original Campaign with Roland, Archibald, and Back. Select each campaign separately and verify the matching first scenario opens.
3. Re-enter the selector and use Back. Verify it returns once to New Game with no leftover selector audio or delayed campaign selection.
4. Skip the intro through the existing physical, upper-touchscreen, mouse, and keyboard paths and verify the lower selector appears correctly afterward.
5. Rapidly tap Roland, Archibald, and Back. Verify only one transition occurs and the resulting scenario or New Game context is correct.

### Price of Loyalty campaign selector

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- The animated expansion selector exposes Price of Loyalty, Voyage Home, Wizard's Isle, Descendants, and Back as stable semantic lower-screen actions. A campaign selection executes once and hands off to the existing campaign scenario screen.
- The existing resource gate remains authoritative: the Expansion entry requires `X_LOADCM`, `X_IVY`, and all maps from all four campaigns.
- The existing per-video behavior is preserved. A missing `IVYPOL.SMK` shows the warning under Dialog context and falls back to Price of Loyalty; any other missing selector video leaves its campaign selectable without that hover animation.
- Upper-screen hover animations, static-background restoration, mouse, touchscreen, physical-controller, configurable-hotkey, and text-support behavior remain available. Back and Cancel return to New Game after clearing selector audio, palette state, and queued lower-screen actions.
- Android build and lint pass through the required short `R:` mapping. The candidate APK installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; SHA-256: `6750BD33D7568AE384B6665BF0B553E277675D634176090E9A2BEC2B5D6E1539`.
- All five focused checks passed on the Thor: all four lower-screen campaign choices opened their matching first scenarios; all hover animations switched, looped, and restored the static background; Back and Cancel returned cleanly without residual audio or palette corruption; rapid taps produced one transition; and upper touchscreen, mouse, physical controls, configurable hotkeys, and the Original selector retained their validated behavior.

#### Focused Price of Loyalty selector validation

1. Open New Game, Campaign, Expansion. Verify the lower display shows Price of Loyalty, Voyage Home, Wizard's Isle, Descendants, and Back.
2. Select each campaign separately and verify its matching first scenario opens exactly once.
3. Hover all four upper-screen regions and verify their animations start, switch cleanly, loop correctly, and restore the static background when the pointer leaves.
4. Re-enter and use Back through the lower display and the existing physical or keyboard Cancel paths. Verify New Game returns once with no residual audio, palette corruption, or delayed selection.
5. Rapidly tap different campaign choices and Back. Verify only one transition occurs, then check upper touchscreen, mouse, physical controls, configurable hotkeys, and the validated Original selector for regressions.

### Game Settings workflow

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- The top-level Game Settings panel uses a dedicated lower-screen context with Language, Graphics, Audio, Hot Keys, Cursor Type, Interface Type, Text Support, and Okay / Back.
- Language follows the engine's supported-language list and is visible but muted when English is the only available choice. The lower information card shows the current language, cursor, interface, and text-support values.
- Cursor Type, Interface Type, and Text Support use the existing settings paths and refresh both displays immediately. Lower-screen duplicate taps are discarded while the panel rebuilds.
- Language, Graphics, Audio, and Hot Keys remain engine-owned child dialogs. They temporarily use Dialog context and restore Game Settings exactly once when closed.
- Configuration saving, Main Menu and New Game routing, right-click help, upper touchscreen, mouse, physical controls, configurable hotkeys, and text-support behavior remain unchanged.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `3D5254C2970770B5268FAA955CA2F1800E30226EFE04FDC0899EF6B32BCBC2EB`.
- Brief post-launch checks found the game process and presentation window without fatal runtime errors. Display 4 remained registered with physical ID `4630946482288158084`; the user then completed the visual and interaction validation manually.
- All five focused checks passed on the Thor: both entry paths and closing behavior; immediate cursor, interface, and text-support synchronization; child-dialog restoration; persisted settings and language availability; and rapid-tap, help, input-path, menu, and campaign-selector regression coverage.

#### Focused Game Settings validation

1. Open Settings from Main Menu and from New Game. Verify both displays agree, all eight lower controls are readable, and closing follows the existing destination behavior.
2. Toggle Cursor Type, cycle every Interface Type value, and toggle Text Support twice. Verify both displays refresh immediately and rapid taps do not queue duplicate changes.
3. Open and close Language, Graphics, Audio, and Hot Keys. Verify each child uses Dialog controls and restores Game Settings exactly once after confirmation or cancellation.
4. Change representative settings, close with Okay / Back, reopen, explicitly relaunch the app, and verify the saved values. Confirm Language is enabled or muted according to the installed assets.
5. Exercise lower touch, upper touchscreen, mouse, physical controls, configurable hotkeys, right-click help, and rapid mixed taps. Verify Main Menu, New Game, and both validated campaign selectors retain their behavior.

### Editor pre-entry hierarchy

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- Main Menu exposes Editor as a stable semantic lower-screen action. Its enabled state follows the existing Price of Loyalty resource gate, while the other validated Main Menu input paths remain unchanged.
- The Map Editor menu exposes New Map, Load Map, and Main Menu. New Map exposes From Scratch, Random, and Back; each creation mode then exposes Small, Medium, Large, Extra Large, and Back under a mode-specific lower-screen context.
- Back restores one visible parent at a time. Main Menu exits the pre-editor hierarchy directly, and context changes clear stale or duplicate lower-screen taps.
- Load Map, right-click help, random-map configuration, warnings, and other engine-owned modal windows use Dialog context. Cancelling or closing them restores the correct Editor parent without duplicating their logic on Android.
- Creating or loading a map clears the pre-editor actions and enters the safe Fallback deck before the existing Editor interface starts. In-map File Options, System Options, tools, and information remain later Editor slices.
- Existing upper-screen mouse, touchscreen, physical-controller, configurable-hotkey, text-support, map-generation, map-loading, and resource-check behavior remains available.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `96C1BDD6C37E7D32632F7580BF22C613A0FD4D20950E60DD054C9BB5428B3808`.
- All five focused checks passed on the Thor: Main Menu entry and Map Editor synchronization; both creation modes, both size decks, and exact Back behavior; scratch-map entry and Random configuration restoration; Load Map cancellation and loading; and rapid-tap, help, physical-control, upper-touchscreen, mouse, Main Menu, New Game, Settings, and campaign-selector regression coverage.

#### Focused Editor pre-entry validation

1. Enter Editor from Main Menu and verify both displays agree on New Map, Load Map, and Main Menu. Confirm the lower Editor entry follows the installed-resource availability.
2. Navigate New Map, From Scratch and Random, and both map-size lists. Verify every Back action restores exactly one visible parent and Main Menu exits directly.
3. Create one scratch map and open the Random configuration for a representative size. Cancel and confirm separately; verify Dialog appears only for the configuration and no pre-editor action fires inside the Editor.
4. Open Load Map, cancel it, and load an existing map if available. Verify empty-list warnings, cancellation, and load failures restore Map Editor once.
5. Exercise rapid mixed taps, right-click help, upper touchscreen, mouse, physical controls, configurable hotkeys, and text support. Recheck Main Menu, New Game, Settings, and both campaign selectors for regressions.

### In-map Editor File Options

Status: `passed`; behavior and focused acceptance tests approved and hardware-validated on 2026-08-27.

- Entering a created or loaded map replaces the generic fallback deck with a dedicated Map Editor context. This first in-map slice exposes File Options while System Options, tools, specifications, and richer map information remain separate later slices.
- File Options mirrors the existing engine-owned New Map, Load Map, Start Map, Save Map, Main Menu, Quit, Auto Playtest, and Cancel actions without duplicating their validation, saving, playtest, or transition logic on Android.
- Engine-owned confirmations, warnings, save/load windows, and help use Dialog context. Cancelled or failed operations restore File Options exactly once, while Cancel returns to Map Editor.
- Starting another workflow clears stale lower-screen actions, and semantic operations reject queued rapid taps until the active operation or nested dialog completes.
- Existing upper-screen mouse, touchscreen, physical-controller, configurable-hotkey, and text-support behavior remains available.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `486EDBAA21185F2D44AF5C0E9F4FBF303657FE6EF03CA05D1E36BA8A2AD8DE16`.
- All five focused checks passed on the Thor: created and loaded maps entered the dedicated Map Editor deck; File Options and nested confirmations restored correctly; save behavior did not duplicate; Start Map, Auto Playtest, Main Menu, and Quit followed existing transition rules; and rapid taps, physical controls, upper touchscreen, mouse, hotkeys, Editor pre-entry, and established Main Menu workflows retained their behavior.

#### Focused in-map Editor File Options validation

1. Create and load a map, and verify the lower screen enters Map Editor and File Options matches the upper menu.
2. Exercise New Map and Load Map confirmations. Cancel each and verify File Options restores once; confirm each and verify the correct pre-entry workflow appears.
3. Save a map, including cancellation and any overwrite prompt. Verify no duplicate save occurs and Map Editor restores afterward.
4. Test Start Map with invalid and valid maps, Auto Playtest, Main Menu, and Quit. Verify failed or cancelled operations restore File Options, while confirmed transitions occur exactly once.
5. Test Cancel/Back, help, rapid mixed taps, physical controls, upper touchscreen, mouse, and hotkeys. Recheck Editor pre-entry and established Main Menu workflows for regressions.

### In-map Editor System Options

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- The Map Editor lower-screen context now exposes System Options beside File Options. The System Options panel mirrors the existing engine-owned Language, Graphics, Audio, Hot Keys, Animation, Passability, Interface Type, Cursor Type, Scroll Speed, and Okay / Back actions.
- The lower information card shows the current language, animation, passability, interface, cursor, and scroll-speed values. Language follows the installed-language availability and is visible but muted when English is the only supported choice.
- Direct settings reuse the existing editor redraw, rebuild, runtime-update, and configuration-save paths. Graphics, Audio, Hot Keys, Language, and right-click help use Dialog context and restore System Options exactly once.
- Semantic operations reject queued rapid taps while the panel changes or a nested dialog is active. Closing System Options restores the in-map Map Editor deck without affecting File Options.
- Editor tools, map specifications, and richer map information remain deferred as separate later slices.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `51664E906A2EBE207CBB888CAD49E16CAA2BCFA14734C390DD852938994157FA`.
- All five focused checks passed on the Thor: both displays and the information card synchronized; direct settings updated immediately; child dialogs and language availability restored correctly; settings persisted across relaunch and interface rebuilds; and rapid taps, help, physical controls, upper touch, mouse, hotkeys, File Options, Editor entry, and established Main Menu workflows retained their behavior.

#### Focused in-map Editor System Options validation

1. Open System Options and verify both screens and the information card show matching current values.
2. Exercise Animation, Passability, Interface Type, Cursor Type, and every Scroll Speed value; verify immediate upper- and lower-screen synchronization.
3. Open and close Language, Graphics, Audio, and Hot Keys. Verify Dialog appears for each child, restores System Options once, and Language is enabled or muted according to the installed assets.
4. Close and reopen System Options, then explicitly relaunch the app and return to the Editor. Verify changed settings persist and the Editor remains usable after interface rebuilds.
5. Exercise rapid mixed taps, right-click help, upper touchscreen, mouse, physical controls, and hotkeys. Recheck File Options, Editor pre-entry, and established Main Menu workflows for regressions.

### In-map Editor Map Specifications

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- The Map Editor lower-screen context exposes Map Specifications beside File Options and System Options. Its main panel provides Map Name, Description, Player Setup, Difficulty, Victory, Loss, Rumors, Events, Map Language, Creator Notes, Okay, and Back / Cancel.
- Player Setup provides previous/next available-player navigation and cycles the existing Human, Computer, and Human-or-Computer states while preserving the Editor's requirement that at least one player remains human-capable.
- Victory and Loss use focused subpanels. Condition navigation filters through the same map-owned availability rules as the upper dropdowns, while condition-specific target, alliance, standard-victory, AI-eligibility, gold, and time controls reuse the existing condition UI state and validation.
- The lower information card follows tentative map, player, difficulty, language, victory, loss, alliance, gold, and time values. Nested text, rumor, event, language, town, hero, artifact, and help windows use Dialog context and restore their exact specifications parent.
- Subpanel Back returns to the main specifications panel while retaining tentative edits. Main Back / Cancel restores the complete map backup; Okay commits through the existing single Editor history action.
- Semantic operations reject queued rapid taps while contexts or child windows change. Existing upper touchscreen, mouse, physical controls, hotkeys, and right-click help remain available.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `068C22872A88D01BD4AC8AE199370819672268E8ADF000648B70858B5B4C0377`.
- All five focused checks passed on the Thor: newly created and loaded maps entered synchronized specifications panels; transactional text, metadata, player, difficulty, and language edits restored or committed correctly; all available victory/loss controls including three-player alliances worked with their map prerequisites; rumors, events, creator notes, and nested dialogs restored their exact parents; and rapid taps, help, upper touch, mouse, physical controls, hotkeys, File Options, System Options, Editor entry, and established menu workflows retained their behavior.
- Editor tools and richer map information remain deferred as separate later slices.

#### Focused in-map Editor Map Specifications validation

1. Open Map Specifications from newly created and loaded maps. Verify the upper window, lower main panel, and information card agree.
2. Change map text, player type, difficulty, and language. Verify main Back restores every value, while Okay preserves all changes after reopening and as one Undo/Redo history action.
3. Exercise every victory and loss condition available on the test map, including target selectors, alliances, gold, time, standard-victory, and AI controls. Verify unavailable conditions cannot be selected and each child restores its exact parent.
4. Add, edit, and cancel rumors, daily events, and creator notes. Verify child Cancel is local and main Back still rolls back accepted tentative child edits.
5. Exercise rapid mixed taps, right-click help, upper touchscreen, mouse, physical controls, and the existing specifications hotkey. Recheck File Options, System Options, Editor entry, and established menu workflows.

### In-map Editor Tools

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-27.

- The Map Editor lower-screen context exposes Editor Tools beside File Options, System Options, and Map Specifications. Its main panel provides Terrain, Landscape Objects, Detail, Adventure Objects, Kingdom Objects, Monsters, Streams, Roads, Erase, Magnify, Undo, Redo, and Back.
- Tool subpanels mirror the existing engine-owned terrain types, brush sizes, object categories and selectors, Detail Edit/Move/Copy modes, monster selection, and erase filters. Android only changes the native `EditorPanel` selection; placement, dragging, area selection, editing, moving, copying, validation, and erasure remain owned by the upper editor map.
- The information card follows the selected tool, terrain, brush, object category and selection, detail mode, erase filters, and live Undo/Redo availability. Undo and Redo are enabled only when the existing history manager permits them.
- Native object selectors and help use Dialog context and restore the exact tool parent. Context changes clear queued actions, and semantic controls are disabled while a selector or Magnify view is active.
- Existing upper touchscreen, mouse, physical controls, hotkeys, File Options, System Options, Map Specifications, and established menu workflows remain available. Richer map information remains deferred as a separate later slice.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `24BE278EA3FBE2CC447FB2BDDEF9B21220CD5F97A020C87668796ECD25F1B200`.
- All five focused checks passed on the Thor: both new and loaded maps stayed synchronized across every tool; terrain, brush, placement, Detail, Erase, Magnify, and Undo/Redo behavior worked through the native editor paths; selectors restored their exact parents; and rapid taps, help, upper touch, mouse, physical controls, hotkeys, File Options, System Options, Map Specifications, Editor entry, and established menu workflows retained their behavior.

#### Focused in-map Editor Tools validation

1. Open Editor Tools from newly created and loaded maps. Select all nine tools and verify the upper toolbar, lower subpanel, and information card agree.
2. Select terrain types and all four brush sizes, then draw single-tile, sized, and dragged-area terrain on the upper screen. Verify Undo/Redo availability and results.
3. Exercise every landscape, adventure, kingdom, monster, stream, and road category. Verify native selectors restore their exact parent and placement remains controlled by the upper map.
4. Exercise Detail Edit/Move/Copy and every Erase filter and brush size. Verify edits use the established history behavior and the lower information card remains synchronized.
5. Exercise rapid mixed taps, selector cancellation, right-click help, upper touchscreen, mouse, physical controls, hotkeys, Magnify, Undo/Redo, File Options, System Options, Map Specifications, Editor entry, and established menu regressions.

### In-map Editor map information

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-30.

- The base Map Editor deck uses the reserved lower-screen panel for read-only map information: map name, dimensions, difficulty, and available-player count.
- Pointing at the upper map adds live tile coordinates, terrain, and the same engine-owned object description used by the Editor's right-click popup. This includes existing resource amounts, monster counts, ownership, artifact, road, and multi-tile-object handling without a second metadata interpretation path.
- The snapshot follows pointer movement, edits, Undo/Redo, loaded maps, and committed Map Specification changes. The existing information bridge suppresses identical snapshots.
- File Options remains modal, while System Options, Map Specifications, and Editor Tools retain their validated context-specific cards. No new semantic command or editable Android-owned state is introduced.
- Deeper aggregate map statistics, validation diagnostics, and dedicated object-metadata views remain deferred as possible later slices.
- Android build and lint passed through the required short `R:` mapping on 2026-08-30. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `CD7DA44BA26E7F961210B1B102D480272106C067A72EF2BC3117D36EEC069A80`.

#### Focused in-map Editor map information validation

1. Create and load representative maps. Verify name, size, difficulty, and player count match Map Specifications.
2. Point at empty terrain, roads, resources, monsters, capturable objects, and multi-tile objects. Verify coordinates, terrain, and descriptions match the existing right-click information.
3. Under and around the pointer, change terrain, place, move, copy, and erase objects, then use Undo and Redo. Verify the card refreshes without stale values.
4. Open and close File Options, System Options, Map Specifications, Editor Tools, selectors, help, and other dialogs. Verify each existing card appears in its context and the map card returns correctly.
5. Exercise rapid pointer movement and lower-screen taps, upper touch, mouse, physical controls, and hotkeys. Recheck all validated Editor workflows for regressions.

All five map-information checks passed on the Thor, including map metadata, tile and object descriptions, live edit and Undo/Redo refresh, exact information-card restoration, rapid pointer movement, lower-screen actions, and physical, touchscreen, mouse, hotkey, and established Editor regressions.

### Touch minimap viewport control

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-30.

- The Adventure Map and base Map Editor information panels add a lower-screen minimap beside their existing information cards. The minimap uses a revisioned copy of the engine-owned radar pixels and visible-tile ROI, preserving native fog, terrain, object, ownership, and viewport rules without Android-owned map interpretation.
- A single-finger tap or drag submits normalized coordinates to a coalescing native mailbox. The SDL thread consumes only the newest request and recenters through the existing `GameArea` path; lower input never selects a tile, plots a route, changes focus, moves a hero, or edits the map.
- Viewport requests are accepted only in the live Adventure Map and base Editor contexts. They are cleared across context changes, disabled during hero movement, and rejected for stale contexts, cancellation, and multitouch.
- The minimap remains synchronized with upper scrolling, radar redraws, fog and object changes, Editor edits, and Undo/Redo. Existing command buttons, information cards, modal restoration, and physical, upper-touchscreen, mouse, and hotkey paths remain unchanged.
- Hero and castle quick-selection markers, zoom gestures, haptics, configurable layouts, and other richer interactive tools remain deferred.
- Android build and lint passed through the required short `R:` mapping on 2026-08-30. The candidate installed successfully and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `5259ED2DD2E2EE109ACC4CFC9AAC773BEF4CA9633A13638565685888583BC31C`.

#### Focused touch minimap validation

1. On representative Adventure maps, compare terrain, fog, ownership colors, and the viewport outline with the upper radar. Scroll and change focused heroes and towns to verify synchronization.
2. Tap the center, edges, and corners, then drag across the minimap. Verify accurate clamped camera movement without tile selection, route creation, hero movement, or focus changes.
3. In newly created and loaded Editor maps of different sizes, pan from the lower minimap and verify edits plus Undo/Redo update it without changing the active tool.
4. Open Hero, Castle, File and System Options, Map Specifications, Editor Tools, selectors, help, and dialogs. Verify minimap input is unavailable there and returns correctly afterward.
5. Exercise rapid drags, multitouch, cancelled touches, upper radar hide/show, physical controls, upper touchscreen, mouse, hotkeys, and established Adventure and Editor workflows without regression.

All five touch-minimap checks passed on the Thor, including Adventure radar fidelity and viewport synchronization, clamped tap and drag navigation without gameplay side effects, Editor editing and Undo/Redo synchronization, exact context restoration and input gating, rapid drag, multitouch, cancellation, upper-radar visibility, physical-control, touchscreen, mouse, hotkey, and established Adventure and Editor regressions.

### Hero and castle quick-selection lists

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-30.

- The Adventure Map deck retains Next Hero and Next Town and adds dedicated Heroes and Towns commands. Each opens a lower-screen-only selection list while the upper Adventure Map remains visible.
- Revisioned native snapshots expose every current kingdom-owned hero or settlement in the engine collection order, with native identity, name, compact status, and current-focus state. Android owns only presentation and paging.
- A list tap submits the snapshot revision and stable native identifier. The SDL thread rejects stale or mismatched requests, resolves the entry against the current kingdom collection, and selects it through the existing `SetFocus(...)` and `RedrawFocus()` paths.
- Successful selection centers the upper viewport and returns to Adventure immediately. Back restores Adventure without changing focus. Lists and requests are unavailable during hero movement, dialogs, and every non-Adventure context.
- Minimap entity markers, portraits or sprites, list-driven dialog opening, reordering, long-press behavior, army drag-and-drop, and configurable layouts remain deferred.
- Android build and lint passed through the required short `R:` mapping. The candidate installed successfully and was explicitly launched on the connected Thor as `org.fheroes2.thor/org.fheroes2.GameActivity`; APK SHA-256: `FF4FB232AF0BEE4968E3D334A52FA45D08C77E649182C3FA1CE8553DEFDB4AA5`.

#### Focused hero and castle quick-selection validation

1. With several heroes including sleeping and exhausted heroes, verify native order, status, current highlight, paging, and direct selection. Confirm upper centering, information, minimap, path, and portrait synchronization.
2. With several towns and castles, verify names and types, paging, direct focus, upper centering, icon selection, information, and minimap synchronization.
3. Recruit or dismiss a hero and gain or lose a settlement where practical. Verify reopening the list reflects the current native collections without stale entries.
4. Exercise Back, rapid taps, context changes, multitouch, and hero movement. Verify there are no stacked screens, duplicate selections, unintended routes or movement, dialogs, or stale focus.
5. Recheck Next Hero, Next Town, all Adventure commands, touch-minimap navigation, Hero and Castle dialogs, physical controls, upper touchscreen, mouse, and hotkeys.

All five quick-selection checks passed on the Thor, including hero and settlement order, status, focus highlighting, paging, direct selection, upper centering, information and minimap synchronization, live kingdom collection refresh, Back behavior, movement gating, rapid taps, multitouch, and Adventure, dialog, physical-control, touchscreen, mouse, and hotkey regressions.

### Expanded Adventure Map

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-30.

- The normal Adventure deck adds a dedicated Map command without replacing View World or any established action. It opens a lower-screen-only overview using roughly 940 square pixels while the upper Adventure Map remains active.
- The overview reuses the engine-owned radar pixels, fog and ownership rendering, visible-tile ROI, and viewport bridge. Android does not interpret map terrain or visibility.
- A revisioned native selection snapshot adds current-player hero and settlement identity, kind, world position, and focus state. Android presents circular hero and square settlement markers, a focused halo, and a visual offset when a hero and settlement share a tile; native code revalidates every selection against the live kingdom collections.
- A marker tap focuses and centers the matching entity while keeping the overview open. An empty tap recenters the viewport, and movement beyond the touch slop becomes viewport dragging without selecting a marker. Multitouch, cancellation, stale revisions, movement, and context changes reject pending input.
- Heroes and Towns open the validated lower-screen selection lists and return to the overview. Back or physical Cancel restores the ordinary Adventure deck without changing focus. The compact Adventure and Editor minimaps remain unchanged.
- Label, clustering, zoom, sprite, configurable marker, and marker-detail support remains deferred.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `7CED56F9A281262DD9A6195DA561336222416CD9A3965B82609348C2346BC08F`. The APK installed successfully over wireless ADB and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the resumed game activity and the companion presentation window on display 4.
- The initial device run exposed a solid-color expanded map because the lower-only context transition could arrive without a freshly tagged radar snapshot. The corrective candidate explicitly republishes the current engine radar when the overview opens or is restored and retains the last valid Adventure radar during that transition. Build and lint pass; corrected APK SHA-256: `FB4AFA4EB98F0206351AEB010023588847CD1D1839AABDACE3490AC244FA7BC8`. It installed successfully and was explicitly relaunched on the Thor.
- The first correction did not resolve the solid sand-color overview. The user confirmed the compact minimap is correct before opening the overview, becomes blank after closing it, and recovers as soon as upper-map movement triggers a normal radar redraw. The next correction therefore removes the direct snapshot publication and schedules a full engine `REDRAW_RADAR` on overview entry and list restoration, ensuring map pixels are rebuilt through the same path that recovers the compact minimap. Build and lint pass; candidate APK SHA-256: `1782F784A5C8CCA89E7D59183036285AACEE95D2FE2BE4BA446B38A36B031218`. The candidate installed successfully and was explicitly relaunched on the Thor.
- The full-redraw correction also retained the failure. Targeted runtime diagnostics then confirmed a valid context-43 radar snapshot (`144 x 144`, `72 x 72` world, 976 pixel transitions), a valid Android bitmap, and correct `886 x 886` expanded bounds. This isolates the fault to large bitmap magnification on the Thor secondary-display Canvas rather than native data or context gating. The next candidate pre-scales the radar bitmap to its expanded dimensions before drawing it 1:1. Build and lint pass; APK SHA-256: `7A909270A3E82853A6A40B745FBE5576B4478C1C5AD99951421E664A0BC1A302`. It installed successfully and was explicitly relaunched on the Thor.
- The pre-scaled and software-rendered candidates retained the solid map. A targeted lower-display screenshot exposed the exact color as the overview's gold border color (`RGB 255, 230, 154`): marker drawing restored the shared Android `Paint` to fill mode, so the final border operation covered the bitmap, viewport, and markers. The corrected candidate explicitly restores stroke mode before drawing the border and removes the diagnostic logging, pre-scaling, and software-layer workarounds. Build and lint pass; APK SHA-256: `EDE99644D8D34DD58F10C7B78D17269D6F96C505D6A9A5EDDE1B22E9517EFBAF`. It installed successfully and was explicitly relaunched on the Thor.

#### Focused expanded-map validation

1. Open Map on small, medium, and maximum maps. Verify the overview is large and readable, matches terrain, fog, and ownership, and Back restores Adventure exactly once.
2. With several heroes and settlements, verify marker position, type, and focus highlighting. Select several markers and confirm exact upper centering plus portrait, information, path, lists, and focus synchronization.
3. Test adjacent and overlapping entities, including a visiting hero at a settlement. Confirm deterministic selection without accidental route creation or movement.
4. Tap empty areas and drag across the map and markers. Confirm smooth clamped viewport navigation, clear tap-versus-drag behavior, and safe rapid-tap and multitouch rejection.
5. Exercise hero movement, dialogs, Hero and Castle screens, turn changes, physical controls, upper touchscreen, mouse, hotkeys, compact minimap, lists, and View World without regression.

The focused Thor validation passed: the expanded radar renders correctly; hero and town marker selection focuses and centers the matching upper-map object; empty taps and drags move the viewport without moving a hero; Heroes and Towns return to the overview; and Back restores the normal deck with its compact minimap and controls working.

### Fog-aware relationship markers

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-31.

- The Expanded Adventure Map now shows owned markers in blue, allied markers in green, enemy markers in red, and neutral markers in gray. Circles remain heroes and squares remain settlements.
- Native code enumerates the world-owned hero and castle collections, classifies each marker against the current player, checks the engine fog state at the object anchor, and publishes only the revisioned visible snapshot. Android does not infer ownership, alliances, availability, or fog and receives no name or detail fields for non-owned markers.
- Owned markers remain selectable through the validated live-kingdom focus paths. Non-owned markers are explicitly non-selectable at both the snapshot and native request-queue boundaries; tapping one retains viewport navigation without changing focus, creating a route, opening a dialog, or moving a hero.
- Non-owned markers are withdrawn as soon as their anchor returns to fog or their live object state changes. The snapshot comparison includes relationship and selectability so ownership or alliance changes also produce a new revision without Android retaining hidden state.
- Heroes/Towns lists remain owned-only. Existing overlap offsets, focused halo, map tap/drag navigation, stale-revision rejection, multitouch cancellation, list restoration, Back behavior, and the compact minimaps remain intact.
- Android build and lint passed through the required short `R:` mapping. The candidate installed successfully over wireless ADB and was explicitly cold-launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the resumed activity and companion window on display 4. Validated source commit: `5967dcb76241b66d3fca3e0e63b528ad4c7f3e98`. Debug APK SHA-256: `368BC26928C542AE43C622ED4D4982F22680E9D719CE750E20A7E81A707FE0F1`.

#### Focused fog-aware marker validation

1. Confirm owned, allied, enemy, and neutral relationship colors plus hero-circle and settlement-square shapes on the Expanded Adventure Map.
2. Reveal enemy objects and return them to fog; confirm their markers appear only while visible and disappear without stale information.
3. Tap every relationship category; confirm owned focus selection and safe non-owned viewport navigation without focus, route, dialog, or movement side effects.
4. Exercise overlapping and nearby objects, dragging across markers, rapid taps, and multitouch; confirm stable offsets and deterministic safe behavior.
5. Recheck Heroes/Towns restoration, Back, compact minimap, View World, physical controls, and upper-screen controls.

All five focused checks passed on the Thor, including relationship colors and shapes, fog-driven appearance and removal, owned selection, informational non-owned navigation, overlap and input safety, list and Back restoration, and compact-minimap, View World, physical-control, and upper-screen regressions.

### Fog-safe marker information card

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-31.

- A stationary long-press on any visible Expanded Adventure Map hero or settlement marker requests a read-only information card. The gesture is cancelled by drag, multitouch, cancellation, stale revisions, or context changes; it does not focus, center, route, move, or open an upper-screen dialog.
- Ordinary input remains unchanged: owned marker taps focus and center, non-owned marker taps retain safe viewport navigation, drags pan the viewport, and an empty tap recenters while clearing the card.
- Native code re-resolves the requested object on the SDL thread and owns every published field. Hero detail follows the existing quick-info rules: owned, allied, and neutral heroes plus Identify Hero or Crystal Ball access receive full level, class, primary-stat, movement, mana, morale, luck, and exact-army information; ordinary visible enemies expose identity and engine-formatted army estimates only.
- Settlement detail includes name, Town or Castle type, faction, relationship, and player color. Friendly or Crystal Ball views receive exact defenders; hostile views follow the existing Thieves' Guild tiers of Unknown, identified creature types with hidden counts, and engine-formatted quantity estimates.
- The inspected relationship is bound to the accepted marker revision. Fog return, removal, ownership or alliance changes, turn/player/context changes, or loss of the relevant visibility privilege withdraw or replace the complete revisioned card; Android does not infer, merge, or retain hidden fields.
- The overview side panel keeps Heroes, Towns, and Back as compact stacked commands beneath the text-only card. Labels, clustering, zoom, sprites, configurable filters, and army-management interactions remain deferred.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully over wireless ADB and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the resumed activity and companion presentation on display 4. Debug APK SHA-256: `CAABD274EADA78CBA13C3A47223F39E6B53F945F8736C86CCDA5A5EBA08021E7`.

#### Focused fog-safe marker information validation

1. Long-press owned, allied, enemy, and neutral heroes. Verify the correct full or limited detail tier and confirm no focus, route, movement, viewport, or dialog side effects.
2. Compare settlements with zero, one, and two-or-more Thieves' Guilds, plus allied and Crystal Ball cases. Confirm Unknown, hidden counts, estimates, and exact counts match native quick info.
3. Reveal and inspect an enemy marker, then return it to fog. Confirm both marker and card disappear promptly without stale text after turn, ownership, alliance, or hot-seat player changes.
4. Exercise short taps, long-presses, dragging across markers, overlaps, rapid input, cancellation, and multitouch. Confirm owned selection and non-owned viewport behavior remain unchanged.
5. Recheck Heroes/Towns restoration, Back, compact minimap, View World, dialogs, physical controls, upper touchscreen, mouse, and hotkeys.

All five focused checks passed on the Thor, including full and limited hero detail tiers, settlement information tiers, fog and state invalidation without stale details, gesture and overlap safety, preserved owned selection and non-owned viewport behavior, exact overview/list restoration, and compact-minimap, View World, dialog, physical-control, touchscreen, mouse, and hotkey regressions.

### Expanded Adventure Map presentation filters

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-31.

- The overview side column adds two compact local controls above Heroes, Towns, and Back. Kind cycles through Both, Heroes, and Towns; relationship cycles through All, Owned, Allied, Enemy, and Neutral.
- Filters default to Both and All, remain in memory when the overview is closed and reopened during the current presentation lifetime, and are not persisted to disk.
- Native code continues publishing the complete fog-safe visible snapshot. Android alone applies the filters to marker rendering, hit testing, and same-tile overlap offsets; hidden entries never intercept map gestures or marker-information requests.
- Changing either filter cancels pending input and clears the current information card locally and through the revisioned native request. Empty-map viewport navigation, owned focus selection, safe non-owned navigation, deterministic visible overlap behavior, and all native privacy and invalidation rules remain unchanged.
- The compact filter rows preserve the existing information-card region and large Heroes, Towns, and Back touch targets. Labels, clustering, zoom, sprites, and army-management interactions remain deferred.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully over wireless ADB and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the resumed activity and companion presentation on display 4. Debug APK SHA-256: `71CC57A50CC87229FC0A86EEAA39AC2CB9EC06A8127EA3E86FCEFE4B04D36D38`.

#### Focused presentation-filter validation

1. Cycle both filters and confirm only the requested kinds and relationships appear; restore Both / All and confirm the complete visible set returns.
2. Hide each marker category in turn, then tap and long-press its former position. Confirm hidden markers do not intercept viewport gestures or publish information.
3. Inspect a marker and then filter it out. Confirm its card clears immediately and does not reappear when the filter is restored without a new long-press.
4. Test overlapping hero and settlement markers under kind and relationship filters. Confirm visible offsets and nearest-marker selection remain stable and deterministic.
5. Recheck owned focus, non-owned navigation, dragging, empty-map recentering, rapid and multitouch input, Heroes/Towns restoration, Back, compact minimap, View World, upper-screen controls, physical controls, mouse, and hotkeys.

All five focused checks passed on the Thor, including kind and relationship filtering, complete-set restoration, hidden-marker input exclusion, immediate information-card clearing, deterministic visible overlap behavior, state retention across overview restoration, and owned/non-owned navigation plus all requested control regressions.

### Expanded Adventure Map presentation zoom

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-08-31.

- The overview provides bounded 1x, 2x, and 4x presentation-only zoom. A small map-corner badge reports the current level; the native radar and fog-safe marker and information snapshots remain unchanged.
- A two-finger pinch crosses deliberate thresholds between levels while anchoring the world point beneath the gesture midpoint. Two-finger translation pans the zoom window, clamped to the map boundaries.
- Starting a two-finger gesture cancels pending button, tap, drag, and long-press input. Lifting either finger ends the complete gesture; third-finger input, cancellation, stale context, and context changes cannot fall through into selection, information, viewport, route, movement, or dialog actions.
- Existing one-finger behavior is retained through the zoom transform: owned marker taps focus, non-owned and empty taps navigate safely, drags move the upper viewport, and stationary long-presses request authorized information.
- One shared transform covers radar pixels, the viewport outline, visible marker rendering, filtered overlap positions, hit testing, long-press targeting, empty-map navigation, and drag coordinates. Marker size remains readable and constant on screen.
- Zoom level and center remain in memory through overview closure and Heroes/Towns restoration during the presentation lifetime. A radar snapshot with different world dimensions resets zoom safely to the complete 1x map. Zoom changes do not clear an already authorized information card.
- Validated kind and relationship filters, hidden-marker input exclusion, native privacy and invalidation, compact minimaps, View World, and all established controls remain unchanged. Labels, clustering, sprites, army management, haptics, and configurable layouts remain deferred.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `BABA7499AAD2BAB8805131FD59C0040B4385AC9434F92DCBB2B913280DBCD663`. The APK installed successfully over wireless ADB and was explicitly cold-launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the resumed activity and the presentation window on lower display 4.

#### Focused presentation-zoom validation

1. Exercise 1x, 2x, and 4x on small, medium, and maximum maps; confirm correct terrain crops, zoom badge, midpoint anchoring, boundary clamping, and stable threshold changes.
2. At every level, tap and drag empty terrain; confirm exact transformed upper-viewport navigation without routes, hero movement, or dialogs.
3. Tap owned markers and long-press every relationship at every level, including overlaps; confirm transformed targeting, focus, information tiers, viewport outline, and deterministic offsets.
4. Exercise kind and relationship filters while zoomed; confirm hidden markers do not render, affect overlap, intercept gestures, or publish information.
5. Exercise rapid pinches, early finger lift, a third finger, cancellation, context changes, and gestures starting over markers; confirm no fallthrough or stuck input.
6. Restore from Heroes/Towns and reopen the overview to confirm in-memory zoom restoration; change map dimensions to confirm a safe 1x reset.
7. Recheck Back, compact minimap, View World, dialogs, movement gating, physical controls, upper touchscreen, mouse, and hotkeys.

All seven focused checks passed on the Thor, including bounded zoom levels and badges, anchored pinch and clamped two-finger panning, transformed viewport navigation, marker focus and authorized information at every level, filtered and overlapping marker behavior, safe rapid and multi-finger cancellation, exact overview and list restoration, map-size reset, and all requested compact-minimap, View World, dialog, movement, physical-control, touchscreen, mouse, and hotkey regressions.

### Expanded Adventure Map presentation clustering

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Android groups only the already-authorized, filtered, on-crop marker snapshot. Deterministic screen-distance components use 64-pixel membership at 1x and 48-pixel membership at 2x; 4x retains individual markers.
- Visible hero and town markers sharing one tile remain individually offset and are excluded from clustering. Hidden, filtered, fogged, and off-crop markers do not affect membership or counts.
- A cluster badge shows its visible count, fixed-order proportional Owned / Allied / Enemy / Neutral relationship ring, hero-circle and town-square presence, and a white halo when it contains the focused marker.
- Rendering and hit testing use the same final layout. A short cluster tap advances the lower overview by one zoom level and centers it on the cluster centroid with normal clamping; it does not submit a native selection, viewport, information, route, movement, or dialog request.
- A stationary cluster long-press is consumed without publishing information. Individually resolved markers retain the validated owned focus, safe non-owned navigation, and native-authorized long-press information behavior.
- Marker-snapshot, filter, zoom-gesture, context, cancellation, and multitouch changes cancel pending cluster input. Existing zoom and filter restoration, information-card validity, Heroes/Towns restoration, Back, compact minimaps, View World, and established controls remain unchanged.
- Labels, sprites, army-management interactions, haptics, and configurable layouts remain deferred separately.
- Android build and lint pass through the required short `R:` mapping. Final debug APK SHA-256: `64F3707B04B2439A2AC9E873811BDD311B7B89514428EEA688E65EFFB1331242`. The APK installed successfully over wireless ADB and was explicitly cold-launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks confirmed the activity and the companion package window on lower display 4 with no matching fatal log entry.

#### Focused presentation-clustering validation

1. On dense small, medium, and maximum maps, verify stable clusters and counts at 1x and 2x, individual markers at 4x, and preserved same-tile hero/town offsets.
2. Verify uniform and mixed relationship rings, hero/town indicators, focused-cluster halo, deterministic membership, and exact overview restoration.
3. Cycle every kind and relationship filter; verify immediate count and composition changes with no contribution or input interception from hidden or off-crop markers.
4. Tap clusters at 1x and 2x; verify clamped lower-only drill-down without upper viewport, focus, route, movement, dialog, or information-card side effects.
5. Long-press clusters and individual markers; verify clusters reveal nothing while individual markers retain the validated privacy tiers and live invalidation.
6. Recheck pinch/pan, marker selection, empty-map navigation, dragging, rapid taps, multitouch cancellation, Heroes/Towns restoration, Back, compact minimap, View World, and established upper and physical controls.

All six focused checks passed on the Thor, including deterministic density reduction and counts at 1x and 2x, the individual 4x transition, preserved same-tile offsets, relationship and kind presentation, focused-cluster highlighting, filter-first membership, lower-only drill-down, cluster long-press suppression, individual privacy-aware information, exact restoration, and all requested navigation, gesture, list, minimap, View World, upper-screen, and physical-control regressions.

### Expanded Adventure Map owned-marker labels

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Individually resolved owned heroes and towns show their authorized snapshot names at 2x and 4x. Clusters, 1x markers, allied, enemy, neutral, and empty-name markers remain unlabeled.
- Labels are presentation-only dark capsules with stable screen-space sizing. Text truncates with an ellipsis at a 160-pixel maximum width at 2x and 220 pixels at 4x.
- Placement tries above, below, right, and left in that fixed order. Candidates crossing the map edge or colliding with a visible marker, cluster, accepted label, or zoom badge are rejected; a label with no safe position is omitted.
- The focused owned marker lays out first, followed by the existing stable snapshot order. Zoom, pan, filter, focus, snapshot, and cluster-layout changes recompute labels without storing separate state.
- Labels do not participate in hit testing or expand marker targets. Existing individual-marker taps and long-presses, cluster drill-down and suppression, privacy and invalidation, same-tile offsets, zoom restoration, lists, Back, and established controls remain unchanged.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `E102F2886207192CCAB7892D93C8B4C56A41FB5FDA943838C151DEF8B3AFF390`. The APK installed successfully and cold-launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks found the resumed and drawn main activity, the companion package window on display 4, and no matching fatal log entry.

#### Focused owned-label validation

1. At 1x, verify there are no labels. At 2x and 4x, verify only individual owned heroes and towns receive names.
2. Check long names and markers near all four edges; verify clean ellipsis, in-map placement, and no overlap with visible markers, clusters, or the zoom badge.
3. Use a dense owned area and same-tile hero/town pairs; verify stable placement, focused-label priority, and clean omission when no position is available.
4. Pan, pinch, drill into clusters, and cycle both filters; verify labels appear, disappear, and reposition immediately without stale names.
5. Tap and long-press through and beside labels; verify unchanged marker targeting and privacy-aware information, with no label touch targets.
6. Verify exact Heroes/Towns and Back restoration, compact minimap, View World, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including 1x suppression, owned-only individual labels at 2x and 4x, clean truncation and edge handling, stable collision avoidance and focused-label priority, same-tile behavior, immediate zoom/filter/pan/cluster transitions, input transparency, information privacy, exact restoration, and all requested View World, touchscreen, mouse, hotkey, and physical-control regressions.

### Expanded Adventure Map context-aware haptics

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Accepted lower-touch interactions use one subtle Android `CLOCK_TICK`: an owned-marker selection request accepted against the current native revision, a cluster drill-down that advances one level, each completed 1x/2x/4x pinch transition, and each kind or relationship filter change.
- Panning, two-finger translation within one zoom level, empty-map and non-owned navigation, long-press information, maximum-zoom clusters, bounded zoom attempts, rejected or cancelled gestures, stale revisions, multitouch rejection, physical controls, upper touchscreen, mouse, and hotkeys remain silent.
- Cluster drill-down owns its single confirmation rather than also emitting a zoom confirmation. Zoom bounds are checked before feedback, and every filter command emits exactly one confirmation despite rebuilding multiple presentation elements.
- Feedback uses the lower presentation view's standard `performHapticFeedback` path without flags that override Android settings, so the system touch-feedback preference remains authoritative. No vibrator permission, JNI change, native state, or persistent option is introduced.
- Owned-label placement and input transparency, clustering, zoom anchoring and restoration, filters, information-card privacy and invalidation, owned focus, safe non-owned navigation, lists, Back, compact minimaps, View World, and established controls remain unchanged.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `C42BCD784DC45F7626ECC9EBD49D636D249DFBE77D401156794244205C9322FF`. The APK installed successfully over wireless ADB and cold-launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks found the resumed and focused main activity, the companion package window on lower display 4, and no matching fatal startup log entry.

#### Focused context-aware haptic validation

1. Tap owned heroes and towns; verify one subtle tick and correct upper focus. Tap non-owned markers and empty map; verify safe navigation without haptics.
2. Tap clusters at 1x and 2x; verify exactly one tick for each successful drill-down, and no tick for cluster long-press or a non-actionable maximum-zoom case.
3. Pinch across the 1x, 2x, and 4x thresholds; verify one tick per completed level transition and silence while translating within a level or pushing past a bound.
4. Cycle both filter rows; verify one tick per displayed change with correct card clearing, clustering, filtering, and labels.
5. Exercise drag cancellation, rapid snapshot/context changes, extra-finger cancellation, long-press information, Heroes/Towns restoration, and Back; verify rejected and non-target interactions remain silent.
6. Disable Android touch feedback and verify all target actions become silent; re-enable it and verify feedback returns. Briefly recheck labels, compact minimap, View World, upper touch, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including one confirmation for accepted owned-marker focus, cluster drill-down, completed zoom-level transitions, and filter changes; silence for navigation, panning, bounded and rejected gestures, long-press information, and non-lower-touch controls; system-setting compliance; duplicate suppression; and the requested label, clustering, information, list, minimap, View World, upper-input, mouse, hotkey, and physical-control regressions.

### Expanded Adventure Map allied-marker labels

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Individually resolved allied heroes and towns now receive their native-authorized snapshot names and show them at 2x and 4x. Clusters, 1x markers, enemies, neutrals, and empty-name markers remain unlabeled.
- Native code remains the sole authorization source. It publishes an allied name only after confirming that the object is currently visible and allied. Fog return, removal, ownership or alliance changes, turn/player/context changes, and snapshot replacement withdraw the name through the existing revisioned snapshot path without Android deriving or separately retaining it.
- Allied labels reuse the validated dark capsule, typography, bounded screen-space sizing, ellipsis, fixed placement order, edge and collision rejection, and input transparency. The green marker remains the relationship cue.
- Label placement is deterministic: the focused owned marker lays out first, remaining owned labels follow in stable snapshot order, and allied labels lay out last in stable snapshot order. Contested allied labels yield to owned labels.
- Labels remain outside hit testing and do not change clustering, filters, information tiers, focus, safe non-owned navigation, accepted-action haptics, restoration, or established controls.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `553505CC70FBF2935786E46A472F078A61DE03AD826193B34058F11FEC7FB863`. The APK installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks found the resumed and focused main activity, the companion package window on lower display 4, the live game process, and no matching fatal log entry.

#### Focused allied-label validation

1. At 1x, verify there are no labels. At 2x and 4x, verify only individual owned and allied heroes and towns receive names; clusters, enemies, and neutrals remain unlabeled.
2. Reveal allied objects, return them to fog, and exercise ownership, alliance, and hot-seat player changes. Verify allied names appear and disappear immediately without stale text.
3. Use dense areas, long names, edge markers, and same-tile objects. Verify clean truncation and collision avoidance, with focused-owned priority, remaining-owned priority, and allied labels yielding last.
4. Pan, pinch, drill into clusters, and cycle both filters. Verify labels appear, disappear, and reposition deterministically without stale names.
5. Tap and long-press through and beside allied labels. Verify unchanged safe navigation and native-authorized information, no label touch targets, and no unintended haptics.
6. Verify Heroes/Towns restoration, Back, compact minimap, View World, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including allied-only authorization at 2x and 4x, 1x and cluster suppression, live fog and relationship withdrawal, deterministic owned-first collision handling, truncation and edge behavior, zoom/pan/cluster/filter transitions, label input transparency, native-authorized information, unchanged haptics and safe navigation, exact restoration, and the requested minimap, View World, upper-input, mouse, hotkey, and physical-control regressions.

### Expanded Adventure Map enemy-marker labels

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Individually resolved enemy heroes and towns receive their native-authorized snapshot names and show them at 2x and 4x. Clusters, 1x markers, neutral markers, and empty-name markers remain unlabeled.
- Native code remains the sole authorization source. It publishes an enemy name only while the object is currently visible to the active player. Fog return, removal, capture, ownership or alliance changes, turn/player/context changes, and snapshot replacement withdraw the name through the existing revisioned snapshot path without Android deriving or separately retaining it.
- Enemy labels reuse the validated dark capsule, typography, bounded screen-space sizing, ellipsis, fixed placement order, edge and collision rejection, and input transparency. The red marker remains the relationship cue.
- Label placement remains deterministic: the focused owned marker lays out first, remaining owned labels follow in stable snapshot order, allied labels follow, and enemy labels lay out last. Contested enemy labels yield to owned and allied labels.
- Labels remain outside hit testing and do not change clustering, filters, information tiers, focus, safe non-owned navigation, accepted-action haptics, restoration, or established controls. Neutral labels remain deferred separately.
- Validated source commit: `490282958f641b685df5df322be6d631b7e1c34c`.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `007414B348B7DC59D59F167D308BAF1B998C48FBE27650DD68E93CEE5B857B94`. The APK installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks found the resumed main activity, the companion presentation window on lower display 4, the live SDL process, and no matching fatal launch log entry.

#### Focused enemy-label validation

1. At 1x, verify there are no labels. At 2x and 4x, verify only individual owned, allied, and enemy heroes and towns receive names; clusters and neutral markers remain unlabeled.
2. Reveal enemy objects and return them to fog. Verify enemy names appear and disappear immediately without stale text.
3. Exercise capture, ownership, alliance, turn, and hot-seat player changes. Verify labels and relationship presentation update immediately through native authorization.
4. Use dense areas, long names, edge markers, and same-tile objects. Verify clean truncation and collision avoidance, with focused-owned, remaining-owned, allied, then enemy placement priority.
5. Tap and long-press through and beside enemy labels. Verify unchanged safe navigation and native-authorized information, no label touch targets, and no unintended haptics.
6. Verify filters, zoom, clustering, Heroes/Towns restoration, Back, compact minimap, View World, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including enemy-only authorization at 2x and 4x, 1x, cluster, and neutral suppression, live fog and relationship withdrawal, deterministic focused-owned / owned / allied / enemy collision priority, truncation and edge behavior, zoom/pan/cluster/filter transitions, label input transparency, native-authorized information, unchanged haptics and safe navigation, exact restoration, and the requested minimap, View World, upper-input, mouse, hotkey, and physical-control regressions.

### Expanded Adventure Map neutral-marker labels

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Individually resolved neutral heroes and towns receive their native-authorized snapshot names and show them at 2x and 4x. Clusters, 1x markers, and empty-name markers remain unlabeled.
- Native code remains the sole authorization source. It publishes a neutral name only while the object is valid, currently visible to the active player, and still neutral. Fog return, removal, recruitment or capture, ownership changes, turn/player/context changes, and snapshot replacement withdraw or reclassify the name through the existing revisioned snapshot path without Android deriving or separately retaining it.
- Neutral labels reuse the validated dark capsule, typography, bounded screen-space sizing, ellipsis, fixed placement order, edge and collision rejection, and input transparency. The gray marker remains the relationship cue.
- Label placement remains deterministic: the focused owned marker lays out first, remaining owned labels follow in stable snapshot order, allied labels follow, enemy labels follow, and neutral labels lay out last. Contested neutral labels yield to every player relationship.
- Labels remain outside hit testing and do not change clustering, filters, information tiers, focus, safe non-owned navigation, accepted-action haptics, restoration, or established controls. Sprites, army-management interactions, configurable haptics, and configurable layouts remain deferred separately.
- Validated source commit: `457e4fe1066568820ed97da9bfd3c93bc1f341cb`.
- Android build and lint pass through the required short `R:` mapping. Candidate debug APK SHA-256: `49D1C4EFB5BE1F4C4674B4E9CF5D687E6292ABA126E807BB9A3431B6751EA3EA`. The APK installed successfully over wireless ADB and cold-launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief state checks found the resumed and focused main activity, the companion presentation window on display 4, the live game process, and no matching fatal launch log entry.

#### Focused neutral-label validation

1. At 1x, verify there are no labels. At 2x and 4x, verify visible individual owned, allied, enemy, and neutral heroes and towns receive names; clusters remain unlabeled.
2. Reveal neutral objects, return them to fog, recruit a neutral hero, and capture a neutral town. Verify names disappear or reclassify immediately without stale text.
3. Use dense areas, long names, edge markers, and same-tile objects. Verify clean truncation and collision avoidance, with focused-owned, remaining-owned, allied, enemy, then neutral placement priority.
4. Pan, pinch, drill into clusters, and cycle both filters. Verify labels appear, disappear, and reposition deterministically without stale names.
5. Tap and long-press through and beside neutral labels. Verify unchanged safe navigation and native-authorized information, no label touch targets, and no unintended haptics.
6. Verify Heroes/Towns restoration, Back, compact minimap, View World, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including neutral-only eligibility at 2x and 4x, 1x and cluster suppression, live fog withdrawal, recruitment and capture reclassification, deterministic focused-owned / owned / allied / enemy / neutral collision priority, truncation and edge behavior, zoom/pan/cluster/filter transitions, label input transparency, native-authorized information, unchanged haptics and safe navigation, exact restoration, and the requested minimap, View World, upper-input, mouse, hotkey, and physical-control regressions.

### In-game Adventure and File Options

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-03.

- Validated source commit: `271d2a0fa3c9f55d69b20b5caf01169536478b72`.

- Adventure Options replaces the generic Dialog deck with View World, Puzzle, Scenario Information, Dig, and Cancel while the matching upper menu is visible. Dig follows the native focused-hero availability.
- File Options replaces the generic Dialog deck with New Game, Load Game, Restart Game, Save Game, Quick Save, Quit, and Cancel. Restart remains present but muted because the matching native upper button is disabled.
- Lower-screen actions are consumed by the existing native menu loops on the SDL thread. Confirmations, help, warnings, and save/load selectors temporarily use Dialog context; cancelled nested operations restore the exact parent menu once.
- Context changes clear queued input, and an accepted semantic choice disables follow-up actions until the operation completes. Existing upper touchscreen, mouse, hotkeys, physical controls, Adventure actions, and editor option menus remain unchanged.
- Android assemble and lint pass through the required short `R:` mapping. The first hardware check exposed that Android's defensive context-range guard still ended at the prior Hero Meeting context, so it converted both new native context identifiers to the inert Upper-Screen Control fallback. The corrected guard accepts the two appended identifiers while retaining fallback for every larger invalid value. The corrected candidate installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; the focused hardware checks passed with the final validated gameplay-dialog build recorded below.

#### Focused in-game options validation

1. Open Adventure Options and File Options and verify the lower actions, ordering, availability, and visible upper menus agree.
2. Verify Dig enables with a focused hero and mutes without one; verify Restart Game remains visible and disabled.
3. Exercise View World, Puzzle, Scenario Information, Save Game, Quick Save, and Cancel. Verify each action occurs once and Adventure restores correctly.
4. Cancel New Game, Load Game, and Quit confirmations. Verify Dialog appears for the nested prompt and restores File Options exactly once.
5. Exercise rapid mixed taps, right-click help, upper touchscreen, mouse, hotkeys, and physical controls. Recheck established Adventure Map and Editor option-menu behavior for regressions.

### Gameplay dialog anti-stuck audit

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-03.

- Generic frame-based dialogs publish a modal context for their complete visible lifetime instead of relying on every caller to wrap them correctly. The shared fallback now exposes only Okay and Cancel; standard messages and the audited custom choices use more precise appended contexts.
- Custom full-screen or standard-window dialogs that do not use the shared frame explicitly publish Dialog. The audited paths include Scenario Information, recruitment, army information, Thieves' Guild, and save/load file selection.
- Standard prompts expose the exact Close, Okay, Yes/No, or Okay/Cancel actions represented by their native button mask. Scenario Information and the battle result use a single Close action.
- Load and Save browsers expose Previous Save, Next Save, Load or Save, and Cancel without irrelevant horizontal controls. Treasure chests expose Keep Gold and Take Experience instead of generic Yes/No labels.
- Two-choice level-up dialogs publish both localized native skill names and expose Learn Skill 1, Learn Skill 2, and View Hero. The names are republished after nested Hero or skill-information windows. Arena training exposes Previous Skill, Next Skill, and Learn Selected.
- A dialog context remains active until its upper window has restored. Nested dialogs restore their exact Dialog parent first, and the outer guard then restores Adventure, Adventure Options, File Options, Hero, Castle, Battle, or the originating menu. Context transitions release pressed controls so rapid taps cannot leak into the restored screen.
- Dedicated semantic contexts remain unchanged. Quick information that exists only while an upper right-button press is held keeps its existing press-and-release behavior.
- The first installed candidate used one directional deck for every gameplay dialog. Hardware feedback confirmed that it prevented getting stuck, but exposed irrelevant Left/Right controls in file lists and meaningless directions in Scenario Information and treasure-chest choices; the battle-result screen still inherited Battle controls, and level-up directions did not identify their skills. The refined candidate replaces those controls with the semantic layouts above. Android assemble and lint pass through the required short `R:` mapping. Debug APK SHA-256: `21E5A1E503C8CC25F76BF933314AA216F432A2A7E68FF2EB630017D7B6980E13`. It installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; the activity is resumed and the presentation window is attached to display 4 without an Android runtime failure. Focused manual validation is pending.
- Hardware validation passed Scenario Information, battle results, treasure-chest rewards, file navigation, standard prompts, and restoration. The level-up `View Hero` check exposed that the nested `Heroes::OpenDialog()` relies on its caller to publish Hero context: it wrote Hero information into the still-active Level Up layout, producing corrupted dynamic labels. The corrected level-up caller scopes the nested screen as Hero, then restores Level Up and republishes both skill choices after Hero closes. Build and lint pass, and the corrected APK installed and launched successfully. Corrected debug APK SHA-256: `E30D03A573FAA22513364746C5C26787F091C9C04D8D9823250D34FA3DF63C3B`. The focused View Hero retest is pending.
- The corrected Hero transition worked, but a live lower-screen capture showed `Learn Advanced Leadership` clipped in the narrow three-column level-up layout. The approved visual refinement stacks the two complete localized Learn choices and View Hero as three full-width rows with shared text fitting and safe horizontal insets. Other dialog layouts remain unchanged. Build and lint pass; the candidate installed and launched successfully. Debug APK SHA-256: `FFA118B06F9512F50D1913565CFF7C5FBEF3DA5995ABE08E2458CDB21A540D21`. Focused visual validation is pending.
- The stacked Level Up layout passed its focused hardware retest. A subsequent live battle-result capture showed the single Close control occupying an oversized left-hand half-column with unused space on the right. The approved correction gives battle results a dedicated context and title with one centered, wide horizontal Close control; other one-action dialogs and all established behavior remain unchanged. Build and lint pass, and the candidate installed and launched successfully. Debug APK SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`. The focused battle-result validation passed.

#### Focused gameplay-dialog validation

1. Open Scenario Information and a battle victory/defeat summary. Verify each shows only Close, closes from the lower screen, and restores Adventure or the post-battle flow once.
2. Collect two land treasure chests. Choose Keep Gold once and Take Experience once; verify the named lower action grants the matching reward and restores Adventure once.
3. Reach a two-choice level-up. Verify both localized skill names match the upper choices, each Learn action grants the named skill, View Hero opens the Hero screen, and closing Hero restores the same two named choices.
4. Open Load and Save browsers. Verify only Previous Save, Next Save, Load/Save, and Cancel appear; navigate vertically, complete or cancel, and verify the exact parent returns.
5. Exercise representative Okay, Yes/No, Okay/Cancel, Arena, recruitment/count, marketplace, Hero, Castle, and Battle dialogs. Open nested help where available, use rapid mixed taps, toggle the lower panel, and suspend/resume; verify every modal remains completable, restoration is exact, and no stale action fires.

The focused hardware validation passed. Adventure Options and File Options expose their complete native choices; Scenario Information and battle results close from the lower display; treasure-chest rewards, standard prompts, and vertical save/load navigation match their labels; and dialogs restore their exact parent without trapping the player. Two-choice Level Up shows complete localized skill names in three full-width rows, enters the normal Hero deck for View Hero, restores the same choices, and selects either skill correctly. The centered Battle Result Close control passed visually and advances the post-battle flow once. Existing upper touchscreen, mouse, hotkey, physical-controller, Adventure, Battle, and Editor paths remained unchanged. Final validated debug APK SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.

### Later menu slices

- Battle Only setup is hardware-validated; retain its controls, information card, modal restoration, and Battle transition without regression.
- The Succession Wars and Price of Loyalty selectors are hardware-validated. Retain their selection behavior, missing-video handling, hover-driven animations, Back semantics, and physical-input compatibility without regression.
- High Scores is hardware-validated; retain its Standard/Campaign synchronization, availability rules, Dialog restoration, and direct Exit without regression.
- Game Settings is hardware-validated; retain its enabled states, live summary, nested-dialog restoration, persistence, and existing input paths without regression.
- In-map Editor File Options, System Options, Map Specifications, Editor Tools, and live map information are hardware-validated.
- The safe unknown-menu fallback is hardware-validated; retain its inert transition/invalid state, explicitly classified generic menu navigation, input cancellation, and established controls without regression.

### Safe unknown-menu fallback

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-01.

- Invalid context identifiers, an unavailable native bridge, and explicit transition states use an inert Upper-Screen Control deck. They expose no guessed Confirm, Cancel, navigation, semantic, information, or viewport actions.
- A separate appended Menu Fallback context preserves every existing context identifier and is published only for an explicitly menu-classified unsupported state or a newly added main-menu dispatcher state.
- Menu Fallback exposes key-based Up, Down, Left, Right, Confirm, and Back navigation without inventing semantic actions. Context changes release pressed keys and clear all native semantic, viewport, selection, marker-information, and information state through the established context transition path.
- Existing known dispatcher transitions are classified explicitly so they cannot briefly inherit the interactive menu fallback while their dedicated screen is opening.
- Dialog remains reserved for real modal choices. Upper touchscreen, mouse, hotkeys, physical controls, and every validated dedicated deck remain unchanged.
- Validated source commit: `4260100d658f6354a06fc4dcb0c48b92db40d140`.
- Android build and lint passed through the required short `R:` mapping. The candidate installed successfully over wireless ADB and was explicitly launched as `org.fheroes2.thor/org.fheroes2.GameActivity`; a brief state check confirmed the resumed activity and command deck on display 4. Debug APK SHA-256: `320D2B73CFCD73A14DC2783959A4EC82B03DD3AE03E0BDFBAE12771068BA3DFD`.

#### Focused safe-fallback validation

1. Open Credits and verify the lower display shows Menu Navigation rather than Dialog or a stale parent deck; verify a navigation key exits through the existing upper-screen behavior without duplicate input.
2. Exercise an inert transition into gameplay or the Editor and verify Upper-Screen Control exposes no touch actions while the destination deck is unavailable.
3. Verify an invalid or unavailable native context cannot expose Confirm, Cancel, navigation, stale information, radar, selection, or semantic controls.
4. Rapidly tap during fallback-to-known-context transitions and verify no fallback input fires in the restored context and no button remains pressed.
5. Toggle the lower panel or suspend and resume while fallback is visible; verify exact recovery without a stuck press or stale deck.
6. Recheck Main Menu, one nested menu, Dialog, Adventure Map, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor, including Credits menu fallback and single-action exit, inert gameplay and Editor transitions, invalid/unavailable-state suppression, rapid-transition input cancellation, panel and lifecycle recovery, exact dedicated-deck restoration, and the requested menu, Dialog, Adventure Map, upper-input, mouse, hotkey, and physical-control regressions.

### Acceptance criteria

- The lower deck always matches the visible upper-screen menu and exposes only valid choices.
- Nested choices and Back restore the correct parent without stacked menus or duplicate actions.
- Missing campaign assets and unavailable load categories are reflected in native enabled-action masks.
- Dialog remains reserved for modal Confirm and Cancel. Menu choices use dedicated menu states.
- Build/lint pass through the short `R:` path, followed by focused manual testing on the Thor.

## Validation workflow

- Automate compile, Android build, lint, identifier parity, static checks, APK hashing, installation, explicit launch, and concise diagnostic queries where useful.
- Give the user a short, focused hardware checklist and wait for their manual results before recording validation or publishing a checkpoint.
- Avoid extended ADB-driven UI navigation, repeated screenshots, and exhaustive automated device interaction unless the user explicitly asks for it or a reported failure needs targeted diagnosis. This keeps validation efficient and avoids excessive token use.
- Preserve previously validated behavior through focused regression items rather than replaying every earlier workflow automatically.

## Next recommended planning point

- Precise Hero Meeting stack splitting is the approved next focused slice. Artifact transfers, broader player-installed sprite use, configurable haptic choices, configurable layouts, and multi-slot redistribution remain deferred until their exact behavior and focused tests are approved.

### Hero Meeting precise stack splitting

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-04.

- Validated source commit: `b09695653a83e48c2779e0002f076710e14f68f4`.

- Ordinary lower-screen taps and drags retain their validated full-stack move, merge, and swap behavior. Long-pressing an occupied stack of at least two creatures enters a lower-only Split mode without opening the upper native quantity dialog.
- Split mode highlights valid empty or matching-creature destinations across either army. After choosing a destination, the lower action area exposes exact amount adjustment by one and ten, One, Half, Max, Move, and Cancel. Half is the default and Max means all but one creature, leaving full-stack transfer to the established tap and drag paths.
- The lower preview identifies the source and destination and shows the resulting counts before confirmation. The already-published upper-screen army selection receives a distinct KEEP badge, while the lower direct-manipulation source retains its own MOVE or SPLIT presentation.
- Android owns presentation and transient composition only. Confirmation sends one revision-bound source, destination, and amount request; the existing Hero Meeting loop revalidates context, revision, indices, creature compatibility, amount, and live army state on the SDL thread before applying it.
- A changed snapshot or context, upper army interaction, whole-army action, multitouch, cancellation, panel recreation, nested Hero screen, or lifecycle transition cancels Split mode. Rejected and cancelled requests remain silent, and established overview haptics remain unchanged.
- Multi-slot redistribution, quantity text entry, artifact management, and configuration remain separate deferred slices.
- Android assemble and lint pass through the required short `R:` mapping. The candidate installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; the activity is resumed and the presentation window is attached to display 4 with no matching Android runtime failure. Candidate debug APK SHA-256: `B5951BA3DFFBCD01E760444D1D4EEBA40E03A279CADB6DA4948FC11077B7E823`. Focused manual validation is pending.
- All six focused functional checks passed on the Thor: exact splits into empty and matching slots worked within and across both armies; presets, adjustments, previews, counts, invalid-target rejection, cancellation, lifecycle handling, ordinary taps and drags, whole-army controls, and established input paths behaved correctly. The run exposed one presentation issue: the per-slot KEEP badge is unclear and over-specific because the native whole-army action retains a selected creature type in its source army, not necessarily the exact badged stack. A focused wording/presentation correction is pending approval before this slice is marked passed.
- The approved clarification removes the cyan per-slot KEEP badge and outline. When an upper army selection is active, the information card instead states that a whole-army move keeps the named creature type in the source army; no keep guidance is shown without an upper selection. MOVE and SPLIT remain the only lower slot badges. Build and lint pass, and the corrected candidate installed and launched successfully with its presentation attached to display 4. Corrected debug APK SHA-256: `884A70B1F60A3CFAE4F3DE217096D90C003ACBB72898B95C1AE162DA4E03C35F`.
- The two-check correction retest passed on the Thor: upper selection produced clear named whole-army guidance without a KEEP badge, the selected creature type remained in the source army, clearing the upper selection removed the guidance, and precise splitting retained its validated MOVE/SPLIT presentation. The complete six-check functional run and focused clarification retest therefore pass.

#### Focused precise-splitting validation

1. Select a stack on the upper screen and another on the lower screen. Verify KEEP and lower MOVE or SPLIT selection are visually distinct and affect only their intended operations.
2. Split stacks into empty slots within each army and across both armies using One, Half, Max, and exact adjustments. Verify the upper and lower counts remain identical.
3. Split into matching-creature stacks in both directions. Verify only the chosen amount merges and total creature counts remain exact.
4. Try single-creature sources, different-creature destinations, stale revisions, invalid amounts, and last-stack-sensitive transfers. Verify rejection without an army change, stuck selection, or unintended feedback.
5. Cancel through Cancel, the source slot, multitouch, upper interaction, panel toggling, nested Hero screens, and suspend/resume. Verify exact restoration without a queued request.
6. Recheck ordinary tap transfers, dragging, merging, swapping, whole-army controls, Close, upper touchscreen, mouse, hotkeys, physical controls, and established overview haptics.

### Hero Meeting direct troop manipulation

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-03.

- Validated source commit: `3c1a09c5e65fcfd536b952ec9f9df53ee7e5969a`.

- Drag an occupied lower-screen Hero Meeting stack to any other army slot. Cross-hero drops retain the validated native move, merge, swap, and last-stack rules. Same-hero drops reposition into an empty slot, merge matching creatures, or swap different creatures.
- A drag begins only after crossing the system touch-slop threshold. The validated tap-select/tap-destination workflow remains available and unchanged; releasing outside a slot or without crossing the threshold cannot accidentally move a stack.
- The source and current destination receive clear drag feedback using the already cached player-installed creature visual. Android submits only a revision-bound source/destination request; the existing Hero Meeting loop validates and applies it on the SDL thread.
- Multitouch, cancellation, context or snapshot changes, upper army interaction, whole-army actions, panel recreation, and lifecycle transitions cancel any pending drag. Rejected and cancelled drops remain silent; accepted operations retain the established haptic behavior.
- Partial-stack splitting, quantity dialogs, artifact management, and configuration remain separate deferred slices.
- Android assemble and lint pass through the required short `R:` mapping. The validated candidate installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; a brief state and log check confirmed the resumed activity and command deck on display 4 without a fatal exception or JNI-link error. Validated debug APK SHA-256: `D29FB481A4DB6B923BD2CDD7EE02B889CA9C749D4A0FFD4E9E54CDC5317D6F31`.
- The first hardware run passed all six interaction checks but exposed forced-square scaling of non-square creature sprites in both army slots and the drag preview. The corrected candidate preserves each native bitmap's aspect ratio while fitting and centering it within the existing bounds; the focused visual retest passed with several differently shaped stationary and dragged creatures.

#### Focused direct-manipulation validation

1. Drag stacks across rows into empty, matching, and different destinations; verify move, merge, swap, exact counts, both directions, and native last-stack retention.
2. Reposition, merge, and swap within each hero's row in both directions; verify the upper and lower army bars remain identical.
3. Use short taps and small finger movement; verify the existing lower selection workflow remains unchanged and no unintended drag begins.
4. Drop between slots and outside the army area; verify cancellation without an army change, stale selection, or haptic feedback.
5. Exercise rapid drags, cancelled touches, multitouch, mixed upper/lower interaction, panel toggling, nested Hero screens, and suspend/resume; verify stale requests are rejected and restoration is exact.
6. Recheck Army Right, Army Left, Swap Armies, Close, upper touchscreen, mouse, hotkeys, physical controls, existing Hero and Castle army actions, and established haptics.

All six focused interaction checks passed on the Thor. Cross-hero moves, merges, swaps, exact counts, and last-stack retention remained correct; same-row repositioning, merging, and swapping worked in both armies; tap selection and small-movement handling were unchanged; off-slot drops, rapid input, cancellation, multitouch, mixed upper/lower interaction, panel toggling, nested Hero screens, and suspend/resume remained safe. Whole-army actions, Close, upper touchscreen, mouse, hotkeys, physical controls, Hero and Castle army actions, and established haptics retained their validated behavior. The follow-up aspect-ratio correction also passed visual validation for stationary slots and the drag preview.

### Hero Meeting troop-slot transfer deck

Status: `passed`; behavior and six focused acceptance tests were approved and hardware-validated on 2026-09-02.

- Validated source commit: `40aa66cbb79967c15061a41c8cb0ab9425e75b49`.

- Show both heroes' five army slots on the lower screen with native creature sprite, localized name, exact count, empty destinations, and a clear lower-only source selection.
- A second tap on the other hero's row uses the existing native army-bar rules: move to empty, merge matching creatures, swap different creatures, and preserve the last creature required by a hero army.
- Same-row occupied taps change the source, the selected slot cancels it, and empty source slots do nothing. Upper army interaction, whole-army actions, context changes, panel recreation, and lifecycle restoration clear pending lower selection.
- Android owns presentation only. A versioned native snapshot supplies both armies and player-installed visuals; a revision-bound source/destination request is validated and consumed in the existing Hero Meeting loop on the SDL thread.
- Existing Army Right, Army Left, Swap Armies, Close, upper touchscreen, mouse, hotkeys, physical controls, information, restoration, and haptics remain unchanged.
- Drag-and-drop, splitting, same-army rearranging, artifacts, and configuration remain separate deferred slices.
- Android assemble and lint pass through the required short `R:` mapping. The final candidate installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief checks found the resumed process and lower-display presentation window on display 4 with no matching fatal or JNI-link error. Candidate debug APK SHA-256: `5E07D0D31DCDC5981857433531C86D5BE7B26344FD382D1034055B41984A786D`.

#### Focused troop-slot validation

1. Verify both five-slot armies, creature sprites, names, counts, empty slots, hero ownership, and selection highlight match the upper meeting screen.
2. Move a stack into an empty opposing slot and verify both screens update once; verify the native last-stack rule leaves one creature when required.
3. Merge matching creatures and swap different creatures; verify exact identities, counts, summaries, and one operation per accepted tap.
4. Change and cancel lower selection, then mix lower selection with upper army interaction and whole-army controls; verify no crossed or stale selection.
5. Exercise rapid taps, cancelled touches, multitouch, panel toggle, nested Hero screens, and suspend/resume; verify stale requests are rejected and restoration is exact.
6. Recheck Army Right, Army Left, Swap Armies, Close, upper touchscreen, mouse, hotkeys, physical controls, existing Hero and Castle army actions, and established haptics.

All six focused checks passed on the Thor. Both five-slot rows, player-installed creature sprites, localized names, exact counts, empty destinations, hero ownership, and selection highlighting matched the upper meeting. Cross-hero moves, native last-stack retention, matching-creature merges, different-creature swaps, both directions, exact summaries, and one-shot requests behaved correctly. Selection change and cancellation, mixed upper and lower interaction, whole-army controls, rapid taps, cancelled touches, multitouch, panel toggling, nested Hero screens, and suspend/resume introduced no crossed, queued, or stale state. Close, upper touchscreen, mouse, hotkeys, physical controls, established Hero and Castle army actions, and haptics remained unchanged.

### Hero Meeting army-transfer deck

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-02.

- Validated source commit: `256369f9132639cc7791dee0b25db7f1dc9ae095`.

- Meeting two owned heroes switches the lower display from Adventure Map controls to a dedicated Hero Meeting context with Army Right, Army Left, Swap Armies, and Close actions.
- The actions are consumed in the existing Hero Meeting loop on the SDL thread and reuse the native `MoveTroops` and `SwapTroops` paths. Native preflight checks mute a directional transfer when it cannot change either army; Swap and Close remain available while both meeting armies are valid.
- The information card identifies both heroes and shows each army's live occupied-stack and total-creature counts. Selecting a troop on the upper screen retains the existing native keep-creature behavior and updates the lower guidance and action availability.
- Opening either nested Hero screen temporarily restores the validated Hero deck and returns to Hero Meeting on close. Closing the meeting restores Adventure exactly once; context changes clear queued actions and stale information.
- Drag-and-drop, split and join within the meeting, same-army rearranging, portraits, broader creature-sprite use, artifact transfers, configurable haptics, and configurable layouts remain deferred as separate slices. Upper-screen artifact controls and all established input paths remain unchanged.
- Android build and lint pass through the required short `R:` mapping. The candidate installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief checks found the resumed game process and the package presentation window on lower display 4, with no matching fatal startup log. Candidate debug APK SHA-256: `5CD009D8E0EFF19E8DC31D7F90A87A1088C315C30532C80CB892FAF743E47CBD`.

#### Focused Hero Meeting validation

1. Meet two owned heroes and verify the dedicated deck replaces Adventure controls; verify no Adventure command can fire while the meeting is open.
2. Move armies in both directions and verify the upper army bars and lower summaries update immediately, including selected-stack retention and the last-stack rule. Verify a transfer mutes when it cannot change the armies.
3. Swap armies and verify both heroes receive the correct troops with one operation per accepted tap.
4. Open each Hero screen from the meeting and return. Verify Hero to Hero Meeting to Adventure restoration is exact and no stale information appears.
5. Rapidly tap, cancel a touch, toggle the lower panel, and suspend/resume. Verify no queued transfer, stuck press, stale deck, or stale army summary.
6. Verify upper touchscreen, mouse, hotkeys, physical controls, existing Hero and Castle army actions, and established haptics remain unchanged.

All six focused checks passed on the Thor. The dedicated deck replaced Adventure controls without action leakage; both transfer directions, selected-stack retention, last-stack protection, no-op availability, live summaries, and one-shot army swapping behaved correctly. Nested Hero, Adventure, panel, and suspend/resume restoration remained exact without stale state or stuck input, and all requested upper-screen, mouse, hotkey, physical-control, Hero, Castle, and haptic regressions passed.

### Player-installed Hero portrait

Status: `passed`; behavior and focused acceptance tests were approved and hardware-validated on 2026-09-02.

- Validated source commit: `5a07bdeda0c5c91343a3d8a578aa9e8b301a9fbf`.

- The Hero information card shows the current hero's large portrait beside the existing text. The image is rendered by the native engine from the player's installed Heroes II assets; no proprietary artwork is added to the APK.
- A bounded, versioned native visual snapshot carries only context, revision, dimensions, and ARGB pixels. Android accepts images no larger than 256 by 256 pixels, caches only the latest bitmap, and falls back to the established text-only card for empty or invalid snapshots.
- Portrait changes are revision-driven. Previous and Next update the portrait and text together, while context transitions publish an empty visual snapshot so dialogs, player changes, panel recreation, and unrelated screens cannot retain stale artwork.
- The portrait is decorative and outside hit testing. Existing Hero semantic actions, information, modal restoration, Adventure restoration, upper touchscreen, mouse, hotkeys, and physical controls remain unchanged.
- Broader hero-list portraits, castle imagery, creature sprites, army management, configurable haptics, and configurable layouts remain deferred as separate slices.
- Android build and lint pass through the required short `R:` mapping. The final guarded candidate limits portrait publication to the Android Thor Hero context. It installed successfully over wireless ADB and launched explicitly as `org.fheroes2.thor/org.fheroes2.GameActivity`; brief checks found the resumed game process and the package presentation window on lower display 4, with no matching fatal startup log. Debug APK SHA-256: `252983E52575C5B0E3F8DBC3C315AA589A4A6CCC2BC60156755A4484D4D5D1BF`.

#### Focused player-installed portrait validation

1. Open several heroes and verify each lower portrait matches the upper Hero screen. Use Previous and Next in both directions and verify portrait and text update together.
2. Check heroes with long names and full information values; verify the name, level/race, stats, movement/mana, morale/luck, and portrait remain readable and inside the card.
3. Open and cancel Dismiss or another modal from Hero; verify the same portrait, information, and Hero deck restore exactly once.
4. Close Hero, select a different hero on the Adventure Map, and reopen. Verify no stale portrait appears during either transition.
5. Toggle the lower panel and suspend/resume while Hero is open. Verify the correct portrait recovers without a blank, corrupt, or previous-player image.
6. Recheck Hero semantic actions, Adventure restoration, upper touchscreen, mouse, hotkeys, and physical controls.

All six focused checks passed on the Thor. Portraits matched the upper Hero screen and stayed synchronized with Previous and Next; long names and full information remained bounded and readable; modal, Adventure, panel, and suspend/resume restoration retained the correct portrait without stale or corrupt artwork; and Hero semantic actions, upper touchscreen, mouse, hotkeys, and physical controls remained unchanged.

## Milestone 4: interactive second-screen tools

Status: `deferred`

- Touch radar or minimap with upper-screen viewport control is hardware-validated; retain it without regression.
- Hero and castle quick-selection lists are hardware-validated; retain native ordering, focus ownership, revision checks, paging, and exact Adventure restoration without regression.
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

Status: `in progress`

- Maintain `origin` as the user-owned Thor repository and `upstream` as the official read-only source. Keep the upstream push URL disabled locally to prevent accidental writes.
- Keep Thor-specific changes isolated behind `TARGET_AYN_THOR`, in focused commits and dedicated bridge/UI files where practical, to reduce conflicts with upstream gameplay work.
- Maintain a clean upstream-tracking branch and periodically integrate `upstream/master` into `ayn-thor-dual-screen` using a documented merge or rebase policy. Never rewrite a published validated branch without an explicit decision.
- Before each upstream integration, record the last validated Thor commit and create a recoverable tag or backup branch. Review upstream Android, SDL/controller, rendering, resolution, JNI, and dialog-event changes for overlap.
- After integration, run Android build and lint through the short `R:` path, inspect the Thor-specific diff, install on hardware, and repeat a compact dual-screen, controller, context-transition, and save/load smoke test.
- Add an integration log describing the upstream commit, conflicts and resolutions, APK checksum, device variant, and validation result. Consider CI for compile/lint once the fork remote is established.

### Initial maintenance checkpoint

- On 2026-08-23, `origin` was changed to `https://github.com/CapnChaosDK/fheroes2_thor.git` and `upstream` to the official `https://github.com/ihhub/fheroes2.git`. The upstream push URL is deliberately disabled locally to prevent accidental writes.
- Branch `ayn-thor-dual-screen` and annotated tag `thor-m3-information-validated` were published to the Thor repository. The tag identifies hardware-validated commit `75cb5acf8`.
- After fetching official commit `6a4b27a12`, the Thor branch was 29 commits ahead and 2 translation commits behind `upstream/master`. The Thor and upstream changes had zero overlapping files relative to merge base `20218c07c`, so the pending integration is expected to be low risk.
- Official commit `6a4b27a12` was subsequently integrated by merge commit `f151f77b8` without conflicts. Build/lint, APK installation, cold launch, lower-display startup, and the compact user hardware smoke test all passed.
- Detailed checkpoints and future integrations are recorded in `AYN_THOR_UPSTREAM_LOG.md`.
