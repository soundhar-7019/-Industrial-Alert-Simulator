package com.simulator;

/**
 * AlertLevel.java
 *
 * Enum representing the three alert states used in industrial control systems.
 * These map closely to ISA-18.2 alarm management standard levels used in
 * real power plant DCS environments.
 *
 *   NORMAL   → Reading within safe operating limits
 *   WARNING  → Reading approaching danger zone (operators should monitor)
 *   CRITICAL → Reading exceeded safe limit (immediate operator action needed)
 */
public enum AlertLevel {
    NORMAL,
    WARNING,
    CRITICAL
}
