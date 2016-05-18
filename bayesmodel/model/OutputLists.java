package bayesmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import bayesmodel.constants.Constants;
import bayesmodel.util.AnalysisUtil;

public class OutputLists {
	// private ArrayList<String> skillList;
	// private ArrayList<ArrayList<String>> outputList;
	private ArrayList<String> wordList;
	private ArrayList<String> sentenceList;
	private LinkedHashMap<String, ArrayList<Double>> wordToSkillValues; // skillList

	public OutputLists() {
		wordList = new ArrayList<String>();
		sentenceList = new ArrayList<String>();
		wordToSkillValues = new LinkedHashMap<String, ArrayList<Double>>();
	}

	public void createLists(StudentLogData student, SkillSet skillSet, InitMaps initMaps) {
		System.out.println("Here");
		System.out.println(skillSet.getSkillMap().keySet());
		// create wordLists
		createWordList(student, skillSet, initMaps);
		// create sentence list
		createSentenceList(student, initMaps);
		// create skill list
		// createSkillList(student, skillSet, initMaps);
		createSkillList3(student, skillSet, initMaps);
		System.out.println("donee");
	}

	private void createSentenceList(StudentLogData student, InitMaps initMaps) {
		String sentenceKey = "";
		// create a list for each user step
		// sentenceList.add(Constants.EMPTY_STRING);
		for (int i = 0; i < student.getUserStep().size(); i++) {
			// ArrayList<String> skillList = new ArrayList<String>();
			// add sentence
			if (!student.getSentenceList().get(i).equals(Constants.DEFAULT_SENTENCE)) {
				sentenceKey = AnalysisUtil.convertStringToKey(student.getSentenceList().get(i));
				sentenceList.add(initMaps.getSentenceToText().get(sentenceKey));
			}
		}
		// System.out.println("done with sentence");
		// System.out.println(sentenceList.size());
	}

	private void createWordList(StudentLogData student, SkillSet skillSet, InitMaps initMaps) {
		ArrayList<String> wordsInSentence;
		for (String sentence : initMaps.getSentenceToWords().keySet()) {
			wordsInSentence = initMaps.getSentenceToWords().get(sentence);
			for (int i = 0; i < wordsInSentence.size(); i++) {
				if (!wordList.contains(wordsInSentence.get(i))
						&& !wordsInSentence.get(i).equals(Constants.DEFAULT_WORD)) {
					wordList.add(wordsInSentence.get(i));
				}
			}
			// add syntax as a word
			// wordList.add(Constants.SYNTAX);
		}
		// add syntax as a word
		wordList.add(Constants.SYNTAX);
		// System.out.println("word list1=" + Arrays.toString(wordList.toArray()));
		// System.out.println("Done with words");
	}

