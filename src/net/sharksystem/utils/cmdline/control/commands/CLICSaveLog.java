package net.sharksystem.utils.cmdline.control.commands;

import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CLICSaveLog extends CLICommand {

    public CLICSaveLog(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model, List<String> args) throws Exception {
        if(args.size() >= 1) {
            String fileName = args.get(0);
            File file = new File(fileName);
            if(file.createNewFile()) {
                try {
                    PrintWriter pw = new PrintWriter(fileName);
                    List<String> log = model.getCommandHistoryList();

                    for(String s : log) {
                        pw.println(s);
                    }

                    pw.close();

                } catch (FileNotFoundException e) {
                    ui.printError("Couldn't write to or create file " + fileName);
                }
            } else {
                ui.printError("Specified file name already exists!");
            }
        } else {
            throw new NotEnoughArgumentsSpecifiedException("Expected one argument!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Saves the current log to a file");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
        //TODO: add detailed description
    }
}
