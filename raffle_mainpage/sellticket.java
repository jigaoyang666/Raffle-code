package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class sellticket extends AppCompatActivity {
    TicketTable tt = new TicketTable();
    TicketTable_MarginRaffle margin = new TicketTable_MarginRaffle();

    int RaffleID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellticket);

        // 1. Connection Database
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();

        // 2. Set Controls
        final TextView rTitle = findViewById(R.id.selltitle);
        //final EditText userName = findViewById(R.id.userName);

        final EditText tAmount = findViewById(R.id.tAmount);
        final Button confirm = findViewById(R.id.confirm);

        final AutoCompleteTextView userName = findViewById(R.id.userName);
        String str[] = getResources().getStringArray(R.array.name);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, str);
        userName.setAdapter(adapter);

        final EditText Mobile = findViewById(R.id.Mobile);
        Mobile.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if (!hasFocus)
                {
                    String mobile = Mobile.getText().toString();
                    user u = users_table.getByMobile(db,mobile);
                    if(u != null) {
                        userName.setText(u.getName());
                    }
                }
            }
        });

        // 3. Get data from ticketlist
        Bundle extras = getIntent().getExtras();
        final int rId = extras.getInt("RaffleID"); // it is to get raffle id from the ticketlist
        RaffleID = rId;
        final RaffleDetails rType = RaffleTable.getByID(db,rId);
        final String tName=rType.getTickettable();
        Log.d("ticketID", "User chose ticket ID: " + rId);

        if(rType.getType().equals("Normal")){
            tt.setTABLE_NAME(tName);
        }
        else{
            margin.setTable_name(tName);
        }

        //final ArrayList<TicketDetails> tickettable = tt.selectAll(db);      // donnot know what this mean by Dahoo
        final String t = rType.getName();
        rTitle.setText(t);

        //4. Add Listener to Cancel Button
        Button cancel=findViewById(R.id.sellCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(sellticket.this, ticketlist.class);
                i.putExtra("RaffleID",RaffleID);
                startActivity(i);
                finish();
            }
        });

        // 5. Add Listener to OK Button
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String username = userName.getText().toString();
                String mobile = Mobile.getText().toString();
                String amount = tAmount.getText().toString();
                Log.d("User",username);
                Log.d("Mobile",mobile);
                //if: it is to vaildate whether user has filled all the blank
                if (username.isEmpty() || mobile.isEmpty() || amount.isEmpty()) {
                    AlertDialog.Builder itemclick = new AlertDialog.Builder(sellticket.this);
                    itemclick.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Please fill all blank");
                    AlertDialog dialog = itemclick.create();
                    dialog.show();
                }
                else
                {
                    RaffleDetails rDetails = RaffleTable.getByID(db,rId);
                    int lim = rDetails.getLimit();// to get the ticket limit for per person
                    // to vaild whether the amount which user typed is more than limitation

                    int total = rDetails.getTotal();
                    int sold = 0;
                    int amountofOneuser = 0;

                    if(rDetails.getType().equals("Normal")){
                        amountofOneuser = tt.TicketsofOneUser(db, mobile);
                        sold = tt.soldNumber(db);
                    }
                    else{
                        amountofOneuser = margin.TicketsofOneUser(db,mobile);
                        sold = margin.soldNumber(db);
                    }

                    if(total <= sold + Integer.parseInt(amount)){
                        AlertDialog.Builder itemclick = new AlertDialog.Builder(sellticket.this);
                        itemclick.setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setTitle("The total is "+total+", and "+sold+" tickets have been sold. So your amount is not allowed.");
                        AlertDialog dialog = itemclick.create();
                        dialog.show();
                    }
                    else if(lim<Integer.parseInt(amount))
                    {
                        AlertDialog.Builder itemclick = new AlertDialog.Builder(sellticket.this);
                        itemclick.setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setTitle("The limitation is "+lim+" for a person");
                        AlertDialog dialog = itemclick.create();
                        dialog.show();
                    }
                    else if((lim < Integer.parseInt(amount) + amountofOneuser))
                    {
                        AlertDialog.Builder itemclick = new AlertDialog.Builder(sellticket.this);
                        itemclick.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("The limitation is "+lim+" for a person, and you have bought "+amountofOneuser+ " tickets.");
                        AlertDialog dialog = itemclick.create();
                        dialog.show();
                    }
                    else {
                            if(rType.getType().equals("Normal"))
                            {
                                TicketDetails upticket = new TicketDetails();
                                upticket.setName(username);
                                upticket.setMobile(mobile);
                                for (int i = 0; i < Integer.parseInt(amount); i++)
                                {
                                    //int z = (int) (Math.random() * 1000000);// create a random number
                                    //upticket.setId(z);
                                    //Log.d("TicNumber","Ticket NUMBER IS "+z);
                                    tt.insert(db, upticket);
                                }
                                user u = new user();
                                u.setMobile(mobile);
                                u.setName(username);
                                users_table.insert(db,u);
                                Log.d("Final Username",upticket.name);
                                Log.d("Final Mobile",upticket.mobile);

                            }
                            if(rType.getType().equals("Margin"))
                            {
                                MarginTicketDetails update = new MarginTicketDetails();
                                update.setName(username);
                                update.setMobile(mobile);
                                for (int i = 0; i < Integer.parseInt(amount); i++)
                                {
                                    //int z = (int) (Math.random() * 1000000);// create a random number
                                    //upticket.setId(z);
                                    //Log.d("TicNumber","Ticket NUMBER IS "+z);
                                    margin.sell(db, update);
                                }
                                user u = new user();
                                u.setMobile(mobile);
                                u.setName(username);
                                users_table.insert(db,u);
                                Log.d("Final Username",update.name);
                                Log.d("Final Mobile",update.mobile);
                            }

                            Intent i = new Intent(sellticket.this, ticketlist.class);
                            i.putExtra("RaffleID",RaffleID);
                            startActivity(i);
                            finish();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(sellticket.this, ticketlist.class);
        i.putExtra("RaffleID",RaffleID);
        startActivity(i);
        finish();
    }
}