	private void createSkillList(StudentLogData student, SkillSet skillSet, InitMaps initMaps) {
		String sentence = "";
		String sentenceKey = "";
		ArrayList<Skill> skillObjectList;
		System.out.println("word list2=" + Arrays.toString(wordList.toArray()));
		if (!(wordList.isEmpty()) || !(wordList == null)) {
			for (int i = 0; i < wordList.size(); i++) {
				// put word in map if not contains
				if (!wordToSkillValues.keySet().contains(wordList.get(i))) {
					wordToSkillValues.put(wordList.get(i), new ArrayList<Double>());
					// System.out.println(wordList.get(i));
				}
			}
			// add syntax as a word
			// wordToSkillValues.put(Constants.SYNTAX, new ArrayList<Double>());
			// pad values if necessary
			double mostRecentValue = Constants.INITIAL_SKILL_VALUE;
			ArrayList<String> sentenceForAWord;
			// System.out.println("/////////" + skillSet.getSkillMap().keySet());
			for (String word : skillSet.getSkillMap().keySet()) {
				System.out.println(word);

				// get arraylist of skill objects for a word
				skillObjectList = skillSet.getSkillMap().get(word);
				// System.out.println("word= " + word + Arrays.toString(skillObjectList.toArray()));

				for (Skill s : skillObjectList) {
					System.out.print("word= " + s.getWord() + s.getSkillValue() + " ");
				}
				System.out.println();
				// add skill sentence for a word into an arraylist
				sentenceForAWord = new ArrayList<String>();
				for (Skill skill : skillObjectList) {
					if (skill.getSentence().isEmpty() || skill.getSentence() == null)
						continue;
					sentenceForAWord.add(skill.getSentence());
				}
				// get target index
				// pad the values until target index is reached
				mostRecentValue = Constants.INITIAL_SKILL_VALUE;
				int i = 0;
				int skillObjectListIndex = 1;
				wordToSkillValues.get(word).add(mostRecentValue);
				while (i < (sentenceList.size()) && wordToSkillValues.get(word).size() <= sentenceList.size()) {
					// System.out.println(skillObjectList.get(skillObjectListIndex).getSentence());
					// System.out.println(student.getSentenceList().get(i));
					if (skillObjectListIndex < skillObjectList.size()
							&& skillObjectList.get(skillObjectListIndex).getSentence()
									.equals(student.getSentenceList().get(i))) {
						for (; skillObjectListIndex < skillObjectList.size(); skillObjectListIndex++) {
							if (skillObjectListIndex < skillObjectList.size()
									&& skillObjectList.get(skillObjectListIndex).getSentence()
											.equals(student.getSentenceList().get(i))) {
								mostRecentValue = skillObjectList.get(skillObjectListIndex).getSkillValue();
								wordToSkillValues.get(word).add(mostRecentValue);
								// skillObjectListIndex = skillObjectListIndex + 1;
								i = i + 1;
								// skillObjectListIndex = skillObjectListIndex + 1;
								// System.out.println("Added most recent1 = " + mostRecentValue);
							} else {
								break;
							}
						}
					} else {
						wordToSkillValues.get(word).add(mostRecentValue);
						i = i + 1;
						// System.out.println("Added most recent2 = " + mostRecentValue);
					}
					// i = i + 1;
				}
			}
		}

	}

	private void createSkillList2(StudentLogData student, SkillSet skillSet, InitMaps initMaps) {
		ArrayList<Skill> skillObjectList;
		System.out.println("word list2=" + Arrays.toString(wordList.toArray()));
		if (!(wordList.isEmpty()) || !(wordList == null)) {
			for (int i = 0; i < wordList.size(); i++) {
				// put word in map if not contains
				if (!wordToSkillValues.keySet().contains(wordList.get(i))) {
					wordToSkillValues.put(wordList.get(i), new ArrayList<Double>());
				}
			}
			double mostRecentValue = Constants.INITIAL_SKILL_VALUE;
			for (String word : skillSet.getSkillMap().keySet()) {
				skillObjectList = skillSet.getSkillMap().get(word);
				mostRecentValue = Constants.INITIAL_SKILL_VALUE;
				int i = 0;
				int skillObjectListIndex = 1;
				wordToSkillValues.get(word).add(mostRecentValue);
				while (i < (sentenceList.size()) && wordToSkillValues.get(word).size() <= sentenceList.size()) {
					// System.out.println(skillObjectList.get(skillObjectListIndex).getSentence());
					// System.out.println(student.getSentenceList().get(i));
					if (skillObjectListIndex < skillObjectList.size()
							&& skillObjectList.get(skillObjectListIndex).getSentence()
									.equals(student.getSentenceList().get(i))) {
						for (; skillObjectListIndex < skillObjectList.size(); skillObjectListIndex++) {
							if (skillObjectListIndex < skillObjectList.size()
									&& skillObjectList.get(skillObjectListIndex).getSentence()
											.equals(student.getSentenceList().get(i))
									&& !skillObjectList.get(skillObjectListIndex).getVerification().isEmpty()) {
								mostRecentValue = skillObjectList.get(skillObjectListIndex).getSkillValue();
								wordToSkillValues.get(word).add(mostRecentValue);
								// skillObjectListIndex = skillObjectListIndex + 1;
								i = i + 1;
								// skillObjectListIndex = skillObjectListIndex + 1;
								// System.out.println("Added most recent1 = " + mostRecentValue);
							} else {
								break;
							}
						}
					} else {
						wordToSkillValues.get(word).add(mostRecentValue);
						i = i + 1;
						// System.out.println("Added most recent2 = " + mostRecentValue);
					}
					// i = i + 1;
				}
			}
		}
	}

