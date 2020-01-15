package com.example.invinityapp.inventoryitems;

public class ProductData {

    public int number, Product_id;
    public String name, UOM, barcode, qun, date;

    public ProductData(int num, int Product_id, String name, String UOM, String barcode, String qun, String date){

        this.number = num;
        this.Product_id = Product_id;
        this.name = name;
        this.UOM = UOM;
        this.barcode = barcode;
        this.qun = qun;
        this.date = date;

    }
}
