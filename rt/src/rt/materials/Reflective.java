package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Reflective implements Material {

	Spectrum kd;
	Spectrum ks;
	Spectrum ka;
	float s;
	float r;
	
	/**
	 * Creates a Blinn material with reflection
	 * The BRDF is calculated according to Blinns formula
	 * 
	 * @param kd	diffuse colour
	 * @param ks	spectral colour
	 * @param s		spectral exponent
	 * @param r		reflectance [0,1] where 0 - no reflection and 1 - full reflection
	 */
	public Reflective(Spectrum kd, Spectrum ks, float s, float r)
	{
		this.kd = new Spectrum(kd);
		// Normalize
		this.kd.mult(0.9f);
		
		this.ks = new Spectrum(ks);
		this.ka = new Spectrum(kd.r*0.05f,kd.g*0.05f,kd.b*0.05f);
		this.s = s;
		this.r = Math.min(Math.max(r, 0), 1);
	}
	
	/**
	 * Returns diffuse BRDF value
	 * 
	 *  @param wOut outgoing direction, by convention towards camera
	 *  @param wIn incident direction, by convention towards light
	 *  @param hitRecord hit record to be used
	 */
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn)
	{
		Spectrum od = new Spectrum(kd);
		Spectrum os = new Spectrum(ks);
		
		// Multiply with cosine of surface normal and incident direction
		float ndotl = hitRecord.normal.dot(wIn);
		ndotl = Math.max(ndotl, 0.f);
		od.mult(ndotl);
		od.add(ka);
		od.mult(1f - r);
		
		// Multiply with cosine of direction towards camera and incident light to the power of the reflectance
		Vector3f h = new Vector3f(wOut);
		h.add(wIn);
		h.normalize();
		float ndoth = hitRecord.normal.dot(h);
		ndoth = Math.max(ndoth, 0.f);
		ndoth = (float) Math.pow(ndoth, s);
		os.mult(ndoth);
		os.mult(r);
		
		od.add(os);
		return od;
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSpecularReflection()
	{
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord)
	{
		ShadingSample ss = new ShadingSample();
		
		ss.w = new Vector3f(hitRecord.normal);
		ss.w.scale(2 * hitRecord.w.dot(hitRecord.normal));
		ss.w.sub(hitRecord.w);
		
		ss.brdf = new Spectrum(1,1,1);
		ss.brdf.mult(r);
		
		return ss;
	}

	@Override
	public boolean hasSpecularRefraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean castsShadows() {
		return true;
	}

}
