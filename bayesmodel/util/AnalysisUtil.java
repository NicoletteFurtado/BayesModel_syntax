package bayesmodel.util;

import java.util.ArrayList;
import java.util.HashMap;

import bayesmodel.constants.Constants;
import bayesmodel.model.InitMaps;

public class AnalysisUtil {
	public static String convertStringToKey(String sentence) {
		String s = sentence.toUpperCase();
		String[] s1 = s.split(Constants.WORD_SEPARATOR);
		String key = "";
		int i = 0;
		for (i = 0; i < s1.length - 1; i++) {
			// String key = s1[0].trim() + Constants.KEY_SEPARATOR + s1[1].trim() + Constants.KEY_SEPARATOR +
			// s1[3].trim();
			if (!s1[i].equals("-")) {
				key += s1[i].trim() + Constants.KEY_SEPARATOR;
			}
			// System.out.println(s1[i]);
			// key += s1[i].trim() + Constants.KEY_SEPARATOR;
		}
		key += s1[i];
		// System.out.println(key);
		return key;
	}

	//
	// public static ArrayList<String> constructRepeatedUserStepList(StudentLogData student, InitMaps initMaps) {
	// ArrayList<String> repeatedUserStepList = new ArrayList<String>();
	// for (int i = 0; i < student.getUserStep().size(); i++) {
	// // get the number of skills for each step in each sentence
	//
	// }
	// return repeatedUserStepList;
	// }
	//
	// public static boolean checkForFarmerPronoun(String sentence, InitMaps initMaps) {
	// String sentenceText = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence));
	// for (String word : sentenceText.split(Constants.WORD_SEPARATOR)) {
	// if (word.equals(Constants.FARMER_PRONOUN)) {
	// return true;
	// }
	// }
	// return false;
	// }

	// public static boolean checkForPossession(String sentence, InitMaps initMaps) {
	// String sentenceText = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence));
	// for (String word : sentenceText.split(Constants.WORD_SEPARATOR)) {
	// if (word.equals(Constants.POSSESSION)) {
	// return true;
	// }
	// }
	// return false;
	// }

	public static boolean checkIfSentenceContainsWord(String sentence, String word, InitMaps initMaps) {
		String sentenceText = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence));
		for (String w : sentenceText.split(Constants.WORD_SEPARATOR)) {
			if (w.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}

	// contains lasttolast, last, next, nexttonext
	public static ArrayList<String> getSurroundingWords(String sentence, String word, InitMaps initMaps) {
		ArrayList<String> surroundingWords = new ArrayList<String>();
		String sentenceText = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence));
		if (sentenceText.contains(word)) {
			String[] wordsInSentence = sentenceText.split(Constants.WORD_SEPARATOR);
			for (int i = 2; i < wordsInSentence.length - 2; i++) {
				if (wordsInSentence[i].equals(Constants.POSSESSION)) {
					surroundingWords.add(wordsInSentence[i - 2]);
					surroundingWords.add(wordsInSentence[i - 1]);
					surroundingWords.add(wordsInSentence[i + 1]);
					surroundingWords.add(wordsInSentence[i + 2]);
					break;
				}
			}
		}
		return surroundingWords;
	}

	public static int getNoOfOccurrences(String sentence, InitMaps initMaps, String word) { // here word should be s
		String sentenceWords[] = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence))
				.split(Constants.WORD_SEPARATOR);
		int count = 0;
		for (String w : sentenceWords) {
			if (w.equalsIgnoreCase(word)) {
				count++;
			}
		}
		return count;
	}

	public static String getPossessiveNoun(String sentence, InitMaps initMaps) {
		String[] sentenceWords = initMaps.getSentenceToText().get(AnalysisUtil.convertStringToKey(sentence))
				.split(Constants.WORD_SEPARATOR);
		String result = "";
		for (int i = 0; i < sentenceWords.length; i++) {
			if (sentenceWords[i].equals(Constants.POSSESSION)) {
				return result;
			}
		}
		return result;
	}

	private static HashMap<String, String> penToAnimal = new HashMap<String, String>();
	static {
		penToAnimal.put("pen1", "sheep");
		penToAnimal.put("pen2", "pig");
		penToAnimal.put("pen3", "goat");
		penToAnimal.put("pen4", "cow");
	}

	public static boolean checkIfAnswerContainsWord(ArrayList<String> actionWords, String word) {
		if (actionWords.contains(word))
			return true;
		return false;
	}
}
