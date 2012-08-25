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
/**
 * Start the program in console mode.
 * You can not play against the computer, but only with human players.
 * 
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class Scala40Console {

	public static void main(String[] args) {
 		String[] players=args;
		Scala40 partita;
		try {
			partita = new Scala40(CtrlScala40.initPlayer(players));
			boolean vittoria=false;
			do{
				vittoria=CtrlScala40.goGame(partita);
			} while (!vittoria);
			} catch (Exception e) {
				e.printStackTrace();
		}
	}
}
