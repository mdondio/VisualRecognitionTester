package net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier.VisualClass;

import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Images;

/**
 * This class contains information regarding a test performed by Watson visual
 * recognition
 * 
 * @author Marco Dondio
 *
 */
// TODO refactor
public class WatsonBinaryClassificationResult {

	public static enum METRIC {
		TP, TN, FP, FN, POS, NEG, tp, tn, fp, fn, pp, pn, tpr, fpr, precision, recall, accuracy, fallout, f1, jaccard
	};

	private Classifier classifierJson;
	private Dataset testSet;

	private double threshold;
//	private HashMap<Long, Boolean> realValues;
	private HashMap<Long, Boolean> predictedValues;

	int datasetSize; // size of vectors

	// Vector of basic measures: TP, TN, FP, FN
	EnumMap<METRIC, Integer> measures = new EnumMap<METRIC, Integer>(METRIC.class);

	// Vector of FP and FN
	private List<Long> falsePositives;
	private List<Long> falseNegatives;

	private List<Double> histogramPositive;	// scores dei positivi
	private List<Double> histogramNegative;	// scores dei negativi


	public WatsonBinaryClassificationResult(Classifier classifier, Dataset testSet, List<VisualClassification> watsonResults,
			double threshold) {

		this.classifierJson = classifier;
		this.testSet = testSet;
		this.threshold = threshold;

		this.histogramPositive = new ArrayList<Double>();
		this.histogramNegative = new ArrayList<Double>();

		this.predictedValues = buildPredictedValuesAndHistogram(watsonResults);
		
		// real images returned
		this.datasetSize = histogramPositive.size() + histogramNegative.size();

		
		
		// build contingency matrix
		computeStats();
		
	}


	// TODO fare più robusto: devo controllare nome classificatore e nome classe
	// per essere sicuri, ora assumo ce ne sia solo uno...
	private HashMap<Long, Boolean> buildPredictedValuesAndHistogram(List<VisualClassification> watsonResults) {

		HashMap<Long, Boolean> values = new LinkedHashMap<Long, Boolean>();

		// -------------------

		// TODO aggiungere un controllo: se supero il limite di classificazioni
		// watRes può non essere corretto (capire, ad es no images).. da gestire correttamente
		// For each block of results...
		for (VisualClassification watRes : watsonResults) {

//			System.out.println("--------------------------------------");
//			System.out.println(watRes);
//			System.out.println("--------------------------------------");

//			System.out.println("Processed: " + watRes.getImagesProcessed() + " images.");
	//		System.out.println("images Size: " + watRes.getImages().size() + " images.");

			// for each image
			for (ImageClassification img : watRes.getImages()) {
				
				// XXX capire che succede, mi è capitato un caso con eccezione
				if(img == null){
					System.out.println("[WatsonBinaryClassificationResult buildPredictedValuesAndHistogram()] watRes images null. Skip");
					continue;
				}

				// First generate imageID from name
				// img.getImage(); // XXX nome dell'immagine? qui estrai ID
				// immagine

				String s = img.getImage().replaceAll(".jpg", "");
				s = s.substring(s.lastIndexOf("/") + 1);
				long imageID = Long.parseUnsignedLong(s);

				// If no classifier found prediction is false...
				if (img.getClassifiers().isEmpty()) {
					values.put(imageID, false); // classifier not found!
					
					if(testSet.getImages().getPositives().contains(imageID))
					  histogramPositive.add(0.0);
					else
						histogramNegative.add(0.0);
					continue;
				}
				// XXX assume we have only one classifier
				VisualClassifier classifier = img.getClassifiers().get(0);

				String classifierName = classifierJson.getLabel() + "_classifier";
				if (!classifierName.equals(classifier.getName())) {
					values.put(imageID, false); // classifier not found!
					if(testSet.getImages().getPositives().contains(imageID))
						  histogramPositive.add(0.0);
						else
							histogramNegative.add(0.0);
					continue;
				}

				// If no class found prediction is false...
				if (classifier.getClasses().isEmpty()) {
					values.put(imageID, false);
					if(testSet.getImages().getPositives().contains(imageID))
						  histogramPositive.add(0.0);
						else
							histogramNegative.add(0.0);
					continue;

				}

				// XXX assume we have only one class
				VisualClass myClass = classifier.getClasses().get(0);

				if (!classifierJson.getLabel().equals(myClass.getName())) {
					values.put(imageID, false); // class not found!
					if(testSet.getImages().getPositives().contains(imageID))
						  histogramPositive.add(0.0);
						else
							histogramNegative.add(0.0);
					continue;
				}

				// Classify according to threshold
				double watsonScore = myClass.getScore();

				if (Double.compare(watsonScore, threshold) < 0)
					values.put(imageID, false); // classified as negative!
				else // classified as positive!
					values.put(imageID, true); // classifier not found!

				
				if(testSet.getImages().getPositives().contains(imageID))
					  histogramPositive.add(watsonScore);
					else
						histogramNegative.add(watsonScore);

				
				// System.out.println(Long.toUnsignedString(imageID) + " Watson
				// Score -> " + watsonScore);
			}

			////////////////////////////////////////////////

			// TODO aggiungere quelli non riconosciuti!!
			// XXX assumption: se Watson non ritorna uno score per una immagine
			// su questa classe, significa che per lui lo score è < 0.5
			// per noi score < 0.5 diventa classe negativa!

			// ATTENZIONE! Supponiamo di forzare la classe a 0 in questo caso
			// for (Long l : realValues.keySet())
			// if(!values.containsKey(l))
			// values.put(l, false);
			// Non serve più

		}
		// -------------------

		return values;
	}


