package rt.intersectables;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;

/**
 * Instances hold Instance specific data and a reference to the instanced Intersectable
 * 
 * @author Florian
 */
public class Instance implements Intersectable
{
	Intersectable intersectable;
	Matrix4f rayt;
	Matrix4f t;
	Matrix4f normalt;
	
	/**
	 * Create a new Instance of the "Primitive" intersectable transformed by the matrix t
	 * 
	 * @param intersectable primitive to be instanced
	 * @param t object to world transformation applied to the new instance
	 */
	public Instance(Intersectable intersectable, Matrix4f t)
	{
		this.intersectable = intersectable;
		this.t= new Matrix4f(t);
		rayt = new Matrix4f(t);
		rayt.invert();
		normalt = new Matrix4f(rayt);
		normalt.transpose();
	}

	@Override
	public HitRecord intersect(Ray r)
	{
		// transform ray to intersectable coordinates
		Point3f origin = new Point3f(r.origin);
		Vector3f direction = new Vector3f(r.direction);
		rayt.transform(origin);
		rayt.transform(direction);
		
		HitRecord hit = intersectable.intersect(new Ray(new Vector3f(origin), direction));
		
		if(hit == null)
		{
			return null;
		}
		
		// transform HitRecord content from intersectable coordinates
		origin = new Point3f(hit.position);
		t.transform(origin);
		hit.position = new Vector3f(origin);
		
		t.transform(hit.t1);
		t.transform(hit.t2);
		normalt.transform(hit.normal);
		
		return hit;
	}

}
