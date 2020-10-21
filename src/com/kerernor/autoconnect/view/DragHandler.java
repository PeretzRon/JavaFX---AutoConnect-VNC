package com.kerernor.autoconnect.view;

import javafx.beans.property.DoubleProperty;

public class DragHandler {

    private static double oldX = 0;
    private static double drawerHiddenX = 0;
    private static double drawerShownX = 0;
    private static double drawerButtonRightMargin = 0;
    private static double drawerButtonWidth = 0;
    private static DoubleProperty dragXDiffValue;
    private static Object lastDraggedRowInstance = null;

    public static void drag(double DRAWER_BUTTON_RIGHT_MARGIN,
                            double DRAWER_BUTTON_WIDTH,
                            DoubleProperty _draggedDiffValue,
                            double newXPosition,
                            Object _lastDraggedRowInstance) {

        if (_lastDraggedRowInstance != null && !_lastDraggedRowInstance.equals(lastDraggedRowInstance)) {

            handleEndDrag(lastDraggedRowInstance);
        }


        lastDraggedRowInstance = _lastDraggedRowInstance;
        drawerShownX = DRAWER_BUTTON_RIGHT_MARGIN / 2;
        drawerHiddenX = DRAWER_BUTTON_RIGHT_MARGIN + DRAWER_BUTTON_WIDTH;
        drawerButtonRightMargin = DRAWER_BUTTON_RIGHT_MARGIN;
        drawerButtonWidth = DRAWER_BUTTON_WIDTH;
        dragXDiffValue = _draggedDiffValue;
        onDragAction(newXPosition);
    }

    private static void onDragAction(double newXPosition) {
        double diffx;
        if (oldX != 0) {
            diffx = oldX - newXPosition;
        } else {
            diffx = 0;
        }

        oldX = newXPosition;

        if (limitDragWidthToAvoidUIOverFlow(diffx)) {
            dragXDiffValue.setValue(dragXDiffValue.getValue() - diffx);
        }
    }

    private static boolean limitDragWidthToAvoidUIOverFlow(double diffX) {
        return (((isDrawerDragHigherBound() && diffX > 0))
                || (isDrawerDragHigherBound() && diffX < 0))
                && (dragXDiffValue.getValue() >= -drawerHiddenX);
    }

    private static boolean isDrawerDragHigherBound() {
        return dragXDiffValue.getValue() < (drawerButtonWidth + drawerButtonRightMargin) * 2;
    }

    public static void handleEndDrag(Object _lastDraggedRowInstance) {
        if (dragXDiffValue != null) {
            if (dragXDiffValue.get() >= 0) {
                dragXDiffValue.setValue(drawerHiddenX);
                if (_lastDraggedRowInstance != null && _lastDraggedRowInstance.equals(lastDraggedRowInstance)) {
                    lastDraggedRowInstance = null;
                }
            } else {
                dragXDiffValue.setValue(drawerShownX);

            }
        }
    }

    public static boolean isDrawerShown(Object _lastDraggedRowInstance) {
        return _lastDraggedRowInstance != null &&
                _lastDraggedRowInstance.equals(lastDraggedRowInstance) &&
                dragXDiffValue != null &&
                Math.abs(dragXDiffValue.getValue() - drawerShownX) < 0.0001;
    }

}

