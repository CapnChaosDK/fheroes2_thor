# AYN Thor development backlog

This file is the maintained backlog for Thor-specific work. Update the status and acceptance criteria here when a milestone changes; do not discard deferred items when implementing an earlier milestone.

Status values: `planned`, `in progress`, `blocked`, `done`, `deferred`.

## Latest release checkpoint

- `thor-v0.4.0` was published on 2026-08-23 as a debug-signed AYN Thor prerelease.
- Release: https://github.com/CapnChaosDK/fheroes2_thor/releases/tag/thor-v0.4.0
- Source commit: `503ef2318ca254329748ce443ee69271bcc2bf41`.
- APK: `fheroes2-thor-v0.4.0-debug.apk`.
- APK SHA-256: `2520C1C55BC954DFA2E1F91FF37A7BE2BE75305FEEE6B1BA22D5E0FE8930D01F`.
- The release adds the hardware-validated Scenario Setup player-editing workflow: player navigation, Standard human-position transfer, fixed-count Hot Seat seat swapping, faction selection, handicap selection, synchronized upper highlighting, and lower information refresh.

## Latest validated development checkpoint

- Commit `f395e0a8d` is the latest hardware-validated development checkpoint. It adds the navigable Game Settings workflow on top of both campaign selectors, validated Battle Only setup, High Scores workflow, and `thor-v0.4.0`.
- Game Settings exposes all seven engine-owned settings groups plus Okay / Back, live state synchronization, safe language availability, configuration persistence, and exact restoration from child dialogs.
- Debug APK SHA-256: `3D5254C2970770B5268FAA955CA2F1800E30226EFE04FDC0899EF6B32BCBC2EB`.
- Android build and lint passed. The APK installed and launched explicitly on the Thor, and the user passed all focused settings, persistence, dialog-restoration, rapid-tap, input, menu, and campaign regression checks.

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

### Later menu slices

- Battle Only setup is hardware-validated; retain its controls, information card, modal restoration, and Battle transition without regression.
- The Succession Wars and Price of Loyalty selectors are hardware-validated. Retain their selection behavior, missing-video handling, hover-driven animations, Back semantics, and physical-input compatibility without regression.
- High Scores is hardware-validated; retain its Standard/Campaign synchronization, availability rules, Dialog restoration, and direct Exit without regression.
- Game Settings is hardware-validated; retain its enabled states, live summary, nested-dialog restoration, persistence, and existing input paths without regression.
- In-map Editor File Options and System Options are hardware-validated; tools, map specifications, and richer information remain separate later slices.
- A safe Menu fallback for an unknown or newly added upstream menu state.

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
