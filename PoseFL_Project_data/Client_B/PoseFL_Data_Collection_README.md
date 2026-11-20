# PoseFL — Teammate Data Collection Guide

This README explains how to collect a comparable set of phone-sensor recordings to Client A's recordings. **Please match the physical surfaces and holding styles used by Client A as closely as possible.** This helps reduce differences caused by placement when we compare phones.

> **Before starting:** Agree on the physical definitions of Landscape Left and Landscape Right. For Standard Test 2, Client A used a **hard notebook**; use a similarly firm, flat hardcover notebook and keep the phone stable.

## 1. What you will record

For the six-pose evaluation and robustness recordings, use **your own client prefix** (usually `B_` if you are Client B) and the same suffixes, splits, labels, and counts as below. Do **not** reuse Client A's `A_` prefix on your phone.

| Sitting | Your session ID (Client B example) | App split | Windows per pose | Total windows | Match Client A's setup |
|---|---|---|---:|---:|---|
| Standard Test 1 | `B_te01` | `test` | 2 runs × 10 | 120 | **Table**; reposition slightly between runs |
| Standard Test 2 | `B_te02` | `test` | 2 runs × 10 | 120 | **Hard notebook** (firm, flat surface) |
| Standard Test 3 | `B_te03` | `test` | 2 runs × 10 | 120 | **Handheld**, steady in the intended pose |
| Calibration | `B_cal01` | `calib` | 1 run × 15 | 90 | **Stable table/desk**, phone as still as possible |
| Tilt | `B_tilt01` | `calib` | 1 run × 20 | 120 | **Table/desk with support**; 10–20° tilt |
| Shake | `B_shake01` | `calib` | 1 run × 20 | 120 | **Handheld**, natural mild tremor |

**Target:** 690 windows (6 × 20 × 3 + 6 × 15 + 6 × 20 + 6 × 20). Minor differences of one window are not necessarily a reason to re-record; write down actual counts. Client A collected **691** windows (Test 1 had one extra window).

**Timing:** At the project's nominal one-second window length, each 10-, 15-, or 20-window run takes approximately 10, 15, or 20 seconds of accepted data, plus setup and repositioning. Expect roughly **3–6 minutes** per standard sitting, **2–4 minutes** for calibration, and **3–6 minutes** each for tilt and shake. **Always stop by the app's window counter, not a stopwatch.**

## 2. Pose labels — use these for every sitting

| Label | Pose | Physical orientation | Expected main gravity axis* |
|---:|---|---|---|
| 0 | Screen Up | Phone flat, screen toward ceiling | z ≈ +9.8 |
| 1 | Screen Down | Phone flat, screen toward floor | z ≈ −9.8 |
| 2 | Portrait Up | Upright, top edge up | y ≈ +9.8 |
| 3 | Portrait Down | Upside down, top edge down | y ≈ −9.8 |
| 4 | Landscape Left | Sideways, **top edge pointing left** | x ≈ +9.8 |
| 5 | Landscape Right | Sideways, **top edge pointing right** | x ≈ −9.8 |

\*These are the project's working conventions. Check the live gravity readout on **your own device**, especially landscape signs; coordinate/sign conventions must agree across phones before combining data. For a 10–20° tilt, the dominant-axis magnitude is roughly 9.2–9.7 and a secondary axis is roughly 1.7–3.4 in absolute value.

## 3. Before each sitting

1. Charge phone (preferably above 50%), switch on Do Not Disturb, and keep the phone case setup consistent.
2. Open **PoseFL → Collect**. Confirm the gravity values change when moving the phone.
3. Enter the **exact session ID** for the sitting and select the **correct split** (`test` or `calib`).
4. Check the pose label **before** pressing **Start Recording**.
5. Record on the agreed surface and note the room, surface, whether supported/handheld, and date/time.
6. **Do not press Clear Data** between sittings. Old totals may remain visible; session IDs identify the new recordings.

### Example: Standard Test 1 (`B_te01`) on the table

