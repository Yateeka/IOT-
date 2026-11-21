
# PoseFL Android Client

Android client for **Privacy-Preserving Smartphone Pose Classification Using Federated Learning**.

**Course:** CSC 8223  
**Team:** Yateeka Goyal and Zihan Tang

## 1. Project Overview

PoseFL recognizes six smartphone poses using gravity and accelerometer sensor measurements.

Two Android phones train models locally and participate in federated learning (FL). Only model parameters are exchanged with the laptop-hosted coordinator during FL training; raw training windows remain on each phone unless manually exported for analysis.

The Android app provides three tabs:

| Tab | Function |
|---|---|
| Collect | Record labeled sensor windows and export CSV files |
| Train | Train local models, participate in FL, and evaluate |
| Infer | Predict the current phone pose |

## 2. Correct Phone Pose Definitions

**IMPORTANT: Use the physical edge of the phone to identify each pose.**

The course assignment defines landscape poses by which side edge points upward.

| Label | Pose | Correct Physical Position | Expected Gravity |
|---|---|---|---|
| 0 | Screen Up | Screen faces upward | Z ≈ +9.8 |
| 1 | Screen Down | Screen faces downward | Z ≈ -9.8 |
| 2 | Portrait Up | Top edge points upward | Y ≈ +9.8 |
| 3 | Portrait Down | Top edge points downward | Y ≈ -9.8 |
| 4 | Landscape Left | LEFT side edge points UP; top edge points RIGHT | X ≈ -9.8 |
| 5 | Landscape Right | RIGHT side edge points UP; top edge points LEFT | X ≈ +9.8 |

Gravity readings are approximate values based on Client A's tested device. Verify the readings on Client B's phone before recording.

### Important Correction — November 21, 2026

An earlier data-collection guide accidentally reversed the physical instructions for Landscape Left and Landscape Right.

The old instructions were:

- Label 4: Top edge points LEFT.
- Label 5: Top edge points RIGHT.

The correct course definitions are:

- **Label 4: LEFT side edge UP, top edge RIGHT.**
- **Label 5: RIGHT side edge UP, top edge LEFT.**

Client A followed the original instructions when collecting the first six evaluation and robustness sessions.

Corrected CSV copies were created by swapping labels 4 and 5 while preserving the sensor measurements.

**Do not swap the already corrected CSV files again.**

Changing exported CSV files does not automatically update the data stored inside the Android application. The phone's stored records must be corrected or excluded before using the built-in evaluation feature.

## 3. Android Application

The application is written in Kotlin and uses Android SensorManager and Jetpack Compose.

### Main Components

| File | Purpose |
|---|---|
| `Config.kt` | Pose labels and constants |
| `Sensors.kt` | Gravity and accelerometer collection |
| `Features.kt` | Feature extraction |
| `Storage.kt` | Sensor-window storage and CSV export |
| `Mlp.kt` | Neural network |
| `Trainer.kt` | Local training and model evaluation |
| `FlClient.kt` | HTTP connection to FL coordinator |
| `AppVM.kt` | Application state and integration |
| `PoseFLScreen.kt` | Android application interface |
| `MainActivity.kt` | Android activity entry point |

The existing `POSES` array is correct and must retain indices 0–5.

## 4. Sensor Collection

The application samples gravity and accelerometer readings at a requested rate of approximately 50 Hz.

Samples are grouped into non-overlapping one-second windows.

For each window, the application calculates sensor summary statistics and stores one CSV row.

Each CSV row contains 17 columns:

1. Session ID
2. Dataset split
3. Pose label
4. Accelerometer mean X
5. Accelerometer mean Y
6. Accelerometer mean Z
7. Gravity mean X
8. Gravity mean Y
9. Gravity mean Z
10. Accelerometer median X
11. Accelerometer median Y
12. Accelerometer median Z
13. Gravity median X
14. Gravity median Y
15. Gravity median Z
16. Accelerometer magnitude standard deviation
17. Timestamp

CSV files have no header row.

### Dataset Splits

| Split | Purpose |
|---|---|
| `train` | Model training |
| `test` | Standard six-pose evaluation |
| `calib` | Calibration, tilt, and shake experiments |

**Only `train` windows are used for training.**

Never combine test or calibration windows with training data.

## 5. Client Assignments

The proposed non-IID training assignments are:

| Client | Assigned Training Poses |
|---|---|
| Client A — Yateeka | 0, 1, 2 |
| Client B — Zihan | 3, 4, 5 |

**Both teammates must confirm these assignments before collecting training data.**

Both phones record all six poses for evaluation.

## 6. Training Data Collection

Each client records six training sessions using only their three assigned poses.

### Client A

Session IDs:

- `A_tr01`
- `A_tr02`
- `A_tr03`
- `A_tr04`
- `A_tr05`
- `A_tr06`

Split: `train`

For each assigned pose in every session:

1. Select the correct pose label.
2. Tap Start Recording.
3. Record 20 windows.
4. Tap Stop.
5. Pick up and reposition the phone.
6. Record another 20 windows.
7. Tap Stop.
8. Continue to the next assigned pose.

Expected per session:

- 40 windows per pose
- 3 poses
- Approximately 120 windows

Expected total:

**6 sessions × 120 windows = approximately 720 training windows.**

### Client B

Use the same protocol with:

- `B_tr01` through `B_tr06`
- Split `train`
- Assigned labels 3, 4, and 5

## 7. Standard Test Data Collection

Each client records all six poses.

| Session | Surface or Condition | Split |
|---|---|---|
| `A_te01` / `B_te01` | Stable table | `test` |
| `A_te02` / `B_te02` | Hard notebook | `test` |
| `A_te03` / `B_te03` | Handheld, as steady as possible | `test` |

