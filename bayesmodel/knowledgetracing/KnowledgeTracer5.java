package bayesmodel.knowledgetracing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import bayesmodel.constants.Constants;
import bayesmodel.model.InitMaps;
import bayesmodel.model.Skill;
import bayesmodel.model.SkillSet;
import bayesmodel.model.StudentLogData;
import bayesmodel.util.AnalysisUtil;

public class KnowledgeTracer5 {
	HashMap<String, Skill> wordToValueMap; // word to skill map
	LinkedHashMap<String, ArrayList<Skill>> skillMap;

	public KnowledgeTracer5() {
		wordToValueMap = new HashMap<String, Skill>();
		skillMap = new LinkedHashMap<String, ArrayList<Skill>>();
	}

	public SkillSet calculateSkill(StudentLogData studentLogData, SkillSet skillSet, InitMaps initMaps) {
		SkillSet skillSet1 = new SkillSet();
		skillMap = skillSet.getSkillMap();

		// initialize wordToValueMap so that the last skill value of the word is stored
		for (String word : skillMap.keySet()) {
			wordToValueMap.put(word, skillMap.get(word).get(0));
			// System.out.println("wordToValue " + word);
		}
		// add syntax to wordToValue
		wordToValueMap.put(Constants.SYNTAX, skillMap.get(Constants.SYNTAX).get(0));
		System.out.println(wordToValueMap);

		LinkedHashMap<String, ArrayList<String>> actionMap = new LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, ArrayList<String>> sentenceMap = new LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, ArrayList<Integer>> userStepMap = new LinkedHashMap<String, ArrayList<Integer>>();
		LinkedHashMap<String, ArrayList<String>> inputDataMap = new LinkedHashMap<String, ArrayList<String>>();
		LinkedHashMap<String, ArrayList<String>> verificationMap = new LinkedHashMap<String, ArrayList<String>>();

		String sentenceKey = "";

		ArrayList<String> tempSentenceList = new ArrayList<String>();
		ArrayList<String> tempActionList = new ArrayList<String>();
		ArrayList<Integer> tempUserStepList = new ArrayList<Integer>();
		ArrayList<String> tempInputDataList = new ArrayList<String>();
		ArrayList<String> tempVerificationList = new ArrayList<String>();

		String currSentence = "";
		String currAction = "";
		int currUserStep;
		String currInputData = "";
		String currVerification = "";

		for (int i = 0; i < studentLogData.getVerificationList().size(); i++) {
			// get the current sentence and action
			// System.out.println("i=" + i);
			// System.out.println(Arrays.toString(studentLogData.getSentenceList().toArray()));
			currSentence = studentLogData.getSentenceList().get(i);
			currAction = studentLogData.getActionList().get(i);
			currUserStep = studentLogData.getUserStep().get(i);
			currInputData = studentLogData.getInputData().get(i);
			currVerification = studentLogData.getVerificationList().get(i);

			sentenceKey = studentLogData.getSentenceList().get(i);

			if (sentenceMap.keySet().contains(sentenceKey)) {
				tempSentenceList = sentenceMap.get(sentenceKey);
			} else {
				tempSentenceList = new ArrayList<String>();
				tempSentenceList.add(currSentence);
			}
			tempSentenceList.add(currSentence);
			sentenceMap.put(sentenceKey, tempSentenceList);

			// add action
			if (actionMap.keySet().contains(sentenceKey)) {
				tempActionList = actionMap.get(sentenceKey);
			} else {
				tempActionList = new ArrayList<String>();
			}
			tempActionList.add(currAction);
			actionMap.put(sentenceKey, tempActionList);

			// add inputdata
			if (inputDataMap.keySet().contains(sentenceKey)) {
				tempInputDataList = inputDataMap.get(sentenceKey);
			} else {
				tempInputDataList = new ArrayList<String>();
			}
			tempInputDataList.add(currInputData);
			inputDataMap.put(sentenceKey, tempInputDataList);

			// add userStep
			if (userStepMap.keySet().contains(sentenceKey)) {
				tempUserStepList = userStepMap.get(sentenceKey);
			} else {
				tempUserStepList = new ArrayList<Integer>();
			}
			tempUserStepList.add(currUserStep);
			userStepMap.put(sentenceKey, tempUserStepList);

			// add verification
			if (verificationMap.keySet().contains(sentenceKey)) {
				tempVerificationList = verificationMap.get(sentenceKey);
			} else {
				tempVerificationList = new ArrayList<String>();
			}
			tempVerificationList.add(currVerification);
			verificationMap.put(sentenceKey, tempVerificationList);
		}
		evaluateSentence(actionMap, sentenceMap, userStepMap, inputDataMap, verificationMap, initMaps, studentLogData);
		skillSet1.setSkillMap(skillMap);
		for (String word : skillMap.keySet()) {
			System.out.println(word);
			for (int i = 0; i < skillMap.get(word).size(); i++)
				System.out.println(skillMap.get(word).get(i).getSkillValue() + " "
						+ skillMap.get(word).get(i).getSentence() + " " + skillMap.get(word).get(i).getAction());

		}
		return skillSet1;
	}

