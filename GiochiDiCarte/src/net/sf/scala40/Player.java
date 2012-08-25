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
import java.util.Collection;
/**
 * The representation of the player. Its features include a name and a collection of cards of type Card
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (per-alpha)
 */
public class Player {
	private String name;
	private ArrayList<Card> playerCard;
	/**
	 * The parameterless Constructor. It sets to <b>null</b> the player name and the card collection.
	 */
	public Player (){
		this.setName(null);
		this.setPlayerCard(null);
	}
	public Player (String name){
		this.setName(name);
		this.setPlayerCard(null);
	}
	public Player (String name,ArrayList<Card> playerCard){
		this.setName(name);
		this.setPlayerCard(playerCard);
	}
	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return The playerCard
	 */
	public ArrayList<Card> getPlayerCard() {
		return playerCard;
	}
	/**
	 * @param playerCard The playerCard to set
	 */
	public void setPlayerCard(ArrayList<Card> playerCard) {
		this.playerCard = playerCard;
	}
	public void addPlayerCard(Card card){
		playerCard.add(card);
	}
	public boolean removePlayerCard(Collection<?> c){
		return this.playerCard.removeAll(c);
	}
}