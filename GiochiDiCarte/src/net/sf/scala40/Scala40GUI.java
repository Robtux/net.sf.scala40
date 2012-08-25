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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;

import net.sf.scala40.Scala40.Jolly;

/**
 * Gestisce il gioco in modalità grafica
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 pre-alpha
 */
public class Scala40GUI extends JFrame {
	
	private static final long serialVersionUID = -7101303270880053808L;
	
	/* 
	 * -------------------------------  Variabili per le definizioni dei giocatori.  ----------------------------------
	 */
	/**
	 * Costante che dichiara il numero massimo di giocatori ammesso (6) 
	 */
	private static final int MAX_NUM_PLAYER=6;
	/**
	 * HashMap che contiene per ogni "chiave" giocatore il rispettivo oggetto
	 * di tipo PlayerInfo che per le memorizzazioni, gestioni e visualizzazioni relative.
	 */
	private Map<Player,PlayerInfo> playerInfo=new HashMap<Player,PlayerInfo>(MAX_NUM_PLAYER);
	/**
	 * HashMap per memorizzare se un giocatore è umano o gestito dal pc.
	 */
	private Map<Player,Boolean> playerPcMap=new HashMap<Player,Boolean>(MAX_NUM_PLAYER);
	/**
	 * Percorso per le immagini delle carte
	 */
	private String pathCards="C:\\Users\\zaniol\\Workspace\\GiochiDiCarte\\img\\card\\";
	/*
	 * ---------------------------------------  Container e Layout vari  ----------------------------------------------
	 */
	private JPanel contentPane;
	private JPanel tavolo;
	private JPanel southPanel;
	private FlowLayout fl_southPanel;
	private JPanel commandPanel;
	private FlowLayout ly_commandPanel;
	private JLabel message;
	/*
	 * ---------------------------  Elementi di Swing da visualizzare per il player di turno  --------------------------
	 */
	private JLayeredPane handPane;
	private JLabel[] imgPlayerCard;
	private JLabel lblPly;
	private JLabel imgMazzo;
	private JLabel lblMazzo;
	private JLabel imgScarti;
	private JLabel lblScarti;
	private JButton btnAnnulla;
	private JButton btnScarta;
	private JButton btnApri;
	private JButton btnAddComb;
	private JButton btnAttacca;
	private JButton btnChangeJolly;
	/*
	 * --------------------------------  Variabili di Scala40 fisse per ogni partita  -----------------------------------
	 */
	private Scala40 partita;
	private ArrayList<Player> players=new ArrayList<Player>(MAX_NUM_PLAYER);
	/*
	 * ------------------------------------------  Variabili di gioco  --------------------------------------------------
	 */
	private boolean fromScarti;
	private int faseDiGioco=0;
	private int scarta;
	private Map<Integer,Boolean> selectedCard=new HashMap<Integer,Boolean>();
	private Map<ArrayList<Integer>,Combination> combMap=new HashMap<ArrayList<Integer>,Combination>(3);
	private Map<Integer,Card> cardForJolly=new HashMap<Integer,Card>(3);
	private int selectedComb=0;
	private Player selectedCombPlayer;
	
