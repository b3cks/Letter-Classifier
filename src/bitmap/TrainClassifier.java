package bitmap;

import java.io.*;

/**
 * This program trains a classifier and saves it in a file to be read when used.
 * @author Mikael Boden
 * @version 1.0
 */

public class TrainClassifier {

  public TrainClassifier(String[] args) {
    // create the classifier
    // NNClassifier c1=new NNClassifier(32, 32);
    // or - for ID3 - replace with the following line of code
    // ID3Classifier c=new ID3Classifier(32, 32);
    Classifier_43129597 c = new Classifier_43129597(32,32);
    // load data
    try {
      ClassifiedBitmap[] bitmaps=LetterClassifier.loadLetters(args[1]);
      // train it using all available training data
      // or - for ID3 - replace with the following line of code
      long startTime = System.currentTimeMillis();
      
      c.train(bitmaps,7500,0.1);
      
      long endTime = System.currentTimeMillis();
      
      System.out.println((endTime-startTime)/1000.0);
      // Visualize
      /*PrintWriter writer = new PrintWriter("myOutput.txt", "UTF-8");
      for (int i = 0; i < 32*32; i++){
    	  writer.println(c.nn.w[2][i]);
      }
      writer.close();*/
    } catch (IOException ex) {
      System.err.println("Error loading data.txt: "+ex.getMessage());
    }
    try {
      Classifier.save(c, args[0]);
      /*Classifier.save(c2, args[2]);
      Classifier.save(c3, args[3]);
      Classifier.save(c4, args[4]);
      Classifier.save(c5, args[5]);
      Classifier.save(c6, args[6]);*/
    } catch (Exception ex) {
      System.err.println("Failed to serialize and save file: "+ex.getMessage());
    }
  }

  public static void main(String[] args) {
    if (args.length!=2) {
      System.err.println("Usage: TrainClassifier <classifier-file> <bitmap-file>");
      System.exit(1);
    }
    new TrainClassifier(args);
  }

}