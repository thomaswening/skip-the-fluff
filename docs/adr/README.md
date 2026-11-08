# Architecture Decision Records

Each file records one significant decision: its context, the decision, the alternatives considered and the consequences. Records are numbered and never rewritten. If a decision changes, a new record supersedes the old one. The old one's status is updated to point to it.

Status values: **Proposed** (direction chosen, not yet validated), **Accepted**, **Superseded by ADR-NNNN**. Each record is proposed in its own pull request and accepted by merging it, so the default branch holds only accepted records.

A record that settles an open decision of [requirements.md](../requirements.md) names it in its title and links it. The decision's entry in the requirements points back here. Evidence that was measured rather than argued lives in `docs/spikes/` and is linked from the record.

New records copy [template.md](template.md).

| Record | Decision | Status |
|---|---|---|
| [ADR-0001](0001-android-webview.md) | [The website](../glossary.md#the-website) is shown in Android's own [WebView](../glossary.md#webview) | Accepted |
| [ADR-0002](0002-fix-engine.md) | [Fixes](../glossary.md#fix) are data, applied by one engine that runs before the website's own scripts | Accepted |
| [ADR-0003](0003-lesson-survives-switch.md) | The lesson survives an app switch because the app keeps its [WebView](../glossary.md#webview) and [renderer process](../glossary.md#renderer-process) alive | Accepted |
