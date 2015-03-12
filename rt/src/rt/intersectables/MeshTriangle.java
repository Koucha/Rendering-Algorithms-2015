package rt.intersectables;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;

/**
 * Defines a triangle by referring back to a {@link Mesh}
 * and its vertex and index arrays. 
 */
public class MeshTriangle implements Intersectable {

	private Mesh mesh;
	private int index;
	
	/**
	 * Make a triangle.
	 * 
	 * @param mesh the mesh storing the vertex and index arrays
	 * @param index the index of the triangle in the mesh
	 */
	public MeshTriangle(Mesh mesh, int index)
	{
		this.mesh = mesh;
		this.index = index;		
	}
	
	public HitRecord intersect(Ray r)
	{
		HitRecord hr = new HitRecord();
		
		hr.intersectable = mesh;
		hr.material = mesh.material;
		hr.w = new Vector3f(r.direction);
		hr.w.negate();
		hr.w.normalize();
		
		float vertices[] = mesh.vertices;
		float normals[] = mesh.normals;
		
		// Access the triangle vertices as follows (same for the normals):		
		// 1. Get three vertex indices for triangle
		int v0 = mesh.indices[index*3];
		int v1 = mesh.indices[index*3+1];
		int v2 = mesh.indices[index*3+2];
		
		// 2. Access x,y,z coordinates for each vertex
		float x0 = vertices[v0*3];
		float x1 = vertices[v1*3];
		float x2 = vertices[v2*3];
		float y0 = vertices[v0*3+1];
		float y1 = vertices[v1*3+1];
		float y2 = vertices[v2*3+1];
		float z0 = vertices[v0*3+2];
		float z1 = vertices[v1*3+2];
		float z2 = vertices[v2*3+2];
		
		// 2. Access x,y,z coordinates for each normal
		float nx0 = normals[v0*3];
		float nx1 = normals[v1*3];
		float nx2 = normals[v2*3];
		float ny0 = normals[v0*3+1];
		float ny1 = normals[v1*3+1];
		float ny2 = normals[v2*3+1];
		float nz0 = normals[v0*3+2];
		float nz1 = normals[v1*3+2];
		float nz2 = normals[v2*3+2];
		
		Matrix3f mat = new Matrix3f(x0 - x1, x0 - x2, r.direction.x, 
									y0 - y1, y0 - y2, r.direction.y, 
									z0 - z1, z0 - z2, r.direction.z);
		mat.invert();
		Vector3f v = new Vector3f(x0 - r.origin.x, y0 - r.origin.y, z0 - r.origin.z);
		mat.transform(v);
		
		float beta = v.x;
		float gamma = v.y;
		float alpha = 1 - beta - gamma;
		hr.t = v.z;
		
		if(beta < 0 || gamma < 0 || beta + gamma > 1 || hr.t < 0)	// no hit
		{
			return null;
		}
		
		hr.position = new Vector3f(r.origin.x + r.direction.x*hr.t, r.origin.y + r.direction.y*hr.t, r.origin.z + r.direction.z*hr.t);
		
		hr.normal = new Vector3f(alpha*nx0 + beta*nx1 + gamma*nx2,
								 alpha*ny0 + beta*ny1 + gamma*ny2,
								 alpha*nz0 + beta*nz1 + gamma*nz2);
		hr.normal.normalize();
		
		hr.t1 = new Vector3f(x1 - x0, y1 - y0, z1 - z0);
		hr.t1.normalize();
		
		hr.t2 = new Vector3f();
		hr.t2.cross(hr.normal, hr.t1);
		
		return hr;
	}
	
}
