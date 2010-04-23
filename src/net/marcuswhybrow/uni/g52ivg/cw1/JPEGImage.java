
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

		int[] values = this.getHistogram('a');

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
					output.setRGB(x, y, getRed(x, y), getGreen(x, y), getBlue(x, y));
				else
					output.setRGB(x, y, 255, 255, 255);
			}
		
		return output;
	}


	public JPEGImage regGrow(int x, int y, int sensitivity)
	{
		JPEGImage output = new JPEGImage(getWidth(), getHeight());
		int meanIntensity;

		List<Pixel> region = new LinkedList<Pixel>();
		List<Pixel> newlyAdded = new LinkedList<Pixel>();
		List<Pixel> previouslyAdded = new LinkedList<Pixel>();

		Iterator newPixels, regionPixels;
		Pixel newPixel, neighbouringPixel, pixel;

		// Set the intial pixel
		region.add(new Pixel(x, y, getRed(x, y), getGreen(x, y), getBlue(x, y)));
		newlyAdded.add(region.get(0));

		while(!newlyAdded.isEmpty())
		{
			// Copy the list of newly added pixels, and rest the newlyAdded list
			previouslyAdded = newlyAdded;
			newlyAdded = new LinkedList<Pixel>();

			// Calculate the existing mean intensity of the region
			regionPixels = region.iterator();
			meanIntensity = 0;

			while(regionPixels.hasNext())
				meanIntensity += ((Pixel) regionPixels.next()).getIntensity();

			meanIntensity /= region.size();


			// Check the candidate neighbouring pixels against that intensity
			newPixels = previouslyAdded.iterator();

			while(newPixels.hasNext())
			{
				newPixel = (Pixel) newPixels.next();

				// Find the new candidate pixels
				for(int i = newPixel.getX() - 1; i <= newPixel.getX() + 1; i++)
					for(int j = newPixel.getY() - 1; j <= newPixel.getY() + 1; j++)
					{
						try
						{
							neighbouringPixel = new Pixel(i, j, getRed(i, j), getGreen(i, j), getRed(i, j));
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
							continue;
						}

						if(!region.contains(neighbouringPixel) && Math.abs(neighbouringPixel.getIntensity() - meanIntensity) <= sensitivity)
						{
							region.add(neighbouringPixel);
							newlyAdded.add(neighbouringPixel);
						}
					}
			}
		}

		regionPixels = region.iterator();

		while(regionPixels.hasNext())
		{
			pixel = (Pixel) regionPixels.next();
			output.setRGB(pixel.getX(), pixel.getY(), getRed(pixel.getX(), pixel.getY()), getGreen(pixel.getX(), pixel.getY()), getBlue(pixel.getX(), pixel.getY()));
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
		return (getRed(x, y) + getGreen(x, y) + getBlue(x, y)) / 3;
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
	 * @param value the new red value at the given coordinates
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
	 * @param value the new green value at the given coordinates
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
	 * @param value the new blue value at the given coordinates
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

	private int[] getHistogram(char colour)
	{
		int[] values = new int[256];

		// initialise values
		for (int i = 0; i < 256; i++)
			values[i] = 0;

		// add up the different pixel values into the histogram
		for (int x = 0; x < this.getWidth(); x++)
			for (int y = 0; y < this.getHeight(); y++)
				switch (colour)
				{
					case 'a':
						values[(this.getRed(x, y) + this.getGreen(x, y) + this.getBlue(x, y)) / 3]++;
						break;
					case 'r':
						values[this.getRed(x, y)]++;
						break;
					case 'g':
						values[this.getGreen(x, y)]++;
						break;
					case 'b':
						values[this.getBlue(x, y)]++;
						break;
				}

		return values;
	}

	private static class Pixel
	{
		private int _x;
		private int _y;
		private int _intensity;

		public Pixel(int x, int y, int r, int g, int b)
		{
			_x = x;
			_y = y;

			_intensity = (r + g + b) / 3;
		}

		public int getX()
		{
			return _x;
		}

		public int getY()
		{
			return _y;
		}

		public int getIntensity()
		{
			return _intensity;
		}

		@Override
		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		public boolean equals(Object o)
		{
			Pixel otherPixel = (Pixel) o;

			if(this.getX() == otherPixel.getX() && this.getY() == otherPixel.getY())
				return true;
			else
				return false;
		}

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 89 * hash + this._x;
			hash = 89 * hash + this._y;
			return hash;
		}
	}
}
