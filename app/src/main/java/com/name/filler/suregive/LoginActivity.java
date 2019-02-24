package com.name.filler.suregive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "humzaqavi@gmail.com:suregive"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private Intent mainActivity;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView createAccount;
    private Button mEmailSignInButton;
    //New elements after clicking create account
    private Button giver;
    private Button receiver;
    private TextView or;
    //Giver or Receiver?
    private static final int GIVER = 0;
    private static final int RECEIVER = 1;
    private int mode;
    //Receiver input elements
    private TextInputEditText authKey;
    private TextView name;
    private TextInputEditText nameInput;
    private TextView bio;
    private TextInputEditText bioInput;
    private Button addPhoto;
    private Button submit;
    //Layouts
    private LinearLayout verticalLayout;
    private LinearLayout giveOrReceive;
    private LinearLayout options;
    private LinearLayout receiverInput;
    //Images
    private byte[] profilePic;
    private byte[] image1;
    private byte[] image2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form. Instantiate elements

        //Email Input
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        //Password Input
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //Create Account text
        createAccount = findViewById(R.id.create_account);
        createAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        //Sign in button
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        if (sharedPreferences.getString("username", null) != null) {
            //Autofill
            mEmailView.setText(sharedPreferences.getString("username", null));
            mPasswordView.setText(sharedPreferences.getString("password", null));
            attemptLogin();
        }
        //Set up intent
        mainActivity = new Intent(this,MainActivity.class);

        //Set up layouts
        verticalLayout = findViewById(R.id.verticalLayout);
        options = findViewById(R.id.options);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void createAccount() {
        //Set up animation
        ObjectAnimator slideRight = ObjectAnimator.ofFloat(options, "translationX", 1300f);
        slideRight.setDuration(1000);
        //Add new horizontal layout
        giveOrReceive = new LinearLayout(this);
        giveOrReceive.setOrientation(LinearLayout.HORIZONTAL);
        giveOrReceive.setGravity(Gravity.CENTER_HORIZONTAL);
        //Giver button
        giver = new Button(this);
        giver.setText("Giver");
        giver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                giver();
            }
        });
        //Receiver button
        receiver = new Button(this);
        receiver.setText("Receiver");
        receiver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                receiver();
            }
        });
        //Or text
        or = new TextView(this);
        or.setText("or");
        or.setPadding(30, 0, 30, 0);
        //Add to horizontal layout
        giveOrReceive.addView(giver);
        giveOrReceive.addView(or);
        giveOrReceive.addView(receiver);
        //Slide animation and add
        slideRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //Set invisible
                Animation disappear = new AlphaAnimation(1, 0);
                disappear.setDuration(0);
                giveOrReceive.startAnimation(disappear);
                //Add fade in
                verticalLayout.addView(giveOrReceive, 3);
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(700);
                giveOrReceive.startAnimation(fadeIn);
            }
        });
        slideRight.start();
    }


    private void giver() {
        mode = GIVER;
        //TODO add code to add account to server
        //Go to map view
        attemptLogin();
    }

    private void receiver() {
        mode = RECEIVER;
        //Set up animation
        ObjectAnimator slideRight = ObjectAnimator.ofFloat(giveOrReceive, "translationX", 1300f);
        slideRight.setDuration(1000);
        //Set up receiverInput fields
        receiverInput = new LinearLayout(this);
        receiverInput.setOrientation(LinearLayout.VERTICAL);
        //Layout Parameter
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        //Authentication Token
        authKey = new TextInputEditText(this);
        authKey.setLayoutParams(params);
        authKey.setHint("Authentication Key");
        receiverInput.addView(authKey);

        //Name
        name = new TextView(this);
        name.setText("Name:");
        //Name input
        nameInput = new TextInputEditText(this);
        nameInput.setLayoutParams(params);
        nameInput.setHint("First and Last Name");

        LinearLayout nameLayout = new LinearLayout(this);
        receiverInput.addView(nameLayout);
        nameLayout.setOrientation(LinearLayout.HORIZONTAL);
        nameLayout.addView(name);
        nameLayout.addView(nameInput);

        //Bio
        bio = new TextView(this);
        bio.setText("Bio:");
        bio.setGravity(Gravity.TOP);
        //Bio input
        bioInput = new TextInputEditText(this);
        bioInput.setLayoutParams(params);
        bioInput.setHint("Tell us about yourself");
        bioInput.setLines(2);

        LinearLayout bioLayout = new LinearLayout(this);
        receiverInput.addView(bioLayout);
        bioLayout.setOrientation(LinearLayout.HORIZONTAL);
        bioLayout.addView(bio);
        bioLayout.addView(bioInput);
        bioLayout.setGravity(Gravity.TOP);

        //Instruction text
        TextView instructions = new TextView(this);
        instructions.setText("Please submit a profile picture of yourself");
        receiverInput.addView(instructions);
        //Camera button
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addPhoto = new Button(this);
        addPhoto.setLayoutParams(params);
        addPhoto.setText("Add Profile Photo");
        addPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfilePic();
            }
        });
        receiverInput.addView(addPhoto);

        //Submit
        submit = new Button(this);
        submit.setText("Submit");
        submit.setLayoutParams(params);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO click submit
            }
        });
        receiverInput.addView(submit);

        slideRight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //Set invisible
                Animation disappear = new AlphaAnimation(1, 0);
                disappear.setDuration(0);
                receiverInput.startAnimation(disappear);
                //Add fade in
                verticalLayout.addView(receiverInput, 3);
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(700);
                receiverInput.startAnimation(fadeIn);
            }
        });
        //Slide right
        slideRight.start();


    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    private void getProfilePic(){
        //TODO implement pictures

        //startActivity(chooser);
        startActivityForResult(getPickImageChooserIntent(),1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == Activity.RESULT_OK) {

            //ImageView imageView = findViewById(R.id.imageView);

            if (requestCode == 1) {

                String filePath = getImageFilePath(intent);
                if (filePath != null) {
                    File file = new File(filePath);
                    try{
                        byte[] byteArr = new byte[(int)file.length()];
                        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                        inputStream.read(byteArr,0,byteArr.length);
                        inputStream.close();
                        profilePic = byteArr;

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Account getData(){
        return new Account((String)name.getText(),(String)bio.getText(),profilePic,image1,image2);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    boolean success = pieces[1].equals(mPassword);
                    if (success) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", mEmail);
                        editor.putString("password", mPassword);
                        editor.commit();
                    }

                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                //SUCCESSFUL LOGIN
                //Intent to go to MainActivity
                startActivity(mainActivity);

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

