package com.farid.workfloworchestration.util;

import java.util.Map;

/**
 * MetadataPrinter
 *
 * <p>A generic utility class that prints metadata entries in various formats.
 * Demonstrates:</p>
 * <ul>
 *   <li><strong>Parametric Polymorphism:</strong> Generic type {@code T extends Map<?, ?>}</li>
 *   <li><strong>Method Overloading:</strong> Multiple {@code printMetadata} methods for flexibility</li>
 *   <li><strong>Clean Abstraction:</strong> Encapsulates formatting logic for metadata printing</li>
 * </ul>
 *
 * @param <T> A type that extends {@code Map<?, ?>}, allowing support for any key-value metadata.
 */

//  No fields defined – class is stateless and safe by design.
// All logic is accessed through overloaded printMetadata(...) methods.
// Fully compliant with abstraction and information hiding principles.

public class MetadataPrinter<T extends Map<?, ?>> {

    /**
     * Prints all metadata key-value pairs.
     *
     * @param metadata The metadata map to print
     */
    public void printMetadata(T metadata) {
        if (metadata == null || metadata.isEmpty()) {
            System.out.println(" Metadata is empty.");
            return;
        }
        metadata.forEach((k, v) -> System.out.println("Key: " + k + ", Value: " + v));
    }

    /**
     * Fallback when no metadata is provided.
     */
    public void printMetadata() {
        System.out.println("📎 No metadata provided.");
    }

    /**
     * Prints metadata with a custom prefix.
     *
     * @param metadata The metadata map
     * @param prefix   A string prefix prepended to each printed line
     */
    public void printMetadata(T metadata, String prefix) {
        if (metadata == null || metadata.isEmpty()) {
            System.out.println(prefix + " [No metadata]");
            return;
        }
        metadata.forEach((k, v) -> System.out.println(prefix + " " + k + " = " + v));
    }

    /**
     * Filters and prints only metadata entries where the key contains a specified keyword.
     *
     * @param metadata     The metadata map
     * @param prefix       A prefix for each printed line
     * @param keyContains  A keyword to filter keys
     */
    public void printMetadata(T metadata, String prefix, String keyContains) {
        if (metadata == null || metadata.isEmpty()) {
            System.out.println(prefix + " [No metadata]");
            return;
        }

        metadata.forEach((k, v) -> {
            if (k.toString().contains(keyContains)) {
                System.out.println(prefix + " " + k + " = " + v);
            }
        });
    }
}
