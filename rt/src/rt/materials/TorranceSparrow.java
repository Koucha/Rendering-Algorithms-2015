package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class TorranceSparrow implements Material {

	Spectrum kd;
	Spectrum ks;
	Spectrum ka;
	float e;
	float k;
	float n;
	
	/**
	 * Creates a Torrance-Sparrow material
	 * 
	 * @param e		spectral exponent
	 * @param n		reflectance
	 * @param k		absorbance
	 */
	public TorranceSparrow(float e, float n, float k)
	{
		this.kd = new Spectrum(0,0,0);
		this.ks = new Spectrum(0.3f,0.3f,0.3f);
		this.ka = new Spectrum(kd.r*0.05f,kd.g*0.05f,kd.b*0.05f);
		this.e = e;
		this.k = k;
		this.n = n;
	}
	
	/**
	 * Creates a Torrance-Sparrow material
	 * 
	 * @param kd	diffuse colour
	 * @param ks	spectral colour
	 * @param e		spectral exponent
	 * @param n		reflectance
	 * @param k		absorbance
	 */
	public TorranceSparrow(Spectrum kd, Spectrum ks, float e, float n, float k)
	{
		this.kd = new Spectrum(kd);
		// Normalize
		this.kd.mult(0.9f);
		
		this.ks = new Spectrum(ks);
		this.ka = new Spectrum(kd.r*0.05f,kd.g*0.05f,kd.b*0.05f);
		this.e = e;
		this.k = k;
		this.n = n;
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn)
	{
		float ndoti = hitRecord.normal.dot(wIn);
		Vector3f h = new Vector3f(wOut);
		h.add(wIn);
		h.normalize();
		float ndoth = hitRecord.normal.dot(h);
		float ndoto = hitRecord.normal.dot(wOut);
		float odoth = wOut.dot(h);
		
		float fresnel = ((((n*n + k*k)*ndoto*ndoto-2*n*ndoto+1)/((n*n + k*k)*ndoto*ndoto+2*n*ndoto+1))+(((n*n + k*k)-2*n*ndoto+ndoto*ndoto)/((n*n + k*k)+2*n*ndoto+ndoto*ndoto)))/2;
		
		Spectrum od = new Spectrum(kd);
		Spectrum os = new Spectrum(ks);
		
		// Multiply with cosine of surface normal and incident direction
		ndoti = Math.max(ndoti, 0.f);
		od.mult(ndoti);
		od.add(ka);
		od.mult(1f - fresnel);
		
		// Multiply with cosine of direction towards camera and incident light to the power of the reflectance
		os.mult(Math.min(1, Math.min(2*ndoth*ndoto/odoth, 2*ndoth*ndoti/odoth)));
		os.mult((float) ((e+2)/(2*Math.PI)*Math.pow(ndoth, e)));
		os.mult(fresnel);
		os.mult(1/(4*ndoto*ndoti));
		
		od.add(os);
		return od;
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSpecularReflection()
	{
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord)
	{
		return null;
	}

	@Override
	public boolean hasSpecularRefraction()
	{
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord)
	{
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample)
	{
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