For each pose:

1. Record 10 windows.
2. Stop.
3. Reposition the phone.
4. Record another 10 windows.
5. Stop.
6. Continue to the next label.

Expected: approximately 20 windows per pose, or 120 windows per session.

## 8. Calibration Collection

Session IDs:

- Client A: `A_cal01`
- Client B: `B_cal01`

Split: `calib`

Record all six poses on a stable table or desk.

Record 15 windows per pose.

Expected total: 90 windows.

## 9. Tilt Collection

Session IDs:

- Client A: `A_tilt01`
- Client B: `B_tilt01`

Split: `calib`

Record all six poses with an approximately 10–20 degree tilt away from their ideal orientation.

Use stable support when possible.

Record 20 windows per pose.

Expected total: 120 windows.

## 10. Shake Collection

Session IDs:

- Client A: `A_shake01`
- Client B: `B_shake01`

Split: `calib`

Record all six poses while holding the phone with gentle natural hand movement.

Record 20 windows per pose.

Expected total: 120 windows.

## 11. Client A Completed Data Collection

Yateeka has completed six evaluation and robustness sessions.

| Session | Split | Windows | Condition |
|---|---|---:|---|
| `A_te01` | test | 121 | Table |
| `A_te02` | test | 120 | Hard notebook |
| `A_te03` | test | 120 | Handheld |
| `A_cal01` | calib | 90 | Stable table or desk |
| `A_tilt01` | calib | 120 | Supported tilt |
| `A_shake01` | calib | 120 | Gentle hand tremor |
| **Total** | | **691** | |

All six corrected session-specific CSV files were checked for consistent landscape labels.

An earlier tilt export contained an unrelated test row. The cleaned `A_tilt01` dataset contains 120 valid windows.

**These 691 windows are evaluation and robustness data, not training data.**

The six Client A training sessions remain to be collected.

## 12. Feature Configurations

Three feature configurations will be compared.

### BASELINE

Uses normalized accelerometer and gravity mean values.

### GRAV_NORM

Uses normalized gravity direction and gravity-removed acceleration.

### ROBUST

Uses median-based features and rejects windows exceeding the configured motion-variability threshold.

All three configurations must be evaluated using comparable datasets.

## 13. Local Neural Network

PoseFL uses a multilayer perceptron (MLP) with:

- 6 input neurons
- 16 ReLU hidden neurons
- 6 softmax output neurons
- Cross-entropy loss
- Stochastic gradient descent

The model has 214 parameters.

| Parameter | Shape | Offset |
|---|---|---:|
| W1 | 16 × 6 | 0 |
| b1 | 16 | 96 |
| W2 | 6 × 16 | 112 |
| b2 | 6 | 208 |

The Kotlin client and Python server must use the same weight ordering.

## 14. Local Training and Evaluation

After the training dataset has been collected and validated:

1. Open the Train tab.
2. Select `BASELINE`.
3. Train the local model.
4. Evaluate using the six-pose `test` dataset.
5. Record accuracy, macro-F1, per-class accuracy, and confusion matrix.
6. Repeat for `GRAV_NORM`.
7. Repeat for `ROBUST`.

**Before evaluating on Client A's phone, ensure the old in-app landscape labels have been corrected or excluded. Correcting exported CSVs alone is not sufficient.**

Save the model results and screenshots.

## 15. Federated Learning

The Python FastAPI coordinator is assigned to Zihan.

The planned server supports:

```text
GET /model
POST /update
```

Each federated round:

1. Both clients download the global model.
2. Both clients train locally using their own training windows.
3. Each client uploads its model weights and sample count.
4. The server calculates the weighted FedAvg model.
5. Both clients download the updated global model.

FedAvg:

```text
W_global = (nA * WA + nB * WB) / (nA + nB)
```

The planned experiment uses:

- 10 federated rounds
- 3 local epochs per round
- Learning rate 0.05

The server and final experiment settings must be confirmed by Zihan.

## 16. Results to Compare

The final report will compare:

- Client A local model
- Client B local model
- FedAvg global model
- BASELINE features
- GRAV_NORM features
- ROBUST features
- Standard, tilt, and shake performance
- Cross-device transfer

Required metrics:

- Overall accuracy
- Per-class accuracy
- Macro-F1
- Confusion matrices

Results should be added only after experiments are completed.

## 17. Current Project Status

| Task | Status |
|---|---|
| Client A evaluation data | Completed |
| Client A calibration | Completed |
| Client A tilt and shake recordings | Completed |
| Corrected CSV exports | Completed |
| Client A training sessions | Remaining |
| Client B data collection | Needs confirmation |
| Local model experiments | Remaining |
| FL server implementation | Zihan to confirm |
| Two-phone FL experiments | Remaining |
| Final report and demonstration | Remaining |

## 18. Important Data Safety Instructions

- Keep original CSV exports as backups.
- Do not swap already corrected landscape labels again.
- Do not use `test` or `calib` data for training.
- Do not uninstall the app or clear its data without backups.
- Remember that exported CSV corrections do not modify records stored inside the Android app.
- Use the course's corrected edge-up landscape definitions for all future recordings.

## 19. Team Responsibilities

### Yateeka Goyal

- Android application and sensor collection
- Pose labeling and data export
- Feature extraction and inference UI
- Android client integration
- Client A training data collection
- Client A local model evaluation
- Joint federated testing and demonstration

### Zihan Tang

- MLP algorithm validation
- FastAPI coordinator
- FedAvg aggregation
- SQLite persistence
- Tailscale networking
- Client B data collection
- Metrics, scripts, and experimental analysis
- Joint federated testing and demonstration

## 20. Repository

https://github.com/Yateeka/IOT-

Course: CSC 8223.
