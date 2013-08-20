package il.ac.huji.shoppit;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.*;
import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.widget.*;

public class TakePictureActivity extends Activity {

	Camera camera = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_picture);

		camera = Camera.open();

		if (camera == null) { //No backfacing camera
			Toast.makeText(getApplicationContext(), "Cannot access camera", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		/*Camera.Parameters parameters = camera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		camera.setParameters(parameters);*/

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Cannot create stream from camera",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		camera.startPreview();



		//Create on click listener for the button that snaps the photo
		((Button)findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback); 
			}
		});


		//Create on click listener for the barcode button
		((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO
			}
		});

		//Create on click listener for the skip button
		((Button)findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO
			}
		});

	}



	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// TODO Do something when the shutter closes.
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Do something with the image RAW data.
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Do something with the image JPEG data.
		}
	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onDestroy() {

		super.onDestroy();

		try {
			camera.stopPreview();
		} catch (Exception e) {}
		try {
			camera.release();
		} catch (Exception e) {}

	}




}
