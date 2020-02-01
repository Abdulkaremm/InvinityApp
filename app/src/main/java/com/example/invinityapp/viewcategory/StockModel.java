package com.example.invinityapp.viewcategory;

public class StockModel {

    public String branch,
            location,
            uom,
            stock,
            exDate,
            lastSync;


    public StockModel(String branch, String location, String uom, String stock, String exDate, String lastSync){

        this.branch   = branch;
        this.location = location;
        this.uom      = uom;
        this.stock    = stock;
        this.exDate   = exDate;
        this.lastSync = lastSync;

    }
}
