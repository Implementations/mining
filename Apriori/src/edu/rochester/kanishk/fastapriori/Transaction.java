package edu.rochester.kanishk.fastapriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rochester.kanishk.Constants;

public class Transaction {
	
	/** This list is cleaned up after all the items are moved into set*/
	private List<Item> itemList;
	
	Set<Item> itemSet;
	
	int id;

	public Transaction(int id) {
		this.id = id;
		this.itemList = new ArrayList<>(13);
		this.itemSet = new LinkedHashSet<>(13);
	}

	public void add(String category, String itemType, String value) {
		itemList.add(new Item(category, itemType, value));
	}

	public void add(Item item) {
		itemList.add(item);
	}
	
	
	/**
	 * Takes the median capital gain and capital loss value to use for grouping. The grouping is
	 * as follows.
	 * None:0, 0 < Low <= median, High > median.
	 * The discrete values are: gain_none, gain_low, gain_high. Same goes for capital loss. 
	 */
	public void cleanUp(float gain, float loss, Map<Item, Integer> oneItemSet) {
		correctValue(gain, itemList.get(Constants.GAIN_INDEX), Constants.GAIN);
		correctValue(loss, itemList.get(Constants.LOSS_INDEX), Constants.LOSS);
		ageGroup(itemList.get(Constants.AGE_INDEX));
		hoursGroup(itemList.get(Constants.HOURS_INDEX));
		List<Item> newList = new ArrayList<>(itemList.size());
		Integer itemCount;
		for (Item i : itemList) {
			if (!Constants.GARBAGE.equals(i.value)) {
				newList.add(i);
				itemCount = oneItemSet.get(i);
				if (itemCount == null) {
					oneItemSet.put(i, 1);
				} else {
					itemCount++;
					oneItemSet.put(i, itemCount);
				}
			}
		}
		itemList = null;
		Collections.sort(newList);
		itemSet.addAll(newList);
	}

	private void correctValue(float correctValue, Item item, String gainLoss) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value == 0) {
				item.itemType = gainLoss + Constants.CAPITAL_NONE;
			} else if (value < correctValue) {
				item.itemType = gainLoss + Constants.LOW;
			} else {
				item.itemType = gainLoss + Constants.HIGH;
			}
		}
	}
	
	/**
	 * Age into group
	 */
	private void ageGroup(Item item) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value <= 25) {
				item.itemType = Constants.YOUTH;
			} else if (value <= 45) {
				item.itemType = Constants.MIDDLE_AGE;
			} else if (value < 65) {
				item.itemType = Constants.SENIOR;
			} else {
				item.itemType = Constants.SUPER_SENIOR;
			}
		}
	}

	/**
	 * Working hours into group
	 */
	private void hoursGroup(Item item) {
		if (!Constants.GARBAGE.equals(item.value)) {
			int value = Integer.parseInt(item.value);
			if (value <= 25) {
				item.itemType = Constants.PART_TIME;
			} else if (value <= 40) {
				item.itemType = Constants.FULL_TIME;
			} else if (value < 60) {
				item.itemType = Constants.OVERTIME;
			} else {
				item.itemType = Constants.BURNOUT;
			}
		}
	}
	
	/**
	 * Gets all the items in the transaction as a string.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Item i : itemList) {
			sb.append(i.itemType).append(" ,");
		}
		return sb.toString();
	}
	
	public void print() {
		System.out.println(toString());
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
