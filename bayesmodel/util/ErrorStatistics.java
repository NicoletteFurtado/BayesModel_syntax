package bayesmodel.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import bayesmodel.constants.Constants;
import bayesmodel.model.InitMaps;
import bayesmodel.model.StudentLogData;

public class ErrorStatistics {

	private String folder = "C:\\Users\\Nicolette\\OneDrive\\Documents\\EMBRACE\\Analysis\\With user step\\log data by student\\log data with play word";
	private String inputDataFolder = "inputdata/";
	private HashMap<String, Integer> wordToHelpRequests = new HashMap<String, Integer>();
	private HashMap<String, Integer> wordToErrors = new HashMap<String, Integer>();
	private StudentLogData student;
	private StringBuilder sb = new StringBuilder("");
	private File resultFile;
	private static InitMaps initMaps;

	private static int noHelpRequests = 0;
	private static HashMap<String, Integer> helpRequestsForWord = new HashMap<String, Integer>();

	public static void main(String[] args) {
		ErrorStatistics es = new ErrorStatistics();
		readStoryFiles();
		es.analyze();
	}

	private void analyze() {
		File dir = new File(folder);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			File resultFile1 = new File(dir.getPath() + "\\Error_Statistics_help_requests.csv");
			File resultFile2 = new File(dir.getPath() + "\\Error_Statistics_errors_all.csv");
			try {
				FileUtils.writeStringToFile(resultFile1, "Student id,Help Requests\n", true);
				for (File child : directoryListing) {
					String fileName = child.getName();
					if (fileName.contains("log_data")) {
						File logfile = new File(dir.getPath() + "\\" + fileName);
						if (logfile.exists()) {
							analyzeLogFile(logfile, resultFile1);
						} else {
							System.out.println("=======> Log file does not exist..." + logfile.getName());
						}
					}
				}
				sb.append("No. of students with no help requests = " + noHelpRequests + "\n");
				sb.append("No. of help requests per word = " + helpRequestsForWord.toString());
				FileUtils.writeStringToFile(resultFile1, sb.toString(), true);
			} catch (IOException ie) {
				System.out.println(ie.getMessage());
			}

		}
	}

	private void analyzeLogFile(File logFile, File resultFile) {
		// readStudentData(logFile);
		ReadFiles readFiles = new ReadFiles();
		try {
			student = readFiles.readLogData(logFile.getCanonicalPath().toString());
			String studentId = logFile.getName().split("_")[2].split("\\.")[0];
			readInputData(readFiles, studentId);
			updateHelpRequests();
			if (wordToHelpRequests.keySet().isEmpty()) {
				noHelpRequests++;
			}
			sb.append(studentId + "," + wordToHelpRequests.toString() + "\n");
			wordToHelpRequests = new HashMap<String, Integer>();
			student = new StudentLogData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readInputData(ReadFiles readFiles, String studentid) {
		File dir = new File(inputDataFolder);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String fileName = child.getName();
				if (fileName.contains(studentid)) {
					File inputDataFile = new File(dir.getPath() + "\\" + fileName);
					if (inputDataFile.exists()) {
						ArrayList<String> inputDataList = readFiles.readInputData(inputDataFile.getAbsolutePath(),
								student);
						// processInputData(inputDataList);
						student.setInputData(processInputData(inputDataList));
						System.out.println(student.getInputData());
					} else {
						System.out.println("=======> Log file does not exist..." + inputDataFile.getName());
					}
				}
			}
		}
	}

	private ArrayList<String> processInputData(ArrayList<String> inputDataList) {
		for (String inputData : inputDataList) {
			inputData = inputData.trim();
			if (inputData.contains("pen1") || inputData.contains("pen2") || inputData.contains("pen3")
					|| inputData.contains("pen4")) {
				inputData = "pen";
			} else if (inputData.contains("corralDoor") || inputData.contains("corralArea")) {
				inputData = "corral";
			} else if (inputData.contains("pumpkinPatch") || inputData.contains("pumpkin")) {
				inputData = "pumpkins";
			} else if (inputData.contains("farmerFall")) {
				inputData = "farmer";
			} else if (inputData.contains("nearGoat")) {
				inputData = "goat";
			} else if (!inputData.contains(" ") && inputData.contains(Constants.HELP_REQUEST_ENDING)) {
				inputData = inputData.split(Constants.HELP_REQUEST_ENDING)[0];
				System.out.println("////////// " + inputData);
			}
		}
		return inputDataList;
	}

	private static void readStoryFiles() {
		ReadFiles readFiles = new ReadFiles();
		initMaps = readFiles.readPropertiesFile(Constants.WORDS_FILE, Constants.ACTION_FILE);
	}

	private void updateHelpRequests() {
		for (String inputData : student.getInputData()) {
			if (!inputData.contains(" ")) {
				addToMap(wordToHelpRequests, inputData);
				addToMap(helpRequestsForWord, inputData);
			}
		}
	}

	private void updateErrors() {
		String currSentence = "";
		String currUserStep = "";
		String currInputData = "";
		ArrayList<String> wordsInSentence;
		ArrayList<String> wordsInAction;
		String[] objectsMoved;
		// go through all actions
		for (int i = 0; i < student.getActionList().size(); i++) {
			if (student.getVerificationList().get(i).equals(Constants.INCORRECT)) {
				// check sentence
				currSentence = student.getSentenceList().get(i);
				currInputData = student.getInputData().get(i);
				objectsMoved = currInputData.split(Constants.STUDENT_INPUT_DATA_SEPARATOR);
				wordsInSentence = initMaps.getSentenceToWords().get(AnalysisUtil.convertStringToKey(currSentence));
				wordsInAction = initMaps.getSentenceToActions().get(AnalysisUtil.convertStringToKey(currSentence))
						.get(Integer.parseInt(currUserStep) - 1);
				// check words

				// update hashmap
			}
		}
	}

	private void addToMap(HashMap<String, Integer> map, String key) {
		if (map.keySet().contains(key)) {
			map.put(key, map.get(key) + 1);
		} else {
			map.put(key, 1);
		}
	}
}
