
package net.marcuswhybrow.uni.g52ivg.cw1;

import com.sun.image.codec.jpeg.ImageFormatException;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author marcus
 */
public class MainFrame extends JFrame
{
	private History _history;
	private JLabel _imageLabel;

	private String _applicationName;
	private int _historyLastSave;

	public MainFrame()
	{
		super("Photoshop CS5 RC1");

		_applicationName = this.getTitle();

		_history = new History();
		_imageLabel = new JLabel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 400);
		this.setJMenuBar(new MenuBar(this));

		this.add(_imageLabel);

		this.setVisible(true);
	}

	public void updateImage(JPEGImage newImage) throws IOException
	{
		_history.push(new JPEGImage(newImage));

	}

	public JPEGImage getImage()
	{
		return _history.peek();
	}

	public void open() throws IOException, ImageFormatException
	{
		JPEGImage image;

		JFileChooser c = new JFileChooser();
		if (c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			image = new JPEGImage(c.getSelectedFile().getAbsolutePath());
			_history.clear();
			_history.push(image);
			reloadImageLabel();
		}
	}

	public void save()
	{
		
	}

	public void saveAs() throws IOException
	{
		JFileChooser c = new JFileChooser();
		if (c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			this.getImage().write(c.getSelectedFile().getAbsolutePath());
	}

	public void meanFilter()
	{
		System.out.println("meanFilter");
	}

	public void medianFilter()
	{
		System.out.println("medianFilter");
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
	}

	public void redo()
	{
		_history.redo();
	}

	private void reloadImageLabel()
	{
		_imageLabel.setIcon(new ImageIcon(_history.peek().getBufferedImage()));
		String asterisk = _history.isCurrentImageSaved() ? "*" : "";
		this.setTitle(_history.peek().getPath() + asterisk + " - " + _applicationName);
	}
}
