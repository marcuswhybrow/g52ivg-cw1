
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This class provides basic image manupulation routines.
 * This class is essentially a wrapper around a <tt>BufferedImage</tt>, and
 * provides simple methods for reading and writing images in JPEG format,
 * accessing pixel values, and creating new images.
 * </p>
 * <p>
 * There is no range checking either on image coordinates or on pixel
 * colour values. Entering values outside of the allowed ranges may
 * generate runtime exceptions, or result in garbled images.
 * </p>
 *
 * @author <a href="mailto:smx@cs.nott.ac.uk">Steven Mills</a>
 * @version 1.0 (Jan 8 2003)
 */
public class JPEGImage implements Cloneable
{
	private static final int HISTOGRAM_HEIGHT = 128;
	private BufferedImage _img;

	/**
	 * Default image constructor.
	 *
	 * Creates an empty JPEGImage. Since this object does not actually
	 * contain an image, any attempts to access the image (through get
	 * or set methods or write) will result in runtime exceptions.
	 */
	public JPEGImage()
	{
		
	}

	/**
	 * Blank image constructor.
	 *
	 * Creates a new image with the given dimensions, and
	 * all pixels in the image are set to be black. The dimensions
	 * should be positive integers.
	 *
	 * @param width the width of the image in pixels
	 * @param height the height of the image in pixels
	 */
	public JPEGImage(int width, int height)
	{
		_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				_img.setRGB(x, y, 0xff000000);
	}

	public JPEGImage(String path) throws IOException, ImageFormatException
	{
		JPEGImageDecoder jpegDec = JPEGCodec.createJPEGDecoder(new FileInputStream(path));
		_img = jpegDec.decodeAsBufferedImage();
	}

	@Override
	public JPEGImage clone()
	{
		JPEGImage image = new JPEGImage(this.getWidth(), this.getHeight());
		for(int x = 0; x < image.getWidth(); x++)
			for(int y = 0; y < image.getHeight(); y++)
			{
				int red = image.getRed(x, y);
				int green = image.getGreen(x, y);
				int blue = image.getBlue(x, y);

				this.setRGB(x, y, red, green, blue);
			}

		return image;
	}

	public BufferedImage getBufferedImage()
	{
		return _img;
	}

	public JPEGImage meanFilter(int filterRadius)
	{
		JPEGImage output = new JPEGImage(this.getWidth(), this.getHeight());

		int redSum, greenSum, blueSum;
		int reds, greens, blues;

		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
			{
				redSum = greenSum = blueSum = 0;
				reds = greens = blues = 0;
				
				for(int u = x - filterRadius; u <= x + filterRadius; u++)
					for(int v = y - filterRadius; v <= y + filterRadius; v++)
					{
						try
						{
							redSum += this.getRed(u, v);
							reds++;

							greenSum += this.getGreen(u, v);
							greens++;

							blueSum += this.getBlue(u, v);
							blues++;
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
							// If the pixel does not exist don't add it to the sum.
							continue;
						}
					}

				output.setRGB(x, y, redSum/reds, greenSum/greens, blueSum/blues);
			}

		return output;
	}

	public JPEGImage medianFilter(int filterRadius)
	{
		JPEGImage output = new JPEGImage(this.getWidth(), this.getHeight());

		int possibleSize = (int) Math.pow(filterRadius * 2 + 1, 2);
		int i;

		int[] reds, greens, blues;

		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
			{
				reds = new int[possibleSize];
				greens = new int[possibleSize];
				blues = new int[possibleSize];

				i = 0;
				
				for(int u = x - filterRadius; u <= x + filterRadius; u++)
					for(int v = y - filterRadius; v <= y + filterRadius; v++)
					{
						try
						{
							reds[i] = this.getRed(u, v);
							greens[i] = this.getGreen(u, v);
							blues[i] = this.getBlue(u, v);

							i++;
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
							continue;
						}
					}
				Arrays.sort(reds);
				Arrays.sort(greens);
				Arrays.sort(blues);

				output.setRGB(x, y, reds[possibleSize - i + (i / 2)], greens[possibleSize - i + (i / 2)], blues[possibleSize - i + (i / 2)]);
			}
		
		return output;
	}

