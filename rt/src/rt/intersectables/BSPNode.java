package rt.intersectables;

import java.util.ArrayList;

import rt.BoundingBox;
import rt.Intersectable;

public class BSPNode
{
	BoundingBox bound;
	BSPNode left;
	BSPNode right;
	int depth;
	
	ArrayList<Intersectable> list;
	
	public BSPNode(BoundingBox bound, int depth)
	{
		this.bound = bound;
		this.depth = depth;
		left = null;
		right = null;
		list = new ArrayList<Intersectable>(3);
	}
	
	public void add(Intersectable obj, int maxdepth)
	{
		if(!bound.intersect(obj.getBoundingBox()))
		{	// no intersection
			return;
		}
		
		if(left == null)
		{
			if(list.size() < 3 || !(depth < maxdepth))
			{
				list.add(obj);
			}else
			{
				float w = bound.getMaxx() - bound.getMinx();
				float h = bound.getMaxy() - bound.getMiny();
				float d = bound.getMaxz() - bound.getMinz();
				
				if(w > h && w > d)
				{
					float m = (bound.getMaxx() + bound.getMinx())/2.0f;
					left = new BSPNode(new BoundingBox(bound.getMinx(),m,bound.getMiny(),bound.getMaxy(),bound.getMinz(),bound.getMaxz()), depth+1);
					right = new BSPNode(new BoundingBox(m,bound.getMaxx(),bound.getMiny(),bound.getMaxy(),bound.getMinz(),bound.getMaxz()), depth+1);
				}else if(h > d)
				{
					float m = (bound.getMaxy() + bound.getMiny())/2.0f;
					left = new BSPNode(new BoundingBox(bound.getMinx(),bound.getMaxx(),m,bound.getMaxy(),bound.getMinz(),bound.getMaxz()), depth+1);
					right = new BSPNode(new BoundingBox(bound.getMinx(),bound.getMaxx(),bound.getMiny(),m,bound.getMinz(),bound.getMaxz()), depth+1);
				}else
				{
					float m = (bound.getMaxz() + bound.getMinz())/2.0f;
					left = new BSPNode(new BoundingBox(bound.getMinx(),bound.getMaxx(),bound.getMiny(),bound.getMaxy(),m,bound.getMaxz()), depth+1);
					right = new BSPNode(new BoundingBox(bound.getMinx(),bound.getMaxx(),bound.getMiny(),bound.getMaxy(),bound.getMinz(),m), depth+1);
				}
				
				for(Intersectable i:list)
				{
					left.add(i, maxdepth);
					right.add(i, maxdepth);
				}
				left.add(obj, maxdepth);
				right.add(obj, maxdepth);
				
				list.clear();
				list = null;
			}
		}else
		{
			left.add(obj, maxdepth);
			right.add(obj, maxdepth);
		}
	}
}
