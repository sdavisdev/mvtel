package com.mvtel.image;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//import com.sun.image.codec.jpeg.ImageFormatException;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Thumbnailer
{
	private static final String BASE_DIR = "c:/Screenshots/";
	private static final int WIDE_PIXEL_WIDTH = 150;
	private static final int TALL_PIXEL_WIDTH = 113;

/*
	
	public static void main(String[] args) throws Exception
	{
		if(args.length != 1)
		{
			System.out.println("Enter the base directory.");
			System.exit(-1);
		}
		
		File baseDir = new File(args[0]);
		if(baseDir.exists())
		{
			System.out.println("Using base directory: " + baseDir);
			
			for(File dir : baseDir.listFiles())
			{
				File thumbnail = new File(dir, "thumb.jpg");
				
				File source = new File(dir, "1.jpg");
				if(!source.exists())
				{
					source = new File(dir, "1.jpeg");
				}
				
				create(source, thumbnail);
			}
		}
		else
		{
			System.out.println(baseDir + " does not exist.");
		}
	}	
	
	public static void create(File source, File thumbnail)
	{
		System.out.print("Creating thumbnail of: " + source + ", under: " + thumbnail);
		
		long start = System.currentTimeMillis();
		
		try{
			BufferedImage image = ImageIO.read(source);
			int height = image.getHeight();
			int width = image.getWidth();
			
			float ratio = (float)width/(float)height;
			if(ratio > 1)
			{
				width  = WIDE_PIXEL_WIDTH;
				height = TALL_PIXEL_WIDTH;
			}
			else
			{
				width = TALL_PIXEL_WIDTH;
				height = WIDE_PIXEL_WIDTH;
			}
			
			Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
			
			BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
			Graphics2D g = result.createGraphics();
			g.drawImage(scaledImage, 0, 0, null);
			g.dispose();
			 
			ImageIO.write(result, "JPG", thumbnail);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("  --  Scale Time: " + (System.currentTimeMillis() - start));
	}
	
	private byte[] image_byte_data(BufferedImage image)
	{
		WritableRaster raster = image.getRaster();
	    DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
	    return buffer.getData();
	}


	public static byte[] bufferedImageToByteArray(BufferedImage img) throws ImageFormatException, IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(img);
	  
		return os.toByteArray();
	}

*/
}
