package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ticketlist extends AppCompatActivity {
    TicketTable tt = new TicketTable();
    TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
    ArrayList<TicketDetails> ticketlist = new ArrayList<TicketDetails>();

    Toast toast;
    //RaffleDetails t;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketlist);
        toast = Toast.makeText(getApplicationContext(),"",0);

        //1、Open the database, so that we can read and write
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();


        //2 get data from Manactivity and set ticket table name
        Bundle extras = getIntent().getExtras();
        final int rId = extras.getInt("RaffleID"); //Create a parameter to get ticket id
        final RaffleDetails t = RaffleTable.getByID(db,rId);
        final String g = t.getType();
        final String rName= t.getName();
        //final int tPrice = extras.getInt("Raffleprice"); // to get ticket price from the Mainactivity
        //Log.d("RaffleId", "Raffle id is:"+tPrice);
        Log.d("Raffletype", "The raffle type is: " + g);
        //tt.setTABLE_NAME(tName);

        Log.d("Tickettable","table name is"+t.getTickettable());
        final TextView listtitle = findViewById(R.id.listTitle);
        listtitle.setText(rName);
        //temname.add(t.getTickettable());


        //1 show ticket list
        final ListView realList = findViewById(R.id.realList);
        if(t.getType().equals("Normal")){
            tt.setTABLE_NAME(t.getTickettable());
            ticketlist = tt.selectAll(db);
        }
        else{
            tm.setTable_name(t.getTickettable());
            ArrayList<MarginTicketDetails> mtl = tm.selectAll(db);
            for (int counter = 0; counter < mtl.size(); counter++) {
                ticketlist.add((TicketDetails)mtl.get(counter));
            }
        }

        final ticketAdapter TicketListAdapter =
                new ticketAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_1, ticketlist);
        realList.setAdapter(TicketListAdapter);

        //2、To get the ticket of user's selection
        realList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Log.d("Clicked", "Item Clicked: "+ position);
                TicketDetails tds = (TicketDetails) realList.getItemAtPosition(position);
                int tId =tds.getId();
                String uName =tds.getName();
                String uMobile = tds.getMobile();
                Date tDate= tds.getCreation();

                //Following two line is to convert date type from Date to String
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String finalDate = sdf.format(tDate);

                //Log.d("Clicked", "Items Clicked in total: "+ tds.size());
                Intent i = new Intent(ticketlist.this, information.class);
                i.putExtra("Ticketid",tId);
                i.putExtra("Username",uName);
                i.putExtra("Usermobile",uMobile);
                i.putExtra("Pdate",finalDate);
                i.putExtra("Ticketprice",t.getPrice());
                i.putExtra("Rafflename",rName);
                i.putExtra("RaffleID",rId);
                //Log.d("test","show ticket id is : "+tds.getId());
                //Log.d("test","show ticket  name is: "+tds.getName());
                //Log.d("test","show ticket mobile is : "+tds.getMobile());
                //Log.d("test","show ticket date is : "+tds.getCreation());
                //Log.d("test","show ticket price is : "+tPrice);
                startActivity(i);
                finish();
            }
        });

        //3 jump to sellticket page
        Button sell = findViewById(R.id.sell);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Winner"," "+t.getWinner());
                if(t.getWinner() == 0){
                    Intent i = new Intent(ticketlist.this, sellticket.class);
                    //i.putExtra("Rafflename",rName);
                    i.putExtra("RaffleID",rId);
                    //i.putExtra("Tablename",t.getTickettable());
                    Log.d("Tname",t.getTickettable());
                    startActivity(i);
                    finish();
                }
                else{
                String text = "No! This raffle has already been drawn!";
                toast.cancel();
                toast = Toast.makeText(getApplicationContext(), text,1);
                toast.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(ticketlist.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
