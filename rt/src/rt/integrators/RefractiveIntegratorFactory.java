package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes a {@link RefractiveIntegrator}.
 */
public class RefractiveIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new RefractiveIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
