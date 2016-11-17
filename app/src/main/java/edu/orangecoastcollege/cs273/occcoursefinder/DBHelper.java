package edu.orangecoastcollege.cs273.occcoursefinder;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

class DBHelper extends SQLiteOpenHelper {

    private Context mContext;

    //TASK: DEFINE THE DATABASE VERSION AND NAME  (DATABASE CONTAINS MULTIPLE TABLES)
    static final String DATABASE_NAME = "OCC";
    private static final int DATABASE_VERSION = 1;

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE COURSES TABLE
    private static final String COURSES_TABLE = "Courses";
    private static final String COURSES_KEY_FIELD_ID = "_id";
    private static final String FIELD_ALPHA = "alpha";
    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_TITLE = "title";

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE INSTRUCTORS TABLE
    private static final String INSTRUCTORS_TABLE = "Instructors";
    private static final String INSTRUCTORS_KEY_FIELD_ID = "_id";
    static final String FIELD_FIRST_NAME = "first_name";
    static final String FIELD_LAST_NAME = "last_name";
    private static final String FIELD_EMAIL = "email";

    //TASK: DEFINE THE FIELDS (COLUMN NAMES) FOR THE OFFERINGS TABLE
    private static final String OFFERINGS_TABLE = "Offerings";
    private static final String OFFERINGS_KEY_FIELD_ID = "crn";
    private static final String FIELD_SEMESTER_CODE = "semester_code";
    private static final String FIELD_COURSE_ID = "course_id";
    private static final String FIELD_INSTRUCTOR_ID = "instructor_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createQuery = "CREATE TABLE " + COURSES_TABLE + "("
                + COURSES_KEY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_ALPHA + " TEXT, "
                + FIELD_NUMBER + " TEXT, "
                + FIELD_TITLE + " TEXT" + ")";
        database.execSQL(createQuery);

        createQuery = "CREATE TABLE " + INSTRUCTORS_TABLE + "("
                + INSTRUCTORS_KEY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_FIRST_NAME + " TEXT, "
                + FIELD_LAST_NAME + " TEXT, "
                + FIELD_EMAIL + " TEXT" + ")";
        database.execSQL(createQuery);

        createQuery = "CREATE TABLE " + OFFERINGS_TABLE + "("
                + OFFERINGS_KEY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FIELD_SEMESTER_CODE + " INTEGER, "
                + FIELD_COURSE_ID + " INTEGER, "
                + FIELD_INSTRUCTOR_ID + " INTEGER, "
                + "FOREIGN KEY(" + FIELD_COURSE_ID + ") REFERENCES "
                + COURSES_TABLE + "(" + COURSES_KEY_FIELD_ID + "), "
                + "FOREIGN KEY(" + FIELD_INSTRUCTOR_ID + ") REFERENCES "
                + INSTRUCTORS_TABLE + "(" + INSTRUCTORS_KEY_FIELD_ID + ")" +
                ")";
        database.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int oldVersion,
                          int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + INSTRUCTORS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + OFFERINGS_TABLE);
        onCreate(database);
    }

    //********** COURSE TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

    public void addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ALPHA, course.getAlpha());
        values.put(FIELD_NUMBER, course.getNumber());
        values.put(FIELD_TITLE, course.getTitle());

        db.insert(COURSES_TABLE, null, values);

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> coursesList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(queryList, null);
        Cursor cursor = database.query(
                COURSES_TABLE,
                new String[]{COURSES_KEY_FIELD_ID, FIELD_ALPHA, FIELD_NUMBER, FIELD_TITLE},
                null,
                null,
                null, null, null, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()) {
            do {
                Course course =
                        new Course(cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));
                coursesList.add(course);
            } while (cursor.moveToNext());
        }
        return coursesList;
    }

    public void deleteCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(COURSES_TABLE, COURSES_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
    }

    public void deleteAllCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COURSES_TABLE, null, null);
        db.close();
    }

    public void updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ALPHA, course.getAlpha());
        values.put(FIELD_NUMBER, course.getNumber());
        values.put(FIELD_TITLE, course.getTitle());

        db.update(COURSES_TABLE, values, COURSES_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(course.getId())});
        db.close();
    }

    public Course getCourse(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                COURSES_TABLE,
                new String[]{COURSES_KEY_FIELD_ID, FIELD_ALPHA, FIELD_NUMBER, FIELD_TITLE},
                COURSES_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Course course = new Course(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

        db.close();
        return course;
    }


    //********** INSTRUCTOR TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

    public void addInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_LAST_NAME, instructor.getLastName());
        values.put(FIELD_FIRST_NAME, instructor.getFirstName());
        values.put(FIELD_EMAIL, instructor.getEmail());

        db.insert(INSTRUCTORS_TABLE, null, values);

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    public ArrayList<Instructor> getAllInstructors() {
        ArrayList<Instructor> instructorsList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                INSTRUCTORS_TABLE,
                new String[]{INSTRUCTORS_KEY_FIELD_ID, FIELD_LAST_NAME, FIELD_FIRST_NAME, FIELD_EMAIL},
                null,
                null,
                null, null, null, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()) {
            do {
                Instructor instructor =
                        new Instructor(cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3));
                instructorsList.add(instructor);
            } while (cursor.moveToNext());
        }
        return instructorsList;
    }

    public void deleteInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(INSTRUCTORS_TABLE, INSTRUCTORS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(instructor.getId())});
        db.close();
    }

    public void deleteAllInstructors() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(INSTRUCTORS_TABLE, null, null);
        db.close();
    }

    public void updateInstructor(Instructor instructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_FIRST_NAME, instructor.getFirstName());
        values.put(FIELD_LAST_NAME, instructor.getLastName());
        values.put(FIELD_EMAIL, instructor.getEmail());

        db.update(INSTRUCTORS_TABLE, values, INSTRUCTORS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(instructor.getId())});
        db.close();
    }

    public Instructor getInstructor(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                INSTRUCTORS_TABLE,
                new String[]{INSTRUCTORS_KEY_FIELD_ID, FIELD_LAST_NAME, FIELD_FIRST_NAME, FIELD_EMAIL},
                INSTRUCTORS_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Instructor instructor = new Instructor(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

        db.close();
        return instructor;
    }


    //********** OFFERING TABLE OPERATIONS:  ADD, GETALL, EDIT, DELETE

    public void addOffering(int crn, int semesterCode, int courseId, int instructorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(OFFERINGS_KEY_FIELD_ID, crn);
        values.put(FIELD_SEMESTER_CODE, semesterCode);
        values.put(FIELD_COURSE_ID, courseId);
        values.put(FIELD_INSTRUCTOR_ID, instructorId);

        db.insert(OFFERINGS_TABLE, null, values);

        // CLOSE THE DATABASE CONNECTION
        db.close();
    }

    public ArrayList<Offering> getAllOfferings() {
        ArrayList<Offering> offeringsList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        //Cursor cursor = database.rawQuery(queryList, null);
        Cursor cursor = database.query(
                OFFERINGS_TABLE,
                new String[]{OFFERINGS_KEY_FIELD_ID, FIELD_SEMESTER_CODE, FIELD_COURSE_ID, FIELD_INSTRUCTOR_ID},
                null,
                null,
                null, null, null, null);

        //COLLECT EACH ROW IN THE TABLE
        if (cursor.moveToFirst()) {
            do {
                Course course = getCourse(cursor.getInt(2));
                Instructor instructor = getInstructor(cursor.getInt(3));
                Offering offering = new Offering(cursor.getInt(0),
                        cursor.getInt(1), course, instructor);

                offeringsList.add(offering);
            } while (cursor.moveToNext());
        }
        return offeringsList;
    }

    public void deleteOffering(Offering offering) {
        SQLiteDatabase db = this.getWritableDatabase();

        // DELETE THE TABLE ROW
        db.delete(OFFERINGS_TABLE, OFFERINGS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(offering.getCRN())});
        db.close();
    }

    public void deleteAllOfferings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFERINGS_TABLE, null, null);
        db.close();
    }

    public void updateOffering(Offering offering) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_SEMESTER_CODE, offering.getSemesterCode());
        values.put(FIELD_COURSE_ID, offering.getCourse().getId());
        values.put(FIELD_INSTRUCTOR_ID, offering.getInstructor().getId());

        db.update(OFFERINGS_TABLE, values, OFFERINGS_KEY_FIELD_ID + " = ?",
                new String[]{String.valueOf(offering.getCRN())});
        db.close();
    }

    public Offering getOffering(int crn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                OFFERINGS_TABLE,
                new String[]{OFFERINGS_KEY_FIELD_ID, FIELD_SEMESTER_CODE, FIELD_COURSE_ID, FIELD_INSTRUCTOR_ID},
                OFFERINGS_KEY_FIELD_ID + "=?",
                new String[]{String.valueOf(crn)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Course course = getCourse(cursor.getInt(2));
        Instructor instructor = getInstructor(cursor.getInt(3));
        Offering offering = new Offering(cursor.getInt(0),
                cursor.getInt(1), course, instructor);


        db.close();
        return offering;
    }



    public Cursor getInstructorNamesCursor() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                INSTRUCTORS_TABLE,
                new String[]{INSTRUCTORS_KEY_FIELD_ID, FIELD_LAST_NAME, FIELD_FIRST_NAME},
                null,
                null,
                null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public boolean importCoursesFromCSV(String csvFileName) {
        AssetManager manager = mContext.getAssets();
        InputStream inStream;
        try {
            inStream = manager.open(csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int id = Integer.parseInt(fields[0].trim());
                String alpha = fields[1].trim();
                String number = fields[2].trim();
                String title = fields[3].trim();
                addCourse(new Course(id, alpha, number, title));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean importInstructorsFromCSV(String csvFileName) {
        AssetManager am = mContext.getAssets();
        InputStream inStream = null;
        try {
            inStream = am.open(csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int id = Integer.parseInt(fields[0].trim());
                String lastName = fields[1].trim();
                String firstName = fields[2].trim();
                String email = fields[3].trim();
                addInstructor(new Instructor(id, lastName, firstName, email));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean importOfferingsFromCSV(String csvFileName) {
        AssetManager am = mContext.getAssets();
        InputStream inStream = null;
        try {
            inStream = am.open(csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 4) {
                    Log.d("OCC Course Finder", "Skipping Bad CSV Row: " + Arrays.toString(fields));
                    continue;
                }
                int crn = Integer.parseInt(fields[0].trim());
                int semesterCode = Integer.parseInt(fields[1].trim());
                int courseId = Integer.parseInt(fields[2].trim());
                int instructorId = Integer.parseInt(fields[3].trim());
                addOffering(crn, semesterCode, courseId, instructorId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
