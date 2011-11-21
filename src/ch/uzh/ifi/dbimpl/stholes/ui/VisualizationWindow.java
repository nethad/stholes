package ch.uzh.ifi.dbimpl.stholes.ui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class VisualizationWindow {

	private JFrame frame;
	private JComponent canvas;

	public VisualizationWindow() {
		initializeGui();
	}

	private void initializeGui() {
		this.frame = new JFrame();
		this.frame.setSize(800, 800);
		this.frame.setLayout(new GridLayout(1, 1));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("STHoles");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 1));
		canvas = newCanvas();
		mainPanel.add(canvas);
		this.frame.add(mainPanel);
		this.frame.setVisible(true);
	}

	protected abstract JComponent newCanvas();

}