	// generates all needed basic metrics
	// TODO refactor... molte cose inutili
	private void computeStats() {

		// TODO fill absMetrics (TP, TN, FP, FN)

		// Gather basic measures...
		int TP = 0, TN = 0, FP = 0, FN = 0;
		
		// Initialize FP and FN
		// TODO probabilmente inutili li posso recuperare dal dataset!!!!
		falsePositives = new ArrayList<Long>();
		falseNegatives = new ArrayList<Long>();

		///------------------------------------------------------------------
		// TODO sistemare qui sotto
		// For each image...
	//	for (Long imageID : realValues.keySet()) {
		Images images = testSet.getImages();
			for (Long imageID : predictedValues.keySet()) {	// for all ID
			
			boolean realClass = images.getPositives().contains(imageID) ? true : false;
			boolean predictedClass = predictedValues.get(imageID);

			///------------------------------------------------------------------

			// System.out.println(
			// Long.toUnsignedString(imageID) + " - real: " + realClass + " vs
			// predicted: " + predictedClass);

			if (realClass && predictedClass)
				TP++;
			else if (realClass && !predictedClass){
				falseNegatives.add(imageID);
				FN++;
			}
			else if (!realClass && predictedClass){
				falsePositives.add(imageID);
				FP++;
			}
			else // !realClass && !predictedClass
				TN++;
		}
		measures.put(METRIC.TP, TP);
		measures.put(METRIC.TN, TN);
		measures.put(METRIC.FP, FP);
		measures.put(METRIC.FN, FN);

//		 for (METRIC m : measures.keySet())
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()]" + m + " -> " + measures.get(m));
//
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] tp -> " + computeMetric(METRIC.tp));
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] tn -> " + computeMetric(METRIC.tn));
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] fp -> " + computeMetric(METRIC.fp));
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] fn -> " + computeMetric(METRIC.fn));
//
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] tpr -> " + computeMetric(METRIC.tpr));
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] fpr -> " + computeMetric(METRIC.fpr));
//
//		 
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] POS -> " + computeMetric(METRIC.POS));
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] NEG -> " + computeMetric(METRIC.NEG));
//
//		 System.out.println("[WatsonBinaryClassificationResult computeStats()] datasetSize -> " + datasetSize);

		 
	}

	
//	private void buildHistograms(List<VisualClassification> watsonResults) {
//		// TODO
//	
//		// Get all real image classes...
//		List<Long> positives = testSet.getImages().getPositives();
//		List<Long> negatives = testSet.getImages().getNegatives();
//		
//		
//		// per tutti i risultati
//		for(VisualClassification : watsonResults)
//		
//		
//		
//	}


	public double computeMetric(METRIC m) {
		switch (m) {
		case POS:	// positive values in dataset
			return histogramPositive.size();
		case NEG: // negative values in dataset
			return histogramNegative.size();
//		case POS:	// positive values in dataset
//			return testSet.getImages().getPositives().size();
//		case NEG: // negative values in dataset
//			return testSet.getImages().getNegatives().size();
		case tp: // true positive %
			return (double) measures.get(METRIC.TP) / (double)datasetSize;
		case tn: // true negative %
			return (double) measures.get(METRIC.TN) / (double)datasetSize;
		case fp: // false positive %
			return (double) measures.get(METRIC.FP) / (double)datasetSize;
		case fn: // false negative %
			return (double) measures.get(METRIC.FN) / (double)datasetSize;
		case tpr: // true positive ratio
			return (double) computeMetric(METRIC.tp) / (double) (computeMetric(METRIC.tp) + computeMetric(METRIC.fn));
		case fpr: // false positive ratio
			return (double) computeMetric(METRIC.fp) / (double) (computeMetric(METRIC.fp) + computeMetric(METRIC.tn));
//		case fpr: // false positive ratio
//			return (double) computeMetric(METRIC.fp) / (double) (computeMetric(METRIC.tp) + computeMetric(METRIC.fn));
		case precision:
			return (double) measures.get(METRIC.TP) / (double)(measures.get(METRIC.TP) + measures.get(METRIC.FP));
		case recall:
			return (double) measures.get(METRIC.TP) / (double)(measures.get(METRIC.TP) + measures.get(METRIC.FN));
		case accuracy:
			return (double) (measures.get(METRIC.TP)+measures.get(METRIC.TN) ) / (double)(computeMetric(METRIC.POS) + computeMetric(METRIC.NEG));
		default:
			return 0;
		}
	}

	public double getThreshold(){
		return threshold;
	}
	
	public double getClassifierTrainingSize(){
		return classifierJson.getTrainingSize();
	}

	public List<Long> getfalsePositives() {

		return falsePositives;
	
	}
	public List<Long> getfalseNegatives() {
		return falseNegatives;
	}

	public List<Double> getHistogramPositive() {
		return histogramPositive;
	}
	public List<Double> getHistogramNegative() {
		return histogramNegative;
	}

}
