package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.util.List;

public class CLICExit extends CLICommand{


    public CLICExit(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        model.terminate();
    }


    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("exit");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
