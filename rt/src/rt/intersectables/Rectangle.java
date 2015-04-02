package rt.intersectables;

import javax.vecmath.*;

import rt.BoundingBox;
import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;

/**
 * A rectangle that can be intersected by a ray.
 */
public class Rectangle implements Intersectable {

	Vector3f bottomLeft;
	Vector3f right;
	float rightlen;
	Vector3f top;
	float toplen;
	Vector3f normal;
	float d;
	BoundingBox bound;
	public Material material;

	/**
	 * Creates a Rhomboid surface
	 * 
	 * @param bl Bottom left corner
	 * @param r Vector from bottom left to bottom right corner
	 * @param t Vector from bottom left to top left corner
	 */
	public Rectangle(Vector3f bl, Vector3f r, Vector3f t)
	{
		bound = new BoundingBox(min(bl.x, bl.x + r.x, bl.x + t.x, bl.x + r.x + t.x),
								max(bl.x, bl.x + r.x, bl.x + t.x, bl.x + r.x + t.x),
								min(bl.y, bl.y + r.y, bl.y + t.y, bl.y + r.y + t.y),
								max(bl.y, bl.y + r.y, bl.y + t.y, bl.y + r.y + t.y),
								min(bl.z, bl.z + r.z, bl.z + t.z, bl.z + r.z + t.z),
								max(bl.z, bl.z + r.z, bl.z + t.z, bl.z + r.z + t.z));
		
		bottomLeft = new Vector3f(bl);
		right = new Vector3f(r);
		right.normalize();
		rightlen = r.length();
		top = new Vector3f(t);
		top.normalize();
		toplen = t.length();
		normal = new Vector3f();
		normal.cross(right, top);
		d = -normal.dot(bl);
	}

	public HitRecord intersect(Ray r) {

		float tmp = normal.dot(r.direction);

		if (tmp != 0) {
			float t = -(normal.dot(r.origin) + d) / tmp;
			if (t <= 0)
				return null;
			Vector3f position = new Vector3f(r.direction);
			position.scaleAdd(t, r.origin);
			Vector3f ctop = new Vector3f(position);
			ctop.sub(bottomLeft);
			float u = right.dot(ctop);
			float v = top.dot(ctop);
			
			if(u < 0 || v < 0 || u > rightlen || v > toplen)
			{
				return null;
			}
			
			Vector3f retNormal = new Vector3f(normal);
			if(normal.dot(r.direction) > 0)
			{
				retNormal.negate();
			}
			// wIn is incident direction; convention is that it points away from
			// surface
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
			wIn.normalize();
			return new HitRecord(t, position, retNormal, wIn, this, material, u, v);
		} else {
			return null;
		}
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		return bound;
	}
	
	private float min(float a, float b, float c, float d)
	{
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}
	
	private float max(float a, float b, float c, float d)
	{
		return Math.max(a, Math.max(b, Math.max(c, d)));
	}

}
