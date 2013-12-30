

/**
 * @author Joe Azar
 * @version 1.3.1
 * 
 * v1.0 - Standard Build, all basic features
 * v1.1 - Added linear drawing, optimized point selection code and drawing methods, updated GUI and overall look.
 * v1.2 - Two new sizes, Pixel and Super-Sampled. Optimized sampling drawing algorithm. 
 * v1.3 - Added caching [about an 11% increase in generations/second].
 * v1.3.1 - Patched bug where cached values [yes, I know how that sounds] would cause erratic life behavior. Also added ability to show cachedVals. Sped up drawing algorithm.
 * v1.3.2 - Fully integrated Breesenham's line algorithm with Pixel and Super-Sampled grids. Much faster and more reliable.
 * v1.4 - Cleaned up the code significantly, implemented more encapsulation.
 * 
 * NOTE: fractals can be observed with these settings:  (Sierpinski's Triangle [U|3 - O|7 - B|3]) (Squares[U|1 - O|8 - B|1])
 * For drawing: [U|2 - O|6 - B|7]
 * TODO: Boundless grid
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class CGOL_UI extends JFrame
{
	GridManager tr = new GridManager();
	int gX = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	int gY = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	static JSlider cVal1, cVal2, cVal3, brush;
	static JLabel gen1, pop1, cVal1L, cVal2L, cVal3L, timingL, brushL;
	static JButton step, reset;
	static JToggleButton start, showCached;
	static JTextField timing;
	static JComboBox<?> grid;
	int preX, preY;
	boolean first = true;
	boolean zoom = false;
	String[] txts1 = { "Big", "Medium", "Small", "Tiny", "Eensy-Weensy", "Pixel", "Super-Sampled" };

	public CGOL_UI()
	{
		setResizable(true);
		setTitle("Conway's GOL");
		setSize(gX, gY);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// --------------------------------------------------------------------
		timing = new JTextField();
		timing.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					tr.setDelay(Integer.parseInt(timing.getText().trim()));
					timingL.setText("Delay (ms) [" + tr.getDelay() + "]: ");
					repaint();
				}
				catch (Exception e)
				{
					System.out.println("Bad Input!");
				}
				finally
				{
					timing.setText("");
				}
			}
		});
		cVal1 = new JSlider(SwingConstants.HORIZONTAL, 1, 8, 2);
		cVal1.setMajorTickSpacing(1);
		cVal1.setPaintTicks(true);
		cVal1.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tr.setVal1(cVal1.getValue());
				repaint();
			}
		});
		cVal2 = new JSlider(SwingConstants.HORIZONTAL, 1, 8, 3);
		cVal2.setMajorTickSpacing(1);
		cVal2.setPaintTicks(true);
		cVal2.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tr.setVal2(cVal2.getValue());
				repaint();
			}
		});
		cVal3 = new JSlider(SwingConstants.HORIZONTAL, 1, 8, 3);
		cVal3.setMajorTickSpacing(1);
		cVal3.setPaintTicks(true);
		cVal3.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tr.setVal3(cVal3.getValue());
				repaint();
			}
		});
		step = new JButton("Step");
		step.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				// tr.toggleStep();
				//
				tr.findIndexSmoothedII(1450, 0, 1450, 2000);
				repaint();
			}
		});
		reset = new JButton("Reset Grid");
		reset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tr.setStart(false);
				start.setSelected(false);
				start.setText("Start");
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				tr.reset();
				repaint();
			}
		});
		showCached = new JToggleButton("Show Cached");
		showCached.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent ev)
			{
				if (!tr.isSuperSampled())
				{
					if (ev.getStateChange() == ItemEvent.SELECTED)
					{
						tr.setUsingCached(true);
						showCached.setText("Hide Cached");
					}
					else if (ev.getStateChange() == ItemEvent.DESELECTED)
					{
						tr.setUsingCached(false);
						showCached.setText("Show Cached");
						repaint();
					}
				}
				else
				{
					if (ev.getStateChange() == ItemEvent.SELECTED)
					{
						tr.setShowingHeatmap(true);
						showCached.setText("Hide HeatMap");
					}
					else if (ev.getStateChange() == ItemEvent.DESELECTED)
					{
						tr.setShowingHeatmap(false);
						showCached.setText("Show HeatMap");
						repaint();
					}
				}
			}
		});
		start = new JToggleButton("Start");
		start.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent ev)
			{
				if (ev.getStateChange() == ItemEvent.SELECTED)
				{
					tr.setStart(true);
					start.setText("Stop");
				}
				else if (ev.getStateChange() == ItemEvent.DESELECTED)
				{
					tr.setStart(false);
					start.setText("Start");
					repaint();
				}
			}
		});
		brush = new JSlider(SwingConstants.HORIZONTAL, 1, 20, 2);
		brush.setMajorTickSpacing(1);
		brush.setPaintTicks(true);
		brush.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tr.setBrushSize(brush.getValue());
				repaint();
			}
		});
		grid = new JComboBox<Object>(txts1);
		grid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				tr.setStart(false);
				start.setSelected(false);
				start.setText("Start");
				JComboBox<?> cb = (JComboBox<?>) evt.getSource();
				// a quick but effective fix
				try
				{
					Thread.sleep(150);
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
				tr.resizeGrid(cb.getSelectedIndex());
				if (tr.isSuperSampled())
					showCached.setText("Show HeatMap");
				else
				{
					if (showCached.isSelected())
					{
						showCached.setText("Hide Cached");
					}
					else
					{
						showCached.setText("Show Cached");
					}
				}
				brush.setValue(tr.getBrushSize());
			}
		});
		timingL = new JLabel("Delay (ms) [" + tr.getDelay() + "]: ");
		brushL = new JLabel("Brush size: ");
		cVal1L = new JLabel("Underpop Lim [Default 2]: ");
		cVal2L = new JLabel(" Overpop Lim [Default 3]: ");
		cVal3L = new JLabel(" \"Birth\" Lim [Default 3]: ");
		gen1 = new JLabel("Generation: " + tr.getGen());
		pop1 = new JLabel("Living Cells: " + tr.getPop());
		// ------------------------------------------------------------------------
		tr.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				tr.findIndexSmoothed(arg0.getX(), arg0.getY());
				preX = arg0.getX();
				preY = arg0.getY();
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				tr.updatePop();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}
		});
		tr.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseDragged(MouseEvent arg0)
			{
				if (first)
				{
					preX = arg0.getX();
					preY = arg0.getY();
					tr.findIndexSmoothed(preX, preY, arg0.getX(), arg0.getY());
					first = false;
				}
				else if (tr.isSuperSampled())
				{
					tr.findIndexSmoothedII(preX << 1, preY << 1, arg0.getX() << 1, arg0.getY() << 1);
				}
				else if (tr.isUsingBres())
				{
					tr.findIndexSmoothedII(preX, preY, arg0.getX(), arg0.getY());
				}
				else
				{
					tr.findIndexSmoothed(preX, preY, arg0.getX(), arg0.getY());
				}
				preX = arg0.getX();
				preY = arg0.getY();
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent arg0)
			{
			}
		});
		// -----------------------------------------------------------------------
		reset.setBounds(gX - 190, 70, 150, 50);
		timingL.setBounds(gX - 190, 250, 100, 20);
		timing.setBounds(gX - 190, 280, 150, 40);
		tr.setBounds(5, 5, 1154, 928);
		start.setBounds(gX - 190, 130, 150, 50);
		step.setBounds(gX - 190, 190, 150, 50);
		brush.setBounds(gX - 190, 360, 155, 40);
		brushL.setBounds(gX - 190, 330, 150, 20);
		grid.setBounds(gX - 190, 420, 150, 30);
		showCached.setBounds(gX - 190, 470, 150, 50);
		cVal1L.setBounds(gX - 190, 530, 150, 20);
		cVal1.setBounds(gX - 190, 560, 150, 40);
		cVal2L.setBounds(gX - 190, 610, 150, 20);
		cVal2.setBounds(gX - 190, 640, 150, 40);
		cVal3L.setBounds(gX - 190, 690, 150, 20);
		cVal3.setBounds(gX - 190, 720, 150, 40);
		gen1.setBounds(gX - 190, 10, 150, 20);
		pop1.setBounds(gX - 190, 35, 150, 20);
		add(gen1);
		add(pop1);
		add(reset);
		add(cVal1);
		add(cVal1L);
		add(cVal2);
		add(cVal2L);
		add(cVal3);
		add(cVal3L);
		add(showCached);
		add(grid);
		add(brushL);
		add(brush);
		add(start);
		add(step);
		add(timingL);
		add(timing);
		add(tr); // always last
		setVisible(true);
		tr.popGrid();
		while (true)
		{ // this little ditty took me a *while* to figure out
			gen1.setText("Generation: " + tr.getGen());
			pop1.setText("Living Cells: " + tr.getPop());
			repaint();
		}
	}
}
