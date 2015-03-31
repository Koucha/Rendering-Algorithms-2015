package rt.films;

import rt.Film;
import rt.Spectrum;

/**
 * Uses a area filter when accumulating samples on a film. A Ray contributes to a pixel according to the area it covers. Sample values
 * are averaged with weights.
 */
public class AreaFilterFilm implements Film {
	
	private Spectrum[][] image;
	public int width, height;
	private Spectrum[][] unnormalized;
	private float nSamples[][];
	
	public AreaFilterFilm(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new Spectrum[width][height];
		unnormalized = new Spectrum[width][height];
		nSamples = new float[width][height];
		
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				image[i][j] = new Spectrum();
				unnormalized[i][j] = new Spectrum();
			}
		}
	}
	
	public void addSample(double x, double y, Spectrum s)
	{
		if((int)x>=0 && (int)x<width && (int)y>=0 && (int)y<height)
		{
			int idx_x = (int)x;
			int idx_y = (int)y;
			float dx = (float) (x%1);
			float dy = (float) (y%1);
			float weight;
			Spectrum ws;
			
			for(int i = -1; i < 2; i++)
			{
				for(int j = -1; j < 2; j++)
				{
					if((idx_x+i)>=0 && (idx_x+i)<width && (idx_y+j)>=0 && (idx_y+j)<height)
					{
						weight = area(i,j,dx,dy);
						ws = new Spectrum(s);
						ws.mult(weight);
						unnormalized[idx_x+i][idx_y+j].add(ws);
						nSamples[idx_x+i][idx_y+j] += weight;
					}
				}
			}
		}
	}
	
	private float area(int x, int y, float dx, float dy)
	{
		final float rayradius = 0.3f;
		final float pixelradius = 0.5f;
		
		float r1x = dx - rayradius,
			  r1y = dy - rayradius, 
			  r2x = dx + rayradius,
			  r2y = dy + rayradius;

		float p1x = x - pixelradius,
			  p1y = y - pixelradius, 
			  p2x = x + pixelradius,
			  p2y = y + pixelradius;
		
		r1x = Math.max(r1x, p1x);
		r1y = Math.max(r1y, p1y);		
		r2x = Math.min(r2x, p2x);
		r2y = Math.min(r2y, p2y);
		
		if(r1x > r2x || r1y > r2y)
		{
			return 0;
		}
		
		return (r2x - r1x)*(r2y - r1y);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Spectrum[][] getImage()
	{
		for(int i=0; i<width; i++)
		{
			for(int j=0; j<height; j++)
			{
				image[i][j].r = unnormalized[i][j].r/nSamples[i][j];
				image[i][j].g = unnormalized[i][j].g/nSamples[i][j];
				image[i][j].b = unnormalized[i][j].b/nSamples[i][j];
			}
		}
		return image;
	}
}
