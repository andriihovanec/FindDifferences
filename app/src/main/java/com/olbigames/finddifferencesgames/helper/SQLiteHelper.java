package com.olbigames.finddifferencesgames.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import com.olbigames.finddifferencesgames.game.AAAsettings;
import com.olbigames.finddifferencesgames.game.Differences;
import com.olbigames.finddifferencesgames.game.HiddenHintData;
import com.olbigames.finddifferencesgames.game.Levels;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int[] hidden_hints_data = new int[]{
                    1, 515, 344, 15,
                    2, 586, 508, 15,
                    3, 637, 41, 15,
                    4, 558, 138, 15,
                    5, 603, 60, 15,
                    6, 315, 119, 15,
                    7, 340, 367, 15,
                    8, 706, 216, 15,
                    9, 440, 312, 15,
                    10, 443, 490, 15,
                    11, 255, 218, 15,
                    12, 654, 420, 15,
                    13, 151, 465, 15,
                    14, 743, 330, 15,
                    15, 107, 444, 15,
                    16, 418, 486, 15,
                    17, 771, 487, 15,
                    18, 484, 408, 15,
                    19, 582, 272, 15,
                    20, 732, 374, 15,
                    21, 513, 334, 15,
                    22, 735, 386, 15,
                    23, 663, 484, 15,
                    24, 596, 180, 15,
                    25, 102, 340, 15,
                    26, 260, 200, 15,
                    27, 411, 342, 15,
                    28, 621, 66, 15,
                    29, 247, 329, 15,
                    30, 557, 393, 15,
                    31, 117, 422, 15,
                    32, 410, 556, 15,
                    33, 476, 489, 15,
                    34, 656, 367, 15,
                    35, 643, 406, 15,
                    36, 128, 204, 15,
                    37, 405, 290, 15,
                    38, 235, 254, 15,
                    39, 749, 46, 15,
                    40, 768, 184, 15,
                    41, 500, 529, 15,
                    42, 730, 464, 15,
                    43, 127, 320, 15,
                    44, 184, 278, 15,
                    45, 770, 390, 15,
                    46, 690, 390, 15,
                    47, 674, 144, 15,
                    48, 82, 258, 15,
                    49, 764, 229, 15,
                    50, 409, 376, 15,
                    51, 282, 350, 15,
                    52, 740, 387, 15,
                    53, 640, 360, 15,
                    54, 112, 366, 15,
                    55, 565, 314, 15,
                    56, 363, 350, 15,
                    57, 746, 354, 15,
                    58, 417, 336, 15,
                    59, 444, 200, 15,
                    60, 45, 157, 15,
                    61, 668, 145, 15,
                    62, 530, 338, 15,
                    63, 484, 452, 15,
                    64, 687, 354, 15,
                    65, 527, 363, 15,
                    66, 666, 466, 15,
                    67, 306, 347, 15,
                    68, 75, 115, 15,
                    69, 474, 157, 15,
                    70, 191, 402, 15,
                    71, 515, 257, 15,
                    72, 107, 215, 15,
                    73, 609, 237, 15,
                    74, 316, 535, 15,
                    75, 782, 181, 15,
                    76, 400, 446, 15,
                    77, 613, 259, 15,
                    78, 223, 280, 15,
                    79, 122, 219, 15,
                    80, 481, 208, 15,
                    81, 546, 150, 15,
                    82, 702, 523, 15,
                    83, 80, 482, 15,
                    84, 504, 485, 15,
                    85, 26, 173, 15,
                    86, 268, 197, 15,
                    87, 360, 215, 15,
                    88, 285, 347, 15,
                    89, 506, 470, 15,
                    90, 242, 307, 15,
                    91, 315, 138, 15,
                    92, 356, 218, 15,
                    93, 556, 220, 15,
                    94, 720, 440, 15,
                    95, 290, 500, 15,
                    96, 593, 418, 15,
                    97, 740, 236, 15,
                    98, 143, 327, 15,
                    99, 380, 457, 15,
                    100, 744, 243, 15,
                    101, 642, 378, 15,
                    102, 226, 371, 15,
                    103, 625, 277, 15,
                    104, 247, 327, 15,
                    105, 570, 230, 15,
                    106, 123, 521, 15,
                    107, 522, 323, 15,
                    108, 537, 390, 15,
                    109, 391, 494, 15,
                    110, 122, 494, 15,
                    111, 431, 295, 15,
                    112, 710, 280, 15,
                    113, 622, 271, 15,
                    114, 603, 220, 15,
                    115, 549, 274, 15,
                    116, 664, 367, 15,
                    117, 537, 310, 15,
                    118, 466, 284, 15,
                    119, 657, 509, 15,
                    120, 530, 220, 15,
                    121, 479, 349, 15,
                    122, 532, 200, 15,
                    123, 364, 509, 15,
                    124, 57, 357, 15,
                    125, 143, 56, 15,
                    126, 518, 449, 15,
                    127, 131, 200, 15,
                    128, 746, 530, 15,
                    129, 287, 231, 15,
                    130, 678, 153, 15,
                    131, 265, 290, 15,
                    132, 35, 216, 15,
                    133, 424, 299, 15,
                    134, 677, 533, 15,
                    135, 32, 344, 15,
                    136, 118, 301, 15,
                    137, 106, 366, 15,
                    138, 469, 346, 15,
                    139, 770, 549, 15,
                    140, 390, 554, 15,
                    141, 360, 416, 15,
                    142, 625, 245, 15,
                    143, 558, 322, 15,
                    144, 124, 342, 15,
                    145, 227, 350, 15,
                    146, 471, 369, 15,
                    147, 490, 121, 15,
                    148, 538, 508, 15,
                    149, 756, 180, 15,
                    150, 437, 357, 15,
                    151, 390, 500, 15,
                    152, 115, 322, 15,
                    153, 370, 350, 15,
                    154, 455, 380, 15,
                    155, 253, 305, 15,
                    156, 664, 421, 15,
                    157, 384, 294, 15,
                    158, 190, 195, 15,
                    159, 502, 129, 15,
                    160, 774, 374, 15,
                    161, 575, 117, 15,
                    162, 69, 315, 15,
                    163, 472, 421, 15,
                    164, 99, 351, 15,
                    165, 710, 204, 15,
                    166, 498, 221, 15,
                    167, 276, 350, 15,
                    168, 606, 114, 15,
                    169, 382, 442, 15,
                    170, 741, 205, 15,
                    171, 740, 191, 15,
                    172, 270, 416, 15,
                    173, 516, 275, 15,
                    174, 370, 295, 15,
                    175, 400, 301, 15,
                    176, 652, 94, 15,
                    177, 314, 333, 15,
                    178, 478, 480, 15,
                    179, 109, 296, 15,
                    180, 688, 399, 15,
                    181, 235, 368, 15,
                    182, 728, 392, 15,
                    183, 713, 347, 15,
                    184, 682, 385, 15,
                    185, 495, 322, 15,
                    186, 319, 337, 15,
                    187, 671, 226, 15,
                    188, 400, 302, 15,
                    189, 25, 177, 15,
                    190, 465, 268, 15,
                    191, 401, 559, 15,
                    192, 661, 77, 15,
                    193, 612, 569, 15,
                    194, 189, 266, 15,
                    195, 614, 338, 15,
                    196, 702, 472, 15,
                    197, 749, 544, 15,
                    198, 300, 314, 15,
                    199, 402, 452, 15,
                    200, 628, 292, 15
            };

    // All Static variables
    // Database Version
    private static final int HINT_COUNT = AAAsettings.hintCount;
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "findthedifference";
    private static final String TABLE_LEVELS = "levels";
    private static final String TABLE_SETTINGS = "settings";
    private static final String TABLE_DIFFERENCES = "differences";
    private static final String TABLE_HIDDEN_HINTS = "hiddenhints";

    private static final String KEY_LEVEL = "lvl";
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_R = "r";
    private static final String KEY_FINDED = "finded";
    private static final String KEY_HINT = "hint";
    // Levels Table Columns names
    private static final String KEY_ID = "id";
    // TABLE_SETTINGS Columns names
    private static final String VALUE = "show_rate";


    final String LOG_TAG = "myLogs";

    SQLiteDatabase db;

    TextView textDifs, btnHint;

    public SQLiteHelper(Context context, TextView textDifs0, TextView btnHint0) {
        // конструктор суперкласса
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        textDifs = textDifs0;
        btnHint = btnHint0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_SETTINGS + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + VALUE + " integer" + ");");
        String insert_query = "INSERT INTO " + TABLE_SETTINGS + " VALUES(";
        db.execSQL(insert_query + "'0','1');");
        db.execSQL(insert_query + "'1','2');");
        db.execSQL(insert_query + "'2','0');");
        db.execSQL(insert_query + "'3','10');");

        db.execSQL("create table " + TABLE_DIFFERENCES + " ("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_LEVEL + " integer,"
                + KEY_X + " integer,"
                + KEY_Y + " integer,"
                + KEY_R + " integer,"
                + KEY_FINDED + " integer,"
                + KEY_HINT + " integer" + ");");

        db.execSQL("create table " + TABLE_HIDDEN_HINTS + " ("
                + KEY_LEVEL + " integer,"
                + KEY_X + " integer,"
                + KEY_Y + " integer,"
                + KEY_R + " integer,"
                + KEY_FINDED + " integer" + ");");

        insert_query = "INSERT INTO " + TABLE_DIFFERENCES + " VALUES(";
        int data_len = AAAsettings.differences_data.length - 1;
        int i = 0;
        int id = 0;
        do {
            db.execSQL(insert_query + "'" + id + "','" + AAAsettings.differences_data[i] + "','" + AAAsettings.differences_data[i + 1]
                    + "','" + AAAsettings.differences_data[i + 2] + "','" + AAAsettings.differences_data[i + 3] + "','0'," + HINT_COUNT + ");");
            i += 4;
            id++;
        } while (data_len > i);
        insert_query = "INSERT INTO " + TABLE_HIDDEN_HINTS + " VALUES(";
        data_len = hidden_hints_data.length - 1;
        i = 0;
        do {
            db.execSQL(insert_query + "'" + hidden_hints_data[i] + "','" + hidden_hints_data[i + 1]
                    + "','" + hidden_hints_data[i + 2] + "','" + hidden_hints_data[i + 3] + "','0');");
            i += 4;
        } while (data_len > i);
    }

    // ћетод будет вызван, если изменитс¤ верси¤ базы
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // “ут можно организовать миграцию данных из старой базы в новую
        // или просто "выбросить" таблицу и создать заново
    }

    // Getting All Contacts
    public void getLevel(Levels level, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LEVELS, new String[]{KEY_ID,
                        KEY_LEVEL, KEY_X, KEY_Y, KEY_R, KEY_FINDED}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                level.setID(Integer.parseInt(cursor.getString(0)));
                level.setType(Integer.parseInt(cursor.getString(1)));
                level.setImage(Integer.parseInt(cursor.getString(2)));
            } while (cursor.moveToNext());
        }
    }

    // Getting All Contacts
    public void getDifferences(Differences differences, int lvl) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIFFERENCES, new String[]{KEY_ID,
                        KEY_X, KEY_Y, KEY_R, KEY_FINDED}, KEY_LEVEL + "=?",
                new String[]{String.valueOf(lvl)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                differences.setID(cursor.getInt(0));
                differences.setX(cursor.getInt(1));
                differences.setY(cursor.getInt(2));
                differences.setR(cursor.getInt(3));
                differences.setFinded(cursor.getInt(4));
            } while (cursor.moveToNext());
        }
    }

    public void setDifferences(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FINDED, String.valueOf(1));
        db.update(TABLE_DIFFERENCES, values, KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        Cursor mCursor = db.rawQuery("select " + KEY_LEVEL + " from " + TABLE_DIFFERENCES + " where " + KEY_ID + "='" + String.valueOf(id) + "'", null);
        mCursor.moveToFirst();
        int lvl = mCursor.getInt(0);
        mCursor.close();
        String difCount = this.getDifCount(lvl);

        //Log.e("FFFFF", "textDifs.setText :" + difCount);
        textDifs.setText(difCount + "/10");
    }


    public String getDifCount(int lvl) {
        SQLiteDatabase db = this.getReadableDatabase();
        return String.valueOf(DatabaseUtils.queryNumEntries(db, TABLE_DIFFERENCES,
                KEY_LEVEL + "=? AND " + KEY_FINDED + "=?", new String[]{String.valueOf(lvl), String.valueOf(1)}));
    }

    public void refreshLVL(int lvl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FINDED, "0");
        db.update(TABLE_DIFFERENCES, values, KEY_LEVEL + "=?",
                new String[]{String.valueOf(lvl)});
    }

    public int getHintCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='3'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getInt(0);
        }
        return hintcount;
    }

    public void addHint(int addcount) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = this.getHintCount() + addcount;
        ContentValues values = new ContentValues();
        values.put(VALUE, String.valueOf(count));
        db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                new String[]{"3"});

        btnHint.setText(String.valueOf(count));
    }

    public void subtractOneHint() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = this.getHintCount();
        if (count > 0) {
            count--;
        }
        ContentValues values = new ContentValues();
        values.put(VALUE, String.valueOf(count));
        db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                new String[]{"3"});
    }

    public void setHiddenHintFinded(int lvl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FINDED, "1");
        db.update(TABLE_HIDDEN_HINTS, values, KEY_LEVEL + "=?",
                new String[]{String.valueOf(lvl)});
    }

    public HiddenHintData getHiddenHint(int lvl) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIDDEN_HINTS, new String[]{
                        KEY_X, KEY_Y, KEY_R, KEY_FINDED}, KEY_LEVEL + "=?",
                new String[]{String.valueOf(lvl)}, null, null, null, null);
        HiddenHintData hhd = new HiddenHintData();
        hhd.x = 0.0f;
        hhd.y = 0.0f;
        hhd.r = 0.0f;
        hhd.f = 1.0f;
        if (cursor.moveToFirst()) {
            //hiddenHint = new HiddenHint(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), 0.0f, 0.0f);
            hhd.x = cursor.getInt(0);
            hhd.y = cursor.getInt(1);
            hhd.r = cursor.getInt(2);
            hhd.f = cursor.getInt(3);
        }
        return hhd;
    }

    public boolean showGesturesTip() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='" + 1 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getInt(0);
        }
        if (hintcount <= AAAsettings.GestureTipCount) {
            return true;
        }
        return false;
    }

    public void addCountGesturesTip() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='" + 1 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getInt(0);
            //Log.e("dfgdgfdg", String.valueOf( hintcount));
            ContentValues values = new ContentValues();
            values.put(VALUE, String.valueOf(++hintcount));
            db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                    new String[]{"1"});
        } else {
            String insert_query = "INSERT INTO " + TABLE_SETTINGS + " VALUES(";
            db.execSQL(insert_query + "'1','1');");
        }
    }

    public int getInterstitialLastDiffCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='" + 2 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getInt(0);
        } else {
            hintcount = this.getAllFindedCount();
        }
        mCount.close();
        return hintcount;
    }

    public void resetInterstitialLastDiffCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='" + 2 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = this.getAllFindedCount();
            ContentValues values = new ContentValues();
            values.put(VALUE, String.valueOf(hintcount));
            db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                    new String[]{"2"});
        } else {
            String insert_query = "INSERT INTO " + TABLE_SETTINGS + " VALUES(";
            db.execSQL(insert_query + "'2','1');");
        }
        mCount.close();
    }

    public void dontShowRate() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VALUE, "0");
        db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                new String[]{"0"});
    }

    public int showRate() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select " + VALUE + " from " + TABLE_SETTINGS + " where " + KEY_ID + "='" + 0 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getInt(0);
        }
        return hintcount;
    }

    public void showRateLater() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VALUE, String.valueOf(getAllFindedCount()));
        db.update(TABLE_SETTINGS, values, KEY_ID + "=?",
                new String[]{"0"});
    }

    public int getAllFindedCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select " + KEY_ID + " from " + TABLE_DIFFERENCES + " where " + KEY_FINDED + "='" + 1 + "'", null);
        int hintcount = 0;
        if (mCount.moveToFirst()) {
            hintcount = mCount.getCount();
        }
        return hintcount;
    }
}
