package com.name.filler.suregive;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileInfo extends AppCompatActivity {

    private String personId = "";
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        final byte[] byteArray = ((MyApplication) this.getApplication()).getProfile();
        final ImageView image = (ImageView) findViewById(R.id.profile_img);

        image.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                int width = (int) (image.getHeight() / (double) bmp.getHeight()) * bmp.getWidth();
                image.setImageBitmap(Bitmap.createScaledBitmap(bmp, width,
                        image.getHeight(), false));
            }
        });

        Button donate = (Button) findViewById(R.id.donate_button);
        final EditText donationValue = (EditText) findViewById(R.id.donate_text);
        final ProfileInfo context = this;
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("AAAAAAAAAAAAAAAAAA"+donationValue.getText());
                if (donationValue.getText() != null && donationValue.getText().length()>0) {
                    RequestParams params = new RequestParams();
                    params.put("person_id", personId);
                    //System.out.println("AAAA"+Integer.parseInt(donationValue.getText().toString()));
                    params.put("value", Double.parseDouble(donationValue.getText().toString()));
                    final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.coin);
                    mp.start();
                    //TODO: change this to input
                    ServerRestClient.post("/transaction", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            dialog.hide();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            dialog.hide();
                        }
                    });
                    dialog = new ProgressDialog(context);
                    dialog.setMessage("Donating");
                    dialog.setCancelable(false);
                    dialog.setInverseBackgroundForced(false);
                    dialog.show();

                }
            }
        });

        TextView text = (TextView) findViewById(R.id.profile_name);
        text.setText(bundle.getString("name"));

        text = (TextView) findViewById(R.id.bio);
        text.setText(bundle.getString("bio"));

        personId = bundle.getString("person_id");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(myIntent, 0);
                break;
        }
        return true;
    }
}
