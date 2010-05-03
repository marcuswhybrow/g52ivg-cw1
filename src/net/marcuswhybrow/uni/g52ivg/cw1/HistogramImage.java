package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author marcus
 */
public class HistogramImage extends Image implements MouseListener, MouseMotionListener
{
	private HistogramDelegate _delegate;

	private Line2D _startLine = null;
	private Line2D _endLine = null;
	private Rectangle _area = null, _area2 = null;

	private enum State { IDLE, FINDING_START, FINDING_END, DONE };
	private State _state = State.IDLE;

	public HistogramImage(BufferedImage i, HistogramDelegate delegate)
	{
		super(i);
		addMouseListener(this);
		addMouseMotionListener(this);
		_delegate = delegate;
	}

	public int getMin()
	{
		double sizeMult = (double) this.getWidth() / (double) 256;
		return (int) (_startLine.getX1() / sizeMult);
	}

	public int getMax()
	{
		double sizeMult = (double) this.getWidth() / (double) 256;
		return (int) (_endLine.getX1() / sizeMult);
	}

	public void mouseClicked(MouseEvent e)
	{
		// pressed + released
	}

	public void mousePressed(MouseEvent e)
	{
		System.out.println("pressed");

		_area2 = null;

		switch(_state)
		{
			case DONE:
				_startLine.setLine(e.getX(), 0, e.getX(), this.getHeight());
			case FINDING_START:
				_state = State.FINDING_END;
				_endLine = new Line2D.Double(e.getX(), 0, e.getX(), this.getHeight());
				_area = new Rectangle((int) _startLine.getX1(), 0, (int) _endLine.getX1() - (int) _startLine.getX1(), this.getHeight());
				this.repaint();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (_state == State.FINDING_END)
		{
			_state = State.DONE;
			_delegate.regionSelected();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		if (_state == State.IDLE)
		{
			_state = State.FINDING_START;
			_startLine = new Line2D.Double(e.getX(), 0, e.getX(), this.getHeight());
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(new Color(255, 0, 0, 80));

		if (_area != null)
			g2.fill(_area);

		if (_area2 != null)
			g2.fill(_area2);

		g2.setColor(new Color(255, 0, 0, 150));
		g2.setStroke(new BasicStroke(1));

		if (_startLine != null)
			g2.draw(_startLine);

		if (_endLine != null)
			g2.draw(_endLine);
	}

	public void mouseDragged(MouseEvent e)
	{
		if (_state == State.FINDING_END)
		{
			_endLine.setLine(e.getX(), 0, e.getX(), this.getHeight());

			int difference = (int) _endLine.getX1() - (int) _startLine.getX1();
			if (difference >= 0)
			{
				_area.setRect(_startLine.getX1(), 0, difference, this.getHeight());
				_area2 = null;
			}
			else
			{
				_area.setRect(0, 0, _endLine.getX1(), this.getHeight());
				
				if (_area2 == null)
					_area2 = new Rectangle();

				_area2.setRect(_startLine.getX1(), 0, this.getWidth() - _startLine.getX1(), this.getHeight());
			}
			this.repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		if (_state == State.FINDING_START)
		{
			_startLine.setLine(e.getX(), 0, e.getX(), this.getHeight());
			this.repaint();
		}
	}

	@Override
	public void setImage(BufferedImage image)
	{
		super.setImage(image);

		Dimension d = new Dimension(512, 256);
		setMaximumSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
	}
}
