
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.ImageFormatException;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author marcus
 */
public class MainFrame extends JFrame implements ComponentListener, OverlayDelegate, RegGrowDelegate
{
	private History _history;
	private RegGrowImage _image;

	private String _applicationName;
	private String _fileName;
	private MenuBar _menuBar;

	private Box _horizontalBox;
	private Box _verticalBox;

	private HistogramImage _histogram;
	private HistogramFrame _histogramFrame;

	private Overlay _cancelOverlay;

	private enum State { IDLE, MEAN_FILTER, MEDIAN_FILTER, HIST_SEG, HIST_EQ, REG_GROW };
	private State _state;

	public MainFrame()
	{
		super("G52IVG Coursework");

		// Get the name set using super(String str) above
		_applicationName = this.getTitle();

		// initialise the history and image instances
		_history = new History();
		_image = new RegGrowImage(this);
		_cancelOverlay = new Overlay(this);

		// Define the layout shceme
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

		// Add the Custom JMenuBar to the this panel
		setJMenuBar(new MenuBar(this));
		_menuBar = (MenuBar) getJMenuBar();

		// Set the initial size of the window
		setSize(600, 400);

		// Add the this class as the listener for this panel
		addComponentListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_verticalBox = Box.createVerticalBox();
		_verticalBox.add(Box.createVerticalGlue());
		_verticalBox.add(_image);
		_verticalBox.add(Box.createVerticalGlue());

//		_verticalBox.setBorder(new LineBorder(Color.BLACK));

		_horizontalBox = Box.createHorizontalBox();
		_horizontalBox.add(Box.createHorizontalGlue());
		_horizontalBox.add(_verticalBox);
		_horizontalBox.add(Box.createHorizontalGlue());

//		_horizontalBox.setBorder(new LineBorder(Color.BLACK));

		// Make the overlay invisible untill needed
		_cancelOverlay.setVisible(false);

		add(_horizontalBox);
		add(_cancelOverlay);

		// Add the image to this panel which will display the working image.
//		_image.setBorder(new LineBorder(Color.BLACK));

		setLocationRelativeTo(null);

		_state = State.IDLE;

		// Finally make the MainFrame visible
		setVisible(true);
	}

	public JPEGImage getImage()
	{
		return _history.peek();
	}

	public History getHistory()
	{
		return _history;
	}

	public void open() throws IOException, ImageFormatException
	{
		JFileChooser c = new JFileChooser();
		if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			_fileName = c.getSelectedFile().getAbsolutePath();
			_history.clear();
			_history.push(new JPEGImage(_fileName));
			_history.setCurrentImageAsSaved();
			reloadImage();
		}

		_menuBar.getButton("save").setEnabled(true);
		_menuBar.getButton("save").setEnabled(true);
		_menuBar.getButton("saveAs").setEnabled(true);
		_menuBar.getButton("meanFilter").setEnabled(true);
		_menuBar.getButton("medianFilter").setEnabled(true);
		_menuBar.getButton("histEq").setEnabled(true);
		_menuBar.getButton("histSeg").setEnabled(true);
		_menuBar.getButton("regGrow").setEnabled(true);

