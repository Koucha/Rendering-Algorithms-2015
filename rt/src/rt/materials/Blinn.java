package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Blinn implements Material {

	Spectrum kd;
	Spectrum ks;
	Spectrum ka;
	float s;
	
	/**
	 * Creates a Blinn material
	 * The BRDF is calculated according to Blinns formula
	 * 
	 * @param kd	diffuse colour
	 * @param ks	spectral colour
	 * @param s		spectral exponent
	 */
	public Blinn(Spectrum kd, Spectrum ks, float s)
	{
		this.kd = new Spectrum(kd);
		// Normalize
		this.kd.mult(0.9f);
		
		this.ks = new Spectrum(ks);
		this.ka = new Spectrum(kd.r*0.05f,kd.g*0.05f,kd.b*0.05f);
		this.s = s;
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
		
		// Multiply with cosine of direction towards camera and incident light to the power of the reflectance
		Vector3f h = new Vector3f(wOut);
		h.add(wIn);
		h.normalize();
		float ndoth = hitRecord.normal.dot(h);
		ndoth = Math.max(ndoth, 0.f);
		ndoth = (float) Math.pow(ndoth, s);
		os.mult(ndoth);
		
		od.add(os);
		od.add(ka);
		return od;
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

	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return new Spectrum(0.f, 0.f, 0.f);
	}

	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return new ShadingSample();
	}

	@Override
	public boolean castsShadows() {
		return true;
	}

}
