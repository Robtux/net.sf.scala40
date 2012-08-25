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

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Represents the deck of cards. It has different constructors so you
 * can define different types of decks. It has two enumerations for
 * basic Suit and Rank to use in the constructor without parameters.
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class Deck{
	/**
	 * The four Suit of French card (or "piemontesi")
	 */
	private static enum Suit{
		COPPE,DENARI,BASTONI,SPADE;
	}
	/**
	 * The 13 value/rank of French card (or "piemontesi")
	 */
	private static enum Rank{
		ASSO,DUE,TRE,QUATTRO,CINQUE,SEI,SETTE,
		OTTO,NOVE,DIECI,FANTE,CAVALLO,RE;
	}
	/**
	 * The deck
	 */
	private ArrayList<Card> deck=new ArrayList<Card>();
	/**
	 * The Constructor without any input parameters that uses the
	 * basic set of enum as constants in this class.
	 */
	public Deck(){
		this(EnumSet.allOf(Suit.class),EnumSet.allOf(Rank.class));
	}
	/**
	 * The Constructor without jolly. It is also used by the parameterless
	 * constructor that invokes them by passing default enumerations.
	 * 
	 * @param suit
	 * @param rank
	 */
	public Deck(EnumSet<?> suit, EnumSet<?> rank){
		for (Enum<?> currentSuit:suit){
			for(Enum<?> currentRank:rank){
				deck.add(new Card(currentSuit,currentRank));
			}
		}
	}
	/**
	 * The Constructor whit jolly.
	 * 
	 * @param suit
	 * @param rank
	 * @param jolly
	 */
	public Deck(EnumSet<?> suit, EnumSet<?> rank, EnumSet<?> jolly){
		this(suit,rank);
		for (Enum<?> currentJolly:jolly){
			deck.add(new Card(currentJolly,currentJolly));
		}
	}

	public Card getCard (int i){
		return deck.get(i);
	}
	public ArrayList<Card> getDeck(){
		return this.deck;
	}
}