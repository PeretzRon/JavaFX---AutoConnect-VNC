package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public interface ISearchTextFlow {
    Set<JSearchableTextFlowController> getActiveSearchableTextFlowMap();
}
