package com.example.invinityapp.ExportAPurchaseBill;

public class BillsModel {

    public  String
            id,
            name,
            date;

    public BillsModel(String ID, String Name,String CreateDate){

        this.id = ID;
        this.name = Name;
        this.date = CreateDate;
    }
}
