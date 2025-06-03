package ASAPEngineTestSuite.testScenarios;


import ASAPEngineTestSuite.utils.CommandListToFile;
import ASAPEngineTestSuite.utils.fileUtils.FileUtils;

import java.io.FileOutputStream;


public class ScenarioTCPChain extends TestComponents {
	private static final int SCENARIO_INDEX = 1;
	private static final int STARTING_TCP_PORT = 4443;
	private static final int WAIT_TIME_IN_MILLIS = 8000;
	final CommandListToFile commandListToFile;

	public ScenarioTCPChain(CommandListToFile commandListToFile) {
		this.commandListToFile = commandListToFile;
	}

	/**
	 * Generates a commandlist for the peers to execute the test scenario: TCP, chain communication.
	 * @param peerIndex the index of the peer
	 * @return the commandlist as a string
	 * @throws IllegalArgumentException if the peer index is invalid
	 */
	@Override
	public String generateTestScenarioCommands(int peerIndex) throws IllegalArgumentException {
		int peerCount = commandListToFile.getScenarioParamAllocation().getPeerCount();
		if (peerIndex < 1 || peerIndex > peerCount) {
			throw new IllegalArgumentException("Invalid peer index: " + peerIndex);
		}
		// a waiting period in milliseconds
		String peerSpecificFileNameToBeSent = peerIndex + "_" + this.commandListToFile.getScenarioParamAllocation().getFileNameToBeSent();
		//this block consists of the common commands for all peers.
		StringBuilder scenarioScript = new StringBuilder();
		if (peerIndex == peerCount) {
			scenarioScript
				.append(CommandListToFile.SEND_MESSAGE)
				.append(" ")
				.append(peerSpecificFileNameToBeSent)
				.append(' ')
				.append(CommandListToFile.FORMAT_DESC_FILE)
				.append(System.lineSeparator());
		}
		if (peerIndex < peerCount - 1) {
			scenarioScript
				.append(CommandListToFile.WAIT + ' ')
				.append(WAIT_TIME_IN_MILLIS * 100)
				.append(System.lineSeparator())
				.append(CommandListToFile.OPEN_TCP + ' ')
				.append(STARTING_TCP_PORT + peerIndex)
				.append(System.lineSeparator());
		}
		if (peerIndex > 1) {
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
		scenarioScript.append(CommandListToFile.WAIT + ' ')
				.append(WAIT_TIME_IN_MILLIS * 100)
				.append(System.lineSeparator())
				.append(CommandListToFile.LIST_MESSAGES)
				.append(System.lineSeparator())
				.append(CommandListToFile.EXIT);
		return scenarioScript.toString();
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
