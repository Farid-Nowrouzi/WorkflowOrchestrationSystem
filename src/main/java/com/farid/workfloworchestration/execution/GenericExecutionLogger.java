package com.farid.workfloworchestration.execution;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic logger that captures and prints execution-related data or results.
 *
 * <p>Supports logging of any result type <code>T</code> and provides multiple
 * overloaded methods to log messages, tags, and numerical scores.</p>
 *
 * <p>This class demonstrates key object-oriented programming concepts such as:</p>
 * <ul>
 *     <li>Parametric polymorphism (generics)</li>
 *     <li>Ad-hoc polymorphism (method overloading)</li>
 *     <li>Coercion polymorphism (automatic type conversion)</li>
 *     <li>Encapsulation and modularity</li>
 * </ul>
 *
 * @param <T> The type of result to be logged
 */
// === Information Hiding Compliance ===
// The internal list `results` is private and final. All interactions are mediated via methods.
// No changes needed.

public class GenericExecutionLogger<T> { // Parametric Polymorphism


    private final List<T> results = new ArrayList<>(); // Composition: has-a List of results

    /**
     * Stores a result of type T.
     */
    public void logResult(T result) {
        results.add(result);
    }

    /**
     * Returns all stored results.
     *
     * @return List of logged results
     */
    public List<T> getResults() {
        return results; // Encapsulation: read-only access
    }

    /**
     * Clears all results.
     */
    public void clear() {
        results.clear(); // Modularity: one method per operation
    }

    /**
     * Checks if the result list is empty.
     *
     * @return true if no results are stored
     */
    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Returns the number of results stored.
     *
     * @return the size of the result list
     */
    public int size() {
        return results.size();
    }

    // ===== Ad-hoc Polymorphism: Overloaded log methods =====

    /**
     * Logs a result to the console.
     *
     * @param result A result of type T
     */
    public void log(T result) { // Ad-hoc Polymorphism (overloading)
        System.out.println("[LOGGED RESULT - Generic] " + result);
    }

    /**
     * Logs a plain text message.
     *
     * @param message The message to log
     */
    public void logString(String message) { // Ad-hoc Polymorphism
        System.out.println("📝 Logged message: " + message);
    }

    /**
     * Logs a message with an optional timestamp.
     *
     * @param message The message to log
     * @param withTimestamp Whether to prepend a timestamp
     */
    public void logString(String message, boolean withTimestamp) { // Ad-hoc Polymorphism
        if (withTimestamp) {
            String timestamp = "[" + java.time.LocalTime.now().withNano(0) + "] ";
            System.out.println(timestamp + "📘 " + message);
        } else {
            logString(message);
        }
    }

    /**
     * Logs a result of type T with a tag prefix.
     *
     * @param result The result to log
     * @param tag A label describing the context
     */
    public void logWithTag(T result, String tag) { // Ad-hoc Polymorphism
        System.out.println("🔖 [" + tag + "] " + result);
    }

    // ===== Coercion Polymorphism: Type conversions for logScore =====

    /**
     * Logs an integer score (coerced to double).
     *
     * @param score Integer score
     */
    public void logScore(int score) { // Coercion Polymorphism (int → double)
        logScore((double) score);
    }

    /**
     * Logs a float score (coerced to double).
     *
     * @param score Float score
     */
    public void logScore(float score) { // Coercion Polymorphism (float → double)
        logScore((double) score);
    }

    /**
     * Logs a double score.
     *
     * @param score Score in double precision
     */
    public void logScore(double score) {
        System.out.println(" Score logged: " + score);
    }

    /**
     * Logs a score parsed from a string.
     *
     * @param scoreStr A numeric score as string
     */
    public void logScore(String scoreStr) { // Coercion Polymorphism (String → double via parsing)
        try {
            double parsed = Double.parseDouble(scoreStr);
            logScore(parsed);
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid score string: " + scoreStr);
        }
    }
}
