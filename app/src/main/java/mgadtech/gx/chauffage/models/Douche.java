package mgadtech.gx.chauffage.models;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class Douche implements Serializable, Comparable {
    @Exclude
    private String id;
    private String nom_user;
    private Date start_date;

    public Douche() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Douche guest = (Douche) obj;
        if (guest.getId() == this.getId()) return true ;
        return false;
    }

    public Douche(String nom_user, Date start_date) {
        this.nom_user = nom_user;
        this.start_date = start_date;
    }

    public String getNom_user() {
        return nom_user;
    }

    public void setNom_user(String nom_user) {
        this.nom_user = nom_user;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
