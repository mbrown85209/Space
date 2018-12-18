package *********;

/*
 * Display a bunch of cubes.
 * 
 * The purpose of this class is to exercise the rotation transformation,
 * both on the view and on a model.
 * 
 * To implement this code in an existing LibGDX project, first set the package
 * name (above), then in the desktop launcher class, in the main method,
 * on the LwjglApplication line, replace the ApplicationListener parameter
 * class to "Space"; like this:
 *    	new LwjglApplication( new Space(), config );
 * 
 * To rotate the view, drag with mouse right button.
 * To rotate a cube, drag with the left button.
 *
 * I want the object to rotate in the direction it is dragged by the mouse,
 * no matter which direction it happens to be orientated at the time. As it
 * is now, when I first drag the mouse to the right, the object rotates to
 * the right about the screen Y axis as expected; but then when I drag the
 * mouse upward I want the object to rotate upward about the screen X axis,
 * but instead it spins to the left about the screen Z axis.
 *
 * It seems to me that the mouse movement is transforming the objects
 * directly in their local coordinate system; but instead I think it
 * needs to transform the axis of rotation itself into the Object
 * Coordinate System before applying it to the object.
 * 
 * I suppose the solution will be in the touchDragged() method,
 * around lines 140 to 150.
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Space extends Game implements Screen, InputProcessor
{
	PerspectiveCamera			camera;
	float						backgroundColor[];
	Environment					environment;
	Cube						cubes[];
	int							selectedCube;
	ModelBatch					modelBatch;
	int							touchedButton;
	int							lastX, lastY;
	Vector3						dxdyDir;
	float						dxdyLen;

	@Override public void create()
	{
		Vector3 cubePositions[] = new Vector3[ 27 ];
		for ( int i = 0; i < 27; i++ )
			cubePositions[ i ] = new Vector3( i/9, ( i%9 )/3, i%3 )
					.scl( 20f ).add( -20f, -20f, -20f );
			
		cubes = new Cube[ 27 ];
		selectedCube = 14;
		for ( int i = 0; i < 27; i++ )
			cubes[ i ] = new Cube( cubePositions[ i ], ( i == selectedCube ) );
		
		modelBatch = new ModelBatch();
		dxdyDir = new Vector3();
		Gdx.input.setInputProcessor( this );
		setScreen( this );
	}

	@Override public void show()
	{
		camera = new PerspectiveCamera( 67f, 3f, 2f );
		camera.position.set( 10f, -10f, 70f );
		camera.lookAt( Vector3.Zero ); 
		camera.up.set( Vector3.Y );
		camera.near = 1f;
		camera.far = 500f;
		camera.update();
		
		backgroundColor	= new float[] {	.9f, .9f, .7f };
		environment = new Environment();
		environment.set( new ColorAttribute( 
				ColorAttribute.AmbientLight, .6f, .6f, .6f, 1f ) );
		environment.add( new DirectionalLight().set(
				.8f, .8f, .8f,		// color RGB
				50f, 50f, 50f ) );	// position XYZ
		environment.add( new DirectionalLight().set(
				.5f, .5f, .5f,
				-50f, -50f, 50f ) );

		Gdx.graphics.requestRendering();
	}

	@Override public void render( float delta )
	{
		Gdx.gl.glClearColor( backgroundColor[0], backgroundColor[1],
				backgroundColor[2], 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		Gdx.gl.glEnable( GL20.GL_DEPTH_TEST );	// //////////
		Gdx.gl.glEnable( GL20.GL_CULL_FACE );	// //////////
		
		modelBatch.begin( camera );

		for ( int i = 0; i < cubes.length; i++ )
			modelBatch.render( cubes[ i ].modelInstance, environment );

		modelBatch.end();
	}

	@Override public boolean touchDown( int screenX, int screenY, int pointer, int button )
	{
		touchedButton = button;
		lastX = screenX;
		lastY = screenY;
		return true;
	}

	@Override public boolean touchDragged( int screenX, int screenY, int pointer )
	{
		lastX = screenX-lastX;
		lastY = screenY-lastY;

		// distance of mouse movement
		dxdyLen = (float) Math.sqrt( lastX*lastX + lastY*lastY );
		// direction vector of the AOR
		dxdyDir.set( lastY/dxdyLen, lastX/dxdyLen, 0f );
		if ( touchedButton == 0 )
		{
			cubes[ selectedCube ].modelInstance.transform.rotate( dxdyDir, dxdyLen );
		}
		else
		{
			// direction vector of the AOR
			dxdyDir.scl( -1f );
			camera.rotateAround( Vector3.Zero, dxdyDir, dxdyLen/5.5f );
			camera.update();
		}

		lastX = screenX;
		lastY = screenY;
		Gdx.graphics.requestRendering();
		return true;
	}

	// unused
	@Override public void hide() {}
	@Override public void resize( int w, int h ) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
	@Override public boolean keyDown( int k ) { return false; }
	@Override public boolean keyUp( int k ) { return false; }
	@Override public boolean keyTyped( char c ) { return false;	}
	@Override public boolean touchUp( int X, int Y, int p, int b )  { return false; }
	@Override public boolean mouseMoved( int X, int Y )  { return false; }
	@Override public boolean scrolled( int a )  { return false; }
}

class Cube
{
	Vector3			position;
	Model			model;
	ModelInstance	modelInstance;
	
	Cube( Vector3 position, boolean selected  )
	{
		this.position = position;
		compose( selected );
	}
	
	void compose( boolean highlighted )
	{
		float w = 10f, d = 10f, h = 10f;
		float wd2 = w/2f, dd2 = d/2f, hd2 = h/2f;
		float surfaceVertices[][] =
		{ { //	  positions					normals		  colors
			 wd2,	 dd2,	 hd2,		 0f,  0f,  1f,		0f,		// top 
			-wd2,	 dd2,	 hd2,		 0f,  0f,  1f,		0f,
			-wd2,	-dd2,	 hd2,		 0f,  0f,  1f,		0f,
			 wd2,	-dd2,	 hd2,		 0f,  0f,  1f,		0f,
		}, {
			-wd2,	-dd2,	-hd2,		 0f,  0f, -1f,		0f,		// bottom
			-wd2,	 dd2,	-hd2,		 0f,  0f, -1f,		0f,
			 wd2,	 dd2,	-hd2,		 0f,  0f, -1f,		0f,
			 wd2,	-dd2,	-hd2,		 0f,  0f, -1f,		0f,
		}, {
			 wd2,	 dd2,	 hd2,		 1f,  0f,  0f,		0f,		// right
			 wd2,	-dd2,	 hd2,		 1f,  0f,  0f,		0f,
			 wd2,	-dd2,	-hd2,		 1f,  0f,  0f,		0f,
			 wd2,	 dd2,	-hd2,		 1f,  0f,  0f,		0f,
		}, {
			-wd2,	-dd2,	-hd2,		-1f,  0f,  0f,		0f,		// left
			-wd2,	-dd2,	 hd2,		-1f,  0f,  0f,		0f,
			-wd2,	 dd2,	 hd2,		-1f,  0f,  0f,		0f,
			-wd2,	 dd2,	-hd2,		-1f,  0f,  0f,		0f,
		}, {
			-wd2,	-dd2,	-hd2,		 0f,  1f,  0f,		0f,		// front
			 wd2,	-dd2,	-hd2,		 0f,  1f,  0f,		0f,
			 wd2,	-dd2,	 hd2,		 0f,  1f,  0f,		0f,
			-wd2,	-dd2,	 hd2,		 0f,  1f,  0f,		0f,
		}, {
			 wd2,	 dd2,	 hd2,		 0f, -1f,  0f,		0f,		// back
			 wd2,	 dd2,	-hd2,		 0f, -1f,  0f,		0f,
			-wd2,	 dd2,	-hd2,		 0f, -1f,  0f,		0f,
			-wd2,	 dd2,	 hd2,		 0f, -1f,  0f,		0f,
		} };
		short surfaceIndicies[] =
		{
			0, 1, 2, 3, 
		};
		int vertexIds[] =
		{	0, 1, 2, 3,
			2, 3, 0, 1, 
			0, 3, 1, 2,
			0, 2, 1, 3,
			0, 1, 3, 2,
			0, 2, 3, 1
		};
		float colors[] = new float[ 4 ];
		Color cc;
		for ( int i = 0; i < 4; i++ )
		{
			cc = new Color().fromHsv( 90f*i, ( (highlighted ) ? 1f : .4f ), 1f );
			colors[ i ] = Color.toFloatBits( cc.r, cc.g, cc.b, 1f );
		}
		
		for ( int i = 0; i < 6; i++ )
			for ( int j = 0; j < 4; j++ )
				surfaceVertices[ i ][ j*7+6 ] = colors[ vertexIds[ i*4+j ] ];
			
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		Mesh mesh;
		
		for ( int i = 0; i < 6; i++ )
		{
			mesh = new Mesh( true, 4, 4,
					VertexAttribute.Position(),
					VertexAttribute.Normal(),
					VertexAttribute.ColorPacked() );
			mesh.setVertices( surfaceVertices[ i ] );
			mesh.setIndices( surfaceIndicies );

			modelBuilder.part( "", mesh, GL20.GL_TRIANGLE_FAN, new Material() );
		}
		model = modelBuilder.end();
		modelInstance = new ModelInstance( model );
		modelInstance.transform.setTranslation( position );
	}
}
