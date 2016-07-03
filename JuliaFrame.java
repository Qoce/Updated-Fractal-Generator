package main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

public class JuliaFrame extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4034421417096133974L;
	private JRadioButton zoomBox;
	private JRadioButton pointIterations;
	private JRadioButton nothingH;
	private JRadioButton zoomsIn;
	private JRadioButton switchesToJulia;
	private JRadioButton nothingC;
	private JButton reset;
	private JButton resetZoom;
	private JProgressBar progressRendering;
	private JLabel fractleType;
	private JLabel juliaCoords;
	private JLabel zoomCoords;
	private JLabel zoomWidth;
	public JuliaFrame(){
		super();
		setPreferredSize(new Dimension(320, 360));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 2;
		add(new JLabel("Hovering Mouse Shows:"), c);
		c.gridy = 1;
		zoomBox = new JRadioButton("Zoom Box");
		zoomBox.setSelected(true);
		add(zoomBox, c);
		c.gridy = 2;
		pointIterations = new JRadioButton("Iteration Path");
		add(pointIterations, c);
		c.gridy = 3;
		nothingH = new JRadioButton("Nothing");
		add(nothingH, c);
		ButtonGroup group = new ButtonGroup();
		group.add(zoomBox);
		group.add(pointIterations);
		group.add(nothingH);
		c.gridy = 4;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.PAGE_END;
		add(new JLabel("Clicking Does:"), c);
		c.anchor = GridBagConstraints.PAGE_START;
		c.weighty = 0.0;
		c.ipady = 0;
		c.gridy = 5;
		zoomsIn = new JRadioButton("Zooms in/out");
		zoomsIn.setSelected(true);
		add(zoomsIn, c);
		c.gridy = 6;
		switchesToJulia = new JRadioButton("Shows Julia Set");
		add(switchesToJulia, c);
		c.gridy = 7;
		nothingC = new JRadioButton("Nothing");
		add(nothingC, c);
		ButtonGroup clickGroup = new ButtonGroup();
		clickGroup.add(zoomsIn);
		clickGroup.add(switchesToJulia);
		clickGroup.add(nothingC);
		c.gridwidth = 1;
		c.gridy = 8;
		resetZoom = new JButton("Reset Zoom");
		add(resetZoom, c);
		c.gridx = 1;
		reset = new JButton("Reset All");
		add(reset, c);
		reset.addActionListener(this);
		resetZoom.addActionListener(this);
		c.gridy = 9;
		c.gridwidth = 2;
		c.gridx = 0;
		add(new JLabel("Progress Rendering:"), c);
		c.gridy = 10;
		c.ipadx = 100;
		progressRendering = new JProgressBar();
		progressRendering.setMaximum(100);
		progressRendering.setSize(new Dimension(300, 20));
		add(progressRendering,c);
		c.ipadx = 0;
		c.gridy = 11;
		add(new JLabel("Viewing:"), c);
		c.gridy = 12;
		fractleType = new JLabel("Mandelbrot Set");
		add(fractleType, c);
		c.gridy = 13;
		juliaCoords = new JLabel(" ");
		add(juliaCoords, c);
		c.gridy = 14;
		add(new JLabel("Coordinants:"), c);
		c.gridy = 15;
		zoomCoords = new JLabel("-");
		add(zoomCoords, c);
		c.gridy = 16;
		add(new JLabel("Width:"), c);
		c.gridy = 17;
		zoomWidth = new JLabel("-");
		add(zoomWidth, c);
	}
	public static final int ZOOM_BOX = 1;
	public static final int POINT_ITERATIONS = 2;
	public static final int NOTHING = 0;
	public static final int ZOOM = 1;
	public static final int JULIA = 2;
	public int getHoverType(){
		if(zoomBox.isSelected()){
			return ZOOM_BOX;
		}
		else if(pointIterations.isSelected()) {
			return POINT_ITERATIONS;
		}
		else{
			return NOTHING;
		}
	}
	public int getClickType(){
		if(zoomsIn.isSelected()){
			return ZOOM;
		}
		else if(switchesToJulia.isSelected()) {
			return JULIA;
		}
		else{
			return NOTHING;
		}
	}
	public void setJulia(boolean julia){
		this.switchesToJulia.setEnabled(!julia);
		if(julia) this.zoomsIn.setSelected(true);
	}
	private ArrayList<JuliaFrameListener> listeners = new ArrayList<JuliaFrameListener>();
	public void addJuliaFrameListener(JuliaFrameListener jfl){
		listeners.add(jfl);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == reset){
			for(JuliaFrameListener jfl : listeners) jfl.onResetAllButtonPressed();
		}
		else if(e.getSource() == resetZoom){
			for(JuliaFrameListener jfl : listeners) jfl.onResetZoomButtonPressed();
		}
		
	}
	int progress;
	public void setProgress(int progress){
		this.progress = progress;
	}

	public void updateProgress(){
		this.progressRendering.setValue(progress);
		
	}
	public void setJuliaCoords(ComplexNumber cn){
		if(cn == null){
			fractleType.setText("Mandelbrot Set");
			juliaCoords.setText(" ");
		}
		else{
			fractleType.setText("Julia Set At:");
			char sign = '+';
			int beggining = 0;
			if(cn.getImaginary() < 0){
				sign = '-';
				beggining = 1;
			}
			juliaCoords.setText(Double.toString(cn.getReal()).substring(beggining, 8) + " " + sign + " " + Double.toString(cn.getImaginary()).substring(beggining, 8) + "i");
		}
	}
	public void setViewCoords(ComplexNumber c1, ComplexNumber c2){
		double width = c2.getReal() - c1.getReal();
		int lengthC1 = Double.toString(c1.getReal()).length();
		int lengthC1I = Double.toString(c1.getImaginary()).length();
		int lengthWidth = Double.toString(width).length();
		char sign = '+';
		int beggining = 0;
		if(c1.getImaginary() < 0){
			sign = '-';
			beggining = 1;
		}
		zoomCoords.setText(Double.toString(c1.getReal()).substring(beggining, lengthC1 > 8 ? 8 : lengthC1) + " " + sign + " " + Double.toString(c1.getImaginary()).substring(beggining, lengthC1I > 8 ? 8 : lengthC1I) + "i");
		zoomWidth.setText(Double.toString(width).substring(beggining, lengthWidth > 16 ? 16 : lengthWidth));
	}
}
