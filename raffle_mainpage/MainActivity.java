package au.edu.utas.gaoyangj.raffle_mainpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String USERNAME_KEY = "USERNAME";
    String operation ="normal"; // for store different acitvities that user conduct
    Toast toast;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(getApplicationContext(),"",0);

        //1. open database
        Database databaseConnection = new Database(this);
        final SQLiteDatabase db = databaseConnection.open();

        //2、The following code is to load raffle list
        final ListView Listdetail = findViewById(R.id.enListdetail);
         raffleAdapter RaffleListAdapter =
                new raffleAdapter(getApplicationContext(),
                        android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
        Listdetail.setAdapter(RaffleListAdapter);
        operation ="normal";
        RaffleListAdapter.notifyDataSetChanged();    // What is thie???  Commented by Dahoo


        //ADD
        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, Edit_Create.class);
                        i.putExtra(USERNAME_KEY,"add");
                        i.putExtra("raffleid",0);
                        startActivity(i);
                        finish();
                }
        });

        //EDIT
        Button edit=findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operation.equals("normal")){  // normal --> select
                    final raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_single_choice, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    Listdetail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    operation = "select-edit";
                }
                else if(operation.equals("edit"))
                {
                    operation = "normal";
                    int position = Listdetail.getCheckedItemPosition();
                    if(position != Listdetail.INVALID_POSITION){
                        RaffleDetails p = (RaffleDetails)Listdetail.getItemAtPosition(position);
                        Intent i = new Intent(MainActivity.this, Edit_Create.class);
                        i.putExtra(USERNAME_KEY,"edit");
                        i.putExtra("raffleid",p.getId());
                        i.putExtra("Rafflename",p.getName());
                        startActivity(i);
                        finish();
                    }
                }
                else{   // others -> normal
                    raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    operation = "normal";
                }
                //Intent i = new Intent(MainActivity.this, Enginepage.class);
                //i.putExtra("Useraction","edit");
                //startActivity(i);
            }
        });

        //DELETE
        Button delete = findViewById(R.id.enBack);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operation.equals("normal")) {   // normal -> select-delete
                    final raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_multiple_choice, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    Listdetail.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    operation = "select-delete";
                }
                else if(operation.equals("delete")){  // delete -> normal
                    AlertDialog.Builder itemclick=new AlertDialog.Builder(MainActivity.this);
                    itemclick.setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int len = Listdetail.getCount();
                                    SparseBooleanArray checked = Listdetail.getCheckedItemPositions();
                                    int count = 0;
                                    int delete = 0;
                                    int nodelete = 0;
                                    for(int i=0;i<len;i++)
                                    {
                                        if(checked.get(i)){
                                            count ++;
                                            RaffleDetails rd = (RaffleDetails)Listdetail.getItemAtPosition(i);
                                            boolean result = RaffleTable.delete(db, RaffleTable.getByID(db,rd.getId()));
                                            if(result== false)
                                            {
                                                nodelete ++;
                                            }
                                            else
                                            {
                                                delete ++;
                                            }
                                        }
                                    }
                                    String text = "Delete: "+String.valueOf(delete)+" Raffles\r\n" + String.valueOf(nodelete) + " Raffles can not be deleted!";
                                    toast.cancel();
                                    toast = Toast.makeText(getApplicationContext(), text,1);
                                    toast.show();
                                    raffleAdapter RaffleListAdapter =
                                            new raffleAdapter(getApplicationContext(),
                                                    android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
                                    Listdetail.setAdapter(RaffleListAdapter);
                                    operation = "normal";
                                }
                            })
                            .setTitle("Delete? Click OK to confirm!");
                    itemclick.setCancelable(true)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    raffleAdapter RaffleListAdapter =
                                            new raffleAdapter(getApplicationContext(),
                                                    android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
                                    Listdetail.setAdapter(RaffleListAdapter);
                                    operation = "normal";
                                }
                            });
                    AlertDialog dialog =itemclick.create();
                    dialog.show();
                }
                else{  // others -> normal
                    raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    operation = "normal";
                }
                //Intent i = new Intent(MainActivity.this, Enginepage.class);
                //i.putExtra("Useraction","delete");
                //startActivity(i);
            }
        });

        //DRAW
        Button draw = findViewById(R.id.draw);
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(operation.equals("normal")){  // normal --> select-draw
                    final raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_single_choice, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    Listdetail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    operation = "select-draw";
                }
                else if(operation.equals("draw"))  // draw --> normal
                {
                    operation = "normal";
                    int position = Listdetail.getCheckedItemPosition();
                    if(position != Listdetail.INVALID_POSITION){
                        RaffleDetails p = (RaffleDetails)Listdetail.getItemAtPosition(position);
                        Intent i = new Intent(MainActivity.this, Winner.class);
                        i.putExtra("RaffleID",p.getId());
                        i.putExtra("Rafflename",p.getName());
                        i.putExtra("Raffletype", p.getType());
                        startActivity(i);
                        finish();
                    }
                }
                else{   // others -> normal
                    raffleAdapter RaffleListAdapter =
                            new raffleAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, RaffleTable.selectAll(db));
                    Listdetail.setAdapter(RaffleListAdapter);
                    operation = "normal";
                }
                //Intent i = new Intent(MainActivity.this, Enginepage.class);
                //i.putExtra("Useraction","draw");
                //startActivity(i);
            }
        });

        //4、To get the raffe of user's selection
        Listdetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(operation.equals("select-delete") || operation.equals("delete"))
                {
                    operation = "delete";
                }
                else if(operation.equals("select-edit") || operation.equals("edit"))
                {
                    operation = "edit";
                }
                else if(operation.equals("select-draw") || operation.equals("draw"))
                {
                    operation = "draw";
                    RaffleDetails p = (RaffleDetails)Listdetail.getItemAtPosition(position);
                    if(p.getWinner() != 0)
                    {
                        String text = "No! This raffle has been drawn!";
                        toast.cancel();
                        toast = Toast.makeText(getApplicationContext(), text,1);
                        toast.show();
                    }
                }
                else{
                    operation = "normal";
                    RaffleDetails p = (RaffleDetails)Listdetail.getItemAtPosition(position);

                    //junping to show ticket list page
                    Log.d("Clicked", "Item Clicked: "+ position);

                    Intent i = new Intent(MainActivity.this, ticketlist.class);
                    i.putExtra("RaffleID",p.getId());
                    //i.putExtra("Raffleprice",p.getPrice());
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