		//this.pack();
	}

	public void save() throws IOException
	{
		_history.peek().write(_fileName);
		_history.setCurrentImageAsSaved();
		this.reloadImage();
	}

	public void saveAs() throws IOException
	{
		JFileChooser c = new JFileChooser();
		if (c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			_fileName = c.getSelectedFile().getAbsolutePath();
			this.getImage().write(_fileName);
			_history.setCurrentImageAsSaved();
			this.reloadImage();
		}
	}

	public void meanFilter()
	{
		_state = State.MEAN_FILTER;
		int value;
		
		String input = JOptionPane.showInputDialog(this, "Filter Radius: ", "Filter Radius", JOptionPane.INFORMATION_MESSAGE);
		if(input == null) return;

		try
		{
			value = Integer.parseInt(input);
			_history.push(_history.peek().meanFilter(value));
			this.reloadImage();
		}
		catch(NumberFormatException e)
		{
			showError("That's not a number.");
		}

		_state = State.IDLE;
	}

	public void medianFilter()
	{
		_state = State.MEDIAN_FILTER;
		int value;
		
		String input = JOptionPane.showInputDialog(this, "Filter Radius: ", "Filter Radius", JOptionPane.INFORMATION_MESSAGE);
		if(input == null) return;

		try
		{
			value = Integer.parseInt(input);
			_history.push(_history.peek().medianFilter(value));
			this.reloadImage();
		}
		catch(NumberFormatException e)
		{
			showError("That's not a number.");
		}

		_state = State.IDLE;
	}

	public void histEq()
	{
		_state = State.HIST_EQ;

		_history.push(_history.peek().histEq());
		reloadImage();

		_state = State.IDLE;
	}

	public void histSeg()
	{
		_state = State.HIST_SEG;
		_cancelOverlay.setVisible(false);
		_image.setCursor(null);

		_histogramFrame = new HistogramFrame(this);

		_state = State.IDLE;
	}

	public void regGrow()
	{
		_state = State.REG_GROW;

		if (_histogramFrame != null)
			_histogramFrame.dispose();

		//_image = new RegGrowImage(this, _history.peek().getBufferedImage());
		_image.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		//reloadImage();
		
		_cancelOverlay.setMessage("Pick a location to begin region growing:");
		_cancelOverlay.setHasOkButton(false);
		//getRootPane().setDefaultButton(_cancelOverlay.getDefaultButton());
		_cancelOverlay.setVisible(true);
	}

	/**
	 * Region growing delegate method, called when a point has been chosen
	 * for region growing
	 *
	 * @param x The pixel's x value
	 * @param y The pixel's y value
	 */
	public void pointChosen(int x, int y)
	{
		if (_state == State.REG_GROW)
		{
			_image.setCursor(null);

			String input = JOptionPane.showInputDialog(this, "Enter Sensitivity (0-255): ", "Region Growing Sensitivity", JOptionPane.INFORMATION_MESSAGE);
			if(input != null)
			{
				try
				{
					int sensitivity = Integer.parseInt(input);
					_history.push(_history.peek().regGrow(x, y, sensitivity));
					reloadImage();
				}
				catch(NumberFormatException e)
				{
					showError("That's not a number.");
				}
			}

			_cancelOverlay.setVisible(false);
			_state = State.IDLE;
		}
	}

	public void undo()
	{
		_history.undo();
		reloadImage();
	}

	public void redo()
	{
		_history.redo();
		reloadImage();
	}

	public void history()
	{
		System.out.println("history");
	}

	public void histogram()
	{
		System.out.println("histogram");
	}
	
	

	public void componentResized(ComponentEvent e)
	{
		
	}

	public void componentMoved(ComponentEvent e)
	{
//		JPEGImage image = _history.peek();
//
//		if(!_image.hasImage()) return;
//
//		//System.out.println(" Image Width: " + _image.getWidth() + " Panel Width: " + this.getContentPane().getWidth());
//
//		Container panel = getContentPane();
//
//		if(_image.getWidth() > panel.getWidth() || (_image.getWidth() < this.getWidth() && _image.getWidth() < image.getWidth()))
//			_image.setPreferredWidth(panel.getWidth());
//
////		if(_image.getHeight() > panel.getHeight() || (_image.getHeight() < this.getHeight() && _image.getHeight() < image.getHeight()))
////			_image.setPreferredHeight(panel.getHeight());

	}

	public void componentShown(ComponentEvent e)
	{
		System.out.println("shown");
	}

	public void componentHidden(ComponentEvent e)
	{
		System.out.println("hidden");
	}

	public void pressedOk()
	{
		// this should not be called
	}

	public void pressedCancel()
	{
		_image.setCursor(null);
		_cancelOverlay.setVisible(false);
		_state = State.IDLE;
	}


	public void reloadImage()
	{
		// Get the latest image
		JPEGImage image = _history.peek();
		_image.setImage(image.getBufferedImage());

		if (_histogramFrame != null)
			if (_histogramFrame.getHistogram() != null)
			{
				_histogramFrame.getHistogram().setImage(image.getHistogram().getBufferedImage());
				//_histogramFrame.getHistogram().repaint();
			}

		// Update the undo button status
		if (_history.hasUndos())
			_menuBar.getButton("undo").setEnabled(true);
		else
			_menuBar.getButton("undo").setEnabled(false);

		// Update the redo button status
		if (_history.hasRedos())
			_menuBar.getButton("redo").setEnabled(true);
		else
			_menuBar.getButton("redo").setEnabled(false);

		// Decide whether an asterisk is needed for the title bar
		String asterisk = _history.isCurrentImageSaved() ? "" : "*";

		// Update the title bar
		this.setTitle(_fileName + asterisk + " - " + _applicationName);
	}

	private void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE, null);
	}
}
