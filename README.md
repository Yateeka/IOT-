# PoseFL Android Client

Android client for **Privacy-Preserving Smartphone Pose Classification Using Federated Learning** (CSC 8223 course project, Zihan Tang and Yateeka Goyal).

The app reads the phone's gravity and accelerometer sensors, trains a small neural network on-device, and takes part in federated learning with a laptop-hosted FastAPI server. Raw sensor data never leaves the phone. Only model weights are uploaded.

## What the app does

| Tab | Purpose |
|---|---|
| **Collect** | Record labeled 1-second sensor windows for one of six phone poses, save them to a local CSV, and export it. |
| **Train** | Train a local-only baseline model, run one federated round, refresh the global model, and evaluate on test data. |
| **Infer** | Live pose prediction with per-class probability bars, using either the local or the global model. |

### The six poses

| Label | Pose | Expected gravity |
|---|---|---|
| 0 | Screen Up | z about +9.8 |
| 1 | Screen Down | z about -9.8 |
| 2 | Portrait Up | y about +9.8 |
| 3 | Portrait Down | y about -9.8 |
| 4 | Landscape Left (LEFT edge UP; top edge RIGHT) | x about -9.8 |
| 5 | Landscape Right (RIGHT edge UP; top edge LEFT) | x about +9.8 |

The landscape signs can differ between phone models. Verify on your device and record what you observe.

## How it works

1. **Sensing.** `SensorCollector` registers the gravity and accelerometer sensors at a 20,000 microsecond period (about 50 Hz, a hint rather than a guarantee). If the phone has no gravity sensor, a low-pass filter on the accelerometer is used.
2. **Windowing.** `WindowBuilder` makes non-overlapping 1-second windows, so adjacent windows cannot leak between train and test.
3. **Features.** Each window is stored as raw summaries (means, medians, accelerometer std). Six model inputs are computed from those on demand, using one of three configurations:
   - `BASELINE`: accel mean and gravity mean, each divided by 9.81.
   - `GRAV_NORM`: unit gravity vector plus gravity-removed acceleration divided by 9.81.
   - `ROBUST`: like `GRAV_NORM` but with medians, and windows with accelerometer std above `STD_MAX` are rejected.
4. **Model.** An MLP with 6 inputs, 16 ReLU hidden units, and 6 softmax outputs, trained with cross-entropy and per-sample SGD in plain Kotlin (no ML library).
5. **Federated learning.** Each round, the phone downloads the global weights, trains 3 local epochs on its own `train` windows, and uploads its weights and sample count. The server aggregates with FedAvg:

   `W_global = (nA * WA + nB * WB) / (nA + nB)`

## Model weight layout (shared contract with the server)

The model is a flat array of **214 floats**. Kotlin and NumPy must use exactly this order.

| Part | Shape | Offset | Length |
|---|---|---|---|
| `W1` | 16 x 6, row-major | 0 | 96 |
| `b1` | 16 | 96 | 16 |
| `W2` | 6 x 16, row-major | 112 | 96 |
| `b2` | 6 | 208 | 6 |

## Server API

| Endpoint | Request | Response |
|---|---|---|
| `GET /model` | none | `{"round": 3, "weights": [214 floats]}` |
| `POST /update` | `{"client_id": "A", "round": 3, "n_samples": 240, "weights": [214 floats]}` | HTTP 200 on success |

In round 0 the server should return initial random weights so both clients start from the same point.

## Requirements

