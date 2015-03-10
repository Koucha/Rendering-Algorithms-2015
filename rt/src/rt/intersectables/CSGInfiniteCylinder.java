package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;

/**
 * An infinte cylinder that can be used in CSG bodies
 * 
 * @author Florian
 */
public class CSGInfiniteCylinder extends CSGSolid {
	
	Material mat;
	
	/**
	 * Create a infinitely high cylinder (in y direction) with radius 1
	 * 
	 * @param mat Material to be used when rendering the cylinder
	 */
	public CSGInfiniteCylinder(Material mat)
	{
		this.mat = mat;
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> bounds = new ArrayList<IntervalBoundary>(2);
		
		IntervalBoundary b1, b2;
		b1 = new IntervalBoundary();
		b2 = new IntervalBoundary();
		
		float a, b, c, det;
		a = r.direction.x*r.direction.x + r.direction.z*r.direction.z;
		b = 2*(r.direction.x * r.origin.x  + r.direction.z * r.origin.z);
		c = r.origin.x*r.origin.x + r.origin.z*r.origin.z - 1;
		det = b*b - 4*a*c;
		
		if(!(det > 0))	// no intersection (case det = 0 included)
		{
			if(r.origin.x*r.origin.x + r.origin.z*r.origin.z < 1)	// ray completely inside
			{
				b1.hitRecord = null;
				b1.type = BoundaryType.START;
				b1.t = Float.NEGATIVE_INFINITY;
				
				b2.hitRecord = null;
				b2.type = BoundaryType.END;
				b2.t = Float.POSITIVE_INFINITY;
				
				bounds.add(b1);
				bounds.add(b2);
			}
			return bounds;
		}
		
		det = (float) Math.sqrt(det);
		
		float t1 = (-b - det)/2/a;
		float t2 = (-b + det)/2/a;
		
		b1.hitRecord = createHitRecord(r, t1);
		b1.type = BoundaryType.START;
		b1.t = t1;
		
		b2.hitRecord = createHitRecord(r, t1);
		b2.type = BoundaryType.END;
		b2.t = t2;
		
		bounds.add(b1);
		bounds.add(b2);
		
		return bounds;
	}

	private HitRecord createHitRecord(Ray r, float t)
	{
		HitRecord hr = new HitRecord();
		
		hr.intersectable = this;
		hr.material = mat;
		hr.w = new Vector3f(r.direction);
		hr.w.negate();
		hr.w.normalize();
		
		hr.t = t;
		
		hr.position = new Vector3f(r.origin.x + r.direction.x*t, r.origin.y + r.direction.y*t, r.origin.z + r.direction.z*t);
		
		hr.normal = new Vector3f(hr.position);
		hr.normal.y = 0;
		hr.normal.normalize();
		
		hr.t1 = new Vector3f(0,1,0);
		
		hr.t2 = new Vector3f(0,1,0);
		hr.t2.cross(hr.t2, hr.normal);
		hr.t2.normalize();
		
		return hr;
	}

}