1. Set Session ID `B_te01`, split `test`, label `0 — Screen Up`.
2. Put the phone **screen-up on the table**, tap **Start Recording**, collect **10 new windows**, then **Stop**.
3. Lift the phone and place it **about 1–2 inches away on the same table**, still screen-up. Record **10 more new windows**, then **Stop**. You now have **20 windows for label 0**.
4. Change only the pose label to `1 — Screen Down`. Repeat the same two runs of 10 windows.
5. Continue through labels `2`, `3`, `4`, and `5` (20 windows each), keeping the phone steady. Use stable support where needed for upright/sideways poses.
6. **Do not change the session ID between labels.** The whole sitting is one session.

### Standard Test 2 (`B_te02`)

Repeat the same **two runs of 10 windows per pose** using a **hard notebook**, as Client A did for `A_te02`. Place the notebook on a stable surface. For flat poses, rest the phone directly on the hard notebook; for upright/sideways poses, hold it steadily or use a stand. Between the two runs for each label, lift and reposition the phone slightly on the same notebook (or reset your grip/support while keeping the same orientation). Note the notebook and support in the log.

### Standard Test 3 (`B_te03`)

Repeat the same **two runs of 10 windows per pose** while **holding the phone in your hand**, as Client A did. Keep each orientation steady, without deliberate shaking or tilting. Lower/relax the hand and re-establish the pose between runs.

### Calibration (`B_cal01`)

On a **stable table/desk**, record **one continuous 15-window run per pose** (labels 0–5). Keep the phone still and correctly aligned. **Do not** make a second run for a pose. Total target: **90 windows**. Split must be `calib`.

### Tilt (`B_tilt01`)

On a table/desk, use folded paper, a book edge, or a stand to hold the phone **10–20° off the ideal pose**, keeping the intended label. Record **one 20-window run per pose**. Use the live gravity readout to confirm tilt: a secondary axis should be nonzero. **Hold the tilt steady**; this is not a shake test. Total: **120 windows**, split `calib`.

### Shake (`B_shake01`)

Hold the phone in the correct orientation and allow **natural mild hand tremor** or small disturbances. Do not flip, wave, or violently shake it. Record **one 20-window run per pose**. Total: **120 windows**, split `calib`. Some windows may later be rejected by the ROBUST feature configuration; keep the original export.

## 4. Export and filenames

- After each full sitting, press **Stop** and check the recorded counts.
- Tap **Export CSV** and save a copy to a computer, plus a second backup.
- Suggested file names: `B_te01_YYYY-MM-DD.csv`, `B_te02_YYYY-MM-DD.csv`, `B_te03_YYYY-MM-DD.csv`, `B_cal01_YYYY-MM-DD.csv`, `B_tilt01_YYYY-MM-DD.csv`, and `B_shake01_YYYY-MM-DD.csv`.
- **Verify these are actual CSV files**, not Apple Numbers files renamed with `.csv` or files ending `.csv.csv`.
- The app may export **all previous sessions together**. Save that full export as a backup; make individual per-session CSVs by filtering the **session ID column**, without deleting original data. Check that each row in a session has the correct split and one of labels 0–5.
- Never tap **Clear Data**, uninstall the app, or reinstall without confirming backups are readable.

## 5. Recording log (fill in after each sitting)

| Session ID | Date / time | Surface / room / support | Split | Per-label counts (0,1,2,3,4,5) | Notes |
|---|---|---|---|---|---|
| `B_te01` | | Table: ______ | test | | |
| `B_te02` | | Hard notebook: ______ | test | | |
| `B_te03` | | Handheld: ______ | test | | |
| `B_cal01` | | Stable table/desk: ______ | calib | | |
| `B_tilt01` | | Table + tilt support: ______ | calib | | |
| `B_shake01` | | Handheld: ______ | calib | | |

## 6. Training is a separate task

Do **not** use these six-pose `test` or `calib` sessions as training data. The project's non-IID training protocol assigns **only three poses** to each client. Before recording training sessions, confirm with the team which client/labels you are assigned and the target training-session protocol. Keep training split `train` and training session IDs separate from the six evaluation/robustness sittings.

## 7. Why we match surfaces

Recordings made with different devices can also differ because of the surface, support, or movement. Using the **same type of surface and holding method** as Client A gives a more meaningful comparison between the two phones. We are recording **separately**, not simultaneously, so report that remaining placement/timing differences can still affect the comparison.

**Surface match confirmed:** Client A used a **hard notebook** for `A_te02`. Please use a comparably rigid notebook for `B_te02`.
