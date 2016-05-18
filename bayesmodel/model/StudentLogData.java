package bayesmodel.model;
import java.util.ArrayList;

public class StudentLogData {
	private ArrayList<String> actionList;
	private ArrayList<String> verificationList;
	private ArrayList<String> sentenceList;
	private ArrayList<Integer> userStep;
	private ArrayList<String> inputData;
	private ArrayList<String> userStepRepeated;
	private ArrayList<String> userList;

	// ////
	private double guess = 0.1; // 0.25 //0.6
	private double transition = 0.1; // 0.1 //0.7 //0.4
	private double slip = 0.2; // 0.4 //0.1

	private double guess2 = 0.3; // 0.25 //0.6
	private double transition2 = 0.1; // 0.1 //0.7 //0.4
	private double slip2 = 0.1; // 0.4 //0.1

	public ArrayList<String> getActionList() {
		return actionList;
	}

	public void setActionList(ArrayList<String> actionList) {
		this.actionList = actionList;
	}

	public ArrayList<String> getVerificationList() {
		return verificationList;
	}

	public void setVerificationList(ArrayList<String> verificationList) {
		this.verificationList = verificationList;
	}

	public ArrayList<String> getSentenceList() {
		return sentenceList;
	}

	public void setSentenceList(ArrayList<String> sentenceList) {
		this.sentenceList = sentenceList;
	}

	public ArrayList<Integer> getUserStep() {
		return userStep;
	}

	public void setUserStep(ArrayList<Integer> userStep) {
		this.userStep = userStep;
	}

	public double getGuess() {
		return guess;
	}

	public void setGuess(double guess) {
		this.guess = guess;
	}

	public double getTransition() {
		return transition;
	}

	public void setTransition(double transition) {
		this.transition = transition;
	}

	public double getSlip() {
		return slip;
	}

	public void setSlip(double slip) {
		this.slip = slip;
	}

	public ArrayList<String> getInputData() {
		return inputData;
	}

	public void setInputData(ArrayList<String> inputData) {
		this.inputData = inputData;
	}

	public ArrayList<String> getUserStepRepeated() {
		return userStepRepeated;
	}

	public void setUserStepRepeated(ArrayList<String> userStepRepeated) {
		this.userStepRepeated = userStepRepeated;
	}

	public double getGuess2() {
		return guess2;
	}

	public void setGuess2(double guess2) {
		this.guess2 = guess2;
	}

	public double getTransition2() {
		return transition2;
	}

	public void setTransition2(double transition2) {
		this.transition2 = transition2;
	}

	public double getSlip2() {
		return slip2;
	}

	public void setSlip2(double slip2) {
		this.slip2 = slip2;
	}

	public ArrayList<String> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<String> userList) {
		this.userList = userList;
	}

}
