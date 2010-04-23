
package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author marcus
 */
public class Overlay extends JPanel implements ActionListener
{
	private JLabel _label;
	private JButton _cancelButton;
	private JButton _okButton;

	private OverlayDelegate _delegate;

	public Overlay(OverlayDelegate delegate)
	{
		_delegate = delegate;
		_label = new JLabel();

		// Set up the cancel button
		_cancelButton = new JButton("Cancel");
		_cancelButton.setActionCommand("cancel");
		_cancelButton.addActionListener(this);
		_cancelButton.setBackground(new Color(0,0,0,0));

		// Set up the ok button
		_okButton = new JButton("OK");
		_okButton.setActionCommand("ok");
		_okButton.addActionListener(this);

		setMaximumSize(new Dimension(999999, 100));
		setBackground(new Color(0, 0, 0, 20));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(_cancelButton);
		add(Box.createHorizontalStrut(10));
		add(_okButton);
		add(Box.createHorizontalStrut(10));
		add(_label);
	}

	public void setMessage(String message)
	{
		_label.setText(message);
	}

	public void setHasOkButton(boolean b)
	{
		_okButton.setVisible(b);
	}

	public JButton getDefaultButton()
	{
		return _okButton.isVisible() ? _okButton : _cancelButton;
	}
	
	public JButton getOkButton()
	{
		return _okButton;
	}

	public JButton getCancelButton()
	{
		return _cancelButton;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("ok"))
			_delegate.pressedOk();
		else if (e.getActionCommand().equals("cancel"))
			_delegate.pressedCancel();
	}
}
