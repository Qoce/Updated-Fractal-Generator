package main;

public class ComplexNumber {
	/**
	 * Complex number class, contains a real part and an imaginary part
	 */
	private double r;
	private double i;
	/**
	 * Creates a complex number from a real and imaginary argument
	 * @param r
	 * @param i
	 */
	public ComplexNumber(double r, double i){
		this.i = i;
		this.r = r;
	}
	/**
	 * Returns this complex number multiplied by another complex number
	 * @param c other complex number
	 */
	public ComplexNumber multiply(ComplexNumber c){
		return new ComplexNumber(c.r * r - c.i * i, c.r * i + c.i * r);
	}
	/**
	 * Sums two complex numbers
	 * @param c other complex number
	 * @return the some of c and this
	 */
	public ComplexNumber add(ComplexNumber c){
		return new ComplexNumber(c.r + r, c.i + i);
	}
	public double getReal(){
		return r;
	}
	public double getImaginary(){
		return i;
	}
	/**
	 * Finds the square of the magnitude of the complex number, this is used instead of the actual magnitude
	 * Because taking the square root of this number is unnecessary, and wastes time, since this is only used
	 * for comparison when testing for divergence
	 * @return Square of magnitude
	 */
	public double getSquareOfMagnitude(){
		return r * r + i * i;
	}
}
