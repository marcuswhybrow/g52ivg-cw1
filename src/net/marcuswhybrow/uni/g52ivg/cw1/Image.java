
package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author marcus
 */
public class Image extends JPanel
{
	private BufferedImage _image;
	private Dimension _originalSize = new Dimension();

	public Image()
	{
		this(null);
	}

	public Image(BufferedImage image)
	{
		setImage(image);
	}

	public void setImage(BufferedImage image)
	{
		if(image != null)
		{
			_image = image;
			_originalSize = new Dimension(_image.getWidth(), _image.getHeight());
			
			setPreferredSize(_originalSize);
			setMaximumSize(_originalSize);
			setMinimumSize(_originalSize);

			// Reflows the layout with the new image
			setVisible(false);
			setVisible(true);

//			System.out.println(_originalSize.toString());
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		if(_image != null)
			g.drawImage(_image, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	public void setPreferredHeight(int height)
	{
//		System.out.println("Set height to " + height);

		if (height > _originalSize.height)
			height = _originalSize.height;

		double proportion = (double) height / (double) _originalSize.height;

		super.setMinimumSize(new Dimension((int) (proportion * _originalSize.width), height));
		super.setPreferredSize(new Dimension((int) (proportion * _originalSize.width), height));
		super.setMaximumSize(new Dimension((int) (proportion * _originalSize.width), height));

		System.out.println(getPreferredSize().toString());
	}

	public void setPreferredWidth(int width)
	{
//		System.out.println("Set width to " + width);

		if (width > _originalSize.width)
			width = _originalSize.width;

		double proportion = (double) width / (double) _originalSize.width;

		super.setMinimumSize(new Dimension(width, (int) (proportion * _originalSize.height)));
		super.setPreferredSize(new Dimension(width, (int) (proportion * _originalSize.height)));
		super.setMaximumSize(new Dimension(width, (int) (proportion * _originalSize.height)));

		System.out.println(getPreferredSize().toString());
	}

	@Override
	public void setPreferredSize(Dimension d)
	{
		double wProp = (double) d.width / (double) _originalSize.width;
		double hProp = (double) d.height /(double)  _originalSize.height;

//		System.out.println("prefered size: " + wProp + " " + hProp);

		if (wProp > _originalSize.width || hProp > _originalSize.height) return;

		if (wProp < hProp)
			setPreferredWidth(d.width);
		else if (hProp < wProp)
			setPreferredHeight(d.height);
		else
		{
			super.setMinimumSize(d);
			super.setPreferredSize(d);
			super.setMaximumSize(d);
		}
	}

	public boolean hasImage()
	{
		return _image != null;
	}
}
