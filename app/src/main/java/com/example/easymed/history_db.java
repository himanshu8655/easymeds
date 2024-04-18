package com.example.easymed;

public class history_db {
    public String date,amount,UpiID;
    public history_db(String date,String UpiID,String amount)
    {
        this.date=date;
        this.UpiID=UpiID;
        this.amount=amount;
    }
    history_db()
    {}

}
