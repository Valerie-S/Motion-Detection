package com.test.motion.detection;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class MotionDetection {
	
	static int SENSITIVITY_VALUE = 20;
	static int BLUR_SIZE = 10;
	static int WIDTH = 640;
	static int HEIGHT = 480;
	
	static OpenCVWindow thresholdWindow;
	static OpenCVWindow drawnWindow;

	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		thresholdWindow = new OpenCVWindow(WIDTH,HEIGHT);
		drawnWindow = new OpenCVWindow(WIDTH,HEIGHT);
		readFromCamera();

	}
	
	static void readFromCamera()
	{
		while (true)
		{
			VideoCapture camera = new VideoCapture(0);
			if (!camera.isOpened())
				System.out.println("Cannot open file");

			else
			{
				Mat previousFrame = new Mat();
				Mat currentFrame = new Mat();
				Mat grayImage1 = new Mat();
				Mat grayImage2 = new Mat();
				Mat differenceImage = new Mat();
				Mat thresholdImage = new Mat();
				boolean frameSuccess;

				// THE INFINITE LOOP
				camera.read(previousFrame);
				
				Imgproc.resize(previousFrame, previousFrame, new Size(WIDTH,HEIGHT));
				Imgproc.cvtColor(previousFrame, grayImage1,
						Imgproc.COLOR_BGR2GRAY);
				
				while (true)
				{

					frameSuccess = camera.read(currentFrame);
					if (frameSuccess == true)
					{
						Imgproc.resize(currentFrame, currentFrame, new Size(WIDTH,HEIGHT));
						Imgproc.cvtColor(currentFrame, grayImage2,
								Imgproc.COLOR_BGR2GRAY);
					
					}else
						break;
					
					// DIFFERENCE
					Core.absdiff(grayImage1, grayImage2, differenceImage);
					Imgproc.threshold(differenceImage, thresholdImage,
							SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);
					Imgproc.blur(thresholdImage, thresholdImage, new Size(
							BLUR_SIZE, BLUR_SIZE));
					Imgproc.threshold(thresholdImage, thresholdImage,
							SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);
					
					thresholdWindow.showImage(thresholdImage);
					
					searchForMovement(thresholdImage, currentFrame);
					
					//Previous frame is now this frame
					grayImage2.copyTo(grayImage1);
				}
				camera.release();
			}

		}
	}
	
	static void searchForMovement(Mat thresholdImage, Mat frame)
	{
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(thresholdImage, contours, hierarchy,
			Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Rect objectBoundingRectangle = new Rect(0, 0, 0, 0);
		for (int i = 0; i < contours.size(); i++)
		{
			objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
			if(objectBoundingRectangle.area()>500)
			Core.rectangle(frame, objectBoundingRectangle.tl(), objectBoundingRectangle.br(), new Scalar(0,255,0));
		}
		
		drawnWindow.showImage(frame);
	}

}
