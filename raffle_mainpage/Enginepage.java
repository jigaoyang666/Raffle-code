package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Enginepage extends AppCompatActivity {
    public static final String USERNAME_KEY = "USERNAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enginepage);


        //1. Database test for Dahoo temporarily
        //1、Open the database, so that we can read and write
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();


        //2、The following code is to load raffle list
        final ArrayList<RaffleDetails> rafflelist = RaffleTable.selectAll(db);
        final ListView Listdetail = findViewById(R.id.enListdetail);
        final raffleAdapter RaffleListAdapter =
                new raffleAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_single_choice, rafflelist);
        Listdetail.setAdapter(RaffleListAdapter);
        Listdetail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        RaffleListAdapter.notifyDataSetChanged();

        // 3. get useraction from mainactivity
        Bundle extras = getIntent().getExtras();
        final  String action = extras.getString("Useraction");


        // 4. set item click listenner
        Listdetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                RaffleDetails p = (RaffleDetails)Listdetail.getItemAtPosition(position);
                //temp=p.getId();
                List<RaffleDetails> rds = new ArrayList<RaffleDetails>();
                //RaffleDetails t = RaffleTable.getByID(db,p.getId());
                rds.add(p);
                if(action.equals("edit") || action.equals("delete") || action.equals("draw"))
                {
                    if(action.equals("edit"))
                    {   //page will jump to Edit page
                        int enteredContent=rds.size();
                        Intent i = new Intent(Enginepage.this, Edit_Create.class);
                        i.putExtra(USERNAME_KEY,"edit");
                        i.putExtra("raffleid",rds.get(0).getId());
                        i.putExtra("Rafflename",rds.get(0).getName());
                        startActivity(i);
                    }
                    if(action.equals("delete"))
                    {   //program will implement delete function
                        TicketTable temptable= new TicketTable();
                        boolean result = RaffleTable.delete(db, RaffleTable.getByID(db,p.getId()));
                        if(result== false)
                        {
                            AlertDialog.Builder itemclick=new AlertDialog.Builder(Enginepage.this);
                            itemclick.setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Enginepage.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    })
                                    .setTitle("Tickets have been sold, so this raffle can not be deleted!");
                            AlertDialog dialog =itemclick.create();
                            dialog.show();
                        }
                        else
                        {
                            AlertDialog.Builder itemclick=new AlertDialog.Builder(Enginepage.this);
                            itemclick.setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Enginepage.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    })
                                    .setTitle(p.getName()+" has been deleted");
                            AlertDialog dialog =itemclick.create();
                            dialog.show();
                        }
                    }
                    if(action.equals("draw"))
                    {   //jumping to draw a winner page
                        Intent i = new Intent(Enginepage.this, Winner.class);
                        i.putExtra("RaffleID",rds.get(0).getId());
                        i.putExtra("Rafflename",rds.get(0).getName());
                        i.putExtra("Raffletype", rds.get(0).getType());
                        startActivity(i);
                    }
                }
            }
        });

        // 5. set a back button
        Button back= findViewById(R.id.enBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Enginepage.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}
