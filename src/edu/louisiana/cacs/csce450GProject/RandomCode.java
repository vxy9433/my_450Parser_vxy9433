package edu.louisiana.cacs.csce450Project;

/*
 * This file is intended to provide instruction on how to create unit tests.
 * See the test code in 
 * test/edu/louisiana/cacs/csce450Project/SanityCheck.java
 *
 */


public class RandomCode{
	public int addTwo(int a, int b){
		return a+b;
	}
	public int subTwo(int a, int b){
		return a-b;
	}
	public int multTwo(int a, int b){
		return a*b;
	}
	public float divide(float numerator, float denominator){
		if (denominator == 0.0f){
			throw new IllegalArgumentException("Argument 'denominator' is 0");
		}else return numerator/denominator;
	}
}