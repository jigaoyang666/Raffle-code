package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Edit_Create extends AppCompatActivity {
    //public static String USERNAME_KEY = "USERNAME";
    DatePickerDialog picker; // to show a dialog for user to select a draw time
    Date date; // to store draw time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__create);

        // 1. Connection Database
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();

        // 2. Set Controls
        // Set spinner
        final ArrayList<String> spinner = new ArrayList<String>();
        spinner.add("Normal");
        spinner.add("Margin");
        ArrayAdapter<String> mySpinner = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinner);
        final Spinner raffleType = findViewById(R.id.raffleType);
        raffleType.setAdapter(mySpinner);

        // Set other controls
        final EditText rafflename = findViewById(R.id.rafflename);
        final EditText description = findViewById(R.id.description);
        final EditText numbers = findViewById(R.id.numbers);
        final EditText limitation = findViewById(R.id.limitation);
        final EditText price = findViewById(R.id.price);
        final TextView drawtime = findViewById(R.id.date);
        //EditText draw ...

        // 3. Get data from MainActivity
        Bundle extras = getIntent().getExtras();
        final String j = extras.getString(MainActivity.USERNAME_KEY); //Create a parameter to get user's action such as add or edit
        final int k = extras.getInt("raffleid"); //k is ato store raffle id
        final String rName= extras.getString("Rafflename");// rName is to get raffle name from Mainactivity
        Log.d("Status", "User clicked:"+j);
        Log.d("RaffleID", "User chose raffle ID: " + k);

        // 4. Prefill content
        if(j.equals("add"))   // If user clicked 'add', empty screen
        {
            // do nothing
        }
        else if(j.equals("edit")) // 'edit', fill in raffle information
        {
            RaffleDetails d = RaffleTable.getByID(db, k);
            rafflename.setText(d.getName());       // name can not be changed because of ticket table
            rafflename.setEnabled(false);
            description.setText(d.getDescription());
            numbers.setText(String.valueOf(d.getTotal()));
            numbers.setEnabled(false);
            limitation.setText(String.valueOf(d.getLimit()));
            price.setText(String.valueOf(d.getPrice()));
            price.setEnabled(false);

            //Following two line is to convert date type from Date to String
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String finalDate = sdf.format(d.getDrawtime());
            drawtime.setText(finalDate);
            //((EditText)this.findViewById(R.id.date)).setText(d.getDrawtime());    // need to added
            if(d.getType().equals("Normal"))
            {
                raffleType.setSelection(0);
            }
            else
            {
                raffleType.setSelection(1);
            }
            raffleType.setEnabled(false);   // raffle type can not be changed because of ticket table exists
        }

        // Add Listenner to darw time
      drawtime.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              final Calendar cldr = Calendar.getInstance();
              int day = cldr.get(Calendar.DAY_OF_MONTH);
              int month = cldr.get(Calendar.MONTH);
              int year = cldr.get(Calendar.YEAR);
              // date picker dialog
              picker = new DatePickerDialog(Edit_Create.this,
                      new DatePickerDialog.OnDateSetListener() {
                          @Override
                          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                              drawtime.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                          }
                      }, year, month, day);
              //Log.d("Darwtime", "The draw time is : " +date);
              picker.show();
          }
      });

        // 5. Add Listener to  Ok Button
        //When user click the button, the following code will work
        Button edit_Create = findViewById(R.id.edit_Create);
        edit_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                RaffleDetails ed = new RaffleDetails();
                String name = rafflename.getText().toString();
                String desc = description.getText().toString();
                String nums = numbers.getText().toString();
                String lim = limitation.getText().toString();
                String pri = price.getText().toString();
                String type = raffleType.getSelectedItem().toString();
                String selecttime = drawtime.getText().toString();

                //convert drawtime from string to date
                              SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                              try {
                                  date = sdf.parse(selecttime);
                              } catch (ParseException e) {
                                  e.printStackTrace();
                              }
                Log.d("Darwtime", "The draw time is : " +date);

                //if: it is to vaildate whether user has filled all the blank
                if (name.isEmpty() || nums.isEmpty() || lim.isEmpty() || pri.isEmpty()) {
                    AlertDialog.Builder itemclick = new AlertDialog.Builder(Edit_Create.this);
                    itemclick.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Name, Numbers, Price can not be empty! Limitation default 1.");
                    AlertDialog dialog = itemclick.create();
                    dialog.show();
                }
                else
                {
                    AlertDialog.Builder itemclick=new AlertDialog.Builder(Edit_Create.this);
                    itemclick.setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RaffleDetails ed = new RaffleDetails();
                                    String name = rafflename.getText().toString();
                                    String desc = description.getText().toString();
                                    String nums = numbers.getText().toString();
                                    String lim = limitation.getText().toString();
                                    String pri = price.getText().toString();
                                    String type = raffleType.getSelectedItem().toString();
                                    ed.setName(name);
                                    ed.setDescription(desc);
                                    ed.setTotal(Integer.parseInt(nums));
                                    ed.setLimit(Integer.parseInt(lim));
                                    ed.setPrice(Integer.parseInt(pri));
                                    ed.setDrawtime(date);
                                    ed.setType(type);

                                    if(j.equals("add"))
                                    {
                                        RaffleTable.insert(db, ed);
                                    }
                                    else if(j.equals("edit"))
                                    {
                                        Log.d("RaffleID", "User chose raffle ID: " + k);
                                        ed.setId(k);         // when update, id can not be empty
                                        RaffleTable.update(db, ed);
                                    }
                                    Intent i = new Intent(Edit_Create.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setTitle("Confirm?");
                    AlertDialog dialog =itemclick.create();
                    dialog.show();
                }
            }
        });

        // 6. Add Listener to  Cancel Button
        final Button etCancel = findViewById(R.id.edit_cancel);
        etCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder itemclick=new AlertDialog.Builder(Edit_Create.this);
                itemclick.setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Edit_Create.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setTitle("Do you want to cancel editing the raffle ");
                AlertDialog dialog =itemclick.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Edit_Create.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
