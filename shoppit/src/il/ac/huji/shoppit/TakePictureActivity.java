package il.ac.huji.shoppit;

import java.io.*;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;

public class TakePictureActivity extends ActionBarActivity {

	Camera camera = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		if (!startCameraDisplay() || !GeneralInfo.startGettingLocation(getBaseContext(), 0))
			return;


		//Create on click listener for the button that snaps the photo
		((ImageButton)findViewById(R.id.take_picture_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				/*stopCamera();
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(camera, TAKE_PICTURE);*/
			}
		});


		//Create on click listener for the barcode button
		((Button)findViewById(R.id.barcode_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO
			}
		});

		//Create on click listener for the skip button
		((Button)findViewById(R.id.skip_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO
			}
		});

	}


	private boolean startCameraDisplay() {
		
		if (camera != null)
			return true;

		//Access the backfacing camera.
		camera = Camera.open();
		if (camera == null) { //No backfacing camera
			Toast.makeText(getApplicationContext(), "Cannot access camera", Toast.LENGTH_LONG).show();
			finish();
			return false;
		}

		//Note sure if need this, need to test on a real device.
		/*Camera.Parameters parameters = camera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				camera.setParameters(parameters);*/

		//Set the large surface view to display what the camera sees.
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Cannot create stream from camera",
					Toast.LENGTH_LONG).show();
			finish();
			return false;
		}

		//Start displaying.
		camera.startPreview();
		return true;
	}


	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// TODO Do something when the shutter closes.
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Do something with the image RAW data.
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			
			//Store the picture taken and start the item adding activity.
			GeneralInfo.itemImageData = data;
//			GeneralInfo.itemImage = BitmapFactory.decodeByteArray(data, 0, data.length);
			Intent intent = new Intent(getBaseContext(), AddItemActivity.class);
			startActivity(intent);
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
		stopCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startCameraDisplay();
	}


	private void stopCamera() {
		try {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
		} catch (NullPointerException e) {}
		camera = null;
		GeneralInfo.stopGettingLocation();
	}



}
