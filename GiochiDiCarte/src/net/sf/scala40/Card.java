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

import java.util.Comparator;

/**
 * Represents the single playing card. Features of the card is the suit (suit) and rank (rank).
 * Suit and rank are two enumerations and are the parameters to be passed in this order to the
 * Constructor. The {@code Card} class implements the interfaces {@code Comparable} and
 * {@code Comparator} so that the {@code compareTo} method allow to order a collection of cards
 * on the basis of the suit, The {@code compare} method nested onto the constant
 * {@code FIRST_RANK_ORDER} allows sorting on the basis of rank. For completeness, it is define
 * the constant {@code FIRST_SUIT_ORDER} on which is nested {@code compare} method to order, with
 * Comparator interface, a collection of cards on the basis of the suit.
 *  
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class Card implements Comparable<Card>, Comparator<Card>{
	//**************************  variabili  ************************//
	/**
	 * Represents suit feature.
	 */
	private final Enum<?> suit;
	/**
	 * Represents rank feature
	 */
	private final Enum<?> rank;
	//*************************  costruttore  ***********************//
	/**
	 * The Constructor
	 * 
	 * @param suit The suit card
	 * @param rank The rank card
	 */
	public Card(Enum<?> suit, Enum<?> rank) {
		this.suit=suit;
		this.rank=rank;
	}
	//****************  implementazione di Comparable ***************//
	/** 
	 * Allows the comparison two objects belonging to a enumeration based on
	 * the number of orders that have into enumeration
	 * 
	 * @param e1
	 * @param e2
	 * @return 1 se e1>e2, 0 se e1==e2, -1 se e1<e2
	 */
	private static int compareEnum (Enum<?> e1, Enum<?> e2){
		return (e1.ordinal()>e2.ordinal() ? 1 : (e1.ordinal()==e2.ordinal() ? 0 : -1)); 
	}
	/**
	 * Taking advantage of the method {@code compareEnum} compare the current object
	 * whit a Card passed as an argument
	 * 
	 * @param card The card to compare with the current object
	 * @return 1 if {@code this>card}, 0 if {@code this==card}, {@code this<card}
	 */
	public int compareTo(Card card) {
		int ordine = compareEnum(this.getRank(),card.getRank());
		return (ordine!=0 ? ordine : compareEnum(this.getSuit(),card.getSuit()));
	}
	//****************  implementazione di Comparator  **************//
	public int compare(Card card1, Card card2) {
		return 0;
	}
	/**
	 * Lets sort a collection of cards on the basis of rank
	 */
	public static final Comparator<Card> FIRST_RANK_ORDER = new Comparator<Card>() {
		public int compare(Card card1, Card card2) {
			int ordine = compareEnum(card1.getRank(),card2.getRank());
			return (ordine!=0 ? ordine : compareEnum(card1.getSuit(),card2.getSuit()));
		}
	};
	/**
	 * Lets sort a collection of cards on the basis of suit
	 */
	public static final Comparator<Card> FIRST_SUIT_ORDER = new Comparator<Card>() {
		public int compare(Card card1, Card card2) {
			int ordine = compareEnum(card1.getSuit(),card2.getSuit());
			return (ordine!=0 ? ordine : compareEnum(card1.getRank(),card2.getRank()));
		}
	};
	//******************** metodi getter/setter  *********************//
	/**
	 * Return the suit card
	 * 
	 * @return The suit card
	 */
	public Enum<?> getSuit(){
		return suit;
	}
	/**
	 * Return the rank card
	 * 
	 * @return The rank card
	 */
	public Enum<?> getRank(){
		return rank;
	}
}

