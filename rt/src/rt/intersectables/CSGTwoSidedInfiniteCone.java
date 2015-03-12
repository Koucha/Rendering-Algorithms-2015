package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;

/**
 * Infinite two sided cone with an opening angle of 45°
 * 
 * @author Florian
 */
public class CSGTwoSidedInfiniteCone extends CSGSolid {

	Material material;
	
	/**
	 * Creates a infinite two sided cone with an opening angle of 45°
	 * 
	 * @param mat Material to be used when rendering the cone
	 */
	public CSGTwoSidedInfiniteCone(Material mat)
	{
		material = mat;
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> bounds = new ArrayList<IntervalBoundary>(2);
		
		IntervalBoundary b0, b1, b2, b3;
		b1 = new IntervalBoundary();
		b2 = new IntervalBoundary();
		
		float a, b, c, det;
		a = r.direction.x*r.direction.x - r.direction.y*r.direction.y + r.direction.z*r.direction.z;
		b = 2 * (r.direction.x * r.origin.x - r.direction.y * r.origin.y + r.direction.z * r.origin.z);
		c = r.origin.x*r.origin.x - r.origin.y*r.origin.y + r.origin.z*r.origin.z;
		det = b*b - 4*a*c;
		
		if(!(det > 0))	// no intersection
		{
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
		
		if(b1.hitRecord.position.y < 0 && b2.hitRecord.position.y > 0 ||
		   b1.hitRecord.position.y > 0 && b2.hitRecord.position.y < 0)
		{
			b0 = new IntervalBoundary();
			b3 = new IntervalBoundary();
			
			b0.hitRecord = null;
			b0.t = Float.NEGATIVE_INFINITY;
			b0.type = BoundaryType.START;
			b1.type = BoundaryType.END;
			b2.type = BoundaryType.START;
			b3.hitRecord = null;
			b3.t = Float.POSITIVE_INFINITY;
			b3.type = BoundaryType.END;
			
			bounds.add(b0);
			bounds.add(b3);
		}
		
		bounds.add(b1);
		bounds.add(b2);
		
		return bounds;
	}
	
	private HitRecord createHitRecord(Ray r, float t)
	{
		HitRecord hr = new HitRecord();
		
		hr.intersectable = this;
		hr.material = material;
		hr.w = new Vector3f(r.direction);
		hr.w.negate();
		hr.w.normalize();
		
		hr.t = t;
		
		hr.position = new Vector3f(r.origin.x + r.direction.x*hr.t, r.origin.y + r.direction.y*hr.t, r.origin.z + r.direction.z*hr.t);
		
		hr.t1 = new Vector3f(hr.position);
		hr.t1.normalize();
		
		if(hr.t1.y > 0)
		{
			hr.t2 = new Vector3f(0,1,0);
		}else
		{
			hr.t2 = new Vector3f(0,-1,0);
		}
		hr.t2.cross(hr.t2, hr.t1);
		hr.t2.normalize();
		
		hr.normal = new Vector3f();
		hr.normal.cross(hr.t2, hr.t1);
		hr.normal.normalize();
		
		return hr;
	}

}
