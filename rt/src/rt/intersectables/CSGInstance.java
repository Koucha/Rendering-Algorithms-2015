package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.BoundingBox;
import rt.HitRecord;
import rt.Ray;

public class CSGInstance extends CSGSolid{

	CSGSolid base;
	Matrix4f rayt;
	Matrix4f t;
	Matrix4f normalt;
	BoundingBox bound;
	
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
		
		bound = base.getBoundingBox();
		if(bound != null)
		{
			Point3f p1 = new Point3f(bound.getMinx(), bound.getMiny(), bound.getMinz());
			Point3f p2 = new Point3f(bound.getMaxx(), bound.getMiny(), bound.getMinz());
			Point3f p3 = new Point3f(bound.getMinx(), bound.getMaxy(), bound.getMinz());
			Point3f p4 = new Point3f(bound.getMaxx(), bound.getMaxy(), bound.getMinz());
			Point3f p5 = new Point3f(bound.getMinx(), bound.getMiny(), bound.getMaxz());
			Point3f p6 = new Point3f(bound.getMaxx(), bound.getMiny(), bound.getMaxz());
			Point3f p7 = new Point3f(bound.getMinx(), bound.getMaxy(), bound.getMaxz());
			Point3f p8 = new Point3f(bound.getMaxx(), bound.getMaxy(), bound.getMaxz());
			t.transform(p1);
			t.transform(p2);
			t.transform(p3);
			t.transform(p4);
			t.transform(p5);
			t.transform(p6);
			t.transform(p7);
			t.transform(p8);
			bound = new BoundingBox(min(p1.x,p2.x,p3.x,p4.x,p5.x,p6.x,p7.x,p8.x),
									max(p1.x,p2.x,p3.x,p4.x,p5.x,p6.x,p7.x,p8.x),
									min(p1.y,p2.y,p3.y,p4.y,p5.y,p6.y,p7.y,p8.y),
									max(p1.y,p2.y,p3.y,p4.y,p5.y,p6.y,p7.y,p8.y),
									min(p1.z,p2.z,p3.z,p4.z,p5.z,p6.z,p7.z,p8.z),
									max(p1.z,p2.z,p3.z,p4.z,p5.z,p6.z,p7.z,p8.z));
		}
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

	@Override
	public BoundingBox getBoundingBox()
	{
		return bound;
	}

	private float min(float a, float b, float c, float d, float e, float f, float g, float h)
	{
		return Math.min(a, Math.min(b, Math.min(c, Math.min(d, Math.min(e, Math.min(f, Math.min(g, h)))))));
	}
	
	private float max(float a, float b, float c, float d, float e, float f, float g, float h)
	{
		return Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, Math.max(f, Math.max(g, h)))))));
	}
	
}
