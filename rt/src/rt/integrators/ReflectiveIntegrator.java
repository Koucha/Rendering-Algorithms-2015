package rt.integrators;

import java.util.Iterator;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Integrator;
import rt.Intersectable;
import rt.LightList;
import rt.LightGeometry;
import rt.Ray;
import rt.Sampler;
import rt.Scene;
import rt.Spectrum;
import rt.StaticVecmath;
import rt.Material.ShadingSample;

/**
 * Integrator for Whitted style ray tracing.
 */
public class ReflectiveIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	
	public ReflectiveIntegrator(Scene scene)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
	}

	/**
	 * TODO
	 */
	public Spectrum integrate(Ray r) {

		HitRecord hitRecord = root.intersect(r);
		// immediately return background color if nothing was hit
		if(hitRecord == null) { 
			return new Spectrum(0,0,0);
		}	
		Spectrum outgoing = new Spectrum(0.f, 0.f, 0.f);	
		// Iterate over all light sources
		Iterator<LightGeometry> it = lightList.iterator();
		while(it.hasNext()) {
			LightGeometry lightSource = it.next();
			
			// Make direction from hit point to light source position; this is only supposed to work with point lights
			float dummySample[] = new float[2];
			HitRecord lightHit = lightSource.sample(dummySample);
			Vector3f lightDir = StaticVecmath.sub(lightHit.position, hitRecord.position);
			float d2 = lightDir.lengthSquared();
			lightDir.normalize();
			
			Vector3f approxpos = new Vector3f(hitRecord.normal);
			approxpos.scale(0.000001f);
			approxpos.add(hitRecord.position);
			HitRecord shadowHit = root.intersect(new Ray(approxpos, new Vector3f(lightDir)));
			
			if(shadowHit != null && shadowHit.t*shadowHit.t < d2 - 0.000001f)	// shadowCast
			{
				continue;
			}
			
			// Evaluate the BRDF
			Spectrum brdfValue = hitRecord.material.evaluateBRDF(hitRecord, hitRecord.w, lightDir);
			
			// Multiply together factors relevant for shading, that is, brdf * emission * ndotl * geometry term
			Spectrum s = new Spectrum(brdfValue);
			
			// Multiply with emission
			s.mult(lightHit.material.evaluateEmission(lightHit, StaticVecmath.negate(lightDir)));
			
			// Geometry term: multiply with 1/(squared distance), only correct like this 
			// for point lights (not area lights)!
			s.mult(1.f/d2);
			
			// Accumulate
			outgoing.add(s);
		}
		
		if(hitRecord.material.hasSpecularReflection())
		{
			ShadingSample ss = hitRecord.material.evaluateSpecularReflection(hitRecord);
			Vector3f approxpos = new Vector3f(hitRecord.normal);
			approxpos.scale(0.000001f);
			approxpos.add(hitRecord.position);
			ss.brdf.mult(integrate(new Ray(approxpos, new Vector3f(ss.w))));

			// Accumulate
			outgoing.add(ss.brdf);
		}
		
		return outgoing;	
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
