import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.Random;


public class coursework2 {
	
	public static int[][] picArr1 = new int[20][64]; //hard coded size needs changing
	public static int[] intArr1_answers1 = new int [20]; //the actual number the 8 by 8 corresponds to
	
	public static int[][] picArr2 = new int[2810][64]; //training set
	public static int[] picArr2_answers = new int [2810];
	public static int[] picArr2_guesses = new int [2810];
	
	public static int[][] picArr3 = new int[2810][64]; //test set
	public static int[] picArr3_answers = new int [2810];
	public static int[] picArr3_guesses = new int [2810];
	
	//arrays only work for one pic at a time before being overwritten? 
	public static double [] inputWeights = new double[64]; //64 inputs
	
	public static double[] hiddenInputs = new double[64];
	public static double[][] hiddenWeights = new double[5][64]; //5 nodes, each take input+weight from each input node(64)
	
	public static double[] outputInputs = new double[5];
	public static double[][] outputWeights = new double[10][5]; //10 nodes, each take input+weight from each hidden node(5)
	
	public static double[] outputLayerOutputs = new double[10];
	
	//stores weights of all pictures so they can be changed with backpropagation 
	//could have coded this so I didn't need separate arrays hiddenWeights and outputWeights but it's too late for that and this works fine
	public static double[][] allOutputLayerOutputs = new double [2810][10];
	public static double[][][] allHiddenWeights = new double[2810][5][64]; 
	public static double[][][] allOutputWeights = new double[2810][10][5];
	
	public static double bias = 0.01; //is this supposed to change?
	public static double learningRate = 0.1;
	
	public static String csvFile1 =  "digits.csv"; //file is in project folder so no full path needed
	public static String csvFile2 =  "cw2DataSet1.csv"; 
	public static String csvFile3 =  "cw2DataSet2.csv"; 
	
	private static void print1D(int array[]){
		for (int i = 0; i< array.length; i++){
			System.out.print(array[i]+",");
		}
		System.out.print("\n");
	}
	
	private static void print2D(int array[][]){
		for (int i = 0; i< array.length; i++){
			print1D(array[i]);
		}
	}
	
