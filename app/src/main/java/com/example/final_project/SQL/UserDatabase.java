package com.example.final_project.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UserDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB.db";
    private static final int DATABASE_VERSION = 27;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_IS_PREMIUM = "isPremium";

    private static final String TABLE_FILES = "uploaded_files";
    private static final String COLUMN_FILE_ID = "file_id";
    private static final String COLUMN_FILENAME = "filename";
    private static final String COLUMN_FILEURI = "fileuri";
    private static final String COLUMN_FILESIZE = "filesize";
    private static final String COLUMN_FILE_EMAIL = "email";
    private static final String COLUMN_FILE_DATE = "publishedDate";
    private static final String COLUMN_FILE_STATUS = "status";
    private static final String COLUMN_FILE_FIREBASE_KEY = "firebaseKey";


    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_ORDER_EMAIL = "email";
    private static final String COLUMN_ORDER_NAME = "order_name";
    private static final String COLUMN_ORDER_PRICE = "order_price";

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANSACTION_ID = "id";
    private static final String COLUMN_TRANSACTION_EMAIL = "email";
    private static final String COLUMN_TRANSACTION_TYPE = "type";
    private static final String COLUMN_TRANSACTION_AMOUNT = "amount";
    private static final String COLUMN_TRANSACTION_DATE = "date";
    private static final String TABLE_COMMENTS = "comments";
    private static final String COLUMN_COMMENT_ID = "comment_id";
    private static final String COLUMN_COMMENT_FILE_ID = "file_id";
    private static final String COLUMN_COMMENT_EMAIL = "email";
    private static final String COLUMN_COMMENT_TEXT = "comment";
    private static final String COLUMN_COMMENT_TIME = "timestamp";

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_IS_PREMIUM + " INTEGER DEFAULT 0)";
        db.execSQL(createUsers);

        String createFiles = "CREATE TABLE " + TABLE_FILES + " (" +
                COLUMN_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILENAME + " TEXT, " +
                COLUMN_FILEURI + " TEXT, " +
                COLUMN_FILESIZE + " INTEGER, " +
                COLUMN_FILE_EMAIL + " TEXT, " +
                COLUMN_FILE_DATE + " TEXT," +
                COLUMN_FILE_STATUS + " TEXT DEFAULT 'pending'," +
                COLUMN_FILE_FIREBASE_KEY + " TEXT" + ")";
        db.execSQL(createFiles);

        String createOrders = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_EMAIL + " TEXT, " +
                COLUMN_ORDER_NAME + " TEXT, " +
                COLUMN_ORDER_PRICE + " REAL)";
        db.execSQL(createOrders);

        String createTransactions = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACTION_EMAIL + " TEXT, " +
                COLUMN_TRANSACTION_TYPE + " TEXT, " +
                COLUMN_TRANSACTION_AMOUNT + " REAL, " +
                COLUMN_TRANSACTION_DATE + " TEXT)";
        db.execSQL(createTransactions);

        db.execSQL("CREATE TABLE " + TABLE_COMMENTS + " (" +
                COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COMMENT_FILE_ID + " INTEGER, " +
                COLUMN_COMMENT_EMAIL + " TEXT, " +
                COLUMN_COMMENT_TEXT + " TEXT, " +
                COLUMN_COMMENT_TIME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

    public boolean insertUser(String fullname, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullname);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_IS_PREMIUM, 0);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public boolean updateFile(int fileId, String filename, String fileuri, int filesize, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILENAME, filename);
        values.put(COLUMN_FILEURI, fileuri);
        values.put(COLUMN_FILESIZE, filesize);
        values.put(COLUMN_FILE_STATUS, status);

        int rows = db.update(TABLE_FILES, values, COLUMN_FILE_ID + "=?", new String[]{String.valueOf(fileId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteFile(int fileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FILES, COLUMN_FILE_ID + "=?", new String[]{String.valueOf(fileId)});
        db.close();
        return rows > 0;
    }

    public Cursor getFileById(int fileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FILES, null, COLUMN_FILE_ID + "=?", new String[]{String.valueOf(fileId)}, null, null, null);
    }


    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rows > 0;
    }

    public boolean updateUser(String email, String fullname, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullname);
        values.put(COLUMN_PHONE, phone);
        int rows = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rows > 0;
    }

    public boolean updatePremiumStatus(String email, int isPremium) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_PREMIUM, isPremium);
        int rows = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rows > 0;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public boolean insertFile(String filename, String fileuri, int filesize, String email, String publishedDate, String firebaseKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILENAME, filename);
        values.put(COLUMN_FILEURI, fileuri);
        values.put(COLUMN_FILESIZE, filesize);
        values.put(COLUMN_FILE_EMAIL, email);
        values.put(COLUMN_FILE_DATE, publishedDate);
        values.put(COLUMN_FILE_STATUS, "pending");
        values.put(COLUMN_FILE_FIREBASE_KEY, firebaseKey);
        long result = db.insert(TABLE_FILES, null, values);
        db.close();
        return result != -1;
    }


    public Cursor getAllFiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FILES, null);
    }

    public void clearAllFiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILES, null, null);
        db.close();
    }

    public String getPasswordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        String password = null;
        if (cursor.moveToFirst()) password = cursor.getString(0);
        cursor.close();
        db.close();
        return password;
    }

    public boolean updatePasswordById(int id, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Cursor getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public boolean updateUserById(int id, String fullname, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullname);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        int rows = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean insertOrder(String email, String orderName, double orderPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_EMAIL, email);
        values.put(COLUMN_ORDER_NAME, orderName);
        values.put(COLUMN_ORDER_PRICE, orderPrice);
        long result = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getOrdersByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ORDERS,
                new String[]{COLUMN_ORDER_ID, COLUMN_ORDER_NAME, COLUMN_ORDER_PRICE},
                COLUMN_ORDER_EMAIL + "=?",
                new String[]{email},
                null, null, COLUMN_ORDER_ID + " DESC");
    }

    public boolean insertTransaction(String email, String type, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (email == null || email.trim().isEmpty()) {
            db.close();
            return false;
        }
        if (type == null || type.trim().isEmpty()) {
            type = "Top Up";
        }
        values.put(COLUMN_TRANSACTION_EMAIL, email);
        values.put(COLUMN_TRANSACTION_TYPE, type);
        values.put(COLUMN_TRANSACTION_AMOUNT, amount);
        values.put(COLUMN_TRANSACTION_DATE, date);
        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getTransactionsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRANSACTIONS,
                new String[]{COLUMN_TRANSACTION_ID, COLUMN_TRANSACTION_TYPE, COLUMN_TRANSACTION_AMOUNT, COLUMN_TRANSACTION_DATE},
                COLUMN_TRANSACTION_EMAIL + "=?",
                new String[]{email},
                null, null, COLUMN_TRANSACTION_DATE + " DESC");
    }

    public double getTotalBalance(String email) {
        double balance = 0;
        if (email == null || email.trim().isEmpty()) {
            return 0;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRANSACTIONS,
                new String[]{COLUMN_TRANSACTION_TYPE, COLUMN_TRANSACTION_AMOUNT},
                COLUMN_TRANSACTION_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String type = cursor.getString(0);
                double amount = cursor.getDouble(1);
                if (type != null) {
                    String t = type.trim().toLowerCase();
                    if (t.equals("top up") || t.equals("deposit") || t.equals("recharge")) {
                        balance += amount;
                    } else if (t.equals("withdraw") || t.equals("purchase") || t.equals("payment")) {
                        balance -= amount;
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return balance;
    }

    public String getEmailById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = null;
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return email;
    }
    public boolean isPremiumUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_IS_PREMIUM},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        boolean isPremium = false;
        if (cursor.moveToFirst()) {
            isPremium = cursor.getInt(0) == 1;
        }
        cursor.close();
        db.close();
        return isPremium;
    }

    public void setPremiumStatus(String email, boolean premium) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_PREMIUM, premium ? 1 : 0);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
    }
    public boolean insertComment(int fileId, String email, String comment, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT_FILE_ID, fileId);
        values.put(COLUMN_COMMENT_EMAIL, email);
        values.put(COLUMN_COMMENT_TEXT, comment);
        values.put(COLUMN_COMMENT_TIME, timestamp);
        long result = db.insert(TABLE_COMMENTS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getCommentsByFileId(int fileId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_COMMENTS,
                new String[]{COLUMN_COMMENT_EMAIL, COLUMN_COMMENT_TEXT, COLUMN_COMMENT_TIME},
                COLUMN_COMMENT_FILE_ID + "=?",
                new String[]{String.valueOf(fileId)},
                null, null,
                COLUMN_COMMENT_ID + " DESC");
    }

}
