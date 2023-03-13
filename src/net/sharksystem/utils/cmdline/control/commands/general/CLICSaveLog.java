package net.sharksystem.utils.cmdline.control.commands.general;

import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;
import net.sharksystem.utils.cmdline.model.CLIModelInterface;
import net.sharksystem.utils.cmdline.view.CLIInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CLICSaveLog extends CLICommand {

    private final CLICStringArgument fileName;

    public CLICSaveLog(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.fileName = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return null;
    }

    @Override
    public void execute(CLIInterface ui, CLIModelInterface model) throws Exception {
        String fileName = this.fileName.getValue();
        File file = new File(fileName);
        if (file.createNewFile()) {
            try {
                PrintWriter pw = new PrintWriter(fileName);
                List<String> log = model.getCommandHistoryList();

                for (String s : log) {
                    pw.println(s);
                }

                pw.println("exit");
                pw.close();

            } catch (FileNotFoundException e) {
                ui.printError("Couldn't write to or create file " + fileName);
            }
        } else {
            ui.printError("Specified file name already exists!");
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Saves the current log to a file.");
        return sb.toString();
    }

    @Override
    public String getDetailedDescription() {
        return this.getDescription();
    }
}
