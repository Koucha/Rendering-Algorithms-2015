package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Sampler;
import rt.Scene;
import rt.samplers.RandomSampler;

/**
 * Makes a {@link PathTracingIntegrator}.
 */
public class PathTracingIntegratorFactory implements IntegratorFactory {
	
	Sampler sampler;
	
	public PathTracingIntegratorFactory()
	{
		sampler = new RandomSampler();
	}
	
	public Integrator make(Scene scene) {
		return new PathTracingIntegrator(scene, sampler);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