- Android Studio (current stable) with Android SDK, minimum SDK 26
- A real Android phone with USB debugging enabled (the emulator has no real sensor data)
- [Tailscale](https://tailscale.com/) on the phone, signed in to the same tailnet as the laptop running the server
- The FL server running and reachable at `http://<laptop-tailscale-ip>:8000`

## Project layout

All source files are in `app/src/main/java/com/csc8223/posefl/`.

| File | Contents |
|---|---|
| `Config.kt` | Pose names, feature config enum, constants |
| `Features.kt` | `Sample` data class, window summaries, feature vectors |
| `Sensors.kt` | `SensorCollector` and `WindowBuilder` |
| `Storage.kt` | CSV session store, CSV export, model file store |
| `Mlp.kt` | MLP forward pass, backprop, SGD step |
| `Trainer.kt` | Training loop, confusion matrix, accuracy, macro-F1 |
| `FlClient.kt` | OkHttp client for `/model` and `/update` |
| `AppVM.kt` | ViewModel connecting sensors, storage, training, and networking |
| `MainActivity.kt` | Jetpack Compose UI (Collect, Train, Infer tabs) |

Tests are in `app/src/test/java/com/csc8223/posefl/MlpTest.kt`.

## Setup

1. Clone the repository and open it in Android Studio. Let Gradle sync finish.
2. Edit `app/src/main/res/xml/network_security_config.xml` and replace `100.x.y.z` with the server laptop's Tailscale IP. Android blocks cleartext HTTP unless the address is listed.
3. On the phone, enable Developer options (tap Build number 7 times) and turn on USB debugging.
4. Connect the phone by USB, accept the RSA prompt, select it in Android Studio, and press **Run**.
5. On the phone, open `http://<laptop-tailscale-ip>:8000/docs` in the browser to confirm the server is reachable.
6. In the app's Train tab, enter the same server URL and set the client ID (`A` or `B`).

## Usage

### 1. Collect data

1. Open the **Collect** tab. Set a Session ID (for example `A_tr01`), choose a split (`train`, `test`, or `calib`), and choose a pose label.
2. Tap **Start recording**, place the phone in the pose, and keep it still. Tap **Stop recording** when done.
3. Re-place the phone and repeat, then change the pose and repeat.
4. Tap **Export CSV** at the end of every collecting day.

Rules that keep the evaluation honest:

- One sitting uses one Session ID and one split. Never share a session between train and test.
- Training uses only windows tagged `train`. Evaluation uses only windows tagged `test`.
- Change the Session ID for every new sitting.

Suggested session names: `A_tr01` to `A_tr06` (training), `A_te01` to `A_te03` (standard test), `A_cal01` (calibration), `A_tilt01` (tilted poses), `A_shake01` (shaky poses). Use `B_` on the second phone. Tilt, shake, and cross-device sittings are separated by session name during offline analysis.

### 2. Train and evaluate

On the **Train** tab:

1. Choose a feature config. Both clients must use the same config during an FL run.
2. **Train local-only model** trains for 30 epochs (equal to 10 rounds of 3 epochs) from a fixed seed.
3. **Eval local** reports accuracy, per-class accuracy, macro-F1, and the confusion matrix on `test` windows.

### 3. Federated rounds

1. Both phones tap **Run FL round**. Each downloads the global weights, trains 3 epochs, and uploads.
2. When the server has logged both updates and advanced the round, both phones tap **Refresh global model from server**.
3. Repeat for 10 rounds, then tap **Eval global**.

### 4. Live prediction

On the **Infer** tab, choose `global` or `local`, choose the feature config, and tap **Start live prediction**. The prediction updates once per second.

## Data format

`windows.csv` (in the app's private storage, exported with the Export CSV button) has **no header row** and 17 columns:

| Columns | Content |
|---|---|
| 1 to 3 | session ID, split, label (0 to 5) |
| 4 to 6 | accelerometer mean x, y, z |
| 7 to 9 | gravity mean x, y, z |
| 10 to 12 | accelerometer median x, y, z |
| 13 to 15 | gravity median x, y, z |
| 16 | accelerometer magnitude std |
| 17 | timestamp (milliseconds) |

To pull the file from a debug build using a computer:

```
adb exec-out run-as com.csc8223.posefl cat files/windows.csv > windows_A.csv
```

## Reproducing the experiments

For each feature config (`BASELINE`, `GRAV_NORM`, `ROBUST`), using the same collected data:

1. Train and evaluate the local-only model on each phone.
2. Run 10 FL rounds with 3 local epochs per round, then evaluate the global model on each phone.
3. Compare the three models on the same six-pose test windows using overall accuracy, per-class accuracy, macro-F1, and confusion matrices.
4. For robustness, repeat evaluation on the tilt and shake sessions, and on the other phone's test recordings for cross-device transfer.

Hyperparameters used: learning rate 0.05, per-sample SGD, 10 rounds, 3 local epochs per round, 30 epochs for local-only baselines. Adjust after pilot tests and report any changes.

## Testing

Run the unit tests from Android Studio (right-click `MlpTest` and choose Run) or with:

```
./gradlew test
```

They check that softmax outputs sum to 1, that backprop matches finite differences, that the model can overfit one sample, and that a flat screen-up window gives a unit z gravity vector.

Before the first federated run, also confirm the Kotlin and server NumPy forward passes agree to about 1e-5 for the same weights and a fixed 6-number input. This catches any weight-layout mismatch.

## Troubleshooting

| Problem | Fix |
|---|---|
| `CLEARTEXT communication not permitted` | The server IP is missing from `network_security_config.xml`, or differs from the URL in the app. |
| Every request times out | Tailscale is off on the phone, or the server is not bound to `0.0.0.0`. Test the URL in the phone browser. |
| `Expected 214 weights` | The server uses a different weight layout. Re-check the layout table above. |
| `No train data for ROBUST` | `STD_MAX` rejected all windows. Raise it or record with the phone resting still. |
| Gravity readout is zero | The phone has no gravity sensor. Move the phone slightly so the fallback low-pass filter warms up. |
| Test accuracy is suspiciously perfect | Train and test windows probably came from the same sitting. Use separate sessions. |
| Phone not detected by Android Studio | Try another cable (many are charge-only), check USB mode, accept the RSA prompt, and run `adb devices`. |

## Data loss warning

The **Clear data** button, uninstalling the app, and clearing storage in Android settings all delete the collected CSV and saved models. Export before doing any of these.

## Team

| Member | Primary ownership |
|---|---|
| Yateeka Goyal | Android client: sensors, collection UI, feature extraction, inference UI, HTTP client, on-device integration and testing |
| Zihan Tang | Learning and FL server: MLP validation, FastAPI, FedAvg, SQLite persistence, Tailscale setup, metrics and analysis |

Course: CSC 8223.
