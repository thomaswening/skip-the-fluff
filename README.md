# Skip the Fluff

An unofficial, distraction-free wrapper for duolingo.com on Android.

Skip the Fluff shows the Duolingo website in an Android app and leaves out the screens that do not serve learning: "use the app" prompts, leaderboard popups, streak nags, upsells, celebration animations and lesson intros. A lesson then takes little more time than its [exercises](docs/glossary.md#exercise) do. It keeps the whole screen for the lesson and keeps your place when you switch apps briefly.

It does **not** answer, skip or complete exercises. It does not change XP, gems or streaks, does not unlock paid features and does not modify Duolingo's own app. The full list is in [REQ-003](docs/requirements.md#req-003).

## Disclaimer

Skip the Fluff is unofficial and is not affiliated with or endorsed by Duolingo. You use it at your own risk.

## Status

Design. No code and no release yet. The project is described in:

- [Requirements](docs/requirements.md): what Skip the Fluff must do, verifiably
- [Architecture](docs/architecture.md): how the app is built, its parts and its risks
- [Roadmap](docs/roadmap.md): the phases in which the app is built, and which decisions each needs
- [Decisions](docs/adr/README.md): the [architecture decision records](docs/glossary.md#adr), one decision each
- [Glossary](docs/glossary.md): the terms the documents use
- [Development](docs/development.md): branches, commits, releases and the checks every pull request runs

## Installing

Not yet possible. Once there is a first release, you download its [APK](docs/glossary.md#apk) from [GitHub Releases](https://github.com/thomaswening/skip-the-fluff/releases) on your phone and install it. You need no computer, root or developer options for this.

## Reporting a fix that stopped working

Duolingo can change its website at any time. A distraction can then come back, or Skip the Fluff may hide something it should not. The app will have a report button that opens a pre-filled issue for you ([REQ-033](docs/requirements.md#req-033)). Until then, please [open an issue](https://github.com/thomaswening/skip-the-fluff/issues) that names the screen, includes a screenshot and gives the app version from the about screen. Issues are public: before attaching a screenshot, crop or cover your name, profile picture and other learners' names.

## Contributing

How changes are made and checked is in [docs/development.md](docs/development.md).

## Credit

The idea of wrapping the Duolingo website follows [DL: language lessons](https://github.com/hmlendea/dl-desktop) by hmlendea, a desktop wrapper on Flathub. Skip the Fluff contains none of its code.

## Licence

GNU General Public License v3.0 only. See [LICENSE](LICENSE).
