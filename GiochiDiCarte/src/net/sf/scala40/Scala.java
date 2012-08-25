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
import java.util.Iterator;

/**
 * A cards combination that represent the concept of straight.
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 *
 */
public class Scala extends Combination{
	private static final int MIN_LENGHT_SCALA=3;
	private static final int MAX_LENGHT_SCALA=14;

	public Scala(ArrayList<Card> scala) throws IOException {
		super(scala, MIN_LENGHT_SCALA,MAX_LENGHT_SCALA);
	}
	public Scala(ArrayList<Card> scala,EnumSet<?> jolly) throws IOException {
		super(scala, MIN_LENGHT_SCALA,MAX_LENGHT_SCALA,jolly);
	}
	/**
	 * Verify that an ArrayList is a valid Combination of type Scala
	 * 
	 * @param scala The ArrayList that must be check to be a valid Scala
	 */
	@Override
	public boolean isCombinable(ArrayList<Card> scala){
		if (!super.isCombinable(scala)) return false;
		Collections.sort(scala, Card.FIRST_RANK_ORDER);
		if (scala.get(0).getRank().ordinal()==0){
			if(scala.get(scala.size()-1).getRank().ordinal()==12){
				scala.add(scala.remove(0));
			}
		}
		Enum<?> scalaSuit=null;
		Iterator<Card> it=scala.iterator();
		do {
			scalaSuit=it.next().getSuit();
			if (jolly.contains(scalaSuit)) scalaSuit=null;
		} while (scalaSuit==null);
		for (int i=0; i<scala.size();i++){
			if (scala.get(i).getSuit()!=scalaSuit) { 
				if (!jolly.contains(scala.get(i).getSuit())){
					return false;
				}
			}
			if (i>0){
				int c1=scala.get(i-1).getRank().ordinal();
				int c2=scala.get(i).getRank().ordinal();
				if (c1!=(c2-1)){
					if (!(c1==12 && c2==0)) return false;
				}
			}	
		}
		return true;
	}
	/**
	 * To calculate the combination value
	 */
	@Override
	public int valueCombination(){
		int value=0;
		for (int i=0; i<this.getCombination().size();i++){
			if (!(jolly.contains(this.getSuitOrdinal(i)))){
				int from=(this.getRankOrdinal(i)+1-i);
				int to=from+this.getCombination().size();
				for (int n=from;n<to;n++){
					value+=(n<11 ? n : (n==14 ? 11 : 10));
				}
				return value;
			}
		}
		return value;
	}
}
