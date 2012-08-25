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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * costituisce lo strato di controllo del gioco Scala Quaranta verso la console
 * 
 * @author Roberto Zaniol
 * 
 *
 */
public class CtrlScala40 {
	public CtrlScala40(){
	}
	public static String[] initPlayer(String... players){
		if (players.length<2){
			String[] pcPlayers=new String[2];
			System.out.println("Il gioco necessita di almeno due giocatori.\n" +
					"Il programma aggiungerà i giocatori mancanti.");
			pcPlayers[0]="Player1";
			if (players.length==0) {
				pcPlayers[1]="Player2";
			}
			else {
				pcPlayers[1]=players[0];
			}
			return pcPlayers;
		}
		else return players;
	}
	public static void displayCard(Card card){
		System.out.print(card.getRank().toString()+" di "+card.getSuit().toString());
	}	
	/**
	 * Mostra sulla console il mazzo da cui estrarre le carte
	 */
	public static void displayCardList(List<Card> cardList){
		int i=1;
		for (Card card: cardList){
			System.out.print("("+(i++)+"-");
			displayCard(card);
			System.out.print(")"+(i==9 ? "\n":""));
		}
		System.out.println();
	}
	public static void displayCombination(List<Card> cardList){
		for (Card card:cardList){
			displayCard(card);
			System.out.print(" ");
		}
	}
	public static void displayPlayerAperture(ArrayList<Combination> playerAperture){
		int i=1;
		for (Combination comb:playerAperture){
			System.out.print("** "+(i++)+")");
			displayCombination(comb.getCombination());

		}
		System.out.println("");
	}
	public static void displayPlayerName(Player ply){
		System.out.println("**************************************  Giocatore: "+ply.getName()+"  *****************************************");
	}
	public static void displayPlayerCard (Player ply){
		Collections.sort(ply.getPlayerCard());
		//Collections.sort(ply.getPlayerCard(), Card.FIRST_RANK_ORDER);
		displayCardList(ply.getPlayerCard());		
	}
	public static void displayMazzi(ArrayList<Card> mazzo){
		StringBuffer s=new StringBuffer();
		s.append("Mazzo:\n");
		for (Card card:mazzo){
			s.append(card.getRank()+" di "+card.getSuit()+"\n");
		}
		System.out.print(s);
	}
	public static void displayHand(ArrayList<Player> players, Player ply,Map<Player, ArrayList<Combination>> playersAperture,Card discartedCard, String question){
		for (Player player:players){
			if (player!=ply) {
				displayPlayerName(player);
				displayPlayerAperture(playersAperture.get(player));
			}
		}
		displayPlayerName(ply);
		displayPlayerAperture(playersAperture.get(ply));
		displayPlayerCard(ply);
		System.out.print("La carta sul mazzo degli scarti è:");
		if (discartedCard!=null) {
			displayCard(discartedCard);
		}
		System.out.println("\n"+question);
	}
	public static boolean goGame(Scala40 partita){
		for(Player ply:partita.getPlayers()){	//********************** gestione mani ************************
			
			partita.checkMazzo();	// se il mazzo è finito usa il mazzo degli scarti dopo averlo mischiato
			
			displayHand(partita.getPlayers(),ply,partita.getPlayerAperture(), partita.getLastScarti(),"Scegli la carta:\n"+
					"1 per la carta dal mazzo\n"+
					"2 per la carta degli scarti"); // chiede di pescare dal mazzo o dagli scarti
			
			String command=null;
			InputStreamReader isr=new InputStreamReader(System.in);
			BufferedReader in=new BufferedReader(isr);
			
			boolean fromScarti;
			
			//************************************ gestione della pescata **************************************
			pesca:
				do {
					fromScarti=false;
					try {
						command=in.readLine();
						if (!(command.equals("1") || command.equals("2"))) throw new Exception();
						
						switch (partita.pesca(ply, command)) {	//-Chiamo il metodo pesca di Scala40 che mi restituisce -1,0,1
						
						case -1 : {
							fromScarti=true;
							break;
						}
						case 0 : {
							System.out.println(ply.getName()+", hai scelto la carta dal mazzo coperto");
							break;
						}
						case 1 : {
							System.out.println(ply.getName()+", hai scelto la carta dal mazzo degli scarti");
							break;
						}
						}
					} catch (Exception e) {
						// e.printStackTrace();
						System.out.println("Dati inseriti errati.\n"+
							"Digitare 1 per la carta dal mazzo o 2 per la carta dagli scarti");
						continue pesca;
					}
					
					//************************************ gestione scarti e aperture ***************************************					
					boolean scartato=false;
					choose:		
						do{ 
							int cmd=-1;
							if (!fromScarti){ // altrimenti dò per scontato che il player vuole aprire
								displayHand(partita.getPlayers(),ply,partita.getPlayerAperture(),partita.getLastScarti(),"Scegli la carta da scartare inserisci un numero da 1 a 14) oppure\n"+
													"0 per aprire o attaccare una o più carte"); // propone al player di scartare o aprire
								try {
									command=in.readLine();
									cmd=Integer.parseInt(command);
									if (cmd<0 || cmd>14) throw new Exception();
								} catch (Exception e) {
									// e.printStackTrace();
									System.out.println("Dati inseriti errati. Riprova");
									continue choose;
								}
							} else {
								displayHand(partita.getPlayers(),ply,partita.getPlayerAperture(),partita.getLastScarti(),
										"Hai pescato dagli scarti ma non hai ancora aperto. Ora devi aprire o digita 0 per annullare");
								cmd=0;
							}
							
							try { // questo non gestisce gli errori di input!
								if (cmd==0){	//**************************************** gestione dell'apertura ************************************
									if (!fromScarti && !partita.getPlayerAperture(ply).isEmpty()) {
										System.out.println(ply.getName() + ", hai deciso di attaccare o aggiungere delle combinazioni alla tua apertura");
										System.out.println("Digita 1 per attaccare\n2 per aggiungere combinazioni alla tua apertura"+
												"\n3 per cambiare un jolly con una carta\n0 per scartare");
										
										attach:	//****************************************** gestione attacca ****************************************
											do { 
												try {	// gestisco gli errori di input
													command=in.readLine();
													cmd=Integer.parseInt(command);
													if (cmd<0 || cmd>3) throw new Exception();
												} catch (Exception e) {
													// e.printStackTrace();
													System.out.println("Dati inseriti errati. Riprova");
													continue attach;
												}
												// sostituire con switch ????
												if (cmd==0) continue choose; //torno alla richiesta di scartare o aprire
												
												if (cmd==1){
													System.out.println("digitare il numero della carta da attaccare seguito dal nome del player" + 
																					"e dal numero di combinazione a cui attaccare, oppure 0 scartare");
													String[] cmdAttacca;
													try {
														command=in.readLine();
														cmdAttacca=command.split(" ");
														
														if (cmdAttacca[0].equals("0")) continue choose;
														
														int nCard=Integer.parseInt(cmdAttacca[0]);
														String toPlayer=cmdAttacca[1];
														int nComb=Integer.parseInt(cmdAttacca[2]);
														Card cardForJolly=null;
														if (nCard<0 || nCard>14) throw new Exception();
														if (partita.checkJolly(ply, nCard)){
															System.out.println("Digita il valore o il seme della carta che il jolly "+
																	" deve sostituire (es. RE oppure QUADRI, ma non RE QUADRI)");
															input :
																do {
																	try { // questo gestisce errori di input
																		command=in.readLine();
																		cardForJolly=partita.setJollyToCard(ply,nCard,command); // aggiungo a cardForJolly le carte che i jolly sostituiranno
																		break input;
																	} catch (IOException e) {
																		System.out.println("La combinazione digitata è errata. Riprova");
																	}
																} while (true);
														}
														if (partita.attacca(ply,nCard,toPlayer,nComb,cardForJolly)) { // richiamo il metodo di Scala40 attacca(...) che ritorna true/false
															System.out.println("La carta è stata attaccata");
															continue choose;
														}
														else {
															System.out.println("Non è possibile attaccare la carta selezionata alla combinazione scelta");
															System.out.println("Digita 1 per attaccare\n2 per aggiungere combinazioni alla tua apertura\n0 per scartare");
														}
														
													} catch (Exception e) {
														e.printStackTrace();
														System.out.println("Dati inseriti errati. Riprova");
														System.out.println("Digita 1 per attaccare\n2 per aggiungere combinazioni alla tua apertura"+
																"\n3 per cambiare un jolly con una carta\n0 per scartare");
													}
												}
												if (cmd==3){
													System.out.println("digitare il numero della carta da cambiare, il nome del player tra le cui carte\n"+
																	"c'è il jolly e il numero di combinazione in cui si trova il jolly, oppure\n"+
																	"0 pre scartare");
													String[] cmdChangeJolly;
													try {
														command=in.readLine();
														cmdChangeJolly=command.split(" ");
														
														if (cmdChangeJolly[0].equals("0")) continue choose;
														
														int nCard=Integer.parseInt(cmdChangeJolly[0]);
														String toPlayer=cmdChangeJolly[1];
														int nComb=Integer.parseInt(cmdChangeJolly[2]);
														
														if (nCard<0 || nCard>14) throw new Exception();
														
														if (partita.changeJolly(ply,nCard,toPlayer,nComb)) { // richiamo il metodo di Scala40 changeJolly(...) che ritorna true/false
															System.out.println("Il jolly è stato cambiato");
															continue choose;
														}
														else {
															System.out.println("Non è possibile attaccare la carta selezionata alla combinazione scelta");
															System.out.println("Digita 1 per attaccare\n2 per aggiungere combinazioni alla tua apertura\n0 per scartare");
														}
														
													} catch (Exception e) {
														e.printStackTrace();
														System.out.println("Dati inseriti errati. Riprova");
														System.out.println("Digita 1 per attaccare\n2 per aggiungere combinazioni alla tua apertura"+
																"\n3 per cambiare un jolly con una carta\n0 per scartare");
													}
												}
												else break attach; //allora command==2 voglio aggiungere combinazioni all'apertura, vado al ciclo apertura
											
											} while (true);
									}
									
									//ArrayList<Combination> combArray=new ArrayList<Combination>(5);//inizializzo una collection di combinazioni a cui aggiungere le selectedCard
									
									Map<ArrayList<Integer>,Combination> combMap=new HashMap<ArrayList<Integer>,Combination>(3);
									Map<Integer,Card> cardForJolly=new HashMap<Integer,Card>(3);
									open:	//**************************************** gestione delle combinazioni *****************************************
										do{	
											System.out.println("Seleziona una combinazione di carte o digita 0 per scartare");
											try { // questo gestisce errori di input
												command=in.readLine();
											} catch (IOException e) {
												System.out.println("La combinazione digitata è errata. Riprova");
												continue open;
											}  
											
											try { 	// gestisce l'errore lanciato da "checkCombination" richiamato in "apri"
													// per dire che la combinazione digitata non è una combinazione valida
												
												select:
													if (!command.equals("0")) {
														ArrayList<Integer> cmdApri=new ArrayList<Integer>();
														try {	// Gestisce errori di trasformazione dell'input
															for (String c : command.split(" ")){
																Integer i=Integer.valueOf(c);
																if (i<1 || i > 14) throw new Exception();
																cmdApri.add(i);
															}
														} catch (Exception e) {
															System.out.println("Dati inseriti errati");
															continue open;
														}
														// controllo se sono stati selezionati dei jolly
														
														int nJolly=1;
														for (Integer index:cmdApri){ 
															if (partita.checkJolly(ply, index)){
																System.out.println("Digita il valore o il seme della carta che il jolly n°"+(nJolly++)+
																		" deve sostituire (es. RE oppure QUADRI, ma non RE QUADRI)");
																input :
																	do {
																		try { // questo gestisce errori di input
																			command=in.readLine();
																			cardForJolly.put(index,partita.setJollyToCard(ply,index,command)); // aggiungo a cardForJolly le carte che i jolly sostituiranno
																			break input;
																		} catch (IOException e) {
																			System.out.println("La combinazione digitata è errata. Riprova");
																		}
																	} while (true);
															}
														}
														
														//int value=partita.apri(ply,combArray,cmdApri,cardForJolly); //richiamo il metodo apri
														int value=partita.apri(ply,combMap,cmdApri,cardForJolly); //richiamo il metodo apri
														if (value==-1){
															combMap.clear();
															break select;
														} else if (value==0){
															break open;
														} else if (value>=40){
															System.out.println("Complimenti hai aperto con "+value+" punti");
															fromScarti=false;
															break open;
														} else {
															System.out.println("Hai totalizzato "+value+" punti."+
																	"Non sono sufficienti per aprire, aggiungi un'altra combinazione");
														}
													} else { // se il player ha digitato 0 per scartare
														if (fromScarti) {	// se aveva pescato senza poterlo
															partita.annullaPescaDaScarti(ply);	// annullo la pescata (e in automatico la ripeto dal mazzo)
															fromScarti=false; 
														}
														break open;	// torno al ciclo choose (potrebbe funzionare 'continue choose;'?
													}
											} catch (IOException e) {
												System.out.println("La combinazione digitata non è valida. Riprova");
											} catch (Exception e) {
												e.printStackTrace();
												System.out.println("Errore generico lanciato nel ciclo select");
												//throw new Exception();
											}
										} while (true);
								}
								else if (cmd>0 && cmd<15){ 	// se ha scelto la carta da scartare
									if(scartato=partita.scarta(ply,cmd)){
										System.out.print(ply.getName()+", hai deciso di scartare la carta: ");
										displayCard(partita.getLastScarti());
										System.out.println("");
									
										if (ply.getPlayerCard().size()==0) {  /// VERIFICA LA VITTORIA  ///
											System.out.println(ply.getName().toUpperCase()+" HAI VINTO!!!");
											return true;
										}
									
										break pesca;
									} else {
										System.out.println("La carta selezionata attacca. Scegliere un altra carta");
									}
									////////////// GESTIONE DATI ERRATI  //////////////	
								} 	else { throw new IOException();}
							} catch (Exception e) {
								//e.printStackTrace();
								System.out.println("Dati inseriti errati.\n"+
										"Scegli la carta da scartare inserendo un numero da 1 a 14 oppure\n"+
										"0 per aprire attaccare una o più carte");
								break choose;
							}
							
						} while (!scartato); //******* ciclo choose ******
				} while (true); //****** ciclo pesca *******
		}
		return false;
	}
}
