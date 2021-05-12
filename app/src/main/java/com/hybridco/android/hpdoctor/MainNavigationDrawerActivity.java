package com.hybridco.android.hpdoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hybridco.android.hpdoctor.Map.MapActivity;
import com.hybridco.android.hpdoctor.meddata.MedDataExpandableListData;
import com.hybridco.android.hpdoctor.meddata.MedDataUtils;
import com.hybridco.android.hpdoctor.pills.PillsFragment;
import com.hybridco.android.hpdoctor.meddata.MedDataFragment;
import com.hybridco.android.hpdoctor.utilities.Utilities;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainNavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private static Context mContext;
    public static Activity activity = null;
    public static final int REQUEST_CODE = 1;
    SignInButton signInButton;
    TextView userNameTextView;
    CircleImageView userPhoto;
    ImageButton uploadButton, downloadButton;
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreference init
        SharedPreferences sharedPref =
                this.getSharedPreferences("com.hybridco.android.hpdoctor_preferences",
                        MODE_PRIVATE);

        // Remembered SharedPreference theme to display
        Boolean nightMode = sharedPref.getBoolean("night_mode", false);
        if(nightMode == false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Remembered SharedPreference language to display
        String langValue = sharedPref.getString("language_list", "");
        Resources resources = getResources();
        if (langValue.equals("1")) {
            Utilities.changeLang("en", resources);
        } else if (langValue.equals("2")) {
            Utilities.changeLang("ro", resources);
        }

        // Views to be displayed
        setContentView(R.layout.main_activity_navigation_drawer);

        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer view
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Header layout
        View headerLayout = navigationView.getHeaderView(0);
        signInButton = headerLayout.findViewById(R.id.nav_sign_in_button);
        mAuth = FirebaseAuth.getInstance();

        // Firebase request based on ID and email type
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        userNameTextView = headerLayout.findViewById(R.id.nav_user_name);
        userPhoto = headerLayout.findViewById(R.id.nav_user_photo);
        uploadButton = headerLayout.findViewById(R.id.nav_upload);
        downloadButton = headerLayout.findViewById(R.id.nav_download);

        // Gets current user and updates UI if logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateUI(user);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { signIn(); }

        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PillsFragment()).commit();
        }
    }

    /** Fragment and activity starters for each drawer menu option */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_pills:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PillsFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MedDataFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_maps:
                Intent intentMap = new Intent(this, MapActivity.class);
                startActivity(intentMap);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                // Callback to ensure that settings changes apply
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /** Recreates the activity when coming back from settings activity in order to apply changes */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            recreate();
        }
    }

    /** Starts google login activity */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            FirebaseGoogleAuth(null);
        }
    }

    /** Google account firebase login */
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    updateUI(null);
                }
            }
        });
    }

    /** Updates the navigation drawer UI elements to visible/invisible */
    private void updateUI(FirebaseUser fUser) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            userNameTextView.setVisibility(View.VISIBLE);
            userPhoto.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
            downloadButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            userNameTextView.setText(account.getDisplayName());
            Picasso.with(this).load(account.getPhotoUrl()).into(userPhoto);

            userPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });

            userNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                    alert.setMessage((activity.getString(R.string.drawer_menu_data_upload_request)));

                    alert.setPositiveButton(activity.getString(R.string.drawer_menu_data_upload),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Utilities.uploadToFirebase(account.getDisplayName(), activity);
                                }
                            });

                    alert.setNegativeButton(activity.getString(R.string.drawer_menu_data_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });

                    alert.show();
                }
            });

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                    alert.setMessage((activity.getString(R.string.drawer_menu_data_download_request)));

                    alert.setPositiveButton(activity.getString(R.string.drawer_menu_data_dowload),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Utilities.downloadFromFirebase(account.getDisplayName(), activity);
                                }
                            });

                    alert.setNegativeButton(activity.getString(R.string.drawer_menu_data_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });

                    alert.show();
                }
            });
        }
    }

    private void logOut() {
        boolean ok = false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(this.getString(R.string.drawer_menu_logout));
        alertDialogBuilder.setPositiveButton(this.getString(R.string.drawer_menu_ok),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    userNameTextView.setVisibility(View.GONE);
                    userPhoto.setVisibility(View.GONE);
                    uploadButton.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.GONE);
                    signInButton.setVisibility(View.VISIBLE);
                    signOut();
//
//                    FirebaseAuth.getInstance().signOut();
////                    mAuth.signOut();
                }
            });

        alertDialogBuilder.setNegativeButton(this.getString(R.string.drawer_menu_cancel),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialogBuilder.show();
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

}