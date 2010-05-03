
package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


/**
 *
 * @author marcus
 */
public class MenuBar extends JMenuBar
{
	private JFrame _mainFrame;
	private HashMap<String, JMenuItem> _menuItems = new HashMap<String, JMenuItem>();

	public MenuBar(JFrame mainFrame)
	{
		super();

		_mainFrame = mainFrame;

		JMenu menu, innerMenu;
		JMenuItem item;

		menu = new JMenu("File");
		addButton(menu, "open", "Open").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.addSeparator();
		addButton(menu, "save", "Save").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		addButton(menu, "saveAs", "Save As...").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK ^ ActionEvent.SHIFT_MASK));
		this.add(menu);

		menu = new JMenu("Edit");
		addButton(menu, "undo", "Undo").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		addButton(menu, "redo", "Redo").setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK ^ ActionEvent.SHIFT_MASK));
		this.add(menu);

		menu = new JMenu("Enhancement");
		innerMenu = new JMenu("Filters");
		addButton(innerMenu, "meanFilter", "Mean");
		addButton(innerMenu, "medianFilter", "Median");
		menu.add(innerMenu);
		addButton(menu, "histEq", "Histogram Equalisation");
		this.add(menu);

		menu = new JMenu("Segmentation");
		addButton(menu, "histSeg", "Histogram Segmentation");
		addButton(menu, "regGrow", "Region Growing");
		this.add(menu);

		disableAllButtons();

		_menuItems.get("open").setEnabled(true);
	}

	public JMenuItem getButton(String key)
	{
		return _menuItems.get(key);
	}

	private void disableAllButtons()
	{
		for(JMenuItem menuItem : _menuItems.values())
			menuItem.setEnabled(false);
	}

	private JMenuItem addButton(JMenu menu, String identifier, String name)
	{
		_menuItems.put(identifier, menu.add(new JMenuItem(name)));
		JMenuItem button = _menuItems.get(identifier);
		button.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this._mainFrame, identifier));

		return button;
	}

	private JCheckBoxMenuItem addCheckBox(JMenu menu, String identifier, String name)
	{
		_menuItems.put(identifier, menu.add(new JCheckBoxMenuItem(name)));
		JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) _menuItems.get(identifier);
		checkBox.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this._mainFrame, identifier));

		return checkBox;
	}
}
