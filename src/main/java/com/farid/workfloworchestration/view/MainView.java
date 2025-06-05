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
 * MainView
 *
 * <p>This class is responsible for launching and rendering the main GUI window
 * of the Workflow Orchestration System using JavaFX. It wires together the FXML layout,
 * controller logic, and CSS styling at application startup.</p>
 *
 * <p><strong>Design Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Abstraction:</b> Hides GUI initialization complexity from the main app class.</li>
 *   <li><b>Separation of Concerns:</b> Keeps view logic separate from controllers and services.</li>
 *   <li><b>Loose Coupling:</b> Uses controller injection and avoids hardcoding logic in FXML.</li>
 * </ul>
 */
public class MainView {

    private static final String MAIN_VIEW_FXML = "/com/farid/workfloworchestration/main-view.fxml";
    private static final String NODE_VIEW_FXML = "/com/farid/workfloworchestration/node-view.fxml";

    private final Stage primaryStage;

    /**
     * Constructor
     *
     * @param primaryStage The primary application window
     */
    public MainView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Launches and displays the main application window.
     * Wires up the MainViewController with all required backend services.
     */
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(MAIN_VIEW_FXML)));
            Parent root = loader.load();

            // === Controller Wiring ===
            MainViewController viewController = loader.getController();
            WorkflowService workflowService = new WorkflowServiceImpl();
            WorkflowExecutionService workflowExecutionService = new WorkflowExecutionService();
            WorkflowEventNotifier eventNotifier = new WorkflowEventNotifier();

            MainController mainController = new MainController(workflowService, eventNotifier);
            viewController.setMainController(mainController);                          // Link Controller to View
            mainController.setMainViewController(viewController);                      // Link View to Controller 🔁

            // === Scene Setup ===
            Scene scene = new Scene(root);
            primaryStage.setTitle("Workflow Orchestration System");
            primaryStage.setScene(scene);

            // === Load Custom CSS Styling ===
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/farid/workfloworchestration/style.css"))
                            .toExternalForm()
            );

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("❌ Error loading main view FXML:");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("❌ FXML path or CSS not found: " + MAIN_VIEW_FXML);
            e.printStackTrace();
        }
    }

    /**
     * Loads the standalone node view layout from FXML (used when injecting nodes dynamically).
     *
     * @return JavaFX Parent node for node-view.fxml layout.
     */
    public Parent loadNodeView() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(NODE_VIEW_FXML)));
            Parent nodeView = loader.load();

            // ✅ Apply CSS styling to the loaded node view
            nodeView.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/com/farid/workfloworchestration/style.css"))
                            .toExternalForm()
            );

            return nodeView;

        } catch (IOException e) {
            System.err.println("❌ Error loading node view FXML:");
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            System.err.println("❌ FXML or CSS not found for node-view: " + NODE_VIEW_FXML);
            e.printStackTrace();
            return null;
        }
    }
}
