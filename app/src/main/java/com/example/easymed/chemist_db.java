package com.example.easymed;

public class chemist_db {
    public String Name,ph,location,id,uid,lat,lon;


    public chemist_db(String Name,String ph,String location,String id,String uid,String lat,String lon)
    {
        this.Name=Name;
        this.ph=ph;
        this.location=location;

        this.id=id;
        this.uid=uid;
        this.lat=lat;
        this.lon=lon;
    }
    chemist_db()
    {}
}
