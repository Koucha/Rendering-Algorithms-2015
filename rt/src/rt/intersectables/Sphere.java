package rt.intersectables;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;

/**
 * A sphere that can be intersected by a ray
 * 
 * @author Florian
 */
public class Sphere implements Intersectable
{
	Vector3f position;
	float radius;
	public Material material;

	/**
	 * Constructs a sphere with given radius and position
	 * 
	 * @param position
	 * @param radius
	 */
	public Sphere(Vector3f position, float radius)
	{
		this.position = position;
		this.radius = radius;
	}
	
	/**
	 * Constructs a sphere with radius 1 at the point (0,0,0)
	 */
	public Sphere()
	{
		position = new Vector3f(0,0,0);
		radius = 1;
	}

	@Override
	public HitRecord intersect(Ray r)
	{
		HitRecord hr = new HitRecord();
		
		hr.intersectable = this;
		hr.material = material;
		hr.w = new Vector3f(r.direction);
		hr.w.negate();
		hr.w.normalize();
		
		float a, b, c, det;
		a = r.direction.x*r.direction.x + r.direction.y*r.direction.y + r.direction.z*r.direction.z;
		b = 2 * (r.direction.x * (r.origin.x - position.x) + r.direction.y * (r.origin.y - position.y) + r.direction.z * (r.origin.z - position.z));
		c = (r.origin.x - position.x)*(r.origin.x - position.x) + (r.origin.y - position.y)*(r.origin.y - position.y) + (r.origin.z - position.z)*(r.origin.z - position.z) - radius*radius;
		det = b*b - 4*a*c;
		
		if(!(det > 0))	// no intersection
		{
			return null;
		}
		
		det = (float) Math.sqrt(det);
		
		if(-b + det < 0) //both t0 and t1 invalid
		{
			return null;
		}
		if(-b -det < 0)	//t0 invalid
		{
			det = -det;	// test t1 instead
		}
		
		hr.t = (-b - det)/2/a;
		
		hr.position = new Vector3f(r.origin.x + r.direction.x*hr.t, r.origin.y + r.direction.y*hr.t, r.origin.z + r.direction.z*hr.t);
		
		hr.normal = new Vector3f(hr.position);
		hr.normal.sub(position);
		hr.normal.scale(1.f/radius);
		
		hr.t2 = new Vector3f(0,1,0);
		hr.t2.cross(hr.t2, hr.normal);
		hr.t2.normalize();
		
		hr.t1 = new Vector3f();
		hr.t1.cross(hr.normal, hr.t2);
		hr.t1.normalize();
		
		return hr;
	}

}
