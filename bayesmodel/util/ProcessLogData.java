package bayesmodel.util;

import java.util.ArrayList;

import bayesmodel.constants.Constants;
import bayesmodel.model.InitMaps;
import bayesmodel.model.StudentLogData;

public class ProcessLogData {

	private static int UPDATED_lIST_SIZE = 0;

	public void updateLogData(StudentLogData student, InitMaps initMaps) {
		removeExtraAttempts(student);
		// repeatAttempts(student, initMaps);
		repeatAttempts2(student, initMaps);
	}

	// remove all attempts except the first
	public void removeExtraAttempts(StudentLogData student) {
		ArrayList<String> actionListNew = new ArrayList<String>();
		ArrayList<String> verificationListNew = new ArrayList<String>();
		ArrayList<String> sentenceListNew = new ArrayList<String>();
		ArrayList<Integer> userStepNew = new ArrayList<Integer>();
		ArrayList<String> inputDataNew = new ArrayList<String>();
		// ArrayList<String> userStepRepeatedNew = new ArrayList<String>();

		String prevSentence = "";
		Integer prevUserStep = 0;
		String prevAction = "";
		for (int i = 0; i < student.getSentenceList().size(); i++) {
			// first two conditions only applies to move actions, but we want all play word actions
			if (!prevSentence.equals(student.getSentenceList().get(i)) || prevUserStep != student.getUserStep().get(i)
					|| student.getActionList().get(i).equals(Constants.PLAY_WORD)
					|| prevAction.equals(Constants.PLAY_WORD)) {
				// if not equal to previous then add
				verificationListNew.add(student.getVerificationList().get(i));
				actionListNew.add(student.getActionList().get(i));
				sentenceListNew.add(student.getSentenceList().get(i));
				userStepNew.add(student.getUserStep().get(i));
				// System.out.println("input data size=" + student.getInputData().size());
				inputDataNew.add(student.getInputData().get(i));

				// System.out.println("added step " + student.getUserStep().get(i) + "of sentence "
				// + student.getSentenceList().get(i));
			}
			prevSentence = student.getSentenceList().get(i);
			prevUserStep = student.getUserStep().get(i);
			prevAction = student.getActionList().get(i);
		}
		student.setVerificationList(verificationListNew);
		student.setActionList(actionListNew);
		student.setSentenceList(sentenceListNew);
		student.setUserStep(userStepNew);
		student.setInputData(inputDataNew);
		UPDATED_lIST_SIZE = sentenceListNew.size();
		// System.out.println("first attempt=" + Arrays.toString(student.getUserStep().toArray()));
		// System.out.println("first attempt=" + student.getVerificationList().size());
		// System.out.println("first attempt input data=" + student.getInputData().size());
		// System.out.println("first attempt=" + Arrays.toString(student.getInputData().toArray()));
		// System.out.println("first attempt=" + Arrays.toString(student.getSentenceList().toArray()));
	}

	public void repeatAttempts2(StudentLogData student, InitMaps initMaps) {
		int j = 0; // arraylist for all steps
		int k = 0;// arraylist per step
		ArrayList<String> actionListNew = new ArrayList<String>();
		ArrayList<String> verificationListNew = new ArrayList<String>();
		ArrayList<String> sentenceListNew = new ArrayList<String>();
		ArrayList<Integer> userStepNew = new ArrayList<Integer>();
		ArrayList<String> inputDataNew = new ArrayList<String>();

		for (int i = 0; i < UPDATED_lIST_SIZE; i++) {
			// get the skills for this sentence
			// System.out.println("i=" + i);
			// System.out.println(AnalysisUtil.convertStringToKey(student.getSentenceList().get(i)));
			// System.out.println("/////// " + wordsInSentence);
			// if (!wordsInSentence.contains(Constants.DEFAULT_WORD)) {
			// get all data for the first attempt
			String currSentence = student.getSentenceList().get(i);
			String currVerification = student.getVerificationList().get(i);
			String currAction = student.getActionList().get(i);
			Integer currUserStep = student.getUserStep().get(i);
			String currInputData = student.getInputData().get(i);
			// you have the first attempt
			// add the data wordsInSentence number of times

			// j = 0;
			// while (j < initMaps.getSentenceToActions().get(AnalysisUtil.convertStringToKey(currSentence)).size()) {
			// // all
			// the
			// steps
			k = 0;
			// <= added = for syntax as a skill
			if (!currAction.equals(Constants.PLAY_WORD)) {
				while (k <= initMaps.getSentenceToActions().get(AnalysisUtil.convertStringToKey(currSentence))
						.get(currUserStep - 1).size()) {
					// for (int j = 0; j < (wordsInSentence.size() - 1); j++) {
					// System.out.println("k=" + k);
					// add at i th position, k times
					sentenceListNew.add(currSentence);
					verificationListNew.add(currVerification);
					actionListNew.add(currAction);
					userStepNew.add(currUserStep);
					// inputDataNew.add(i, currInputData);
					inputDataNew.add(currInputData);
					// System.out.println("repeated step " + currUserStep + "of sentence " + currSentence + " for word "
					// + currInputData);
					k++;
				}
			} else { // add play word only once
				sentenceListNew.add(currSentence);
				verificationListNew.add(currVerification);
				actionListNew.add(currAction);
				userStepNew.add(currUserStep);
				inputDataNew.add(currInputData);
				// System.out.println("play word step " + currUserStep + "of sentence " + currSentence + " for word "
				// + currInputData);
			}
			// }
			// }
		}
		student.setVerificationList(verificationListNew);
		student.setActionList(actionListNew);
		student.setSentenceList(sentenceListNew);
		student.setUserStep(userStepNew);
		student.setInputData(inputDataNew);
		// System.out.println("final attempt=" + Arrays.toString(student.getSentenceList().toArray()));
		// System.out.println("final attempt=" + Arrays.toString(student.getInputData().toArray()));
		// System.out.println("final attempt=" + Arrays.toString(student.getVerificationList().toArray()));
		// System.out.println("final attempt=" + student.getVerificationList().size());
		// System.out.println("final attempt=" + student.getInputData().size());
	}
}
