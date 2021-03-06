package comp3170.demos.week7.sceneobjects;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import comp3170.GLBuffers;
import comp3170.demos.week7.cameras.Camera;
import comp3170.demos.week7.shaders.ShaderLibrary;

public class Cube extends SceneObject {
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;

	private Matrix4f cameraModelMatrix = new Matrix4f();
	private Vector4f cameraPosition = new Vector4f();
	
	final private static String VERTEX_SHADER = "fogVertex.glsl";
	final private static String FRAGMENT_SHADER = "fogFragment.glsl";

	public Cube() {
		super(ShaderLibrary.compileShader(VERTEX_SHADER, FRAGMENT_SHADER));

		//          6-----7
		//         /|    /|
		//        / |   / |
		//       1-----0  |     y    RHS coords
		//       |  |  |  |     | 
		//       |  5--|--4     +--x
		//       | /   | /     /
		//       |/    |/     z
		//       2-----3
		
		vertices = new Vector4f[] {
			new Vector4f( 1, 1, 1, 1),
			new Vector4f(-1, 1, 1, 1),
			new Vector4f(-1,-1, 1, 1),
			new Vector4f( 1,-1, 1, 1),
			new Vector4f( 1,-1,-1, 1),
			new Vector4f(-1,-1,-1, 1),
			new Vector4f(-1, 1,-1, 1),
			new Vector4f( 1, 1,-1, 1),
		};
		
		this.vertexBuffer = GLBuffers.createBuffer(vertices);

		// indices for the triangle forming each face

		this.indices = new int[] {
			// front
			0, 1, 2,
			2, 3, 0,
			
			// back
			4, 5, 6,
			6, 7, 4,
			
			// top
			0, 7, 6,
			6, 1, 0,
			
			// bottom 
			2, 5, 4,
			4, 3, 2,
			
			// left
			1, 2, 5,
			5, 6, 1,
			
			// right
			0, 3, 4,
			4, 7, 0,
			
		};

		this.indexBuffer = GLBuffers.createIndexBuffer(indices);
		
		// scale down to fit in window
		this.setScale((float) (1.0f / Math.sqrt(3)));

		this.colour = new Vector3f(1f, 1f, 1f); // default is white
	}
	
	@Override
	public void draw(Camera camera) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		shader.enable();

		calcModelMatrix();
		shader.setUniform("u_modelMatrix", modelMatrix);
		shader.setUniform("u_viewMatrix", camera.getViewMatrix(viewMatrix));
		shader.setUniform("u_projectionMatrix", camera.getProjectionMatrix(projectionMatrix));		

		camera.getModelMatrix(cameraModelMatrix);
		cameraModelMatrix.getColumn(3, cameraPosition);
		shader.setUniform("u_cameraPosition", cameraPosition);

		shader.setUniform("u_fogColour", new float[] { 1, 1, 1 });

		// connect the vertex buffer to the a_position attribute
		shader.setAttribute("a_position", vertexBuffer);

		// write the colour value into the u_colour uniform
		shader.setUniform("u_colour", colour);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
	}

}
