package mgadtech.gx.chauffage.models;


import java.io.Serializable;
import java.util.Date;

public class Temperature implements Serializable {
    private float tempE;
    private float tempS;
    private float tempV;
    private Date date;
    private int timestamp;

    public Temperature(float tempE, float tempS, float tempV, Date date, int timestamp) {
        this.tempE = tempE;
        this.tempS = tempS;
        this.tempV = tempV;
        this.date = date;
        this.timestamp = timestamp;
    }

    public float getTempV() {
        return tempV;
    }

    public void setTempV(float tempV) {
        this.tempV = tempV;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTempE() {
        return tempE;
    }

    public void setTempE(float tempE) {
        this.tempE = tempE;
    }

    public float getTempS() {
        return tempS;
    }

    public void setTempS(float tempS) {
        this.tempS = tempS;
    }
}
