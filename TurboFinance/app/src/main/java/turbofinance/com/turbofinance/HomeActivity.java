package turbofinance.com.turbofinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

public class HomeActivity extends AppCompatActivity {
    private String username, firstName, lastName, loginID;
    private Boolean firstLogin;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_calc);


        //Customize actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Turbo Finance");

        // Regular Login button
        Button logOut = (Button)findViewById(R.id.logout);
        logOut.setVisibility(View.INVISIBLE);

        // Facebook Login button
        LoginButton logout = (LoginButton)findViewById(R.id.logout_button);
        logout.setVisibility(View.INVISIBLE);

        // Get username from main activity login
        Intent myIntent = getIntent();
        username = myIntent.getStringExtra("Username");

//        loginID = myIntent.getStringExtra("loginID");


        final TextView helloUser = (TextView) findViewById(R.id.helloUser);
        if(username.equals("test")){
            //Facebook logout manager
            logout.setVisibility(View.VISIBLE);
            firstName = myIntent.getStringExtra("first");
            helloUser.setText(helloUser.getText() + firstName  +  "!");

            logout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    Intent logout = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(logout);
                    finish();
                }
            });

        } else{
            // Database Login!
            logOut.setVisibility(View.VISIBLE);
            firstName = myIntent.getStringExtra("First Name");
            lastName = myIntent.getStringExtra("Last Name");

                helloUser.setText(helloUser.getText() + firstName + " " + lastName + "!");

            logOut.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent logout = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(logout);
                    finish();
                }
            });
        }

        final Button simpleCalc = (Button)findViewById(R.id.simpleMortgage);
        simpleCalc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent simpleCalc = new Intent(HomeActivity.this, MortgageCalc.class);
                startActivity(simpleCalc);
            }
        });

        // Enter baseline data button
        final Button baseline = (Button) findViewById(R.id.baselineData);
        baseline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent baseline = new Intent(HomeActivity.this, BaselineActivity.class);

                baseline.putExtra("loginTest",loginID);

                startActivity(baseline);
            }
        });

        // Enter optimize mortgage calculator button
        final Button optimize = (Button) findViewById(R.id.optomizeMortg);
        optimize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming soon...",
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}