# fheroes2

**fheroes2** is a recreation of the Heroes of Might and Magic II game engine.

This open source multiplatform project, written from scratch, is designed to reproduce the original game with significant
improvements in gameplay, graphics and logic (including support for high-resolution graphics, improved AI, numerous fixes
and user interface improvements), breathing new life into one of the most addictive turn-based strategy games.

You can find a complete list of all of our changes and enhancements in [**this page**](docs/GAME_IMPROVEMENTS.md).

<p align="center">
    <img src="docs/images/screenshots/screenshot_world_map.webp" width="820" alt="Screenshot of the world map">
</p>

<p align="center">
    <img src="docs/images/screenshots/screenshot_battle.webp" width="410" alt="Screenshot of the battle screen">
    <img src="docs/images/screenshots/screenshot_castle.webp" width="410" alt="Screenshot of the castle screen">
</p>

## AYN Thor dual-screen edition

This fork adds an AYN Thor-specific Android build. The game runs at 1920 x 1080 on the upper display while the lower display provides a Heroes II-styled, context-sensitive command deck and information panel, including navigable New Game, Load Game, Scenario Setup, Battle Only setup, High Scores, Game Settings, both campaign selectors, and the complete Map Editor workflow through Map Specifications and Editor Tools. The Thor package can be installed alongside the official Android build.

Download the current test APK from the [Thor releases page](https://github.com/CapnChaosDK/fheroes2_thor/releases). Build, installation, dual-screen, and diagnostic instructions are available in the [AYN Thor guide](docs/README_ayn_thor.md).

### AYN Thor physical controls

The built-in controls follow the PlayStation Vita port's mapping:

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

The mapping follows SDL's A/B/X/Y convention. If the Thor system settings offer Xbox and Nintendo button modes, use **Xbox mode** so the physical button positions match this table.

## Download and Install

Please follow the [**installation guide**](docs/INSTALL.md) to download and install fheroes2.

[![Github Downloads](https://img.shields.io/github/downloads/ihhub/fheroes2/total.svg)](https://github.com/ihhub/fheroes2/releases)

## Copyright

All rights for the original game and its resources belong to former The 3DO Company. These rights were transferred to Ubisoft.
We do not encourage and do not support any form of illegal usage of the original game. We strongly advise to purchase the original
game on [**GOG**](https://www.gog.com) or [**Ubisoft Store**](https://store.ubi.com) platforms. Alternatively, you can download a
free demo version of the game. Please refer to the [**installation guide**](docs/INSTALL.md) for more information.

## License

This project is licensed under the [**GNU General Public License v2.0**](https://github.com/ihhub/fheroes2/blob/master/LICENSE).

Initially, the project was developed on [**sourceforge**](https://sourceforge.net/projects/fheroes2/).

## Contribution and Development

This repository is a place for everyone. If you want to contribute, please read more to learn [**how you can contribute**](docs/FAQ.md#q-how-can-i-contribute-to-the-project).

### Developing fheroes2 engine

To build the project from source, please follow [**this guide**](docs/DEVELOPMENT.md).

[![Build Status](https://github.com/ihhub/fheroes2/actions/workflows/push.yml/badge.svg)](https://github.com/ihhub/fheroes2/actions)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ihhub_fheroes2&metric=bugs)](https://sonarcloud.io/dashboard?id=ihhub_fheroes2)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ihhub_fheroes2&metric=code_smells)](https://sonarcloud.io/dashboard?id=ihhub_fheroes2)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ihhub_fheroes2&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=ihhub_fheroes2)

To assist with the graphical asset efforts of the project, please look at our [**graphical artist guide**](docs/GRAPHICAL_ASSETS.md).

If you would like to help translating the project, please read the [**translation guide**](docs/TRANSLATION.md).

### Developing fheroes2 documentation site

To build the [website](https://ihhub.github.io/fheroes2/) from source, please follow
[**this guide**](docs/WEBSITE_LOCAL_DEV.md).

## Donation

We accept donations via [**Patreon**](https://www.patreon.com/fheroes2), [**PayPal**](https://www.paypal.com/paypalme/fheroes2) or [**Boosty**](https://boosty.to/fheroes2).
All donations will be used only for the future project development as we do not consider this project as a source of income by any means.

[![Patreon Donate](https://img.shields.io/badge/Donate-Patreon-green.svg)](https://www.patreon.com/fheroes2)
[![Paypal Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/paypalme/fheroes2)
[![Boosty Donate](https://img.shields.io/badge/Donate-Boosty-green.svg)](https://boosty.to/fheroes2)

## Contacts

Follow us on social networks: [**Facebook**](https://www.facebook.com/groups/fheroes2) or [**VK**](https://vk.com/fheroes2).
We also have a [**Discord**](https://discord.gg/xF85vbZ) server to discuss the development of the project.

[![Facebook](https://img.shields.io/badge/Facebook-blue.svg)](https://www.facebook.com/groups/fheroes2)
[![VK](https://img.shields.io/badge/VK-blue.svg)](https://vk.com/fheroes2)
[![Discord](https://img.shields.io/discord/733093692860137523.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/xF85vbZ)

## Frequently Asked Questions (FAQ)

You can find answers to the most commonly asked questions on our [**F.A.Q. page**](docs/FAQ.md).