	public static void readin(String csvFile){ //pass in file and arrays
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int j = 0;

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
            	//creates array with no commas
                String[] splitLine = line.split(cvsSplitBy);
                
                int[] lineArr = new int[splitLine.length]; 
                
                //convert string array to int array
                for (int i = 0; i< splitLine.length-1; i++){
                	lineArr[i] = Integer.parseInt(splitLine[i]);
                }
                
                
                //don't know if this is a very efficient way of doing things?
                if (csvFile == "digits.csv") {
                	
                    //puts final number into separate array
                    intArr1_answers1[j] = Integer.parseInt(splitLine[splitLine.length-1]); 
                    
                    //moves one line from file into array
                    picArr1[j] = lineArr;
                }
                else if (csvFile == "cw2DataSet1.csv") {
                    picArr2_answers[j] = Integer.parseInt(splitLine[splitLine.length-1]); 
                    picArr2[j] = lineArr;
                }
                else {
                    picArr3_answers[j] = Integer.parseInt(splitLine[splitLine.length-1]); 
                    picArr3[j] = lineArr;
                }
                
                j++;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public static String sizePrint(int num) {
		// nothing, ., x, X
		//0123, 4567, 8 9 10 11, 12 13 14 15 16
		
		if (num <4) {
			return " ";
		}
		else if (num <8) {
			return ".";
		}
		else if (num < 12) {
			return "x";
		}
		else {
			return "X";
		}
		
	}
	
	public static void print8by8(int[] numArray) {

		//print 8 by 8
		//print first 8, new line, loop 8 times
		//before every print, loop get " " . x X
		
		int j = 0;
		for (int i = 0; i < (numArray.length); i++) {
			System.out.print(sizePrint(numArray[i]));
			j++;
			if(j % 8 == 0) {
				System.out.print("\n");
			}
		}
		
		
	}
	
	public static void printAll(int[][] numArray) {
		
		for (int i = 0; i < (numArray.length); i++) {
			print8by8(numArray[i]);
			System.out.print("\n\n");
		}
		
	}
	
	
	public static double dist(int point1[], int point2[]){ 
		
		//n dimensions from point p-q
		//sqrt (Px - Qx)^2 + (Py - Qy)^2 + (Pz - Qz)^2 + ...)
		// x y z coords, goes up to n. 
		
		//number of dimensions is length of array point1[]
		//0 is x, 1 is y, 2 is z ... 63 is n.
		
		int runningTotal = 0;
		
		for (int i = 0; i< point1.length; i++){
			runningTotal += Math.pow((point1[i] - point2[i]),2);
		}
		
		double distance = Math.sqrt(runningTotal);
		
		return distance;
	}
	
	//returns number closest to p1 that is not p1
	public static int compare(int[] p1, int[][] intArr) { //maybe expand to take 2 larger arrays to search
		//get distance from 1 point to all other points in intArr
		double smallestDist = 100;
		int matchNo = 0;
		
		for(int i = 0; i <intArr.length; i++) {
			double newDist = dist(p1,intArr[i]);
			
			
			if ((p1 != intArr[i]) && (smallestDist > newDist )) {
				smallestDist = newDist;
				
				//search array of numbers corresponding to array passed into function
				matchNo = getNumArrVal(intArr,i);
				
			}
		}
		
		return matchNo;
		
	}
	
	//enter 64 number array and 2d array to search for comparison
	//return printed array and single number the program thinks it corresponds to
	public static void whatNumber(int[] num, int[][] compareTo) {
		print8by8(num);
		System.out.println("is the number: "+compare(num, compareTo));
	}
	
	public static int getNumArrVal(int[][] arr, int i){
		if (arr == picArr1) {
			 return intArr1_answers1[i];
		}
		else if (arr == picArr2) {
			return picArr2_answers[i];
		}
		else {
			return picArr3_answers[i];
		}
	}
	
	
	public static void twoFold(int[][] set1, int[][] set2){
		int right = 0;
		int wrong = 0;
		int matchNo = 0;
		
		//not sure if I should put these for loops into a function and make right/wrong variables global?
		
		//train on set1, test set2
		for (int i = 0; i< set1.length; i++){
			
			int comp = compare(set1[i], set2); //get number that most closely resembles set1[i] in training set
			
			matchNo = getNumArrVal(set1,i); //get the actual number set1[i] is supposed to be
			
			//if the numbers match, the program was right.
			if (comp == matchNo){
				right++;
			}
			else{
				wrong++;
			}
			
		}
		
		//train on set2, test set1
		for (int i = 0; i< set2.length; i++){
			
			int comp = compare(set2[i], set1); //get number that most closely resembles set2[i] in training set
			
			matchNo = getNumArrVal(set2,i); //get the actual number set2[i] is supposed to be
			
			//if the numbers match, the program was right.
			if (comp == matchNo){
				right++;
			}
			else{
				wrong++;
				
			}
			
		}
		
		//print out results
		System.out.println("Euclidian distance results: ");
		System.out.println("total tests: "+(right+wrong)+"\nright: "+right+"\nwrong: "+wrong);
	}
	
	
	/*
	 * make function that acts as a perceptron
	 * can be run whenever a perceptron needs to be used
	 */
	
	//10 input/output nodes
	//each have input of arr[64], weights, bias
	//nodes in hidden layer???
	//sigmoid activation function
	//backprop - delta rule?
	
	private static void MLP_train() {
		
		/*
		 * 1.-setup, populate weights
		 * 2.-get 1 picture (picArr2[i], each is a picture. so many oh my god.)
		 * 3.-send each pixel value + its weight + bias to inputPerceptron()
		 * 4.-send output of inputPerceptron() + weight + bias to hidden layer
		 * 5.-send output of hiddenPerceptron() + weight + bias to outputlayer()
		 * 6.-compare outputs of outputlayer(), highest is 'answer'
		 * 7.-do this for all pictures
		 * 8. get list of how many answers were correct
		 * 9. for every answer that is wrong: backprop. change weights (by v small amount)
		 * 		get weight change formula, 0 if no error
		 *10. do loop again with new weights
		 *11. after a few loops, consider system trained
		 *12. do trained loop with test set
		 *13. return final answer - how many right/wrong
		 */

		//forward propagate
		
		//go through all pictures in test set and get 'guesses'
		for (int pic = 0; pic<picArr2.length; pic++) {
			//populate all weights randomly between 0-1
			initWeights();
			
			inputLayer(pic); //for 1st picture only atm, change later
			
			hiddenLayer(hiddenInputs, hiddenWeights); 
			
			int picNumberGuess = outputLayer(outputInputs, outputWeights);
			
			picArr2_guesses[pic] = picNumberGuess;
			
			//pass weights into 3d array (so they are accessible for backprop)
			allHiddenWeights[pic] = hiddenWeights;
			allOutputWeights[pic] = outputWeights;
			allOutputLayerOutputs[pic] = outputLayerOutputs;
		}
		
		//print results
		printResults();
		
		//no, check which weights need to be changed, not just number that were wrong fuck
		//maybe look up backprop algorithm first
		
		//initialise weights for first time picture used only
		//after that backprop is being used, different algorithm.
		//MAKE SURE I'M NOT ACCIDENTALLY WIPING WEIGHTS WITHT THE INIT
	}
	
	private static void printResults() {
		int numCorrect = checkGuesses();
		System.out.println("\nMulti Layer Perceptrons results: ");
		System.out.println("total tests: "+picArr2_guesses.length);
		System.out.println("number of correct results: "+numCorrect);
		System.out.println("number of wrong results: "+(picArr2_guesses.length-numCorrect));
		
	}
	
	private static void backProp(int pic) {
		
		/*
		double outputDeltas[] = new double[picArr2_guesses.length];
		double hiddenDeltas[][] = new double[hiddenWeights.length][picArr2_guesses.length]; //5 per pic, many pics...?? maybe this i need a new array for
		
		for (int weight = 0; weight>outputDeltas.length; weight++) {
			outputDeltas[weight] = getOutputError(picArr2_answers[weight], picArr2_guesses[weight]);
		}
		
		*/
		
		double hiddenDeltas[] = new double[hiddenWeights.length]; //5
		
		double outputDelta = getOutputError(picArr2_answers[pic], picArr2_guesses[pic]);
		
		//i think i need to have the last neuron as a its own thing>
		getHiddenError(allOutputWeights[pic][0][0], outputDelta, allOutputLayerOutputs[pic][0]); //pic by [10][5] //pic by [10]
		
		/*
		 * 1. calculate deltas (errors) for output layer
		 * 2. calculate deltas for hidden layer
		 * 3. calculate weight changes
		 * 4. repeat until total sum of squared errors is less than specified criterion?
		 * 		a. maybe just do a few loops and call it a day
		 */
		
		/*
		 * get all expected values (picArr2_answers) and actual output weights (picArr2_guesses)
		 * loop and fill delta array
		 * i pic per loop?
		 */
		
	}
	
	//this is wrong
	private static int checkGuesses() {
		//compare list of 'guesses' to correct answers 
		//picArr2_guesses - picArr2_answers
		int numCorrect = 0;
		for(int guess = 0; guess<picArr2_guesses.length; guess++) {
			if (picArr2_guesses[guess] == picArr2_answers[guess]) {
				numCorrect++;
			}
		}
		
		/*
		 * whenever something is wrong
		 * get derivative
		 * get delata (error)
		 * modify weight
		 */
		
		 /*
		 * which weights get changed? 
		 * all in network when it outputs something wrong?
		 * formula for changes different for every neuron?
		 * or just every layer?
		 */
		
		
		return numCorrect;
	}
	
	private static double transfer_derivative(double output) {
		return output * (1.0 - output);
	}
	
	private static double getOutputError(double expected, double output) {
		
		/*
		 * expected is the expected output value for the neuron
		 * output is the output value for the neuron
		 * transfer_derivative() calculates the slope of the neuron’s output value,
		 */
		
		return (expected - output) * transfer_derivative(output);
	}
	
	private static double getHiddenError(double weight, double error, double output) {
		
		/*
		 * error for a neuron in the hidden layer is calculated as the weighted error of each neuron in the output layer
		 * 
		 * error is the error signal from the jth neuron in the output layer
		 * weight is the weight that connects the kth neuron to the current neuron
		 * output is the output for the current neuron
		 * 
		 * do for every separate weight in the hidden layer
		 */
		
		return (weight * error) * transfer_derivative(output);
	}
	
	private static double getNewWeight(double weight, double error, double input) {

		/*
		 * weight is a given weight
		 * error is the error calculated by the backpropagation procedure for the neuron
		 * input is the input value that caused the error
		 */
		
		return weight + learningRate * error * input;
	}
	
	private static void initWeights() {
		
		Random rand = new Random();
		
		/*
		 * not sure if random to start is best for all weights???
		 * some should be same, maybe weights for initial inputs? 
		 * for identical outputs from input/hidden layer???
		 */
		
		//this is the same every time
		for (int i = 0; i<inputWeights.length; i++) {
			inputWeights[i] = 0.5; //not sure if this is a good weight to start with
		}
		
		//these are randomly generated
		for (int i = 0; i<hiddenWeights.length; i++) {
			for (int j = 0; j<hiddenWeights[i].length; j++) {
				hiddenWeights[i][j] = rand.nextFloat();
			}
				
		}
		
		for (int i = 0; i<outputWeights.length; i++) {
			for (int j = 0; j<outputWeights[i].length; j++) {
				outputWeights[i][j] = rand.nextFloat();
			}
				
		}
	}

	private static double sigmoid(double x) {
	    return (1.0 / (1 + Math.exp(-x)));
	}
	
	public static void inputLayer(int picture) {
		//do inputPerceptron for each pixel in picture
		for (int pixel = 0; pixel < 64; pixel++) { //2810 //64
			
			//pixel input, weight, bias
			double output = inputPerceptron(picArr2[picture][pixel], inputWeights[pixel], bias);
			
			hiddenInputs[pixel] = output;
		}
	}
	
	public static double inputPerceptron(double input, double weight, double bias) {
		
		//initial input,takes whole pic w 64 inputs
		//change to take 1 of the 64 inputs
		
		double sum = bias;
		double weightedInput = input*weight;
		sum += weightedInput;
		
		//activation function
		double output = sigmoid(sum);

		return output; 
	}
	
	public static void hiddenLayer(double inputs[], double weights[][]) {
		//5 hidden perceptrons
		for (int node = 0; node < 5; node++) {
			double output = hiddenPerceptron(inputs, weights[node]);
			outputInputs[node] = output;
		}
		
		
	}
	
	public static double hiddenPerceptron(double inputs[], double weights[]) {
		
		double sum = bias;
		for (int i = 0; i<inputs.length; i++) {
			//multiply each input by it's weight
			inputs[i] = inputs[i] * weights[i];
			//sum all weighted parts
			sum += inputs[i];
		}
		
		//activation function
		double output = sigmoid(sum);
		return output;
	}
	
	public static double outputPerceptron(double inputs[], double weights[]) {

		//at the moment this is essentially the same as hiddenPerceptron()
		double sum = bias;
		for (int i = 0; i<inputs.length; i++) {
			//multiply each input by it's weight
			inputs[i] = inputs[i] * weights[i];
			//sum all weighted parts
			sum += inputs[i];
		}
		
		//activation function
		double output = sigmoid(sum); //could change this for final output?
		
		return output;
	}

	public static int outputLayer(double inputs[], double weights[][]) {
		//10 output perceptrons
		double largestNum = 0;
		int answer = 0;
		
		for (int node = 0; node < 10; node++) {
			double output = outputPerceptron(inputs, weights[node]);
			outputLayerOutputs[node] = output;
			
			if (output > largestNum){
				largestNum = output;
				answer = node;
			}
		}

		return answer;
	}
	
	public static void main(String[] args) {
		
		//file 1 is the test file from week 16
		
		//read data into arrays
		readin(csvFile2);
		readin(csvFile3);
		
		//printAll(picArr2);
		
		//whatNumber(picArr1[15], picArr2);
		
		twoFold(picArr2,picArr3);
		
		MLP_train();
		
		
		/*
		 * perceptron algorithm:
		 * 1. for every input, multiply that input by it's weight
		 	* 1. add a bias weight 1
		 * 2. sum all of the weighted parts
		 * 3 compute the output of the perceptron based on that sum passed through an activation function (the sign of the sum)
		 */
		 //sigmoid

		// for quality of code: get rid of i and j, name constants, block comment at beginning, more commnents 
	}

}
