package com.example.bayan_oh.inspect;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import 	java.text.ParseException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


public class ScannedExpDateActivity extends Activity implements OnClickListener {

    //UI References
    private EditText expDate;
    private Button changeButton;
    private Button cancelButton;
    private Button nextButton;
    private String recognizedDate;
    private Date xpdate;

    private DatePickerDialog expDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_exp_date);

        //Get the recognized expiration date from the Camera Scan
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recognizedDate = extras.getString("recognizedDate");

            if(recognizedDate != null){
                Toast.makeText(getApplicationContext(), "Date: "+recognizedDate,
                        Toast.LENGTH_LONG).show();
            }
            else {
                //If the expiration date is not recognized, show a toast message informing the user
                Toast.makeText(getApplicationContext(), "Sorry, the expiration date is not recognized",
                        Toast.LENGTH_LONG).show();
            }
        }


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();
        setDateTimeField();
    }

    //UI References
    private void findViewsById() {
        expDate = (EditText) findViewById(R.id.txtExpDate);
        expDate.setInputType(InputType.TYPE_NULL);
        if(recognizedDate != null){
            expDate.setText(recognizedDate);
        }
        expDate.setText(recognizedDate);
        expDate.setEnabled(false);

        changeButton = (Button) findViewById(R.id.btnChangeDate);
        cancelButton = (Button) findViewById(R.id.btnCancel);
        nextButton = (Button) findViewById(R.id.btnNext);

        cancelButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    //This method set the expiration date text field to the expiration date the user inserted
    private void setDateTimeField() {
        changeButton.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        expDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                expDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        //If the Change button is clicked, show a date picker to let the user change the date
        if(view == changeButton) {
            expDatePickerDialog.show();
        }

        //Cancel the scanning process if the Capture button is clicked and return to the home activity
        if(view == cancelButton) {
            Intent k = new Intent(ScannedExpDateActivity.this, MainActivity.class);
            startActivity(k);
        }

        if(view == nextButton) {
                //Check if the expiration date test field is filled and in the right format
            if (!expDate.getText().toString().trim().matches("^\\d{2}\\D\\d{2}\\D\\d{4}$")|| expDate.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Expiration date is not in in the right format",
                        Toast.LENGTH_LONG).show();
                }
            else{
                try {
                    xpdate = dateFormatter.parse(expDate.getText().toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Check if the expiration date has not already ended
                if (xpdate.before(new Date()))
                { // Expiration Date has already ended
                    Toast.makeText(getApplicationContext(), "Expiration Date has already ended",
                            Toast.LENGTH_LONG).show();
                }
                //Go to the Fill Product Info activity
                else {
                    Intent k = new Intent(ScannedExpDateActivity.this, EditProductActivity.class);
                    k.putExtra("expDate", expDate.getText().toString());
                    startActivity(k);
                }

            }
        }
    }
}
