
# CSC 8223 — PoseFL Project Handoff Notes

**Project:** Privacy-Preserving Smartphone Pose Classification Using Federated Learning  
**Team:** Yateeka Goyal (Android client and sensors) · Zihan Tang (learning and FL server)  
**Status date:** November 21, 2026  
**Purpose:** Track what is complete, what remains, who owns each task, and what evidence must be shared before the final report and live demonstration.

> **Status convention:** **Completed** means the work has been completed and confirmed. **Confirmed** means both teammates agreed on a requirement or design. **Pending** means completion has not yet been verified. Agreement on a design does not mean its implementation is finished.

## 1. Completed / confirmed: Client A recordings (Yateeka)

Yateeka is confirmed as **Client A**, assigned training labels **0, 1, and 2**.

Client A has completed the six *evaluation and robustness* sittings below. These recordings contain all six pose labels and are separate from training data.

| Session | Split | Collection protocol | Conditions | Recorded windows | Status |
|---|---|---|---|---:|---|
| `A_te01` | `test` | 6 poses × (10 + 10 windows), reposition between runs | Table | 121 | Completed; one extra window in pose 2 |
| `A_te02` | `test` | 6 poses × (10 + 10 windows) | Hard notebook | 120 | Completed |
| `A_te03` | `test` | 6 poses × (10 + 10 windows) | Handheld, as steady as possible | 120 | Completed |
| `A_cal01` | `calib` | 6 poses × 15 windows, one run per pose | Stable table/desk | 90 | Completed |
| `A_tilt01` | `calib` | 6 poses × 20 windows, one run per pose | Approximately 10–20° tilt | 120 | Completed; analyze actual angles offline |
| `A_shake01` | `calib` | 6 poses × 20 windows, one run per pose | Handheld with gentle natural tremor | 120 | Completed |
| **Total** | | | | **691** | **Client A evaluation and robustness collection complete** |

### Important landscape pose correction

The course assignment defines landscape orientation by **which side edge points upward**, rather than which direction the phone's top edge points.

The correct definitions are:

| Label | Pose | Correct physical orientation | Expected Client A gravity X |
|---|---|---|---|
| 4 | Landscape Left | LEFT edge UP; top edge RIGHT | Approximately -9.8 m/s² |
| 5 | Landscape Right | RIGHT edge UP; top edge LEFT | Approximately +9.8 m/s² |

Client A's original six sessions were recorded using an earlier guide with the opposite landscape instructions.

This was a **labeling convention mismatch**, not a sensor failure.

Corrected CSV copies were created and checked. In these copies, labels 4 and 5 were exchanged while all sensor readings were preserved.

**Do not swap labels 4 and 5 again in the corrected CSVs.**

The corrected exported files have the expected gravity X signs:

- Label 4: negative X.
- Label 5: positive X.

The `A_tilt01` cleaned export contains 120 valid tilt windows after excluding one unrelated test row.

**Important:** Correcting exported CSV files does not automatically change the original recordings stored inside the Android app. Before using the app's built-in evaluation feature, its old landscape labels must be corrected or the affected stored test sessions excluded and replaced with a verified evaluation dataset.

### Data handling

Keep:

- Six cleaned, corrected session-specific CSVs.
- Untouched original exports as backups.
- Session notes and collection conditions.
- Any notes about excluded rows or label corrections.

Each usable window is one CSV row with 17 columns.

Use session IDs to filter cumulative app exports.

Do not erase on-phone recordings without a verified backup.

Do not use `test` or `calib` sessions for training.

### Still to verify for Client A handoff

- Final CSV filenames and backup locations.
- Phone model, Android version, and case status.
- Observed gravity-axis values for all six poses.
- Dates, surfaces, and recording conditions.
- Installed app version and GitHub commit.
- Correct handling of older mislabeled data inside the Android app.

Share actual corrected CSV files, not screenshots alone.

## 2. Immediate remaining data collection

### Confirmed team decisions

Zihan's email confirmed the following agreements:

| Item | Agreed decision | Status |
|---|---|---|
| Client A | Yateeka, training labels 0, 1, 2 | Confirmed |
| Client B | Zihan, training labels 3, 4, 5 | Confirmed |
| Session naming | Use `A_` and `B_` prefixes | Confirmed |
| Landscape definitions | Use the course's side-edge-up convention | Confirmed |
| Training separation | Only `train` samples are used for training | Confirmed |
| Evaluation separation | `test` and `calib` remain separate | Confirmed |
| Initial feature configuration | `GRAV_NORM` on both clients | Agreed |
| Cross-device recording | Independent recordings with matching protocols | Agreed |

