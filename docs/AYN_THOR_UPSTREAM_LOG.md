# AYN Thor upstream integration log

Use this log for every official-upstream assessment or integration. Keep validated tags and APK checksums so each operation has a recoverable reference.

## Current Thor handoff baseline

- Branch: `ayn-thor-dual-screen`
- Latest published release: `thor-v0.9.0` at `271d2a0fa3c9f55d69b20b5caf01169536478b72`
- Latest hardware-validated source commit: `b09695653a83e48c2779e0002f076710e14f68f4`
- Development APK SHA-256: `884A70B1F60A3CFAE4F3DE217096D90C003ACBB72898B95C1AE162DA4E03C35F`
- Release APK: `fheroes2-thor-v0.9.0-debug.apk`; SHA-256: `0AD83689B90F81E0C9001365CD0A60650DFCD4FF0CE0E63564222E694D720B66`.
- Precise Hero Meeting stack splitting, direct manipulation, tap-based troop-slot and whole-army transfers, player-installed Hero portrait and correctly proportioned creature sprites, semantic anti-stuck gameplay dialogs, complete in-game Adventure and File Options, complete Expanded Adventure Map, safe unknown-menu fallback, quick-selection lists, touch minimap viewport control, live Editor map information, complete Editor workflow, gameplay information and semantic controls, Game Settings, Battle Only setup, High Scores, and both campaign selectors are hardware-validated. The next focused slice must be selected and approved from richer army or artifact management, broader player-installed sprite use, configurable haptics, or configurable layouts.
- For future integration candidates, automate build/lint and concise diagnostics, then use a focused user-run manual Thor smoke test. Reserve extended automated device interaction for explicit requests or targeted failure diagnosis.

## 2026-08-23: initial fork checkpoint

- Thor repository: `https://github.com/CapnChaosDK/fheroes2_thor.git`
- Thor branch: `ayn-thor-dual-screen`
- Validated Thor commit: `75cb5acf8b7a4fd6e57f9d8a2cf06df7d4e88264`
- Recovery tag: `thor-m3-information-validated`
- Official upstream: `https://github.com/ihhub/fheroes2.git`
- Merge base: `20218c07cc31fb8ca3f0db033cec294bc9e18434`
- Assessed upstream commit: `6a4b27a12d07c47c0e2c924e96d0e6e1001a9e4f`
- Divergence: 2 upstream-only commits and 29 Thor-only commits
- Changed-file overlap: 0 files
- Upstream-only changes: translation updates `cd10174a4` and `6a4b27a12`
- Operation performed: remote setup, push protection, branch publication, tag publication, fetch, and read-only divergence assessment
- Integration performed: no
- Conflicts: none assessed; no overlapping changed files
- Build/lint: not required because no source integration occurred
- APK/device validation: existing Milestones 1-3 hardware validation remains represented by the recovery tag

### Standard integration procedure

1. Confirm the working tree is clean and create a new recovery tag or backup branch.
2. Fetch `upstream` and recalculate divergence and changed-file overlap.
3. Review upstream changes affecting Android, SDL/controller input, rendering, resolution, JNI, battle, Hero, Castle, or dialog event loops.
4. Integrate using the agreed merge or rebase policy without rewriting an already published validated branch.
5. Build and lint through the short `R:` drive mapping.
6. Record the APK SHA-256 checksum and install it on the Thor.
7. Run the compact dual-screen, cursor/controller, context restoration, information-card, and save/load smoke test.
8. Record the integrated upstream commit, conflicts, resolutions, device variant, and validation result here.

## 2026-08-23: integrate upstream through 6a4b27a12

