package uk.ac.soton.ecs.jw15g18;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {
	private float[][] kernel;

	public MyConvolution(float[][] kernel) {
		this.kernel = kernel;
	}

	@Override
	public void processImage(FImage image) {
		// convolve image with kernel and store result back in image
		int rows = image.pixels.length;
		int cols = image.pixels[0].length;
		
		FourierTransform fourierTransform = new FourierTransform(32);
		
		FImage clone = image.clone();
		float[][] buffReal = clone.pixels;
		
		// initialising the imaginary part of the image (all zeros)
		float[][] buffImag = new float[rows][cols];
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				buffImag[i][j] = 0;
			}
		}
		ComplexMatrix pixels = new ComplexMatrix(buffReal, buffImag);
		pixels.padMatrix();
		
		// initialising the imaginary part of the kernel (all zeros)
		float[][] kernelImag = new float[kernel.length][kernel[0].length];
		for (int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				kernelImag[i][j] = 0;
			}
		}
		ComplexMatrix complexKernel = new ComplexMatrix(kernel, kernelImag);
		complexKernel.padMatrix();
		
		pixels = fourierTransform.FastFourierTransform(pixels);
		complexKernel = fourierTransform.FastFourierTransform(complexKernel);
		
		ComplexMatrix pointByPoint = pixels.pointByPointMultiply(complexKernel);
		pixels = fourierTransform.InverseFastFourierTransform(pointByPoint);
		
		FImage buffImage = new FImage(pixels.getReal());
		image.internalAssign(buffImage);
		// hint: use FImage#internalAssign(FImage) to set the contents
		// of your temporary buffer image to the image.
	}
	
	/*
	private float getPointTemplateConvolve(float[][] pixels, float[][] kernel, int x, int y, int a, int b) throws ArrayIndexOutOfBoundsException {
		// setting the bounds (xmin & ymin can't be below zero)
		int xmin = x - b/2;
		int ymin = y - a/2;
		int xbound = pixels[0].length;
		int ybound = pixels.length;
		
		float sum = 0f;
		// i covers the range of pixels of the size of the kernel on the current y, and ii represents the y position in the kernel
		for (int i = 0, ii = a - 1; i < a; i++, ii--) {
			// i covers the range of pixels of the size of the kernel on the current y, and ii represents the y position in the kernel
			for (int j = 0, jj = b - 1; j < b; j++, jj--) {
				// the current x and y position in the image 
				int xCurrent = xmin + j;
				int yCurrent = ymin + i;
				// checking the current iteration is within the bounds of the image
				if ( (xCurrent >= 0 && yCurrent >= 0) && (xCurrent < xbound && yCurrent < ybound) ) {
					sum += pixels[yCurrent][xCurrent]*kernel[ii][jj];
				}
			}
		}
		return sum;
	}
	*/
}