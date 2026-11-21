
# CSC 8223 — PoseFL Project Handoff Notes

**Project:** Privacy-Preserving Smartphone Pose Classification Using Federated Learning

**Team:** Yateeka Goyal and Zihan Tang

**Updated:** November 21, 2026

## 1. Team Responsibilities

| Member | Primary Responsibility |
|---|---|
| Yateeka Goyal | Android client, sensor collection, feature extraction, inference UI, on-device integration |
| Zihan Tang | MLP numerical validation, FastAPI server, FedAvg, SQLite persistence, metrics and analysis |
| Both | Data collection, model experiments, integration, final report and demonstration |

## 2. Important Pose Definition Correction

The course assignment defines landscape orientation by **which side edge points upward**.

### Correct Definitions

**Label 4 — Landscape Left**

- LEFT side edge points UP.
- Top edge points RIGHT.
- Expected gravity X on Client A: approximately -9.8 m/s².

**Label 5 — Landscape Right**

- RIGHT side edge points UP.
- Top edge points LEFT.
- Expected gravity X on Client A: approximately +9.8 m/s².

### Previous Recording Issue

The earlier guide used the opposite physical definitions.

Client A collected the first six evaluation and robustness sessions using those earlier instructions.

Corrected exported CSV files have been checked with labels 4 and 5 exchanged.

**Do not swap the labels again in corrected files.**

The sensor readings themselves remain unchanged.

The Android app's internally stored data may still contain the old mappings.

Before running local or global evaluations on the phone, correct or exclude the affected stored records.

All future recordings must use the course's physical edge-up definitions.

Both phones must verify their observed gravity signs.

## 3. Yateeka — Completed Work

Client A completed six evaluation and robustness sessions.

| Session | Dataset Split | Windows | Recording Condition | Status |
|---|---|---:|---|---|
| A_te01 | test | 121 | Stable table | Complete |
| A_te02 | test | 120 | Hard notebook | Complete |
| A_te03 | test | 120 | Handheld | Complete |
| A_cal01 | calib | 90 | Stable desk/table | Complete |
| A_tilt01 | calib | 120 | Approximately 10–20° tilt | Complete |
| A_shake01 | calib | 120 | Gentle hand tremor | Complete |
| **Total** | | **691** | | |

The completed recordings cover all six pose labels.

### CSV Verification

The session-specific files were checked for:

- Correct session IDs
- Correct dataset splits
- Six pose classes
- Expected window counts
- 17 columns per CSV row
- Corrected landscape gravity-axis signs

An unrelated row from an earlier test was removed from the cleaned tilt export.

**The 691 windows are reserved for evaluation and robustness experiments, not training.**

## 4. How the Data Was Collected

The Android app uses gravity and accelerometer sensors.

Sensor readings are summarized into approximately one-second windows.

### Standard Test Sessions

For each of the six poses:

1. Select the correct pose label.
2. Record 10 windows.
3. Stop recording.
4. Pick up and reposition the phone.
5. Record another 10 windows.
6. Stop and continue to the next label.

Recording conditions:

| Session | Condition |
|---|---|
| te01 | Stable table |
| te02 | Hard notebook |
| te03 | Handheld, held as steady as practical |

### Calibration

Record 15 windows for each pose on a stable surface.

### Tilt

Record 20 windows per pose with an approximately 10–20 degree tilt.

### Shake

Record 20 windows per pose with gentle natural hand movement.

### Dataset Separation

- Training data uses `train`.
- Standard test data uses `test`.
- Calibration, tilt, and shake use `calib`.

Each sitting has a unique Session ID and one split.

Never train on `test` or `calib` data.

## 5. Yateeka — Remaining Tasks

### Task A: Confirm Training Assignment

Confirm with Zihan that:

- Client A trains on labels 0, 1, 2.
- Client B trains on labels 3, 4, 5.

This must be agreed upon before training collection.

### Task B: Collect Client A Training Data

Record:

- `A_tr01`
- `A_tr02`
- `A_tr03`
- `A_tr04`
- `A_tr05`
- `A_tr06`

Split: `train`

For each assigned pose, in each session:

- Record 20 windows.
- Stop.
- Reposition the phone.
- Record another 20 windows.

Expected totals:

- 40 windows per pose per sitting
- Approximately 120 windows per sitting
- Approximately 720 training windows across six sittings

### Task C: Verify Android Application

- Confirm the corrected physical pose instructions appear in the Collect UI.
- Confirm `train`, `test`, and `calib` splits are saved correctly.
- Confirm CSV exports work.
- Verify that the GitHub repository builds correctly.
- Run the existing tests.

### Task D: Run Local Baselines

Train and evaluate:

1. BASELINE
2. GRAV_NORM
3. ROBUST

Record:

- Training windows used
- Training loss
- Overall accuracy
- Per-class accuracy
- Macro-F1
- Confusion matrices

**Important:** Do not evaluate against old mislabeled records inside the app. Correct or exclude the affected records first.

### Task E: Federated Integration

Once Zihan's server is ready:

- Configure the correct Tailscale IP and server port.
- Verify connectivity.
- Run federated rounds with both devices.
- Download the updated global model.
- Evaluate global inference.
- Save logs and results.

### Task F: Final Report and Demonstration

