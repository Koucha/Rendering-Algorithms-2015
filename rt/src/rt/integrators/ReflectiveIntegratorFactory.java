package rt.integrators;

import rt.Integrator;
import rt.IntegratorFactory;
import rt.Scene;

/**
 * Makes a {@link ReflectiveIntegrator}.
 */
public class ReflectiveIntegratorFactory implements IntegratorFactory {

	public Integrator make(Scene scene) {
		return new ReflectiveIntegrator(scene);
	}

	public void prepareScene(Scene scene) {
		// TODO Auto-generated method stub
	}

}
