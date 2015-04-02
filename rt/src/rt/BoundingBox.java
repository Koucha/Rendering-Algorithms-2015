package rt;

import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 * Axis aligned finite 3D BoundingBox
 * 
 * @author Florian
 */
public class BoundingBox
{
	float minx;
	float maxx;
	float miny;
	float maxy;
	float minz;
	float maxz;
	
	/**
	 * Construct BoundingBox from minimal and maximal axis values (boundaries on axes).
	 * Upper and lower values are automatically swapped if lower > upper. Therefore inputs with lower > upper and upper > lower are equally valid.
	 * 
	 * @param minx lower boundary on x-axis
	 * @param maxx upper boundary on x-axis
	 * @param miny lower boundary on y-axis
	 * @param maxy upper boundary on y-axis
	 * @param minz lower boundary on z-axis
	 * @param maxz upper boundary on z-axis
	 */
	public BoundingBox(float minx, float maxx, float miny, float maxy, float minz, float maxz)
	{
		if(minx < maxx)
		{
			this.minx = minx;
			this.maxx = maxx;
		}else
		{
			this.minx = maxx;
			this.maxx = minx;
		}
		
		if(miny < maxy)
		{
			this.miny = miny;
			this.maxy = maxy;
		}else
		{
			this.miny = maxy;
			this.maxy = miny;
		}
		
		if(minz < maxz)
		{
			this.minz = minz;
			this.maxz = maxz;
		}else
		{
			this.minz = maxz;
			this.maxz = minz;
		}
	}
	
	/**
	 * Construct BoundingBox from its central vector and its uniform size a
	 * (height = width = depth = 2*a)
	 * p inside BoundingBox <=> maxnorm(p - center) < a
	 * 
	 * @param center central vector
	 * @param a distance from bounds to central vector
	 */
	public BoundingBox(Vector3f center, float a)
	{
		minx = center.x - a;
		maxx = center.x + a;
		miny = center.y - a;
		maxy = center.y + a;
		minz = center.z - a;
		maxz = center.z + a;
	}
	
	/**
	 * Construct BoundingBox from central vector and its height, width and depth.
	 * 
	 * @param center central vector
	 * @param w width (x-axis aligned)
	 * @param h height (y-axis aligned)
	 * @param d depth (z-axis aligned)
	 */
	public BoundingBox(Tuple3f center, float w, float h, float d)
	{
		minx = center.x - w/2.0f;
		maxx = center.x + w/2.0f;
		miny = center.y - h/2.0f;
		maxy = center.y + h/2.0f;
		minz = center.z - d/2.0f;
		maxz = center.z + d/2.0f;
	}
	
	/**
	 * Construct BoundingBox from two diagonally opposite corner vectors of the BoundnigBox
	 * 
	 * @param a first corner vector
	 * @param b second corner vector
	 */
	public BoundingBox(Tuple3f a, Tuple3f b)
	{
		minx = Math.min(a.x, b.x);
		maxx = Math.max(a.x, b.x);
		miny = Math.min(a.y, b.y);
		maxy = Math.max(a.y, b.y);
		minz = Math.min(a.z, b.z);
		maxz = Math.max(a.z, b.z);
	}
	
	/**
	 * Create identical copy of BoundingBox b
	 * 
	 * @param b BoundingBox to be copied
	 */
	public BoundingBox(BoundingBox b)
	{
		minx = b.minx;
		maxx = b.maxx;
		miny = b.miny;
		maxy = b.maxy;
		minz = b.minz;
		maxz = b.maxz;
	}

	/**
	 * Extend this BoundingBox, so that it contains the BoundingBox b too.
	 * 
	 * @param b BoundingBox to be included
	 * @return this BoundingBox(extended) or null if extension is not possible (e.g. b = null)
	 */
	public BoundingBox combinationWith(BoundingBox b)
	{
		if(b == null)
		{
			return null;
		}
		
		minx = Math.min(minx, b.minx);
		maxx = Math.max(maxx, b.maxx);
		miny = Math.min(miny, b.miny);
		maxy = Math.max(maxy, b.maxy);
		minz = Math.min(minz, b.minz);
		maxz = Math.max(maxz, b.maxz);
		
		return this;
	}
	
	/**
	 * Intersect this BoundingBox with another, resulting in a smaller BoundingBox that is inside of both
	 *  
	 * @param b BoundingBox to be intersected with
	 * @return this BoundingBox(intersection) or null if intersection is empty
	 */
	public BoundingBox intersectionWith(BoundingBox b)
	{
		if(!intersect(b))
		{
			return null;
		}
		
		minx = Math.max(minx, b.minx);
		maxx = Math.min(maxx, b.maxx);
		miny = Math.max(miny, b.miny);
		maxy = Math.min(maxy, b.maxy);
		minz = Math.max(minz, b.minz);
		maxz = Math.min(maxz, b.maxz);
		
		return this;
	}

	/**
	 * Test if this BoundingBox intersects with other BoundingBox b
	 * 
	 * @param b	BoundingBox to be intersected
	 * @return true if the boxes intersect
	 */
	public boolean intersect(BoundingBox b)
	{
		if(b == null)
		{
			return false;
		}
		
		return ((minx < b.maxx && b.minx < maxx)&&(miny < b.maxy && b.miny < maxy)&&(minz < b.maxz && b.minz < maxz));
	}
	
	/**
	 * fast intersection test of the BoundingBox and a {@link Ray}
	 * 
	 * @param r	Ray to be intersected with
	 * @return parameter of intersection point (>0) or -1 if no intersection
	 */
	public float intersect(Ray r)
	{
		float n1 = (minx - r.origin.x)/r.direction.x;
		float n2 = (maxx - r.origin.x)/r.direction.x;
		if(n1 > n2)
		{
			float temp = n1;
			n1 = n2;
			n2 = temp;
		}
		
		float n3 = (miny - r.origin.y)/r.direction.y;
		float n4 = (maxy - r.origin.y)/r.direction.y;
		if(n3 > n4)
		{
			float temp = n3;
			n3 = n4;
			n4 = temp;
		}
		
		n1 = Math.max(n1, n3);
		n2 = Math.min(n2, n4);
		
		n3 = (minz - r.origin.z)/r.direction.z;
		n4 = (maxz - r.origin.z)/r.direction.z;
		if(n3 > n4)
		{
			float temp = n3;
			n3 = n4;
			n4 = temp;
		}
		
		n1 = Math.max(n1, n3);
		n2 = Math.min(n2, n4);
		
		if(n1 > n2)
		{
			return Float.NEGATIVE_INFINITY; // no intersection
		}
		
		if(n1 > 0)	// intersection
		{
			return n1;
		}
		
		return 0;	// ray begins inside
	}
	
	public float getMinx() {
		return minx;
	}

	public float getMaxx() {
		return maxx;
	}

	public float getMiny() {
		return miny;
	}

	public float getMaxy() {
		return maxy;
	}

	public float getMinz() {
		return minz;
	}

	public float getMaxz() {
		return maxz;
	}
}
