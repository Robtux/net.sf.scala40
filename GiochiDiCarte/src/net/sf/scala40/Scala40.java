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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * The game engine.
 * 
 *@author Roberto Zaniol
 *@version 0.0.0.1 (pre-alpha)
 *
 */
public class Scala40 extends Throwable {
	
	private static final long serialVersionUID = 1L;
	/*
	 ********************************************** Variabili ***********************************************
	 */
	/*
	 * Number of player
	 */
	private final int nPly;
	/*
	 * {@code array} of type {@code Player} that contain the player
	 */
	////////////////////  CAMBIARE PLAYERS IN HASMAP O ALTRO DI SIMILARE   ////////////////////////////////
	//private final Map<String,ArrayList<Card>> players;
	private ArrayList<Player> players;

	protected static enum Suit{
		HEARTS,DIAMONDS,CLUBS,SPADES;
	}
	
	protected static enum Rank{
		ACE,DEUCE,THREE,FOUR,FIVE,SIX,SEVEN,
		EIGHT,NINE,TEN,JACK,QUEEN,KING;
	}
	protected static enum Jolly{
		JOLLYRED,JOLLYBLACK;
	}
	
	/**
	 * The deck of card initialized whit the enumeration of this class
	 */
	protected static final Deck deck=new Deck(EnumSet.allOf(Suit.class),EnumSet.allOf(Rank.class),EnumSet.allOf(Jolly.class));
	/*
	 * The deck of cards from which cards are caught
	 */
	private ArrayList<Card> mazzo=new ArrayList<Card>();//=new ArrayList<Card>(Arrays.asList(deck.getDeck()));
	/*
	 * The deck where the cards are discarded
	 */
	private ArrayList<Card> scarti;//=new ArrayList<Card>(104);
	/*
	 * To store the card that is discarded
	 */
	private Card cartaPescata=null; 
	/*
	 * a collection that contains for each player, which is the {@ code key} of {@ code Map},
	 * a {@ code ArrayList} of combinations that store aperture and subsequent additions.
	 */
	private HashMap<Player, ArrayList<Combination>> playerAperture;
	/*
	 ******************************************* Costruttori ************************************************
	 */
	/**
	 * The Constructor
	 * 
	 * @param players if the number of arguments are <2 warns and adds the number of players needed
	 * @throws Exception 
	 */
	public Scala40 (String... players) throws Exception{
		if (players.length<2) throw new Exception();
		this.nPly=players.length;
		this.players=new ArrayList<Player>(players.length);
		mazzo.addAll(deck.getDeck());
		mazzo.addAll(deck.getDeck());
		Collections.shuffle(mazzo);
		scarti=new ArrayList<Card>();
		for (String name: players){
			this.players.add(new Player(name,new ArrayList<Card>(13)));
		}
		daiCarte();
		playerAperture=new HashMap<Player,ArrayList<Combination>>(nPly);
		for (Player ply:this.players){
			playerAperture.put(ply, new ArrayList<Combination>());
		}		
	}
	/*
	 * Distributes the cards to the players and one on discard pile
	 */
	private void daiCarte (){
		for (int i=0;i<13;i++){
			for (Player ply: players){
				ply.addPlayerCard(mazzo.remove(mazzo.size()-1));
			}
		}// carte distribuite ai player
		scarti.add(mazzo.remove(mazzo.size()-1));//carta scoperta sul banco
	}
	/**
	 * Check how many card are on the deck and if there not are change the deck with the discard pile
	 */
	protected void checkMazzo(){
		if (mazzo.size()==0){
			changeDeck();				
		}
	}
	/*
	 * Change the deck with the discard pile after it is shuffled
	 */
	private void changeDeck(){
		Card lastScarto=scarti.remove(scarti.size()-1);
		Collections.shuffle(scarti);
		ArrayList<Card> tmp=new ArrayList<Card>();
		tmp=mazzo;
		mazzo=scarti;
		scarti=tmp;
		scarti.add(lastScarto);
	}
	/*
	 ************************************************* Core ****************************************************
	 */
	/**
	 * The management of the draw
	 * 
	 * @param ply The player whose turn
	 * @param command The command received from user. It must be 1 or 2
	 * @return -1 if the draw is made ​​from the discard pile without having already opened
	 * @return  0 if the draw is made ​​from the deck face
	 * @return  1 if the draw is made ​​from the discard pile having already opened
	 * @throws IOException When the received command is not 1 or 2
	 */
	public int pesca (Player ply, String command) throws IOException {
		switch (command){
			case "1" : {
				cartaPescata=mazzo.remove(mazzo.size()-1);
				ply.addPlayerCard(cartaPescata);
				return 0;
			}
			case "2" : {
				cartaPescata=scarti.remove(scarti.size()-1);
				ply.addPlayerCard(cartaPescata);
				return (playerAperture.get(ply).isEmpty() ? -1 : 0);
			}
			default : {
				throw new IOException("Metodo pesca: la stringa di comando non uguale a 1 o 2"); // lo mantengo per controllare l'uso
			}
		}
	}
	/**
	 * Check if an ArrayList of Card is a Tris or a Scala. If it's a valid combination return
	 * an Object type {@code Tris} or {@code Scala}. If it's not a valid combination launches
	 * an exception that the calling method will have to handle.
	 * 
	 * @param combToCheck The combination to check
	 * @return a Combination type {@code Tris} or {@code Scala} if combToCheck is a valid combination	    
	 * @throws IOException if the combination is not a valid combination
	 */
	public static Combination checkCombination(ArrayList<Card> combToCheck) throws IOException{
		try {
			Tris tris=new Tris(combToCheck, EnumSet.allOf(Jolly.class));
			return tris;
		} catch (Exception e1) {//inputOK=false;}
			try{
				Scala scala=new Scala(combToCheck,EnumSet.allOf(Jolly.class));
				return scala;
			} catch (Exception e2) {
				throw new IOException();
			}
		}
	}
	/*
	 * Calculate the value of all the cards discovered from the player
	 * 
	 * @param ply The player whose turn
	 * @return The value of all the cards discovered from the player
	 */
	private int valueComb (Player ply){
		return valueComb(playerAperture.get(ply));
	}
	/**
	 * Calculate the value of Combination contained in the ArrayList passed as a parameter
	 * 
	 * @param cardList An ArrayList of Combination which must calculate the value
	 * @return The value of Combination contained in cardList
	 */
	protected static int valueComb (ArrayList<Combination> cardList){
		int value=0;
		for (Combination comb : cardList){
			value+=comb.valueCombination();
		}
		return value;
	} 
	/*
	 * Check if into combination {@code comb} there's a jolly that taking place of {@code card}
	 * 
	 * @param comb The Combination to check
	 * @param card The card to be compared with jolly 
	 * @return {@code -1} If the jolly not taking place of {@code card}; otherwise
	 * a value >0 corresponding to index of card in the combination
	 */
	private int jollyAliasCard (Combination comb, Card card){
		Enum<?> rank=null;
		Enum<?> suit=null;
		int index=-1;
		ArrayList<Card> combToCheck=comb.getCombination();
		if (comb instanceof Tris){
			for (int i=0; i<combToCheck.size();i++){
				if (!(combToCheck.get(i).getRank() instanceof Jolly)) rank=combToCheck.get(i).getRank();
				else {
					suit=combToCheck.get(i).getSuit();
					index=i;
				}
			}
		} else {
			for (int i=0;i<combToCheck.size();i++){
				if (!(combToCheck.get(i).getSuit() instanceof Jolly)) suit=combToCheck.get(i).getSuit();
				else {
					rank=combToCheck.get(i).getRank();
					index=i;
				}
			}
		}
		return ((card.getRank()==rank && card.getSuit()==suit) ? index:-1);
	}
	/*
	 * Check if the card you want to discard attacks or can replace a jolly
	 * 
	 * @param ply The player whose turn
	 * @param index The card position between those of the player. (starting from 1, not from 0)
	 * @return {@code true} if the card no attacks
	 */
	private boolean cardNoAttach (Player ply,int index){
		Card card=ply.getPlayerCard().get(index-1);

		for (ArrayList<Combination> combArray: playerAperture.values()){
			for (Combination currentComb:combArray){
				
				if (jollyAliasCard(currentComb,card)>=0) return false; // se la carta può sostituire un jolly
				
				ArrayList<Card> combToCheck=new ArrayList<Card>();
				combToCheck.addAll(currentComb.getCombination());
				combToCheck.add(card); // verifico le possibili combinazioni aggiungendo una carta
				try {
					checkCombination(combToCheck);
					return false;
				} catch (Exception e){
				}
			}
		}
		return true;
	}
	/**
	 * Discard the card which are at position {@code i-1} among those of the player {@code ply},
	 * adding it to the discard pile. First check if this is the last card of the player, in this case returns true, then that  the card does not attacks 
	 * 
	 * @param ply The player whose turn
	 * @param index The position of the card to discard among those of the player (starting from 1, not from 0)
	 * @return true if there are not problems
	 */
	protected boolean scarta(Player ply,int index){
		return ((ply.getPlayerCard().size()==1 || cardNoAttach(ply,index)) ? scarti.add(ply.getPlayerCard().remove(index-1)):false);
	}
	/**
	 * Check if card is a jolly
	 * 
	 * @param card The card to check
	 * @return true if the card is a jolly
	 */
	protected static boolean checkJolly(Card card){
		return (card.getRank() instanceof Jolly ? true:(card.getSuit() instanceof Jolly ? true:false));
	}
	/**
	 * Check if the card at position {@code cmd} among those of player {@code ply} is a jolly
	 * 
	 * @param ply The player that have the card to be check
	 * @param cmd card index among those of player ply (index starting from 1, not from 0)
	 * @return true if the card is a jolly
	 */
	protected boolean checkJolly(Player ply,Integer cmd){
			return (checkJolly(ply.getPlayerCard().get(cmd-1)));
	}
	/**
	 * Change the suit or the rank of the card at the position {@code index-1} among those of the
	 * player {@code ply} based on the string {@code command}. Create a card whit rank(suit)
	 * jollyred or jollyblack, according the original and a suit(rank) equals to {@code command}.
	 * This special card allows the method isCombinable () of the class Tris or Scale to 
	 * establish whether a combination containing a jolly is valid. Also sets the correspondence
	 * between a jolly and the card indicated by the string {@code command}. 
	 * 
	 * @param ply The player 
	 * @param index
	 * @param command
	 * @return
	 */
	public Card setJollyToCard(Player ply,int index, String command){
		Card card;
		for (Suit suit: Suit.values()){
			if (suit.name().equals(command.toUpperCase())) {
				card=new Card (Enum.valueOf(Suit.class,command.toUpperCase()) , ply.getPlayerCard().get(index-1).getRank());
				return card;
			}
		}
		card=new Card (ply.getPlayerCard().get(index-1).getSuit() , Enum.valueOf(Rank.class,command.toUpperCase()));
		return card;
	}
	/**
	 * Change the jolly at position {@code nCard-1} into combination number {@code nComb-1} among those 
	 * of the Player {@code toPlayer} with the Card {@code card} of the Player {@code fromPly}
	 * 
	 * @param fromPly The player (usually the player whose turn) from which the card to replace the jolly 
	 * @param nCard The position of the card among those of {@code fromPlayer} from to substitute at the jolly
	 * @param toPlayer The player among whose the combinations there's the jolly be replace
	 * @param nComb The number of the combination from which to remove the jolly
	 * @return {@code true} If the change is successful
	 */
	protected boolean changeJolly(Player fromPly, int nCard, String toPlayer, int nComb){
		
		Combination selectedComb=playerAperture.get(getPlayer(toPlayer)).get(nComb-1);
		Card card=fromPly.getPlayerCard().remove(nCard-1);
		
		int index=jollyAliasCard(selectedComb,card);
		
		if (index<0) {	// la carta non corrisponde a un jolly
			fromPly.addPlayerCard(card);
			return false;
		}
		else{			// la carta corrisponde a un jolly
			Card jolly;
			Enum<?> suit=selectedComb.getCombination().get(index).getSuit();
			Enum<?> rank=selectedComb.getCombination().get(index).getRank();
			if (suit instanceof Jolly) jolly=new Card(suit,suit);
			else jolly=new Card(rank,rank);
			selectedComb.getCombination().set(index, card);
			fromPly.getPlayerCard().add(jolly);
		}	
		
		return true;
	}
	/**
	 * Add a combination to aperture
	 * 
	 * @param ply The player whose aperture add the combination 
	 * @param combArray The combination to add
	 */
	protected void addAperture(Player ply,Collection<Combination> combArray){
		playerAperture.get(ply).addAll(combArray);
		for (Combination currentComb:combArray){
			ply.removePlayerCard(currentComb.getCombination());
		}
	}
	/**
	 * Calculate the value and adds it to the other
	 * 
	 * @param ply The player whose turn
	 * @param combMap The HasMap containing the combinations for the aperture 
	 * @return 0 If this is not the first aperture; Otherwise a value equals
	 * to the sum of the combinations of combMap.
	 */
	protected int registraApertura(Player ply,Map<ArrayList<Integer>,Combination> combMap){
		ArrayList<Card> cardList=new ArrayList<Card>();
		int value=0;
		boolean nuovaApertura=playerAperture.get(ply).isEmpty();
		playerAperture.get(ply).addAll(combMap.values());
		if (nuovaApertura){
			value=valueComb(ply);
			if (value<40) {
				playerAperture.get(ply).clear();
				return value;
			}
		}
		
		for (Combination comb:combMap.values()){
			for (Card card:comb.getCombination()){
				cardList.add(card);
			}
		}
		for (Iterator<Card> plyCardIt=ply.getPlayerCard().iterator();plyCardIt.hasNext();){
			Card itCard=plyCardIt.next();
			if (cardList.contains(itCard)){
				cardList.remove(itCard);
				plyCardIt.remove();
			} else if (itCard.getSuit() instanceof Jolly) {
				for (Card iCard:cardList){
					if (iCard.getRank()==itCard.getRank() || iCard.getSuit()==itCard.getSuit()){
						cardList.remove(iCard);
						plyCardIt.remove();
						break;
					}
				}
			}
		}
		return value;
	}
	/**
	 * Manages the player aperture. In the case of the first opening is called multiple times for each
	 * selection of a combination of up to reach 40 points minimum for opening
	 * 
	 * @param ply The player whose turn
	 * @param combMap The HashMap of the combinations for the aperture
	 * @param cmdApri The list of last cards selection
	 * @return -1 if you have selected cards already previously selected
	 * 			0 if the player has already opened and so there is no need to value combinations
	 * 			value>0 The total value of the selected combination	
	 * @throws IOException relaunches the exception thrown by checkCombination to let know that
	 * the selection in cmdApri is not a valid combination
	 */
	public int apri(Player ply, Map<ArrayList<Integer>,Combination> combMap, ArrayList<Integer> cmdApri, Map<Integer,Card> cardForJolly) throws IOException{

		if (!combMap.isEmpty()){	// verifico la doppia selezione confrontando le key di combMap con cmdApri
			for (ArrayList<Integer> currentKey:combMap.keySet()){
				if (!Collections.disjoint(cmdApri, currentKey)){
					return -1;
				}
			}
		}
		
		// ora sostituisco i jolly con le carte di cardForJolly, poi passo a checkCombination: 
		
		ArrayList<Card> selectedCard=new ArrayList<Card>(cmdApri.size()); // inizializzo una collection per le carte selezionate
		for (Integer n:cmdApri){		// copio le carte in selectedCard
			if (!(ply.getPlayerCard().get(n-1).getRank() instanceof Jolly)){
				selectedCard.add(ply.getPlayerCard().get(n-1));
			} else selectedCard.add(cardForJolly.get(n));
		}
		
		combMap.put(cmdApri,Scala40.checkCombination(selectedCard));	// trasformo le selectedCard in combinazioni e le passo in combMap
																		// se la combinazione non è valida il metodo checkCombination lancia una IOException
																		// che verrà raccolto dallo strato di controllo
		return registraApertura(ply,combMap);
	}
	
