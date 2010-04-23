package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author marcus
 */
public class RegGrowImage extends Image implements MouseListener
{
	private RegGrowDelegate _delegate;

	public RegGrowImage(RegGrowDelegate delegate)
	{
		this(delegate, null);
	}

	public RegGrowImage(RegGrowDelegate delegate, BufferedImage image)
	{
		super(image);
		_delegate = delegate;
		//setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e)
	{
		// assumes a scalling of one-to-one
		_delegate.pointChosen(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
