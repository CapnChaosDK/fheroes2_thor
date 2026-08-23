# AYN Thor upstream integration log

Use this log for every official-upstream assessment or integration. Keep validated tags and APK checksums so each operation has a recoverable reference.

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

### Next integration procedure

1. Confirm the working tree is clean and create a new recovery tag or backup branch.
2. Fetch `upstream` and recalculate divergence and changed-file overlap.
3. Review upstream changes affecting Android, SDL/controller input, rendering, resolution, JNI, battle, Hero, Castle, or dialog event loops.
4. Integrate using the agreed merge or rebase policy without rewriting an already published validated branch.
5. Build and lint through the short `R:` drive mapping.
6. Record the APK SHA-256 checksum and install it on the Thor.
7. Run the compact dual-screen, cursor/controller, context restoration, information-card, and save/load smoke test.
8. Record the integrated upstream commit, conflicts, resolutions, device variant, and validation result here.
