# CSC 8223 — PoseFL Project Handoff Notes

**Project:** Privacy-Preserving Smartphone Pose Classification Using Federated Learning  
**Team:** Yateeka Goyal (Android client and sensors) · Zihan Tang (learning and FL server)  
**Status date:** November 19, 2026  
**Purpose:** Track what is complete, what remains, who owns the next step, and what evidence must be shared before the final report and live demonstration.

> **Status convention:** **Completed** means confirmed from our Client A recording workflow. **Pending** means the work has not been confirmed complete here; it does **not** mean it has not been started.

## 1. Completed / confirmed: Client A recordings (Yateeka)

Client A has finished the six *evaluation and robustness* sittings below. The dataset split and intended conditions are shown so Client B can follow the same protocol on a separate device.

| Session | Split | Collection protocol | Conditions | Recorded windows | Status |
|---|---|---|---|---:|---|
| `A_te01` | `test` | 6 poses × (10 + 10 windows), reposition between runs | Table | 121 | Completed; one extra window in pose 2 |
| `A_te02` | `test` | 6 poses × (10 + 10 windows) | Hard notebook | 120 | Completed |
| `A_te03` | `test` | 6 poses × (10 + 10 windows) | Handheld, as steady as possible | 120 | Completed |
| `A_cal01` | `calib` | 6 poses × 15 windows, one run per pose | Stable table/desk | 90 | Completed |
| `A_tilt01` | `calib` | 6 poses × 20 windows, one run per pose | ~10–20° tilt, held steady | 120 | Completed; check angles in offline analysis |
| `A_shake01` | `calib` | 6 poses × 20 windows, one run per pose | Handheld with gentle tremor/light disturbance | 120 | Completed |
| **Total** | | | | **691** | **Standard test and robustness collection complete** |

**Data handling:** Keep the six cleaned session-specific CSVs and untouched original exports. Use the session ID to filter cumulative app exports. Each usable sensor window is a row; the PoseFL export format has 17 columns. Do **not** erase on-phone recordings, and do not train on `test` or `calib` sessions.

**Still to verify for Client A handoff:** final filenames and checksums/backup locations; phone/Android version, case status, observed gravity-axis signs (especially landscape); notes about where and when each sitting occurred. Share the actual CSVs, not screenshots alone.

## 2. Immediate remaining data collection

| Priority | Owner | Task | Completion evidence |
|---|---|---|---|
| 1 | **Both** | **Confirm the non-IID assignment:** Client A trains on labels **0, 1, 2**; Client B on **3, 4, 5**, as proposed in the report. | Agreement in team chat / recording log |
| 2 | **Yateeka / A** | Collect `A_tr01`–`A_tr06` using split `train` and **only labels 0, 1, 2**, after confirmation. Each sitting: **2 × 20 windows per label** (about 120 per sitting; ~720 total, ~240 per assigned label). Reposition between runs and vary sitting conditions. | Six session IDs with `train` rows; counts by label; CSV backup |
| 3 | **Zihan / B** | Collect `B_tr01`–`B_tr06` using split `train` and **only labels 3, 4, 5**, after confirmation, with the same **2 × 20 windows per label** protocol. | Six session IDs with `train` rows; counts by label; CSV backup |
| 4 | **Zihan / B** | Record `B_te01`–`B_te03`, `B_cal01`, `B_tilt01`, and `B_shake01` using all six labels. Match Client A conditions: **table → hard notebook → handheld**, then stable calibration, supported tilt, and gentle shake. | Six exported sessions; ~360 test + 90 calibration + 120 tilt + 120 shake windows |
| 5 | **Both** | Record actual pose-axis signs and phone information. For physical Landscape Left the **top edge points left**; for Landscape Right it points right. Confirm signs on each device rather than assuming them. | Device and orientation notes in shared log |

**Recording reminder:** Use unique session IDs. For standard testing, stop after ~10 new windows, reposition slightly **on the same surface**, then record ~10 more for that pose. Calibration uses one run of 15; tilt and shake each use one run of 20. A *window* is not necessarily one second on the app counter. Preserve the separation of `train`, `test`, and `calib`.

## 3. Software, training, and federated-learning work still to confirm or complete

