package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class information extends AppCompatActivity {

    int RaffleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //1、Open the database, so that we can read and write
        //Database databaseConnection = new Database(this);
        //final SQLiteDatabase db = databaseConnection.open();

        //1. set control
        TextView viewId = findViewById(R.id.viewId);
        TextView viewName = findViewById(R.id.viewName);
        TextView viewMobile = findViewById(R.id.viewMobile);
        TextView viewPrice = findViewById(R.id.viewPrice);
        TextView viewDate = findViewById(R.id.viewDate);

        //2. get data from the ticket list
        Bundle extras= getIntent().getExtras();
        final int tId = extras.getInt("Ticketid"); //Create a parameter to get ticket id from ticket list
        final String uName = extras.getString("Username"); //Create a parameter to get customer's name from ticket list
        final String uMobile = extras.getString("Usermobile"); // get customer's phone number
        final int tPrice = extras.getInt("Ticketprice"); // get ticket price
        final String tDate = extras.getString("Pdate"); // get purchase date
        final String rName= extras.getString("Rafflename");// rName is to get raffle name from ticketlist
        RaffleID = extras.getInt("RaffleID"); // it is to get raffle id from the ticketlist
        Log.d("User","Information page id is : "+ tId);
        Log.d("User","information page customer's name is : "+ uName);
        Log.d("User","information page customer's phone is : "+ uMobile);
        Log.d("User","information page ticket price is : "+ tPrice);
        //Log.d("User","information page date is : "+ tDate);

        //set a title
        TextView title = findViewById(R.id.infoTitle);
        title.setText(rName);

        // 3.show ticket information
        viewId.setText(String.valueOf(tId));
        viewName.setText(uName);
        viewMobile.setText(uMobile);
        viewPrice.setText(String.valueOf(tPrice));
        viewDate.setText(tDate);

        //d)、When user click the dram a winner buttion, the page will jump to winner page
        Button draw = findViewById(R.id.button8);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String share = uName + " (Mobile:" + uMobile + "), Ticket " + tId + ", $"+ tPrice + ", Purchased " + tDate;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        // set a confirm click listenner
        Button confirm = findViewById(R.id.infoConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(information.this, ticketlist.class);
                i.putExtra("RaffleID",RaffleID);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(information.this, ticketlist.class);
        i.putExtra("RaffleID",RaffleID);
        startActivity(i);
        finish();
    }
}
