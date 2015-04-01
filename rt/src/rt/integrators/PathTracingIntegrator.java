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

/**
 * Integrator for path tracing.
 */
public class PathTracingIntegrator implements Integrator {

	LightList lightList;
	Intersectable root;
	Sampler sampler;
	
	public PathTracingIntegrator(Scene scene, Sampler sampler)
	{
		this.lightList = scene.getLightList();
		this.root = scene.getIntersectable();
		this.sampler = sampler;
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
			float[][] samples = sampler.makeSamples(1, 2);
			HitRecord lightHit = lightSource.sample(samples[0]);
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
			
			// Multiply with cosine of surface normal and incident direction
			float ndotl = hitRecord.normal.dot(lightDir);
			ndotl = Math.max(ndotl, 0.f);
			s.mult(ndotl/15.0f);
			
			// Accumulate
			outgoing.add(s);
		}
		return outgoing;	
	}

	public float[][] makePixelSamples(Sampler sampler, int n) {
		return sampler.makeSamples(n, 2);
	}

}
