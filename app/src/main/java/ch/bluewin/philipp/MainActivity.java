package ch.bluewin.philipp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Context context = this;
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get imageview
        imageView = findViewById(R.id.imageView);

        // get solution button
        Button solutionBtn = findViewById(R.id.solutionButton);
        solutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder inputAlert = new AlertDialog.Builder(context);
                inputAlert.setTitle("Submit Solution");
                inputAlert.setMessage("Solution");
                final EditText userInput = new EditText(context);
                inputAlert.setView(userInput);
                inputAlert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        log(userInput.getText().toString());
                    }
                });
                inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = inputAlert.create();
                alertDialog.show();
            }
        });

        // get picture button
        Button decypherBtn = findViewById(R.id.pictureButton);
        decypherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(view);
            }
        });

    }

    private void takePicture(View view) {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = applyFilter(imageBitmap);

            imageView.setImageBitmap(imageBitmap);
        }

    }

    private Bitmap applyFilter(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];

        bitmap.getPixels(data, 0, width, 0, 0, width, height);

        int[] rgbteil = new int[3];

        int rot = Color.RED;

        //get alpha
        int rota = (rot >> 24) & 0xff;

        //get red
        int rotr = (rot >> 16) & 0xff;

        //get green
        int rotg = (rot >> 8) & 0xff;

        //get blue
        int rotb = rot & 0xff;


        for (int i = 0; i < data.length; i++) {
            //get alpha
            int a = (data[i] >> 24) & 0xff;

            //get red
            int r = (data[i] >> 16) & 0xff;

            //get green
            int g = (data[i] >> 8) & 0xff;

            //get blue
            int b = data[i] & 0xff;

            a *= rota;
            r *= rotr;
            g *= rotg;
            b *= rotb;

            data[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Logging
    private void log(String logmessage) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject result = new JSONObject();
        try {
            result.put("task", "Dechiffrierer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            result.put("solution", logmessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("ch.appquest.logmessage", result.toString());

        startActivity(intent);
    }

}
