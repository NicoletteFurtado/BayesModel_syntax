package bayesmodel.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import bayesmodel.constants.Constants;
import bayesmodel.model.OutputLists;
import bayesmodel.model.StudentLogData;

public class WriteOutput3 {
	public void writeCSV(OutputLists outputLists, StudentLogData student) {
		BufferedWriter writer = null;

		String outputFile = Constants.OUTPUT_FILE;
		try {
			FileWriter fw = new FileWriter(outputFile);
			writer = new BufferedWriter(fw);
			writer.write("Student id E0237EE\n");
			// write the word lists
			writer.write(Constants.CSV_FILE_SEPARATOR);
			writer.write(Constants.CSV_FILE_SEPARATOR);
			writer.write(Constants.CSV_FILE_SEPARATOR);
			// for object moved
			writer.write(Constants.CSV_FILE_SEPARATOR);
			for (String word : outputLists.getWordList()) {
				writer.write(word + Constants.CSV_FILE_SEPARATOR);
			}
			writer.newLine();
			// write all sentences and skills into a rowList and then print that out
			String skillValueToAdd = "";
			ArrayList<String> rowList = new ArrayList<String>();
			Iterator<Map.Entry<String, ArrayList<Double>>> entries = outputLists.getWordToSkillValues().entrySet()
					.iterator();
			Map.Entry<String, ArrayList<Double>> entry = null;

			// for the first row where there is no sentence just initial skill value
			int i = 0; // sentencelist index
			int j = 0; // wordtoskill index
			rowList.add(Constants.EMPTY_STRING);
			rowList.add(Constants.EMPTY_STRING);
			rowList.add(Constants.EMPTY_STRING);
			rowList.add(Constants.EMPTY_STRING);
			while (entries.hasNext()) {
				entry = entries.next();
				rowList.add(entry.getValue().get(j).toString());
				// entry = entries.next();
				// System.out.print(j + entry.getKey() + " ");
			}
			for (String str : rowList) {
				writer.write(str);
				writer.write(Constants.CSV_FILE_SEPARATOR);
			}
			writer.newLine();
			writer.flush();
			rowList.clear();
			// for later rows && j < entry.getValue().size()
			entries = outputLists.getWordToSkillValues().entrySet().iterator();
			entry = entries.next();
			// entry = entries.next();
			j = j + 1;
			int resetCount = 0;
			while (i < outputLists.getSentenceList().size() && resetCount <= outputLists.getSentenceList().size()
					&& j < entry.getValue().size()) {
				rowList.add(outputLists.getSentenceList().get(i));
				rowList.add(student.getVerificationList().get(i));
				rowList.add(student.getUserStep().get(i).toString());
				rowList.add(student.getInputData().get(i).toString());
				for (String key : outputLists.getWordToSkillValues().keySet()) {
					rowList.add(outputLists.getWordToSkillValues().get(key).get(j).toString());
				}
				System.out.println();
				// write rowList
				for (String str : rowList) {
					// System.out.println("str=" + str);
					writer.write(str);
					writer.write(Constants.CSV_FILE_SEPARATOR);
				}
				i = i + 1;
				j = j + 1;
				entries = outputLists.getWordToSkillValues().entrySet().iterator();
				resetCount = resetCount + 1;
				rowList.clear();

				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
