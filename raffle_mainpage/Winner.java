package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class Winner extends AppCompatActivity {

    RaffleTable rd = new RaffleTable();
    int count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        // 1. Connection Database
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();

        // 2. Set Controls
        final TextView drawtitle = findViewById(R.id.drawTitle);
        final EditText winmargin = findViewById(R.id.winMargin);
        final TextView wintitle = findViewById(R.id.winTitle);
        final TextView subnumber = findViewById(R.id.subNumber);
        final TextView subname = findViewById(R.id.subName);
        final TextView submobile = findViewById(R.id.subMobile);
        final TextView winnumber = findViewById(R.id.winNumber);
        final TextView winname = findViewById(R.id.winName);
        final TextView winmobile = findViewById(R.id.winMobile);

        wintitle.setText("Winner");
        subnumber.setText("Ticket Nmuber: ");
        subname.setText("Name: ");
        submobile.setText("Mobile: ");

        //3. Get data from mainavtivity
        Bundle extras = getIntent().getExtras();
        final int rId = extras.getInt("RaffleID"); //Create a parameter to get ticket id
        //comments: maybe we can get its name from t(line 39) because of t.getName()
        final String g = extras.getString("Raffletype"); //g is ato store raffle type such as normal or margin
        final String rName= extras.getString("Rafflename");// rName is to get raffle name from Mainactivity

        //Log.d("Tickettable","table name is111"+t.getTickettable());
        drawtitle.setText(rName);
        if (g.equals("Normal"))
        {
            winmargin.setEnabled(false);
        }

        if(RaffleTable.getByID(db, rId).getWinner() != 0){
            RaffleDetails r = RaffleTable.getByID(db,rId);
            winnumber.setText(String.valueOf(r.getWinner()));
            int winner = r.getWinner();
            if(g.equals("Normal"))
            {
                TicketTable tt = new TicketTable();
                tt.setTABLE_NAME(r.getTickettable());
                winname.setText(tt.getByID(db,winner).getName());
                winmobile.setText(tt.getByID(db,winner).getMobile());
            }
            else{
                TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
                tm.setTable_name(r.getTickettable());
                winname.setText(tm.getByTicketID(db,winner).getName());
                winmobile.setText(tm.getByTicketID(db,winner).getMobile());
            }

            winmargin.setEnabled(false);
            findViewById(R.id.draw).setEnabled(false);
        }

        //3. set a onclick listener and draw a winner
        Button draw = findViewById(R.id.draw);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RaffleDetails t = RaffleTable.getByID(db,rId);

                int sold = 0;
                if(t.getType().equals("Normal")) {
                    TicketTable tt = new TicketTable();
                    tt.setTABLE_NAME(t.getTickettable());
                    sold = tt.soldNumber(db);
                }
                else{
                    TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
                    tm.setTable_name(t.getTickettable());
                    sold = tm.soldNumber(db);
                }

                Date drawtime = t.getDrawtime();
                Log.d("Tickettable","table name is "+t.getTickettable());
                Date now = new Date();

                if (now.compareTo(drawtime) < 0) {
                    Log.d("Draw", "It is not drawtime yet!");   // too early
                    AlertDialog.Builder itemclick=new AlertDialog.Builder(Winner.this);
                    itemclick.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Winner.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setTitle("It is not drawtime yet!");
                    AlertDialog dialog =itemclick.create();
                    dialog.show();
                }
                else if ( sold <= 0) {
                    Log.d("Draw", "Tickets have not been sold, so this raffle can not be drawed!");
                    AlertDialog.Builder itemclick=new AlertDialog.Builder(Winner.this);
                    itemclick.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Winner.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setTitle("Tickets have not been sold, so this raffle can not be drawed!");
                    AlertDialog dialog =itemclick.create();
                    dialog.show();
                }
                else
                {
                    findViewById(R.id.draw).setEnabled(false);
                    // draw a winner in normal raffle
                    if(g.equals("Normal"))
                    {
                        TicketTable tt = new TicketTable();
                        tt.setTABLE_NAME(t.getTickettable());
                        int tickets = tt.selectAllID(db).size();

                        int min = 1;
                        int z = (int) (Math.random() * (tickets - min + 1) + min);
                        RaffleTable.draw(db , t , z);

                        winnumber.setText(String.valueOf(z));
                        winname.setText(tt.getByID(db,z).getName());
                        winmobile.setText(String.valueOf(tt.getByID(db,z).getMobile()));
                    }
                    else
                    { // this part is to draw a winner in margin raffle
                        TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
                        tm.setTable_name(t.getTickettable());
                        ArrayList<MarginTicketDetails> mtl = tm.selectAll(db);
                        String number = winmargin.getText().toString();
                        int num = Integer.parseInt(number);

                        //tm.draw(db,num);
                        if(RaffleTable.draw(db , t , num)==1){
                            winnumber.setText(number);
                            winname.setText(tm.getByTicketID(db,num).getName());
                            winmobile.setText(tm.getByTicketID(db,num).getMobile());
                        }
                        else{
                            winnumber.setText("No winner!");
                            winname.setText("");
                            winmobile.setText("");
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Winner.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
