package au.edu.utas.gaoyangj.raffle_mainpage;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ticketAdapter extends ArrayAdapter<TicketDetails> {
    private int mLayoutResourceID;

    public ticketAdapter(Context context, int resource, List<TicketDetails> objects) {
        super(context, resource, objects);
        this.mLayoutResourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(mLayoutResourceID, parent, false);
        TicketDetails r = this.getItem(position);
        String text = "Ticket Number: "+ r.getId() + "   " + "                Name: "+r.getName();
        if(r.getWinner() == 1)
        {
            text = text + "\r\nWin!";
        }
        TextView textView = row.findViewById(android.R.id.text1);
        textView.setText(text);
        return row;
    }
}
