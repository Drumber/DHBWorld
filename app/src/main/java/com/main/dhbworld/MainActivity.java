package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.main.dhbworld.KVV.KVVDataLoader;
import com.main.dhbworld.Navigation.NavigationUtilities;

public class MainActivity extends AppCompatActivity{
    FloatingActionButton editButton;
    MaterialButton saveButton;
    MaterialButton cancelButton;
    TextView name;
    TextView mNumber;
    TextView lNumber;
    TextView studentMail;
    TextView freeNotes;
    SharedPreferences sp;

    public static final String MyPREFERENCES = "myPreferencesKey" ;
    public static final String Name = "nameKey";
    public static final String LibraryNumber = "libraryNumberKey";
    public static final String MatriculationNumber = "matriculationNumberKey";
    public static final String StudentMail= "studentMailKey";
    public static final String FreeNotes = "freeNotesKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information);
        //instantiate buttons, text fields
        editButton = findViewById(R.id.edit_Button);
        saveButton = findViewById(R.id.save_Button);
        cancelButton = findViewById(R.id.cancel_Button);
        name = findViewById(R.id.studentName);
        mNumber = findViewById(R.id.matriculationNumber);
        lNumber = findViewById(R.id.libraryNumber);
        studentMail = findViewById(R.id.studentMail);
        freeNotes = findViewById(R.id.freeNotes);

        name.setEnabled(false);
        mNumber.setEnabled(false);
        lNumber.setEnabled(false);
        studentMail.setEnabled(false);
        freeNotes.setEnabled(false);
        //when the app is opened it loads the data if there is any
        loadAndUpdateData();

        NavigationUtilities.setUpNavigation(this, R.id.personalInformationNav);




        View.OnClickListener copy= new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", name.getText().toString());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
            }
        };
        TextInputLayout textInputLayout_name = findViewById(R.id.textInputLayoutPI_name);
        TextInputLayout textInputLayout_matriculationNumber = findViewById(R.id.textInputLayoutPI_matriculationNumber);
        TextInputLayout textInputLayout_adresse = findViewById(R.id.textInputLayoutPI_adresse);
        TextInputLayout textInputLayout_libraryNummer = findViewById(R.id.textInputLayoutPI_libraryNummer);


        textInputLayout_name.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", name.getText().toString());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
            }
        });
        textInputLayout_matriculationNumber.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", mNumber.getText().toString());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
            }
        });
        textInputLayout_libraryNummer.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", lNumber.getText().toString());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
            }
        });
        textInputLayout_adresse.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", studentMail.getText().toString());
                clipboard.setPrimaryClip(clip);


                Toast.makeText(MainActivity.this, "Kopiert", Toast.LENGTH_LONG).show();
            }
        });

        editButton.setOnClickListener(v -> {
            //visibility off buttons
            editButton.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            //enabling text field
            name.setEnabled(true);
            mNumber.setEnabled(true);
            lNumber.setEnabled(true);
            studentMail.setEnabled(true);
            freeNotes.setEnabled(true);

        });

        saveButton.setOnClickListener(v -> {

            if(checkMNumber(mNumber.getText().toString())){
                if(checkLNumber(lNumber.getText().toString())){
                    saveData();
                    //visibility off buttons
                    editButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.INVISIBLE);
                    cancelButton.setVisibility(View.INVISIBLE);
                    //disabling text field
                    name.setEnabled(false);
                    mNumber.setEnabled(false);
                    lNumber.setEnabled(false);
                    studentMail.setEnabled(false);
                    freeNotes.setEnabled(false);
                }
            }


        });

        cancelButton.setOnClickListener(v -> {
            //visibility off buttons
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            //disabling text field
            name.setEnabled(false);
            mNumber.setEnabled(false);
            lNumber.setEnabled(false);
            studentMail.setEnabled(false);
            freeNotes.setEnabled(false);

            loadAndUpdateData();
        });

    }





    public boolean checkLNumber(String l){
        if(l.isEmpty()){
            return true;
        }else{
            try{
                long libraryNumberLength = Long.parseLong(l);
                if(libraryNumberLength>999999999999l){
                    lNumber.setError("Can only contain 12 digits");
                    return false;
                }else{
                    return true;
                }
            }catch(NumberFormatException g){
                lNumber.setError("No letters, only digits");
                return false;
            }
        }
    }

    public boolean checkMNumber(String m){
        if(m.isEmpty()){
            return true;
        }else{
            try{
                long matriculationNumberLength = Long.parseLong(m);
                if(matriculationNumberLength>9999999){
                    mNumber.setError("Can only contain 7 digits");
                    return false;
                }else{
                    return true;
                }
            }catch (NumberFormatException g){
                mNumber.setError("No letters, only digits");
                return false;
            }
        }


    }

    public void saveData(){
        //instantiate the SharedPreferences
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //put the data into it
        editor.putString(Name, name.getText().toString());
        editor.putString(LibraryNumber, lNumber.getText().toString());
        editor.putString(MatriculationNumber, mNumber.getText().toString());
        editor.putString(StudentMail, studentMail.getText().toString());
        editor.putString(FreeNotes, freeNotes.getText().toString());
        editor.apply();
        //show a message to the user
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadAndUpdateData(){
        //load the data and put it into the text field
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        name.setText(sp.getString(Name, ""));
        mNumber.setText(sp.getString(MatriculationNumber,""));
        lNumber.setText(sp.getString(LibraryNumber, ""));
        studentMail.setText(sp.getString(StudentMail,""));
        freeNotes.setText(sp.getString(FreeNotes,""));
    }



}