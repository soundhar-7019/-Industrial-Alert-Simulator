package com.simulator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main.java
 *
 * Entry point for the Industrial Alert Simulator.
 *
 * This class wires together the Sensor, AlertManager, and Logger components
 * and drives the main polling loop. It mimics the scan cycle of a PLC
 * (Programmable Logic Controller) — a fixed-interval loop that reads all
 * field inputs, evaluates logic, and triggers outputs (in our case, alerts).
 *
 * Inspired by NCTPS-2 (North Chennai Thermal Power Station Unit 2), which
 * uses a DCS with hundreds of analog and digital sensors continuously
 * monitored for safe plant operation.
 *
 * Run this class with:
 *   javac -d out src/com/simulator/*.java
 *   java -cp out com.simulator.Main
 *
 * Press Ctrl+C to stop.
 */
public class Main {

    /** Polling interval in milliseconds (matches typical PLC scan cycles) */
    private static final int POLL_INTERVAL_MS = 2000;

    public static void main(String[] args) {

        // ── 1. Configure Sensors ──────────────────────────────────────────────
        //
        // Values are based on approximate real-world operating parameters for
        // a 600 MW coal-fired thermal power plant:
        //
        //   Temperature: Boiler steam ~300–540°C. Warn at 510°C, Critical at 535°C.
        //   Pressure:    Main steam ~150–170 bar.  Warn at 165 bar, Critical at 172 bar.
        //   Load:        Generator output 0–600 MW. Warn at 550 MW, Critical at 580 MW.

        Sensor[] sensors = {
            new Sensor(
                "Temperature",  // Sensor name
                "°C",           // Unit
                280.0,          // Min operating value
                545.0,          // Max operating value
                510.0,          // WARNING threshold
                535.0           // CRITICAL threshold
            ),
            new Sensor(
                "Pressure",
                "bar",
                140.0,          // Min operating value
                175.0,          // Max operating value
                165.0,          // WARNING threshold
                172.0           // CRITICAL threshold
            ),
            new Sensor(
                "Load",
                "MW",
                100.0,          // Min operating value (minimum stable load)
                595.0,          // Max operating value
                550.0,          // WARNING threshold
                580.0           // CRITICAL threshold
            )
        };

        // ── 2. Initialize AlertManager ────────────────────────────────────────
        AlertManager alertManager = new AlertManager();

        // ── 3. Initialize Logger ──────────────────────────────────────────────
        // Log file named with current date for easy identification
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String logPath = "logs/sensor_log_" + date + ".txt";

        Logger logger = null;
        try {
            logger = new Logger(logPath);
        } catch (IOException e) {
            System.err.println("[ERROR] Cannot create log file at: " + logPath);
            System.err.println("        Make sure the 'logs/' directory exists.");
            System.err.println("        " + e.getMessage());
            System.exit(1);
        }

        // ── 4. Register Shutdown Hook ─────────────────────────────────────────
        // This block runs when Ctrl+C is pressed, ensuring a clean shutdown
        final Logger finalLogger = logger;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            alertManager.printShutdownMessage();
            finalLogger.logShutdown();
            finalLogger.close();
            System.out.println("[INFO] Log saved to: " + logPath);
        }));

        // ── 5. Print Startup Banner ───────────────────────────────────────────
        alertManager.printStartupBanner(sensors);
        System.out.println("[INFO] Logging to: " + logPath);
        System.out.println();

        // ── 6. Main Polling Loop ──────────────────────────────────────────────
        //
        // Each iteration simulates one PLC scan cycle:
        //   a) Generate new reading for each sensor
        //   b) Evaluate alert level and print to console
        //   c) Log reading and any alert to file
        //   d) Sleep until next cycle
        //
        // The loop runs indefinitely until Ctrl+C triggers the shutdown hook.

        int cycleNumber = 0;

        while (true) {
            cycleNumber++;

            // Print cycle separator with cycle counter
            System.out.printf("─── Cycle #%-4d ────────────────────────────────────────────%n",
                    cycleNumber);

            for (Sensor sensor : sensors) {

                // a) Simulate new field reading
                sensor.generateReading();

                // b) Evaluate and print color-coded status to console
                String alertMessage = alertManager.evaluate(sensor);

                // c) Log the reading unconditionally
                logger.logReading(sensor);

                // d) Log the alert message only if an alert was triggered
                if (alertMessage != null) {
                    logger.logAlert(alertMessage);
                }
            }

            System.out.println(); // Blank line between cycles for readability

            // e) Wait for next poll cycle
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                // Thread interrupted — exit gracefully (shutdown hook will handle cleanup)
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
