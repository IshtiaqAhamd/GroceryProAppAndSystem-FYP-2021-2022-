package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class DataBaseHelper extends SQLiteOpenHelper {
    private static final String Database_Name = "ITEMS_DB";
    private static final String Table_Name = "ITEMS_TABLE";
    private static final int DB_Version = 1;
    SQLiteDatabase sqLiteDatabase;

    public DataBaseHelper(Context context) {
        super(context, Database_Name,null, DB_Version);
        sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Table_Name + "(Item_Id INTEGER PRIMARY KEY AUTOINCREMENT, Item_PID VARCHAR, Item_Name VARCHAR, Item_Price_Each VARCHAR, Item_Price VARCHAR, Item_Quantity VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }
    public long addToCart(String productID, String title, String priceEach, String price, String quantity)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Item_PID", productID);
        contentValues.put("Item_Name", title);
        contentValues.put("Item_Price_Each", priceEach);
        contentValues.put("Item_Price", price);
        contentValues.put("Item_Quantity", quantity);
        long cartData = sqLiteDatabase.insert(Table_Name,null,contentValues);
        return cartData;
    }
    public Boolean deleteCartItem(String cartItemId) {

        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from " +Table_Name+ " where Item_PID = ?", new String[]{cartItemId});
        if (cursor.getCount() > 0) {
            long result = DB.delete(Table_Name, "Item_PID=?", new String[]{cartItemId});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else return false;
    }

    public Cursor getCartData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Table_Name, null);
        return cursor;
    }

    public Cursor cartCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor count = db.rawQuery("select COUNT(*) from " + Table_Name, null);
        return count;
    }
}