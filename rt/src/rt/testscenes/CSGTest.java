package rt.testscenes;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import rt.*;
import rt.films.*;
import rt.integrators.*;
import rt.intersectables.*;
import rt.lightsources.PointLight;
import rt.materials.Diffuse;
import rt.samplers.*;
import rt.tonemappers.*;
import rt.cameras.*;

/**
 * Test for instancing functionality. Note: the spheres look strange (elliptical) and
 * it looks like they intersect a bit because of perspective projection!
 */
public class CSGTest extends Scene {

	public CSGTest()
	{
		// Output file name
		outputFilename = new String("../output/testscenes/CSGTest");
		
		// Image width and height in pixels
		width = 1280;
		height = 720;
		
		// Number of samples per pixel
		SPP = 1;
		
		// Specify which camera, film, and tonemapper to use
		Vector3f eye = new Vector3f(0.f, 0.f, 3.f);
		Vector3f lookAt = new Vector3f(0.f, 0.f, 0.f);
		Vector3f up = new Vector3f(0.f, 1.f, 0.f);
		float fov = 60.f;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new BoxFilterFilm(width, height);
		tonemapper = new ClampTonemapper();
		
		// Specify which integrator and sampler to use
		integratorFactory = new PointLightIntegratorFactory();
		samplerFactory = new OneSamplerFactory();
			
		// Make sphere and instances; the default sphere is a unit sphere
		// placed at the origin
		CSGTwoSidedInfiniteCone sphere = new CSGTwoSidedInfiniteCone(new Diffuse(new Spectrum(1.f, 0.8f, 0.2f)));
		//sphere.material = new Diffuse(new Spectrum(1.f, 0.8f, 0.2f));
				
		IntersectableList intersectableList = new IntersectableList();
		intersectableList.add(sphere);
		
		root = intersectableList;
		
		// Light sources
		Vector3f lightPos = new Vector3f(eye);
		lightPos.add(new Vector3f(-1.f, 0.f, 0.f));
		LightGeometry pointLight1 = new PointLight(lightPos, new Spectrum(14.f, 14.f, 14.f));
		lightList = new LightList();
		lightList.add(pointLight1);
	}
}
