import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SaveFrame extends JPanel implements ActionListener{
	/**
	 * Eclipse told me to put this here...
	 */
	private static final long serialVersionUID = 3373077751631331291L;
	private JTextField fileName;
	private JButton saveButton;
	private ArrayList<SaveFrameListener> listeners = new ArrayList<SaveFrameListener>();
	public SaveFrame(){
		super();
		setSize(320, 120);
		fileName = new JTextField();
		fileName.setColumns(12);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.ipadx = 120;
		add(fileName, c);
		c.gridx = 0;
		c.ipadx = 10;
		add(new JLabel("File Name:"), c);
		c.gridy = 1;
		c.gridwidth = 2;
		c.ipadx = 120;
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		add(saveButton, c);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton){
		//	setVisible(false);
			for(SaveFrameListener sfl : listeners){
				sfl.onSaveButtonPressed(fileName.getText());
			}
		//	dispose();
		}
		
	}
	public void addSaveFrameListener(SaveFrameListener sfl){
		listeners.add(sfl);
	}
}
