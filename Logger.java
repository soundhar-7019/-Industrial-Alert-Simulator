package com.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger.java
 *
 * Handles persistent logging of all sensor readings and alerts to a .txt file.
 *
 * In real industrial environments, DCS historian software (like OSIsoft PI or
 * Wonderware) records every tag value with millisecond precision for post-event
 * analysis and compliance. This Logger simulates that behavior by writing
 * timestamped entries to a plain text file on disk.
 *
 * Log file path is set at construction time. The file is opened in append mode
 * so logs are preserved across restarts (within the same session file).
 */
public class Logger {

    private final String logFilePath;   // Path to the output log file
    private BufferedWriter writer;      // Buffered writer for efficient I/O

    // Shared timestamp format for all log entries
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a Logger and opens (or creates) the log file.
     *
     * @param logFilePath Absolute or relative path to the .txt log file
     * @throws IOException If the file cannot be opened or created
     */
    public Logger(String logFilePath) throws IOException {
        this.logFilePath = logFilePath;
        // true = append mode, so existing log data is not overwritten
        this.writer = new BufferedWriter(new FileWriter(logFilePath, true));
        writeHeader();
    }

    /**
     * Writes a session start header to the log file.
     * Makes it easy to identify where each run begins in a shared log.
     */
    private void writeHeader() throws IOException {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        writer.write("═══════════════════════════════════════════════════════");
        writer.newLine();
        writer.write(String.format("  SESSION STARTED: %s", timestamp));
        writer.newLine();
        writer.write("  Industrial Alert Simulator | NCTPS-2 Inspired");
        writer.newLine();
        writer.write("═══════════════════════════════════════════════════════");
        writer.newLine();
        writer.flush();
    }

    /**
     * Logs a single sensor reading to the file.
     * Called every polling cycle for every sensor regardless of alert level.
     *
     * Format: [TIMESTAMP] SENSOR_NAME | VALUE UNIT | LEVEL
     *
     * @param sensor The sensor whose reading should be logged
     */
    public void logReading(Sensor sensor) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = String.format("[%s] %-15s | %8.2f %-4s | %s",
                timestamp,
                sensor.getName(),
                sensor.getCurrentReading(),
                sensor.getUnit(),
                sensor.getAlertLevel().name());
        writeLine(entry);
    }

    /**
     * Logs an alert message (WARNING or CRITICAL) to the file.
     * These entries are prefixed with "!!! ALERT" for easy filtering with
     * tools like grep in post-incident analysis.
     *
     * @param alertMessage The formatted alert message from AlertManager
     */
    public void logAlert(String alertMessage) {
        if (alertMessage != null && !alertMessage.isEmpty()) {
            writeLine("!!! ALERT → " + alertMessage);
        }
    }

    /**
     * Writes a session end footer when the simulator is shut down.
     * Provides a clean boundary in the log file.
     */
    public void logShutdown() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        writeLine("───────────────────────────────────────────────────────");
        writeLine(String.format("  SESSION ENDED: %s", timestamp));
        writeLine("───────────────────────────────────────────────────────");
    }

    /**
     * Internal helper: writes a single line to the log file and flushes.
     * Errors are printed to stderr but do not crash the simulator.
     *
     * @param line The text line to write
     */
    private void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
            writer.flush(); // Flush after each entry so logs survive a crash
        } catch (IOException e) {
            System.err.println("[Logger] Failed to write log entry: " + e.getMessage());
        }
    }

    /**
     * Closes the log file. Should be called during graceful shutdown.
     */
    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("[Logger] Failed to close log file: " + e.getMessage());
        }
    }

    /**
     * Returns the path of the active log file.
     *
     * @return Log file path as a String
     */
    public String getLogFilePath() {
        return logFilePath;
    }
}
