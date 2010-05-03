package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

/**
 *
 * @author marcus
 */
public class HistogramFrame extends JFrame implements OverlayDelegate, HistogramDelegate
{
	private HistogramImage _histogram;
	private Overlay _overlay;
	private MainFrame _frame;

	public HistogramFrame(MainFrame frame)
	{
		super("Histogram Segmentation");

		_frame = frame;

		setResizable(false);
		setLocationRelativeTo(frame);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		_histogram = new HistogramImage(_frame.getHistory().peek().getHistogram().getBufferedImage(), this);
		_histogram.setPreferredSize(new Dimension(512, 256));
		add(_histogram);

		_overlay = new Overlay(this);
		_overlay.setMessage("Choose a region by clicking and dragging");

		_overlay.getOkButton().setEnabled(false);

		add(_overlay);

		pack();
		setVisible(true);
	}

	public void pressedOk()
	{
		System.out.println("ok pressed");
		History history = _frame.getHistory();
		history.push(history.peek().applyHistSeg(_histogram.getMin(), _histogram.getMax()));
		_frame.reloadImage();
		this.dispose();
	}

	public void pressedCancel()
	{
		System.out.println("cancel pressed");
		this.dispose();
	}

	public void regionSelected()
	{
		_overlay.getOkButton().setEnabled(true);
	}

	public HistogramImage getHistogram()
	{
		return _histogram;
	}
}
