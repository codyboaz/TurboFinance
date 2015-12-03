package turbofinance.com.turbofinance;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MortgageCalc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage);

        //Customize actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Turbo Finance");



        final Button calculate = (Button)findViewById(R.id.calcBttn);
        calculate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText pvInput, pmtInput, irInput, numYrInput;
                double presVal, payment, intRate, years;
                TextView pv,monthPay, numYear, ir;


                pvInput = (EditText)findViewById(R.id.pv);
                pmtInput = (EditText)findViewById((R.id.monthPmt));
                irInput = (EditText)findViewById(R.id.ir);
                numYrInput = (EditText)findViewById(R.id.numYear);

                if(pvInput.getText().toString().isEmpty() && !pmtInput.getText().toString().isEmpty()
                        && !irInput.getText().toString().isEmpty() && !numYrInput.getText().toString().isEmpty()) {
                    payment = Double.parseDouble(pmtInput.getText().toString());
                    intRate = Double.parseDouble(irInput.getText().toString());
                    intRate = ((intRate/100)/12);
                    years = Double.parseDouble(numYrInput.getText().toString());
                    years = years * 12;
                    double x =  (Math.pow((1. + intRate),years));
                    presVal = ((0 - payment / intRate * ( 1.0 - x) ) / x);
                    presVal =  (presVal * 100) / 100;
                    pv = (TextView)findViewById(R.id.pv);
                    String formattedString = String.format("%.02f", presVal);
                    pv.setText(formattedString);
                } else if(!pvInput.getText().toString().isEmpty() && pmtInput.getText().toString().isEmpty()
                        && !irInput.getText().toString().isEmpty() && !numYrInput.getText().toString().isEmpty()) {

                    presVal = Double.parseDouble(pvInput.getText().toString());
                    intRate = Double.parseDouble(irInput.getText().toString());
                    intRate = ((intRate/100)/12);
                    years = Double.parseDouble(numYrInput.getText().toString());
                    years = years * 12;
                    payment = (presVal * intRate) / (1 -  Math.pow(1 + intRate, -years));
                    payment = (payment * 100) / 100;
                    String formattedString = String.format("%.02f", payment);

                    monthPay = (TextView)findViewById(R.id.monthPmt);
                    monthPay.setText(formattedString);
                } else if(!pvInput.getText().toString().isEmpty() && !pmtInput.getText().toString().isEmpty()
                        && irInput.getText().toString().isEmpty() && !numYrInput.getText().toString().isEmpty()) {

                    presVal = Double.parseDouble(pvInput.getText().toString());
                    payment = Double.parseDouble(pmtInput.getText().toString());
                    years = Double.parseDouble(numYrInput.getText().toString());
                    years = years * 12;
                    double i, f, fprime, x;
                    intRate = 0.11;
                    i = 0.;
                    while( Math.abs(intRate - i) > .0001){
                        i = intRate;
                        x = Math.pow(1.0 + intRate, years);
                        f =  -payment / intRate * (1.0 - x) - presVal * x;
                        fprime = payment / intRate / intRate * (1.0 + Math.pow((1.0+intRate), years - 1) *
                                years * intRate - x) - presVal * years * Math.pow(1.0 + intRate, years-1);
                        intRate = i - f/fprime;
                    }
                    intRate = (12 * 100 * intRate * 100) / 100;
                    String formattedString = String.format("%.02f", intRate);

                    ir = (TextView)findViewById(R.id.ir);
                    ir.setText(formattedString);
                } else if(!pvInput.getText().toString().isEmpty() && !pmtInput.getText().toString().isEmpty()
                        && !irInput.getText().toString().isEmpty() && numYrInput.getText().toString().isEmpty()) {

                    presVal = Double.parseDouble(pvInput.getText().toString());
                    payment = Double.parseDouble(pmtInput.getText().toString());
                    intRate = Double.parseDouble(irInput.getText().toString());
                    intRate = (intRate / 100) / 12;
                    years = Math.log((0. - payment/intRate)/(presVal - payment / intRate))
                            /Math.log(1.0 + intRate);
                    years = Math.round((years / 12 * 100) / 100);
                    numYear = (TextView)findViewById(R.id.numYear);

                    if(years <= 0) {
                        Toast.makeText(getApplicationContext(), "Interest Charge is greater than monthly payment.", Toast.LENGTH_LONG).show();
                        numYear.setText("");
                    } else {
                        String formattedString = String.format("%.02f", years);
                        numYear.setText(formattedString);
                    }


                } else if(!pvInput.getText().toString().isEmpty() && !pmtInput.getText().toString().isEmpty()
                        && !irInput.getText().toString().isEmpty() && !numYrInput.getText().toString().isEmpty()) {
                    pvInput.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Three Fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


