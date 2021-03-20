package com.kerernor.autoconnect.util;

import animatefx.animation.ZoomIn;
import javafx.scene.Node;

public class Animation {

    public static void zoomIn(Node node, double speed) {
        ZoomIn zoomIn = new ZoomIn(node);
        zoomIn.setSpeed(speed);
        zoomIn.play();
    }
}
