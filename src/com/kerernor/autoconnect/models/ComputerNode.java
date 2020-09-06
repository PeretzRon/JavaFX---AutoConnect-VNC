package com.kerernor.autoconnect.models;

import javafx.scene.Node;

public class ComputerNode {
    private Computer computer;
    private Node node;

    public ComputerNode(Computer computer, Node node) {
        this.computer = computer;
        this.node = node;
    }

    public Computer getItem() {
        return computer;
    }

    public void setItem(Computer computer) {
        this.computer = computer;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

}
