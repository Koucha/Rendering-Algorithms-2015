package rt.testscenes;

import rt.*;
import rt.cameras.*;
import rt.films.BoxFilterFilm;
import rt.integrators.*;
import rt.intersectables.*;
import rt.lightsources.*;
import rt.samplers.*;
import rt.materials.*;
import rt.tonemappers.ClampTonemapper;

import javax.vecmath.*;

/**
 * Test scene for refractive objects, renders a sphere in front of a planar background.
 */
public class ReflectiveSphere extends Scene {
		
	public ReflectiveSphere()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/ReflectiveSphere");
		
		// Image width and height in pixels
		width = 512;
		height = 512;
		
		// Number of samples per pixel
		SPP = 32;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.1f, 3.f, 0.3f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 0.f, 1.f);
		float fov = 60.f;
		float aspect = 1.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new ReflectiveIntegratorFactory();//new RefractiveIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();		
		
		Material reflective = new Reflective(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(.3f, .3f, .3f), 21, 0.8f);

		
		// Ground and back plane
		// A grid with red and white lines, line thickness 0.01, zero offset shift, and tile size 0.125, all in world coordinates
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.01f, new Vector3f(0.f, 0.f, 0.f), 0.125f);
		Plane backPlane = new Plane(new Vector3f(0.f, 0.f, 1.f), 2.15f);
		backPlane.material = grid;
		
		// A sphere for testing
		Sphere sphere = new Sphere();
		sphere.material = reflective;
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();

		
		intersectableList.add(sphere);
		intersectableList.add(backPlane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-1.f, 0.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(14.f, 4.f, 4.f));
		lightPos.add(new Vector3f(2.f, 0.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(8.f, 15.f, 10.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(0.f, 0.f, 5.f), new Spectrum(4.f, 4.f, 14.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
	}
	
}