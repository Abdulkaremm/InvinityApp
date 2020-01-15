package com.example.invinityapp.goods;

public class ProductsModel {

    public  String

            barcode,
            name,
            qun,
            unit,
            id;


    public ProductsModel(String id, String bars, String prname, String qunn, String UOM){

        this.barcode = bars;
        this.name = prname;
        this.qun = qunn;
        this.unit = UOM;
        this.id = id;
    }

}