**The non-IID client assignments are now confirmed. Neither teammate needs to wait for further role approval before collecting training data.**

### Remaining collection tasks

| Priority | Owner | Task | Completion evidence |
|---|---|---|---|
| 1 | **Yateeka / A** | Collect `A_tr01`–`A_tr06` using split `train` and only labels **0, 1, 2**. Each sitting: 2 × 20 windows per label, approximately 120 per sitting and 720 total. | Six training session IDs, counts by label, backed-up CSVs |
| 2 | **Zihan / B** | Collect `B_tr01`–`B_tr06` using split `train` and only labels **3, 4, 5**, following the same recording protocol. | Six training session IDs, counts by label, backed-up CSVs |
| 3 | **Zihan / B** | Record `B_te01`–`B_te03`, `B_cal01`, `B_tilt01`, and `B_shake01` using all six poses. Match Client A's table, hard notebook, handheld, calibration, tilt, and shake procedures. | Six exported evaluation/robustness sessions |
| 4 | **Both** | Verify all six physical poses and gravity-axis readings on each phone. In particular, Label 4 must use LEFT edge UP and Label 5 RIGHT edge UP. | Device-specific pose and sensor readings |
| 5 | **Both** | Record independently from different locations, using consistent conditions and documenting any differences. | Session logs, device details, and comparable test datasets |

### Training recording instructions

For each training sitting:

1. Open the PoseFL Collect tab.
2. Enter the correct session ID.
3. Select dataset split `train`.
4. Select the first assigned pose.
5. Record approximately 20 windows.
6. Stop recording.
7. Reposition the phone.
8. Record another 20 windows.
9. Stop and move to the next assigned pose.
10. Repeat for all three assigned poses.
11. Export and back up the data.

Expected totals for each client:

| Measurement | Expected windows |
|---|---:|
| Per pose per sitting | 40 |
| Per sitting (3 poses) | 120 |
| Across six sittings | 720 |
| Per assigned pose across all sittings | 240 |

Vary recording conditions across training sittings, such as surface, holding style, location, or time, while keeping the correct physical pose definitions.

### Evaluation recording reminder

For standard testing:

- Record 10 windows.
- Stop.
- Reposition slightly using the same surface or holding condition.
- Record another 10 windows.

Repeat for all six poses.

For calibration:

- One run of 15 windows per pose.

For tilt:

- One run of 20 windows per pose with approximately 10–20° tilt.

For shake:

- One run of 20 windows per pose with gentle natural movement.

Each sitting must use a unique session ID and one split tag.

### Cross-device collection agreement

Zihan confirmed that the two teammates are working from different locations.

Therefore, the plan is to:

- Record datasets independently.
- Use identical physical pose definitions.
- Use comparable recording surfaces and procedures.
- Document differences between devices and recording environments.
- Evaluate cross-device transfer using the independently collected data.

**Do not describe these recordings as simultaneous or side-by-side in the report.**

## 3. Software, training, and federated-learning work still to confirm or complete

### Confirmed model and server contract

Zihan agreed to the following model and communication format:

**MLP architecture**

- 6 input neurons.
- 16 ReLU hidden neurons.
- 6 softmax output neurons.
- 214 total model parameters.

**Flat weight ordering**

1. `W1` — 16 × 6, row-major.
2. `b1` — 16 values.
3. `W2` — 6 × 16, row-major.
4. `b2` — 6 values.

Both the Android client and Python server must use the same layout.

**Server API endpoints**

`GET /model`

Expected JSON fields:

```json
{
    "round": 0,
    "weights": []
}
```

In a real response, `weights` contains the complete 214-element global parameter vector. The empty array above illustrates the JSON structure only.

`POST /update`

Expected JSON fields:

```json
{
    "client_id": "A",
    "round": 0,
    "n_samples": 720,
    "weights": []
}
```

The sample count is illustrative. Actual requests must provide the correct count and complete 214-element weight vector.

**Initial FL settings**

- Feature configuration: `GRAV_NORM`.
- Server initializes and stores the round-0 global model.
- Both clients receive identical initial weights.
- Aggregation uses sample-weighted FedAvg.
- The server maintains round state in SQLite.
- The server validates incoming rounds and updates.

The FedAvg computation is:

```text
W_global = (nA * WA + nB * WB) / (nA + nB)
```

### Software and experiment responsibilities

