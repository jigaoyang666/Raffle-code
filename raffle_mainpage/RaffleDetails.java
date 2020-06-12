package au.edu.utas.gaoyangj.raffle_mainpage;

/**
 * Created by dahoo on 02-May-20.
 */

import java.util.Date;
public class RaffleDetails {
    private int id;                // 1
    private String type;           //2
    private String name;
    private String description;
    private int price;
    private String image;
    private int total;
    private int limit;
    private Date creation;
    private Date drawtime;
    private int winner;            //11
    private String tickettable;

    public String getTickettable() {
        return tickettable;
    }

    public void setTickettable(String tickettable) {
        this.tickettable = tickettable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getLimit() {
        return limit;
    }

    public int getPrice() {
        return price;
    }

    public int getTotal() {
        return total;
    }

    public Date getCreation() {
        return creation;
    }

    public int getWinner() {
        return winner;
    }

    public String getDescription() {
        return description;
    }

    public Date getDrawtime() {
        return drawtime;
    }


    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDrawtime(Date drawtime) {
        this.drawtime = drawtime;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}
