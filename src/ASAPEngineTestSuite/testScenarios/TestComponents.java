package ASAPEngineTestSuite.testScenarios;

import ASAPEngineTestSuite.utils.CommandListToFile;
import ASAPEngineTestSuite.utils.ScenarioParamAllocation;

public abstract class TestComponents extends ScenarioParamAllocation{
	/**
	 * Generates a command list for receiving messages in a TCP scenario.
	 *
	 * @param hostIPAddress the IP address of the host to connect to
	 * @return the command list as a string
	 */
	public static String generateReceiveScenarioCommands(String hostIPAddress) {
		StringBuilder receiveCommands = new StringBuilder();
		receiveCommands.append(sendIPAddressCommandList());
		receiveCommands
			.append(CommandListToFile.WAIT + ' ' + 1000)
			.append(System.lineSeparator())
			.append(CommandListToFile.CONNECT_TCP + " ")
			.append(hostIPAddress).append(' ')
			.append(CommandListToFile.HOST_PORT)
			.append(System.lineSeparator())
			.append(CommandListToFile.WAIT + ' ' + 500)
			.append(System.lineSeparator())
			.append(CommandListToFile.LIST_MESSAGES)
			.append(System.lineSeparator())
			.append(CommandListToFile.WAIT + ' ' + 300000)
			.append(System.lineSeparator())
			.append(CommandListToFile.EXIT);
		return receiveCommands.toString();
	}

	/**
	 * Generates a command list to send a file to the test host containing an IP address.
	 *
	 * @return the command list as a string
	 */
	static String sendIPAddressCommandList() {
		StringBuilder sb = new StringBuilder();
		sb
			.append(CommandListToFile.SEND_MESSAGE)
			.append(' ')
			.append("ipAddress_")
			.append("FILLER_PEERNAME")
			.append(CommandListToFile.TEXT_FILE_EXTENSION)
			.append(' ')
			.append(CommandListToFile.FORMAT_DESC_FILE)
			.append(System.lineSeparator());
		return sb.toString();
	}

	/**
	 * Generates a command list for the wrappers to execute that sends the logs to the test host.
	 *
	 * @param peerIndex the index of the peer
	 * @param hostAddress the IP address of the host to connect to
	 * @return the command list as a string
	 * @throws IllegalArgumentException if the peer index is invalid
	 */
	public String generateRunScenarioCommands(int peerIndex, String hostAddress) {
		int peerCount = getPeerCount();
		if (peerIndex < 1 || peerIndex > peerCount) {
			throw new IllegalArgumentException("Invalid peer index: " + peerIndex);
		}
		if (hostAddress == null || hostAddress.isEmpty()) {
			hostAddress = CommandListToFile.DEFAULT_HOST_ADDRESS;
		}
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder scenarioScript = stringBuilder
			.append(CommandListToFile.WAIT + ' ')
			.append(100 * peerCount)
			.append(System.lineSeparator())
			.append(CommandListToFile.SEND_MESSAGE).append(" snm_P")
			.append(peerIndex)
			.append(".txt ")
			.append(CommandListToFile.FORMAT_DESC_FILE)
			.append(System.lineSeparator())
			.append(CommandListToFile.SEND_MESSAGE)
			.append(" asapLogsP")
			.append(peerIndex)
			.append(".txt ")
			.append(CommandListToFile.FORMAT_DESC_FILE)
			.append(System.lineSeparator())
			.append(CommandListToFile.CONNECT_TCP + ' ')
			.append(hostAddress)
			.append(' ')
			.append(CommandListToFile.HOST_PORT)
			.append(System.lineSeparator())
			.append(CommandListToFile.WAIT + ' ' + 1000)
			.append(System.lineSeparator())
			.append(CommandListToFile.EXIT);
		return scenarioScript.toString();
	}

	abstract String generateTestScenarioCommands(int peerIndex) throws IllegalArgumentException;
}