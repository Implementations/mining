package edu.rochester.kanishk.fpgrowth;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.rochester.kanishk.Constants;

/**
 * @author kanishk
 * Generates the transaction data and 1-frequent itemsets. Fields like fnlwght, education num
 * are not taken. Continuous fields like age, number of hours, capital gain, capital loss are discretized.
 * The capital gain and capital loss are collected and their median value is used for grouping.
 */
public class Preprocessor {
	
	/** Maintain a 1-frequent itemset and links of node between the FP tree*/
	private Map<Item, Header> oneItemMap;
	
	private List<Transaction> transList;
	
	private int supportCount;

	public void generateItems(String filePath, int supportCount) throws IOException {
		this.transList = new ArrayList<>();
		this.supportCount = supportCount;
		this.oneItemMap = new HashMap<>();
		List<Integer> capitalGain = new ArrayList<>();
		List<Integer> capitalLoss = new ArrayList<>();
		BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), Constants.ENCODING);
		String line = null;
		int counter = 0;
		while ((line = reader.readLine()) != null && !line.isEmpty()) {
			createTransaction(transList, line, capitalGain, capitalLoss);
			counter++;
		}
		reader.close();
		Collections.sort(capitalGain);
		Collections.sort(capitalLoss);
		float medianGain = medianCalculate(capitalGain);
		float medianLoss = medianCalculate(capitalLoss);
		generateOneItemSet(transList, medianGain, medianLoss);
	}
	
	private float medianCalculate(List<Integer> sortedList) {
		int size = sortedList.size();
		int index = size/2;
		if(size % 2 == 0) {
			return (sortedList.get(index) + sortedList.get(size - 1))/2;
		} else {
			return sortedList.get(index);
		}
	}
	
	/**
	 * Generate frequent one itemset.
	 */
	private void generateOneItemSet(List<Transaction> transactions, 
			float medianGain, float medianLoss) {
		Map<Item, Integer> itemCountMap = new HashMap<>();
		for(Transaction t : transactions) {
			t.cleanUp(medianGain, medianLoss, itemCountMap); 
		}
		for(Entry<Item, Integer> e : itemCountMap.entrySet()) {
			if(e.getValue() >= supportCount) {
				this.oneItemMap.put(e.getKey(), new Header(e.getKey(), e.getValue()));				
			}
		}
	}
	
	/**
	 * Takes a single line from data file and creates transaction object from it.
	 * Removes redundant fields from the transaction. Also adds the capital gain and
	 * loss values to calculate the median values.
	 */
	private void createTransaction(List<Transaction> transList, String value, List<Integer> gain, 
			List<Integer> loss) {
		String[] values = value.split(", ");
		Transaction transaction = new Transaction();
		transaction.add("age", values[0], values[0]);
		transaction.add("work", values[1], values[1]);
		transaction.add("edu", values[3], values[3]);
		transaction.add("marital", values[5], values[5]);
		transaction.add("job", values[6], values[6]);
		transaction.add("rel", values[7], values[7]);
		transaction.add("race", values[8], values[8]);
		transaction.add("sex", values[9], values[9]);
		transaction.add("gain", values[10], values[10]);
		transaction.add("loss", values[11], values[11]);
		transaction.add("hours", values[12], values[12]);
		transaction.add("country", values[13], values[13]);
		transaction.add("status", values[14], values[14]);
		transList.add(transaction);
		if(!values[10].equals(Constants.GARBAGE)) {
			gain.add(Integer.parseInt(values[10]));
		}
		if(!values[11].equals(Constants.GARBAGE)) {
			loss.add(Integer.parseInt(values[11]));
		}
	}

	public List<Transaction> getTransList() {
		return transList;
	}

	public Map<Item, Header> getOneItemMap() {
		return oneItemMap;
	}

	public void setOneItemMap(Map<Item, Header> oneItemMap) {
		this.oneItemMap = oneItemMap;
	}
}
