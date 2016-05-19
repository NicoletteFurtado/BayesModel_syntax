package bayesmodel.knowledgetracing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import bayesmodel.constants.Constants;
import bayesmodel.model.InitMaps;
import bayesmodel.model.OutputLists;
import bayesmodel.model.Skill;
import bayesmodel.model.SkillSet;
import bayesmodel.model.StudentLogData;
import bayesmodel.util.ProcessLogData;
import bayesmodel.util.ReadFiles;
import bayesmodel.util.WriteOutput3;

public class MainClass {
	public static void main(String args[]) {

		ReadFiles reader = new ReadFiles();
		// read properties file into LinkedHashMap
		InitMaps initMaps = reader.readPropertiesFile(Constants.WORDS_FILE, Constants.ACTION_FILE);
		// read log data into StudentModel
		// StudentLogData studentLogData = reader
		// .readLogData("C:\\Users\\Nicolette\\OneDrive\\Documents\\EMBRACE\\Analysis\\With user step\\log data by student\\log_data_B0038EE_chapters_1_7.csv");
		StudentLogData studentLogData = reader.readLogData(Constants.STUDENT_LOG_DATA);

		// ArrayList<String> inputData = reader.readInputData("inputdata/text.txt", studentLogData);
		ArrayList<String> inputData = reader.readInputData(Constants.INPUT_DATA, studentLogData);
		studentLogData.setInputData(inputData);
		System.out.println(Arrays.toString(inputData.toArray()));
		System.out.println(inputData.size());
		System.out.println(studentLogData.getSentenceList().size());

		// process log data
		ProcessLogData processData = new ProcessLogData();
		processData.updateLogData(studentLogData, initMaps);
		// System.out.println("final attempt111111=" + Arrays.toString(studentLogData.getVerificationList().toArray()));
		// System.out.println("final attempt111111=" + studentLogData.getVerificationList().size());

		// initialize all skills
		HashMap<String, ArrayList<String>> sentenceToWords = initMaps.getSentenceToWords();
		System.out.println(initMaps.getSentenceToWords());

		SkillSet skillSet = new SkillSet();
		LinkedHashMap<String, ArrayList<Skill>> skillMap = skillSet.getSkillMap();
		ArrayList<String> wordList = null;
		ArrayList<String> allWordsList = new ArrayList<String>(); // list of all unique words, we do this so that
																	// duplicate skills are not added later
		for (String sentence : sentenceToWords.keySet()) {
			// System.out.println(sentence);
			// System.out.println("mmmm=" + Arrays.toString(sentenceToWords.get(sentence).toArray()));
			for (String word1 : sentenceToWords.get(sentence)) {
				// System.out.println(word1);
				wordList = new ArrayList<String>();
				if (!word1.equals(Constants.DEFAULT_WORD)) {
					// System.out.println("1=" + !word1.equals(Constants.DEFAULT_WORD));
					if (!allWordsList.contains(word1)) {
						// System.out.println("2=" + !allWordsList.contains(word1));
						allWordsList.add(word1);
						wordList.add(word1);
						// System.out.println("Added word=" + word1);
					}
				}
			}
		}

		// if (!wordList.isEmpty() || !(wordList == null)) {
		if (!allWordsList.isEmpty() && !(allWordsList == null)) { // add each unique word as a skill in skillMap in
																	// skillSet
			// for (String word : wordList) {
			for (String word : allWordsList) {

				Skill skill = new Skill();
				skill.setWord(word);
				skill.setAction(Constants.DEFAULT_ACTION);
				skill.setSkillValue(Constants.INITIAL_SKILL_VALUE);
				// skill.setSentence(Constants.DEFAULT_SENTENCE);
				ArrayList<Skill> skillList = new ArrayList<Skill>();
				skillList.add(skill);
				skillMap.put(word, skillList);
			}
			Skill skill;
			ArrayList<Skill> skillList;
			// add syntax as a skill
			skill = new Skill();
			skill.setWord(Constants.SYNTAX);
			skill.setAction(Constants.DEFAULT_ACTION);
			skill.setSkillValue(Constants.INITIAL_SKILL_VALUE);
			skillList = new ArrayList<Skill>();
			skillList.add(skill);
			skillMap.put(Constants.SYNTAX, skillList);
			// add syntax_pronoun as a skill
			skill = new Skill();
			skill.setWord(Constants.SYNTAX_PRONOUN);
			skill.setAction(Constants.DEFAULT_ACTION);
			skill.setSkillValue(Constants.INITIAL_SKILL_VALUE);
			skillList = new ArrayList<Skill>();
			skillList.add(skill);
			skillMap.put(Constants.SYNTAX_PRONOUN, skillList);
			// }
			// skillMap.put(sentence, new ArrayList<Skill>());
		}
		skillSet.setSkillMap(skillMap);
		// calculate
		// KnowledgeTracer3 k = new KnowledgeTracer3();
		// SkillSet skillSet1 = k.calculateSkill(studentLogData, skillSet, initMaps);

		KnowledgeTracer5 k2 = new KnowledgeTracer5();
		SkillSet skillSet2 = k2.calculateSkill(studentLogData, skillSet, initMaps);

		OutputLists outputLists = new OutputLists();
		outputLists.createLists(studentLogData, skillSet2, initMaps);

		WriteOutput3 writeOutput = new WriteOutput3();
		writeOutput.writeCSV(outputLists, studentLogData);
	}
}
