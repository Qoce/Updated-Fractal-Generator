package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;


public class Main implements MouseListener, MouseMotionListener, MouseWheelListener, DetailFrameListener, SaveFrameListener, JuliaFrameListener{
	//Width in pixels of the Mandelbrot frame
	public int PIXEL_WIDTH = 720; 
	//Resolution of how many points are calculated to display the image
	public int CALCULATION_RESOLUTION = 720;
	//Maximum number of iterations calculated for each point
	public int NUM_ITERATIONS = 256;
	
	//Default boundaries in complex plane for Mandelbrot observation
	public static final ComplexNumber BOUND_ONE = new ComplexNumber(-1.7, -1.1);
	public static final ComplexNumber BOUND_TWO = new ComplexNumber(0.5, 1.1);
	//The current boundaries that are being looked at, each number represents one corner,
	//and it is expected that c2 is bigger than c1 in both the real and imaginary parts
	public ComplexNumber c1;
	public ComplexNumber c2;
	//The seed of the Julia set that is being generated, null if generating Mandelbrot set
	public ComplexNumber julia;
	//JFrame of the program, which includes the image as well as several UIs
	public JFrame frame;
	//JLabel that the fractal is generated on
	public JLabel fractalLabel;
	//The JPanel that fractal label is on, as well as the mouse label
	public JPanel fractalPanel;
	//Frame in the UI contains buttons regarding what type of fractal to generate,
	//Stored as instance variable in order to update the progress bar.
	public JuliaFrame jf;
	public static void main(String[] args){
		new Main();
	}
	/**
	 * Creates the program and generates an image of the Mandelbrot set at the 
	 * default boundaries
	 */
	public Main(){
		//JFrame is initialized using a grid bag layout.  
		//A large square containing the image is placed in the left of the screen
		//And in the small margin three different UI's are placed above each other
		frame = new JFrame();
		frame.setBounds(0, 0, PIXEL_WIDTH + 240, PIXEL_WIDTH);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridheight = 4;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		fractalPanel = new JPanel();
		fractalPanel.setPreferredSize(new Dimension(PIXEL_WIDTH, PIXEL_WIDTH));
		frame.add(fractalPanel, c);
		jf = new JuliaFrame();
		//Sets the viewing coordinates to the defaults and generates the image
		//This is done before generating any of the UI's so that the fractalpanel
		//Is properly sized first
		setCoords(BOUND_ONE, BOUND_TWO);
		renderImage();
		c.ipady = 20;
		c.gridheight = 1;
		c.gridx = 1;
		DetailFrame df = new DetailFrame();
		df.addDetailFrameListener(this);
		frame.add(df, c);
		c.gridy = 1;
		SaveFrame sf = new SaveFrame();
		sf.addSaveFrameListener(this);
		frame.add(sf, c);
		c.gridy = 2;
		jf.addJuliaFrameListener(this);
		frame.add(jf, c);
	
		frame.repaint();
		frame.setVisible(true);
		fractalPanel.addMouseListener(this);
		fractalPanel.addMouseMotionListener(this);
		frame.addMouseWheelListener(this);
		
	}
	/**
	 * Creates the image of the Mandelbrot or Julia set
	 * @param c1 The lower bound of the area to be generated
	 * @param c2 The upper bound of the area to be generated
	 * @param resolution Resolution of how many points to be tested in each dimension
	 * @param iterations Number of iterations to test a point before assuming it diverges
	 * @param juliaLocation seed of Julia set to be generated - null makes it generate Mandelbrot set
	 * @return The image of the set at the coordinates specified
	 */
	public Image createImage(ComplexNumber c1, ComplexNumber c2, int resolution, int iterations, ComplexNumber juliaLocation){
		//Initializes image at proper resolution
		BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_3BYTE_BGR);
		//Step to change coordinate with each iteration of loop, such that the loop has evenly spaced points generated
		//Throughout the area to view
		double realStep = (c2.getReal() - c1.getReal()) / resolution;
		double imaginaryStep = (c2.getImaginary() - c1.getImaginary()) / resolution;
		for(int r = 0; r < resolution; r++){
			for(int i = 0; i < resolution; i++){
				//Finds the proper complex number to generate based on the loops location,
				//Generates the Mandelbrot set if Julia Location is null by setting the constant to the point it self
				//Otherwise, sets the constant to the Julia seed
				ComplexNumber p = new ComplexNumber(c1.getReal() + realStep * r, c1.getImaginary() + imaginaryStep * i);
				if(juliaLocation == null) image.setRGB(r, i, getColorForPoint(p, p, iterations).getRGB());
				else  image.setRGB(r, i, getColorForPoint(p, juliaLocation, iterations).getRGB());
			}
			if(jf != null) {
				//Updates the progress bar in the Julia frame based on the progress of the loop
				jf.setProgress((int) ((100 * (double) (r) / resolution)));
				jf.updateProgress();
			}
		}
		if(jf != null) jf.setProgress(100);
		return image;
		
	}
	/**
	 * Returns the color for a specific point to generate by iterating it through the equation with 
	 * The specified constant value
	 * @param p The point to be tested
	 * @param c The Constant to use in the iterations - either the point itself for Mandelbrot sets, or a constant for julia sets
	 * @param numIterations Maximum number of iterations to test a point
	 * @return the color based o the number of iteratiosn of the point
	 */
	public Color getColorForPoint(ComplexNumber p, ComplexNumber c, int numIterations){
		int num = iteratePoint(p, c, numIterations);
		//Returns black if the point converges
		if(num == -1) return Color.BLACK; 
		//Otherwise, returns a color based on a gradient, depending on how many
		//Iterations it took to diverge.
		else {
			int colorRotation = NUM_ITERATIONS;
			if(multipleIterations) {
				num %= 256;
				colorRotation = 256;
			}
			if(num < colorRotation / 5){
				return new Color(num * 5 * 256 / colorRotation, 0 , 0);
			}
			else if(num < 2 * colorRotation / 5){
				num -= colorRotation / 5;
				return new Color(255 - num * 5 * 256 / colorRotation, 5 * num * 256 / colorRotation, 0);
			}
			else if(num < 3 * colorRotation / 5){
				num -= 2 * colorRotation / 5;
				return new Color(0, 255 - 5 * num * 256 / colorRotation, 5 * num * 256 / colorRotation);
			}
			else if(num < 4 * colorRotation / 5){
				num -= 3 * colorRotation / 5;
				return new Color(5 * num / 2 * 256 / colorRotation, 5 * num / 2 * 256 / colorRotation, 255 - 5 * num * 256 / colorRotation);
			}
			else{
				num -= 4 * colorRotation / 5;
				return new Color(130 - 5 * num / 2 * 256 / colorRotation, 130 - 5 * num / 2 * 256 / colorRotation, 0);
			}
		}
	}
	/**
	 * Iterates a point through the Mandelbrot equation by squaring it and adding the constant
	 * @param p point to iterate
	 * @param c constant point to add after squaring
	 * @param iterationsTotal, max iterations to test before returning -1
	 * @return if the point diverges, the number of iterations it took to diverge, otherwise, -1
	 */
	public int iteratePoint(ComplexNumber p, ComplexNumber c, int iterationsTotal){
		double constSquaredMagnitude = c.getSquareOfMagnitude();
		double maxValue = 0.5  * (1 + Math.sqrt(1 + 4 * constSquaredMagnitude) + 2 * constSquaredMagnitude);
		if(maxValue > 4.0) maxValue = 4.0;
		for(int i = 0; i < iterationsTotal; i++){
			if(p.getSquareOfMagnitude() > maxValue) return i;
			p = p.multiply(p).add(c);
		}
		return -1;
	}
	@Override
	public void mouseDragged(MouseEvent e) {}
	// Size of the square above the mouse that is displayed when the mouse is in zoom box mode
	public int squareSize = 72;
	@Override
	/**
	 * Called when mouse is moved, displays either a zoom box or a point iteration, or nothing, based on the user selected mode.
	 */
	public void mouseMoved(MouseEvent e) {
		int k = jf.getHoverType();
		if(k == JuliaFrame.ZOOM_BOX )createZoomSquare(new Point(e.getX() - squareSize / 2, e.getY() - squareSize / 2), new Point(e.getX() + squareSize / 2, e.getY() + squareSize / 2));
		else if(k == JuliaFrame.POINT_ITERATIONS) createPointDivergence(new Point(e.getX(), e.getY()));
	}
	//Label of zoom square displayed on mouse, or the point iteration
	private JLabel squareLabel;
	/**
	 * Creates the zoom square label and adds it to the frame based on the PIXEL bounds
	 * @param c1 lower point bounds on screen for where it should be displayed 
	 * @param c2 upper point bounds
	 */
	public void createZoomSquare(Point c1, Point c2){
		if(squareLabel != null) frame.getLayeredPane().remove(squareLabel);
		BufferedImage image = new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_4BYTE_ABGR);
		//Fills edges of image with white
		for(int i = 0; i < squareSize; i++){
			image.setRGB(i, 0, Color.white.getRGB());
			image.setRGB(i, squareSize - 1, Color.white.getRGB());
			image.setRGB(0, i, Color.white.getRGB());
			image.setRGB(squareSize - 1, i, Color.white.getRGB());
		}
		zoomSquareC1 = c1;
		zoomSquareC2 = c2;
		squareLabel = new JLabel();
		squareLabel.setIcon(new ImageIcon(image));
		squareLabel.setBounds(c1.x, c1. y, squareSize, squareSize);
		frame.getLayeredPane().add(squareLabel);
		frame.repaint();
	}
	/**
	 * Creates the point divergence based on the location of the mouse.  This visual draws
	 * lines from the value of each iteration of the point to each of the next iterations.
	 * @param c location in pixels on mouse.
	 */
	public void createPointDivergence(Point c){
		if(squareLabel != null) frame.getLayeredPane().remove(squareLabel);
		BufferedImage image = new BufferedImage(PIXEL_WIDTH, PIXEL_WIDTH, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		ComplexNumber cn = pointToComplex(c);
		ComplexNumber constcn = julia == null ? pointToComplex(c) : julia;
		Point lastPoint = c;
		for(int i = 0; i < NUM_ITERATIONS; i++){
			cn = cn.multiply(cn).add(constcn);
			Point nextPoint = complexToPoint(cn);
			g.drawLine(lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y);
			lastPoint = nextPoint;
		}
		squareLabel = new JLabel();
		squareLabel.setIcon(new ImageIcon(image));
		squareLabel.setBounds(0, 0, PIXEL_WIDTH, PIXEL_WIDTH);
		frame.getLayeredPane().add(squareLabel);
		frame.repaint();
	}
	/**
	 * Complex boundaries of the zoom squares
	 */
	Point zoomSquareC1;
	Point zoomSquareC2;
	@Override
	/**
	 * If the click type is zoom, it zooms in based on the location of the zoom square (this will happen even
	 * if the zoom square is not shown). Changes the bonds and renders the image. 
	 * Otherwise, Julia set is generated based on Julia point
	 */
	public void mouseClicked(MouseEvent e) {
		if(zoomSquareC1 != null && jf.getClickType() == JuliaFrame.ZOOM){
			ComplexNumber c1 = pointToComplex(zoomSquareC1);
			ComplexNumber c2 = pointToComplex(zoomSquareC2);
			//If the left mouse button is pressed, it zooms in
			if(e.getButton() == MouseEvent.BUTTON1){
				setCoords(c1, c2);
				renderImage();
			}
			//Otherwise, if the right mouse button is pressed, it zooms out, zooming out more the smaller the zoom box
			else if(e.getButton() == MouseEvent.BUTTON3){
				double frameWidth = this.c2.getReal() - this.c1.getReal();
				double zoomWidth = c2.getReal() - c1.getReal();
				double inverseRatio = frameWidth / zoomWidth;
				ComplexNumber center = new ComplexNumber((c2.getReal() + c1.getReal()) / 2, (c2.getImaginary() + c1.getImaginary()) / 2);
				setCoords(new ComplexNumber(center.getReal() - frameWidth * inverseRatio / 2, center.getImaginary() - frameWidth * inverseRatio / 2), new ComplexNumber(center.getReal() + frameWidth * inverseRatio / 2, center.getImaginary() + frameWidth * inverseRatio / 2));
				renderImage();
			}
		}
		else if(jf.getClickType() == JuliaFrame.JULIA){
			//Generates julia set at default coordinates
			setJulia(pointToComplex(e.getPoint()));
			setCoords(new ComplexNumber(-1.1, -1.7), new ComplexNumber(1.1, 0.5));
			renderImage();
		}
	}
	/**
	 * Sets the coordinates to be viewed in the image, updates JuliaFrame label
	 */
	public void setCoords(ComplexNumber c1, ComplexNumber c2){
		this.c1 = c1;
		this.c2 = c2;
		jf.setViewCoords(c1, c2);
	}
	/**
	 * Converts a pixel on the screen to the complex number it corresponds to
	 * @param p the point being converged
	 * @return the complex number converted to
	 */
	public ComplexNumber pointToComplex(Point p){
		return new ComplexNumber(this.c1.getReal() + (c2.getReal() - this.c1.getReal()) * p.x / PIXEL_WIDTH, this.c1.getImaginary() + (c2.getImaginary() - this.c1.getImaginary()) * (double)(p.y) / (double) PIXEL_WIDTH);
	}
	/**
	 * Converts a complex number to the pixel on the screen it would correspond to
	 * @param cn the complex number
	 * @return converted point.
	 */
	public Point complexToPoint(ComplexNumber cn){
		return new Point((int) ((cn.getReal() - c1.getReal()) / (c2.getReal() - c1.getReal()) * PIXEL_WIDTH),(int) ((cn.getImaginary() - c1.getImaginary()) / (c2.getImaginary() - c1.getImaginary()) * PIXEL_WIDTH));
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	/**
	 * Removes square label if the mouse leaves the fractalPanel
	 */
	public void mouseExited(MouseEvent e) {
		if(squareLabel != null) {
			fractalPanel.remove(squareLabel);
			fractalPanel.repaint();
		}
	}
	/**
	 * Changes the size of the zoom square based on the mouse scroll wheel
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int amount = e.getWheelRotation();
		if(squareSize + amount > 10 && squareSize + amount < PIXEL_WIDTH / 2) squareSize += amount;
		mouseMoved(e);
	}
	//True if the color gradient used for points that do not diverge should cycle more than one time
	//For example, if the number of iterations is 1024, it will cycle 4 times if this is true
	//Otherwise, if this was false it would iterate once, through the cycle over the number of iterations
	public boolean multipleIterations = false;
	@Override
	/**
	 * Called whenever the DetailFrame is changed, updates resolution, number of iterations, and weather or not to use multiple iterations
	 * And regenerates the image
	 */
	public void onChangedPressed(int newResolution, int newNumIterations,
			boolean multipleIterations) {
		CALCULATION_RESOLUTION = newResolution;
		NUM_ITERATIONS = newNumIterations;
		this.multipleIterations = multipleIterations;
		renderImage();
	}
	//Image for displayed fractal
	BufferedImage fractalImage;
	/**
	 * Performs the large calculations used to render the image in the background, which allows the progress bar to be updated
	 * while it is running.  Regenerates image at the coordinates and resolution set by the instance variables.
	 */
	public void renderImage(){
		

		SwingWorker<BufferedImage, Void> sw = new SwingWorker<BufferedImage, Void>(){

			@Override
			protected BufferedImage doInBackground() throws Exception {
				fractalImage = null;
				BufferedImage scaledImage = new BufferedImage(PIXEL_WIDTH,PIXEL_WIDTH, BufferedImage.TYPE_3BYTE_BGR);
				fractalImage = (BufferedImage) createImage(c1, c2, CALCULATION_RESOLUTION, NUM_ITERATIONS, julia);
				scaledImage.getGraphics().drawImage(fractalImage, 0, 0, 720, 720, null);
				if(fractalLabel != null) fractalPanel.remove(fractalLabel);
				fractalLabel = new JLabel();
				fractalLabel.setIcon(new ImageIcon(scaledImage));
				fractalLabel.setSize(PIXEL_WIDTH, PIXEL_WIDTH);
				fractalPanel.add(fractalLabel);
				fractalPanel.repaint();
				return null;
			}
			
		};
		sw.execute();
	}
	/**
	 * Called when the save button is pressed on the save view controller, saves the image currently displayed in the frame
	 * To the string the user typed in the textbox
	 */
	@Override
	public void onSaveButtonPressed(String fileName) {
		BufferedImage image = fractalImage;
		try {
			ImageIO.write(image, "jpg", new File(fileName));
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Saved!");
	}
	/**
	 * sets Julia seed and updates JuliaFrame
	 * @param julia
	 */
	public void setJulia(ComplexNumber julia){
		if(julia == null){
			jf.setJulia(false);
		}
		else{
			jf.setJulia(true);
		}
		jf.setJuliaCoords(julia);
		this.julia = julia;
	}
	/**
	 * Resets to Mandelbrot set mode and sets the Frame to default zoom resolution
	 */
	@Override
	public void onResetAllButtonPressed() {
		setJulia(null);
		onResetZoomButtonPressed();
	}
	/**
	 * Resets zoom of complex number to its default value and redisplays the image
	 */
	@Override
	public void onResetZoomButtonPressed() {
		c1 = new ComplexNumber(-1.7, -1.1);
		c2 = new ComplexNumber(0.5, 1.1);
		renderImage();
	}
}