	private void createSkillList3(StudentLogData student, SkillSet skillSet, InitMaps initMaps) {
		ArrayList<Skill> skillObjectList;
		System.out.println("word list2=" + Arrays.toString(wordList.toArray()));
		if (!(wordList.isEmpty()) || !(wordList == null)) {
			for (int i = 0; i < wordList.size(); i++) {
				// put word in map if not contains
				if (!wordToSkillValues.keySet().contains(wordList.get(i))) {
					wordToSkillValues.put(wordList.get(i), new ArrayList<Double>());
				}
			}
		}
		double mostRecentValue = Constants.INITIAL_SKILL_VALUE;
		for (String word : skillSet.getSkillMap().keySet()) {
			skillObjectList = skillSet.getSkillMap().get(word);
			mostRecentValue = Constants.INITIAL_SKILL_VALUE;
			int i = 0;
			int skillObjectListIndex = 1;
			wordToSkillValues.get(word).add(mostRecentValue);
			while (i < sentenceList.size()) {
				// && wordToSkillValues.get(word).size() <= sentenceList.size()
				// System.out.println(skillObjectList.get(skillObjectListIndex).getSentence());
				// System.out.println(student.getSentenceList().get(i));
				if (skillObjectListIndex < skillObjectList.size()
						&& skillObjectList.get(skillObjectListIndex).getSentence()
								.equals(student.getSentenceList().get(i))) {
					for (; skillObjectListIndex < skillObjectList.size(); skillObjectListIndex++) {
						if (skillObjectListIndex < skillObjectList.size()
								&& skillObjectList.get(skillObjectListIndex).getSentence()
										.equals(student.getSentenceList().get(i))) {
							// && !skillObjectList.get(skillObjectListIndex).getVerification().isEmpty()
							mostRecentValue = skillObjectList.get(skillObjectListIndex).getSkillValue();
							wordToSkillValues.get(word).add(mostRecentValue);
							// skillObjectListIndex = skillObjectListIndex + 1;
							i = i + 1;
							// skillObjectListIndex = skillObjectListIndex + 1;
							// System.out.println("Added most recent1 = " + mostRecentValue + "for word " + word + " "
							// + skillObjectList.get(skillObjectListIndex).getSentence() + " "
							// + skillObjectList.get(skillObjectListIndex).getAction());
						} else {
							break;
						}
					}
				} else {
					wordToSkillValues.get(word).add(mostRecentValue);
					i = i + 1;
					// System.out.println("Added most recent2 = " + mostRecentValue);
				}
				// i = i + 1;
			}
		}
		// System.out.println(wordToSkillValues);
		// }
	}

	public ArrayList<String> getWordList() {
		return wordList;
	}

	public void setWordList(ArrayList<String> wordList) {
		this.wordList = wordList;
	}

	public ArrayList<String> getSentenceList() {
		return sentenceList;
	}

	public void setSentenceList(ArrayList<String> sentenceList) {
		this.sentenceList = sentenceList;
	}

	public LinkedHashMap<String, ArrayList<Double>> getWordToSkillValues() {
		return wordToSkillValues;
	}

	public void setWordToSkillValues(LinkedHashMap<String, ArrayList<Double>> wordToSkillValues) {
		this.wordToSkillValues = wordToSkillValues;
	}

}
