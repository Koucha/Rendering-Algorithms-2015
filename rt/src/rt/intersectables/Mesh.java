package rt.intersectables;

import java.util.Iterator;

import rt.BoundingBox;
import rt.Intersectable;
import rt.Material;
import rt.Spectrum;
import rt.materials.Diffuse;

/**
 * A triangle mesh. The mesh internally stores the triangles using vertex
 * and index arrays. The mesh also instantiates a {@link MeshTriangle} for each triangle,
 * and the mesh provides an iterator to iterate through the triangles.
 */
public class Mesh extends Aggregate
{
	/**
	 * BoundingBox that contains the whole mesh
	 */
	private BoundingBox bound;
	
	/**
	 * Array of triangle vertices. Stores x,y,z coordinates for each vertex consecutively.
	 */
	public float[] vertices;
	
	/**
	 * Array of triangle normals (one normal per vertex). Stores x,y,z coordinates for each normal consecutively.
	 */
	public float[] normals;
	
	/**
	 * Index array. Each triangle is defined by three consecutive
	 * indices in this array. The indices refer to the {@link Mesh#vertices} 
	 * and {@link Mesh#normals} arrays that store vertex and normal coordinates.
	 */
	public int[] indices;
	
	/**
	 * Array of triangles stored in the mesh.
	 */
	private MeshTriangle[] triangles;
	
	/**
	 * A material.
	 */
	public Material material;
	
	/**
	 * Make a mesh from arrays with vertices, normals, and indices.
	 */
	public Mesh(float[] vertices, float[] normals, int[] indices)
	{
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		
		this.vertices = vertices;
		this.normals = normals;
		this.indices = indices;
		triangles = new MeshTriangle[indices.length/3];
		
		// calculate bounding box
		float xmin = Float.POSITIVE_INFINITY;
		float xmax = Float.NEGATIVE_INFINITY;
		float ymin = Float.POSITIVE_INFINITY;
		float ymax = Float.NEGATIVE_INFINITY;
		float zmin = Float.POSITIVE_INFINITY;
		float zmax = Float.NEGATIVE_INFINITY;
		
		for(int i=0; i<vertices.length/3; i++)
		{
			xmin = Math.min(xmin, vertices[3*i]);
			xmax = Math.max(xmax, vertices[3*i]);
			ymin = Math.min(ymin, vertices[3*i+1]);
			ymax = Math.max(ymax, vertices[3*i+1]);
			zmin = Math.min(zmin, vertices[3*i+2]);
			zmax = Math.max(zmax, vertices[3*i+2]);
		}
		
		this.bound = new BoundingBox(xmin, xmax, ymin, ymax, zmin, zmax);
		
		// A triangle simply stores a triangle index and refers back to the mesh 
		// to look up the vertex data
		for(int i=0; i<indices.length/3; i++)
			triangles[i] = new MeshTriangle(this, i);
	}
	
	public Iterator<Intersectable> iterator() {
		return new MeshIterator(triangles);
	}
	
	private class MeshIterator implements Iterator<Intersectable>
	{
		private int i;
		private MeshTriangle[] triangles;
		
		public MeshIterator(MeshTriangle[] triangles)
		{
			this.triangles = triangles;
			i = 0;
		}
		
		public boolean hasNext()
		{
			return i<triangles.length;
		}
		
		public MeshTriangle next()
		{
			int j = i;
			i++;
			return triangles[j];
		}
		
		public void remove()
		{
		}
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		return bound;
	}
		
}
