package uk.ac.soton.ecs.jw15g18;

public class ComplexMatrix {
	float[][] real;
	float[][] imaginary;
	
	ComplexMatrix(float[][] real, float[][] imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	
	void padMatrix(){
		int initialN = real.length;
		int initialM = real[0].length;
		int N = Math.min(initialN, initialM);
		
		// checking the provided matrix is square
		// log(N)/log(2) = log base 2 of N
		if (Math.log(N)/Math.log(2.0) != Math.floor(Math.log(N)/Math.log(2.0))) {
			N = (int) Math.pow(2.0, Math.ceil(Math.log(N)/Math.log(2.0)));
			float[][] paddedReal = new float[N][N];
			float[][] paddedImaginary = new float[N][N];
			
			for (int i = 0; i < initialN; i++){
				for (int j = 0; j < initialM; j++){
					paddedReal[i][j] = real[i][j];
					paddedImaginary[i][j] = imaginary[i][j];
				}
			}
			// method to pad the matrix with zeros to make it a power of 2 size to allow recursion
			for (int i = initialN; i < N; i++){
				for (int j = initialM; j < N; j++){
					paddedReal[i][j] = 0;
					paddedImaginary[i][j] = 0;
				}
			}
			this.real = paddedReal;
			this.imaginary = paddedImaginary;
		}
	}
	
	ComplexMatrix pointByPointMultiply(ComplexMatrix matrix){
		float[][] newReal = new float[real.length][real.length];
		float[][] newImaginary = new float[imaginary.length][imaginary.length];
		for (int i = 0; i < real.length; i++){
			for (int j = 0; j < real.length; j++){
				for (int k = 0; k < matrix.getReal().length; k++) {
					for (int l = 0; l < matrix.getReal().length; l++){
						newReal[i][j] += real[i][j]*matrix.getReal()[k][l] - imaginary[i][j]*matrix.getImaginary()[k][l];
						newImaginary[i][j] += real[i][j]*matrix.getImaginary()[k][l] + imaginary[i][k]*matrix.getReal()[k][l];
					}
				}
			}
		}
		return new ComplexMatrix(newReal, newImaginary);
	}
	
	void setReal(float number, int i, int j){
		this.real[i][j] = number;
	}
	void setImaginary(float number, int i, int j){
		this.imaginary[i][j] = number;
	}
	
	float[][] getReal(){
		return this.real;
	}
	float[][] getImaginary(){
		return this.imaginary;
	}
}