	/**
	 * Classe interna per le gestioni dedicate di ogni giocatore
	 * 
	 * @author Roberto Zaniol
	 * 
	 */
	protected class PlayerInfo {
		/**
		 *Per ogni {@code player} viene creato un oggetto di tipo PlayerInfo 
		 */
		private Player player;
		/** 
		 * Per la visualizzazione del nome e delle aperture del gicatore 
		 */
		protected JPanel plyInfoPanel;
		/**
		 * Per visualizzare il nome del giocatore
		 */
		protected JLabel lblPlayer;
		/**
		 * Il contenitore per le immagini delle combinazioni di carte per le aperture
		 */
		protected JPanel plyAperturePanel;
		/**
		 * Il contenitore per la singola combinazione di carte di apertura
		 */
		protected ArrayList<JLayeredPane> combPanel;
		/**
		 * Un HashMap che ha come chiave i contenitori al cui interno troveranno posto
		 * le JLabel che conterranno a loro volta le immagini delle carte delle
		 * combinazioni di apertura. Valore dell'HashMap sono gli ArrayList contenenti 
		 * le JLabel e costituiti in base alle combinazioni. 
		 */
		protected Map<JLayeredPane,ArrayList<JLabel>> imgCombCardList;
		/**
		 * Un HashMap per i Mouse Listener che consentono di selezionare i blocchi di
		 * combinazioni di carte delle aperture
		 */
		protected Map<JLayeredPane,MouseListener> combListenerList;
		/**
		 * Costruttore della classe PlayerInfo
		 * @param player di tipo {@code Player}
		 */
		protected PlayerInfo (Player player){
			this.player=player;
			imgCombCardList=new HashMap<JLayeredPane,ArrayList<JLabel>>(5);
			selectedCombPlayer=new Player();
			
			plyInfoPanel = new JPanel();
			plyInfoPanel.setBackground(new Color(0, 128, 0));
			tavolo.add(plyInfoPanel);
			plyInfoPanel.setLayout(new BoxLayout(plyInfoPanel, BoxLayout.Y_AXIS));
			
			lblPlayer = new JLabel(player.getName());
			lblPlayer.setLabelFor(plyInfoPanel);
			lblPlayer.setForeground(new Color(255, 255, 255));
			plyInfoPanel.add(lblPlayer);
			
			plyAperturePanel = new JPanel();
			plyAperturePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			plyInfoPanel.add(plyAperturePanel);
			plyAperturePanel.setBackground(new Color(0, 128, 0));
			plyAperturePanel.setLayout(new BoxLayout(plyAperturePanel, BoxLayout.X_AXIS));
			combPanel=new ArrayList<JLayeredPane>();
			combListenerList=new HashMap<JLayeredPane,MouseListener>();
	
		}
		/**
		 * Visualizza l'immagine della singola combinazione di carte {@code combination} nel
		 * contenitore {@code comb}. 		
		 * @param combination di tipo {@code ArrayList<Card>}: la combinazione che si deve visualizzare
		 * @param comb di tipo {@code JLayeredPane}: il contenitore per le JLabel che conterranno le immagini delle carte
		 */
		protected void displayImageOfCardCombination(ArrayList<Card> combination, JLayeredPane comb){
			comb.removeAll(); 												// reinizializza.
			for (int z=combination.size()-1;z>=0;z--){						// Partendo dall'ultima carta in combination
				Card card=combination.get(z);								// utilizza le JLabel per visualizzare
				JLabel imgCard=new JLabel("");								// nel contenitore comb le immagini della
				ArrayList<JLabel> imgCombCard=imgCombCardList.get(comb);	// combinazione
				imgCombCard.add(imgCard);
				String imgPath=getPathCard(card);
				imgCard.setIcon(new ImageIcon(imgPath));
				imgCard.setBounds(z*20, 5, 71, 96);
				comb.add(imgCard);
			}
		}
		/**
		 * Prepara i contenitore per la visualizzazione della singola combinazione. Per la visualizzazione
		 * vera e propria delle carte utilizza il metodo {@code displayImageOfCardCombination}
		 * @param combination di tipo {@code ArrayList<Card>}: la combinazione da visualizzare
		 */
		protected void displayOneCombination (ArrayList<Card> combination){
			JLayeredPane comb=new JLayeredPane();
			comb.setLayout(null);
			comb.setBackground(new Color(0, 128, 0));
			combPanel.add(comb);
			plyAperturePanel.add(comb);
			imgCombCardList.put(comb, new ArrayList<JLabel>());
			displayImageOfCardCombination(combination, comb);			
		}
		/**
		 * Per ogni combinazione contenuta in {@code playerAperture} (solo il singolo player) richiama
		 * il metodo {@code displayOneCombination} per visualizzarle.
		 * @param playerAperture di tipo {@code ArrayList<Combination>}: le combinazioni di aperture
		 * relative ad un singolo player
		 */
		protected void displayApertureCombination (ArrayList<Combination> playerAperture) {
			if (playerAperture!=null){
				int nComb=playerAperture.size();
				for (int i=combPanel.size();i<nComb;i++){
					displayOneCombination (playerAperture.get(i).getCombination());
				}
			}
		}
		/**
		 * Reinizializza (deseleziona) il bordo del contenitore della combinazione selezionata
		 */
		public void setNullBorderCombOfSelectedComb(){
			playerInfo.get(selectedCombPlayer).combPanel.get(selectedComb-1).setBorder(null);
			selectedComb=0;
			selectedCombPlayer=null;
		}
		/**
		 * Attiva i mouse listener sui contenitori delle combinazioni permettendone la selezione
		 * (colora il bordo della combinazione selezionata)
		 */
		protected void activeCombListener (){
			final Player ply=player;
			for (JLayeredPane currentCombPanel:combPanel){
				final JLayeredPane current=currentCombPanel;
				MouseListener combListener=new MouseListener(){
					@Override
					public void mouseClicked(MouseEvent arg0) {
						if (selectedComb!=0){
							setNullBorderCombOfSelectedComb();
						}
						selectedComb=combPanel.indexOf(current)+1;
						selectedCombPlayer=ply;
						current.setBorder(new LineBorder(new Color(255, 255, 255)));
					}
					@Override
					public void mouseEntered(MouseEvent arg0) {	
					}
					@Override
					public void mouseExited(MouseEvent arg0) {	
					}
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					@Override
					public void mouseReleased(MouseEvent arg0) {	
					}
				};
				combListenerList.put(current,combListener);
				current.addMouseListener(combListener);
			}
		}
		
		protected void deactiveCombListener(){
			if (selectedComb!=0){
				setNullBorderCombOfSelectedComb();
			}
			for (JLayeredPane currentCombPanel:combPanel){
				currentCombPanel.removeMouseListener(combListenerList.get(currentCombPanel));
			}
		}
	}
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scala40GUI frame = new Scala40GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Costruttore.
	 */
	public Scala40GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 800, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnPartita = new JMenu("Partita");
		menuBar.add(mnPartita);
		
