package com.farid.workfloworchestration.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class Arrow extends Group {
    private final Line line;
    private final Polygon arrowHead;
    private final Label label;
    private Timeline flowAnimation;
    private boolean isHighlighted = false;

    public Arrow(double startX, double startY, double endX, double endY) {
        line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(2);
        line.getStrokeDashArray().addAll(10.0, 10.0);

        arrowHead = createArrowHead();

        label = new Label("");
        label.setStyle("-fx-background-color: white; -fx-padding: 2; -fx-border-color: black;");

        getChildren().addAll(line, arrowHead, label);

        updateArrowRotation();
        updateLabelPosition();
        startFlowAnimation();
    }

    private Polygon createArrowHead() {
        Polygon head = new Polygon();
        head.getPoints().addAll(
                0.0, 0.0,
                -10.0, -5.0,
                -10.0, 5.0
        );
        head.setFill(Color.BLACK);
        return head;
    }

    private void updateArrowRotation() {
        double dx = line.getEndX() - line.getStartX();
        double dy = line.getEndY() - line.getStartY();
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;

        arrowHead.setRotate(angle);
        arrowHead.setLayoutX(line.getEndX());
        arrowHead.setLayoutY(line.getEndY());
    }

    private void updateLabelPosition() {
        double midX = (line.getStartX() + line.getEndX()) / 2;
        double midY = (line.getStartY() + line.getEndY()) / 2;
        label.setLayoutX(midX);
        label.setLayoutY(midY - 20);
    }

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

    // ========== Public Methods ==========

    public void setStart(double x, double y) {
        line.setStartX(x);
        line.setStartY(y);
        updateArrowRotation();
        updateLabelPosition();
    }

    public void setEnd(double x, double y) {
        line.setEndX(x);
        line.setEndY(y);
        updateArrowRotation();
        updateLabelPosition();
    }

    public void setLabel(String text) {
        label.setText(text);
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
        if (flowAnimation != null) {
            flowAnimation.stop();
        }
    }

    public void restartAnimation() {
        if (flowAnimation != null) {
            flowAnimation.play();
        }
    }
}
