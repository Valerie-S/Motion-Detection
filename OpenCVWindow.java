package com.test.motion.detection;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class OpenCVWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	Sheet sheet;
	int height, width;


	public OpenCVWindow( int length, int breadth)
	{
		width = length;
		height = breadth;
		sheet = new Sheet(breadth,length);
		
		this.setSize(new Dimension(length, breadth));
		this.add(sheet);

		this.setFocusable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	


	public void showImage(Mat m)
	{
		MatOfByte matOfByte = new MatOfByte();
		Highgui.imencode(".jpg", m, matOfByte);
		
		byte[] byteArray = matOfByte.toArray();
		try
		{

			InputStream in = new ByteArrayInputStream(byteArray);
			sheet.paintSheet(ImageIO.read(in));
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