	private void evaluateSentence(LinkedHashMap<String, ArrayList<String>> actionMap,
			LinkedHashMap<String, ArrayList<String>> sentenceMap,
			LinkedHashMap<String, ArrayList<Integer>> userStepMap,
			LinkedHashMap<String, ArrayList<String>> inputDataMap,
			LinkedHashMap<String, ArrayList<String>> verificationMap, InitMaps initMaps, StudentLogData student) {
		double prevSkillValue = 0.0;
		int prevUserStep = 0;
		String prevInputData = "";
		String prevAction = "";
		double newSkill = 0.0;
		double skillEvaluated = 0.0;
		HashSet<String> playWordSet;
		// for each sentence
		System.out.println("input data map = " + inputDataMap);
		for (String sentence : actionMap.keySet()) {
			// playwords for this sentence
			playWordSet = new HashSet<String>();
			// for each step update each skill
			System.err.println("/////////// " + sentence);
			// get local arrayLists
			ArrayList<String> tempSentenceList = sentenceMap.get(sentence);
			ArrayList<String> tempActionList = actionMap.get(sentence);
			ArrayList<Integer> tempUserStepList = userStepMap.get(sentence);
			ArrayList<String> tempInputDataList = inputDataMap.get(sentence);
			ArrayList<String> tempVerificationList = verificationMap.get(sentence);
			// go through each action
			for (int i = 0; i < tempActionList.size(); i++) {
				HashMap<String, Boolean> wordToVerif = checkIncorrectSkill2(student, initMaps, sentence,
						tempInputDataList.get(i), tempUserStepList.get(i));
				if (wordToVerif != null) {
					// for move
					// just in case computer move actions get considered
					if ((tempActionList.get(i).equals(Constants.MOVE_TO_HOTSPOT) || tempActionList.get(i).equals(
							Constants.MOVE_TO_OBJECT))
							&& !tempVerificationList.get(i).isEmpty()) {
						System.out.println("verif " + tempVerificationList.get(i));
						// unique step and sentence combination only ignore if previous action was not play word
						// if (prevUserStep != student.getUserStep().get(i).intValue()) {
						if (!prevInputData.equals(tempInputDataList.get(i))) {
							System.out.println("prevInputdata = " + prevInputData);
							// || prevAction.equals(Constants.PLAY_WORD)
							// HashMap<String, Boolean> wordToVerif = checkIncorrectSkill2(student, initMaps, sentence,
							// tempInputDataList.get(i), tempUserStepList.get(i));
							if (tempVerificationList.get(i).equals(Constants.CORRECT)) {
								System.err.println("wordToVerif correct" + wordToVerif);
								for (String word : wordToVerif.keySet()) {
									if (wordToValueMap.keySet().contains(word)) { // some words that the student moved
																					// to
																					// are
										// not skills

										prevSkillValue = wordToValueMap.get(word).getSkillValue();
										// change skills differently if they are in playWords list
										System.err.println(" correct1" + Arrays.toString(playWordSet.toArray()));
										if (playWordSet.contains(word)) {
											System.err.println(" correct" + Arrays.toString(playWordSet.toArray()));
											skillEvaluated = this.calcCorrectPlayWord(student, prevSkillValue);
										} else
											skillEvaluated = this.calcCorrect(student, prevSkillValue);
										newSkill = calcNewSkillValue(student, skillEvaluated);
										updateSkills(student, newSkill, word, tempVerificationList.get(i), sentence,
												tempActionList.get(i), tempUserStepList.get(i).intValue());
										System.out.println("updated correct");
									}
								}
							} else if (tempVerificationList.get(i).equals(Constants.INCORRECT)) {
								System.err.println("wordToVerif incorrect" + wordToVerif);
								for (String word : wordToVerif.keySet()) {
									if (wordToValueMap.keySet().contains(word)) { // some words that the student moved
																					// to
																					// are
																					// not skills
										if (wordToVerif.get(word)) {
											prevSkillValue = wordToValueMap.get(word).getSkillValue();
											// skillEvaluated = this.calcCorrect(studentLogData, prevSkillValue);
											System.err.println(" incorrect correct1"
													+ Arrays.toString(playWordSet.toArray()));
											if (playWordSet.contains(word)) {
												System.out.println(" incorrect correct"
														+ Arrays.toString(playWordSet.toArray()));
												skillEvaluated = this.calcCorrectPlayWord(student, prevSkillValue);
											} else
												skillEvaluated = this.calcCorrect(student, prevSkillValue);
											System.out.println("in incorrect correct " + word);
										} else {
											prevSkillValue = wordToValueMap.get(word).getSkillValue();
											System.err.println(" incorrect incorrect1"
													+ Arrays.toString(playWordSet.toArray()));
											if (playWordSet.contains(word)) {
												System.out.println(" incorrect incorrect"
														+ Arrays.toString(playWordSet.toArray()));
												skillEvaluated = this.calcIncorrectPlayWord(student, prevSkillValue);
											} else
												skillEvaluated = this.calcIncorrect(student, prevSkillValue);
											System.out.println("in incorrect incorrect " + word);
										}
										newSkill = calcNewSkillValue(student, skillEvaluated);
										// updateSkills(studentLogData, initMaps, newSkill, word, i);
										updateSkills(student, newSkill, word, tempVerificationList.get(i), sentence,
												tempActionList.get(i), tempUserStepList.get(i).intValue());
										System.out.println("updated incorrect");
									}
								}
							}
						}
						// prevUserStep = student.getUserStep().get(i).intValue();
						// prevAction = student.getActionList().get(i);
						// prevInputData = student.getInputData().get(i);
						// }
						// prevUserStep = student.getUserStep().get(i).intValue();
						// prevAction = student.getActionList().get(i);

					} else if (tempActionList.get(i).equals(Constants.PLAY_WORD)) {
						// for play word
						// also treat as incorrect, but with regular probabilities
						for (String word : wordToVerif.keySet()) {
							if (wordToValueMap.keySet().contains(word)) {
								playWordSet.add(word);
								String wordInMap = word;
								System.out.println("wordInMap=" + wordInMap);
								prevSkillValue = wordToValueMap.get(wordInMap).getSkillValue();
								skillEvaluated = this.calcIncorrect(student, prevSkillValue);
								System.out.println("in incorrect play word " + wordInMap);
								newSkill = calcNewSkillValue(student, skillEvaluated);
								// updateSkills(studentLogData, initMaps, newSkill, word, i);
								updateSkills(student, newSkill, wordInMap, tempVerificationList.get(i), sentence,
										tempActionList.get(i), tempUserStepList.get(i).intValue());
								System.out.println("updated incorrect play word");
							}
						}
					}
					// prevUserStep = student.getUserStep().get(i).intValue();
					// prevAction = student.getActionList().get(i);
					prevInputData = tempInputDataList.get(i);
				}
			}
		}
	}

