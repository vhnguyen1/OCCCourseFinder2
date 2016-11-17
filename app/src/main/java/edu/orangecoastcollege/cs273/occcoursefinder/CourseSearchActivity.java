package edu.orangecoastcollege.cs273.occcoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {

    private DBHelper db;
    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;

    private OfferingListAdapter offeringListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList);
        allInstructorsList = db.getAllInstructors();
        allCoursesList = db.getAllCourses();

        courseTitleEditText = (EditText) findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);

        instructorSpinner = (Spinner) findViewById(R.id.instructorSpinner);
        // Since we're only using getInstructorNames which is an array of Strings, simply use
        // a regular/simple adapter rather than a CustomAdapter
        ArrayAdapter<String> instructorSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getInstructorNames());
        instructorSpinner.setAdapter(instructorSpinnerAdapter);
        instructorSpinner.setOnItemSelectedListener(instructorSpinnerListener);

        offeringsListView = (ListView) findViewById(R.id.offeringsListView);
        offeringListAdapter =
                new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList);
        offeringsListView.setAdapter(offeringListAdapter);

        /* If you want to go through the database by making a CustomCursorAdapter
       Cursor instructorNamesCursor = db.getInstructorNamesCursor();
        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_spinner_item,
                        instructorNamesCursor,
                        new String[] {DBHelper.FIELD_LAST_NAME},
                        new int[] {android.R.id.text1}, 0);
        cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructorSpinner.setAdapter(cursorAdapter);
        */
    }

    private String[] getInstructorNames() {
        String instructorNames[] = new String[allInstructorsList.size() + 1];

        // Having the String below in strings.xml would be much better
        instructorNames[0] = "[Select Instructor]";

        for (int i = 1; i < instructorNames.length; i++)
            instructorNames[i] = allInstructorsList.get(i-1).getFullName();

        return instructorNames;
    }

    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String input = charSequence.toString().toLowerCase();
            offeringListAdapter.clear();

            // If the input is empty, repopulate the ListAdapter with ALL the offerings
            if (input == "")
                for (Offering offering : allOfferingsList)
                    offeringListAdapter.add(offering);
            else {
                Course course;
                String instructorName;
                for (Offering offering : allOfferingsList) {
                    // If the course title contains the user input, add it to the ListAdapter
                    course = offering.getCourse();
                    instructorName = String.valueOf(instructorSpinner.getSelectedItem());
                    if (course.getTitle().toLowerCase().contains(input))
                        offeringListAdapter.add(offering);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public AdapterView.OnItemSelectedListener instructorSpinnerListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
            String selectedInstructorName = String.valueOf(parent.getItemAtPosition(position));
            offeringListAdapter.clear();

            // Having the String below in strings.xml would be much better
            if (selectedInstructorName.equals("[Select Instructor]"))
                for (Offering offering : allOfferingsList)
                    offeringListAdapter.add(offering);
            else {
                Instructor instructor;
                for (Offering offering : allOfferingsList) {
                    instructor = offering.getInstructor();
                    if (instructor.getFullName().equals(selectedInstructorName)) {
                        offeringListAdapter.add(offering);
                    }
                }
            }
        }
        // If the user clicks on the spinner then clicks outside the spinner
        // without making a choice
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            parent.setSelection(0);
        }
    };

    public void reset(View view) {
        courseTitleEditText.setText("");
        instructorSpinner.setSelection(0);
    }
}