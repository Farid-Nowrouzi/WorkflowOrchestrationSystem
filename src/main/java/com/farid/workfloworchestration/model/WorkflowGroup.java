package com.farid.workfloworchestration.model;

import java.util.List;

/**
 * WorkflowGroup
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Aggregation:</b> This class holds references to WorkflowNode instances, but does not own their lifecycle.</li>
 *   <li><b>Modularity:</b> Represents a logical module that groups nodes under a common name.</li>
 *   <li><b>Information Hiding:</b> All internal fields are private and accessed via public getters/setters.</li>
 * </ul>
 */
public class WorkflowGroup {

    // === Private Fields (Information Hiding) ===
    private String groupName;
    private List<WorkflowNode> members;

    /**
     * Constructs a new group with a given name and list of member nodes.
     * This is an example of aggregation — it does not own the nodes.
     *
     * @param groupName name of the group
     * @param members list of WorkflowNode instances belonging to the group
     */
    public WorkflowGroup(String groupName, List<WorkflowNode> members) {
        this.groupName = groupName;
        this.members = members;
    }

    // === Encapsulation: Getters and Setters ===

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<WorkflowNode> getMembers() {
        return members;
    }

    public void setMembers(List<WorkflowNode> members) {
        this.members = members;
    }

    // === Utility Methods ===

    /**
     * Prints the current members of the group in a readable format.
     */
    public void printGroupMembers() {
        System.out.println("=== Group: " + groupName + " ===");
        for (WorkflowNode node : members) {
            System.out.println("- " + node.getName() + " (" + node.getNodeType() + ")");
        }
    }

    /**
     * Adds a node to the group if it is not already present.
     * Prevents duplicates.
     *
     * @param node the WorkflowNode to add
     */
    public void addMember(WorkflowNode node) {
        if (!members.contains(node)) {
            members.add(node);
        }
    }

    /**
     * Removes a node from the group.
     *
     * @param node the WorkflowNode to remove
     */
    public void removeMember(WorkflowNode node) {
        members.remove(node);
    }
}
