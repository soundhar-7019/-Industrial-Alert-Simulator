package com.simulator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AlertManager.java
 *
 * Responsible for evaluating sensor readings against thresholds and
 * printing color-coded alerts to the console.
 *
 * In real power plant SCADA/DCS systems, alert management is governed by
 * ISA-18.2 standards. This simulator mimics that behavior with:
 *   - GREEN text  → NORMAL readings
 *   - YELLOW text → WARNING (threshold approaching)
 *   - RED text    → CRITICAL (immediate action required)
 *
 * ANSI escape codes are used for terminal color output.
 */
public class AlertManager {

    // --- ANSI Color Codes ---
    private static final String RESET   = "\u001B[0m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String RED     = "\u001B[31m";
    private static final String BOLD    = "\u001B[1m";
    private static final String CYAN    = "\u001B[36m";

    // Timestamp formatter matching log file format
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Evaluates a sensor's current reading, prints a color-coded status line,
     * and returns the alert message (if any) for logging.
     *
     * @param sensor The sensor to evaluate
     * @return A plain-text alert message, or null if reading is NORMAL
     */
    public String evaluate(Sensor sensor) {
        AlertLevel level    = sensor.getAlertLevel();
        String timestamp    = LocalDateTime.now().format(FORMATTER);
        String sensorName   = sensor.getName();
        double reading      = sensor.getCurrentReading();
        String unit         = sensor.getUnit();
        String alertMessage = null;

        switch (level) {
            case NORMAL:
                // Print normal reading in green
                System.out.printf("%s[%s]%s %-15s %s%8.2f %-4s%s  [%sNORMAL%s]%n",
                        CYAN, timestamp, RESET,
                        sensorName,
                        GREEN, reading, unit, RESET,
                        GREEN, RESET);
                break;

            case WARNING:
                // Print warning in bold yellow with alert tag
                alertMessage = String.format("[%s] ⚠ WARNING  | %s = %.2f %s (threshold: %.2f)",
                        timestamp, sensorName, reading, unit, sensor.getWarningThreshold());

                System.out.printf("%s[%s]%s %-15s %s%s%8.2f %-4s%s  [%s⚠ WARNING%s]  → Exceeds threshold %.2f %s%n",
                        CYAN, timestamp, RESET,
                        sensorName,
                        BOLD, YELLOW, reading, unit, RESET,
                        YELLOW, RESET,
                        sensor.getWarningThreshold(), unit);
                break;

            case CRITICAL:
                // Print critical in bold red with full alarm banner
                alertMessage = String.format("[%s] ✖ CRITICAL | %s = %.2f %s (threshold: %.2f)",
                        timestamp, sensorName, reading, unit, sensor.getCriticalThreshold());

                System.out.println(RED + BOLD +
                        "╔══════════════════════════════════════════════════════════╗" + RESET);
                System.out.printf(RED + BOLD +
                        "║  [%s] ✖ CRITICAL ALARM                            ║%n" + RESET,
                        timestamp);
                System.out.printf(RED + BOLD +
                        "║  Sensor  : %-47s║%n" + RESET, sensorName);
                System.out.printf(RED + BOLD +
                        "║  Reading : %-6.2f %-4s  (Critical limit: %-6.2f %-4s)  ║%n" + RESET,
                        reading, unit, sensor.getCriticalThreshold(), unit);
                System.out.println(RED + BOLD +
                        "╚══════════════════════════════════════════════════════════╝" + RESET);
                break;
        }

        return alertMessage;
    }

    /**
     * Prints the startup banner with sensor configuration table.
     * Gives operators an overview of all monitored points before the loop begins.
     *
     * @param sensors Array of sensors being monitored
     */
    public void printStartupBanner(Sensor[] sensors) {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║         INDUSTRIAL ALERT SIMULATOR — NCTPS-2 INSPIRED           ║");
        System.out.println("║         Power Plant Sensor Monitoring System v1.0                ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Sensor          | Unit | Warn Threshold | Critical Threshold    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        for (Sensor s : sensors) {
            System.out.printf("║  %-16s| %-5s| %-15.1f| %-22.1f║%n",
                    s.getName(), s.getUnit(),
                    s.getWarningThreshold(), s.getCriticalThreshold());
        }
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Polling interval: 2 seconds   |  Press Ctrl+C to stop          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    /**
     * Prints a clean shutdown message when the operator terminates the program.
     */
    public void printShutdownMessage() {
        System.out.println();
        System.out.println(YELLOW + BOLD +
                "⏹  Simulator stopped by operator. All logs saved. Goodbye." + RESET);
    }
}
