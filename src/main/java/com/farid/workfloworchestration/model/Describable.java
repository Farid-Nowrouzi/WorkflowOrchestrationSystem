package com.farid.workfloworchestration.model;

/**
 * {@code Describable} is an interface that enforces a contract for objects
 * to provide and update a textual description.
 *
 * <p>It is typically implemented by workflow nodes that require human-readable
 * or contextual descriptions (e.g., explanation of what a node does).</p>
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Abstraction</b>: Defines a high-level interface without implementation</li>
 *   <li><b>Polymorphism</b>: Enables multiple classes to offer custom behavior for description handling</li>
 *   <li><b>Loose Coupling</b>: Consumers depend on this interface, not concrete implementations</li>
 *   <li><b>Reusability</b>: Any class can implement this interface to gain description support</li>
 * </ul>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public interface Describable {

    /**
     * Returns a short or detailed description of the object.
     *
     * @return A human-readable textual description.
     */
    String getDescription();

    /**
     * Updates the description of the object.
     *
     * @param description The new description text to be stored.
     */
    void setDescription(String description);
}
