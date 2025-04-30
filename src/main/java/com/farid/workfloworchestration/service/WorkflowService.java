package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;

/**
 * WorkflowService interface
 *
 * OOP Concepts:
 * - Abstraction: Defines "what" services should do, not "how"
 * - Loose Coupling: Controller depends on this interface, not on concrete classes
 * - Interface vs Abstract Class: Using Interface here
 */
public interface WorkflowService {

    void addNode(WorkflowNode node);

    void addConnection(WorkflowConnection connection);

    void clearAll();


    List<WorkflowNode> findStartNodes();

    List<WorkflowConnection> getConnectionsFrom(WorkflowNode node);

    List<WorkflowNode> getAllNodes();

    List<WorkflowConnection> getConnectionsTo(WorkflowNode node);

    WorkflowNode findNodeById(String id);

    List<WorkflowConnection> getAllConnections();




}
