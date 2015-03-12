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
public class DemoScene extends Scene {
		
	public DemoScene()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/DemoScene");
		
		// Image width and height in pixels
		width = 512;
		height = 512;
		
		// Number of samples per pixel
		SPP = 32;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(1.f, 1.f, 3.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = 1.f;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new RefractiveIntegratorFactory();
		samplerFactory = new RandomSamplerFactory();		

		Material refractive = new Refractive(new Spectrum(0.2f, 0.2f, 0.19f), new Spectrum(.1f, .1f, .1f), 32, 0.6f, 1.05f);

		
		// Ground and back plane
		// A grid with red and white lines, line thickness 0.01, zero offset shift, and tile size 0.125, all in world coordinates
		XYZGrid grid = new XYZGrid(new Spectrum(0.2f, 0.f, 0.f), new Spectrum(1.f, 1.f, 1.f), 0.01f, new Vector3f(0.f, 0.f, 0.f), 0.125f);
		Plane backPlane = new Plane(new Vector3f(0.f, 0.f, 1.f), 2.15f);
		backPlane.material = grid;
		Plane groundPlane = new Plane(new Vector3f(0.f, 1.f, 0.f), 2.15f);
		groundPlane.material = grid;
		
		XYZGrid grid2 = new XYZGrid(new Spectrum(0.0f, 0.f, 0.2f), new Spectrum(1.0f, 1.f, 1.0f), 0.01f, new Vector3f(0.f, 0.f, 0.f), 0.125f);
		Plane bg1Plane = new Plane(new Vector3f(-1.f, -1.f, -1.f), 5f);
		backPlane.material = grid2;
		Plane bg2Plane = new Plane(new Vector3f(1.f, -1.f, 1.f), 5f);
		groundPlane.material = grid2;
		
		// A CSGBody for testing
		CSGSolid body = new CSGDodecahedron(refractive);
		
		// Collect objects in intersectable list
		IntersectableList intersectableList = new IntersectableList();


		intersectableList.add(body);
		intersectableList.add(backPlane);
		intersectableList.add(groundPlane);
		intersectableList.add(bg1Plane);
		intersectableList.add(bg2Plane);
		
		// Set the root node for the scene
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-2.f, 1.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(14.f, 10.f, 10.f));
		lightPos.add(new Vector3f(3.f, 1.f, 0.f));
		LightGeometry pointLight2 = new PointLight(lightPos, new Spectrum(10.f, 14.f, 10.f));
		LightGeometry pointLight3 = new PointLight(new Vector3f(0.f, 3.f, 1.5f), new Spectrum(10.f, 10.f, 14.f));
		lightList = new LightList();
		lightList.add(pointLight1);
		lightList.add(pointLight2);
		lightList.add(pointLight3);
	}
	
}
