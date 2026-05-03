package com.simulator;

import java.util.Random;

/**
 * Sensor.java
 *
 * Represents a physical industrial sensor (e.g., Temperature, Pressure, Load).
 * Inspired by field instruments used in power plants like NCTPS-2 that feed
 * real-time data into DCS (Distributed Control Systems) and PLC panels.
 *
 * Each sensor has:
 *   - A name and unit (e.g., "Temperature", "°C")
 *   - A realistic operating range (min/max for random generation)
 *   - A WARNING threshold (approach danger zone)
 *   - A CRITICAL threshold (immediate action required)
 *   - A current reading updated every cycle
 */
public class Sensor {

    // --- Sensor Identity ---
    private final String name;       // Human-readable sensor name
    private final String unit;       // Unit of measurement (°C, bar, MW)

    // --- Operating Range ---
    private final double minValue;   // Minimum realistic reading
    private final double maxValue;   // Maximum realistic reading

    // --- Alert Thresholds ---
    private final double warningThreshold;   // Triggers a WARNING alert
    private final double criticalThreshold;  // Triggers a CRITICAL alert

    // --- State ---
    private double currentReading;   // Most recent sensor reading
    private final Random random;     // RNG for simulating sensor data

    /**
     * Constructs a new Sensor with full configuration.
     *
     * @param name              Sensor display name (e.g., "Temperature")
     * @param unit              Unit string (e.g., "°C")
     * @param minValue          Lowest value in operating range
     * @param maxValue          Highest value in operating range
     * @param warningThreshold  Value at which a WARNING is raised
     * @param criticalThreshold Value at which a CRITICAL alert is raised
     */
    public Sensor(String name, String unit, double minValue, double maxValue,
                  double warningThreshold, double criticalThreshold) {
        this.name = name;
        this.unit = unit;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;
        this.random = new Random();
        this.currentReading = minValue; // Initialize at safe baseline
    }

    /**
     * Simulates a new sensor reading by generating a random value
     * within the sensor's defined operating range.
     *
     * In a real DCS, this would instead poll a field transmitter
     * via 4–20 mA loops or HART/Modbus protocols.
     */
    public void generateReading() {
        // Weighted simulation: 80% normal, 20% chance of drifting toward danger
        double spike = random.nextDouble();
        if (spike > 0.80) {
            // Simulate an anomalous spike approaching or beyond warning threshold
            double spikeRange = maxValue - warningThreshold;
            currentReading = warningThreshold + (random.nextDouble() * spikeRange);
        } else {
            // Normal operating band
            double normalRange = warningThreshold - minValue;
            currentReading = minValue + (random.nextDouble() * normalRange);
        }
        // Round to 2 decimal places for clean display
        currentReading = Math.round(currentReading * 100.0) / 100.0;
    }

    /**
     * Evaluates the current reading against thresholds and returns
     * the appropriate AlertLevel.
     *
     * @return AlertLevel: NORMAL, WARNING, or CRITICAL
     */
    public AlertLevel getAlertLevel() {
        if (currentReading >= criticalThreshold) {
            return AlertLevel.CRITICAL;
        } else if (currentReading >= warningThreshold) {
            return AlertLevel.WARNING;
        } else {
            return AlertLevel.NORMAL;
        }
    }

    // --- Getters ---

    public String getName()              { return name; }
    public String getUnit()              { return unit; }
    public double getCurrentReading()    { return currentReading; }
    public double getWarningThreshold()  { return warningThreshold; }
    public double getCriticalThreshold() { return criticalThreshold; }

    /**
     * Returns a formatted status string for console display.
     * Example: "Temperature   | 312.45 °C  | WARNING"
     */
    @Override
    public String toString() {
        return String.format("%-15s | %8.2f %-4s | %s",
                name, currentReading, unit, getAlertLevel().name());
    }
}
