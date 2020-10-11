package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.model.Computer;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;

public class KorEvents {

    public static class SearchComputerEvent extends Event{
        public static final EventType<SearchComputerEvent> SEARCH_COMPUTER_EVENT = new EventType("SearchComputerEvent");
        private final String text;

        public SearchComputerEvent(EventType<SearchComputerEvent> eventType, String text) {
            super(eventType);
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class PingerEvent extends Event {
        public static final EventType<PingerEvent> UPDATE_PINGER_ITEM = new EventType("UPDATE_PINGER_ITEM");
        public static final EventType<PingerEvent> UPDATE_PINGER_NAME = new EventType("UPDATE_PINGER_NAME");
        private final String name;
        private final int listSize;

        public PingerEvent(EventType<PingerEvent> eventType, String name,  int listSize) {
            super(eventType);
            this.name = name;
            this.listSize = listSize;
        }

        public String getName() {
            return name;
        }

        public int getListSize() {
            return listSize;
        }
    }

    public static class ConnectVNCEvent extends Event{
        public static final EventType<ConnectVNCEvent> CONNECT_VNC_EVENT_EVENT = new EventType("ConnectVNCEvent");
        private final String ipAddress;
        private final Parent paneBehind;

        public ConnectVNCEvent(EventType<ConnectVNCEvent> eventType, String ipAddress, Parent paneBehind ) {
            super(eventType);
            this.ipAddress = ipAddress;
            this.paneBehind = paneBehind;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public Parent getBehindParent() {
            return paneBehind;
        }
    }

    public static class AddComputerEvent extends Event{
        public static final EventType<AddComputerEvent> ADD_COMPUTER_EVENT = new EventType("AddComputerEvent");
        private final Computer computer;

        public AddComputerEvent(EventType<AddComputerEvent> eventType, Computer computer) {
            super(eventType);
            this.computer = computer;
        }

        public Computer getComputer() {
            return computer;
        }
    }
}
