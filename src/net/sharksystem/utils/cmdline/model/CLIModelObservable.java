package net.sharksystem.utils.cmdline.model;

import net.sharksystem.utils.cmdline.view.CLIModelStateObserver;

/**
 * Any observer of the CLIModel has access to the model through this interface
 */
public interface CLIModelObservable {

    /**
     * Register as an observer
     * @param observer The observer
     */
    void registerObserver(CLIModelStateObserver observer);
}
