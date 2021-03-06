package com.androidadvance.androidsurvey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CognitiveCa extends AppCompatActivity {

    private   static String DB_NAME = "InsideView";
    private   static String ENTITY_NAME_PROFILES = "profiles";
    private   static FirebaseDatabase firebase;
    private   static DatabaseReference database;
    private static DatabaseReference table;


    public CognitiveCa() {
        firebase = FirebaseDatabase.getInstance();
        database = firebase.getReference(DB_NAME);
        table = database.child(ENTITY_NAME_PROFILES);
    }
   // private FaceServiceClient faceServiceClient = new FaceServiceRestClient("2b72ded620924d3f939549773ee9dd7d");
    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("68fd4ebbbc74483ca022db483d8077d6");
    private final int PICK_IMAGE = 1;
    private static final int CAMERA_REQUEST = 18;
    private ProgressDialog detectionProgressDialog;

    Button button1;
    Bitmap photo;
    Face[] resultant;
    public Face[] result1;
    public static ImageView imageView;
    public Face[] getface(){return result1;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cognitive_ca);
        button1 = (Button) findViewById(R.id.button1);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               Intent gallIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);;
//
//                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
                Intent intent=new Intent(getApplicationContext(),UserProfile.class);
                intent.putExtra("userimage",photo);
                startActivity(intent);
                finish();

            }
        });

        detectionProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//
//            Uri uri = data.getData();
//            try {
//                //final Uri imageUri = data.getData();
////                final InputStream imageStream = getContentResolver().openInputStream(uri);
////                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//
//               Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//                imageView.setImageBitmap(bitmap);
//               // detectAndFrame(bitmap);
//                drawFaceRectanglesOnBitmap(bitmap,resultant);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//             photo = (Bitmap) data.getExtras().get("data");
//
//            Uri uri = data.getData();
//             imageView = (ImageView) findViewById(R.id.imageView1);
//
//imageView.setImageBitmap(photo);


                Bitmap photo = (Bitmap) data.getExtras().get("data");

                Uri uri = data.getData();

                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageBitmap(photo);

                detectAndFrame(photo);
                //drawFaceRectanglesOnBitmap(photo,resultant);


            }
//thread for overlapping image view and another activity
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                }
//            }).start();






        else
        {

            Toast.makeText(getApplicationContext(),"erro",Toast.LENGTH_LONG).show();
        }
    }

    // Detect faces by uploading face images
// Frame faces after detection

    private void detectAndFrame(final Bitmap imageBitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            result1 = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    true,        // returnFaceLandmarks
                                    null        // returnFaceAttributes: a string like "age, gender"
                            );


                            if (result1 == null)
                            {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result1.length));
                            //faceServiceClient.getPersonFace();

                       //Toast.makeText(MainActivity.this, ">>>> "+result1[0].faceLandmarks.eyebrowLeftInner.x, Toast.LENGTH_LONG).show();
                            return result1;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }
                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }
                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }
                    @Override
                    public void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();

//                        if (result == null) return;
//                        ImageView imageView = (ImageView)findViewById(R.id.imageView1);
//                        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
//                        imageBitmap.recycle();

 //Write a message to the database
                        DatabaseReference record = table.push();
                        record.setValue(result[0]);
                        resultant = result;

                    }
                };
        detectTask.execute(inputStream);
    }
    public Face[] getResult1(){return result1;}
    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }



}