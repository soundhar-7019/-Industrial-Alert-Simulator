# Industrial Alert Simulator

A Java console application that simulates a **sensor monitoring system** inspired by real power plant control systems — specifically the kind used in **NCTPS-2 (North Chennai Thermal Power Station, Unit 2)**.

---

## 📌 Inspiration: NCTPS-2

The **North Chennai Thermal Power Station Unit 2** is a 600 MW coal-fired power plant operated by TANGEDCO (Tamil Nadu Generation and Distribution Corporation). Like all modern thermal plants, it runs a **DCS (Distributed Control System)** that continuously monitors hundreds of sensors across the boiler, turbine, and generator systems.

Key parameters monitored in real plants include:
- **Main Steam Temperature** — must stay within ~300–540°C
- **Main Steam Pressure** — typically 150–170 bar
- **Generator Load (MW)** — managed against grid demand

Operators watch these parameters on alarm screens governed by **ISA-18.2 alarm management standards**, with tiered alerts (Advisory → Warning → Critical). This simulator replicates that three-tier structure in a lightweight Java console app.

---

## ✨ Features

| Feature | Details |
|---|---|
| **3 Simulated Sensors** | Temperature (°C), Pressure (bar), Load (MW) |
| **Realistic ranges** | Based on 600 MW thermal plant operating parameters |
| **Tiered alerts** | NORMAL → WARNING → CRITICAL |
| **Color-coded console** | 🟢 Green / 🟡 Yellow / 🔴 Red with bold alarm banners |
| **File logging** | All readings + alerts saved to timestamped `.txt` file |
| **Graceful shutdown** | Ctrl+C writes session footer and closes log cleanly |
| **OOP design** | `Sensor`, `AlertManager`, `Logger`, `Main` — fully separated |

---

## 📁 Project Structure

```
IndustrialAlertSimulator/
├── src/
│   └── com/
│       └── simulator/
│           ├── AlertLevel.java     ← Enum: NORMAL, WARNING, CRITICAL
│           ├── Sensor.java         ← Models a physical field sensor
│           ├── AlertManager.java   ← Evaluates thresholds, prints colored alerts
│           ├── Logger.java         ← Writes timestamped readings to .txt log
│           └── Main.java           ← Entry point + main polling loop
├── logs/                           ← Auto-created log files go here
└── README.md
```

---

## 🚀 How to Run

### Prerequisites
- **Java 8+** installed (`java -version` to check)
- Terminal with **ANSI color support**
  - ✅ Linux / macOS Terminal — works natively
  - ✅ Windows Terminal / VS Code terminal — works natively
  - ⚠️ Old Windows `cmd.exe` — colors may not render (use Windows Terminal instead)

### Steps

**1. Clone or download the project**
```bash
cd IndustrialAlertSimulator
```

**2. Create output directories**
```bash
mkdir -p out logs
```

**3. Compile all Java source files**
```bash
javac -d out src/com/simulator/*.java
```

**4. Run the simulator**
```bash
java -cp out com.simulator.Main
```

**5. Stop the simulator**
```
Press Ctrl+C
```

The log file will be saved in the `logs/` directory as `sensor_log_YYYY-MM-DD.txt`.

---

## 📊 Sensor Thresholds

| Sensor | Unit | Operating Range | ⚠ Warning | ✖ Critical |
|---|---|---|---|---|
| Temperature | °C | 280 – 545 | ≥ 510 | ≥ 535 |
| Pressure | bar | 140 – 175 | ≥ 165 | ≥ 172 |
| Load | MW | 100 – 595 | ≥ 550 | ≥ 580 |

---

## 🖥 Sample Console Output

```
╔══════════════════════════════════════════════════════════════════╗
║         INDUSTRIAL ALERT SIMULATOR — NCTPS-2 INSPIRED            ║
║         Power Plant Sensor Monitoring System v1.0                ║
╠══════════════════════════════════════════════════════════════════╣
║  Sensor          | Unit | Warn Threshold | Critical Threshold    ║
╠══════════════════════════════════════════════════════════════════╣
║  Temperature     | °C   | 510.0          | 535.0                 ║
║  Pressure        | bar  | 165.0          | 172.0                 ║
║  Load            | MW   | 550.0          | 580.0                 ║
╚══════════════════════════════════════════════════════════════════╝

─── Cycle #1    ────────────────────────────────────────────
[2026-05-03 10:45:01] Temperature      432.18 °C    [NORMAL]
[2026-05-03 10:45:01] Pressure         163.45 bar   [NORMAL]
[2026-05-03 10:45:01] Load             557.92 MW    [WARNING]  → Exceeds threshold 550.00 MW

╔══════════════════════════════════════════════════════════╗
║  [2026-05-03 10:45:03] ✖ CRITICAL ALARM                  ║
║  Sensor  : Temperature                                   ║
║  Reading : 537.81 °C    (Critical limit: 535.00 °C  )    ║
╚══════════════════════════════════════════════════════════╝
```

---

## 📄 Sample Log File (`logs/sensor_log_2026-05-03.txt`)

```
═══════════════════════════════════════════════════════
  SESSION STARTED: 2026-05-03 10:45:00
  Industrial Alert Simulator | NCTPS-2 Inspired
═══════════════════════════════════════════════════════
[2026-05-03 10:45:01] Temperature     |   432.18 °C   | NORMAL
[2026-05-03 10:45:01] Pressure        |   163.45 bar  | NORMAL
[2026-05-03 10:45:01] Load            |   557.92 MW   | WARNING
!!! ALERT → [2026-05-03 10:45:01] ⚠ WARNING  | Load = 557.92 MW (threshold: 550.00)
[2026-05-03 10:45:03] Temperature     |   537.81 °C   | CRITICAL
!!! ALERT → [2026-05-03 10:45:03] ✖ CRITICAL | Temperature = 537.81 °C (threshold: 535.00)
───────────────────────────────────────────────────────
  SESSION ENDED: 2026-05-03 10:45:10
───────────────────────────────────────────────────────
```

---

## 🔧 OOP Design

| Class | Responsibility |
|---|---|
| `AlertLevel` | Enum — defines the 3 alert states |
| `Sensor` | Models a single field sensor with range, thresholds, and reading simulation |
| `AlertManager` | Evaluates sensor state, prints color-coded console output |
| `Logger` | Opens log file, writes timestamped entries, closes on shutdown |
| `Main` | Wires all components, runs the polling loop, registers shutdown hook |

---

## 🔌 Extending the Simulator

- **Add more sensors**: Instantiate additional `Sensor` objects in `Main.java`
- **Change thresholds**: Edit the constructor arguments in `Main.java`
- **Email alerts**: Extend `AlertManager` to send SMTP notifications on CRITICAL
- **Database logging**: Replace `Logger`'s file writer with a JDBC connection
- **REST API**: Wrap the loop in a Spring Boot controller to expose readings as JSON

---

## 📜 License

MIT — free to use, extend, and learn from.
