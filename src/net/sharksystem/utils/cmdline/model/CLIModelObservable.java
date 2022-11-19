package net.sharksystem.utils.cmdline.model;

import net.sharksystem.utils.cmdline.view.CLIModelStateObserver;

public interface CLIModelObservable {

    void registerObserver(CLIModelStateObserver observer);
}