	public JPEGImage getHistogram()
	{
		int max = 0;
		JPEGImage output;
		int[] values;

		values = this.getRGBHistogram();

		// Get the maximum value in the histogram
		for (int i = 0; i < 256; i++)
			if (values[i] > max)
				max = values[i];

		output = new JPEGImage(256, HISTOGRAM_HEIGHT);

		// Fill output image with white
		for (int x = 0; x < output.getWidth(); x++)
			for(int y = 0; y < output.getHeight(); y++)
				output.setRGB(x, y, 255, 255, 255);

		// Colour in pixels of the output image
		for (int x = 0; x < 256; x++)
			for (int u = HISTOGRAM_HEIGHT - 1; u > HISTOGRAM_HEIGHT - (int) (((double) values[x] / (double) max) * HISTOGRAM_HEIGHT); u--)
				output.setRGB(x, u, 0, 0, 0);

		return output;
	}

	public JPEGImage applyHistSeg(int min, int max)
	{
		JPEGImage output = new JPEGImage(this.getWidth(), this.getHeight());

		int intensity;
		
		for (int x = 0; x < this.getWidth(); x++)
			for (int y = 0; y < this.getHeight(); y++)
			{
				intensity = getIntensity(x, y);
				if ((max > min && (intensity >= min && intensity <= max)) || (max < min && (intensity >= min || intensity <= max)))
					output.setRGB(x, y, 0x00ffffff);

//					output.setRGB(x, y, getRed(x, y), getGreen(x, y), getBlue(x, y));
//				else
//					output.setRGB(x, y, 255, 255, 255);
			}
		
		return output;
	}


	public JPEGImage regGrow(int x, int y, int sensitivity)
	{
		JPEGImage output = new JPEGImage(getWidth(), getHeight());
		int meanIntensity;

		HashSet<Point> region = new HashSet<Point>();
		HashSet<Point> newlyAdded = new HashSet<Point>();
		HashSet<Point> previouslyAdded = new HashSet<Point>();

		Point newPixel, neighbouringPixel, pixel;

		// Set the intial pixel
		Point point = new Point(x, y);
		region.add(point);
		newlyAdded.add(point);

		while(!newlyAdded.isEmpty())
		{
			// Copy the list of newly added pixels, and rest the newlyAdded list
			previouslyAdded = newlyAdded;
			newlyAdded = new HashSet<Point>();

			// Calculate the existing mean intensity of the region
			meanIntensity = 0;

			for(Object o : region)
			{
				point = (Point) o;
				meanIntensity += this.getIntensity(point.x, point.y);
			}

			meanIntensity /= region.size();
			
			// Check the candidate neighbouring pixels against that intensity
			for(Object o : previouslyAdded)
			{
				newPixel = (Point) o;

				// Find the new candidate pixels
				for(int i = newPixel.x - 1; i <= newPixel.x + 1; i++)
					for(int j = newPixel.y - 1; j <= newPixel.y + 1; j++)
					{
						neighbouringPixel = new Point(i, j);

						try
						{
							if(!region.contains(neighbouringPixel) && Math.abs(getIntensity(neighbouringPixel.x, neighbouringPixel.y) - meanIntensity) <= sensitivity)
							{
								region.add(neighbouringPixel);
								newlyAdded.add(neighbouringPixel);
							}
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
							continue;
						}
					}
			}
		}

		for(Object o : region)
		{
			pixel = (Point) o;
			output.setRGB(pixel.x, pixel.y, 0x00ffffff);
		}

		return output;
	}

