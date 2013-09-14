package il.ac.huji.shoppit;

import java.io.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.widget.*;

public class TakePictureActivity extends ActionBarActivity {

	//	Camera camera = null;

	public static final String TAG = "TAKE_PIC_ACT";

	private Camera camera;
	private SurfaceView surfaceView;
	private ImageButton photoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		photoButton = (ImageButton) findViewById(R.id.camera_photo_button);

		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(this, "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera == null)
					return;

				camera.takePicture(new Camera.ShutterCallback() {

					@Override
					public void onShutter() {
						// nothing to do
					}

				}, null, new Camera.PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						saveScaledPhoto(data);
						//						addPhotoToShopAndReturn(data);
					}

				});

			}
		});

		surfaceView = (SurfaceView) findViewById(R.id.camera_surface_view);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});
	}


	private void saveScaledPhoto(byte[] data) {

		// Resize photo from camera byte array
		Bitmap shopImage = BitmapFactory.decodeByteArray(data, 0, data.length);

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotatedShopImage = Bitmap.createBitmap(shopImage, 0,
				0, shopImage.getWidth(), shopImage.getHeight(),
				matrix, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedShopImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		addPhotoToShopAndReturn(scaledData);
	}

	private void addPhotoToShopAndReturn(byte[] data) {
		GeneralInfo.itemImageData = data;

		Intent intent = new Intent(getBaseContext(), AddItemActivity.class);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(this, "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	//	@Override
	//	protected void onCreate(Bundle savedInstanceState) {
	//
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.activity_take_picture);
	//
	//		if (!startCameraDisplay() || !GeneralInfo.startGettingLocation(getBaseContext(), 0))
	//			return;
	//
	//
	//		//Create on click listener for the button that snaps the photo
	//		((ImageButton)findViewById(R.id.take_picture_button)).setOnClickListener(new View.OnClickListener() {
	//			public void onClick(View v) {
	//				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	//				/*stopCamera();
	//				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	//				startActivityForResult(camera, TAKE_PICTURE);*/
	//			}
	//		});
	//
	//
	//		//Create on click listener for the barcode button
	//		((Button)findViewById(R.id.barcode_button)).setOnClickListener(new View.OnClickListener() {
	//			public void onClick(View v) {
	//				//TODO
	//			}
	//		});
	//
	//		//Create on click listener for the skip button
	//		((Button)findViewById(R.id.skip_button)).setOnClickListener(new View.OnClickListener() {
	//			public void onClick(View v) {
	//				//TODO
	//			}
	//		});
	//
	//	}
	//
	//
	//	private boolean startCameraDisplay() {
	//		
	//		if (camera != null)
	//			return true;
	//
	//		//Access the backfacing camera.
	//		camera = Camera.open();
	//		if (camera == null) { //No backfacing camera
	//			Toast.makeText(getApplicationContext(), "Cannot access camera", Toast.LENGTH_LONG).show();
	//			finish();
	//			return false;
	//		}
	//
	//		//Note sure if need this, need to test on a real device.
	//		/*Camera.Parameters parameters = camera.getParameters();
	//				parameters.setPictureFormat(PixelFormat.JPEG);
	//				camera.setParameters(parameters);*/
	//
	//		//Set the large surface view to display what the camera sees.
	//		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
	//		SurfaceHolder surfaceHolder = surfaceView.getHolder();
	//		surfaceHolder.addCallback(new Callback() {
	//
	//			public void surfaceCreated(SurfaceHolder holder) {
	//				try {
	//					if (camera != null) {
	//						camera.setDisplayOrientation(90);
	//						camera.setPreviewDisplay(holder);
	//						camera.startPreview();
	//					}
	//				} catch (IOException e) {
	//					Toast.makeText(getApplicationContext(), "Cannot create stream from camera",
	//							Toast.LENGTH_LONG).show();
	//					finish();
	//				}
	//			}
	//
	//			public void surfaceChanged(SurfaceHolder holder, int format,
	//					int width, int height) {
	//				// nothing to do here
	//			}
	//
	//			public void surfaceDestroyed(SurfaceHolder holder) {
	//				// nothing here
	//			}
	//
	//		});
	//		
	//		
	//		/*try {
	//			camera.setPreviewDisplay(surfaceHolder);
	//		} catch (IOException e) {
	//			Toast.makeText(getApplicationContext(), "Cannot create stream from camera",
	//					Toast.LENGTH_LONG).show();
	//			finish();
	//			return false;
	//		}
	//
	//		//Start displaying.
	//		camera.startPreview();*/
	//		return true;
	//	}
	//
	//
	//	ShutterCallback shutterCallback = new ShutterCallback() {
	//		public void onShutter() {
	//			// TODO Do something when the shutter closes.
	//		}
	//	};
	//
	//	PictureCallback rawCallback = new PictureCallback() {
	//		public void onPictureTaken(byte[] data, Camera camera) {
	//			// TODO Do something with the image RAW data.
	//		}
	//	};
	//
	//	PictureCallback jpegCallback = new PictureCallback() {
	//		public void onPictureTaken(byte[] data, Camera camera) {
	//			
	//			//Store the picture taken and start the item adding activity.
	//			GeneralInfo.itemImageData = data;
	////			GeneralInfo.itemImage = BitmapFactory.decodeByteArray(data, 0, data.length);
	//			Intent intent = new Intent(getBaseContext(), AddItemActivity.class);
	//			startActivity(intent);
	//		}
	//	};
	//
	//
	//
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		getMenuInflater().inflate(R.menu.main, menu);
	//		return true;
	//	}
	//
	//
	//	@Override
	//	protected void onDestroy() {
	//		super.onDestroy();
	//		stopCamera();
	//	}
	//
	//	@Override
	//	protected void onPause() {
	//		super.onPause();
	//		stopCamera();
	//	}
	//
	//	@Override
	//	protected void onResume() {
	//		super.onResume();
	//		startCameraDisplay();
	//	}
	//
	//
	//	private void stopCamera() {
	//		try {
	//			camera.stopPreview();
	//			camera.setPreviewCallback(null);
	//			camera.release();
	//		} catch (NullPointerException e) {}
	//		camera = null;
	//		GeneralInfo.stopGettingLocation();
	//	}



}
