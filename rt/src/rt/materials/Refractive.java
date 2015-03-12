package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Refractive implements Material {

	Spectrum kd;
	Spectrum ks;
	Spectrum ka;
	float s;
	float r;
	float n;
	
	/**
	 * Creates a Blinn material with spectral reflection and spectral refraction
	 * The BRDF is calculated according to Blinns formula
	 * 
	 * @param kd	diffuse colour
	 * @param ks	spectral colour
	 * @param s		spectral exponent
	 * @param r		reflectance [0,1] where 0 - only diffuse and 1 - only spectral
	 * @param n		refractive index of the material
	 */
	public Refractive(float n)
	{
		this.kd = new Spectrum(0,0,0);
		this.ks = new Spectrum(0.3f,0.3f,0.3f);
		this.ka = kd;
		this.s = 21;
		this.r = 1;
		this.n = n;
	}
	
	/**
	 * Creates a Blinn material with spectral reflection and spectral refraction
	 * The BRDF is calculated according to Blinns formula
	 * 
	 * @param kd	diffuse colour
	 * @param ks	spectral colour
	 * @param s		spectral exponent
	 * @param r		reflectance [0,1] where 0 - only diffuse and 1 - only spectral
	 * @param n		refractive index of the material
	 */
	public Refractive(Spectrum kd, Spectrum ks, float s, float r, float n)
	{
		this.kd = new Spectrum(kd);
		// Normalize
		this.kd.mult(0.9f);
		
		this.ks = new Spectrum(ks);
		this.ka = new Spectrum(kd.r*0.05f,kd.g*0.05f,kd.b*0.05f);
		this.s = s;
		this.r = Math.min(Math.max(r, 0), 1);
		this.n = n;
	}

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
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut){
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
	public boolean hasSpecularRefraction()
	{
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord)
	{
		ShadingSample ss = new ShadingSample();
		
		float nt, r0;
		float cosI = hitRecord.normal.dot(hitRecord.w);
		
		if(cosI > 0)	// entering
		{
			nt = 1.0f/n;
			r0 = (1 - n)/(1 + n);
			r0 *= r0;
			ss.w = new Vector3f(hitRecord.normal);
		}else
		{
			nt = n;
			r0 = (n - 1)/(n + 1);
			r0 *= r0;
			cosI *= -1;
			ss.w = new Vector3f(hitRecord.normal);
			ss.w.negate();
		}
		
		float sin2 = nt*nt*(1 - cosI*cosI);
		
		if(sin2 > 1)
		{
			ss.w = null;
			return ss;
		}
		
		float cosT = (float) Math.sqrt(1 - sin2);
		
		ss.w.scale(nt*cosI - cosT);
		Vector3f temp = new Vector3f(hitRecord.w);
		temp.scale(-nt);
		ss.w.add(temp);
		
		ss.brdf = new Spectrum(1,1,1);
		ss.brdf.mult(r);
		
		if(nt > 1)
		{
			cosI = cosT;
		}
		
		float x = 1 - cosI;
		ss.p = r0 + (1 - r0)*x*x*x*x*x;
		
		return ss;
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
