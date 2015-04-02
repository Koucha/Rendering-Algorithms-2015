package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import rt.BoundingBox;
import rt.HitRecord;
import rt.Material;
import rt.Ray;

/**
 * A sphere that can be used in CSG bodies
 * 
 * @author Florian
 */
public class CSGSphere extends CSGSolid {
	
	Vector3f position;
	float radius;
	BoundingBox bound;
	public Material material;

	/**
	 * Constructs a sphere with given radius and position
	 * 
	 * @param position
	 * @param radius
	 */
	public CSGSphere(Vector3f position, float radius)
	{
		this.position = position;
		this.radius = radius;
		bound = new BoundingBox(position, radius);
	}
	
	/**
	 * Constructs a sphere with radius 1 at the point (0,0,0)
	 */
	public CSGSphere()
	{
		position = new Vector3f(0,0,0);
		radius = 1;
		bound = new BoundingBox(-1,1,-1,1,-1,1);
	}
	
	/**
	 * Constructs a sphere with given radius, position and material
	 * 
	 * @param position
	 * @param radius
	 * @param material
	 */
	public CSGSphere(Vector3f position, float radius, Material material)
	{
		this.position = position;
		this.radius = radius;
		this.material = material;
		bound = new BoundingBox(position, radius);
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
ArrayList<IntervalBoundary> bounds = new ArrayList<IntervalBoundary>(2);
		
		IntervalBoundary b1, b2;
		b1 = new IntervalBoundary();
		b2 = new IntervalBoundary();
		
		float a, b, c, det;
		a = r.direction.x*r.direction.x + r.direction.y*r.direction.y + r.direction.z*r.direction.z;
		b = 2 * (r.direction.x * (r.origin.x - position.x) + r.direction.y * (r.origin.y - position.y) + r.direction.z * (r.origin.z - position.z));
		c = (r.origin.x - position.x)*(r.origin.x - position.x) + (r.origin.y - position.y)*(r.origin.y - position.y) + (r.origin.z - position.z)*(r.origin.z - position.z) - radius*radius;
		det = b*b - 4*a*c;
		
		if(!(det > 0))	// no intersection (case det = 0 included)
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

	@Override
	public BoundingBox getBoundingBox()
	{
		return bound;
	}

}
