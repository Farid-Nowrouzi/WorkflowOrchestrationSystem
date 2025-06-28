package com.farid.workfloworchestration.observer;

import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Central manager for dispatching workflow-related events to subscribed observers.
 *
 * <p><strong>Design Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Observer Pattern:</b> Allows decoupling of event sources from listeners.</li>
 *   <li><b>Composition:</b> Holds a collection of observer interfaces.</li>
 *   <li><b>Loose Coupling:</b> Observers can vary without changing this class.</li>
 *   <li><b>High Cohesion:</b> Single responsibility—event notification.</li>
 *   <li><b>Thread-Safety:</b> Uses {@link CopyOnWriteArrayList} to avoid concurrency issues in GUI or parallel execution.</li>
 * </ul>
 */
public class WorkflowEventNotifier {

    // Logger for internal debugging and tracking
    private static final Logger LOGGER = Logger.getLogger(WorkflowEventNotifier.class.getName());

    // Thread-safe observer list
    private final List<WorkflowObserver> observers = new CopyOnWriteArrayList<>();

    /**
     * Registers an observer to receive future workflow node events.
     *
     * @param observer the observer to register (non-null)
     */
    public void addObserver(WorkflowObserver observer) {
        if (observer != null) {
            try {
                observers.add(observer);
                LOGGER.info("Observer added: " + observer.getClass().getSimpleName());
            } catch (Exception e) {
                LOGGER.warning("Failed to add observer: " + e.getMessage());
            }
        }
    }


    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to unregister
     */
    public void removeObserver(WorkflowObserver observer) {
        if (observer != null) {
            try {
                if (observers.remove(observer)) {
                    LOGGER.info("Observer removed: " + observer.getClass().getSimpleName());
                }
            } catch (Exception e) {
                LOGGER.warning("Failed to remove observer: " + e.getMessage());
            }
        }
    }


    /**
     * Notifies all observers of a specific workflow node event.
     *
     * @param node    the node where the event occurred
     * @param message a human-readable message describing the event
     */
    public void notifyObservers(WorkflowNode node, String message) {
        if (node == null || message == null) return;

        for (WorkflowObserver observer : observers) {
            try {
                observer.onNodeEvent(node, message);
            } catch (Exception e) {
                LOGGER.warning("Failed to notify observer " + observer.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        LOGGER.fine("Notified " + observers.size() + " observers of event: " + message);
    }


    /**
     * Notifies observers with a default message.
     *
     * @param node the node triggering the notification
     */
    public void notifyObservers(WorkflowNode node) {
        notifyObservers(node, "Default Event");
    }

    /**
     * Returns the number of currently registered observers.
     *
     * @return number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
}
