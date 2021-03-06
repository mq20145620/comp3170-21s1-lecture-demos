package comp3170.demos.week11.cameras;

import java.awt.event.KeyEvent;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import comp3170.InputManager;

/**
 * A orthographic camera that revolves around the origin
 * @author malcolmryan
 *
 */

public class Camera {

	public static final float TAU = (float) (2 * Math.PI);		// https://tauday.com/tau-manifesto

	private float distance = 1;
	private Vector3f angle = new Vector3f();
	private Vector3f target = new Vector3f();
	
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	
	public Camera(float fovy, float aspect, float near, float far) {
		
		projectionMatrix.setPerspective(fovy, aspect, near, far);
		updateModelMatrix();				
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
		updateModelMatrix();		
	}
	
	public void setTarget(float x, float y, float z) {
		this.target.set(x,y,z);
		updateModelMatrix();		
	}

	public void setAngle(float heading, float pitch) {
		this.angle.set(pitch,heading,0);
		updateModelMatrix();		
	}

	public Matrix4f getModelMatrix(Matrix4f dest) {
		return dest.set(modelMatrix);
	}

	public Matrix4f getViewMatrix(Matrix4f dest) {
		// invert the model matrix (we have never applied any scale)
		return modelMatrix.invert(dest);
	}

	public Matrix4f getProjectionMatrix(Matrix4f dest) {
		return dest.set(projectionMatrix);
	}
	
	final static float ROTATION_SPEED = TAU / 4;
	final static float MOVEMENT_SPEED = 2;

	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(KeyEvent.VK_UP)) {
			angle.x -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_DOWN)) {
			angle.x += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_LEFT)) {
			angle.y -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
			angle.y += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_PAGE_DOWN)) {
			distance += MOVEMENT_SPEED * deltaTime;
		}
		if (input.isKeyDown(KeyEvent.VK_PAGE_UP)) {
			distance -= MOVEMENT_SPEED * deltaTime;
		}

		updateModelMatrix();		
	}
	
	private void updateModelMatrix() {
		modelMatrix.identity();
		modelMatrix.translate(target);
		modelMatrix.rotateY(angle.y);	// heading
		modelMatrix.rotateX(angle.x);	// pitch
		modelMatrix.translate(0,0,distance);
	}
}
