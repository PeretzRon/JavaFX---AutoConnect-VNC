package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.model.Computer;
import javafx.event.Event;
import javafx.event.EventType;

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
