package com.farid.workfloworchestration.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

/**
 * Arrow
 *
 * <p>A visual arrow component representing a directional connection between two nodes
 * in the workflow UI. Includes an animated dashed line, arrowhead, and optional label.</p>
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> All animation, rotation, and label logic is kept internal.</li>
 *   <li><b>Abstraction:</b> Exposed public API: setStart, setEnd, setLabel, highlight, etc.</li>
 *   <li><b>Reusability & Modularity:</b> Can be reused for any JavaFX graph connection UI.</li>
 * </ul>
 */
public class Arrow extends Group {

    private final Line line;
    private final Polygon arrowHead;
    private final Label label;
    private Timeline flowAnimation;
    private boolean isHighlighted = false;

    /**
     * Constructs a new animated arrow from (startX, startY) to (endX, endY).
     */
    public Arrow(double startX, double startY, double endX, double endY) {
        this.line = new Line(startX, startY, endX, endY);
        this.line.setStrokeWidth(2);
        this.line.getStrokeDashArray().addAll(10.0, 10.0);

        this.arrowHead = createArrowHead();
        this.label = createBoundLabel();

        getChildren().addAll(line, arrowHead, label);

        updateArrowRotation();
        startFlowAnimation();
    }

    /**
     * Creates the triangle arrowhead polygon.
     */
    private Polygon createArrowHead() {
        Polygon head = new Polygon(
                0.0, 0.0,
                -10.0, -5.0,
                -10.0, 5.0
        );
        head.setFill(Color.BLACK);
        return head;
    }

    /**
     * Creates and binds the arrow label to be centered on the line.
     */
    private Label createBoundLabel() {
        Label lbl = new Label();
        lbl.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95); " +
                        "-fx-border-color: #999999; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 4 8; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-effect: dropshadow(gaussian, lightgray, 2, 0.5, 0, 1);"
        );
        lbl.setMouseTransparent(true);

        lbl.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        (line.getStartX() + line.getEndX()) / 2 - lbl.getWidth() / 2,
                line.startXProperty(), line.endXProperty(), lbl.widthProperty()));

        lbl.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        (line.getStartY() + line.getEndY()) / 2 - lbl.getHeight() - 6,
                line.startYProperty(), line.endYProperty(), lbl.heightProperty()));

        return lbl;
    }

    /**
     * Rotates the arrowhead to follow the line's direction.
     */
    private void updateArrowRotation() {
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        arrowHead.setRotate(angle);
        arrowHead.setLayoutX(line.getEndX());
        arrowHead.setLayoutY(line.getEndY());
    }

    /**
     * Starts the animation of the arrow’s dashed line.
     */
    private void startFlowAnimation() {
        flowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    double offset = line.getStrokeDashOffset();
                    line.setStrokeDashOffset(offset + 1);
                }),
                new KeyFrame(Duration.millis(50))
        );
        flowAnimation.setCycleCount(Timeline.INDEFINITE);
        flowAnimation.play();
    }

    // ===================== PUBLIC METHODS ======================

    public void setStart(double x, double y) {
        line.setStartX(x);
        line.setStartY(y);
        updateArrowRotation();
    }

    public void setEnd(double x, double y) {
        line.setEndX(x);
        line.setEndY(y);
        updateArrowRotation();
    }

    public void setLabel(String text) {
        String clean = text != null ? text.trim() : "";
        label.setText(clean);

        Tooltip tooltip = new Tooltip(
                "From: (" + (int) line.getStartX() + "," + (int) line.getStartY() + ")\n" +
                        "To: (" + (int) line.getEndX() + "," + (int) line.getEndY() + ")\n" +
                        "Label: " + clean
        );
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(300);
        Tooltip.install(label, tooltip);
    }

    public String getLabel() {
        return label.getText();
    }

    public Line getLine() {
        return line;
    }

    public void highlight() {
        isHighlighted = true;
        setArrowColor(Color.ORANGE);
    }

    public void unhighlight() {
        isHighlighted = false;
        setArrowColor(Color.BLACK);
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setArrowColor(Color color) {
        line.setStroke(color);
        arrowHead.setFill(color);
    }

    public void setArrowThickness(double thickness) {
        line.setStrokeWidth(thickness);
    }

    public void stopAnimation() {
        if (flowAnimation != null) flowAnimation.stop();
    }

    public void restartAnimation() {
        if (flowAnimation != null) flowAnimation.play();
    }
}
