package com.name.filler.suregive;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileInfo extends AppCompatActivity {

    private String personId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
//        byte[] byteArray = bundle.getByteArray("image");
//
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        ImageView image = (ImageView) findViewById(R.id.profile_img);

//        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
//                image.getHeight(), false));

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