| Owner / collaborators | Remaining work | Done when |
|---|---|---|
| **Yateeka (Android)** | Ensure the complete Android Studio/Gradle project is available on GitHub. Verify that the latest UI shows correct landscape orientations and all three dataset splits work. | Fresh build and unit tests pass; correct pose guidance appears |
| **Yateeka (data handling)** | Ensure old in-app recordings do not contaminate evaluation with reversed landscape labels. Preserve corrected CSVs and original backups. | Verified evaluation dataset with correct pose labels |
| **Both (Android setup)** | Verify both phones record all six poses and export correct CSVs. | Sample export and sensor readings from each phone |
| **Zihan (learning / FL server)** | Implement and validate FastAPI endpoints, sample-weighted FedAvg, SQLite persistence, round initialization, and update validation. | Tested API responses and a successful synchronous round |
| **Zihan + Yateeka (numerical check)** | Run the same MLP inputs and weights through Kotlin and NumPy and compare forward-pass outputs. | Outputs agree within approximately `1e-5` |
| **Zihan (networking)** | Configure Tailscale, run the coordinator on his laptop, and provide a verified IP address and port. | Both clients successfully connect |
| **Both (integration)** | Run the planned 10 federated rounds × 3 local epochs or document the actual settings used. | Logs for both clients and global rounds |
| **Both (local baselines)** | Train `BASELINE`, `GRAV_NORM`, and `ROBUST` on `train` data only and evaluate on the corrected six-pose `test` data. | Accuracy, macro-F1, per-class results, confusion matrices |
| **Zihan (analysis), both review** | Compare Client A, Client B, and FedAvg models, plus robustness and cross-device transfer. | Results tables and reproducible scripts |
| **Both** | Verify how many windows the ROBUST configuration rejects. | Retained/rejected sample counts reported |

### Current server status

In Zihan's email, he confirmed that he would implement:

- FastAPI `GET /model` and `POST /update`.
- Round-0 model initialization and storage.
- Sample-weighted FedAvg.
- SQLite persistence.
- Round validation.
- Tailscale networking.

**The server was not ready at the time of that email.**

Zihan said he would send the verified Tailscale IP and port the following day.

As of the supplied correspondence, no tested server address or successful two-client FL round has been confirmed.

**Do not mark the server or integration completed until verified.**

### Local baseline sequence

Once the training sessions are recorded:

1. Select `BASELINE` in the Train tab.
2. Train the local model.
3. Evaluate on the corrected six-pose test dataset.
4. Save results and screenshots.
5. Repeat for `GRAV_NORM`.
6. Repeat for `ROBUST`.

Record results before training the next model because the app may overwrite the current local model.

Do not use the old incorrectly labeled in-app test data for evaluation.

## 4. Results still needed for the CSC 8223 final report

The proposed system uses:

- A 6–16–6 multilayer perceptron.
- Cross-entropy loss and SGD.
- Sample-weighted FedAvg.
- Three feature configurations: `BASELINE`, `GRAV_NORM`, and `ROBUST`.
- A planned 10 communication rounds and 3 local epochs per round.

Final report tables must contain **actual experimental results**, not predicted values.

### Required experiments

- **Local-only comparison:** Client A and Client B accuracy, macro-F1, per-class performance, and confusion matrices using six-pose test data.
- **Federated comparison:** FedAvg global-model accuracy, macro-F1, and convergence across the completed rounds.
- **Feature comparison:** BASELINE versus GRAV_NORM versus ROBUST.
- **Robustness:** Performance on standard, tilted, and shaken recordings.
- **Cross-device:** Performance when applying a model to recordings from the other phone.
- **Implementation verification:** Server APIs, SQLite persistence, Kotlin–NumPy numerical consistency, and correct parameter ordering.
- **Reproducibility:** Actual device details, window counts, recording conditions, dataset splits, code version, and settings.
- **Live demonstration:** Two working Android clients, a reachable server, working local inference, and a federated round or reproducible full-round demonstration.

### Cross-device reporting note

Because the recordings are independent and not simultaneous, the report must acknowledge potential differences in:

- Recording location and time.
- Device model and sensor behavior.
- Phone placement.
- Surface conditions.
- Hand movement.

This should be documented as a limitation of cross-device comparisons.

### Sections Zihan still needs to write or verify

**[ZIHAN TO WRITE / VERIFY]**

1. FastAPI coordinator implementation and API behavior.
2. FedAvg aggregation and round management.
3. SQLite persistence and restart recovery.
4. Kotlin–NumPy forward-pass validation results.
5. Server configuration and tested Tailscale connection.
6. Client B dataset and device information.
7. Federated-training logs and convergence results.
8. Actual comparison metrics and confusion matrices.
9. Robustness and cross-device analysis.
10. Final discussion of model performance and limitations.

These items should be filled using actual implementation evidence and experiment results.

## 5. Handoff package checklist

