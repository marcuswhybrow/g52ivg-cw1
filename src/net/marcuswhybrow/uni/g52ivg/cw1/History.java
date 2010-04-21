package net.marcuswhybrow.uni.g52ivg.cw1;

import java.util.ArrayList;

/**
 *
 * @author marcus
 */
public class History
{
	private ArrayList<JPEGImage> _history;
	private int _currentImage;
	private int _lastSavedImage;

	public History()
	{
		_history = new ArrayList<JPEGImage>();
		_currentImage = -1;
		_lastSavedImage = -1;
	}

	public JPEGImage undo()
	{
		try
		{
			return _history.get(--_currentImage);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public JPEGImage redo()
	{
		try
		{
			return _history.get(++_currentImage);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public void push(JPEGImage image)
	{
		this.clearForwardHistory();
		_history.add(image);
		_currentImage++;
	}

	public JPEGImage peek()
	{
		try
		{
			return _history.get(_currentImage);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public void clear()
	{
		_history = new ArrayList<JPEGImage>();
		_currentImage = -1;
	}

	public void setCurrentImageAsSaved()
	{
		_lastSavedImage = _currentImage;
	}

	public boolean isCurrentImageSaved()
	{
		return _lastSavedImage == _currentImage;
	}

	private void clearForwardHistory()
	{
		for(int i = _history.size() - 1; i > _currentImage; i--)
			_history.remove(i);
	}
}
