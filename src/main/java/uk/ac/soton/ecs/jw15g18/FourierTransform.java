package uk.ac.soton.ecs.jw15g18;

public class FourierTransform {
	final int cutoff;
	
	public FourierTransform(int cutoff){
		this.cutoff = cutoff;
	}
	
	public ComplexMatrix DiscreteFourierTransform(ComplexMatrix matrix){
		float[][] realMatrix = matrix.getReal();
		float[][] imagMatrix = matrix.getImaginary();
		
		int N = realMatrix.length;
		float[][] transformReal = new float[N][N];
		float[][] transformImaginary = new float[N][N];
		
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				float real = realMatrix[i][j];
				float imag = imagMatrix[i][j];
				float sumReal = 0;
				float sumImag = 0;
				
				for (int k = 0; k < N; k++){
					for (int l = 0; l < N; l++){
						float c = (1f/N)*(k + l);
						sumReal += real*Math.cos(2*Math.PI*c) - imag*Math.sin(2*Math.PI*c);
						sumImag += real*Math.sin(2*Math.PI*c) + imag*Math.cos(2*Math.PI*c);
					}
				}
				transformReal[i][j] = sumReal;
				transformImaginary[i][j] = sumImag;
			}
		}
		return new ComplexMatrix(transformReal, transformImaginary);
	}
	
	public ComplexMatrix FastFourierTransform(ComplexMatrix matrix){
		int N = matrix.getReal().length;
		
		// do the DFT if the matrix is a sufficient size
		if (N <= cutoff){
			return DiscreteFourierTransform(matrix);
		} else {
			EvenOddComplex matrices = new EvenOddComplex();
			matrices.splitMatrix(matrix);
			ComplexMatrix even = matrices.getEven();
			ComplexMatrix odd = matrices.getOdd();
			ComplexMatrix fourierEven = FastFourierTransform(even);
			ComplexMatrix fourierOdd = FastFourierTransform(odd);
			
			float[][] real = new float[N][N];
			float[][] imag = new float[N][N];
			for (int i = 0; i < N; i++){
				for (int j = 0; j < N; j++){
					real[i][j] = even.getReal()[i][j] + odd.getReal()[i][j];
					imag[i][j] = even.getImaginary()[i][j] + odd.getImaginary()[i][j];
				}
			}
			return FastFourierTransform(new ComplexMatrix(real, imag));
		}
	}
	
	public ComplexMatrix InverseFastFourierTransform(ComplexMatrix matrix){
		int N = matrix.getImaginary().length;
		
		// computing the conjugate of each element in the matrix (as IFT* = FFT* times e^j(...)
		float[][] imaginary = new float[N][N];
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				imaginary[i][j] = -matrix.getImaginary()[i][j];
			}
		}
		
		ComplexMatrix matrixConjugate = new ComplexMatrix(matrix.getReal(), imaginary);
		
		ComplexMatrix ifftConjugate = FastFourierTransform(matrixConjugate);
		float[][] finalImag = new float[N][N];
		for (int i = 0; i < N; i++){
			for (int j = 0; j < N; j++){
				finalImag[i][j] = -ifftConjugate.getImaginary()[i][j];
			}
		}
		
		return new ComplexMatrix(ifftConjugate.getReal(), finalImag);
	}
}

/*
class EvenOddReal {
	float[][] even;
	float[][] odd;
	
	void splitMatrix(float[][] matrix){		
		int N = matrix.length;
		int M = matrix[0].length;
		int halfN = N/2;
		int halfM = M/2;
		
		float[][] even = new float[halfN][halfM];
		float[][] odd = new float[halfN][halfM];
		
		for (int i = 0; i < N; i++){
			for (int j = 0; j < M; j++){
				// if both indices are even or odd, then that element is even
				if ( (i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0) ){
					even[i][j] = matrix[i][j];
				}
				// else one even and one odd, the that element is odd
				else {
					odd[i][j] = matrix[i][j];
				}
			}
		}
		
		this.even = even;
		this.odd = odd;
	}
	
	float[][] getEven(){
		return this.even;
	}
	float[][] getOdd(){
		return this.odd;
	}
}
*/

class EvenOddComplex {
	ComplexMatrix even;
	ComplexMatrix odd;
	
	void splitMatrix(ComplexMatrix matrix){
		float[][] real = matrix.getReal();
		float[][] imaginary = matrix.getImaginary();
		
		int N = real.length;
		int M = real[0].length;
		int halfN = N/2;
		int halfM = M/2;
		
		float[][] evenReal = new float[halfN][halfM];
		float[][] evenImaginary = new float[halfN][halfM];
		float[][] oddReal = new float[halfN][halfM];
		float[][] oddImaginary = new float[halfN][halfM];
		
		for (int i = 0; i < real.length; i++){
			for (int j = 0; j < real[0].length; j++){
				// if both indices are even or odd, then that element is even
				if ( (i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0) ){
					evenReal[i][j] = real[i][j];
					evenImaginary[i][j] = imaginary[i][j];
				}
				// else one even and one odd, the that element is odd
				else {
					oddReal[i][j] = real[i][j];
					oddImaginary[i][j] = imaginary[i][j];
				}
			}
		}
		
		this.even = new ComplexMatrix(evenReal, evenImaginary);
		this.odd = new ComplexMatrix(oddReal, oddImaginary);
	}
	
	ComplexMatrix getEven(){
		return this.even;
	}
	ComplexMatrix getOdd(){
		return this.odd;
	}
}
