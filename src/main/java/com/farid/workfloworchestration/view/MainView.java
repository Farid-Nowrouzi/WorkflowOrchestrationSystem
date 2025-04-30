package com.farid.workfloworchestration.view;

import com.farid.workfloworchestration.controller.MainController;
import com.farid.workfloworchestration.controller.MainViewController;
import com.farid.workfloworchestration.observer.WorkflowEventNotifier;
import com.farid.workfloworchestration.service.WorkflowExecutionService;
import com.farid.workfloworchestration.service.WorkflowService;
import com.farid.workfloworchestration.service.WorkflowServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * MainView handles the main application window and FXML loading.
 */
public class MainView {

    private static final String MAIN_VIEW_FXML = "/com/farid/workfloworchestration/main-view.fxml";
    private static final String NODE_VIEW_FXML = "/com/farid/workfloworchestration/node-view.fxml";

    private final Stage primaryStage;

    public MainView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(MAIN_VIEW_FXML)));
            Parent root = loader.load();

            // Controller Setup
            MainViewController viewController = loader.getController();
            WorkflowService workflowService = new WorkflowServiceImpl();
            WorkflowExecutionService workflowExecutionService = new WorkflowExecutionService();
            WorkflowEventNotifier eventNotifier = new WorkflowEventNotifier();
            MainController mainController = new MainController(workflowService, eventNotifier);

            viewController.setMainController(mainController);

            primaryStage.setTitle("Workflow Orchestration System");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("❌ Error loading main view FXML:");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("❌ FXML path is invalid: " + MAIN_VIEW_FXML);
            e.printStackTrace();
        }
    }

    /**
     * Loads the node-view.fxml component.
     *
     * @return Parent node of the node view layout.
     */
    public Parent loadNodeView() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(NODE_VIEW_FXML)));
            return loader.load();
        } catch (IOException e) {
            System.err.println("❌ Error loading node view FXML:");
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            System.err.println("❌ FXML path is invalid: " + NODE_VIEW_FXML);
            e.printStackTrace();
            return null;
        }
    }
}