- Document the Android implementation.
- Share corrected CSV exports and recording notes.
- Add actual evaluation results.
- Help test the two-phone demonstration.
- Review final project documentation.

## 6. Zihan — Remaining Tasks and Confirmation

The following responsibilities are assigned to Zihan. Their current implementation status must be confirmed by Zihan.

### Client B Data Collection

Record:

- `B_tr01` through `B_tr06`
- `B_te01` through `B_te03`
- `B_cal01`
- `B_tilt01`
- `B_shake01`

Training uses only the assigned Client B labels.

Evaluation uses all six pose labels.

The standard evaluation conditions should match Client A's table, hard notebook, and handheld sessions.

### Learning Algorithm Validation

Verify:

- MLP architecture: 6–16–6
- 214-parameter weight layout
- Kotlin and NumPy numerical consistency
- Forward-pass results
- Gradient calculations
- Training behavior

### Federated Server

Confirm or implement:

- Python FastAPI server
- `GET /model`
- `POST /update`
- FedAvg aggregation
- Correct sample-count weighting
- SQLite persistence
- Synchronous round handling
- Server restart recovery

### Networking

Provide:

- Working Tailscale setup
- Server IP and port
- Connection instructions
- Verification that both phones can reach the server

### Experiment Analysis

Prepare scripts and results for:

- Client A local model
- Client B local model
- FedAvg global model
- Three feature configurations
- Standard test accuracy
- Tilt performance
- Shake performance
- Cross-device evaluation
- Confusion matrices and macro-F1

**Zihan should fill in the actual implementation status and measured results. Do not mark these complete without verification.**

## 7. Federated Learning Experiment Plan

The project proposes a 6-input, 16-hidden-neuron, 6-output MLP.

Planned training parameters:

| Parameter | Planned Value |
|---|---|
| Federated rounds | 10 |
| Local epochs per round | 3 |
| Local-only epochs | 30 |
| Learning rate | 0.05 |
| Aggregation method | FedAvg |

Both clients download the same global model, train on separate local datasets, and submit weights.

The coordinator combines the updates using:

```text
W_global = (nA * WA + nB * WB) / (nA + nB)
```

The server's working implementation and final experimental settings must be verified by Zihan.

## 8. Results Needed for the Final Report

The report must include actual measurements for the following comparisons.

| Experiment | Required Results | Current Status |
|---|---|---|
| Client A local model | Accuracy, macro-F1, per-class results | Pending |
| Client B local model | Accuracy, macro-F1, per-class results | Pending |
| FedAvg global model | Accuracy, macro-F1, confusion matrix | Pending |
| BASELINE features | Evaluation results | Pending |
| GRAV_NORM features | Evaluation results | Pending |
| ROBUST features | Evaluation results and rejected-window count | Pending |
| Tilt robustness | Accuracy under tilt | Pending |
| Shake robustness | Accuracy under movement | Pending |
| Cross-device transfer | Results using the other phone's data | Pending |

Do not invent accuracy values or model results before these experiments are run.

## 9. Client A Handoff Checklist

- [ ] Six corrected evaluation and robustness CSV files
- [ ] Backup copies of original CSV exports
- [ ] Client A training CSV files after collection
- [ ] Session IDs, window counts, and surface notes
- [ ] Confirmed gravity-axis readings
- [ ] Android phone model and software version
- [ ] Successful project build and test results
- [ ] Local baseline results and screenshots
- [ ] Notes about excluded or corrected data

## 10. Client B Handoff Checklist

- [ ] Confirm Client A/B training-label assignments
- [ ] Verify edge-up landscape definitions
- [ ] Provide Client B recorded datasets
- [ ] Provide server address and port
- [ ] Confirm FastAPI and FedAvg implementation
- [ ] Confirm SQLite persistence
- [ ] Provide MLP numerical validation
- [ ] Provide experiment scripts and metrics
- [ ] Help complete two-phone integration

## 11. Final Submission Checklist

- [ ] Two working Android clients
- [ ] Reproducible source-code repository
- [ ] Working federated coordinator
- [ ] Two local model baselines
- [ ] Federated global model
- [ ] Three feature-configuration comparisons
- [ ] Standard, tilt, shake, and cross-device results
- [ ] Completed IEEE report with measured results
- [ ] Updated project README
- [ ] Tested live demonstration

## 12. Project Timeline

| Dates | Planned Work |
|---|---|
| Oct 21–Nov 2 | Android sensing, model development, server |
| Nov 3–9 | Two-client federated integration |
| Nov 10–16 | Local and global model comparison |
| Nov 17–23 | Robustness tests and feature comparisons |
| Nov 24–30 | Final report, source code, README, demo rehearsal |

## 13. Immediate Next Steps

**Yateeka**

1. Confirm the Client A training assignment.
2. Record six training sessions.
3. Verify the corrected app instructions and stored-label handling.
4. Run the local model baselines.
5. Prepare files and results for Zihan.

**Zihan**

1. Confirm training assignments and physical pose definitions.
2. Complete or confirm Client B collection.
3. Confirm server and model-validation status.
4. Provide working server connection details.
5. Prepare metrics and analysis tools.

**Both**

Run the federated experiments and complete the final report and demonstration.

## 14. Repository

https://github.com/Yateeka/IOT-

**Course:** CSC 8223

**Project:** Privacy-Preserving Smartphone Pose Classification Using Federated Learning
