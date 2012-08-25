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
import java.util.Collections;
import java.util.EnumSet;

/**
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 *
 */
public class Tris extends Combination{
	
	private static final int MIN_LENGHT_TRIS=3;
	private static final int MAX_LENGHT_TRIS=4;

	public Tris(ArrayList<Card> tris) throws IOException {
		super(tris,MIN_LENGHT_TRIS,MAX_LENGHT_TRIS);
	}
	public Tris(ArrayList<Card> tris, EnumSet<?> jolly) throws IOException{
		super(tris,MIN_LENGHT_TRIS,MAX_LENGHT_TRIS,jolly);
	}
	/**
	 * Controlla se una lista di carte è un tris e lo ordina.
	 * @param un ArrayList che si deve controllare se è un tris
	 * @return true se la combinazione è valida
	 */
	@Override
	public boolean isCombinable(ArrayList<Card> tris){
		if (!super.isCombinable(tris)) return false;
		Enum<?> suit=null;
		Enum<?> rank=null;
		Collections.sort(tris, Card.FIRST_SUIT_ORDER);
		for (Card card:tris){
			if (card.getSuit()==suit){
				return false;
			} else {
				if (jolly.contains(card.getRank())){
					suit=card.getSuit();
				} else {
					if (rank!=null && card.getRank()!=rank) {
						return false;
					} else{
						suit=card.getSuit();
						rank=card.getRank();
					}
				}
			}
		}
		return true;
	}
	@Override
	public int valueCombination(){
		for (Card currentCard:this.getCombination()){
			if (!(jolly.contains(currentCard.getRank()))){
				int rank=currentCard.getRank().ordinal()+1;
				return this.getCombination().size()*(rank==1 ? 11 : (rank<11 ? rank : 10));
			}
		}
		return 0;
	}
}