	public JPEGImage histEq()
	{
		JPEGImage output = new JPEGImage(getWidth(), getHeight());

		int[] values = getBrightnessHistogram();

		// Create the Cumulative Distribution Function (array)
		// and find the min and max values
		int[] cdf = new int[256];
		int min = -1;
		cdf[0] = values[0];
		for (int i = 1; i < values.length; i++)
		{
			cdf[i] = values[i] + cdf[i-1];

			if (min == -1 && cdf[i] > 0)
				min = cdf[i];
		}
		
		// Create the lookup table
		float[] lookup = new float[256];
		int numPixels = getWidth() * getHeight();
		// This is the complicated formula
		for (int i = 0; i < cdf.length; i++)
		{
			lookup[i] = (float) (cdf[i] - min) / (float) (numPixels - min);
		}

		float[] hsb;
		for (int x = 0; x < output.getWidth(); x++)
			for (int y = 0; y < output.getHeight(); y++)
			{
				hsb = Color.RGBtoHSB(getRed(x, y), getGreen(x, y), getBlue(x, y), null);

				// Set the new brightness value for this pixel
				hsb[2] = lookup[Math.round(hsb[2] * 255)];

				// Set the pixels new values
				output.setRGB(x, y, Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
			}

		return output;
	}

	/**
	 * Writes an image to a file in JPEG format.
	 *
	 * Writing to a file that does not exist, or which is protected
	 * will generate an exception
	 *
	 * @param filename the file to write the image to
	 * @throws IOException indicates a general problem reading the file
	 */
	public void write(String filename) throws IOException
	{
		OutputStream ostream = new FileOutputStream(filename);
		JPEGImageEncoder jpegEnc = JPEGCodec.createJPEGEncoder(ostream);
		jpegEnc.encode(_img);
	}

	/**
	 * Returns the height of the JPEGImage.
	 *
	 * @return the height of the image in pixels
	 */
	public int getHeight()
	{
		return _img.getHeight();
	}

	/**
	 * Returns the width of the JPEGImage.
	 * 
	 * @return the width of the image in pixels
	 */
	public int getWidth()
	{
		return _img.getWidth();
	}

	/**
	 * Returns the red value of the image at the given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The red value is returned as an
	 * integer in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @return the red value at the given coordinates
	 */
	public int getRed(int x, int y)
	{
		return (_img.getRGB(x, y) & 0x00ff0000) >> 16;
	}

	/**
	 * Returns the green value of the image at the given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The green value is returned as an
	 * integer in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @return the green value at the given coordinates
	 */
	public int getGreen(int x, int y)
	{
		return (_img.getRGB(x, y) & 0x0000ff00) >> 8;
	}

	/**
	 * Returns the blue value of the image at the given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The blue value is returned as an
	 * integer in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @return the blue value at the given coordinates
	 */
	public int getBlue(int x, int y)
	{
		return (_img.getRGB(x, y) & 0x000000ff);
	}

	public int getIntensity(int x, int y)
	{
		return (int) (0.2989 * (float) getRed(x, y) + 0.5870 * (float) getGreen(x, y) + 0.1140 * (float) getBlue(x, y));
	}

	/**
	 * Sets the red value of the image at the given coordinates.
	 *
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The red value should be an
	 * integer in the range [0,255].
	 * 
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @param hue the new red value at the given coordinates
	 */
	public void setRed(int x, int y, int value)
	{
		_img.setRGB(x, y, (_img.getRGB(x, y) & 0xff00ffff) | (value << 16));
	}

	/**
	 * Sets the green value of the image at the given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The green value should be an
	 * integer in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @param hue the new green value at the given coordinates
	 */
	public void setGreen(int x, int y, int value)
	{
		_img.setRGB(x, y, (_img.getRGB(x, y) & 0xffff00ff) | (value << 8));
	}

	/**
	 * Sets the blue value of the image at the given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The blue value should be an
	 * integer in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @param hue the new blue value at the given coordinates
	 */
	public void setBlue(int x, int y, int value)
	{
		_img.setRGB(x, y, (_img.getRGB(x, y) & 0xffffff00) | value);
	}

	public void setIntensity(int x, int y, int i)
	{
		setRGB(x, y, i, i, i);
	}

	/**
	 * Sets the red, green, and blue values of the image at the
	 * given coordinates.
	 * 
	 * The coordinates should be non-negative and less than the width (x)
	 * or height (y) of the image. The colour values should be
	 * integers in the range [0,255].
	 *
	 * @param x the horizontal coordinate of the pixel
	 * @param y the vertical coordinate of the pixel
	 * @param r the new red value at the given coordinates
	 * @param g the new green value at the given coordinates
	 * @param b the new blue value at the given coordinates
	 */
	public void setRGB(int x, int y, int r, int g, int b)
	{
		_img.setRGB(x, y, 0xff000000 | (r << 16) | (g << 8) | b);
	}

	public void setRGB(int x, int y, int rgb)
	{
		_img.setRGB(x, y, rgb);
	}

	private int[] getRGBHistogram()
	{
		int[] values = new int[256];

		// initialise values
		for (int i = 0; i < 256; i++)
			values[i] = 0;

		// add up the different pixel values into the histogram
		for (int x = 0; x < this.getWidth(); x++)
			for (int y = 0; y < this.getHeight(); y++)
				values[(int) (0.2989 * (float) this.getRed(x, y) + 0.5870 * (float) this.getGreen(x, y) + 0.1140 * (float) this.getBlue(x, y))]++;

		return values;
	}

	private int[] getBrightnessHistogram()
	{
		int[] values = new int[256];

		// initialise values
		for (int i = 0; i < 256; i++)
			values[i] = 0;

		// add up the different pixel values into the histogram
		for (int x = 0; x < this.getWidth(); x++)
			for (int y = 0; y < this.getHeight(); y++)
				values[Math.round(Color.RGBtoHSB(this.getRed(x, y), this.getGreen(x, y), this.getBlue(x, y), null)[2] * 255)]++;

		return values;
	}
}
