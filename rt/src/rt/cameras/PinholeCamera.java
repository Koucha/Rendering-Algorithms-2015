package rt.cameras;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import rt.Camera;
import rt.Ray;

/**
 * A simple camera
 * Position and view frustum are defined upon construction.
 */
public class PinholeCamera implements Camera{

	Vector3f eye;
	Matrix3f rayMat;
	int width;
	int height;
	float distance;
	
	/**
	 * Makes a camera with position and view frustum according to parameters.
	 * (The definition of aspect, width and height allows non quadratic pixels)
	 * 
	 * @param eye position of the pinhole
	 * @param lookAt point the camera should be centered on
	 * @param up vector indicating the upwards direction of the camera
	 * @param fov "Vertical field of view" vertical opening angle of the Frustum at the pinhole
	 * @param aspect aspect ratio of the film
	 * @param width width of the film in pixels
	 * @param height height of the film in pixels
	 */
	public PinholeCamera(Vector3f eye, Vector3f lookAt, Vector3f up, float fov,
			float aspect, int width, int height)
	{
		this.eye = (Vector3f) eye.clone();
		this.width = width;
		this.height = height;
		
		Vector3f zaxis = new Vector3f(eye.x - lookAt.x,
									eye.y - lookAt.y,
									eye.z - lookAt.z);
		zaxis.normalize();
		
		Vector3f xaxis = new Vector3f();
		xaxis.cross(up, zaxis);
		xaxis.normalize();
		
		Vector3f yaxis = new Vector3f();
		yaxis.cross(zaxis, xaxis);
		
		distance = (float) (-height/2/Math.tan(fov/2/180*Math.PI));
		
		rayMat = new Matrix3f();
		rayMat.m00 = xaxis.x; rayMat.m01 = yaxis.x; rayMat.m02 = zaxis.x;
		rayMat.m10 = xaxis.y; rayMat.m11 = yaxis.y; rayMat.m12 = zaxis.y;
		rayMat.m20 = xaxis.z; rayMat.m21 = yaxis.z; rayMat.m22 = zaxis.z;
		
	}

	/**
	 * Make a world space ray. The method receives a sample that 
	 * the camera uses to generate the ray. It uses the first two
	 * sample dimensions to sample a location in the current 
	 * pixel. The samples are assumed to be in the range [0,1].
	 * 
	 * @param i column index of pixel through which ray goes (0 = left boundary)
	 * @param j row index of pixel through which ray goes (0 = bottom boundary)
	 * @param sample random sample that the camera can use to generate a ray   
	 */
	public Ray makeWorldSpaceRay(int i, int j, float[] sample) {
		// Make point on image plane in viewport coordinates, that is range [0,width-1] x [0,height-1]
		// The assumption is that pixel [i,j] is the square [i,i+1] x [j,j+1] in viewport coordinates
		Vector3f dir = new Vector3f((float)i+sample[0]-width/2.f, (float)j+sample[1]-height/2.f, distance);
		
		// Transform it back to world coordinates
		rayMat.transform(dir);
		
		// Make ray consisting of origin and direction in world coordinates
		dir.normalize();
		Ray r = new Ray(new Vector3f(eye), dir);
		return r;
	}

}
