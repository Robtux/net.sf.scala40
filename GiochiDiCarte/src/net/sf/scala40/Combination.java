/*
 /*
 * Copyright 2012 Roberto Zaniol
 * 
 * This file is part of SCALA40.
 *
 *  SCALA40 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SCALA40 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SCALA40.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.scala40;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Represents a combination of cards. This is the class from which descend the {@code Scala} and
 * {@code tris} classes that represent combinations of tris (poker) and scale. This class can be
 * the base for other games, and then provides the possibility to use or not the wildcard (jolly)
 * and different dimensions of the combinations.
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class Combination {
	/**
	 * Minimum amount of cards that make the combination
	 */
	private final int minLenghtCombination;
	/**
	 * Maximum amount of cards you can make up the combination
	 */
	private final int maxLenghtCombination;
	/**
	 * Enumerations for wildcard(jolly).
	 */
	protected EnumSet<?> jolly=null;
	/**
	 * The Combination
	 */
	private ArrayList<Card> combination;
	/**
	 * The Constructor. Receives as a parameter {@code ArrayList} of {@code Card} that will be
	 * the Object Combination. By calling the method {@code isCombinable} verify that the
	 * ArrayList is a valid combination, otherwise throws an exception.
	 * N.B. In this constructor missing Jolly. It can only be used if you do not use wildcards.
	 * 
	 * @param combination An ArrayList of Card witch contains the combination.
	 * @param min The minimum possible size for the combination
	 * @param max The maximum possible size for the combination
	 * @throws IOException Exception thrown if the ArrayList is not a valid combination
	 */
	public Combination(ArrayList<Card> combination, int min, int max) throws IOException {
		minLenghtCombination=min;
		maxLenghtCombination=max;
		if(isCombinable(combination)){
			this.combination=new ArrayList<Card>(maxLenghtCombination);
			for (Card card:combination){
				this.combination.add(card);
			}
		} else throw new IOException(); //definire un'eccezione personalizzata
	}
	/**
	 * The Constructor with jolly. Receives as a parameter {@code ArrayList} of {@code Card} that will be
	 * the Object Combination. By calling the method {@code isCombinable} verify that the
	 * ArrayList is a valid combination, otherwise throws an exception.
	 *  
	 * @param combination An ArrayList of Card witch contains the combination.
	 * @param min The minimum possible size for the combination
	 * @param max The maximum possible size for the combination
	 * @param jolly The enumeration representing the wildcard.
	 * @throws IOException Exception thrown if the ArrayList is not a valid combination
	 */
	public Combination(ArrayList<Card> combination, int min, int max, EnumSet<?> jolly) throws IOException {
		minLenghtCombination=min;
		maxLenghtCombination=max;
		this.jolly=jolly;
		if(isCombinable(combination)){
			this.combination=new ArrayList<Card>(maxLenghtCombination);
			this.combination.addAll(combination);
		} else throw new IOException();
	}
	/**
	 * Method invoked by the Constructor to verify if the ArrayList passed as a parameter is a valid combination.
	 * In this class occurs only if the size is between the minimum and maximum. Must be overridden in derived
	 * classes to define the criteria for which the derived combination is valid.
	 *  
	 * @param combination An ArrayList of Card that is not yet a combination of which is to verify the
	 * possibility that it is a correct combination
	 * 
	 * @return {@code True} if the ArrayList of Card is a correct combination;
	 */
	public boolean isCombinable(ArrayList<Card> combination){
		if(combination.size()<minLenghtCombination || combination.size()>maxLenghtCombination) return false;
		return true;
	}
	public ArrayList<Card> getCombination(){
		return combination;
	}
	// -------------------------------------  DA MODIFICARE  -----------------------------------
	// I metodi getRankOrdinal e getSuitOrdinal dovrebbero essere definiti nella classe Card !!!
	/**
	 * Return the number {@code Ordinal} in the enumeration Rank of the i-th card of the combination
	 * 
	 * @param i The index of card in the Combination
	 * @return the number {@code Ordinal}
	 */
	public int getRankOrdinal (int i){
		return this.getCombination().get(i).getRank().ordinal();
	}
	/**
	 * Return the number {@code Ordinal} in the enumeration Suit of the i-th card of the combination
	 * 
	 * @param i The index of card in the Combination
	 * @return the number {@code Ordinal}
	 */
	public int getSuitOrdinal (int i){
		return this.getCombination().get(i).getSuit().ordinal();
	}
	// -------------------------------------------------------------------------------------------
	/**
	 * Allows the addition of cards to the combination, if the new combination is valid.
	 * 
	 * @param card The card to be added to the combination
	 * @return {@code True} if the card can be added
	 */
	public boolean addCardToCombination(Card card){
		if (combination.size()!=maxLenghtCombination){
			combination.add(card);
			if (isCombinable(combination)){
				return true;
			} else {
				combination.remove(combination.size()-1);
				return false;
			}
		} else return false;
	}
	/**
	 * Determines the value of the combination by adding the number of the rank of cards.
	 * 
	 * @return The value of combination
 	 */
	public int valueCombination(){
		int value=0;
		for (Card currentCard:combination){
			value+=(currentCard.getRank().ordinal()+1);
		}
		return value;
	}
}
