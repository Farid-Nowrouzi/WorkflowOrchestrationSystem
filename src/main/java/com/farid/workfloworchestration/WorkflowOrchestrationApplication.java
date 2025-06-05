package com.farid.workfloworchestration;

import javafx.application.Application;
import javafx.stage.Stage;
import com.farid.workfloworchestration.view.MainView;

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
            System.out.println("🚀 Starting Workflow Orchestration System...");

            // Instantiate MainView and show the main UI
            mainView = new MainView(primaryStage);
            mainView.showMainView();

        } catch (Exception e) {
            System.err.println("❌ Fatal error during application startup:");
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
