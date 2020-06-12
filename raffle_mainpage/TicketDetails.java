package au.edu.utas.gaoyangj.raffle_mainpage;

import java.util.Date;

/**
 * Created by dahoo on 04-May-20.
 */
public class TicketDetails {
    protected int id;         // in sqlite, first one is 1.
    protected String name;
    protected String mobile;
    protected Date creation;
    protected int winner;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreation() {
        return creation;
    }

    public int getWinner() {
        return winner;
    }

    public String getMobile() {
        return mobile;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }



}

