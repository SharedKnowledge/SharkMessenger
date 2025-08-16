package ASAPEngineTestSuite.testScenarios;


import ASAPEngineTestSuite.ScenarioIndex;
import ASAPEngineTestSuite.utils.CommandListToFile;
import ASAPEngineTestSuite.utils.fileUtils.FileUtils;

import java.io.FileOutputStream;


public class ScenarioTCPChain extends TestComponents {
	ScenarioIndex si;
	private static final int SCENARIO_INDEX = 1;
	private static final int STARTING_TCP_PORT = 4443;
	private static final int WAIT_TIME_IN_MILLIS = 8000;

	public ScenarioTCPChain(CommandListToFile commandListToFile) {
		super(commandListToFile);
		si = ScenarioIndex.TCP_CHAIN;

	}

	public void sendMessage(StringBuilder scenarioScript, String peerSpecificFileNameToBeSent) {
		scenarioScript
			.append(CommandListToFile.SEND_MESSAGE)
			.append(" ")
			.append(peerSpecificFileNameToBeSent)
			.append(' ')
			.append(CommandListToFile.FORMAT_DESC_FILE)
			.append(System.lineSeparator());
	}

	public void waitAndOpenPort(int peerIndex, StringBuilder scenarioScript) {
		scenarioScript
			.append(CommandListToFile.WAIT + ' ')
			.append(WAIT_TIME_IN_MILLIS * 100)
			.append(System.lineSeparator())
			.append(CommandListToFile.OPEN_TCP + ' ')
			.append(STARTING_TCP_PORT + peerIndex)
			.append(System.lineSeparator());
	}

	public void waitAndConnect(int peerIndex, StringBuilder scenarioScript) {
		scenarioScript
			.append(CommandListToFile.WAIT + ' ')
			.append(WAIT_TIME_IN_MILLIS * 100)
			.append(System.lineSeparator())
			.append(CommandListToFile.CONNECT_TCP + ' ')
			.append("FILLER_PREVIOUS_PEER")
			.append(' ')
			.append(STARTING_TCP_PORT + (peerIndex - 1))
			.append(System.lineSeparator());
	}

	public void waitAndListMessages(StringBuilder scenarioScript) {
		scenarioScript.append(CommandListToFile.WAIT + ' ')
				.append(WAIT_TIME_IN_MILLIS * 100)
				.append(System.lineSeparator())
				.append(CommandListToFile.LIST_MESSAGES)
				.append(System.lineSeparator())
				.append(CommandListToFile.EXIT);
	}

	public void testScenarioCommandsToFile(int peerCount) {
		for (int i = 1; i <= peerCount; i++) {
			String commands = generateTestScenarioCommands(i);
			try {
				String fileName = "scenarioScriptP" + i + ".txt";
				FileUtils.writeToFile(new FileOutputStream(fileName), commands);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
