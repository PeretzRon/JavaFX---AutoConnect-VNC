package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.LastConnectionItem;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Parent;

public class KorEvents {

    public static class SearchComputerEvent extends Event {
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
        public static final EventType<PingerEvent> EXIT = new EventType("ExitEditPinger");
        private String name;
        private int listSize;

        public PingerEvent(EventType<PingerEvent> eventType) {
            super(eventType);
        }

        public PingerEvent(EventType<PingerEvent> eventType, String name, int listSize) {
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

    public static class ConnectVNCEvent extends Event {
        public static final EventType<ConnectVNCEvent> CONNECT_VNC_EVENT_EVENT = new EventType("ConnectVNCEvent");
        private final String ipAddress;
        private final Parent paneBehind;

        public ConnectVNCEvent(EventType<ConnectVNCEvent> eventType, String ipAddress, Parent paneBehind) {
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

    public static class SearchHistoryConnectionEvent extends Event {
        public static final EventType<SearchHistoryConnectionEvent> SEARCH_HISTORY_CONNECTION_EVENT_EVENT_TYPE = new EventType("SearchHistoryConnncectionEvent");
        private final LastConnectionItem lastConnectionItem;

        public SearchHistoryConnectionEvent(EventType<SearchHistoryConnectionEvent> eventType, LastConnectionItem lastConnectionItem) {
            super(eventType);
            this.lastConnectionItem = lastConnectionItem;
        }

        public LastConnectionItem getLastConnectionItem() {
            return lastConnectionItem;
        }
    }
}
