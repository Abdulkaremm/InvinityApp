package com.example.invinityapp.inventoryitems;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InfinityDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "invinity.db";// اسم قاعدة البيانات
    private static final String TABLE_NAME    = "categories";// اسم جدول الاصناف الخاص بالجرد
    private static final String TABLE_SETTING    = "setting";// اسم جدول الاعدادات
    private static final String DATA_PRODUCTS    = "Data_Products";// اسم جدول الاصناف
    private static final String DATA_PRODUCTS_UOMS    = "Data_Products_UOMS";// اسم جدول الاصناف
    private static final String DATA_PRODUCT_BARCODES    = "Data_Product_Barcodes";// اسم جدول الباركود
    private final String ALL_UNITS = "all_units"; //   جدول لكل الوحدات المستلمة من المنضومة عند المزامنة
    private final String SUPPLIERS_TABLE = "suppliers_table"; // جدول للموردين من المنضومة الرئيسية
    private final String RECEIVE_FROM_SUPPLIERS = "receive_from_suppliers";//
    private final String RECEIVED_GOODS = "receives_goods";// جدول البضائع المستلمة
    private final String BRANCH = "branch";
    private final String BRANCH_LOCATION = "branch_location";
    private final String TRANSFER_DOCUMENT = "transfer_document";
    private final String DOCUMENT_PRODUCT = "document_product";
    private final String PURCHASE_ORDERS = "purchase_orders";
    private final String CLIENTS_TABLE = "clients";
    private final String PURCHASE_BILLS = "bills";
    private final String BILL_PRODUCTS = "bill_products";

    public InfinityDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //جدول الاصناف  ###########################
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + " (" +
                        " cat_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Product_ID_PK INTEGER NOT NULL ,"+
                        " ProductName VARCHAR(200) NOT NULL ,"+
                        " UOMName VARCHAR(200),"+
                        " UOMID_PK VAECHAR(200),"+
                        " BaseUnitQ VARCHAR(200) NOT NULL ,"+
                        " barcode VARCHAR(20) NOT NULL," +
                        " quantity INTEGER NOT NULL," +
                        " endDate DATE," +
                        " dateTime VARCHAR(200) NOT NULL," +
                        " deviceIP VARCHAR(15) NOT NULL," +
                        " userName VARCHAR(255))");


        //جدول الاعدادات ##############################
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SETTING + " (" +
                " mainSetting VARCHAR(50) NOT NULL," +
                " ifHasDate INTEGER NOT NULL DEFAULT 1," +
                " Price1 VARCHAR(50) NOT NULL DEFAULT 'السعر الاول'," +
                " Price2 VARCHAR(50) NOT NULL DEFAULT 'السعر الثاني'," +
                " Price3 VARCHAR(50) NOT NULL DEFAULT 'السعر الثالث'," +
                " Price4 VARCHAR(50) NOT NULL DEFAULT 'السعر الرابع'," +
                " QuickInvetory INTEGER NOT NULL,"+
                " API_IP VARCHAR(50) NOT NULL,"+
                " deviceID VARCHAR(20),"+
                " PIN VARCHAR(100))");


        //جدول كل الاصناف التي سيتم مزامنتها مع المنضومة الرائيسية ####################
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_PRODUCTS + " ( " +
                " Product_ID_PK INTEGER NOT NULL  PRIMARY KEY, " +
                " Product_Code VARCHAR(200) NOT NULL," +
                " Product_Name VARCHAR(200) NOT NULL, "+
                " invSub_Department VARCHAR(200) NOT NULL," +
                " Product_Group VARCHAR(200) NOT NULL,"+
                " TrademarkDescrption VARCHAR(200) NOT NULL,"+
                " ProductTypeID_FK VARCHAR(200) NOT NULL,"+
                " StockOnHand VARCHAR(200) NOT NULL,"+
                " Modified_Date VARCHAR(200)  NOT NULL, "+
                " Create_Date VARCHAR(200) NOT NULL,"+
                " ProductType VARCHAR(200) NOT NULL, "+
                " Last_Sync_Date DATE  )");

        // جدول الوحدات من المنضومة الرئيسية ################################

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_PRODUCTS_UOMS + " ( " +
                " ProductUOMID_PK INTEGER NOT NULL PRIMARY KEY," +
                " ProductID_FK INTEGER NOT NULL,"+
                " UOMID_PK INTEGER NOT NULL,"+
                " UOM_NAME VARCHAR(200) NOT NULL,"+
                " BaseUnit VARCHAR(200) NOT NULL, " +
                " UomCost VARCHAR(200) NOT NULL, " +
                " UomLast VARCHAR(200) NOT NULL, "+
                " UomPrice1 VARCHAR(200) NOT NULL, " +
                " UomPrice2 VARCHAR(200) NOT NULL, " +
                " UomPrice3 VARCHAR(200) NOT NULL, " +
                " UomPrice4 VARCHAR(200) NOT NULL, " +
                " Last_Sync_Date DATE, "+
                " FOREIGN KEY(ProductID_FK) REFERENCES " + DATA_PRODUCTS +"(Product_ID_PK) )");



        ///جدول الباركودات الخاصة بالوحدة##################

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_PRODUCT_BARCODES + " ( " +
                " ProductBarcodeID_PK INTEGER NOT NULL PRIMARY KEY, " +
                " ProductUOMID_FK INTEGER NOT NULL,"+
                " ProductBarcode VARCHAR(200) NOT NULL," +
                " Last_Sync_Date DATE,"+
                " FOREIGN KEY(ProductUOMID_FK) REFERENCES "+ DATA_PRODUCTS_UOMS+"(ProductUOMID_PK) )");


        ////////////////************************* جدوال  لقسم استلام البضائع********************

        // ############## جدول الوحدات التي تم استرادها

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ALL_UNITS + " ( " +
                " ProductUOMID_PK INTEGER NOT NULL PRIMARY KEY, " +
                " UOM_NAME VARCHAR(200) NOT NULL," +
                " Last_Sync_Date DATE)");

        //******* جدول الموردين من المنضومة الرئيسية *******

        db.execSQL("CREATE TABLE IF NOT EXISTS " + SUPPLIERS_TABLE + " ( " +
                " SupplierID_PK INTEGER NOT NULL PRIMARY KEY, " +
                " Supplier_Name VARCHAR(200) NOT NULL," +
                " Last_Sync_Date DATE)");


        //***** جدول الموردين المستلم منهم البضائع ******
        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECEIVE_FROM_SUPPLIERS + " ( " +
                " Supplier_PK INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " SupplierID_FK INTEGER , " +
                " Supplier_Name VARCHAR(200) NOT NULL," +
                " Bill_Number INTEGER , " +
                " Receive_Date DATE)");

        //********** جدول البضائع المستلمة ******

        db.execSQL("CREATE TABLE IF NOT EXISTS " + RECEIVED_GOODS + " ( " +
                " Product_PK INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " ProductID_PK INTEGER , " +
                " Supplier_FK INTEGER NOT NULL, " +
                " ProductUOMID_FK INTEGER NOT NULL,"+ // رقم الوحدة من وحدات الصنف او من جدول الوحدات اذا غير موجود
                " Product_Name VARCHAR(200) NOT NULL," +
                " Quantity INTEGER NOT NULL," +
                " EndDate DATE," +
                " CountingDate DATETIME," +
                " BaseUnitQ VARCHAR(200) NOT NULL ,"+
                " ProductBarcode VARCHAR(200) NOT NULL," +
                " IsNew VARCHAR(20) NOT NULL," +
                " FOREIGN KEY(Supplier_FK) REFERENCES "+ RECEIVE_FROM_SUPPLIERS+"(Supplier_PK) ON DELETE CASCADE ON UPDATE CASCADE )");


        //جدول الفروع
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BRANCH + " ( " +
                " BranchID_PK INTEGER NOT NULL PRIMARY KEY , " +
                " BranchName VARCHAR(200) NOT NULL , " +
                " IsCurrentBranch INTEGER NOT NULL)");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + BRANCH_LOCATION + " ( " +
                " LocationID_PK INTEGER NOT NULL PRIMARY KEY, " +
                " LocationName VARCHAR(200) NOT NULL," +
                " BranchID_FK INTEGER NOT NULL," +
                "FOREIGN KEY(BranchID_FK) REFERENCES "+ BRANCH+"(BranchID_PK) ON DELETE CASCADE ON UPDATE CASCADE )");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + TRANSFER_DOCUMENT + " ( " +
                " DocumentID_PK INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " CreatDate VARCHAR(200) NOT NULL," +
                " IsExpiredDate INTEGER NOT NULL, " +
                " Discription VARCHAR(255) NOT NULL, " +
                " FromBranchName VARCHAR(255) NOT NULL, " +
                " FromBranchID INTEGER NOT NULL, " +
                " FromLocationName VARCHAR(255) NOT NULL, " +
                " FromLocationID INTEGER NOT NULL, " +
                " ToBranchName VARCHAR(255) NOT NULL, " +
                " ToBranchID INTEGER NOT NULL, " +
                " ToLocationName VARCHAR(255) NOT NULL, " +
                " ToLocationID INTEGER NOT NULL)");





        db.execSQL("CREATE TABLE IF NOT EXISTS  " + DOCUMENT_PRODUCT + " (" +
                        " DocProductID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Product_ID_PK INTEGER NOT NULL ,"+
                        " ProductName VARCHAR(200) NOT NULL ,"+
                        " UOMName VARCHAR(200),"+
                        " UOMID_PK VAECHAR(200),"+
                        " BaseUnitQ VARCHAR(200) NOT NULL ,"+
                        " barcode VARCHAR(20) NOT NULL," +
                        " quantity INTEGER NOT NULL," +
                        " endDate DATE," +
                        " dateTime VARCHAR(200) NOT NULL," +
                        " deviceIP VARCHAR(15) NOT NULL," +
                        " DocumentID_FK INTEGER NOT NULL," +
                        " userName VARCHAR(255),"+
                        "FOREIGN KEY(DocumentID_FK) REFERENCES "+ TRANSFER_DOCUMENT+"(DocumentID_PK) ON DELETE CASCADE ON UPDATE CASCADE )");



        db.execSQL("CREATE TABLE IF NOT EXISTS  " + PURCHASE_ORDERS + " (" +
                " purchaseID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Product_ID_PK INTEGER NOT NULL ,"+
                " ProductName VARCHAR(200) NOT NULL ,"+
                " UOMName VARCHAR(200),"+
                " UOMID_PK VAECHAR(200),"+
                " BaseUnitQ VARCHAR(200) NOT NULL ,"+
                " barcode VARCHAR(20) NOT NULL," +
                " quantity INTEGER NOT NULL," +
                " discription VARCHAR(200) ," +
                " dateTime VARCHAR(200) NOT NULL," +
                " deviceIP VARCHAR(15) NOT NULL," +
                " userName VARCHAR(255))");


        /////***********************  bills dep tables **********************************************\\\\\\\\\\\\


        db.execSQL("CREATE TABLE IF NOT EXISTS " + CLIENTS_TABLE + " ( " +
                " ClientID_PK INTEGER NOT NULL PRIMARY KEY, " +
                " Client_Name VARCHAR(200) NOT NULL," +
                " Last_Sync_Date DATE)");


        //***** جدول الموردين المستلم منهم البضائع ******
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PURCHASE_BILLS + " ( " +
                " Purchase_PK INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " ClientID_FK INTEGER NOT NULL, " +
                " Client_Name VARCHAR(200) NOT NULL," +
                " CreateDate DATETIME)");

        //********** جدول البضائع المستلمة ******

        db.execSQL("CREATE TABLE IF NOT EXISTS " + BILL_PRODUCTS + " ( " +
                " ProductID_PK INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " ProductID INTEGER NOT NULL, " +
                " Purchase_FK INTEGER NOT NULL, " +
                " ProductUOMID_FK INTEGER NOT NULL,"+ // رقم الوحدة من وحدات الصنف او من جدول الوحدات اذا غير موجود
                " Product_Name VARCHAR(200) NOT NULL," +
                " Quantity INTEGER NOT NULL," +
                " CountingDate DATETIME," +
                " BaseUnitQ VARCHAR(200) NOT NULL ,"+
                " ProductBarcode VARCHAR(200) NOT NULL," +
                " FOREIGN KEY(Purchase_FK) REFERENCES "+ PURCHASE_BILLS+"(Purchase_PK) ON DELETE CASCADE ON UPDATE CASCADE )");


        //*************************************************************\\\\





        ContentValues values = new ContentValues();

        values.put("mainSetting", "mainSetting");
        values.put("ifHasDate", 1); // 1 support date ***** 0 not support date
        values.put("Price1", "السعر الاول");
        values.put("Price2", "السعر الثاني");
        values.put("Price3", "السعر الثالث");
        values.put("Price4", "السعر الرابع");
        values.put("QuickInvetory", 0);
        values.put("API_IP", "000.000.0.0");
        values.put("deviceID", "");
        values.put("PIN", "asarya");

        db.insert(TABLE_SETTING,null, values);

    }



    public void DAELETE_ALL_DATA() {

        SQLiteDatabase db  = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.delete(TABLE_SETTING, null, null);
        db.delete(DATA_PRODUCTS, null, null);
        db.delete(DATA_PRODUCTS_UOMS, null, null);
        db.delete(DATA_PRODUCT_BARCODES, null, null);
        db.delete(ALL_UNITS, null, null);
        db.delete(SUPPLIERS_TABLE, null, null);
        db.delete(RECEIVE_FROM_SUPPLIERS, null, null);
        db.delete(BRANCH, null, null);
        db.delete(BRANCH_LOCATION, null, null);
        db.delete(TRANSFER_DOCUMENT, null, null);
        db.delete(DOCUMENT_PRODUCT, null, null);
        onCreate(db);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
        onCreate(db);

    }

    public void DropAllData(){

        SQLiteDatabase db  = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }


        //تعديل على الاصناف اذا كانت موجودة
    public int IfProdectExist(String id, int where){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor data;

        if (where == 1){

            data = db.rawQuery("SELECT * FROM " + DATA_PRODUCTS + " WHERE Product_ID_PK = ?", new String[]{id});

            if (data.getCount() == 0 ){
                data.close();
                return 0;
            }

        }else if(where == 2){

            data = db.rawQuery("SELECT * FROM " + DATA_PRODUCTS_UOMS + " WHERE ProductUOMID_PK = ?", new String[]{id});

            if (data.getCount() == 0 ){
                data.close();
                return 0;
            }

        }else{

            data = db.rawQuery("SELECT * FROM " + DATA_PRODUCT_BARCODES + " WHERE ProductBarcodeID_PK = ?", new String[]{id});


            if (data.getCount() == 0 ){
                data.close();
                return 0;
            }
        }


        data.close();
        return 1;
    }


    //أضافة صنف قادم من المنضومة الرئيسية

    public void InsertDATA_PRODUCTS(ContentValues table) {
        SQLiteDatabase db = this.getWritableDatabase();

        int res = IfProdectExist(table.get("Product_ID_PK").toString(), 1);

        if(res == 0){

            db.insert(DATA_PRODUCTS,null, table);

        }else{

            db.update(DATA_PRODUCTS, table, "Product_ID_PK = ?", new String[]{table.get("Product_ID_PK").toString()});

       }

    }





    //اضافة وحدة قادمة من المنضومة الرئيسية####

    public void InsertDATA_PRODUCTS_UOMS(ContentValues table) {
        SQLiteDatabase db = this.getWritableDatabase();


        int res = IfProdectExist(table.get("ProductUOMID_PK").toString(), 2);

        if(res == 0){

            db.insert(DATA_PRODUCTS_UOMS,null, table);

        }else{

            db.update(DATA_PRODUCTS_UOMS, table, "ProductUOMID_PK = ?", new String[]{table.get("ProductUOMID_PK").toString()});

        }

    }

    //### اضافة باركود قادم من المنضومة الرئيسية###
    public void InsertDATA_PRODUCT_BARCODES(ContentValues table ) {
        SQLiteDatabase db = this.getWritableDatabase();

         int res = IfProdectExist(table.get("ProductBarcodeID_PK").toString(), 3);

         if(res == 0){

        db.insert(DATA_PRODUCT_BARCODES,null, table);

         }else{

            db.update(DATA_PRODUCT_BARCODES, table, "ProductBarcodeID_PK = ?", new String[]{table.get("ProductBarcodeID_PK").toString()});

        }

    }



        //### اضافة صنف جديد خاص بالجرد$#######
    public boolean insertData(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(TABLE_NAME,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }


    // تحديث بيانات الجرد ##############

    public int updateData(ContentValues values){

        SQLiteDatabase db = this.getWritableDatabase();


        return db.update(TABLE_NAME, values, "cat_id = ?", new String[]{values.get("cat_id").toString()});
    }


    // لعرض كل البيانات من جدول الاصناف الخاص بالجرد
    public Cursor selectData(){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY dateTime DESC LIMIT 30", null);


        return result;
    }


    // لعرض كل البيانات من جدول الاصناف الخاص بالجرد
    public Cursor SearchProduct(String input){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ProductName LIKE '%"+ input +"%' OR barcode LIKE '%"+ input +"%' ORDER BY dateTime DESC LIMIT 20", null);


        return result;
    }

    // لعرض كل البيانات من جدول الاصناف الخاص بالجرد
    public Cursor selectDataByID(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE cat_id = ?", new  String[]{id});


        return result;
    }


    //###### لمزامنة كل الاصناف مع المنضومة الرئيسية
    public Cursor syncAllData(){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY cat_id DESC", null);


        return result;
    }

    // ### لاستراد الاعدادات من المنضومة الرئيسية###
    public Cursor ExbordSetting(){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_SETTING + " WHERE mainSetting = ?", new String[]{"mainSetting"});


        return data;
    }




    //##### لتحديث الاعدادات ########
    public int updateSetting(ContentValues data){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.update(TABLE_SETTING, data, "mainSetting = ?", new String[]{"mainSetting"});
    }


    // ##### لفحص كل البركود موجود او لا
    public int checkInventory(String barcod){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT barcode FROM " + TABLE_NAME + " WHERE barcode = ?", new String[]{barcod});

        return res.getCount();
    }

    // ##### لفحص كل البركود موجود او لا
    public int checkInventoryByID(String Product_ID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT Product_ID_PK FROM " + TABLE_NAME + " WHERE Product_ID_PK = ?", new String[]{Product_ID});

        return res.getCount();
    }



    // ##### لفحص كل البركود موجود او لا
    public Cursor checkBarcode(String barcod){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT cat_id, quantity FROM " + TABLE_NAME + " WHERE barcode = ? LIMIT 1", new String[]{barcod});

        return res;
    }


    //لجلب بيانات الصنف حسب الباركود

    public Cursor getBarcods(String barcod){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT cat_id, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + TABLE_NAME + " WHERE barcode = ?", new String[]{barcod});

        return res;
    }

    public Cursor GetBarcodeByProductID(String Product_ID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT barcode FROM " + TABLE_NAME + " WHERE Product_ID_PK = ?", new String[]{Product_ID});

        return res;
    }

    public Cursor getByProductID(String Product_ID_PK){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT cat_id, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + TABLE_NAME + " WHERE Product_ID_PK = ?", new String[]{Product_ID_PK});

        return res;
    }


    //#### لجلب بيانات الصنف حسب المفتاح الاساسي
    public Cursor getById(String id){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT cat_id, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + TABLE_NAME + " WHERE cat_id = ? ORDER BY dateTime DESC", new String[]{id});

        return res;
    }

    //لحدف بيانات الصنف#################
    public int DeleteInventory(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "cat_id = ?", new String[] {id});

    }

    //لحدف بيانات الصنف#################
    public int DeleteInventoryByProductID(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "Product_ID_PK = ?", new String[] {id});

    }

    //لجلب البيانات اللازمة للجرد
    public Cursor getDataProduct(String barcode){

        SQLiteDatabase db  = this.getWritableDatabase();


        String sqlq = "SELECT t1.Product_ID_PK, t1.Product_Name, t2.UOM_NAME, t2.UOMID_PK, t2.BaseUnit, t1.ProductTypeID_FK FROM Data_Products AS t1 INNER JOIN Data_Products_UOMS AS t2 ON t1.Product_ID_PK = t2.ProductID_FK WHERE t1.Product_ID_PK IN(SELECT t1.ProductID_FK FROM Data_Products_UOMS AS t1  INNER JOIN Data_Product_Barcodes AS t2 ON  t2.ProductUOMID_FK  =  t1.ProductUOMID_PK WHERE t2.ProductBarcode = '"+barcode+"')";

        Cursor res  = db.rawQuery(sqlq,null);

        return res;

    }

    public Cursor getDataUOM(String id){
        SQLiteDatabase db  = this.getWritableDatabase();


        String sqlq = "SELECT t2.UOM_NAME, t2.UOMID_PK FROM "+
                " Data_Products AS t1 INNER JOIN Data_Products_UOMS AS t2 ON t1.Product_ID_PK = t2.ProductID_FK WHERE t1.Product_ID_PK = ?";

        Cursor res  = db.rawQuery(sqlq, new String[]{id});

        return res;
    }

    /// *************** VIEWCATEGORY PACKAGE DB ***********************\\\



    ///// check barcode if exist method



    public Cursor isExistBar(String barcode){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT t1.ProductID_FK, t3.Product_Name, t3.invSub_Department FROM Data_Products_UOMS AS t1 INNER JOIN  Data_Products AS t3 ON t1.ProductID_FK = t3.Product_ID_PK INNER JOIN Data_Product_Barcodes AS t2 ON t1.ProductUOMID_PK = t2.ProductUOMID_FK  WHERE t2.ProductBarcode = ?";
        Cursor res = db.rawQuery(query, new String[]{barcode});

        return res;
    }



    public Cursor GetProdectBybar(int UOMID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT UOM_NAME, UomPrice1, UomPrice2, UomPrice3, UomPrice4 FROM Data_Products_UOMS WHERE ProductID_FK ="+ UOMID ;
        Cursor res = db.rawQuery(query,null);

        return res;
    }



    /// *************** RECEIVING GOODS PACKAGE DB ***********************\\\


    public boolean IfUintsSynced(){
        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT ProductUOMID_PK FROM "+ ALL_UNITS;
        Cursor res = db.rawQuery(query,null);

        if(res.getCount() > 0) {
            res.close();
            return true;
        }else {
            res.close();
            return false;
        }
    }


    public Cursor IfOpenReceiving(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT Supplier_PK ,Supplier_Name ,Receive_Date ,Bill_Number, SupplierID_FK FROM "+ RECEIVE_FROM_SUPPLIERS + " ORDER BY Supplier_PK DESC";

        return db.rawQuery(query,null);
    }

    public Cursor GetGoodsBySupllier(int ID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ RECEIVED_GOODS +" WHERE Supplier_FK ="+ ID;

        return db.rawQuery(query,null);

    }


    public Cursor GetProductIfExist(String barcode, int ID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ RECEIVED_GOODS +" WHERE Supplier_FK ="+ ID +" AND "+ "ProductBarcode = "+ barcode;

        return db.rawQuery(query,null);
    }

    public boolean Openreceiving(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();
        long result = db.insert(RECEIVE_FROM_SUPPLIERS,null, values);

        if(result == -1)
            return false;
        else
            return true;
    }


    public boolean DeleteGoodsAfterSync(){

        SQLiteDatabase db  = this.getWritableDatabase();
        int state = db.delete(RECEIVE_FROM_SUPPLIERS,null,null);

        if(state > 0 ) {
                int del = db.delete(RECEIVED_GOODS, null, null);
                if (del > 0)
                    return true;
                else
                    return false;

        }else
            return false;
    }



    public Cursor GetSuppliers(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT SupplierID_PK ,Supplier_Name FROM "+ SUPPLIERS_TABLE +" ORDER BY Supplier_Name";

        return db.rawQuery(query,null);
    }

    public Cursor FilterSuppliers(String filter){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT SupplierID_PK ,Supplier_Name FROM "+ SUPPLIERS_TABLE + " WHERE Supplier_Name LIKE '%"+filter+"%'";

        return  db.rawQuery(query,null);
    }


    public boolean DeleteOpenReceivie(String id){

        SQLiteDatabase db  = this.getWritableDatabase();
        int state = db.delete(RECEIVE_FROM_SUPPLIERS, "Supplier_PK = ?", new String[] {id});


        if(state > 0 ) {
            Cursor res = db.rawQuery("SELECT * FROM "+ RECEIVED_GOODS +" WHERE Supplier_FK ="+id,null);
            if(res.getCount() > 0) {
                int del = db.delete(RECEIVED_GOODS, "Supplier_FK = ?", new String[]{id});
                if (del > 0)
                    return true;
                else
                    return false;
            }else
                return true;
        }else
            return false;
    }



    public Cursor isbarcodeExist(String barcode){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT t1.ProductID_FK, t3.Product_Name,t3.ProductTypeID_FK FROM Data_Products_UOMS AS t1 INNER JOIN  Data_Products AS t3 ON t1.ProductID_FK = t3.Product_ID_PK INNER JOIN Data_Product_Barcodes AS t2 ON t1.ProductUOMID_PK = t2.ProductUOMID_FK  WHERE t2.ProductBarcode = ?";

        return db.rawQuery(query, new String[]{barcode});
    }

    public Cursor GetBarUnits(int prodectID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT ProductUOMID_PK, UOM_NAME,BaseUnit FROM Data_Products_UOMS WHERE ProductID_FK ="+ prodectID ;

        return db.rawQuery(query,null);

    }

    public Cursor GetAllUnits(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT ProductUOMID_PK,UOM_NAME FROM "+ ALL_UNITS +" ORDER BY UOM_NAME" ;

        return db.rawQuery(query,null);

    }


    public boolean AddGoods(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();
        long result = db.insert(RECEIVED_GOODS,null, values);

        if(result == -1)
            return false;
        else
            return true;
    }


    public  Cursor LastAddedGoods(String SupID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT Product_PK,Product_Name,Quantity,ProductBarcode,IsNew,ProductUOMID_FK FROM receives_goods WHERE Supplier_FK = "+SupID+" ORDER BY Product_PK DESC";

        return db.rawQuery(query,null);
    }




    public Cursor LastGoodUnit(String IsNew,String UOMID){

        SQLiteDatabase db  = this.getWritableDatabase();
        Cursor res;
        String query;
        if(IsNew.compareTo("1") == 0){
            query = "SELECT UOM_NAME FROM "+ALL_UNITS +" WHERE ProductUOMID_PK = "+UOMID;
            res = db.rawQuery(query,null);
        }else{

            query = "SELECT UOM_NAME FROM "+DATA_PRODUCTS_UOMS +" WHERE ProductUOMID_PK = "+UOMID;
            res = db.rawQuery(query,null);
        }

        return res;
    }


    public boolean DeleteAddedProduct(String id){


        SQLiteDatabase db  = this.getWritableDatabase();
        int state =  db.delete(RECEIVED_GOODS, "Product_PK = ?", new String[] {id});

        if(state > 0 ) {

            return true;
        }else
            return false;
    }


    public Cursor getDateToUpdate(String id){


        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT Product_Name,Quantity,ProductBarcode,IsNew,EndDate,ProductID_PK,BaseUnitQ FROM receives_goods WHERE Product_PK ="+ id;

        return db.rawQuery(query,null);

    }


    public boolean updateAddedProduct(ContentValues data, String ID){

        SQLiteDatabase db  = this.getWritableDatabase();

        int res =  db.update(RECEIVED_GOODS, data, "Product_PK = ?", new String[]{ID});

        if(res > 0)
            return true;
        else
            return false;
    }

    public boolean IfSupDate(String id){

        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT ProductTypeID_FK FROM "+DATA_PRODUCTS+" WHERE Product_ID_PK ="+ id;

        Cursor res = db.rawQuery(query,null);
        res.moveToFirst();
        if(Integer.parseInt(res.getString(0)) == 6){
            return true;
        }else
            return false;

    }


    public Cursor getLastSup(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT Supplier_PK FROM "+RECEIVE_FROM_SUPPLIERS+" ORDER BY Supplier_PK  DESC LIMIT 1";
        Cursor res = db.rawQuery(query,null);

        return res ;
    }

    public boolean AddAllUints(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();
        long result = db.insert(ALL_UNITS,null, values);

        if(result == -1)
            return false;
        else
            return true;
    }

    public  boolean AddAllSuppliers(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();
        long result = db.insert(SUPPLIERS_TABLE,null, values);

        if(result == -1)
            return false;
        else
            return true;

    }

    public Cursor checkIfproductsSynced(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT Last_Sync_Date FROM "+DATA_PRODUCTS+" ORDER BY Product_ID_PK  DESC LIMIT 1";
        return db.rawQuery(query,null);
    }

    public int getInvetory(){

        SQLiteDatabase db  = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor res = db.rawQuery(query,null);
        return res.getCount();
    }

    //branches and locations


    public void DELETE_ALL_BARNCHES(){

        SQLiteDatabase db  = this.getWritableDatabase();

        db.delete(BRANCH, null, null);
        db.delete(BRANCH_LOCATION, null, null);

    }


    public void insertBranchs(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

       db.insert(BRANCH,null, values);

    }

    public void insertLocations(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(BRANCH_LOCATION,null, values);

    }


    public Cursor SelectAllBranchs(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT BranchID_PK,  BranchName FROM "+BRANCH;
        Cursor res = db.rawQuery(query,null);

        return res ;
    }




    public Cursor SelectLocationByBranch(String branchID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT LocationID_PK,  LocationName FROM "+BRANCH_LOCATION+" WHERE BranchID_FK = ?";
        Cursor res = db.rawQuery(query, new String[]{branchID});

        return res ;
    }


    public boolean insertDocument(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(TRANSFER_DOCUMENT,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }
    public Cursor SelectAllDocument(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ TRANSFER_DOCUMENT + " ORDER BY DocumentID_PK DESC";
        Cursor res = db.rawQuery(query,null);

        return res ;
    }

    public Cursor SelectDocumentByID(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ TRANSFER_DOCUMENT + " WHERE DocumentID_PK = ?";
        Cursor res = db.rawQuery(query,new String[] {id});

        return res ;
    }

    public Cursor SelectDocumentByToBransh(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ TRANSFER_DOCUMENT + " WHERE ToBranchID = ?";
        Cursor res = db.rawQuery(query,new String[] {id});

        return res ;
    }

    public void DeleteDocument(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        db.delete(TRANSFER_DOCUMENT, "DocumentID_PK = ?", new String[] {id});

    }

    public void DeleteDocumentProduct(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        db.delete(DOCUMENT_PRODUCT, "DocumentID_FK = ?", new String[] {id});

    }


    public int DeleteDocumentProductByID(String id, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.delete(DOCUMENT_PRODUCT, "DocProductID = ? AND DocumentID_FK = ?", new String[] {id, DocumentID});

    }




    public int checkProductByID(String Product_ID, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT * FROM " + DOCUMENT_PRODUCT + " WHERE Product_ID_PK = ? AND DocumentID_FK = ?", new String[]{Product_ID, DocumentID});

        return res.getCount();
    }


    public boolean inserProduToDocument(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(DOCUMENT_PRODUCT,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }

    public Cursor SelectDocumentProduct(String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + DOCUMENT_PRODUCT + " WHERE DocumentID_FK = ? ORDER BY dateTime DESC LIMIT 30", new String[]{DocumentID});


        return result;
    }


    public Cursor SelectAllDocumentProduct(String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + DOCUMENT_PRODUCT + " WHERE DocumentID_FK = ?", new String[]{DocumentID});


        return result;
    }


    public Cursor getBarcodeByProductIDAndDocumentID(String Product_ID, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT barcode FROM " + DOCUMENT_PRODUCT + " WHERE Product_ID_PK = ? AND DocumentID_FK = ?", new String[]{Product_ID, DocumentID});

        return res;
    }


    public Cursor getProductByBarcodeAndDocumentID(String barcod, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT DocProductID, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + DOCUMENT_PRODUCT + " WHERE barcode = ? AND DocumentID_FK = ?", new String[]{barcod, DocumentID});

        return res;
    }

    public int updateDocumentProduct(ContentValues values, String DocumentID){

        SQLiteDatabase db = this.getWritableDatabase();


        return db.update(DOCUMENT_PRODUCT, values, "DocProductID = ? AND DocumentID_FK = ?", new String[]{values.get("DocProductID").toString(), DocumentID});
    }

    public int updateDocument(ContentValues values, String DocumentID){

        SQLiteDatabase db = this.getWritableDatabase();


        return db.update(TRANSFER_DOCUMENT, values, "DocumentID_PK = ?", new String[]{DocumentID});
    }


    public Cursor getDocumentProductByID(String id, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT DocProductID, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + DOCUMENT_PRODUCT + " WHERE DocProductID = ? AND DocumentID_FK = ?", new String[]{id, DocumentID});

        return res;
    }

    public Cursor getDocumentProductByProductID(String id, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT DocProductID, Product_ID_PK, ProductName ,barcode, quantity, endDate  FROM " + DOCUMENT_PRODUCT + " WHERE Product_ID_PK = ? AND DocumentID_FK = ?", new String[]{id, DocumentID});

        return res;
    }

    public int DeleteDocumentProductByProductID(String id, String DocumentID){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.delete(DOCUMENT_PRODUCT, "Product_ID_PK = ? AND DocumentID_FK = ?", new String[] {id, DocumentID});

    }




    public Cursor SelectPurchaseProducts(){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + PURCHASE_ORDERS + " ORDER BY dateTime DESC LIMIT 30", null);


        return result;
    }


    public Cursor getPurchaseProductById(String id){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT purchaseID, Product_ID_PK, ProductName ,barcode, quantity, discription FROM " + PURCHASE_ORDERS + " WHERE purchaseID = ? ORDER BY dateTime DESC", new String[]{id});

        return res;
    }


    public int DeletePurchaseProductByID(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

        return db.delete(PURCHASE_ORDERS, "purchaseID = ?", new String[] {id});

    }



    public int checkPurchaseProductByID(String Product_ID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT Product_ID_PK FROM " + PURCHASE_ORDERS + " WHERE Product_ID_PK = ?", new String[]{Product_ID});

        return res.getCount();
    }


    public Cursor GetBarcodeByPurchaseProductID(String Product_ID){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT barcode FROM " + PURCHASE_ORDERS + " WHERE Product_ID_PK = ?", new String[]{Product_ID});

        return res;
    }


    public Cursor SelectPurchasePriductByBarcode(String barcod){

        SQLiteDatabase db  = this.getWritableDatabase();


        Cursor res = db.rawQuery("SELECT purchaseID, Product_ID_PK, ProductName ,barcode, quantity, discription  FROM " + PURCHASE_ORDERS + " WHERE barcode = ?", new String[]{barcod});

        return res;
    }


    public boolean InsertPurchaseProduct(ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(PURCHASE_ORDERS,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }



    public int updatePurchaseData(ContentValues values){

        SQLiteDatabase db = this.getWritableDatabase();


        return db.update(PURCHASE_ORDERS, values, "purchaseID = ?", new String[]{values.get("purchaseID").toString()});
    }


    public Cursor syncAllPurchaseData(){

        SQLiteDatabase db  = this.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + PURCHASE_ORDERS, null);


        return result;
    }

    public void DropAllPurchaseData(){

        SQLiteDatabase db  = this.getWritableDatabase();
        db.delete(PURCHASE_ORDERS, null, null);
    }





    //****************************** purchase bills methods { ******************************************\\\\\\\\\



        public Cursor GetAllBills(){

            SQLiteDatabase db  = this.getWritableDatabase();

            String query = "SELECT * FROM "+PURCHASE_BILLS+" ORDER BY Purchase_PK  DESC";

            return db.rawQuery(query,null);

        }

        public Cursor GetAllClients(){

            SQLiteDatabase db  = this.getWritableDatabase();

            String query = "SELECT * FROM "+CLIENTS_TABLE+" ORDER BY ClientID_PK  DESC";

            return db.rawQuery(query,null);

        }


    public boolean InsertNewBill(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();

        long result = db.insert(PURCHASE_BILLS,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }

    public boolean DeletePurchaseBill(String id){

        SQLiteDatabase db  = this.getWritableDatabase();

         int state = db.delete(PURCHASE_BILLS, "Purchase_PK = ?", new String[] {id});

         if(state > 0) {
             db.delete(BILL_PRODUCTS, "Purchase_FK = ?", new String[] {id});
             return true;
         }else
             return false;
    }


    public boolean AddNewProduct(ContentValues values){

        SQLiteDatabase db  = this.getWritableDatabase();

        long result = db.insert(BILL_PRODUCTS,null, values);

        if(result == -1){

            return false;

        }else{

            return true;
        }
    }


    public Cursor CheckIfProductExist(String barcode ,int ID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ BILL_PRODUCTS +" WHERE Purchase_FK ="+ ID +" AND "+ "ProductBarcode = "+ barcode;
        return db.rawQuery(query,null);

    }

    public Cursor SelectLastBill(){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT * FROM "+ PURCHASE_BILLS +" ORDER BY Purchase_PK DESC LIMIT 1";
        return db.rawQuery(query,null);
    }



    public  Cursor LastBillProducts(String ID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT ProductID_PK,Product_Name,Quantity,ProductBarcode,ProductUOMID_FK FROM "+BILL_PRODUCTS+" WHERE Purchase_FK = "+ID+" ORDER BY ProductID_PK DESC LIMIT 30";

        return db.rawQuery(query,null);
    }

    public  Cursor SelectProductUOM(String UOMID){

        SQLiteDatabase db  = this.getWritableDatabase();

        String query = "SELECT UOM_NAME FROM "+DATA_PRODUCTS_UOMS +" WHERE ProductUOMID_PK = "+UOMID;
        return db.rawQuery(query,null);
    }

    public boolean DeleteBillProduct(String id){


        SQLiteDatabase db  = this.getWritableDatabase();
        int state =  db.delete(BILL_PRODUCTS, "ProductID_PK = ?", new String[] {id});

        if(state > 0 ) {

            return true;
        }else
            return false;
    }


    //****************************** purchase bills methods } ******************************************\\\\\\\\\



    //#######################

}
