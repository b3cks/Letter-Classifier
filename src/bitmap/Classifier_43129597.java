package bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Classifier_43129597 extends LetterClassifier {

	private static String name = "NNM Classifier 1";
	private NNM nnm1 = null;
	private NNM nnm2 = null;
	private NNM nnm3 = null;
	private NNM nnm4 = null;
	private NNM nnm5 = null;
	private Random rand;
	private double[][] targets = null; // target vectors;

	public String getName() {
		return name;
	}

	public double[] test(Bitmap map) {
		double[] output1 = nnm1.feedforwad(map.toDoubleArray());
		double[] output2 = nnm2.feedforwad(map.toDoubleArray());
		double[] output3 = nnm3.feedforwad(map.toDoubleArray());
		double[] output4 = nnm4.feedforwad(map.toDoubleArray());
		double[] output5 = nnm5.feedforwad(map.toDoubleArray());
		int[] answer = new int[5];
		answer[0] = answer(output1);
		answer[1] = answer(output2);
		answer[2] = answer(output3);
		answer[3] = answer(output4);
		answer[4] = answer(output5);
		double[] output = new double[26];
		// voting
		for (int i = 0; i < 5; i++){
			output[answer[i]] += 1; 
		}
		return output;
	}
	
	/**
	 * the answer from the output
	 * @param output
	 * @return the index of biggest value off the output
	 */
	public int answer(double[] output){
		double max = output[0];
		int index = 0;
		for (int i = 0; i < 26; i++){
			if (output[i] > max){
				max = output[i];
				index = i;
			}
		}
		return index;
	}

	public void train(ClassifiedBitmap[] maps, int nPresentations, double eta) {
		for (int p = 0; p < nPresentations; p++) {
			int sample = rand.nextInt(maps.length);
			nnm1.train(((Bitmap) maps[sample]).toDoubleArray(),
					targets[maps[sample].getTarget()], eta);
		}
		System.out.println("Classifier 1 done");
		for (int p = 0; p < nPresentations; p++) {
			int sample = rand.nextInt(maps.length);
			nnm2.train(((Bitmap) maps[sample]).toDoubleArray(),
					targets[maps[sample].getTarget()], eta);
		}
		System.out.println("Classifier 2 done");
		for (int p = 0; p < nPresentations; p++) {
			int sample = rand.nextInt(maps.length);
			nnm3.train(((Bitmap) maps[sample]).toDoubleArray(),
					targets[maps[sample].getTarget()], eta);
		}
		System.out.println("Classifier 3 done");
		for (int p = 0; p < nPresentations; p++) {
			int sample = rand.nextInt(maps.length);
			nnm4.train(((Bitmap) maps[sample]).toDoubleArray(),
					targets[maps[sample].getTarget()], eta);
		}
		System.out.println("Classifier 4 done");
		for (int p = 0; p < nPresentations; p++) {
			int sample = rand.nextInt(maps.length);
			nnm5.train(((Bitmap) maps[sample]).toDoubleArray(),
					targets[maps[sample].getTarget()], eta);
		}
		System.out.println("Classifier 5 done");
	}

	public Classifier_43129597(int nRows, int nCols) {
		rand = new Random();
		nnm1 = new NNM(nRows * nCols, getClassCount());
		nnm2 = new NNM(nRows * nCols, getClassCount());
		nnm3 = new NNM(nRows * nCols, getClassCount());
		nnm4 = new NNM(nRows * nCols, getClassCount());
		nnm5 = new NNM(nRows * nCols, getClassCount());
		targets = new double[getClassCount()][getClassCount()];
		/*for (int i = 0; i < getClassCount(); i++)
			for (int j = 0; j < getClassCount(); j++)
				targets[i][j] = -1;*/
		for (int c = 0; c < getClassCount(); c++)
			targets[c][c] = 1;
	}
	
	private class NNM implements Serializable {
		public List<Layer> layers = new ArrayList<Layer>();
		
		public NNM(int noInput, int noOut){
			Random rand = new Random(System.currentTimeMillis());
			int seed = rand.nextInt();
			layers.add(new Layer(noInput, 500, seed));
			layers.add(new Layer(500, 500, seed));
			layers.add(new Layer(500, 26, seed));
		}
		
		public void train(double[] x, double[] target, double eta){
			// feed forward
			layers.get(0).feedforward(x);
			for (int i = 1; i < layers.size(); i++){
				layers.get(i).feedforward(layers.get(i-1).output);
			}
			
			// error
			for (int i = 0; i < target.length; i++){
				layers.get(layers.size()-1).error[i] = target[i] - layers.get(layers.size()-1).output[i];
			}
			
			for (int i = layers.size() - 2; i >= 0; i--){
				layers.get(i).errorCal(layers.get(i+1));
			}
			
			// update weights
			layers.get(0).updateWeight(x, eta);
			for (int i = 1; i < layers.size(); i++){
				layers.get(i).updateWeight(layers.get(i-1).output, eta);
			}
		}
		
		public double[] feedforwad(double[] x){
			layers.get(0).feedforward(x);
			for (int i = 1; i < layers.size(); i++){
				layers.get(i).feedforward(layers.get(i-1).output);
			}
			return layers.get(layers.size()-1).output;
		}
	}
	
	private class Layer implements Serializable {
		public double[] output;
		public double[][] w;
		public double[] bias;
		public double[] error;
		Random rand;
		public Layer(int noInput, int noOutput, int seed){
			rand = new Random(seed);
			output = new double[noOutput];
			w = new double[noOutput][noInput];
			bias = new double[noOutput];
			error = new double[noOutput];
			for (int j = 0; j < noOutput; j++){
				for (int i = 0; i < noInput; i++){
					w[j][i] = rand.nextGaussian()*.1;
				}
				bias[j]=rand.nextGaussian()*.1;
				// System.out.println(bias[j]);
			}
		}
		
		public double outputFunction(double net) {
			return 1.0 / (1.0 + Math.exp(-net));
			// System.out.println("t");
			//return Math.tanh(net);
		}
		
		public double outputFunctionDerivative(double x) {
			return x * (1.0 - x);
			//return 4.0 * Math.exp(2.0 * x) / Math.pow(Math.exp(2.0 * x) + 1, 2);
		}
		
		public void feedforward(double[] x) {
			for (int j = 0; j < output.length; j++) {
				double sum = 0;
				for (int i = 0; i < x.length; i++)
					sum += x[i] * w[j][i];
				output[j] = outputFunction(sum + bias[j]);
				// System.out.println(output[j]);
			}
		}
		
		public void errorCal(Layer nextLayer){
			for (int i = 0; i < error.length; i++){
				double e = 0;
				for (int j = 0; j < nextLayer.output.length; j++){
					e += nextLayer.error[j]*nextLayer.w[j][i];
				}
				error[i] = e;
			}
		}
		
		public void updateWeight(double input[], double eta){
			for (int j = 0; j < output.length; j++) {
				for (int i = 0; i < input.length; i++) {
					w[j][i] += error[j] * outputFunctionDerivative(output[j]) * input[i] * eta;
				}
				bias[j] += error[j] * outputFunctionDerivative(output[j]) * 1.0 * eta;
			}
		}
		
	}
}