package com.farid.workfloworchestration.observer;

import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Manages workflow event notifications to registered observers.
 * Implements: Composition, Loose Coupling, High Cohesion
 */
public class WorkflowEventNotifier {

    private static final Logger LOGGER = Logger.getLogger(WorkflowEventNotifier.class.getName());

    // 🌟 Thread-safe list to support potential multithreading (e.g., UI updates)
    private final List<WorkflowObserver> observers = new CopyOnWriteArrayList<>();

    /**
     * Registers a new observer.
     *
     * @param observer the observer to add (non-null)
     */
    public void addObserver(WorkflowObserver observer) {
        if (observer != null) {
            observers.add(observer);
            LOGGER.info("Observer added: " + observer.getClass().getSimpleName());
        }
    }

    /**
     * Unregisters an existing observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(WorkflowObserver observer) {
        if (observer != null && observers.remove(observer)) {
            LOGGER.info("Observer removed: " + observer.getClass().getSimpleName());
        }
    }

    /**
     * Notifies all observers of an event on the specified node.
     *
     * @param node    the node related to the event
     * @param message a message describing the event
     */
    public void notifyObservers(WorkflowNode node, String message) {
        if (node == null || message == null) return;

        for (WorkflowObserver observer : observers) {
            observer.onNodeEvent(node, message);
        }

        LOGGER.fine("Notified " + observers.size() + " observers of event: " + message);
    }

    /**
     * Utility: Gets current observer count (e.g., for debugging or logging).
     */
    public int getObserverCount() {
        return observers.size();
    }
}
