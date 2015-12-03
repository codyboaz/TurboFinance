package turbofinance.com.turbofinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private String firstName;
    private Profile profile;
    private ProfileTracker mProfileTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);


        if(isLoggedIn()){
           if( Profile.getCurrentProfile() != null){
               profile = Profile.getCurrentProfile();
               firstName = profile.getName();
               Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
               myIntent.putExtra("first",firstName);
               myIntent.putExtra("Username","test");
               startActivity(myIntent);
               finish();
           }
        }

        final EditText userinput = (EditText) findViewById(R.id.userinput);
        final EditText password = (EditText) findViewById(R.id.password);

        //Customize actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Turbo Finance");

        // Login to database
        Button login = (Button) findViewById(R.id.getservicedata);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restURL = "http://seansabour.net/turbofinance/get_login.php?username=" + userinput.getText().toString().trim() + "&password=" + password.getText().toString().trim();
                new RestOperation().execute(restURL);

            }
        });

        // Create Account
        Button create = (Button) findViewById(R.id.signUp);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        // connect with facebook
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    if(Profile.getCurrentProfile() == null) {
                                        mProfileTracker = new ProfileTracker() {
                                            @Override
                                            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                                Log.v("facebook - profile", profile2.getFirstName());
                                                mProfileTracker.stopTracking();
                                            }
                                        };
                                        mProfileTracker.startTracking();
                                    } else {
                                        profile = Profile.getCurrentProfile();
                                        firstName = profile.getName();
                                        Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                                        myIntent.putExtra("first", firstName);
                                        myIntent.putExtra("Username", "test");
                                        startActivity(myIntent);
                                        finish();
                                    }
                                }
                            }
                        }).executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private class RestOperation extends AsyncTask<String, Void, Void> {

        final HttpClient httpClient = new DefaultHttpClient();
        String content;
        String error;
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String data = "";
        final EditText userinput = (EditText) findViewById(R.id.userinput);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Please wait ...");
            progressDialog.show();

            try {
                data += "&" + URLEncoder.encode("data", "UTF-8") + "=" + userinput.getText();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            BufferedReader br = null;

            URL url;
            try {
                url = new URL(params[0]);

                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);


                OutputStreamWriter outputStreamWr = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWr.write(data);
                outputStreamWr.flush();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = br.readLine())!=null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }

                content = sb.toString();



            } catch (MalformedURLException e) {
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = e.getMessage();
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(error!=null) {
                // Set error if something happens!?
            } else {
                try {
                    JSONObject o = new JSONObject(content);
                    String user = o.getString("username");
                    String fName = o.getString("firstName");
                    String lName = o.getString("lastName");

                    Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
                    myIntent.putExtra("Username", user);
                    myIntent.putExtra("First Name", fName);
                    myIntent.putExtra("Last Name", lName);

                    progressDialog.setTitle("Sucessful Login ...");
                    progressDialog.show();
                    startActivity(myIntent);
                    progressDialog.dismiss();
                    finish();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect username or password",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
