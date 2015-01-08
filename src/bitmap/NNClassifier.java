package bitmap;

import java.util.Random;

import machl.*;

/**
 * <p>A neural network handwritten letter recognizer. Use as an example for writing LetterClassifiers.</p>
 * @author Mikael Boden
 * @version 1.0
 */

public class NNClassifier extends LetterClassifier {

  private static String name="NN Classifier 1";
  public NN1 nn=null;
  private Random rand;
  private double[][] targets=null; // target vectors;
  
  /**
   * Get the underlying neural network from the classifier.
   */
  public NN1 getNN() {
	  return nn;
  }

  /**
   * Identifies the classifier, e.g. by the name of the author/contender, or by whatever you want to
   * identify this instance when loaded elsewhere.
   * @return the identifier
   */
  public String getName() {
    return name;
  }

  /**
   * Classifies the bitmap
   * @param map the bitmap to classify
   * @return the probabilities of all the classes (should add up to 1).
   */
  public double[] test(Bitmap map) {
	ClassifiedBitmap cBmap = Centered((ClassifiedBitmap) map);
    double[] output=nn.feedforward(cBmap.toDoubleArray());
    double sum = 0.0;
	for (int i = 0; i < 26; i++){
		sum += output[i];
		//System.out.println(output[i]);
	}
	System.out.println("sum: "+sum);
    return output;
  }
  

  /**
   * Trains the neural network classifier on randomly picked samples from specified training data.
   * @param maps the bitmaps which are used as training inputs including targets
   * @param nPresentations the number of samples to present
   * @param eta the learning rate
   */
  public void train(ClassifiedBitmap[] maps, int nPresentations, double eta) {
	long startTime = System.currentTimeMillis();
	/*for (int j = 0; j < 50; j++){
		for (int i = 0; i < maps.length; i++){
			nn.train(((Bitmap) maps[i]).toDoubleArray(),
					targets[maps[i].getTarget()], eta);
			//
		}
		System.out.println(j);
	}*/
	for (int p = 0; p < nPresentations; p++) {
		int sample = rand.nextInt(maps.length/2) + maps.length/2;
		// System.out.println(sample);
		nn.train(((Bitmap) maps[sample]).toDoubleArray(),
				targets[maps[sample].getTarget()], eta);
	}
    long endTime = System.currentTimeMillis();
    System.out.println("Done " + "Time: " + (endTime - startTime)/1000.0 );
  }

  /**
   * Construct a neural network classifier for bitmaps of specified size.
   * @param nRows number of rows in the bitmap
   * @param nCols number of columns in the bitmap
   */
  public NNClassifier(int nRows, int nCols) {
    rand=new Random(System.currentTimeMillis());
    nn=new NN1(nRows*nCols, getClassCount(), rand.nextInt());
    targets=new double[getClassCount()][getClassCount()];
    for (int c=0; c<getClassCount(); c++)
      targets[c][c]=1;
  }

}