### Yateeka → Zihan

- [x] Confirmed Client A assignment: labels 0, 1, 2.
- [x] Completed six Client A evaluation/robustness sessions (691 windows).
- [x] Corrected exported landscape labels in the six session-specific CSVs.
- [x] Documented test surfaces and collection procedures.
- [ ] Share all six corrected CSV files and untouched original backups.
- [ ] Collect `A_tr01`–`A_tr06`.
- [ ] Export and back up the Client A training dataset.
- [ ] Verify correct handling of old stored labels inside the Android app.
- [ ] Record device details and observed gravity-axis signs.
- [ ] Provide successful clean-build and test results.
- [ ] Run BASELINE / GRAV_NORM / ROBUST local baselines.
- [ ] Share local results, screenshots, and confusion matrices.

### Zihan → Yateeka

- [x] Confirmed Client B assignment: labels 3, 4, 5.
- [x] Agreed on the course's corrected landscape definitions.
- [x] Agreed to keep `train`, `test`, and `calib` separate.
- [x] Agreed on session naming.
- [x] Agreed on the MLP architecture and weight ordering.
- [x] Agreed on the API contract and initial GRAV_NORM configuration.
- [ ] Confirm Client B gravity-axis readings.
- [ ] Collect Client B training and evaluation/robustness datasets.
- [ ] Provide completed and tested FastAPI/FedAvg/SQLite server.
- [ ] Provide verified Tailscale IP and port.
- [ ] Complete numerical consistency tests.
- [ ] Provide analysis scripts and measured results.
- [ ] Share integration and demo instructions.

### Both → Final submission

- [ ] Working Android client on both phones.
- [ ] Verified federated server and communication.
- [ ] Two local-only baseline models.
- [ ] Federated global model.
- [ ] Feature-configuration results.
- [ ] Standard, tilt, shake, and cross-device evaluations.
- [ ] Reproducible GitHub repository and README.
- [ ] Final report with real metrics and citations.
- [ ] Completed shared presentation.
- [ ] Tested live demonstration.

### Presentation collaboration

Zihan offered to share an online PowerPoint presentation link.

Yateeka proposed Canva as an alternative so both teammates can edit the slides collaboratively.

**The final choice between Canva and PowerPoint Online is still pending.**

Suggested slide ownership:

| Slides | Responsible teammate |
|---|---|
| Android app and sensor collection | Yateeka |
| Dataset protocol and completed counts | Yateeka |
| Model architecture and numerical validation | Zihan |
| FastAPI, FedAvg, and SQLite | Zihan |
| Federated experiment results | Zihan, reviewed by both |
| Project motivation and conclusions | Both |
| Final demo | Both |

## 6. Milestone focus from the project design report

| Project milestone | Dates | Present focus |
|---|---|---|
| Android sensing/UI, local MLP, server | Oct 21–Nov 2 | Verify reproducible Android build and server implementation |
| Remote client + FedAvg integration | Nov 3–9 | Connect both clients and verify real round trips |
| Model comparisons, six poses | Nov 10–16 | Collect training datasets and obtain local/global metrics |
| Robustness + baselines | Nov 17–23 | Client A recordings complete; Client B collection and analysis pending |
| Final report, sources/README, demo rehearsal | Nov 24–30 | Complete results, documentation, presentation, and demonstration |

The dates above are the **original project milestones**, not proof that each milestone was completed on time.

### Immediate next actions

**Yateeka**

1. Start the six confirmed Client A training sessions using labels 0, 1, and 2.
2. Keep the corrected six-pose evaluation CSVs and original backups.
3. Verify that the Android app displays the corrected physical orientations.
4. Resolve or exclude the old mislabeled data stored inside the app.
5. Run three local-only baselines and save results.
6. Prepare Android and data-collection material for the final presentation.

**Zihan**

1. Verify Client B gravity-axis signs.
2. Collect the Client B dataset using the confirmed labels and protocol.
3. Complete and test the FastAPI server, FedAvg aggregation, and SQLite persistence.
4. Share the verified server IP and port.
5. Run Kotlin–NumPy numerical validation.
6. Prepare the experimental analysis and results sections.

**Both**

1. Connect the two Android clients to the coordinator.
2. Run federated-learning experiments.
3. Compare local-only and global-model performance.
4. Complete robustness and cross-device evaluation.
5. Agree on Canva or PowerPoint Online.
6. Finish the final report, slides, and live demonstration.

**Next team dependency:** Client A can begin training data collection immediately. Two-client federated integration must wait until Zihan's coordinator and networking are verified. The final results and report must be updated after experiments have actually been completed.
