package com.example.final_project.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.final_project.Security.CryptoUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UserDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB.db";
    private static final int DATABASE_VERSION = 35;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_IS_PREMIUM = "isPremium"  ;

    private static final String TABLE_FILES = "uploaded_files";
    private static final String COLUMN_FILE_ID = "file_id";
    private static final String COLUMN_FILENAME = "filename";
    private static final String COLUMN_FILEURI = "fileuri";
    private static final String COLUMN_FILESIZE = "filesize";
    private static final String COLUMN_FILE_EMAIL = "email";
    private static final String COLUMN_FILE_DATE = "publishedDate";
    private static final String COLUMN_FILE_STATUS = "status";
    private static final String COLUMN_FILE_FIREBASE_KEY = "firebaseKey";


    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_ORDER_EMAIL = "email";
    public static final String COLUMN_ORDER_NAME = "order_name";
    public static final String COLUMN_ORDER_PRICE = "order_price";

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
                COLUMN_IS_PREMIUM + " INTEGER DEFAULT 0, " +
                "balance REAL DEFAULT 0)";
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
                COLUMN_ORDER_PRICE + " REAL, " +
                "order_status TEXT DEFAULT 'Pending', " +
                "order_date TEXT DEFAULT ''" +
                ")";
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

        String SQL_CREATE_QUIZ_TABLE = "CREATE TABLE Quiz (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "language TEXT," +
                "question TEXT," +
                "code TEXT," +
                "option1 TEXT," +
                "option2 TEXT," +
                "option3 TEXT," +
                "option4 TEXT," +
                "answer TEXT" +
                ")";
        db.execSQL(SQL_CREATE_QUIZ_TABLE);
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Java','What is the output of 1+1 in Java?','', '1','2','11','Error','2')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Python','Which of these is mutable?','', 'tuple','list','string','int','list')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('C++','Which declares a pointer?','', 'int ptr;','int *ptr;','int &ptr;','int ptr&;','int *ptr;')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('JavaScript','Which converts JSON string to object?','', 'JSON.parse()','JSON.stringify()','JSON.toObject()','JSON.objectify()','JSON.parse()')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Java','Keyword to inherit class?','', 'implement','extends','inherits','super','extends')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Python','Keyword to define function?','', 'func','def','function','lambda','def')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('C++','What is the default value of int?','', '0','1','null','undefined','0')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('JavaScript','Type of NaN?','', 'number','NaN','undefined','object','number')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Java','Default boolean value?','', 'true','false','0','null','false')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Python','Which is immutable?','', 'tuple','list','dict','set','tuple')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('C++','Output of: cout << 5/2;','', '2','2.5','3','Error','2')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('JavaScript','Method to add element to array?','', 'push()','pop()','shift()','unshift()','push()')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Java','Which is checked exception?','', 'IOException','NullPointerException','ArithmeticException','ArrayIndexOutOfBoundsException','IOException')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Python','Operator for exponentiation?','', '^','**','%','//','**')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('C++','Which is reference?','', 'int *ptr','int &ref','int ptr','int ref&','int &ref')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('JavaScript','Difference between let and var?','', 'var block scoped','let block scoped','var constant','let global scoped','let block scoped')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Java','Keyword for interface implementation?','', 'implements','extends','interface','super','implements')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('Python','Which function gets length of list?','', 'length()','len()','size()','count()','len()')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('C++','Output: cout << 2*3+1;','', '7','8','5','9','7')");
        db.execSQL("INSERT INTO Quiz(language, question, code, option1, option2, option3, option4, answer) VALUES('JavaScript','How to declare constant?','', 'let','const','var','constant','const')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS Quiz");

        onCreate(db);
    }

    public boolean insertUser(String fullname, String email, String password, String phone) {
        String encName = CryptoUtil.encrypt(fullname);
        String hashedPassword = CryptoUtil.hashPassword(password);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, encName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
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
    public boolean updateOrderStatusAndDate(int orderId, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_status", status);
        values.put("order_date", date);
        int rows = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
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

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE LOWER(email)=?", new String[]{email.toLowerCase()});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }


    public double getBalance(String email) {
        double balance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{"balance"},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        return balance;
    }
    public int insertOrder(String email, String orderName, double orderPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_EMAIL, email);
        values.put(COLUMN_ORDER_NAME, orderName);
        values.put(COLUMN_ORDER_PRICE, orderPrice);
        values.put("order_status", "Pending");
        values.put("order_date", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));

        long id = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return (int) id;
    }

    public boolean updateBalance(String email, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        int rows = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rows > 0;
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




}
