package rt.intersectables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import rt.BoundingBox;
import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;

public class BSPAccelerator implements Intersectable
{
	protected BSPNode root;
	protected ArrayList<Intersectable> infinites = null;
	
	public BSPAccelerator(Aggregate aggr)
	{
		infinites = new ArrayList<Intersectable>();
		Iterator<Intersectable> it = aggr.iterator();
		if(!it.hasNext())
		{
			return;
		}
		
		BoundingBox bound = null, tempbound = null;
		int treecount = 0;
		for(Intersectable i = it.next(); it.hasNext(); i = it.next())
		{
			tempbound = i.getBoundingBox();
			if(tempbound == null)
			{
				infinites.add(i);
			}else
			{
				if(bound == null)
				{
					bound = new BoundingBox(tempbound);
				}else
				{
					bound.combinationWith(tempbound);
				}
				treecount++;
			}
		}
		
		if(bound == null)
		{
			return;
		}
		
		int maxdepth = (int) Math.ceil(8 + 1.3*Math.log(treecount));
		
		root = new BSPNode(bound,1);
		it = aggr.iterator();
		for(Intersectable i = it.next(); it.hasNext(); i = it.next())
		{
			tempbound = i.getBoundingBox();
			if(tempbound != null)
			{
				root.add(i, maxdepth);	
			}
		}
	}
	
	@Override
	public HitRecord intersect(Ray r)
	{
		HitRecord hitRecord = null;
		float t = Float.MAX_VALUE;
		
		// Intersect all objects in infinites, return closest hit
		for(Intersectable o:infinites)
		{
			HitRecord tmp = o.intersect(r);
			if(tmp!=null && tmp.t<t)
			{
				t = tmp.t;
				hitRecord = tmp;
			}
		}
		
		Stack<BSPNode> nodestack = new Stack<BSPNode>();
		nodestack.push(root);
		
		/* intersect all
		BSPNode node;
		while(!nodestack.isEmpty())
		{
			node = nodestack.pop();
			if(node.left == null)
			{
				for(Intersectable o:node.list)
				{
					HitRecord tmp = o.intersect(r);
					if(tmp!=null && tmp.t<t)
					{
						t = tmp.t;
						hitRecord = tmp;
					}
				}
			}else
			{
				nodestack.push(node.left);
				nodestack.push(node.right);
			}
		}
		/*/// intersect optimized
		BSPNode node;
		float n1,n2;
		boolean newHRfound = false;
		while(!nodestack.isEmpty())
		{
			node = nodestack.pop();
			if(node.left == null)
			{
				for(Intersectable o:node.list)
				{
					HitRecord tmp = o.intersect(r);
					if(tmp!=null && tmp.t<t)
					{
						t = tmp.t;
						hitRecord = tmp;
						newHRfound = true;
					}
				}
				if(newHRfound)
				{
					return hitRecord;	// stack is sorted -> this is the best hit
				}
			}else
			{	// put on stack sorted
				n1 = node.left.bound.intersect(r);
				n2 = node.right.bound.intersect(r);
				if(n1 > n2)
				{
					if(!(n1 < 0))
					{
						nodestack.push(node.left);
					}
					if(!(n2 < 0))
					{
						nodestack.push(node.right);
					}
				}else
				{
					if(!(n2 < 0))
					{
						nodestack.push(node.right);
					}
					if(!(n1 < 0))
					{
						nodestack.push(node.left);
					}
				}
			}
		}
		// */
		return hitRecord;
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		if(infinites.isEmpty())
		{
			return root.bound;
		}
		return null;
	}

}
