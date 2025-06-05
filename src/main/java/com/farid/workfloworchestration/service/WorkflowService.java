package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;

/**
 * WorkflowService Interface
 *
 * <p>Defines the core operations required for managing a workflow system,
 * including node management, connection management, and querying utilities.</p>
 *
 * <p><strong>OOP Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Abstraction:</b> Describes behavior without implementation.</li>
 *   <li><b>Loose Coupling:</b> Consumers (controllers, UIs) depend on the interface, not the implementation.</li>
 *   <li><b>Interface vs Abstract Class:</b> Interface used to allow multiple inheritance flexibility.</li>
 * </ul>
 */
public interface WorkflowService {

    // === Node Management ===

    /**
     * Adds a new node to the workflow.
     *
     * @param node the node to add
     */
    void addNode(WorkflowNode node);

    /**
     * Removes a node from the workflow.
     *
     * @param node the node to remove
     */
    void removeNode(WorkflowNode node);

    /**
     * Clears all nodes and connections from the workflow.
     */
    void clearAll();


    // === Connection Management ===

    /**
     * Creates a new connection between two nodes.
     *
     * @param connection the connection to add
     */
    void addConnection(WorkflowConnection connection);

    /**
     * Removes an existing connection between two nodes.
     *
     * @param source the source node
     * @param target the target node
     */
    void removeConnection(WorkflowNode source, WorkflowNode target);


    // === Query Utilities ===

    /**
     * Retrieves all nodes in the workflow.
     *
     * @return a list of all nodes
     */
    List<WorkflowNode> getAllNodes();

    /**
     * Retrieves all connections in the workflow.
     *
     * @return a list of all connections
     */
    List<WorkflowConnection> getAllConnections();

    /**
     * Finds a node by its unique ID.
     *
     * @param id the node ID
     * @return the corresponding node, or null if not found
     */
    WorkflowNode findNodeById(String id);

    /**
     * Gets all connections where the given node is the source.
     *
     * @param node the source node
     * @return list of outgoing connections
     */
    List<WorkflowConnection> getConnectionsFrom(WorkflowNode node);

    /**
     * Gets all connections where the given node is the target.
     *
     * @param node the target node
     * @return list of incoming connections
     */
    List<WorkflowConnection> getConnectionsTo(WorkflowNode node);

    /**
     * Finds all nodes that do not have any incoming connections.
     *
     * @return a list of potential starting nodes
     */
    List<WorkflowNode> findStartNodes();

}
