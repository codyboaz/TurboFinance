package turbofinance.com.turbofinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class BaselineActivity extends AppCompatActivity {

    private EditText moneyNow, moneyLater, balance, balloon, numPeriods, faceInt, sellInt, tempName;
    private TextView saleProf, totalInt,monthPayment;
    private Button calcBttn, saveBttn, loadBttn;
    private String loginID;
    private Double currentBalance,intRate,years,payment,sellerInt, presVal, totalInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseline);
        Intent i = getIntent();
        loginID = i.getStringExtra("loginTest");


        //Customize actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Turbo Finance");

        // Edit Text Views
        moneyNow = (EditText)findViewById(R.id.moneyNow);
        moneyLater = (EditText)findViewById(R.id.moneyLater);
        balance = (EditText)findViewById(R.id.balance);
        balloon = (EditText)findViewById(R.id.balloon);
        numPeriods = (EditText)findViewById(R.id.numPeriods);
        faceInt = (EditText)findViewById(R.id.faceInt);
        sellInt = (EditText)findViewById(R.id.sellInt);
        tempName = (EditText)findViewById(R.id.tempName);

        // Text Views
        saleProf = (TextView)findViewById(R.id.saleProf);
        monthPayment = (TextView)findViewById(R.id.monthPayment);
        totalInt = (TextView)findViewById(R.id.totalInt);

        // Buttons
        calcBttn = (Button)findViewById(R.id.calcBttn);
        calcBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO calc logic here!
                if(moneyNow.getText().toString().isEmpty() || moneyLater.getText().toString().isEmpty() || balance.getText().toString().isEmpty()
                        || balloon.getText().toString().isEmpty() || numPeriods.getText().toString().isEmpty() ||
                        faceInt.getText().toString().isEmpty() || sellInt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please make sure all fields above Monthly Payments are completed...",
                            Toast.LENGTH_LONG).show();
                } else if (!monthPayment.getText().toString().isEmpty() && !saleProf.getText().toString().isEmpty()
                        && !totalInt.getText().toString().isEmpty()) {
                    monthPayment.setText("");
                    totalInt.setText("");
                    saleProf.setText("");
                } else {
                    // Calculates Monthly Payments
                    presVal = Double.parseDouble(balance.getText().toString());
                    intRate = Double.parseDouble(faceInt.getText().toString());
                    intRate = ((intRate/100)/12);
                    years = Double.parseDouble(numPeriods.getText().toString());
                    years = years * 12;
                    payment = (presVal * intRate) / (1 -  Math.pow(1 + intRate, -years));
                    payment = (payment * 100) / 100;
                    String formattedString = String.format("%.02f", payment);

                    monthPayment.setText(formattedString);

                    // Calculates Sale Profit (PV - moneyNow)
                    sellerInt = Double.parseDouble(sellInt.getText().toString());
                    sellerInt = ((sellerInt/100)/12);
                    double x =  (Math.pow((1. + sellerInt),years));
                    presVal = ((0 - payment / sellerInt * ( 1.0 - x) ) / x);
                    presVal =  (presVal * 100) / 100;
                    presVal = (presVal - Double.parseDouble(moneyNow.getText().toString()));
                    String saleProfit = String.format("%.02f", presVal);
                    saleProf.setText("$"+ saleProfit);

                    // Calculate Total Interest
                    totalInterest = (payment * ((Double.parseDouble(numPeriods.getText().toString()) * 12)) - Double.parseDouble(balance.getText().toString()));
                    String totalInter = String.format("%.02f", totalInterest);
                    totalInt.setText("$"+totalInter);

                }
            }
        });

        saveBttn = (Button)findViewById(R.id.saveBttn);
        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moneyNow.getText().toString().isEmpty() || moneyLater.getText().toString().isEmpty() ||
                        balance.getText().toString().isEmpty() || balloon.getText().toString().isEmpty() ||
                        numPeriods.getText().toString().isEmpty() || faceInt.getText().toString().isEmpty() ||
                        sellInt.getText().toString().isEmpty() || tempName.getText().toString().isEmpty() ||
                        saleProf.getText().toString().isEmpty() ||  totalInt.getText().toString().isEmpty() ||
                        monthPayment.getText().toString().isEmpty() || tempName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please make sure all fields are filled...",
                            Toast.LENGTH_LONG).show();
                } else {
                    String restURL = "http://seansabour.net/turbofinance/set_templates.php?loginID=" + loginID + "&templateName="+ tempName.getText().toString() +
                    "&moneyNow="+ moneyNow.getText().toString() +"&moneyLater="+ moneyLater.getText().toString() +"&balance="+ balance.getText().toString() +"&balloon="+
                            balloon.getText().toString() +"&periods="+ numPeriods.getText().toString() +"&faceInterest="+ faceInt.getText().toString() +
                            "&monthlyPayment="+ monthPayment.getText().toString() +"&sellInterest=" + sellInt.getText().toString() +
                            "&sellProfit="+ saleProf.getText().toString() + "&totalInterest=" + totalInt.getText().toString();
                    new RestOperation().execute(restURL);
                }
            }
        });

        loadBttn = (Button)findViewById(R.id.loadBttn);
        loadBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop up with saved templates for user.
                Toast.makeText(getApplicationContext(), "Coming soon...",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private class RestOperation extends AsyncTask<String, Void, Void> {

        final HttpClient httpClient = new DefaultHttpClient();
        String content;
        String error;
        ProgressDialog progressDialog = new ProgressDialog(BaselineActivity.this);
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Please wait ...");
            progressDialog.show();

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

        }
    }

}