	private double calcCorrect(StudentLogData student, double prevSkillValue) {

		return (prevSkillValue * (1 - student.getSlip()))
				/ (prevSkillValue * (1 - student.getSlip()) + (1 - prevSkillValue) * student.getGuess());

	}

	private double calcIncorrect(StudentLogData student, double prevSkillValue) {

		return (prevSkillValue * student.getSlip())
				/ ((student.getSlip() * prevSkillValue) + ((1 - student.getGuess()) * (1 - prevSkillValue)));

	}

	private double calcCorrectPlayWord(StudentLogData student, double prevSkillValue) {

		System.out.println("in correct play word");
		return (prevSkillValue * (1 - student.getSlip2()))
				/ (prevSkillValue * (1 - student.getSlip2()) + (1 - prevSkillValue) * student.getGuess2());
	}

	private double calcIncorrectPlayWord(StudentLogData student, double prevSkillValue) {
		System.out.println("in incorrect play word");
		return (prevSkillValue * student.getSlip2())
				/ ((student.getSlip2() * prevSkillValue) + ((1 - student.getGuess2()) * (1 - prevSkillValue)));

	}

	private double calcNewSkillValue(StudentLogData student, double skillEvaluated) {
		return skillEvaluated + ((1 - skillEvaluated) * student.getTransition());
	}

	private void updateSkills(StudentLogData studentLogData, double newSkill, String word, String verification,
			String sentence, String action, int userStep) {
		// String currAction = studentLogData.getActionList().get(count);
		Skill skill = new Skill();
		skill.setAction(action);
		skill.setSkillValue(newSkill);
		// skill.setWord(wordInList);
		skill.setWord(word);
		skill.setUserStep(userStep);
		skill.setVerification(verification);
		// System.out.println(studentLogData.getSentenceList().get(i));
		skill.setSentence(sentence);
		// skillMap.get(wordInList).add(skill);
		skillMap.get(word).add(skill);
		//
		wordToValueMap.put(word, skill);
		System.out.println("changed value " + word + " " + newSkill + " for sentence " + skill.getSentence()
				+ " for step " + userStep);
	}

