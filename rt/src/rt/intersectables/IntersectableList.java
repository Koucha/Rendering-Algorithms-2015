package rt.intersectables;

import java.util.LinkedList;
import java.util.Iterator;

import rt.*;

public class IntersectableList extends Aggregate {

	public LinkedList<Intersectable> list;
	public BoundingBox bound;
	
	public IntersectableList()
	{
		list = new LinkedList<Intersectable>();
	}
	
	public void add(Intersectable i)
	{
		if(list.isEmpty())
		{
			bound = i.getBoundingBox();
			if(bound != null)
			{
				bound = new BoundingBox(bound);
			}
		}else if(bound != null)
		{
			bound = bound.combinationWith(i.getBoundingBox());
		}
		
		list.add(i);
	}
	
	public Iterator<Intersectable> iterator() {
		return list.iterator();
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		return bound;
	}

}
