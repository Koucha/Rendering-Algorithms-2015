package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class XYZGrid implements Material {
	
	Spectrum line;
	Spectrum bg;
	float linesize;
	Vector3f offset;
	float tilesize;
	
	public XYZGrid(Spectrum line, Spectrum bg, float linesize,
			Vector3f offset)
	{
		this.line = line;
		this.bg = bg;
		this.linesize = linesize;
		this.offset = offset;
		this.tilesize = 1;
	}

	public XYZGrid(Spectrum line, Spectrum bg, float linesize,
			Vector3f offset, float tilesize)
	{
		this.line = line;
		this.bg = bg;
		this.linesize = linesize;
		this.offset = offset;
		this.tilesize = tilesize;
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn)
	{
		Spectrum od;
		
		float x = Math.abs(hitRecord.position.x - offset.x) + (linesize/2f);
		x = x%tilesize;
		float y = Math.abs(hitRecord.position.y - offset.y) + (linesize/2f);
		y = y%tilesize;
		float z = Math.abs(hitRecord.position.z - offset.z) + (linesize/2f);
		z = z%tilesize;
		
		if(x < linesize || y < linesize  || z < linesize )
		{
			od = new Spectrum(line);
		}else
		{
			od = new Spectrum(bg);
		}
		
		// Multiply with cosine of surface normal and incident direction
		float ndotl = hitRecord.normal.dot(wIn);
		ndotl = Math.max(ndotl, 0.f);
		od.mult(ndotl);
		
		return od;
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return new Spectrum(0,0,0);
	}

	@Override
	public boolean hasSpecularReflection() {
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		return null;
	}

	@Override
	public boolean hasSpecularRefraction() {
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public boolean castsShadows()
	{
		return true;
	}

}
