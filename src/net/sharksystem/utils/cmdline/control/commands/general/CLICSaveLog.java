package net.sharksystem.utils.cmdline.control.commands.general;

import net.sharksystem.utils.cmdline.control.CLICQuestionnaireBuilder;
import net.sharksystem.utils.cmdline.control.CLICommand;
import net.sharksystem.utils.cmdline.control.CLICQuestionnaire;
import net.sharksystem.utils.cmdline.control.CLICStringArgument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Command for saving the log in a file.
 */
public class CLICSaveLog extends CLICommand {

    private final CLICStringArgument fileName;

    public CLICSaveLog(String identifier, boolean rememberCommand) {
        super(identifier, rememberCommand);
        this.fileName = new CLICStringArgument();
    }

    @Override
    public CLICQuestionnaire specifyCommandStructure() {
        return new CLICQuestionnaireBuilder().
                addQuestion("File path: ", this.fileName).
                build();
    }

    @Override
    public void execute() throws Exception {
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

}
