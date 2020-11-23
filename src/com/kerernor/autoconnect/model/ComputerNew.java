package com.kerernor.autoconnect.model;

import com.kerernor.autoconnect.util.KorTypes;

import java.util.UUID;

public class ComputerNew {

    private final String id;
    private final String ip;
    private final String name;
    private final String itemLocation;
    private final KorTypes.ComputerType computerType;

    public ComputerNew(Builder builder) {
        this.ip = builder.id;
        this.name = builder.name;
        this.itemLocation = builder.itemLocation;
        this.computerType = builder.computerType;
        this.id = UUID.randomUUID().toString();
    }

    public static class Builder {

        private String id;
        private String ip;
        private String name;
        private String itemLocation;
        private KorTypes.ComputerType computerType;


        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        // Setter methods
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setItemLocation(String itemLocation) {
            this.itemLocation = itemLocation;
            return this;
        }

        public Builder setIP(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setComputerType(KorTypes.ComputerType computerType) {
            this.computerType = computerType;
            return this;
        }

        // build method to deal with outer class
        // to return outer instance
        public ComputerNew build() {
            return new ComputerNew(this);
        }
    }
}
