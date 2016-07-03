package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DetailFrame extends JPanel implements ActionListener{
	/**
	 * Eclipse told me to put this here.
	 */
	private static final long serialVersionUID = 7697147208196450295L;
	private JTextField iterationTF;
	private JTextField resolutionTF;
	private JCheckBox multipleIterations;
	private JButton changeButton;
	private boolean miSelected = true;
	private ArrayList<DetailFrameListener> listeners = new ArrayList<DetailFrameListener>();
	public DetailFrame(){
		//super("Set Resolution and Number of Iterations");
		setSize(320, 140);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 60;
		JLabel label = new JLabel("Resolution:");
		add(label, c);
		c.gridx = 1;
		resolutionTF = new JTextField("720");
		add(resolutionTF, c);
		c.gridx = 0;
		c.gridy = 1;
		JLabel label2 = new JLabel("Iterations:");
		add(label2, c);
		c.gridx = 1;
		iterationTF = new JTextField("256");
		add(iterationTF, c);
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		changeButton = new JButton("Change");
		add(changeButton,c);
		c.gridy = 2;
		multipleIterations = new JCheckBox("Multiple Color Cycles?", true);
		add(multipleIterations, c);
		setVisible(true);
		multipleIterations.addActionListener(this);
		changeButton.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == changeButton){
//			setVisible(false);
			for(DetailFrameListener dfl : listeners){
				dfl.onChangedPressed(Integer.parseInt(resolutionTF.getText()), Integer.parseInt(iterationTF.getText()), miSelected);
			}
//			dispose();
		}
		if(e.getSource() == multipleIterations){
			miSelected = !miSelected;
			System.out.println(miSelected);
		}
	}
	public void addDetailFrameListener(DetailFrameListener dfl){
		listeners.add(dfl);
	}
}