	// set false for the word that the student got wrong
	private HashMap<String, Boolean> checkIncorrectSkill2(StudentLogData student, InitMaps initMaps, String sentence,
			String wordsMoved, int userStep) {
		// System.err.println("entreed check incorrect 2");
		// String objectsMoved[] = student.getInputData().get(count).split(Constants.STUDENT_INPUT_DATA_SEPARATOR);
		String objectsMoved[] = wordsMoved.split(Constants.STUDENT_INPUT_DATA_SEPARATOR);
		System.out.println("///////// " + userStep);
		HashMap<String, Boolean> wordToVerif = null;
		if (initMaps.getSentenceToActions().get(AnalysisUtil.convertStringToKey(sentence)).size() >= userStep) {
			ArrayList<String> actionWords = initMaps.getSentenceToActions()
					.get(AnalysisUtil.convertStringToKey(sentence)).get(userStep - 1);
			for (int i = 0; i < objectsMoved.length; i++) {
				if (objectsMoved[i].contains("pen1") || objectsMoved[i].contains("pen2")
						|| objectsMoved[i].contains("pen3") || objectsMoved[i].contains("pen4")) {
					objectsMoved[i] = "pen";
				} else if (objectsMoved[i].contains("corralDoor") || objectsMoved[i].contains("corralArea")) {
					objectsMoved[i] = "corral";
				} else if (objectsMoved[i].contains("pumpkinPatch") || objectsMoved[i].contains("pumpkin")) {
					objectsMoved[i] = "pumpkins";
				} else if (objectsMoved[i].contains("farmerFall")) {
					objectsMoved[i] = "farmer";
				} else if (objectsMoved[i].contains("nearGoat")) {
					objectsMoved[i] = "goat";
				}
			}
			// HashMap<String, Boolean> wordToVerif = new HashMap<String, Boolean>();
			wordToVerif = new HashMap<String, Boolean>();
			if (objectsMoved.length == 1) {
				wordToVerif.put(objectsMoved[0].trim(), false);
			} else if (objectsMoved.length > 1) {
				System.err.println("objects moved " + Arrays.toString(objectsMoved));
				// String currSentence = student.getSentenceList().get(count);
				// List of correct words for that step
				System.out.println("userStep " + userStep);
				// ArrayList<String> actionWords = initMaps.getSentenceToActions()
				// .get(AnalysisUtil.convertStringToKey(sentence)).get(userStep - 1);
				// student.getUserStep().get(userStep) - 1
				System.err.println("actionWords " + Arrays.toString(actionWords.toArray()));
				// String currSentence = student.getSentenceList().get(count);
				// HashMap<String, Boolean> wordToVerif = new HashMap<String, Boolean>();
				// add syntax to wordToVerif defalut false
				wordToVerif.put(Constants.SYNTAX, false);
				// case for pen
				// for (int i = 0; i < objectsMoved.length; i++) {
				// if (objectsMoved[i].contains("pen1") || objectsMoved[i].contains("pen2")
				// || objectsMoved[i].contains("pen3") || objectsMoved[i].contains("pen4")) {
				// objectsMoved[i] = "pen";
				// } else if (objectsMoved[i].contains("corralDoor") || objectsMoved[i].contains("corralArea")) {
				// objectsMoved[i] = "corral";
				// } else if (objectsMoved[i].contains("pumpkinPatch") || objectsMoved[i].contains("pumpkin")) {
				// objectsMoved[i] = "pumpkins";
				// } else if (objectsMoved[i].contains("farmerFall")) {
				// objectsMoved[i] = "farmer";
				// } else if (objectsMoved[i].contains("nearGoat")) {
				// objectsMoved[i] = "goat";
				// }
				// }
				// get the action for this step
				for (int i = 0; i < actionWords.size(); i++) {
					if (actionWords.get(i).equalsIgnoreCase(objectsMoved[i])) {
						// System.out.println("action word=" + actionWords.get(i));
						// System.out.println("ojbjeys moved=" + objectsMoved[i]);
						wordToVerif.put(actionWords.get(i), true);
						// understood everything about the sentence including syntax
						// wordToVerif.put(Constants.SYNTAX, true);
					} else {
						wordToVerif.put(actionWords.get(i), false);
						// if (actionWords.contains(objectsMoved[i])) {
						// if the students moved the correct words in the wrong order
						// wordToVerif.put(Constants.SYNTAX, true);
						// }
					}
				}
				// check if all words in actionWords are present in objectsMoved
				int flag = 99;
				for (int i = 0; i < objectsMoved.length; i++) {
					if (actionWords.contains(objectsMoved[i])) {
						flag = 1;
					} else {
						flag = 0;
						break;
					}
				}
				if (flag == 1)
					wordToVerif.put(Constants.SYNTAX, true);
				else if (flag == 0)
					wordToVerif.put(Constants.SYNTAX, false);
				else
					System.err.println("flag=" + flag);
			}
		}
		return wordToVerif;
	}
}
