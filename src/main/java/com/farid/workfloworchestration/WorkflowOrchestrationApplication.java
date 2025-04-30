package com.farid.workfloworchestration;

import javafx.application.Application;
import javafx.stage.Stage;
import com.farid.workfloworchestration.view.MainView;

/**
 * WorkflowOrchestrationApplication
 *
 * This is the main entry point for the Workflow Orchestration System.
 * It sets up the JavaFX application and delegates GUI loading to MainView.
 *
 * OOP Concepts Applied:
 * - Inheritance: Inherits from javafx.application.Application
 * - Encapsulation: MainView setup is private and modular
 * - Abstraction: External classes don't handle JavaFX internals
 * - SRP (Single Responsibility): Responsible only for application launch
 * - Modularity: Delegates to MainView for view logic
 * - Dependency Injection: Injects primaryStage into the view
 * - Extensibility: Prepared for logging, future configs, or preloading logic
 */
public class WorkflowOrchestrationApplication extends Application {

    private MainView mainView;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Optional: Logging, environment setup, splash screen can go here
            System.out.println("Starting Workflow Orchestration System...");

            // Dependency Injection: Inject primaryStage into MainView
            mainView = new MainView(primaryStage);
            mainView.showMainView(); // Load and show GUI

        } catch (Exception e) {
            System.err.println("Fatal error during application startup.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Entry point - launches JavaFX application lifecycle
        launch(args);
    }
}