- Pre-integration Thor commit: `943b7d33cecbdd6daba416f4fdb4aa95ff7db86f`
- Recovery tag: `thor-pre-upstream-6a4b27a12`
- Integrated upstream commit: `6a4b27a12d07c47c0e2c924e96d0e6e1001a9e4f`
- Merge commit: `f151f77b889ce23175609c786194f68cf00ad86e`
- Integration method: non-fast-forward merge; published Thor history was not rewritten
- Upstream changes: 26 translation files from commits `cd10174a4` and `6a4b27a12`
- Conflicts: none
- Thor-file overlap: none
- Post-merge divergence: 0 upstream-only commits and 31 Thor-only commits
- Android build and lint: passed through the short `R:` drive mapping
- APK SHA-256: `F1149CFCE9C1C4731B2197DD09BA66CC30D58DE208E53483B939CB432AB2910B`
- Device: AYN Thor, Android device model `kalama`
- Installation: passed on `192.168.68.84:40669`
- Cold launch: passed; game process started and the command deck opened on display 4
- User hardware smoke test: passed on 2026-08-23
- Smoke coverage: cold-start dual displays and input, Hero/Castle context and information restoration, Battle information and single-instance Options behavior, save/load context restoration, and display sleep/wake recovery
- Integration status: hardware validated

## 2026-09-04: pre-slice upstream assessment

- Validated Thor commit after the assessed slice: `b09695653a83e48c2779e0002f076710e14f68f4`
- Assessed upstream commit: `d778cb44b30e4fcf81ee70ccf96354b355c81c4f`
- Divergence after the validated implementation commit: 24 upstream-only commits and 105 Thor-only commits
- Upstream changes since the last integration touch 50 files. None overlap the precise-splitting implementation files in Hero Meeting, army-bar, split-dialog, Thor native bridge, or Android presentation code; the only Android-path change is the upstream Android workflow.
- Operation performed: read-only fetch and overlap assessment before the precise Hero Meeting stack-splitting slice
- Integration performed: no; upstream integration remains a separate planned maintenance decision
- Build/lint: passed for the Thor implementation through the required short `R:` drive mapping
- APK/device validation: installed and explicitly launched on the AYN Thor; the complete precise-splitting run and wording correction retest passed

## 2026-09-04: integrate upstream through d778cb44b

- Pre-integration Thor commit: `f9d67fa9bb0c2e4a7ccad253933e4ad64451006a`
- Recovery tag: `thor-pre-upstream-d778cb44b`
- Upstream candidate: `d778cb44b30e4fcf81ee70ccf96354b355c81c4f`
- Integration method: non-fast-forward merge staged with `--no-commit`; published Thor history has not been rewritten and no integration commit has been created.
- Divergence before integration: 24 upstream-only commits and 106 Thor-only commits.
- Conflicts: none. Both sides changed `editor_interface.cpp`, `editor_interface_panel.cpp`, and `game_assets.cpp`; inspection confirmed that Thor semantic Editor state and 16:9 main-menu scaling remain present alongside upstream water-Hero placement, cursor, and rendering changes.
- Gameplay rule review: upstream's surrendered-hero repair is present in `Heroes::Dismiss()`. An invalid army left after surrender is reset to one tier-1 creature, covering surrender with only temporary or resurrected creatures.
- Maintenance candidate: dedicated `ayn-thor-dual-screen` push CI builds and lints the debug app, runs complete native/Java identifier parity, and publishes no release. A compile-time check rejects same-context action-mask collisions.
- Static checks: all 58 context identifiers and 234 action identifiers match between native and Java; no current same-context mask collision exists.
- Android build and lint: passed through the required short `R:` drive mapping, including `:app:assembleDebug`, `:app:lintDebug`, and `:isotools:lint`.
- Candidate APK SHA-256: `2EDE3EBEE1240236D9D167CA5DB286A52554B07F5C38704215CD286F079CA05A`.
- Device installation and launch: passed on AYN Thor `kalama` at `192.168.68.58:35751`; `org.fheroes2.thor/org.fheroes2.GameActivity` is resumed and the non-focusable command-deck presentation is attached to display 4.
- User hardware validation: passed on 2026-09-04. Cold-start dual displays, cursor, physical controller, upper touchscreen, Hero Meeting One/Half splitting, tap/drag and whole-army transfers, split cancellation and lifecycle recovery, the surrendered-hero edge case, Editor Hero placement on land and water, dialog and save/load restoration, and established haptics all passed.
