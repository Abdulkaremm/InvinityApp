package com.example.invinityapp.Transport_of_goods;

public class DocumentData {

    public String
            DocumentID_PK,
            FromBranchName,
            FromLocationName,
            ToBranchName,
            ToLocationName,
            Discription;

    public DocumentData(String id, String fromBname, String fromLname, String toBname, String toLname, String disc){

        this.DocumentID_PK = id;
        this.FromBranchName = fromBname;
        this.FromLocationName = fromLname;
        this.ToBranchName = toBname;
        this.ToLocationName = toLname;
        this.Discription = disc;

    }
}
