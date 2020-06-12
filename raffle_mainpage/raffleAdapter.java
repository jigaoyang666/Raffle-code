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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class raffleAdapter extends ArrayAdapter<RaffleDetails>
{
    private int mLayoutResourceID;
    public raffleAdapter(Context context, int resource, List<RaffleDetails> objects)
    {
        super(context, resource, objects);
        this.mLayoutResourceID = resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(mLayoutResourceID, parent, false);
        RaffleDetails r = this.getItem(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        String drawtime = dateFormat.format(r.getDrawtime());

        String shown = "Raffle:"+r.getName() + "\t\tType:" + r.getType()+"\r\nDraw time:"+drawtime;
        if(r.getWinner() != 0){
            shown = shown + "\t\tWinner:"+r.getWinner();
        }
        TextView textView = row.findViewById(android.R.id.text1);
        textView.setText(shown);
        return row;
    }
}
