package net.sharksystem.cmdline.sharkmessengerUI.commands.general;

import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerUI;
import net.sharksystem.cmdline.sharkmessengerUI.SharkMessengerApp;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaireBuilder;
import net.sharksystem.cmdline.sharkmessengerUI.UICommand;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandQuestionnaire;
import net.sharksystem.cmdline.sharkmessengerUI.UICommandStringArgument;

/**
 * Command for saving the log in a file.
 */
public class UICommandSaveLog extends UICommand {

    private final UICommandStringArgument fileName;

    public UICommandSaveLog(SharkMessengerApp sharkMessengerApp, SharkMessengerUI sharkMessengerUI,
                            String identifier, boolean rememberCommand) {
        super(sharkMessengerApp, sharkMessengerUI, identifier, rememberCommand);
        this.fileName = new UICommandStringArgument(sharkMessengerApp);
    }

    @Override
    public UICommandQuestionnaire specifyCommandStructure() {
        return new UICommandQuestionnaireBuilder().
                addQuestion("File path: ", this.fileName).
                build();
    }

    @Override
    public void execute() throws Exception {
        this.printTODOReimplement();
        /*
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
         */
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Saves the current log to a file.");
        return sb.toString();
    }

}
