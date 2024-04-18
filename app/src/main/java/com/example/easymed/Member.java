package com.example.easymed;
public class Member {
    public String Name,ph,Email,Usertype,uid;
    public int wallet;

    public Member(String Name,String ph,String Email,int wallet,String Usertype,String uid)
    {
        this.Name=Name;
        this.ph=ph;
        this.Email=Email;
        this.wallet=wallet;
        this.Usertype=Usertype;
        this.uid=uid;
    }
    Member()
    {}

}
