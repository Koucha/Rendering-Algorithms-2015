package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.Material;
import rt.Ray;

/**
 * Cylinder that can be used in CSG bodies
 * Size: 1 high and radius 1
 * 
 * @author Florian
 */
public class CSGUnitCylinder extends CSGSolid {

	CSGSolid unitc;
	
	/**
	 * Create a unit cylinder
	 * 
	 * @param mat Material to be used when rendering the cylinder
	 */
	public CSGUnitCylinder(Material mat)
	{
		CSGPlane topPlane = new CSGPlane(new Vector3f(0.f, 1.f, 0.f), -1f, mat);
		CSGPlane bottomPlane = new CSGPlane(new Vector3f(0.f, -1.f, 0.f), -1f, mat);
		CSGInfiniteCylinder cyl = new CSGInfiniteCylinder(mat);

		unitc = new CSGNode(topPlane, bottomPlane, CSGNode.OperationType.INTERSECT);
		unitc = new CSGNode(unitc, cyl, CSGNode.OperationType.INTERSECT);
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		return unitc.getIntervalBoundaries(r);
	}

}
