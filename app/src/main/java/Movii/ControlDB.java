package Movii;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Geofrey on 2/3/2016.
 */
public class ControlDB extends DatabaseHandler {
    SQLiteDatabase db;
  private static final String TABLE_USERS="users";

 public  ControlDB(Context context){
super(context);
    }



    public String checkUser(){
       String phone="",user_type="";
        db = this.getWritableDatabase();
         Cursor loginCusor =  db.query(TABLE_USERS, new String[]{"phone_no,user_type"},null,null, null, null, null, null);
        if(loginCusor.moveToFirst()){
          do{
phone=loginCusor.getString(loginCusor.getColumnIndexOrThrow("phone_no"));
 user_type=loginCusor.getString(loginCusor.getColumnIndexOrThrow("user_type"));
          } while(loginCusor.moveToNext());
      }
        if (loginCusor != null && !loginCusor.isClosed()) {
            loginCusor.close();
        }
        db.close();
return phone+"##"+user_type;
}


    public boolean addUser(String phone_no,String username,String user_type){
       db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_no",phone_no);
        values.put("username",username);
        values.put("user_type",user_type);

        boolean addedSuccessful = db.insert(TABLE_USERS, null, values) > 0;
        db.close();
        return addedSuccessful;
    }


    public boolean updateUser(String phone_no,String username){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_no", phone_no);
        values.put("username",username);

String where="phone_no=?";
String[] whereArgs={phone_no};
     boolean editStatus =   db.update(TABLE_USERS, values, where, whereArgs) > 0;
        return editStatus;
    }

}
