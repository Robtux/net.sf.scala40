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
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
/**
 * Create the dialog "New Game"
 * 
 * @author Roberto Zaniol
 * @version 0.0.0.1 (pre-alpha)
 */
public class NuovaPartita extends JDialog {

	private static final long serialVersionUID = 2591448990926009784L;
	private final JPanel contentPanel = new JPanel();
	private JTextField[] playerArray=new JTextField[6];
	private JCheckBox[] pcPlayerArray=new JCheckBox[6];
	/**
	 * A {@code HashMap} (will be initialized in the constructor) in which
	 * the keys are the names of players and values ​​are a Boolean object
	 * that are equal to false if the player is human.
	 * 
	 */
	private Map<String,Boolean> playerMap=null;
	

	
	/**
	 * The Constructor. Create the dialog per for entering the player name for new game.
	 */
	public NuovaPartita() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblInsertPlayer = new JLabel("Inserisci i nomi dei giocatori");
		pcPlayerArray[0] = new JCheckBox("Computer");
		playerArray[0] = new JTextField();
		playerArray[0].setColumns(10);
		JLabel lblPlayer1 = new JLabel("Giocatore n\u00B0 1");
		pcPlayerArray[1] = new JCheckBox("Computer");
		playerArray[1] = new JTextField();
		playerArray[1].setColumns(10);
		JLabel lblPlayer2 = new JLabel("Giocatore n\u00B0 2");
		pcPlayerArray[2] = new JCheckBox("Computer");
		playerArray[2] = new JTextField();
		playerArray[2].setColumns(10);
		JLabel lblPlayer3 = new JLabel("Giocatore n\u00B0 3");
		pcPlayerArray[3] = new JCheckBox("Computer");
		playerArray[3] = new JTextField();
		playerArray[3].setColumns(10);
		JLabel lblPlayer4 = new JLabel("Giocatore n\u00B0 4");
		pcPlayerArray[4] = new JCheckBox("Computer");
		playerArray[4] = new JTextField();
		playerArray[4].setColumns(10);
		JLabel lblPlayer5 = new JLabel("Giocatore n\u00B0 5");
		pcPlayerArray[5] = new JCheckBox("Computer");
		playerArray[5] = new JTextField();
		playerArray[5].setColumns(10);
		JLabel lblPlayer6 = new JLabel("Giocatore n\u00B0 6");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblInsertPlayer)
						.addGroup(gl_contentPanel.createSequentialGroup().addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(pcPlayerArray[5], Alignment.LEADING)
								.addComponent(pcPlayerArray[4], Alignment.LEADING)
								.addComponent(pcPlayerArray[3], Alignment.LEADING)
								.addComponent(pcPlayerArray[2], Alignment.LEADING)
								.addComponent(pcPlayerArray[1], Alignment.LEADING)
								.addComponent(pcPlayerArray[0], Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(playerArray[5], Alignment.LEADING)
								.addComponent(playerArray[4], Alignment.LEADING)
								.addComponent(playerArray[3], Alignment.LEADING)
								.addComponent(playerArray[2], Alignment.LEADING)
								.addComponent(playerArray[1], Alignment.LEADING)
								.addComponent(playerArray[0], Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPlayer6)
								.addComponent(lblPlayer5)
								.addComponent(lblPlayer4)
								.addComponent(lblPlayer3)
								.addComponent(lblPlayer2)
								.addComponent(lblPlayer1))))
					.addContainerGap(59, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblInsertPlayer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[0], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[0])
						.addComponent(lblPlayer1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[1], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[1])
						.addComponent(lblPlayer2))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[2], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[2])
						.addComponent(lblPlayer3))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[3], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[3])
						.addComponent(lblPlayer4))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[4], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[4])
						.addComponent(lblPlayer5))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pcPlayerArray[5], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(playerArray[5])
						.addComponent(lblPlayer6))
					.addContainerGap(49, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (playerArray!=null){
							playerMap=new HashMap<String,Boolean>();
							String ply=null;
							for (int i=0; i<playerArray.length;i++){
								ply=playerArray[i].getText();
								if (ply.length()>0) {
									playerMap.put(ply, pcPlayerArray[i].isSelected());
								}
							}
						}
						if (playerMap.size()<2) {
							JOptionPane.showMessageDialog(contentPanel, "Il gioco necessita di almeno due giocatori.\n" +
									"Il programma aggiungerà i giocatori mancanti.", "Numero di giocatori insufficienti",JOptionPane.WARNING_MESSAGE);
						}
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public Map<String,Boolean> getPlayer(){
		return playerMap;
	}
}
