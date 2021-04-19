package comp3170.demos.week7.fog;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import comp3170.GLException;
import comp3170.InputManager;
import comp3170.Shader;
import comp3170.demos.week7.fog.cameras.PerspectiveCamera;
import comp3170.demos.week7.fog.sceneobjects.Axes;
import comp3170.demos.week7.fog.sceneobjects.Cube;
import comp3170.demos.week7.fog.sceneobjects.Grid;

public class FogDemo extends JFrame implements GLEventListener {

	public static final float TAU = (float) (2 * Math.PI);		// https://tauday.com/tau-manifesto
	
	private int width = 800;
	private int height = 800;

	private GLCanvas canvas;
	private Shader shader;
	
	final private File DIRECTORY = new File("src/comp3170/demos/week7/fog"); 
	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Animator animator;
	private long oldTime;
	private InputManager input;

	private Grid grid;

	private int currentCamera;
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f cameraModelMatrix = new Matrix4f();
	private Vector4f cameraPosition = new Vector4f();
	
	private Cube[] cubes;

	private Axes axes;

	private PerspectiveCamera camera;

	public FogDemo() {
		super("Week 6 3D camera demo");

		// set up a GL canvas
		GLProfile profile = GLProfile.get(GLProfile.GL4);		 
		GLCapabilities capabilities = new GLCapabilities(profile);
		this.canvas = new GLCanvas(capabilities);
		this.canvas.addGLEventListener(this);
		this.add(canvas);
		
		// set up Animator		

		this.animator = new Animator(canvas);
		this.animator.start();
		this.oldTime = System.currentTimeMillis();		

		// input
		
		this.input = new InputManager(canvas);
		
		// set up the JFrame
		
		this.setSize(width,height);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		// set the background colour to black
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Compile the shader
		try {
			File vertexShader = new File(DIRECTORY, VERTEX_SHADER);
			File fragementShader = new File(DIRECTORY, FRAGMENT_SHADER);
			this.shader = new Shader(vertexShader, fragementShader);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (GLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Set up the scene
		this.grid = new Grid(shader,11);
		grid.setAngle(0, 0, 0);
		grid.setPosition(0,0,0);
		
		this.axes = new Axes(shader);
		
		this.cubes = new Cube[] {
			new Cube(shader),
			new Cube(shader),
			new Cube(shader),
		};
		
		cubes[0].setPosition(0.7f, 0.05f, -0.3f);
		cubes[0].setScale(0.05f);
		cubes[0].setColour(Color.RED);

		cubes[1].setPosition(-0.5f, 0.05f, 0.3f);
		cubes[1].setScale(0.05f);
		cubes[1].setColour(new Color(0.4f, 0.4f, 1.0f));

		cubes[2].setPosition(0.1f, 0.05f, 0.1f);
		cubes[2].setScale(0.05f);
		cubes[2].setColour(Color.GREEN);

		this.camera = new PerspectiveCamera(2, input, TAU/6, 1, 0.1f, 10f);			
	}

	
	private void update() {
		long time = System.currentTimeMillis();
		float deltaTime = (time-oldTime) / 1000f;
		oldTime = time;
		
		camera.update(deltaTime);		
		input.clear();
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		update();
		
        // clear the colour buffer
		gl.glClear(GL_COLOR_BUFFER_BIT);		
		
		camera.getViewMatrix(viewMatrix);
		camera.getProjectionMatrix(projectionMatrix);
		
		shader.setUniform("u_viewMatrix", viewMatrix);
		shader.setUniform("u_projectionMatrix", projectionMatrix);
		
		shader.setUniform("u_fogColour", new float[] { 1, 1, 1 });

		// get the camera position from its model matrix
		camera.getModellMatrix(cameraModelMatrix);
		cameraModelMatrix.getColumn(3, cameraPosition);
		shader.setUniform("u_cameraPosition", cameraPosition);
			
		// draw the scene
		this.grid.draw();
		this.axes.draw();
		for (int i = 0; i < cubes.length; i++) {
			cubes[i].draw();
		}
		
	}

	@Override
	public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) { 
		new FogDemo();
	}


}
