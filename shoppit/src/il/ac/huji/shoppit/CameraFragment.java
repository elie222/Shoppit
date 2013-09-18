package il.ac.huji.shoppit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.UPCAReader;

/**
 * @author Elie2
 * This fragment is used to take a picture. It is used by both NewItemActivity and NewShopActivity.
 */
public class CameraFragment extends Fragment {

	public static final String TAG = "CameraFragment";

	private Camera camera;
	private SurfaceView surfaceView;
	private ImageButton photoButton;
	private Button barcodeButton;
	private boolean barcodeMode = false,
			currentlyScanning = false;

	private int previewWidth = 0,
			previewHeight = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		barcodeMode = false;
		currentlyScanning = false;
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);

		photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);
		barcodeButton = (Button) v.findViewById(R.id.barcode_button);

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


		//Barcode scanning is allowed only if the camera is of type NV21.
		//Don't really know what that is, but that's the only option I've seen when scanning
		//for a barcode. Seems like it's the most probable option though.
		Parameters parameters = camera.getParameters();
		int imageFormat = parameters.getPreviewFormat();
		if (imageFormat != ImageFormat.NV21)
			barcodeButton.setVisibility(View.INVISIBLE);


		//This part will search for a barcode in the image.
		camera.setPreviewCallback(new Camera.PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if (barcodeMode && !currentlyScanning) {
					
					currentlyScanning = true;

					//This part gets the image from the camera in the appropriate format
					YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewWidth, previewHeight, null);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					yuvimage.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 100, baos);
					byte[] jdata = baos.toByteArray();
					BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
					bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
					Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
					LuminanceSource source = new PlanarYUVLuminanceSource(data, bmp.getWidth(), bmp.getHeight(), 0, 0, bmp.getWidth(), bmp.getHeight(), false);
					BinaryBitmap binBmp = new BinaryBitmap(new HybridBinarizer(source));

					//This is the actual scanning
					try {
						String code = new UPCAReader().decode(binBmp).getText();
						Toast.makeText(getActivity(), code,
								Toast.LENGTH_LONG).show();
						camera.setPreviewCallback(null);

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
						saveScaledPhoto(data);
						// addPhotoToShopAndReturn(data);
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
				}
				else {
					photoButton.setVisibility(View.VISIBLE);
					barcodeButton.setText("Scan barcode");
				}

			}

		});

		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
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
				previewWidth = width;
				previewHeight = height;
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});

		return v;
	}



	/* we don't actually want this scaling/resizing, since it makes the picture too small,
	 * but we might want to do some other scaling, so I've left the code in for now.
	 * 
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	//	@SuppressWarnings("unused")
	//	private void saveScaledPhoto(byte[] data) {
	//		
	//		// Resize photo from camera byte array
	//		Bitmap shopImage = BitmapFactory.decodeByteArray(data, 0, data.length);
	//		Bitmap shopImageScaled = Bitmap.createScaledBitmap(shopImage, 200, 200
	//				* shopImage.getHeight() / shopImage.getWidth(), false);
	//
	//		Matrix matrix = new Matrix();
	//		matrix.postRotate(90);
	//		Bitmap rotatedScaledShopImage = Bitmap.createBitmap(shopImageScaled, 0,
	//				0, shopImageScaled.getWidth(), shopImageScaled.getHeight(),
	//				matrix, true);
	//
	//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//		rotatedScaledShopImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	//
	//		byte[] scaledData = bos.toByteArray();
	//		
	//		addPhotoToShopAndReturn(scaledData);
	//	}

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

		if (getActivity().getClass() == NewShopActivity.class) {
			Log.i(TAG, "NewShopActivity");
			((NewShopActivity) getActivity()).setCurrentPhotoData(data);

			FragmentManager fm = getActivity().getFragmentManager();
			fm.popBackStack("NewShopFragment",
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else if (getActivity().getClass() == NewItemActivity.class) {
			Log.i(TAG, "NewItemActivity");
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
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
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

}
