
package net.marcuswhybrow.uni.g52ivg.cw1;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


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
		addButton(menu, "open", "Open");
		menu.addSeparator();
		addButton(menu, "save", "Save");
		addButton(menu, "saveAs", "Save As...");
		this.add(menu);

		menu = new JMenu("Edit");
		addButton(menu, "undo", "Undo");
		addButton(menu, "redo", "Redo");
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
		addButton(menu, "regGrow", "Median");
		this.add(menu);

		//disableAllButtons();

		_menuItems.get("open").setEnabled(true);
	}

	private void disableAllButtons()
	{
		for(JMenuItem menuItem : _menuItems.values())
			menuItem.setEnabled(false);
	}

	private void addButton(JMenu menu, String identifier, String name)
	{
		_menuItems.put(identifier, menu.add(new JMenuItem(name)));
		_menuItems.get(identifier).addActionListener((ActionListener) EventHandler.create(ActionListener.class, this._mainFrame, identifier));
	}
}
