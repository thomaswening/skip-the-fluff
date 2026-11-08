# ADR-0005: GitHub Actions signs releases with a key held in a protected environment and backed up twice off GitHub

- **Status:** Accepted
- **Date:** 2026-11-08
- **Decided by:** Thomas, 8 November 2026, in conversation, on Claude's brief

## Context

[REQ-043](../requirements.md#req-043) requires every release to be signed with the same [signing key](../glossary.md#signing-key), so that it installs over the previous one and the learner keeps their sign-in and settings. It leaves to an [ADR](../glossary.md#adr) how the key is kept safe.

Android installs an update only if it is signed with the same key as the installed app. In .NET terms the key is like a code-signing certificate's `.pfx` file, except that Android binds every future update to it. Two things can go wrong:

- **The key is lost.** No learner can update any more. Each must uninstall, which deletes their sign-in and settings, and install a release signed with a new key. Skip the Fluff is not on the Play Store ([REQ-028](../requirements.md#req-028)), so Google holds no copy and cannot help.
- **The key is stolen.** Whoever holds it can publish an [APK](../glossary.md#apk) that Android installs over Skip the Fluff as a genuine update.

The repository is public, which means anyone can read it, fork it and open pull requests, but only accounts given write access can push to it. Workflows started by a pull request from a fork never receive the repository's secrets. A thief therefore needs either one of the backups or an account with write access to the repository.

Releases are created by [release-please](../glossary.md#release-please) when Thomas merges the [release PR](../glossary.md#release-pr) ([development.md](../development.md#releases)). The minimum is Android 10 ([ADR-0004](0004-minimum-android-10.md)), so every supported phone understands APK Signature Scheme v3. Its key rotation lets a release say "this new key replaces the old one", provided the old key is still available to sign that statement.

## Decision

**A GitHub Actions job signs each release. The key and its password are secrets of a GitHub environment named `release`, which runs only with Thomas's approval. Two backups are kept off GitHub.**

1. **Signing in CI.** When release-please creates a release, a job builds the APK from that tag on `main` in a clean runner, signs it with signature schemes v2 and v3, and attaches it to the release. The job runs in the `release` environment. That environment:
   - requires Thomas as reviewer for every run;
   - accepts only tags `v*`;
   - holds the secrets `RELEASE_KEYSTORE_BASE64` (the keystore file, base64-encoded), `RELEASE_KEYSTORE_PASSWORD` and `RELEASE_KEY_ALIAS`.

   No other job or workflow can read them. Required reviewers for environments are free on a public repository.
2. **The key.** One RSA key of 4096 bits in a PKCS 12 keystore, valid for 30 years, used for nothing else. The debug key of the [spikes](../glossary.md#spike) is a different key.
3. **Two backups, both off GitHub.** The keystore file goes in Thomas's password manager as an attachment, if it supports one, or else on an encrypted USB stick. A second copy goes on an encrypted USB stick kept somewhere other than Thomas's PC. The password is in Thomas's password manager. Once, at setup, a test APK is signed from a backup to prove that the backup works.
4. **A published fingerprint.** The README and every release page state the SHA-256 fingerprint of the signing certificate. Anyone can check an APK against it, with `apksigner verify --print-certs` or an app such as AppVerifier.
5. **Plans on record.** IF the key is stolen, THEN the next release rotates to a new key with signature scheme v3, signed by the old key, and the README, the release page and an issue announce it. IF the key is lost, THEN a release with a new key is published, and the README and release page explain that learners must uninstall and install it again.
6. **Only Thomas can write to the repository.** Claude's `claudinius` account is not a collaborator, so it can neither change the release workflow nor push a release tag. It still replies to review comments, which anyone can do on a public repository. A repository owned by a personal account offers collaborators only write access, with no narrower role such as Triage. The brief for this record found a pending invitation for `claudinius` with write access, and Thomas had it withdrawn on 8 November 2026.
7. **Only Thomas handles the key.** Thomas generates it on his PC, enters its password, makes the backups and stores the secrets in GitHub himself. Claude gives the commands but never sees or types the password.

## Alternatives considered

- **The key only on Thomas's PC, releases built and signed by hand:** nothing secret on GitHub. It was set aside because every release would be a manual job on one machine, the released APK would not come from the reviewed code in a clean environment, and losing the PC would leave only the backup.
- **A cloud key service (a hardware security module, HSM) that signs without the key ever leaving it:** the strongest protection against theft. Its setup, cost and an account to run are out of proportion for a single-maintainer project.
- **A repository secret instead of an environment secret:** any workflow on any branch could read it. The environment adds Thomas's approval and the tag restriction.

## Consequences

- Easier: a release needs two clicks from Thomas, merging the release PR and approving the job, and the APK is built from reviewed code.
- Easier: learners and later F-Droid ([OPEN-G](../requirements.md#open-g)) can check that an APK is genuine against the published fingerprint.
- Harder: anyone who gains write access to the repository could change the release workflow. The required approval means a changed workflow still cannot run with the key unnoticed. This holds for every account with write access. Today only Thomas has it (point 6). The reviewer rule stays in place as the guard for any account given write access later.
- Harder: the backups must be kept up to date and findable. A key that exists only in GitHub's secrets cannot be read back out.
- Follow-up: Thomas generates the key and the backups, with this command for the keystore:

  ```
  keytool -genkeypair -v -storetype PKCS12 -keystore skip-the-fluff-release.p12 -alias skip-the-fluff -keyalg RSA -keysize 4096 -validity 10950 -dname "CN=Skip the Fluff"
  ```

- Follow-up: the release workflow job, the `release` environment and its documentation in [development.md](../development.md#releases) come with the first build.
