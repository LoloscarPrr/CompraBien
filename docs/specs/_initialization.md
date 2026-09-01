# TLC Initialization Snapshot

- Repository: `LoloscarPrr/CompraBien`
- Working ref: `feat/history-persistence-v0.5`
- Base lineage: `feat/catalog-v0.2`
- App version: `0.5.0` (`versionCode 6`)
- Current focus: persistent price history, observability, adaptive UI and development governance.
- Relevant specs: `CB-CORE-001`, `CB-UI-001`, `CB-OBS-001`.
- Baseline: previous v0.5 CI observed running/green lineage; new combined change requires fresh CI verification.
- Constraints: Firebase project configuration file `app/google-services.json` is not present in the repository, so live Crashlytics delivery cannot be verified yet.
- Next work: implement TLC skill, adaptive compact/normal/large layout and Crashlytics-ready integration without breaking builds lacking Firebase config.
