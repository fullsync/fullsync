
package net.sourceforge.fullsync.ui;

import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;

public class SplashScreen extends JWindow {

	private JLabel bitmapLabel;
	private MouseListener onClickListener;

	public static void main(String[] args) {
		SplashScreen splash = new SplashScreen("./images/About.png");
		splash.setHideOnClick(true);
		splash.setVisible(true);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		splash.setVisible(false);
		splash.dispose();
	}

	/**
	 * Constructs a new splash screen with the bitmap from the given file name.
	 * 
	 * @param filename the name of the image file to display
	 */
	public SplashScreen(String filename) {
		this.bitmapLabel = new JLabel();

		ImageIcon imageIcon = new ImageIcon(filename);
		int height = imageIcon.getIconHeight();
		int width = imageIcon.getIconWidth();
		bitmapLabel.setIcon(imageIcon);
		bitmapLabel.setSize(width, height);
		bitmapLabel.setLocation(0, 0);
		this.getContentPane().setLayout(null);
		this.getContentPane().add(bitmapLabel, null);
		this.setSize(width, height);
		
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((screenWidth - width) / 2, (screenHeight - height) / 2);
		
		onClickListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				hide();
			}
		};

	}
	
	public void setHideOnClick(boolean bool) {
		if (bool) {
			this.addMouseListener(onClickListener);
		}
		else {
			this.removeMouseListener(onClickListener);
		}
	}

}
