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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.scala40.Scala40.Suit;

/**
 * The engine that handle the computer player.
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class PcPlayer {
	
	private ArrayList<Card> playerCard;
	private EnumSet<?> jolly;
	private Card [] jollyArray;
	private Map<ArrayList<Integer>,Combination> combMap=new HashMap<ArrayList<Integer>,Combination>();
	private Map<ArrayList<Integer>,Combination> probCombMap=new HashMap<ArrayList<Integer>,Combination>();
	private ArrayList<Card> indexJolly=new ArrayList<Card>(4);
	
	public PcPlayer(ArrayList<Card> playerCard,EnumSet<?> jolly){
		this.playerCard=playerCard;
		this.jolly=jolly;
		this.jollyArray=new Card[jolly.size()];
		int i=0;
		for (Enum<?> j:this.jolly){
			jollyArray[i++]=new Card(j,j);
		}
	}
	/**
	 * Replaces Card {@code card} on Combination {@code comb} depending on whether
	 * it is a {@code Tris} or a {@code Scala} with a card that has fictitious 
	 * suit(rank) equal to {@code card} and rank(suit) equal to jolly {@ code JC}.
	 * 
	 * @param comb The combination that contains the card to be replaced
	 * @param card The paper to be replaced
	 * @param jC The jolly from which derive the suit or rank
	 */
	public static void changeCardToJolly (Combination comb,Card card, Card jC){
		Card newCard;
		int index=comb.getCombination().indexOf(card);
		if (comb instanceof Tris) {
			newCard=new Card(card.getSuit(),jC.getRank());
			comb.getCombination().set(index, newCard);
		} else {
			newCard=new Card(jC.getSuit(),card.getRank());
			comb.getCombination().set(index, newCard);
		}
	}
	// ----------------------------- UNIFICARE I DUE METODI CHE SEGUONO ---------------------------------
	/**
	 * This method verify if the ArrayList cardArray is a valid Combination. Then put in
	 * {@code combMap} the combination returned from Scala40.checkCombination method.
	 * 
	 * @param cardArray
	 */
	public void calcolaCombinazioniSenzaJolly(ArrayList<Card> cardArray){
		ArrayList<Integer> indexCardOfCombination=new ArrayList<Integer>();
		try {
			Combination comb=Scala40.checkCombination(cardArray);
			for (Card currentCard:comb.getCombination()){
				indexCardOfCombination.add(this.playerCard.lastIndexOf(currentCard));
			}
			this.combMap.put(indexCardOfCombination, comb);
		} catch (IOException e) {
		}
	}
	
	public void calcolaCombinazioniConJolly(ArrayList<Card> cardArray){
		ArrayList<Integer> indexCardOfCombination=new ArrayList<Integer>();
		//bisogna scegliere quale carta il jolly deve sostituire
		//Card cJolly=new Card(jollyArray[0].getSuit(),jollyArray[0].getRank());
		Card cJolly=jollyArray[0];
		cardArray.add(cJolly);
		try {
			Combination comb=Scala40.checkCombination(cardArray);
			for (Card currentCard:comb.getCombination()){
				if (!jolly.contains(currentCard.getRank())) {
					indexCardOfCombination.add(this.playerCard.lastIndexOf(currentCard));
				}
			}
			this.probCombMap.put(indexCardOfCombination, comb);
		} catch (IOException e) {
		}
	}
	// -----------------------------------------------------------------------------------------
	
	public void calcolaCombinazioni(ArrayList<Card> cardArray){
		calcolaCombinazioniSenzaJolly(cardArray);
		if (cardArray.size()>1) {
			for (Card card:cardArray){
				verificaTris(card,cardArray);
				verificaScala (card, cardArray);
			}
		}
	}
	/*
	public void reiteraSuCombinazioni(ArrayList<Card> cardArray, ArrayList<Card> newCardArray){
		for (int i=1; i<=newCardArray.size();i++){
			newCardArray.set(newCardArray.size()-i,cardArray.get(cardArray.size()-i));
			calcolaCombinazioni(newCardArray);
		}
	}
	*/
	/**
	 * Called by the method {@code handPcPlayer} of the class {@code Scala40GUI} to handle the
	 * opening phase of the Computer player.
	 * 
	 * @param cardArray The card fo player
	 * @return A map containing the combinations extracted from cardArray
	 */
	public Map<ArrayList<Integer>,Combination> aprire(ArrayList<Card> cardArray){
		
		Map<ArrayList<Integer>,Combination> newCombMap=new HashMap<ArrayList<Integer>,Combination>();
		
		for (int i=0; i<cardArray.size();i++){		
			if (jolly.contains(cardArray.get(i).getRank())){
				indexJolly.add(cardArray.remove(i));
			}
		}
		
		calcolaCombinazioni(cardArray);
		
		if (indexJolly.size()!=0) cardArray.addAll(indexJolly);		
		
		if (cardArray.size()>3 && !combMap.isEmpty()){
			Map<ArrayList<Integer>,Integer> valoriCombinazioni=new HashMap<ArrayList<Integer>,Integer>();
			for (ArrayList<Integer> key:combMap.keySet()){
				if(!key.isEmpty()) valoriCombinazioni.put(key,combMap.get(key).valueCombination());
			}
		
			while (!valoriCombinazioni.isEmpty()){ 	// ad ogni ciclo estrapola la combinazione con il maggior valore
													// eliminando quelle che hanno una o più carte in comune ad essa
			
				ArrayList<Integer> valTemp=new ArrayList<Integer>();
		
				int val=0;
			
				for (ArrayList<Integer> key:valoriCombinazioni.keySet()){
					if (valoriCombinazioni.get(key)>val){
						val=valoriCombinazioni.get(key);
						valTemp.clear();
						valTemp.addAll(key);
					}
				}
				
				newCombMap.put(valTemp, combMap.get(valTemp));
				
				for (int i:valTemp ){
					for (Iterator<ArrayList<Integer>> keyProb=probCombMap.keySet().iterator();keyProb.hasNext();){
						if (keyProb.next().contains(i)) keyProb.remove();
					}
					for (Iterator<ArrayList<Integer>> keyIt=valoriCombinazioni.keySet().iterator();keyIt.hasNext();){
						if (keyIt.next().contains(i)) keyIt.remove();
					}
				}
			
			}
			
		}
		
		if (!probCombMap.isEmpty()){
				Map<ArrayList<Integer>,Integer> valoriCombinazioni=new HashMap<ArrayList<Integer>,Integer>();
				for (ArrayList<Integer> key:probCombMap.keySet()){
					if(!key.isEmpty()) valoriCombinazioni.put(key,probCombMap.get(key).valueCombination());
				}
		
				while (!valoriCombinazioni.isEmpty() && !indexJolly.isEmpty()){ 	// ad ogni ciclo estrapola la combinazione con il maggior valore
														// eliminando quelle che hanno una o più carte in comune ad essa
			
					ArrayList<Integer> valTemp=new ArrayList<Integer>();
		
					int val=0;
					
					for (ArrayList<Integer> key:valoriCombinazioni.keySet()){
						if (valoriCombinazioni.get(key)>val){
							val=valoriCombinazioni.get(key);
							valTemp.clear();
							valTemp.addAll(key);
						}
					}
					
					int index=probCombMap.get(valTemp).getCombination().lastIndexOf(jollyArray[0]);
					probCombMap.get(valTemp).getCombination().set(index, indexJolly.remove(0));
					newCombMap.put(valTemp, probCombMap.get(valTemp));
				
					for (int i:valTemp ){
						for (Iterator<ArrayList<Integer>> keyProb=probCombMap.keySet().iterator();keyProb.hasNext();){
							if (keyProb.next().contains(i)) keyProb.remove();
						}
						for (Iterator<ArrayList<Integer>> keyIt=valoriCombinazioni.keySet().iterator();keyIt.hasNext();){
							if (keyIt.next().contains(i)) keyIt.remove();
						}
					}
			}
		}
		
		return newCombMap;
	}
	
	/* ----------------------------  NON SERVE PIU'  ----------------------------- 
	public boolean pescareDalMazzo(Card scarti){
		// gestire la valutazione se con la carta dal mazzo degli scarti posso aprire o ottenere comunque un apertura con più carte
		// oppure non posso nemmeno aprire. Se nella valutazione definisco già delle combinazioni, salvarle per non
		// ripetere la valutazione.
		// -------- per il momento aprire da mazzo
		return true;
	}*/
	/**
	 * Returns the card that can be discarded, ie which do not attack and do not replace wildcard contained into
	 * combinations of players aperture, including those contained in cardArray.
	 * 
	 * @param cardArray
	 * @return A List of Card can be discarded
	 */
	public ArrayList<Card> cardScartabili (ArrayList<Card> cardArray){
		ArrayList<Card> scartabili=new ArrayList<Card>();
		if (!probCombMap.isEmpty()){
			for (int i=0;i<cardArray.size();i++){
				Card iCard=cardArray.get(i);
				for (Combination iComb:probCombMap.values()){
					if (!iComb.getCombination().contains(iCard)){
						if (!scartabili.contains(iCard)){
							scartabili.add(iCard);
						}
					}
				}
			}
		} else scartabili.addAll(cardArray);
		return scartabili;
	}
	
	/* --------------------  NON SERVONO PIU'  ----------------------
	public int scartare (ArrayList<Card> cardArray, int index){
		if (!probCombMap.isEmpty()){
			for (int i=index;i<cardArray.size();i++){
				Card iCard=cardArray.get(i);
				for (Combination iComb:probCombMap.values()){
					if (!iComb.getCombination().contains(iCard)){
						return cardArray.lastIndexOf(iCard);
					}
				}
			}
		}
		int x=(int) (Math.random()*(cardArray.size()));
		return x;
	}*/
	/*
	public void putInComb (Card card, ArrayList<Card> cardForComb, ArrayList<Card> cardList){
		cardForComb.add(card);
		calcolaCombinazioniSenzaJolly(cardForComb);
	}*/
	/*
	public void putInProbComb (Card card, ArrayList<Card> cardForComb, ArrayList<Card> cardList){
		
	}*/
	/**
	 * Extracts from ArrayList cardList the cards with the Card card can form a Tris.
	 * Depending on the size of obtained ArrayList the method calls calcolaCombinazioniSenzaJolly or
	 * calcolcaCombinazioniConJolly.
	 * 
	 * @param card The card according to which to extract the other cards.
	 * @param cardList The ArrayList to extract the cards
	 * @return True if the ArrayList has been larger than 2, false if size is equal to or less than 2.
	 */
	public boolean verificaTris(Card card, ArrayList<Card> cardList){
		ArrayList<Card> cardForTris =new ArrayList<Card>(4);

		for (Enum<?> suit:Suit.values()){
			for (Card currentCard: cardList){
				if (cardList.lastIndexOf(currentCard)>=cardList.lastIndexOf(card)){
					if (currentCard.getRank().equals(card.getRank()) && 
						currentCard.getSuit().equals(suit)){
						cardForTris.add(currentCard);
						break;
					}
				}
			}
		}
		
		switch (cardForTris.size()){
		case 3: case 4:	{ calcolaCombinazioniSenzaJolly(cardForTris); return true; }
		case 2:			{ calcolaCombinazioniConJolly(cardForTris); return false; }
		default:		{ return false; }
		}
	}
	/**
	 * Extracts from ArrayList cardList the cards with the Card card can form a Scala.
	 * Depending on the size of obtained ArrayList the method calls calcolaCombinazioniSenzaJolly or
	 * calcolcaCombinazioniConJolly.
	 * 
	 * @param card The card according to which to extract the other cards.
	 * @param cardList The ArrayList to extract the cards
	 * @return True if the ArrayList has been larger than 2, false if size is equal to or less than 2.
	 */
	public boolean verificaScala(Card card, ArrayList<Card> cardList){
		ArrayList<Card> cardForScala=new ArrayList<Card>(3);
		int index=card.getRank().ordinal();
		Enum<?> suit=card.getSuit();
		for (int i=0; i<=2;i++){
			for (Card currentCard: cardList){
				int rank = currentCard.getRank().ordinal();
				if (cardList.lastIndexOf(currentCard)>=cardList.lastIndexOf(card)){
					if (currentCard.getSuit()==suit){
						if (index<12) {
							if ((index==11 && rank==0) || (index!=11 &&	rank==(index+i))){
								cardForScala.add(currentCard);
								break;
							}
						}
					}
				}
			}	
		}
		
		switch (cardForScala.size()){
		case 3:		{ calcolaCombinazioniSenzaJolly(cardForScala); return true; }
		case 2:		{ calcolaCombinazioniConJolly(cardForScala); return false; }
		default:	{ return false; }
		}		
	}
}