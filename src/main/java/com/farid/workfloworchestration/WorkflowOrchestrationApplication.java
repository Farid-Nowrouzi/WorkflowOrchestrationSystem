package com.farid.workfloworchestration;

import com.farid.workfloworchestration.model.AnalysisNode;
import javafx.application.Application;
import javafx.stage.Stage;
import com.farid.workfloworchestration.view.MainView;
import com.farid.workfloworchestration.model.WorkflowNode;
import com.farid.workfloworchestration.model.TaskNode;
import com.farid.workfloworchestration.model.PredictionNode;


/**
 * Main entry point for the Workflow Orchestration System.
 *
 * <p>This class launches the JavaFX application by initializing the main GUI
 * through {@link MainView}, which encapsulates all FXML and scene setup.</p>
 *
 * <strong>OOP Principles Demonstrated:</strong>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@code javafx.application.Application} to integrate with JavaFX lifecycle.</li>
 *   <li><b>Encapsulation:</b> View logic is encapsulated inside {@link MainView} (not exposed here).</li>
 *   <li><b>Abstraction:</b> Abstracts JavaFX details away from the main class. Main logic is clear and concise.</li>
 *   <li><b>SRP (Single Responsibility):</b> This class is only responsible for launching the app — nothing more.</li>
 *   <li><b>Modularity:</b> GUI bootstrapping delegated to a reusable and testable class: {@code MainView}.</li>
 *   <li><b>Dependency Injection:</b> {@code Stage} is passed into {@code MainView}, not hard-coded.</li>
 *   <li><b>Extensibility:</b> Easily extendable with logging, splash screens, configuration loaders, etc.</li>
 * </ul>
 */
public class WorkflowOrchestrationApplication extends Application {
    // === Private Field (Information Hiding Compliance) ===
    // Holds the main view instance; used internally to launch the UI. No external access needed.
    private MainView mainView;

    /**
     * JavaFX entry point — automatically called by the framework after launch().
     * This method initializes the primary stage and delegates view setup.
     *
     * @param primaryStage the primary JavaFX stage provided by the runtime
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Optional setup (logging, config loading) could be placed here
            System.out.println(" Starting Workflow Orchestration System...");

            // Instantiate MainView and show the main UI
            mainView = new MainView(primaryStage);
            mainView.showMainView();

            mainView.showMainView();

            // === Polymorphism by Inclusion Demonstration ===
            // This demonstrates subtype polymorphism where WorkflowNode (superclass) references
            // are used to point to TaskNode, PredictionNode, and AnalysisNode (subclasses).
            // The execute() method is called polymorphically, invoking the correct subclass version.

            WorkflowNode node1 = new TaskNode("1", "Task A", "Process Data");
            WorkflowNode node2 = new PredictionNode("2", "Predict Outcome","Inference Step", "Model-X");
            WorkflowNode node3 = new AnalysisNode("3", "Analyze X", "Data Correlation");

            System.out.println(" Demonstrating polymorphism by inclusion:");
            System.out.println("Node1 type: " + node1.getClass().getSimpleName());
            System.out.println("Node2 type: " + node2.getClass().getSimpleName());
            System.out.println("Node3 type: " + node2.getClass().getSimpleName());

            // Polymorphic method calls via WorkflowNode reference
            node1.execute();  // Calls TaskNode's execute()
            node2.execute();  // Calls PredictionNode's execute()
            node3.execute(); // Will call AnalysisNode's execute()


        } catch (Exception e) {
            System.err.println(" Fatal error during application startup:");
            e.printStackTrace();
        }
    }

    /**
     * Main method — launches the JavaFX application.
     * Triggers {@code start(Stage)} automatically.
     *
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        launch(args); // JavaFX application lifecycle begins here
    }
}
