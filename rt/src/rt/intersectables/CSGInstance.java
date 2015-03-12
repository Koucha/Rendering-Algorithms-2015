package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Ray;

public class CSGInstance extends CSGSolid{

	CSGSolid base;
	Matrix4f rayt;
	Matrix4f t;
	Matrix4f normalt;
	
	/**
	 * Create a new Instance of the "Primitive" CSGSolid transformed by the matrix t
	 * 
	 * @param base primitive to be instanced
	 * @param t object to world transformation applied to the new instance
	 */
	public CSGInstance(CSGSolid base, Matrix4f t)
	{
		this.base = base;
		this.t= new Matrix4f(t);
		rayt = new Matrix4f(t);
		rayt.invert();
		normalt = new Matrix4f(rayt);
		normalt.transpose();
	}
	
	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		// transform ray to intersectable coordinates
		Point3f origin = new Point3f(r.origin);
		Vector3f direction = new Vector3f(r.direction);
		rayt.transform(origin);
		rayt.transform(direction);
		
		ArrayList<IntervalBoundary> bounds = base.getIntervalBoundaries(new Ray(new Vector3f(origin), direction));
		
		HitRecord hit;
		for(IntervalBoundary bound:bounds)
		{
			hit = bound.hitRecord;
			if(hit != null)
			{
				// transform HitRecord content from intersectable coordinates
				origin = new Point3f(hit.position);
				t.transform(origin);
				hit.position = new Vector3f(origin);
				
				t.transform(hit.t1);
				t.transform(hit.t2);
				normalt.transform(hit.normal);
			}
			bound.hitRecord = hit;
		}
		
		return bounds;
	}

}
