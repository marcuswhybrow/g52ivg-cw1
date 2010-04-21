
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.*;
import java.awt.image.*;
import java.io.*;

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

	private BufferedImage _img;
	private String _fileName;
	private String _path;

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
		_path = path;
		JPEGImageDecoder jpegDec = JPEGCodec.createJPEGDecoder(new FileInputStream(_path));
		_img = jpegDec.decodeAsBufferedImage();
	}

	@Override
	public JPEGImage clone()
	{
		JPEGImage image = new JPEGImage(this.getWidth(), this.getHeight());

		image._path = this.getPath();
		image._fileName = this.getFileName();
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

	public String getPath()
	{
		return _path;
	}

	public String getFileName()
	{
		return _fileName;
	}

	public JPEGImage meanFilter(int filterWidth)
	{
		JPEGImage output = new JPEGImage(this.getWidth(), this.getHeight());
		output._path = this.getPath();
		output._fileName = this.getFileName();

		for(int x = 0; x < this.getWidth(); x++)
			for(int y = 0; y < this.getHeight(); y++)
			{

				int red = meanConvMask(x, y, filterWidth, 'r');
				int green = meanConvMask(x, y, filterWidth, 'g') ;
				int blue = meanConvMask(x, y, filterWidth, 'b');

				output.setRGB(x, y, red, green, blue);
			}

		return output;
	}

	private int meanConvMask(int x, int y, int filterWidth, char colour)
	{
		int filterRadius;
		double sum = 0;
		int values = 0;

		filterRadius = (filterWidth - 1) / 2;

		for(int u = x - filterRadius; u <= x + filterRadius; u++)
			for(int v = y - filterRadius; v <= y + filterRadius; v++)
			{
				try
				{
					switch(colour)
					{
						case 'r':
							sum += this.getRed(u, v);
							values++;
							break;
						case 'g':
							sum += this.getGreen(u, v);
							values++;
							break;
						case 'b':
							sum += this.getBlue(u, v);
							values++;
							break;
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					// If the pixel does not exist don't add it to the sum.
					continue;
				}
			}

		return (int) (sum / values);
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
}
