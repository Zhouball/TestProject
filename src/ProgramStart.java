import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.jogamp.common.nio.Buffers;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


public class ProgramStart implements GLEventListener {
	
	// private static GraphicsEnvironment graphicsEnvironment;
	private static boolean isFullScreen = false;
	public static DisplayMode dm, dm_old;
	private static Dimension xgraphic;
	private static Point point = new Point(0,0);
	
	private GLU glu = new GLU();
	
	private GL2 gl;
	
	private float[] vertices = { -0.5f,  0.5f,  0.5f,  1.0f,
								 -0.5f, -0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f,  0.5f,  1.0f,
								 -0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f,  0.5f,  1.0f,
								  0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f, -0.5f,  1.0f,
								  0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f, -0.5f,  1.0f,
								  0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f,  0.5f,  1.0f,
								 -0.5f, -0.5f, -0.5f,  1.0f,
								  0.5f, -0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f,  0.5f,  1.0f,
								  0.5f, -0.5f, -0.5f,  1.0f,
								  0.5f, -0.5f,  0.5f,  1.0f,
								 -0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f,  0.5f,  0.5f,  1.0f,
								 -0.5f,  0.5f, -0.5f,  1.0f,
								  0.5f,  0.5f,  0.5f,  1.0f,
								  0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f, -0.5f,  1.0f,
								 -0.5f,  0.5f, -0.5f,  1.0f,
								  0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f, -0.5f,  1.0f,
								  0.5f,  0.5f, -0.5f,  1.0f,
								  0.5f, -0.5f, -0.5f,  1.0f,
								 -0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f,  0.5f,  1.0f,
								 -0.5f,  0.5f, -0.5f,  1.0f,
								 -0.5f, -0.5f,  0.5f,  1.0f,
								 -0.5f,  0.5f,  0.5f,  1.0f };
	
	private float width;
	private float height;
	
	private int camMatLoc;
	private int projMatLoc;
	private int worldMatLoc;
	private int colorLoc;
	private int posLoc;
	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2(); 
		
		String[] vertexSource = new String[1];
		try {
			vertexSource[0] = readFile("src/shaders/vertexShaderTest.glsl", Charset.forName("US-ASCII"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] fragmentSource = new String[1];
		try {
			fragmentSource[0] = readFile("src/shaders/fragmentShaderTest.glsl", Charset.forName("US-ASCII"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int vertexShader = gl.glCreateShader (GL2.GL_VERTEX_SHADER);
		gl.glShaderSource (vertexShader, 1, vertexSource, null);
		gl.glCompileShader (vertexShader);
		
		int fragmentShader = gl.glCreateShader (GL2.GL_FRAGMENT_SHADER);
		gl.glShaderSource (fragmentShader, 1, fragmentSource, null);
		gl.glCompileShader (fragmentShader);
		
		// Create and link program
		int program = gl.glCreateProgram ();
		gl.glAttachShader (program, vertexShader);
		gl.glAttachShader (program, fragmentShader);
		gl.glLinkProgram (program);

		projMatLoc = gl.glGetUniformLocation(program, "mProj");
		camMatLoc = gl.glGetUniformLocation(program, "mView");
		worldMatLoc = gl.glGetUniformLocation(program, "mWorld");
		colorLoc = gl.glGetUniformLocation(program, "vertColor");
		posLoc = gl.glGetAttribLocation(program, "vertPosition");
		
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		//mainRenderer.gl = gl;

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		Matrix4 worldMat = new Matrix4();
		worldMat.loadIdentity();
		Matrix4 camMat = new Matrix4();
		camMat.loadIdentity();
		Matrix4 projMat = new Matrix4();
		projMat.makePerspective(45.0f, (float) width / (float) height, 0.1f, 1000f);
		
		gl.glUniform3fv(colorLoc, 3 * Buffers.SIZEOF_FLOAT, FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f}));
		gl.glUniformMatrix4fv(projMatLoc, 16 * Buffers.SIZEOF_FLOAT, true, FloatBuffer.wrap(projMat.getMatrix()));
		gl.glUniformMatrix4fv(camMatLoc, 16 * Buffers.SIZEOF_FLOAT, true, FloatBuffer.wrap(camMat.getMatrix()));
		gl.glUniformMatrix4fv(worldMatLoc, 16 * Buffers.SIZEOF_FLOAT, true, FloatBuffer.wrap(worldMat.getMatrix()));
		
		FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(vertices);
		gl.glEnableVertexAttribArray (posLoc);
		int bufs[] = new int[1];
		gl.glGenBuffers(1, bufs, 0);
		gl.glBindBuffer (GL2.GL_ARRAY_BUFFER, bufs[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length * Buffers.SIZEOF_FLOAT, vertexBuffer, GL2.GL_STATIC_DRAW); 
		gl.glVertexAttribPointer (posLoc,
				4,
				GL2.GL_FLOAT,
				false,
				4 * Buffers.SIZEOF_FLOAT,
				0);
		
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, vertices.length / 4);
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		gl = drawable.getGL().getGL2();
		if (height <= 0) 
			height = 1;
		gl.glViewport(0, 0, width, height);
		this.height = height;
		this.width = width;
		
	}
	
	public static void main(String[] args) {
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		
		// canvas
		final GLCanvas glcanvas = new GLCanvas(capabilities);
		ProgramStart r = new ProgramStart();
		glcanvas.addGLEventListener(r);
		glcanvas.setSize(1600, 900);
		
		
		final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
		
		final JFrame frame = new JFrame("Test Project");
		
		frame.getContentPane().add(glcanvas);
		
		// Shutdown
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (animator.isStarted())
					animator.stop();
				System.exit(0);;
			}
		});
		
		
		frame.setSize(frame.getContentPane().getPreferredSize());
		
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(0,0));
		frame.add(p, BorderLayout.SOUTH);
		
		animator.start();
	}
	
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}