| Owner / collaborators | Remaining work | Done when |
|---|---|---|
| **Yateeka (Android)** | Ensure the repository contains the complete Android Studio/Gradle project; check a fresh clone builds and tests pass. Keep the installed app and CSV storage intact. | `./gradlew assembleDebug` and `./gradlew test` succeed on a clean clone |
| **Both (Android setup)** | Verify both phones can run PoseFL, record and export all three splits, and show sensible gravity readings for six poses. | Screenshot or log of working setup plus sample CSV from each phone |
| **Zihan (learning / FL server)** | Verify Kotlin MLP numerical behavior, the FastAPI `/model` and `/update` endpoints, FedAvg weighting, SQLite round-state persistence, and error/duplicate-update handling. | Demonstrated API responses, validation results, and a completed synchronous round |
| **Both (integration)** | Configure Tailscale/network addresses, connect two Android clients to laptop coordinator, and run the planned **10 rounds × 3 local epochs** (or document deviations). | Logs showing both client updates and global-model rounds |
| **Both (local baselines)** | Train **BASELINE**, **GRAV_NORM**, and **ROBUST** local-only models on `train` sessions only; evaluate each on six-pose `test` sessions. Record results immediately because the app may overwrite its current local model. | Train counts/loss, test accuracy, per-class accuracy, macro-F1, confusion matrix, screenshots/logs |
| **Zihan (analysis), both review** | Evaluate Client A local, Client B local, and FedAvg global models on the same six-pose protocol. Compare feature configurations, tilt, shake, and cross-device transfer. | Completed results tables and figures with reproducible analysis scripts |
| **Both** | Verify whether ROBUST discards high-motion windows; report retained windows, not just exported totals. Do not claim performance before evaluating. | Reported retained sample counts per configuration |

## 4. Results still needed for the CSC 8223 final report

The report proposes a six-input / 16-ReLU / six-softmax MLP, cross-entropy with SGD, sample-weighted FedAvg, and three feature settings. The final paper should replace planned/placeholder statements with **measured results**.

- **Local-only comparison:** Client A and Client B accuracy, macro-F1, per-class performance, and confusion matrices on the same six-pose test set.
- **Federated comparison:** FedAvg global-model scores, 10-round convergence or documented actual number of rounds, and comparison against local-only baselines.
- **Robustness:** Standard versus tilted versus shaken conditions for BASELINE, GRAV_NORM, and ROBUST; note rejected windows.
- **Cross-device:** Evaluate a model on the other device's data. State whether recordings were simultaneous; if not, acknowledge placement/time differences.
- **Implementation evidence:** server API / persistence and Kotlin numerical validation, final device details, methodology, CSV session summaries, and reproducibility commands.
- **Demo:** two working Android clients, server running on laptop, a local prediction, one federated round or reproducible demonstration of the full round flow, and global prediction/evaluation.

## 5. Handoff package checklist

### Yateeka → Zihan
- [ ] Six Client A evaluation/robustness CSVs (691 usable windows total), plus untouched originals
- [ ] Six Client A training-session CSVs, **after assigned labels are confirmed**
- [ ] Device information, physical pose convention, observed axis signs, surface/time log, and exclusions
- [ ] Successful Android clean-build/test evidence and app version/commit
- [ ] Client A BASELINE / GRAV_NORM / ROBUST results with screenshots and confusion matrices

### Zihan → Yateeka
- [ ] Confirm Client A/B pose assignments and CSV split handling
- [ ] Client B training and evaluation/robustness CSVs plus device/surface log
- [ ] FL server URL/Tailscale instructions and working FastAPI/SQLite deployment
- [ ] Validated MLP/FedAvg code, analysis scripts, experimental results and plots
- [ ] Any integration steps needed for the joint live demo

### Both → Final submission
- [ ] Reproducible source repository and README
- [ ] Final report: methods, actual window counts, measured results, limitations, citations
- [ ] Two-device end-to-end demo tested before presentation
- [ ] Source/data sharing location agreed upon; avoid uploading private device information unnecessarily

## 6. Milestone focus from the project design report

| Project milestone | Dates | Present focus |
|---|---|---|
| Android sensing/UI, local MLP, server | Oct 21–Nov 2 | Confirm reproducible build and functioning coordinator |
| Remote client + FedAvg integration | Nov 3–9 | Verify true two-client round-trip |
| Model comparisons, six poses | Nov 10–16 | Finish training and collect local/global metrics |
| Robustness + baselines | Nov 17–23 | Client A recordings done; Client B recordings/analysis still need verification |
| Final report, sources/README, demo rehearsal | Nov 24–30 | Finalize measurements, documentation, reproducibility, and demo |

**Next team decision:** Confirm the label split and choose dates for Client B's matching collection and both clients' training/integration. Completion status for the server, Client B dataset, and model evaluation must be updated by the responsible teammate; it has not been independently verified in this handoff.
