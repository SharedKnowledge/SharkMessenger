package net.sharksystem.utils.cmdline.view;

/**
 * Interface implemented by observers which want to be informed by the CLIModel when it was changed.
 */
public interface CLIModelStateObserver {

    /**
     * The state of the model changed to "started".
     */
    void started();

    /**
     * The state of the model changed to "terminated".
     */
    void terminated();
}
