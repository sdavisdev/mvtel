package com.mvtel.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;

/**
 *
 * @author Steve
 */
public class ImageManager 
{
    private static String siteUrl = "www.mvtelonline.com";
    
    public enum ImageScale 
    {
        THUMBNAIL(60, 60, 0, "-thumb"),
        SMALL(113, 150, 8, "-small"),
        MEDIUM(195, 260, 16, "-medium"),
        LARGE(480, 640, 28, "");
        
        int width, height, fontSize;
        String suffix;
        
        ImageScale(int width, int height, int fontSize, String suffix)
        {
            this.width = width;
            this.height = height;
            this.fontSize = fontSize;
            this.suffix = suffix;
        }
    };
    
    /**
     * Generates images of various sizes for the specified phone directory.
     * 
     * @param directory
     *      The directory of a single phone which should have images named
     * in the format [1-9].jpg.
     *
     * @return 
     *      The number of images that have been processed for the specified phone.
     */
    public static int generateImages(File directory)
    {
        if(!directory.isDirectory())
        {
            System.out.println("ImageManager: WARNING: " + directory.getAbsolutePath() 
                    + " is not a directory!");
            return -1;
        }
        
        int count = 0;
        for(File image : directory.listFiles())
        {
            if(image.getName().matches("[0-9]+\\.(jpg|jpeg|JPG|JPEG)"))
            {
                if(createImage(image))
                    count++;
            }
        }
        
        return count;
    }
    
    private static boolean createImage(File source)
    {
        System.out.println("Creating scaled versions of: " + source);
        String name = source.getName();
        
        File original = new File(source.getParent(), '.' + source.getName().split("\\.")[0] + ".jpg");
        
        // seed the original (or new original) based on the timestamps on the files
        // (Originals are saved as .<num>.jpg. These don't get watermarked)
        if(original.exists() && original.lastModified() > source.lastModified())
        {
            // do nothing here, the original is the newer file
            System.out.println("Original file exists and is newer. Using original.");
        }
        else
        {
            // source is the new file. delete original if exists
            if(original.exists())
                original.delete();
            
            // and copy to "original" as a backup
            System.out.println("Backing up the original version.");
            source.renameTo(original);
        }
        
        long start = System.currentTimeMillis();
        
        // Temp Add
        return true;
        // end temp add
/*
        try{
            BufferedImage image = ImageIO.read(original);
            int imgHeight = image.getHeight();
            int imgWidth = image.getWidth();

            ImageScale[] scales;
            if(name.startsWith("1."))
            {
                scales = new ImageScale[4];
                scales[0] = ImageScale.THUMBNAIL;
                scales[1] = ImageScale.SMALL;
                scales[2] = ImageScale.MEDIUM;
                scales[3] = ImageScale.LARGE;
            }
            else
            {
                scales = new ImageScale[2];
                scales[0] = ImageScale.THUMBNAIL;
                scales[1] = ImageScale.LARGE;
            }
            
            for(ImageScale scale : scales)
            {
                float ratio = (float)imgWidth/(float)imgHeight;
                int width, height;
                if(ratio > 1)
                {
                    width  = scale.height;
                    height = scale.width;
                }
                else
                {
                    width = scale.width;
                    height = scale.height;
                }

                Image scaledImage;
                if(scale == ImageScale.THUMBNAIL)
                {
                    int min = Math.min(imgWidth, imgHeight);
                    int offset = Math.abs(imgWidth - imgHeight) / 2;
                    int offsetX = offset * (int)ratio;
                    int offsetY = offset * (1 - (int)ratio);
                    
                    scaledImage = image.getSubimage(offsetX, offsetY, min, min);
                    
                    scaledImage = scaledImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                }
                // special handling for LARGE.. Use the original size of the image
                else if(scale == ImageScale.LARGE)
                {
                    // let the large images be the same size as the original
                    width = image.getWidth();
                    height = image.getHeight();
                    
                    // and don't scale
                    scaledImage = image;
                }
                else
                    scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
                Graphics2D g = result.createGraphics();
                
                g.drawImage(scaledImage, 0, 0, null);
                
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
                
                g.setFont(new Font("Arial", Font.BOLD, scale.fontSize));
                
                // determine width and height of the url
                FontMetrics fm = g.getFontMetrics();
                int watermarkHeight = fm.getHeight();
                int watermarkWidth = 0;
                for(byte character : siteUrl.getBytes())
                {
                    watermarkWidth += fm.charWidth(character);
                }
                
                int startX = (width - watermarkWidth) / 2;
                int startY = (height - (watermarkHeight / 2));
                System.out.println("Width=" + width + ", WMWidth=" + watermarkWidth + ", StartX=" + startX);
                System.out.println("Height=" + height + ", WMHeight=" + watermarkHeight + ", StartY=" + startY);
                
                g.setColor(Color.black);
                g.drawString(siteUrl, startX, startY);
//                g.drawString(siteUrl, 32, 32);
                
                g.setColor(Color.white);
//                g.drawString(siteUrl, 31, 31);
                g.drawString(siteUrl, startX-1, startY-1);
                g.dispose();
                
                File destination = new File(source.getParent(), name.split("\\.")[0] + scale.suffix + ".jpg");
                ImageIO.write(result, "JPG", destination);
                
                // Set the last modified times. Give 60sec bias to the original in case
                // we run this again on the same files.
                destination.setLastModified(System.currentTimeMillis());
                original.setLastModified(System.currentTimeMillis() + 60000);
                
                System.out.println("   And saving to " + destination + " Scale Time: " + (System.currentTimeMillis() - start));
                
            }
            return true;
        }catch (Exception e) {
                e.printStackTrace();
                return false;
        }
        */

    }
    
    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();
        File dir = new File("C:\\Users\\Steve\\Documents\\NetBeansProjects\\MVTel\\build\\web\\phones\\Wall Phones\\iphone4\\");

            int count = generateImages(dir);

    }
}
