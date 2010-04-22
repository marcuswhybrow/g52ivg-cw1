
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.ImageFormatException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author marcus
 */
public class MainFrame extends JFrame implements ComponentListener
{
	private History _history;
	private Image _image;

	private String _applicationName;
	private String _fileName;
	private MenuBar _menuBar;

	private Box _horizontalBox;
	private Box _verticalBox;

	public MainFrame()
	{
		super("Photoshop CS5 RC1");

		// Get the name set using super(String str) above
		_applicationName = this.getTitle();

		// initialise the history and image instances
		_history = new History();
		_image = new Image();

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

		add(_horizontalBox);

		// Add the image to this panel which will display the working image.
//		_image.setBorder(new LineBorder(Color.BLACK));

		setLocationRelativeTo(null);

		// Finally make the MainFrame visible
		setVisible(true);
	}

	public JPEGImage getImage()
	{
		return _history.peek();
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
		_menuBar.getButton("undo").setEnabled(true);
		_menuBar.getButton("redo").setEnabled(true);
		_menuBar.getButton("meanFilter").setEnabled(true);
		_menuBar.getButton("medianFilter").setEnabled(true);
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
	}

	public void medianFilter()
	{
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
	}

	public void histEq()
	{
		System.out.println("histEq");
	}

	public void histSeg()
	{
		System.out.println("histSeq");
	}

	public void regGrow()
	{
		System.out.println("regGrow");
	}

	public void undo()
	{
		_history.undo();
		this.reloadImage();
	}

	public void redo()
	{
		_history.redo();
		this.reloadImage();
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
		System.out.println("resized");
	}

	public void componentMoved(ComponentEvent e)
	{
		JPEGImage image = _history.peek();

		if(!_image.hasImage()) return;

		//System.out.println(" Image Width: " + _image.getWidth() + " Panel Width: " + this.getContentPane().getWidth());

		Container panel = getContentPane();

		if(_image.getWidth() > panel.getWidth() || (_image.getWidth() < this.getWidth() && _image.getWidth() < image.getWidth()))
			_image.setPreferredWidth(panel.getWidth());

//		if(_image.getHeight() > panel.getHeight() || (_image.getHeight() < this.getHeight() && _image.getHeight() < image.getHeight()))
//			_image.setPreferredHeight(panel.getHeight());

	}

	public void componentShown(ComponentEvent e)
	{
		System.out.println("shown");
	}

	public void componentHidden(ComponentEvent e)
	{
		System.out.println("hidden");
	}


	private void reloadImage()
	{
		_image.setImage(_history.peek().getBufferedImage());

		String asterisk = _history.isCurrentImageSaved() ? "" : "*";
		this.setTitle(_fileName + asterisk + " - " + _applicationName);
	}

	private void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE, null);
	}
}
