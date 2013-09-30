package il.ac.huji.shoppit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;
import com.parse.ParseQuery;

/**
 * @author Elie2
 * This fragment is used to take a picture. It is used by both NewItemActivity and NewShopActivity.
 */
public class CameraFragment extends Fragment {

	public static final String TAG = "CameraFragment";

	private static final int IMAGE_WIDTH = 480;

	private Camera camera;
	private SurfaceView surfaceView;
	private ImageButton photoButton;
	private Button barcodeButton;
	private ImageView rectangle;
	private boolean barcodeMode = false,
			currentlyScanning = false;
	private int previewW = 0,
			previewH = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		barcodeMode = false;
		currentlyScanning = false;
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);

		photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);
		barcodeButton = (Button) v.findViewById(R.id.barcode_button);
		rectangle = (ImageView) v.findViewById(R.id.rect);

		rectangle.setVisibility(View.INVISIBLE);

		// hide barcode button when adding shop
		if (getActivity().getClass() == NewShopActivity.class) {
			barcodeButton.setVisibility(View.INVISIBLE);
		}

		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}


		setParams();



		//This part will search for a barcode in the image.
		camera.setPreviewCallback(new Camera.PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {

				if (barcodeMode && !currentlyScanning) {

					currentlyScanning = true;

					// Draw a rectangle
					Activity activity = getActivity();
					int screenW = activity.getWindowManager().getDefaultDisplay().getWidth();
					int screenH = activity.getWindowManager().getDefaultDisplay().getHeight();

					Bitmap bitmap = Bitmap.createBitmap(previewW, previewH, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmap);
					rectangle.setImageBitmap(bitmap);

					Paint paint = new Paint();
					paint.setStrokeWidth(10);

					float ratio = screenW * 1.0f / screenH;
					float rectHeight = screenW * 0.7f;
					float rectWidth = rectHeight * ratio;

					float leftx = (previewW - rectWidth) / 2;
					float topy = (previewH - rectHeight) / 2;
					float rightx = leftx + rectWidth;
					float bottomy = topy + rectHeight;

					//Make the screen dark outside the rectangle
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.FILL);
					paint.setAlpha(170);

					canvas.drawRect(0, 0, previewW, topy, paint);
					canvas.drawRect(0, bottomy, previewW, previewH, paint);
					canvas.drawRect(0, topy, leftx, bottomy, paint);
					canvas.drawRect(rightx, topy, previewW, bottomy, paint);

					//Draw the red rectangle
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.STROKE);
					paint.setAlpha(255);
					canvas.drawRect(leftx, topy, rightx, bottomy, paint);
					rectangle.setVisibility(View.VISIBLE);

					//This part gets the image from the camera in the appropriate format
					Camera.Parameters parameters = camera.getParameters();
					Size size = parameters.getPreviewSize();
					int w = size.width;
					int h = size.height;

					LuminanceSource source = new PlanarYUVLuminanceSource(data, w, h, 0, 0, w, h, false);
					BinaryBitmap binBmp = new BinaryBitmap(new HybridBinarizer(source));

					//This is the actual scanning
					try {
						EAN13Reader reader = new EAN13Reader();
						reader.reset();
						String barcode = reader.decode(binBmp).getText();
						((NewItemActivity) getActivity()).setBarcode(barcode);

						//If came here, scanning was successful 
						//releaseCamera();
						rectangle.setVisibility(View.INVISIBLE);
						searchBarcode(barcode);

					} catch (Exception e) {
						// barcode not recognized in picture
						Log.d("ERROR", e.toString());
					}

					currentlyScanning = false;
				}
			}
		});


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

						//Stop the preview
						releaseCamera();

						//Crop, rotate and scale the image and get the new data
						Bitmap image = rotate(crop(data));
						image = Bitmap.createScaledBitmap(image, IMAGE_WIDTH, IMAGE_WIDTH, false);

						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
						data = bos.toByteArray();

						//Set the list of items with similar barcode to null.
						//This prevents buttons from appearing on the item image when adding.
						if (getActivity() instanceof NewItemActivity)
							((NewItemActivity) getActivity()).setItemList(null);

						addPhotoToShopAndReturn(data);

					}

				});

			}


		});

		barcodeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				barcodeMode = !barcodeMode;
				if (barcodeMode) {
					photoButton.setVisibility(View.INVISIBLE);
					barcodeButton.setText("Take item picture");
				} else {
					photoButton.setVisibility(View.VISIBLE);
					rectangle.setVisibility(View.INVISIBLE);
					barcodeButton.setText("Scan barcode");
				}

			}

		});

		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
		//		create43RatioSurface();

		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						// camera.setDisplayOrientation(90);
						//						camera.setDisplayOrientation(90);
						setCameraDisplayOrientation(getActivity(), 0, camera);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}

			// now that we've fixed the camera orientation to portrait, this won't actually ever be called.
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

				previewW = width;
				previewH = height;
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});

		return v;
	}

	private Bitmap rotate(Bitmap image) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(image, 0,
				0, image.getWidth(), image.getHeight(),
				matrix, true);
	}


	private Bitmap crop(byte[] data) {

		Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

		if (image.getWidth() >= image.getHeight()) {
			return Bitmap.createBitmap(image, 
					Math.abs(image.getWidth() - image.getHeight())/2,
					0,
					image.getHeight(), 
					image.getHeight()
					);
		}
		else {
			return Bitmap.createBitmap(
					image,
					0, 
					image.getHeight()/2 - image.getWidth()/2,
					image.getWidth(),
					image.getWidth() 
					);
		}
	}


	void setParams() {

		//Barcode scanning is allowed only if the camera is of type NV21.
		//Don't really know what that is, but that's the only option I've seen when scanning
		//for a barcode. Seems like it's the most probable option though.
		Parameters parameters = camera.getParameters();
		int imageFormat = parameters.getPreviewFormat();
		if (imageFormat != ImageFormat.NV21)
			barcodeButton.setVisibility(View.INVISIBLE);

		//Set the size of the captured image to be as large as possible.
		int bestWidth = 0,
				bestHeight = 0;
		List<Size> sizes = parameters.getSupportedPictureSizes();
		for (Size s: sizes) {
			if (s.width > bestWidth) {
				bestWidth = s.width;
				bestHeight = s.height;
			}
		}
		parameters.setPictureSize(bestWidth, bestHeight);

		//Define the focus for the picture taking mode, which is the initial mode
		parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		camera.setParameters(parameters);

	}



	void searchBarcode(String barcode) {

		//Get how many products with this barcode exist in our database

		ParseQuery<Item> query = new ParseQuery<Item>("Item");
		query.whereEqualTo("barcode", barcode);
		final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Searching for barcode...", true);

		try {

			List<Item> items = query.find();
			progressDialog.dismiss();
			int numOfItems = items.size();

			//No matching items found, display an error message.
			//An exception and this case show the same error, so just throw an exception.
			if (numOfItems == 0) {
				throw new Exception();
			}

			else {

				//Save the list of retrieved items and start the item uploading screen.
				((NewItemActivity) getActivity()).setItemList(items);
				addPhotoToShopAndReturn(null);

			}

		} catch (Exception e) {

			//Show a message saying no items exist with this barcode
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getActivity());
			dlgAlert.setMessage("No matching items have been found, please take the item picture.");
			dlgAlert.setPositiveButton("OK", null);
			dlgAlert.setCancelable(false);
			dlgAlert.create().show();

			//Make the user take an item picture and continue as usual to uploading the item.
			//Note however that the barcode has been saved and it will be uploaded with the item.

			//Obviously, don't allow the user to scan for barcodes again.
			//Display the barcode text on the barcode button to imply the user that they should take
			//the picture of the current item.

			barcodeMode = false;
			photoButton.setVisibility(View.VISIBLE);
			barcodeButton.setEnabled(false);
			barcodeButton.setText("Barcode: " + barcode);

		}
	}




	/* we don't actually want this scaling/resizing, since it makes the picture too small,
	 * but we might want to do some other scaling, so I've left the code in for now.
	 * 
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	@SuppressWarnings("unused")
	private void saveScaledPhoto(byte[] data) {

		// Resize photo from camera byte array
		Bitmap shopImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap shopImageScaled = Bitmap.createBitmap(shopImage, 0,0,200,200);
		//			Bitmap shopImageScaled = Bitmap.createScaledBitmap(shopImage, 200, 200
		//					* shopImage.getHeight() / shopImage.getWidth(), false);

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotatedScaledShopImage = Bitmap.createBitmap(shopImageScaled, 0,
				0, shopImageScaled.getWidth(), shopImageScaled.getHeight(),
				matrix, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledShopImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		addPhotoToShopAndReturn(scaledData);
	}


	private void addPhotoToShopAndReturn(byte[] data) {

		if (getActivity().getClass() == NewShopActivity.class) {
			Log.i(TAG, "NewShopActivity");

			((NewShopActivity) getActivity()).setCurrentPhotoData(data);

			FragmentManager fm = getActivity().getFragmentManager();
			fm.popBackStack("NewShopFragment",
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else if (getActivity().getClass() == NewItemActivity.class) {
			Log.i(TAG, "NewItemActivity");

			if (data != null)
				((NewItemActivity) getActivity()).setCurrentPhotoData(data);

			Fragment cameraFragment = new NewItemFragment();
			FragmentTransaction transaction = getActivity().getFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.fragmentContainer, cameraFragment);
			transaction.addToBackStack("NewItemFragment");
			transaction.commit();

		} else {
			Log.e(TAG, "error in addPhotoToShopAndReturn");
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		if (camera == null) {
			try {
				camera = Camera.open();
				setParams();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		//barcode will usually be null, unless came here from the new item fragment after
		//scanning a barcode and wanting to take own picture.

		if (getActivity() instanceof NewItemActivity) {
			final NewItemActivity nia = (NewItemActivity) getActivity();
			String barcode = nia.getBarcode();
			if (barcode != null) {
				barcodeButton.setEnabled(false);
				barcodeButton.setText("Barcode: " + barcode);
			}
		}
	}

	@Override
	public void onPause() {
		releaseCamera();
		super.onPause();
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degrees = 0; break;
		case Surface.ROTATION_90: degrees = 90; break;
		case Surface.ROTATION_180: degrees = 180; break;
		case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}


}
