package edu.rochester.kanishk.fastapriori;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

public class CandidateSetThread implements Callable<ItemSet> {

	private ItemSet itemSet;

	private ItemSet itemSet2;

	private Set<ItemSet> frequentItemSet;

	public CandidateSetThread(Set<ItemSet> frequentItemSet, ItemSet itemSet, ItemSet itemSet2) {
		this.itemSet = itemSet;
		this.itemSet2 = itemSet2;
		this.frequentItemSet = frequentItemSet;
	}

	@Override
	public ItemSet call() throws Exception {
		int i = 0;
		boolean join = true;
		ItemSet candidateSet = new ItemSet();
		/*
		 * Perform join operations between frequent itemsets The itemsets are
		 * maintained in a lexicographic order
		 */
		int itemSetCount = itemSet.itemSet.size();
		Iterator<Item> itemSetIterator = itemSet.itemSet.iterator();
		Iterator<Item> itemSet2Iterator = itemSet2.itemSet.iterator();
		while (itemSetIterator.hasNext() && itemSet2Iterator.hasNext()) {
			// Checking condition l1[1] = l2[1] ^ l1[2] = l2[2] ^.....^ l1[k -1]
			// < l2[k -1]
			Item item1 = itemSetIterator.next();
			Item item2 = itemSet2Iterator.next();
			if (i == itemSetCount - 1) {
				join = join && item1.isLessThan(item2);
				candidateSet.addItem(item1);
				candidateSet.addItem(item2);
			} else {
				join = join && item1.equals(item2);
				candidateSet.addItem(item1);
			}
			String threadName = Thread.currentThread().getName();
//			System.out.println(threadName + "::" + itemSet.toString());
//			System.out.println(threadName + "::" + itemSet2.toString());
//			System.out.println(threadName + "::" + candidateSet.toString());
			if (!join) {
				break;
			}
			i++;
		}
		if (join && hasFrequentSubset(frequentItemSet, candidateSet)) {
			// Prune the candidate set
			return candidateSet;
		} else {
			return null;
		}
	}

	private boolean hasFrequentSubset(Set<ItemSet> itemSets, ItemSet candidateSet) {
		Set<Item> tempSet = new HashSet<>();
		tempSet.addAll(candidateSet.itemSet);
		for (Item i : candidateSet.itemSet) {
			boolean found = false;
			tempSet.remove(i);
			for (ItemSet itemSet : itemSets) {
				if (itemSet.itemSet.containsAll(tempSet)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			tempSet.add(i);
		}
		return true;
	}

}
