package rt.testscenes;

import javax.vecmath.*;

import rt.*;
import rt.intersectables.*;
import rt.tonemappers.*;
import rt.integrators.*;
import rt.lightsources.*;
import rt.materials.*;
import rt.samplers.*;
import rt.cameras.*;
import rt.films.*;

public class PathtracingBoxLamp extends Scene {
	
	public PathtracingBoxLamp()
	{	
		outputFilename = new String("../output/testscenes/PathtracingBoxLamp");
				
		// Specify pixel sampler to be used
		samplerFactory = new RandomSamplerFactory();
		
		// Samples per pixel
		SPP = 512;
		outputFilename = outputFilename + " " + String.format("%d", SPP) + "SPP";
		
		// Make camera and film
		Vector3f eye = new Vector3f(-3.f,1.f,4.f);
		Vector3f lookAt = new Vector3f(0.f,1.f,0.f);
		Vector3f up = new Vector3f(0.f,1.f,0.f);
		float fov = 60.f;
		int width = 512;
		int height = 512;
		float aspect = (float)width/(float)height;
		camera = new PinholeCamera(eye, lookAt, up, fov, aspect, width, height);
		film = new AreaFilterFilm(width, height);						
		tonemapper = new ClampTonemapper();
		
		// Specify integrator to be used
//		integratorFactory = new BDPathTracingIntegratorFactory(this);//TODO
		integratorFactory = new PathTracingIntegratorFactory();
		
		// List of objects
		IntersectableList objects = new IntersectableList();	
						
		Rectangle rectangle = new Rectangle(new Vector3f(2.f, -.75f, 2.f), new Vector3f(0.f, 4.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
	
		// Bottom
		rectangle = new Rectangle(new Vector3f(-2.f, -.75f, 2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 0.f, -4.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);

		// Top
		rectangle = new Rectangle(new Vector3f(-2.f, 3.25f, 2.f), new Vector3f(0.f, 0.f, -4.f), new Vector3f(4.f, 0.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		rectangle = new Rectangle(new Vector3f(-2.f, -.75f, -2.f), new Vector3f(4.f, 0.f, 0.f), new Vector3f(0.f, 4.f, 0.f));
		rectangle.material = new Diffuse(new Spectrum(0.8f, 0.8f, 0.8f));
//			rectangle.material = new MirrorMaterial(new Spectrum(0.8f, 0.8f, 0.8f));
		objects.add(rectangle);
		
		// Add objects
		Material mat = new Diffuse(new Spectrum(0.5f, 0.8f, 0.5f));
		Matrix4f trafo = new Matrix4f();
		trafo.setIdentity();
		trafo.setTranslation(new Vector3f(0.f, 1.f, 0.f));
		CSGNode plane = new CSGNode(new CSGPlane(new Vector3f(1,0,0), -0.5f, mat), new CSGPlane(new Vector3f(-1,0,0), -0.5f, mat), CSGNode.OperationType.INTERSECT);
		CSGNode sphere = new CSGNode(new CSGSphere(new Vector3f(0,0,0), 1.3f, mat), new CSGSphere(new Vector3f(0,0,0), 1.29f, mat), CSGNode.OperationType.SUBTRACT);
		CSGNode lamp = new CSGNode(sphere, plane, CSGNode.OperationType.SUBTRACT);
		CSGInstance thelamp = new CSGInstance(lamp, trafo);
		objects.add(thelamp);
		
		// List of lights
		lightList = new LightList();
		
		RectangleLight rectangleLight = new RectangleLight(new Vector3f(-0.25f, 1.f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.5f, 0.f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		rectangleLight = new RectangleLight(new Vector3f(0.25f, 1.f, -0.25f), new Vector3f(-0.5f, 0.f, 0.f), new Vector3f(0.f, 0.5f, 0.f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		rectangleLight = new RectangleLight(new Vector3f(-0.25f, 1.5f, 0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.f, -0.5f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		rectangleLight = new RectangleLight(new Vector3f(-0.25f, 1.f, -0.25f), new Vector3f(0.5f, 0.f, 0.f), new Vector3f(0.f, 0.f, 0.5f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		rectangleLight = new RectangleLight(new Vector3f(0.25f, 1.f, 0.25f), new Vector3f(0.f, 0.f, -0.5f), new Vector3f(0.f, 0.5f, 0.f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		rectangleLight = new RectangleLight(new Vector3f(-0.25f, 1.f, -0.25f), new Vector3f(0.f, 0.f, 0.5f), new Vector3f(0.f, 0.5f, 0.f), new Spectrum(30.f, 30.f, 30.f));
		objects.add(rectangleLight);
		lightList.add(rectangleLight);
		
		// Connect objects to root
		root = objects;
	}
	
	public void finish()
	{
//		if(integratorFactory instanceof BDPathTracingIntegratorFactory)//TODO
//		{
//			((BDPathTracingIntegratorFactory)integratorFactory).writeLightImage("../output/testscenes/lightimage");
//			((BDPathTracingIntegratorFactory)integratorFactory).addLightImage(film);
//		}
	}
	
}