		JMenuItem mnItemNuovaPartita = new JMenuItem("Nuova partita");
		mnItemNuovaPartita.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NuovaPartita dlgNuovaPartita=new NuovaPartita();
				dlgNuovaPartita.setModal(true);
				dlgNuovaPartita.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				dlgNuovaPartita.setVisible(true);
				Map<String,Boolean> playerMap=dlgNuovaPartita.getPlayer();
				if(playerMap!=null){
					checkNumberPlayer(playerMap);
					dlgNuovaPartita.dispose();
					giocaNuovaPartita(playerMap);
				}
			}
		});
		mnPartita.add(mnItemNuovaPartita);
		
		JMenuItem mnItemOpzioni = new JMenuItem("Opzioni");
		mnPartita.add(mnItemOpzioni);
		
		JMenuItem mnItemExit = new JMenuItem("Esci");
		mnPartita.add(mnItemExit);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 135, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		message=new JLabel("Benvenuti nel gioco Scala Quaranta");
		message.setForeground(new Color(255, 255, 255));
		message.setHorizontalAlignment(SwingConstants.CENTER);
		message.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(message, BorderLayout.NORTH);
		
		southPanel = new JPanel();
		southPanel.setBackground(new Color(135, 0, 0));
		fl_southPanel = (FlowLayout) southPanel.getLayout();
		fl_southPanel.setAlignment(FlowLayout.CENTER);
		southPanel.setPreferredSize(new Dimension (800,135));
		contentPane.add(southPanel, BorderLayout.SOUTH);
		
		handPane = new JLayeredPane();
		southPanel.add(handPane);
		handPane.setPreferredSize(new Dimension(350, 130));
		handPane.setLayout(null);
		
		commandPanel = new JPanel();
		commandPanel.setBackground(new Color(128, 0, 0));
		ly_commandPanel=(FlowLayout) commandPanel.getLayout();
		ly_commandPanel.setAlignment(FlowLayout.CENTER);
		commandPanel.setPreferredSize(new Dimension(170, 130));
		southPanel.add(commandPanel);
		
		tavolo = new JPanel();
		tavolo.setBackground(new Color(0, 128, 0));
		contentPane.add(tavolo, BorderLayout.CENTER);
		tavolo.setLayout(new BoxLayout(tavolo, BoxLayout.Y_AXIS));
	}
	
	private void resetView(){
		tavolo.removeAll();
		commandPanel.removeAll();
		handPane.removeAll();
	}
	
	/**
	 * Ricostruisce il percorso dell'immagine della carta che gli viene passata
	 * @param card la carta della cui immagine si vuole recuperare il percorso
	 * @return una stringa contenente il percorso
	 */
	public String getPathCard(Card card){
		int rank=card.getRank().ordinal()+1;
		int suit=card.getSuit().ordinal()+1;
		char cSuit=card.getSuit().name().charAt(0);
		String sRank=card.getRank().name().substring(0, 2);
		String nameFile;
		if (cSuit=='J') {
			nameFile=(rank==1 ? "JR.png" : "JB.png");
		} else if (sRank.equals("JO")) {
			nameFile=(suit==1 ? "JR.png" : "JB.png");
		} else {
			nameFile=String.valueOf(rank)+cSuit+".png";
		}
		return nameFile=pathCards+nameFile;
	}
	
	/**
	 * Visualizza le carte del player di turno e richiama il metodo displayCommandPanel per visualizzare
	 * le icone o i bottoni del pannello di comando a lato delle carte
	 * @param ply il player di turno
	 * @param playersAperture NON SERVE??? 
	 * @param discartedCard la carta in cima al mazzo degli scarti
	 * @param cmd la fase di gioco in ci si trova per visualizzare differenti pannelli di comando
	 */
	public void displayCurrentPlayer(Player ply,
									 Map<Player, 
									 ArrayList<Combination>> playersAperture,
									 Card discartedCard, 
									 int cmd){
		
		handPane.removeAll();
		
		lblPly = new JLabel(ply.getName());
		lblPly.setHorizontalAlignment(SwingConstants.CENTER);
		lblPly.setForeground(new Color(255,255,255));
		lblPly.setFont(new Font("Thaoma",Font.BOLD,12));
		lblPly.setBounds(0, 0, 350, 14);
		handPane.add(lblPly);
		final int faseDiGioco=this.faseDiGioco;
		
		displayCommandPanel(ply,faseDiGioco);
		
		Collections.sort(ply.getPlayerCard());
		imgPlayerCard=new JLabel[ply.getPlayerCard().size()];
		for (int i=imgPlayerCard.length-1;i>=0;i--){
			imgPlayerCard[i]=new JLabel("");
			final JLabel img=imgPlayerCard[i];
			final int index=i;
			String imgPath=getPathCard(ply.getPlayerCard().get(i));
			img.setIcon(new ImageIcon(imgPath));
			img.setBounds(i*21, 24, 71, 96);
			handPane.add(img,new Integer(i+1));
			
			img.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (faseDiGioco!=1){
						int delta=0;
						if (!selectedCard.containsKey(index+1) || !selectedCard.get(index+1)){
							if(img.getLocation().y==24)	{
								if (faseDiGioco==2 || faseDiGioco==5 || faseDiGioco==6) {
									if (scarta>0){
										Point p=imgPlayerCard[scarta-1].getLocation();
										p.setLocation(p.x, p.y+10);
										imgPlayerCard[scarta-1].setLocation(p);
									}
									scarta=index+1;
								} else {
									selectedCard.put(index+1, false);
								}
								delta=-10;
							}
							else {
								if (faseDiGioco==2) scarta=0;
								else selectedCard.remove(index+1);
								delta=10;
							}
							Point p=img.getLocation();
							p.setLocation(p.x, p.y+delta);
							img.setLocation(p);
						}
					}
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {	
				}
				@Override
				public void mouseExited(MouseEvent arg0) {	
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {	
				}
			});
		}		
	}
	
	/**
	 * Visualizza diversi pannelli di comando a lato delle carte del player di turno in base alla fase di gioco
	 * @param currentPlayer il player di turno
	 * @param faseDiGioco la fase di gioco in cui ci si trova
	 * @param discardedCard la carta in cima al mazzo degli scarti
	 */
	public void displayCommandPanel(Player currentPlayer,int faseDiGioco){
		final Player ply=currentPlayer;
		commandPanel.removeAll();
		commandPanel.setLayout(null);
		
		btnScarta = new JButton("Scarta");
		btnScarta.setBounds(20, 25, 130, 23);
		btnScarta.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnScarta);
		btnScarta.setVisible(false);
		
		btnApri = new JButton("Apri");
		btnApri.setBounds(20, 50, 130, 23);
		btnApri.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnApri);
		btnApri.setVisible(false);
		
		btnAttacca = new JButton("Attacca");
		btnAttacca.setBounds(20, 75, 130, 23);
		btnAttacca.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnAttacca);
		btnAttacca.setVisible(false);
		
		btnAnnulla = new JButton("Annulla");
		btnAnnulla.setBounds(20, 75, 130, 23);
		btnAnnulla.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnAnnulla);
		btnAnnulla.setVisible(false);
		
		btnAddComb = new JButton("Aggiungi combinazione");
		btnAddComb.setBounds(20, 50, 130, 23);
		btnAddComb.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnAddComb);
		btnAddComb.setVisible(false);
		
		btnChangeJolly = new JButton("Cambia Jolly");
		btnChangeJolly.setBounds(20, 100, 130, 23);
		btnChangeJolly.setPreferredSize(new Dimension(130,23));
		commandPanel.add(btnChangeJolly);
		btnChangeJolly.setVisible(false);
		
		switch (faseDiGioco){
		case 0: {
			//equivale a cancel, cioè ritorno allo stato precedente??
			break;
		}
		case 1: { // Icone per mazzo scarti e mazzo coperto
			fromScarti=false;
			Card discardedCard=partita.getLastScarti();
			
			lblScarti=new JLabel("Scarti");
			lblScarti.setBounds(10,5,71,23);
			lblScarti.setForeground(new Color(255, 255, 255));
			lblScarti.setHorizontalAlignment(SwingConstants.RIGHT);
			commandPanel.add(lblScarti);
			if (discardedCard!=null){
				imgScarti= new JLabel("");
				imgScarti.setIcon(new ImageIcon(getPathCard(discardedCard)));
				imgScarti.setBounds(10,24,71,96);
				commandPanel.add(imgScarti);
				imgScarti.addMouseListener(new MouseListener(){
					@Override
					public void mouseClicked(MouseEvent arg0) {
						try {
							if(partita.pesca(ply, "2")==-1) {
								fromScarti=true;
								displayHand(ply,3);
							} else {
								displayHand(ply,2);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void mouseEntered(MouseEvent arg0) {	
					}
					@Override
					public void mouseExited(MouseEvent arg0) {	
					}
					@Override
					public void mousePressed(MouseEvent arg0) {
					}
					@Override
					public void mouseReleased(MouseEvent arg0) {	
					}
			
				});
			}
			lblMazzo=new JLabel("Mazzo");
			lblMazzo.setBounds(86,5,130,23);
			lblMazzo.setForeground(new Color(255, 255, 255));
			lblMazzo.setHorizontalAlignment(SwingConstants.LEFT);
			commandPanel.add(lblMazzo);
			
			imgMazzo= new JLabel("");
			imgMazzo.setIcon(new ImageIcon(pathCards+"b1fv.png"));
			imgMazzo.setBounds(86,24,71,96);
			commandPanel.add(imgMazzo);
			imgMazzo.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
						partita.pesca(ply, "1");
						displayHand(ply,2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {	
				}
				@Override
				public void mouseExited(MouseEvent arg0) {	
				}
				@Override
				public void mousePressed(MouseEvent arg0) {
				}
				@Override
				public void mouseReleased(MouseEvent arg0) {	
				}
		
			});
			
			message.setText("Clicca su uno dei mazzi per pescare");
			break;
		}
		case 2: { // Scarta o passa ad apri o attacca
			String txtApri="Apri";
			String txtAttacca="Attacca";
			String txtChangeJolly="Cambia Jolly";
			
			btnScarta.setVisible(true);
			btnScarta.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (scarta!=0){
							if (partita.scarta(ply,scarta)){
								scarta=0;
								selectedComb=0;
								selectedCombPlayer=null;
								newHand(ply);
							} else JOptionPane.showMessageDialog(null, "La carta che vuoi scartare attacca", "Selezione errata", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			if (!partita.getPlayerAperture(ply).isEmpty()){ 
				btnApri.setText("Aggiungi combinazione");
				txtApri=" Aggiungi";
			}
			btnApri.setVisible(true);
			btnApri.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					selectedComb=0;
					selectedCombPlayer=null;
					scarta=0;
					//displayApriOrAddPanel(ply);
					if (partita.getPlayerAperture(ply).isEmpty()){
						displayHand(ply,3);
					} else displayHand(ply,4);
				}
			});
			
			if (!partita.getPlayerAperture(ply).isEmpty()) { 
				btnAttacca.setVisible(true);
				btnAttacca.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						scarta=0;
						displayHand(ply, 5);
					}
				});
				btnChangeJolly.setVisible(true);
				btnChangeJolly.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						displayHand(ply, 6);
					}
				});
			}
			else {
				btnAttacca.setVisible(false);
				txtAttacca="";
				btnChangeJolly.setVisible(false);
				txtChangeJolly="";
			}
			message.setText("Seleziona una carta e premi Scarta, oppure scegli"+txtApri+" "+txtAttacca+" "+txtChangeJolly);
			break;
		}
		case 3: { // Apri o annulla pescata da scarti (solo se fromScarti) altrimenti torna a 2
			btnApri.setVisible(true);
			btnApri.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					apri(ply);
				}
			});
			
			btnAnnulla.setVisible(true);
			btnAnnulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					combMap.clear();
					cardForJolly.clear();
					selectedCard.clear();
					if (fromScarti) {
						try {
							partita.annullaPescaDaScarti(ply); // annullo la pescata (e in automatico la ripeto dal mazzo)
							fromScarti=false;
						} catch (IOException e1) {
							e1.printStackTrace();
						}	
					}
					displayHand(ply,2);
				}
			});
			
			message.setText("Seleziona le carte e premi Apri oppure Annulla per tornare a scartare");
			break;
		}
		case 4: {
			btnAddComb.setVisible(true);
			btnAddComb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					apri(ply);
				}
			});
			btnAnnulla.setVisible(true);
			btnAnnulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					combMap.clear();
					cardForJolly.clear();
					selectedCard.clear();
					displayHand(ply,2);
				}
			});
			
			message.setText("Seleziona le carte e premi Aggiungi oppure Annulla per tornare a scartare");
			break;
		}
		case 5: {
			btnAttacca.setBounds(20, 50, 130, 23);
			btnAttacca.setVisible(true);
			for (Player player:players){
				playerInfo.get(player).activeCombListener();
			}
			btnAttacca.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (scarta!=0){
						if (selectedComb==0){
							JOptionPane.showMessageDialog(null, "Seleziona la combinazione a cui vuoi\n"+
								" attaccare la carta selezionata e poi\n"+"clicca nuovamente su Attacca",
								"Seleziona combinazione", JOptionPane.WARNING_MESSAGE);
						} else { 
							if (partita.checkJolly(ply,scarta)){
								cardForJolly.put(scarta, changeCardToJolly(ply,scarta));// aggiungo a cardForJolly le carte che i jolly sostituiranno
							}
							
							if (partita.attacca(ply,// richiamo il metodo di Scala40 attacca(...) che ritorna true/false
												scarta,
												selectedCombPlayer.getName(),
												selectedComb,
												cardForJolly.get(scarta))){ 
								JOptionPane.showMessageDialog(null, "La carta è stata attaccata","Attacco riuscito", JOptionPane.INFORMATION_MESSAGE);
								ArrayList<Card> combination=partita.getPlayerAperture(selectedCombPlayer).get(selectedComb-1).getCombination();
								JLayeredPane comb=playerInfo.get(selectedCombPlayer).combPanel.get(selectedComb-1);
								playerInfo.get(selectedCombPlayer).displayImageOfCardCombination(combination, comb);
								for (Player player:players){
									playerInfo.get(player).deactiveCombListener();
								}
								cardForJolly.clear();
								scarta=0;
								displayHand(ply, 2);
							}
							else {
								cardForJolly.clear();
								JOptionPane.showMessageDialog(null, "Non è possibile attaccare la carta\n"+"alla combinazione scelta. Riprova",
									"Attacco fallito", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					} else JOptionPane.showMessageDialog(null, "Seleziona la carta che vuoi attaccare e\n"+"" +
							"la combinazione a cui vuoi\n"+"attaccare la carta selezionata", 
							"Selezione per attaccare", JOptionPane.WARNING_MESSAGE);
				}
			});
			
			btnAnnulla.setVisible(true);
			btnAnnulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnAttacca.setBounds(20, 75, 130, 23);
					for (Player player:players){
						playerInfo.get(player).deactiveCombListener();
					}
					scarta=0;
					displayHand(ply,2);
				}
			});
			
			message.setText("Seleziona le carte e premi Attacca oppure Annulla per tornare a scartare");
			break;
		}
		case 6:
			btnChangeJolly.setBounds(20, 50, 130, 23);
			btnChangeJolly.setVisible(true);
			for (Player player:players){
				playerInfo.get(player).activeCombListener();
			}
			btnChangeJolly.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (scarta!=0){
						if (partita.checkJolly(ply,scarta)){
							JOptionPane.showMessageDialog(null, "Hai selezionato un Jolly.\n"+
									"Non puoi cambiare un jolly con un altro.",
									"Seleziona combinazione", JOptionPane.WARNING_MESSAGE);
						}else if (selectedComb==0){
							JOptionPane.showMessageDialog(null, "Seleziona la combinazione che contiene\n"+
								" il Jolly che vuoi cambiare e poi\n"+"clicca nuovamente su Cambia Jolly",
								"Seleziona combinazione", JOptionPane.WARNING_MESSAGE);
						} else { 							
							if (partita.changeJolly(ply,   // richiamo il metodo di Scala40 attacca(...) che ritorna true/false
													scarta,
													selectedCombPlayer.getName(),
													selectedComb)){ 
								JOptionPane.showMessageDialog(null, "Il Jolly è stato sostituito","Sostituzione effettuata", JOptionPane.INFORMATION_MESSAGE);
								ArrayList<Card> combination=partita.getPlayerAperture(selectedCombPlayer).get(selectedComb-1).getCombination();
								JLayeredPane comb=playerInfo.get(selectedCombPlayer).combPanel.get(selectedComb-1);
								playerInfo.get(selectedCombPlayer).displayImageOfCardCombination(combination, comb);
								for (Player player:players){
									playerInfo.get(player).deactiveCombListener();
								}
								scarta=0;
								displayHand(ply, 2);
							}
							else {
								JOptionPane.showMessageDialog(null, "La carta che vuoi sostituire non corrisponde\n"+
															  "a quella che il Jolly sostituisce. Riprova",
									"Sostituzione fallita fallita", JOptionPane.INFORMATION_MESSAGE);
							}
						}
					} else JOptionPane.showMessageDialog(null, "Seleziona la carta che vuoi usare e\n"+"" +
							"la combinazione che contiene il jolly\n"+"che vuoi sostituire", 
							"Selezione per sostituire", JOptionPane.WARNING_MESSAGE);
				}
			});
			btnAnnulla.setVisible(true);
			btnAnnulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnChangeJolly.setBounds(20, 100, 130, 23);
					for (Player player:players){
						playerInfo.get(player).deactiveCombListener();
					}
					scarta=0;
					displayHand(ply,2);
				}
			});
			
			message.setText("Seleziona le carte e premi Cambia Jolly oppure Annulla per tornare a scartare");
			break;
		}
	}
	
	/**
	 * Visualizza la mano. Gestisce tutte le visualizzazioni nelle varie fasi di gioco richiamando gli altri
	 * metodi per le visualizzazioni. Richiama {@code displayCurrentPlayer}, 
	 * @param ply il player di turno
	 * @param playersAperture la mappa delle aperture di tutti i player
	 * @param discartedCard la carta in cima al mazzo degli scarti
	 * @param faseDiGioco la fase di gioco in cui ci si trova
	 */
	public void displayHand(Player currentPlayer,
							int faseDiGioco){
		for (Player ply:players){
			partita.getPlayerAperture().get(ply);
			playerInfo.get(ply).displayApertureCombination(partita.getPlayerAperture(ply));
			if (ply==currentPlayer)	playerInfo.get(ply).lblPlayer.setForeground(Color.RED);
			else playerInfo.get(ply).lblPlayer.setForeground(Color.WHITE);
		}
		this.faseDiGioco=faseDiGioco;
		if (!playerPcMap.get(currentPlayer)){
			displayCurrentPlayer(currentPlayer,partita.getPlayerAperture(), partita.getLastScarti(),faseDiGioco);
		}
		
		paintAll(this.getGraphics());
	}
	
	/**
	 * Controlla il numero di giocatori e ne aggiunge per arrivare almeno a 2.
	 * Serve per garantire un numero minimo di giocatori per inizializzare la partita.
	 * 
	 * @param playerList
	 * @return un Array di Stringhe che rappresenta i giocatori che devono cominciare a giocare
	 */
	public void checkNumberPlayer (Map<String,Boolean> playerMap){
		int i=1;
		while (playerMap.size()<2){
			playerMap.put("Pc Player "+i, true);
			i++;
		}
	}
	
	/**
	 *Passa alla visualizzazione del pannello di comando che gestisce l'apertura
	 * @param currentPlayer
	 */
	public void displayApriOrAddPanel(Player currentPlayer){
		if (scarta>0){
			deselectCard(new ArrayList<Integer>(new Integer(scarta)));
		}
		if (partita.getPlayerAperture(currentPlayer).isEmpty()){
			faseDiGioco=3;
		} else faseDiGioco=4;
		displayHand(currentPlayer,faseDiGioco);
	}
	
	/**
	 * Quando viene selezionato un Jolly per una combinazione o un attacco
	 * richiede quale seme o rank si vuole dare al jolly.
	 * @param currentPlayer il player di turno
	 * @param index l'indice (con index che parte da 1) del jolly nelle carte del player
	 * @return una carta fittizia con il seme(rank) Jollyred o Jollyblack come originale e
	 * come rank(seme) quello deciso in input dall'utente.
	 */
	private Card changeCardToJolly (Player currentPlayer,Integer index){

		Object[] opzioni={"Hearts","Diamonds","Clubs","Spades","Ace","Deuce","Three","Four","Five","Six",
						"Seven","Eight","Nine","Ten","Jack","Queen","King"};
		String command=(String) JOptionPane.showInputDialog(null,"Scegli a quale seme o valore abbinare la carta",
							"Associa Joker",JOptionPane.PLAIN_MESSAGE,null, opzioni,"Heart");
		// non va bene perchè dipende dalla lingua dell'elenco
		return partita.setJollyToCard(currentPlayer,index,command); // aggiungo a cardForJolly le carte che i jolly sostituiranno
	}
	
	/**
	 * Gestisce l'apertura
	 * @param currentPlayer
	 */
	public void apri (Player currentPlayer){
		
		ArrayList<Integer> cmdApri=new ArrayList<Integer>();
		
		for (Integer key:selectedCard.keySet()){
			if (!selectedCard.get(key)){
				cmdApri.add(key);
				selectedCard.put(key, true);
			}
		}
		
		// controllo se sono stati selezionati dei jolly
		
		for (Integer index:cmdApri){ 
			if (partita.checkJolly(currentPlayer, index)){
				cardForJolly.put(index, changeCardToJolly(currentPlayer,index));// aggiungo a cardForJolly le carte che i jolly sostituiranno
			}
		}
		
		int value;
		try {
			value = partita.apri(currentPlayer,combMap,cmdApri,cardForJolly);//richiamo il metodo apri
			if (value==-1){
				combMap.clear();
				JOptionPane.showMessageDialog(null,"La combinazione di carte selezionata non è valida","Combinazione errata",JOptionPane.ERROR_MESSAGE);
			} else if (value>=40 || value==0){
				if (value==0) {
					JOptionPane.showMessageDialog(null, "La combinazione è stata aggiunta","Aggiunta combinazione",JOptionPane.INFORMATION_MESSAGE);
					//playerInfo.get(currentPlayer).displayOneCombination(combMap.get(cmdApri).getCombination());
				} else JOptionPane.showMessageDialog(null,"Complimenti hai aperto con "+value+" punti","Complimenti",JOptionPane.INFORMATION_MESSAGE);
				combMap.clear();
				cardForJolly.clear();
				selectedCard.clear();
				fromScarti=false;
				displayHand(currentPlayer,faseDiGioco=2);
			} else {
				JOptionPane.showMessageDialog(null,"Hai totalizzato "+value+" punti."+
						"Non sono sufficienti per aprire, aggiungi un'altra combinazione"+value+" punti",
						"Non basta!",JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "L'ulima combinazione digitata non è valida.\n"+
												"Riprova con un'altra", "Combinazione errata",JOptionPane.ERROR_MESSAGE);
			if (!combMap.isEmpty()) {
				combMap.remove(cmdApri);
				for (Integer index:cmdApri){ 
					if (partita.checkJolly(currentPlayer, index)){
						cardForJolly.remove(index);
					}
				}
			}
			deselectCard(cmdApri);
			//e.printStackTrace();
		}
	}
	public void deselectCard(ArrayList<Integer> cardToRemove){
		for (int i : cardToRemove){
			Point p=imgPlayerCard[i-1].getLocation();
			p.setLocation(p.x, p.y+10);
			imgPlayerCard[i-1].setLocation(p);
			if (selectedCard!=null && selectedCard.containsKey(i)) selectedCard.remove(i);
			else if (scarta==i) scarta=0;
		}
	}
	
	/**
	 * Passa ad una nuova mano settando il player corrente con il valore del player successivo e richiamando il metodo
	 * {@code displayHand}. Prima però controlla se il player corrente ha terminato le carte dichiarando così la vittoria,
	 * o se il mazzo è terminato sostituendolo così con il mazzo degli scarti
	 * @param currentPlayer il player di turno
	 */
	public void newHand(Player currentPlayer){
		if (currentPlayer.getPlayerCard().size()==0) {  // VERIFICA LA VITTORIA  ///
			JOptionPane.showMessageDialog(this,currentPlayer.getName()+" HAI VINTO");
			// rigenerare la visione iniziale o bloccare tutti i listener e i bottoni
			resetView();
			return;
		}
		partita.checkMazzo();
		Player nextPlayer;
		int index=players.indexOf(currentPlayer);
		nextPlayer=(index==players.size()-1 ? players.get(0) : players.get(index+1));
		displayHand(nextPlayer,faseDiGioco=1);
		if (playerPcMap.get(nextPlayer)){ 
			handPcPlayer (nextPlayer);
		}
	}
	/**
	 * Gestisce il flusso di gioco per un giocate comandato dal Pc
	 * @param currentPlayer il giocatore di turno
	 */
	public void handPcPlayer (Player currentPlayer) {
		int valApertura=-1;
		boolean scartato=false;
		boolean attaccato=false;
		
		try { // pesco dagli scarti
			partita.pesca(currentPlayer, "2");
			fromScarti=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		apertura:
		do {
			PcPlayer pcPlayer=new PcPlayer(currentPlayer.getPlayerCard(),EnumSet.allOf(Jolly.class));
			combMap=pcPlayer.aprire(currentPlayer.getPlayerCard());
			if (!combMap.isEmpty()){
				valApertura=partita.registraApertura(currentPlayer, combMap);
				combMap.clear();
			}
			
			ArrayList<Card> scartabili=pcPlayer.cardScartabili(currentPlayer.getPlayerCard());
			if (!partita.getPlayerAperture(currentPlayer).isEmpty()){
				int nCardPly=currentPlayer.getPlayerCard().size();
				if(nCardPly>2){
					for (Iterator<Card> iCardIt=scartabili.iterator();iCardIt.hasNext();){
						if (attaccato=partita.attaccaOvunque(currentPlayer, iCardIt.next(), null)) iCardIt.remove();
					}
				} else if (nCardPly==2){
					Card iCard=currentPlayer.getPlayerCard().get(0);
					if (!(attaccato=partita.attaccaOvunque(currentPlayer, iCard, null))){
						iCard=currentPlayer.getPlayerCard().get(1);
						if (attaccato=partita.attaccaOvunque(currentPlayer, iCard, null)){
							scartabili.remove(iCard);
						}
					} else {
						scartabili.remove(iCard);
					}
				}
			}
			
			if (fromScarti && !attaccato ) {
				if ((partita.getPlayerAperture(currentPlayer).isEmpty() || valApertura<0)){
					//annulla la pesca dagli scarti e continua il ciclo dall'inizio
					cardForJolly.clear();
					selectedCard.clear();
					try {
						partita.annullaPescaDaScarti(currentPlayer); // annullo la pescata (e in automatico la ripeto dal mazzo)
						fromScarti=false;
						valApertura=-1;
						continue apertura; // faccio ripartire il ciclo dall'inizio
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			
			if (!scartabili.isEmpty()) {
				for (Card iCard:scartabili){
					int i=currentPlayer.getPlayerCard().lastIndexOf(iCard);
					if (scartato=partita.scarta(currentPlayer, i+1)) break apertura;
				}
			}
			if (!scartato){
				for (int i=0; i<currentPlayer.getPlayerCard().size();i++){
					if (scartato=partita.scarta(currentPlayer, i+1)) break apertura;
				}
			}
			
		} while (!scartato);

		newHand(currentPlayer);
	}
	
	//cercare di mantenere separate le visualizzazioni dalla gestione del flusso del programma, in modo da poterlo spostare su una classe esterna.
	/**
	 * E' il core della GUI che gestisce gli input/output durante la partita e le chiamate ai metodi di Scala40.
	 * E' in pratica lo strato di controllo del programma.
	 * @param players
	 */
	public void giocaNuovaPartita(Map<String,Boolean> playerMap){
		String[] players=new String[playerMap.size()];
		int i=0;
		for (String ply:playerMap.keySet()){
			players[i++]=ply;
		}
		try {
			partita=new Scala40(players);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.tavolo.removeAll();
		this.players.clear();
		this.playerPcMap.clear();
		this.playerInfo.clear();
		this.players.addAll(partita.getPlayers());
		Player currentPlayer=partita.getPlayer(0);
		for (Player ply:this.players){
			this.playerInfo.put(ply,new PlayerInfo(ply));
			this.playerPcMap.put(ply, playerMap.get(ply.getName()));
		}
		
		displayHand(currentPlayer,faseDiGioco=1);
		if (playerPcMap.get(currentPlayer)) handPcPlayer(currentPlayer);
	}
}
