

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GridManager extends JPanel
{
	private int size = 16;
	private int gX = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	private int gY = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	private volatile int sX = ((gX - 200) / (size) + 4); // X length of grid
	private volatile int sY = ((gY - 10) / (size) + 2); // Y length of grid
	private int time = 100, brush = 1, val1 = 2, val2 = 3, val3 = 3, gen = 0;
	private long pop = 0;
	private volatile boolean start = false;
	@SuppressWarnings("unused")
	private boolean step = false, showGrid = false, debug = false, superSampled = false, useBres = false, useCached = false,
			heatmap = false;
	private int ssL = 2;// ssL^2 is how many spaces each pixel represents.
	private volatile boolean[][] actualVals = new boolean[sY][sX];
	private volatile boolean[][] flagVals = new boolean[sY][sX];
	private volatile boolean[][] cachedVals = new boolean[sY][sX];
	private volatile int[][][] valCoord = new int[sY][sX][2];

	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		CGOL_UI t = new CGOL_UI();
	}

	@Override
	public void paintComponent(Graphics g2)
	{
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, this.getWidth() - 198, this.getHeight() - 8);
		start();
		if (!superSampled)
			printVals(g2);
		else
			printSuperVals(g2);
	}

	public void popGrid() // populates the valCoord array with points of reference for mouse clicks
	{
		updatePop();
		if (!superSampled)
		{
			int x = size / 2;
			int y = size / 2;
			for (int i = 2; i < sY - 2; i++)
			{
				for (int j = 2; j < sX - 2; j++)
				{
					valCoord[i][j][0] = x;
					valCoord[i][j][1] = y;
					x += size;
				}
				y += size;
				x = size / 2;
			}
		}
	}

	public void start() // Kills and creates life
	{
		if (start == true || step == true)
		{
			int x;
			for (int i = 2; i < sY - 2; i++)
			{
				for (int j = 2; j < sX - 2; j++)
				{
					if (cachedVals[i][j])
					{
						x = numAdjacent(i, j);
						if (x < val1 || x > val2)
						{
							flagVals[i][j] = false;
							if (actualVals[i][j])
								pop--;
						}
						else if (x == val3) // check 4
						{
							pop++;
							flagVals[i][j] = true;
						}
					}
				}
			}
			refresh();
			try
			{
				Thread.sleep(time);
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
			step = false;
			gen++;
		}
	}

	private int numAdjacent(int x, int y) // returns # of adj 1 value places
	{
		int na = 0;
		int xdx, ydy;
		for (int dx = -1; dx < 2; dx++)
		{
			xdx = x + dx;
			for (int dy = -1; dy < 2; dy++)
			{
				if (dx != 0 || dy != 0)
				{
					ydy = y + dy;
					if (actualVals[xdx][ydy])
					{
						setCached(xdx, ydy);
						na++;
					}
				}
			}
		}
		if (na == 0)
			cachedVals[x][y] = false;
		return na;
	}

	private void setCached(int xdx, int ydy)
	{
		cachedVals[xdx + 1][ydy + 1] = true;
		cachedVals[xdx + 1][ydy] = true;
		cachedVals[xdx + 1][ydy - 1] = true;
		cachedVals[xdx][ydy + 1] = true;
		cachedVals[xdx][ydy - 1] = true;
		cachedVals[xdx - 1][ydy + 1] = true;
		cachedVals[xdx - 1][ydy - 1] = true;
		cachedVals[xdx - 1][ydy + 1] = true;
		cachedVals[xdx + 2][ydy + 2] = true;
		cachedVals[xdx + 2][ydy] = true;
		cachedVals[xdx + 2][ydy - 2] = true;
		cachedVals[xdx][ydy + 2] = true;
		cachedVals[xdx][ydy - 2] = true;
		cachedVals[xdx - 2][ydy + 2] = true;
		cachedVals[xdx - 2][ydy - 2] = true;
		cachedVals[xdx - 2][ydy + 2] = true;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void printValsTxt() // Text representation, from pre-alpha
	{
		for (int i = 0; i < 75; i++)
			System.out.println();
		for (int i = 1; i < sY - 1; i++)
		{
			for (int j = 1; j < sX - 1; j++)
			{
				System.out.print(actualVals[i][j] + " ");
			}
			System.out.println();
		}
	}

	private void printVals(Graphics g) // Graphical representation of CGOL
	{
		int x = 0;
		int y = 0;
		for (int i = 2; i < sY - 2; i++)
		{
			for (int j = 2; j < sX - 2; j++)
			{
				/*
				 * Shows WHOLE grid.
				 * 
				 * if (actualVals[i][j] == 0) { if (showGrid == true) { g.setColor(Color.GRAY); g.drawRect(x, y, size, size);
				 * } }
				 */
				if (!useCached)
				{
					if (actualVals[i][j])
					{
						g.setColor(Color.GREEN);
						g.fillRect(x, y, size, size);
						if (showGrid == true)
						{
							g.setColor(Color.BLACK);
							g.drawRect(x, y, size, size);
						}
					}
				}
				else
				{
					if (cachedVals[i][j])
					{
						g.setColor(Color.GREEN);
						g.fillRect(x, y, size, size);
						if (showGrid == true)
						{
							g.setColor(Color.BLACK);
							g.drawRect(x, y, size, size);
						}
					}
				}
				x += size;
			}
			y += size;
			x = 0;
		}
	}

	private void printSuperVals(Graphics g) // Super-Sampled version of printVals()
	{
		int x = 0;
		int y = 0;
		int ss = 0;
		for (int i = 2; i < (sY - ssL); i += ssL)
		{
			for (int j = 2; j < sX - ssL; j += ssL)
			{
				if (cachedVals[i][j])
				{
					ss = 0;
					if (actualVals[i][j])
						ss++;
					if (actualVals[i + 1][j])
						ss++;
					if (actualVals[i][j + 1])
						ss++;
					if (actualVals[i + 1][j + 1])
						ss++;
					if (heatmap)
					{
						if (ss == 1)
							g.setColor(Color.BLUE);
						else if (ss == 2)
							g.setColor(Color.GREEN);
						else if (ss == 3)
							g.setColor(Color.YELLOW);
						else if (ss == 4)
							g.setColor(Color.RED);
						else
							g.setColor(Color.BLACK);
					}
					else
						g.setColor(new Color(0, 60 * ss, 0));
					g.drawLine(x, y, x, y);
				}
				x += 1;
			}
			y += 1;
			x = 0;
		}
	}

	public void expandGrid() // Prototype
	{
		int osX = sX;
		int osY = sY;
		sY *= 1.5;
		sX *= 1.5;
		flagVals = new boolean[sY][sX];
		expand(osY, osX);
		actualVals = new boolean[sY][sX];
		valCoord = new int[sY][sX][2];
	}

	private void expand(int osY, int osX) // Prototype
	{
		int qY = (sY - osY) / 2;
		int qX = (sX - osX) / 2;
		boolean[][] temp = cachedVals;
		cachedVals = new boolean[sY][sX];
		// if grid is being up-scaled
		for (int y = 0; y < osY; y++)
		{
			for (int x = 0; x < osX; x++)
			{
				flagVals[y + qY][x + qX] = actualVals[y][x];
				cachedVals[y + qY][x + qX] = temp[y][x];
			}
		}
	}

	public void resizeGrid(int n) // resets grid and changes it's relative size
	{
		int osX = sX;
		int osY = sY;
		superSampled = false;
		useBres = false;
		if (n == 0)// big
		{
			size = 16;
			sY = ((gY - 10) / (size)) + 2;
			sX = ((gX - 200) / (size) + 3);
			brush = 1;
		}
		else if (n == 1)// med
		{
			size = 12;
			sY = ((gY - 10) / (size)) + 1;
			sX = ((gX - 200) / (size)) + 3;
			brush = 1;
		}
		else if (n == 2)// small
		{
			size = 8;
			sY = ((gY - 10) / (size));
			sX = ((gX - 200) / (size)) + 3;
			brush = 1;
		}
		else if (n == 3)// tiny
		{
			size = 4;
			sY = ((gY - 10) / (size)) - 4;
			sX = ((gX - 200) / (size));
			brush = 2;
		}
		else if (n == 4)// eensy
		{
			size = 2;
			sY = ((gY - 10) / (size)) - 14;
			sX = ((gX - 200) / (size)) - 3;
			brush = 3;
		}
		else if (n == 5)// Pixel
		{
			useBres = true;
			size = 1;
			sY = ((gY - 10) / (size)) - 32;
			sX = ((gX - 200) / (size)) - 14;
			brush = 4;
			showGrid = false;
		}
		else if (n == 6)// super-secret size *hehe*
		{
			superSampled = true;
			size = 1;
			sY = (((gY - 10) / (size)) * ssL) - 70;
			sX = (((gX - 200) / (size)) * ssL) - 26;
			showGrid = false;
			brush = 5;
		}
		flagVals = new boolean[sY][sX];
		dynamicResize(osY, osX);
		actualVals = new boolean[sY][sX];
		valCoord = new int[sY][sX][2];
		refresh();
		updatePop();
		if (!superSampled || !useBres)
			popGrid();
		// diagnostics();
	}

	private void dynamicResize(int osY, int osX) // retain what is currently on the grid, but up/down-scaled to fit new size.
	{
		int qY = (sY - osY) / 2;
		int qX = (sX - osX) / 2;
		boolean[][] temp = cachedVals;
		cachedVals = new boolean[sY][sX];
		// if grid is being up-scaled
		if (osY < sY)
		{
			for (int y = 0; y < osY; y++)
			{
				for (int x = 0; x < osX; x++)
				{
					flagVals[y + qY][x + qX] = actualVals[y][x];
					cachedVals[y + qY][x + qX] = temp[y][x];
				}
			}
		}
		else
		{
			qY = (osY - sY) / 2;
			qX = (osX - sX) / 2;
			for (int y = 2; y < sY - 2; y++)
			{
				for (int x = 2; x < sX - 2; x++)
				{
					flagVals[y][x] = actualVals[y + qY][x + qX];
					cachedVals[y][x] = temp[y + qY][x + qX];
				}
			}
		}
	}

	public void findIndexSmoothedII(int x, int y, int nx, int ny) // A *very* custom implementation of Bresenham's Line
																	// Algorithm
	{
		// preliminary brush size and super-sampling calculations
		int use = (size / 2 + 1) * brush / size;
		int shift = superSampled ? 1 : 0;
		// Determine distance between points in the X and Y axes, regardless of direction
		int dx = Math.abs(nx - x), dy = Math.abs(ny - y);
		// Determine what type of movement to take along line, based on direction
		int sx = x < nx ? 1 : -1, sy = y < ny ? 1 : -1;
		// threshold of offset before incrementing
		int err = (dx > dy ? dx : -dy) / 2;
		// The (sX,sY) values converted from the raw coordinates
		int xS, yS;
		while (true)
		{
			// if Both x and y have been incremented to the location of the second point, line is drawn and the algorithim
			// can end
			if (x == nx && y == ny)
				break;
			// Determine where cursor is in terms of (sY,sX) and handle border cases for X-Axis
			if ((x / size) - use > 0 && (x / size) + use < sX)
				xS = x / size;
			else if ((x / size) - use > 0 && (x / size) + use >= sX)
				xS = 5000;
			else
				xS = -5000;
			// Determine where cursor is in terms of (sY,sX) and handle border cases for Y-Axis
			if ((y / size) - use > 0 && (y / size) + use < sY)
				yS = y / size;
			else if ((y / size) - use > 0 && (y / size) + use >= sY)
				yS = 5000;
			else
				yS = -5000;
			// Below loops are responsible for array access and accounting for brush size
			for (int j = yS - (use << shift); j < yS + (use << shift); j++)
			{
				for (int i = xS - (use << shift); i < xS + (use << shift); i++)
				{
					if (i < sX - 3 && i > 2 && j > 2 && j < sY - 3)
					{
						flagVals[j][i] = true;
						actualVals[j][i] = true;
						cachedVals[j][i] = true;
						cachedVals[j + 1][i + 1] = true;
						cachedVals[j + 1][i] = true;
						cachedVals[j + 1][i - 1] = true;
						cachedVals[j][i + 1] = true;
						cachedVals[j][i - 1] = true;
						cachedVals[j - 1][i + 1] = true;
						cachedVals[j - 1][i - 1] = true;
						cachedVals[j - 1][i + 1] = true;
					}
				}
			}
			// determine where to point to next
			int e2 = err;
			if (e2 > -dx)
			{
				err -= dy;
				x += sx;
			}
			if (e2 < dy)
			{
				err += dx;
				y += sy;
			}
		}
	}

	/**
	 * @param initialX
	 * @param initialY
	 * @param newY
	 * @param newX
	 * 
	 * @info Based on two passed in points, plots a line between them and fills in the appropriate positions. It's no bezier
	 *       curve, but works OK none-the-less. Seems like the verticals still don't draw very well, but C'est la vie. Slows
	 *       WAY down on very large grid sizes. A far superior algorithm was implemented for the aforementioned sizes.
	 * 
	 */
	public void findIndexSmoothed(int x, int y, int nx, int ny)
	{
		int size1 = size / 2 + 1; // radius
		size1 *= brush;
		int searchMargin = 10; // how many squares are checked within a certain range
		double slope;
		// ((x/size) -50 >0) ? ((x/size) -50) : 0
		// Optimizes performance at the expense of function
		// UPDATE: a simple if/else reduced function loss to nominal levels
		if (x + 2.5 < nx)
		{
			slope = (((double) ny - y) / (nx - x));
			for (int i = 0; i < sY; i++)
			{
				for (int j = ((x / size) - searchMargin > 0) ? ((x / size) - searchMargin) : 0; j < sX; j++)
				{
					for (double c = x; c <= nx; c += 1)
					{
						if ((valCoord[i][j][0] >= c - size1 && valCoord[i][j][0] <= c + size1)
								&& (valCoord[i][j][1] >= ((slope * (c - x)) + y) - size1 && valCoord[i][j][1] <= ((slope * (c - x)) + y)
										+ size1))
						{
							flagVals[i][j] = true;
							actualVals[i][j] = true;
							cachedVals[i][j] = true;
							cachedVals[i + 1][j + 1] = true;
							cachedVals[i + 1][j] = true;
							cachedVals[i + 1][j - 1] = true;
							cachedVals[i][j + 1] = true;
							cachedVals[i][j - 1] = true;
							cachedVals[i - 1][j + 1] = true;
							cachedVals[i - 1][j - 1] = true;
							cachedVals[i - 1][j + 1] = true;
						}
					}
				}
			}
		}
		else if (x - 2.5 > nx)
		{
			slope = (((double) ny - y) / (nx - x));
			int d = ((x / size) + searchMargin < sX) ? ((x / size) + searchMargin) : sX;
			for (int i = 0; i < sY; i++)
			{
				for (int j = 0; j < d; j++)
				{
					for (double c = nx; c <= x; c += 1)
					{
						if ((valCoord[i][j][0] >= c - size1 && valCoord[i][j][0] <= c + size1)
								&& (valCoord[i][j][1] >= ((slope * (c - x)) + y) - size1 && valCoord[i][j][1] <= ((slope * (c - x)) + y)
										+ size1))
						{
							flagVals[i][j] = true;
							actualVals[i][j] = true;
							cachedVals[i][j] = true;
							cachedVals[i + 1][j + 1] = true;
							cachedVals[i + 1][j] = true;
							cachedVals[i + 1][j - 1] = true;
							cachedVals[i][j + 1] = true;
							cachedVals[i][j - 1] = true;
							cachedVals[i - 1][j + 1] = true;
							cachedVals[i - 1][j - 1] = true;
							cachedVals[i - 1][j + 1] = true;
						}
					}
				}
			}
		}
		else
		{
			if (ny > y)
			{
				for (int i = 0; i < sY; i++)
				{
					for (int j = ((x / size) - searchMargin > 0) ? ((x / size) - searchMargin) : 0; j < sX; j++)
					{
						for (double c = y; c <= ny; c++)
						{
							if ((valCoord[i][j][0] >= x - size1 && valCoord[i][j][0] <= x + size1)
									&& (valCoord[i][j][1] >= c - size1 && valCoord[i][j][1] <= c + size1))
							{
								flagVals[i][j] = true;
								actualVals[i][j] = true;
								cachedVals[i][j] = true;
								cachedVals[i + 1][j + 1] = true;
								cachedVals[i + 1][j] = true;
								cachedVals[i + 1][j - 1] = true;
								cachedVals[i][j + 1] = true;
								cachedVals[i][j - 1] = true;
								cachedVals[i - 1][j + 1] = true;
								cachedVals[i - 1][j - 1] = true;
								cachedVals[i - 1][j + 1] = true;
							}
						}
					}
				}
			}
			else
			{
				for (int i = 0; i < sY; i++)
				{
					for (int j = ((x / size) - searchMargin > 0) ? ((x / size) - searchMargin) : 0; j < sX; j++)
					{
						for (double c = ny; c <= y; c++)
						{
							if ((valCoord[i][j][0] >= x - size1 && valCoord[i][j][0] <= x + size1)
									&& (valCoord[i][j][1] >= c - size1 && valCoord[i][j][1] <= c + size1))
							{
								flagVals[i][j] = true;
								actualVals[i][j] = true;
								cachedVals[i][j] = true;
								cachedVals[i + 1][j + 1] = true;
								cachedVals[i + 1][j] = true;
								cachedVals[i + 1][j - 1] = true;
								cachedVals[i][j + 1] = true;
								cachedVals[i][j - 1] = true;
								cachedVals[i - 1][j + 1] = true;
								cachedVals[i - 1][j - 1] = true;
								cachedVals[i - 1][j + 1] = true;
							}
						}
					}
				}
			}
		}
	}

	public void findIndexSmoothed(int x, int y) // Finds point based on passed in x and y, adds it
	{
		int size1 = size / 2 + 1; // radius
		size1 *= brush;
		for (short i = 0; i < sY; i++)
		{
			for (short j = 0; j < sX; j++)
			{
				if ((valCoord[i][j][0] >= x - size1 && valCoord[i][j][0] <= x + size1)
						&& (valCoord[i][j][1] >= y - size1 && valCoord[i][j][1] <= y + size1))
				{
					flagVals[i][j] = true;
					actualVals[i][j] = true;
					cachedVals[i][j] = true;
					cachedVals[i + 1][j + 1] = true;
					cachedVals[i + 1][j] = true;
					cachedVals[i + 1][j - 1] = true;
					cachedVals[i][j + 1] = true;
					cachedVals[i][j - 1] = true;
					cachedVals[i - 1][j + 1] = true;
					cachedVals[i - 1][j - 1] = true;
					cachedVals[i - 1][j + 1] = true;
				}
			}
		}
	}

	public void reset() // resets grid
	{
		try
		{
			actualVals = new boolean[sY][sX];
			flagVals = new boolean[sY][sX];
			valCoord = new int[sY][sX][2];
			cachedVals = new boolean[sY][sX];
			gen = 0;
			pop = 0;
			if (!superSampled || !useBres)
				popGrid();
		}
		catch (OutOfMemoryError e)
		{
			System.err.println("Your computer is too weak.");
		}
	}

	public void refresh() // set arrays equal to one another
	{
		for (int i = 1; i < sY - 1; i++)
		{
			for (int j = 1; j < sX - 1; j++)
			{
				actualVals[i][j] = flagVals[i][j];
			}
		}
	}

	public void diagnostics()
	{
		long i = 0;
		for (int k = 0; k < sY; k++)
		{
			for (int j = 0; j < sX; j++)
			{
				i++;
			}
		}
		System.out.println("Array Count: " + i);
		System.out.println("SX: " + sX);
		System.out.println("SY: " + sY);
	}

	public void updatePop() // search entire grid and count/set population
	{
		pop = 0;
		for (int i = 1; i < sY - 1; i++)
		{
			for (int j = 1; j < sX - 1; j++)
			{
				if (actualVals[i][j])
					pop++;
			}
		}
	}

	// ------------------- Setters/Getters ------------------------
	public void setStart(boolean start)
	{
		this.start = start;
	}

	public int getDelay()
	{
		return time;
	}

	public void setDelay(int time)
	{
		this.time = time;
	}

	public int getBrushSize()
	{
		return brush;
	}

	public void setBrushSize(int brush)
	{
		this.brush = brush;
	}

	public int getGen()
	{
		return gen;
	}

	public long getPop()
	{
		return pop;
	}

	public void setVal1(int val1)
	{
		this.val1 = val1;
	}

	public void setVal2(int val2)
	{
		this.val2 = val2;
	}

	public void setVal3(int val3)
	{
		this.val3 = val3;
	}

	public void toggleStep()
	{
		step = true;
	}

	public boolean isShowingGrid()
	{
		return showGrid;
	}

	public void setShowingGrid(boolean showGrid)
	{
		this.showGrid = showGrid;
	}

	public boolean isUsingBres()
	{
		return useBres;
	}

	public boolean isSuperSampled()
	{
		return superSampled;
	}

	public boolean isUsingCached()
	{
		return useCached;
	}

	public void setUsingCached(boolean useCached)
	{
		this.useCached = useCached;
	}

	public boolean isShowingHeatmap()
	{
		return heatmap;
	}

	public void setShowingHeatmap(boolean heatmap)
	{
		this.heatmap = heatmap;
	}

	public <T extends Object> boolean doesItHalt(T program) // The "big picture" solution to the halting problem.
	{
		return true;
	}
}