	public boolean attaccaOvunque (Player currentPlayer, Card currentCard, Card cardForJolly){
		for (Player ply:players){
			for (Combination comb:playerAperture.get(ply)){
				if (attacca (currentPlayer,
						 currentPlayer.getPlayerCard().lastIndexOf(currentCard)+1,
						 ply.getName(),
						 playerAperture.get(ply).lastIndexOf(comb)+1,
						 cardForJolly)) return true;
			}
		}
		return false;
	}
	/**
	 * Manages the procedure for attachment of a card verifying the new combination created by the card and the combination selected.
	 * 
	 * @param ply The player whose turn
	 * @param nCard The position of the selected card among the cards of {@ code ply} (starting from 1, not from 0)
	 * @param toPlayer The player who has the combination that you want to attack paper
	 * @param nComb The number of combination (starting from 1, not from 0)
	 * @return true If the procedure was successful
	 */
	public boolean attacca(Player ply,int nCard,String toPlayer,int nComb,Card cardForJolly){
		ArrayList<Card> selectedComb=new ArrayList<Card>();
		selectedComb.addAll(playerAperture.get(getPlayer(toPlayer)).get(nComb-1).getCombination());
		if(cardForJolly!=null){
			selectedComb.add(cardForJolly);
		} else {
			Card card=ply.getPlayerCard().get(nCard-1);
			selectedComb.add(card);
		}
		try {
			Combination newComb=checkCombination(selectedComb);
			playerAperture.get(getPlayer(toPlayer)).set(nComb-1, newComb);
			ply.getPlayerCard().remove(nCard-1);
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Cancel the draw from the discard pile and draws from the deck
	 * 
	 * @param ply The player whose turn
	 * @throws IOException 
	 */
	public void annullaPescaDaScarti(Player ply) throws IOException{
		int index=ply.getPlayerCard().indexOf(cartaPescata);
		scarti.add(ply.getPlayerCard().remove(index));
		pesca(ply,"1");		
	}	
	/*
	 ******************************************* metodi Getter e Setter ******************************************
	 */

	public ArrayList<Card> getMazzo() {
		return mazzo;
	}

	public void setMazzo(ArrayList<Card> mazzo) {
		this.mazzo = mazzo;
	}


	public ArrayList<Card> getScarti() {
		return scarti;
	}
	public Card getLastScarti(){
		if (scarti.size()>0){
			return scarti.get(scarti.size()-1);
		} else return null;
	}

	public void setScarti(ArrayList<Card> scarti) {
		this.scarti = scarti;
	}

	public int getnPly() {
		return nPly;
	}
	public ArrayList<Player> getPlayers(){
		return players;
	}
	public Player getPlayer(int i){
		return players.get(i);
	}
	public Player getPlayer(String player){
		for (Player ply:players){
			if (ply.getName().equals(player)) return ply;
		}
		return null;
	}
	public ArrayList<Combination> getPlayerAperture(Player ply){
		return playerAperture.get(ply);
	}
	public Map<Player, ArrayList<Combination>> getPlayerAperture(){
		return playerAperture;
	}
	public Card getCartaPescata(){
		return cartaPescata;
	}
	public Card getCard(String[] StringCard){
		Card card=new Card(Enum.valueOf(Suit.class, StringCard[1]),Enum.valueOf(Rank.class, StringCard[0]));
		return card;
	}
}
