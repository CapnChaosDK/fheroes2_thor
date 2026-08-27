# AYN Thor upstream integration log

Use this log for every official-upstream assessment or integration. Keep validated tags and APK checksums so each operation has a recoverable reference.

## Current Thor handoff baseline

- Branch: `ayn-thor-dual-screen`
- Latest published release: `thor-v0.4.0` at `503ef2318ca254329748ce443ee69271bcc2bf41`
- Latest hardware-validated source commit: `0ff1bb67e`
- Development APK SHA-256: `51664E906A2EBE207CBB888CAD49E16CAA2BCFA14734C390DD852938994157FA`
- Editor pre-entry, File Options, System Options, Game Settings, Battle Only setup, High Scores, and both campaign selectors are hardware-validated. The next focused planning point is the in-map Editor Map Specifications workflow; Editor tools and richer map information remain later slices.
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
