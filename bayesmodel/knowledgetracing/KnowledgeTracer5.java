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
		// add syntax_pronoun to wordToValue
		wordToValueMap.put(Constants.SYNTAX_PRONOUN, skillMap.get(Constants.SYNTAX_PRONOUN).get(0));
		System.out.println(wordToValueMap);
		// add syntax_possession to wordToValue
		wordToValueMap.put(Constants.SYNTAX_POSSESSION, skillMap.get(Constants.SYNTAX_POSSESSION).get(0));
		// add usabilty_error to wordToValue
		wordToValueMap.put(Constants.USABILITY_ERROR, skillMap.get(Constants.USABILITY_ERROR).get(0));

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
		String prevInputData = "";
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
			ArrayList<String> tempActionList = actionMap.get(sentence);
			ArrayList<Integer> tempUserStepList = userStepMap.get(sentence);
			ArrayList<String> tempInputDataList = inputDataMap.get(sentence);
			ArrayList<String> tempVerificationList = verificationMap.get(sentence);
			// go through each action
			for (int i = 0; i < tempActionList.size(); i++) {
				HashMap<String, String> wordToVerif = checkIncorrectSkill2(student, initMaps, sentence,
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
										if (wordToVerif.get(word).equals(Constants.CORRECT)) {
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
											newSkill = calcNewSkillValue(student, skillEvaluated);
											// updateSkills(studentLogData, initMaps, newSkill, word, i);
											updateSkills(student, newSkill, word, tempVerificationList.get(i),
													sentence, tempActionList.get(i), tempUserStepList.get(i).intValue());
											System.out.println("in incorrect correct " + word);
										} else if (wordToVerif.get(word).equals(Constants.INCORRECT)) {
											prevSkillValue = wordToValueMap.get(word).getSkillValue();
											System.err.println(" incorrect incorrect1"
													+ Arrays.toString(playWordSet.toArray()));
											if (playWordSet.contains(word)) {
												System.out.println(" incorrect incorrect"
														+ Arrays.toString(playWordSet.toArray()));
												skillEvaluated = this.calcIncorrectPlayWord(student, prevSkillValue);
												newSkill = calcNewSkillValue(student, skillEvaluated);
												// updateSkills(studentLogData, initMaps, newSkill, word, i);
												updateSkills(student, newSkill, word, tempVerificationList.get(i),
														sentence, tempActionList.get(i), tempUserStepList.get(i)
																.intValue());
											} else if (wordToVerif.get(word).equals(Constants.DO_NOTHING)) {
												// newSkill = calcNewSkillValue(student, skillEvaluated);
												// updateSkills(studentLogData, initMaps, newSkill, word, i);
												updateSkills(student, prevSkillValue, word,
														tempVerificationList.get(i), sentence, tempActionList.get(i),
														tempUserStepList.get(i).intValue());
											} else
												skillEvaluated = this.calcIncorrect(student, prevSkillValue);
											newSkill = calcNewSkillValue(student, skillEvaluated);
											// updateSkills(studentLogData, initMaps, newSkill, word, i);
											updateSkills(student, newSkill, word, tempVerificationList.get(i),
													sentence, tempActionList.get(i), tempUserStepList.get(i).intValue());
											System.out.println("in incorrect incorrect " + word);
										}

										System.out.println("updated incorrect");
									}
								}
							}
						}
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
	private HashMap<String, String> checkIncorrectSkill2(StudentLogData student, InitMaps initMaps, String sentence,
			String wordsMoved, int userStep) {
		String objectsMoved[] = wordsMoved.split(Constants.STUDENT_INPUT_DATA_SEPARATOR);
		System.out.println("///////// " + userStep);
		// HashMap<String, Boolean> wordToVerif = null;
		HashMap<String, String> wordToVerif = null;
		if (initMaps.getSentenceToActions().get(AnalysisUtil.convertStringToKey(sentence)).size() >= userStep) {
			ArrayList<String> actionWords = initMaps.getSentenceToActions()
					.get(AnalysisUtil.convertStringToKey(sentence)).get(userStep - 1);
			// go through and change input data words if needed
			processInputData(objectsMoved);
			// HashMap<String, Boolean> wordToVerif = new HashMap<String, Boolean>();
			// wordToVerif = new HashMap<String, Boolean>();
			wordToVerif = new HashMap<String, String>();
			if (objectsMoved.length == 1) { // consider help request as incorrect
				// wordToVerif.put(objectsMoved[0].trim(), false);
				wordToVerif.put(objectsMoved[0].trim(), Constants.INCORRECT);
			} else if (objectsMoved.length > 1) {
				System.err.println("objects moved " + Arrays.toString(objectsMoved));
				// List of correct words for that step
				System.out.println("userStep " + userStep);
				System.err.println("actionWords " + Arrays.toString(actionWords.toArray()));
				// add syntax to wordToVerif default false
				// wordToVerif.put(Constants.SYNTAX, false);
				wordToVerif.put(Constants.SYNTAX, Constants.INCORRECT);
				// get the action for this step
				for (int i = 0; i < actionWords.size(); i++) {
					if (actionWords.get(i).equalsIgnoreCase(objectsMoved[i])) {
						wordToVerif.put(actionWords.get(i), Constants.CORRECT);
						// understood everything about the sentence including syntax
						// wordToVerif.put(Constants.SYNTAX, true);
					} else {
						wordToVerif.put(actionWords.get(i), Constants.INCORRECT);
					}
				}
				// check if all words in actionWords are present in objectsMoved
				System.out.println("syntax = " + checkForSyntaxError(objectsMoved, actionWords, initMaps, sentence));
				// if (checkForSyntaxError(objectsMoved, actionWords, initMaps, sentence).equals(Constants.CORRECT))
				// wordToVerif.put(Constants.SYNTAX, Constants.CORRECT);
				// else if (checkForSyntaxError(objectsMoved, actionWords, initMaps,
				// sentence).equals(Constants.INCORRECT))
				// wordToVerif.put(Constants.SYNTAX, Constants.INCORRECT);
				// else if (checkForSyntaxError(objectsMoved, actionWords, initMaps, sentence)
				// .equals(Constants.DO_NOTHING))
				// wordToVerif.put(Constants.SYNTAX, Constants.DO_NOTHING);
				// wordToVerif.put(Constants.SYNTAX, checkForSyntaxError(objectsMoved, actionWords, initMaps,
				// sentence));
				boolean farmerError = false;
				boolean possessionError = false;
				boolean penError = false;
				// check for syntax pronoun error
				if (AnalysisUtil.checkIfSentenceContainsWord(sentence, Constants.FARMER_PRONOUN, initMaps)) {
					System.out.println("pronoun " + checkForPronounError(objectsMoved, sentence, initMaps));
					farmerError = true;
					if (checkForPronounError(objectsMoved, sentence, initMaps))
						wordToVerif.put(Constants.SYNTAX_PRONOUN, Constants.CORRECT);
					else
						wordToVerif.put(Constants.SYNTAX_PRONOUN, Constants.INCORRECT);
				}
				// check for possession error
				if (AnalysisUtil.checkIfSentenceContainsWord(sentence, Constants.POSSESSION, initMaps)
						&& AnalysisUtil.getNoOfOccurrences(sentence, initMaps, Constants.POSSESSION) == 1) {
					possessionError = true;
					System.out.println("possess=" + checkForPossessionError(sentence, initMaps, wordToVerif));
					wordToVerif.put(Constants.SYNTAX_POSSESSION,
							checkForPossessionError(sentence, initMaps, wordToVerif));
				}
				// check for usability_error
				if (AnalysisUtil.checkIfAnswerContainsWord(actionWords, Constants.PEN)) {
					penError = true;
					System.out.println("usability = "
							+ checkForUsabilityError(sentence, initMaps, wordToVerif, actionWords, objectsMoved));
					wordToVerif.put(Constants.USABILITY_ERROR,
							checkForUsabilityError(sentence, initMaps, wordToVerif, actionWords, objectsMoved));
				}
				if (!farmerError && !possessionError && !penError)
					wordToVerif.put(Constants.SYNTAX,
							checkForSyntaxError(objectsMoved, actionWords, initMaps, sentence));
			}
		}
		return wordToVerif;
	}

	private void processInputData(String objectsMoved[]) {
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
	}

	// return true for error
	private boolean checkForPronounError(String objectsMoved[], String sentence, InitMaps initMaps) {
		// String sentenceText = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence));
		boolean flag = false;
		for (String word : objectsMoved) {
			if (!word.equals(Constants.FARMER))
				flag = false;
			else {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private String checkForSyntaxError(String objectsMoved[], ArrayList<String> actionWords, InitMaps initMaps,
			String sentence) {
		// boolean flag = false;
		String flag = "";
		for (String objectMoved : objectsMoved) {
			if (actionWords.contains(objectMoved))
				flag = Constants.CORRECT;
			else if (AnalysisUtil.checkIfSentenceContainsWord(sentence, objectMoved, initMaps)) {
				flag = Constants.INCORRECT;
				break;
			} else
				flag = Constants.DO_NOTHING;
		}
		return flag;
	}

	private String checkForPossessionError(String sentence, InitMaps initMaps, HashMap<String, String> wordToVerif) {
		String flag = "";
		// if the sentence contains the wrong word, incorrect
		// add words in the sentence to a list
		ArrayList<String> validWords = new ArrayList<String>();
		for (String word : wordToVerif.keySet()) {
			if (AnalysisUtil.checkIfSentenceContainsWord(sentence, word, initMaps))
				validWords.add(word);
		}
		for (String word : validWords) {
			if (wordToVerif.get(word).equals(Constants.INCORRECT)) {

			}
		}
		return flag;
	}

	private String checkForUsabilityError(String sentence, InitMaps initMaps, HashMap<String, String> wordToVerif,
			ArrayList<String> actionWords, String[] objectsMoved) {
		String flag = "";
		for (int i = 0; i < objectsMoved.length; i++) {
			if (objectsMoved[i].equals(Constants.PEN) && actionWords.get(i).equals(objectsMoved[i])
					&& wordToVerif.get(objectsMoved[i]).equals(Constants.INCORRECT)) {
				flag = Constants.INCORRECT;
			} else if (objectsMoved[i].equals(Constants.PEN) && actionWords.get(i).equals(objectsMoved[i])
					&& wordToVerif.get(objectsMoved[i]).equals(Constants.CORRECT)) {
				flag = Constants.CORRECT;
			}
		}
		return flag;
	}
}
