package com.example.invinityapp.goods;

public class ProductsModel {

    public  String

            barcode,
            name,
            qun,
            unit,
            id,
            Edate;


    public ProductsModel(String id, String bars, String prname, String qunn, String UOM, String Edate){

        this.barcode = bars;
        this.name = prname;
        this.qun = qunn;
        this.unit = UOM;
        this.id = id;
        this.Edate = Edate;
    }

}
