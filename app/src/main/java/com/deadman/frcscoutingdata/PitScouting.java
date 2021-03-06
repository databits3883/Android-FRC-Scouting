package com.deadman.frcscoutingdata;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andanhm.quantitypicker.QuantityPicker;
import com.opencsv.CSVWriter;
import com.warkiz.widget.IndicatorSeekBar;
import java.io.File;
import android.media.MediaScannerConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PitScouting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitscouting);


        Launch_Counter_pit();
        Level_rocket();
        Level_climb();

        reset_info_pit();

        // Navbar menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        switch(menuItem.getItemId()) {
                            case R.id.welcome:
                                Intent intent5 = new Intent(PitScouting.this, MainActivity.class);
                                startActivity(intent5);
                                break;
                            case R.id.nav_pit:
                                //Intent intent = new Intent(PitScouting.this, PitScouting.class);
                                //startActivity(intent);
                                //break;
                            case R.id.nav_crowd:
                                Intent intent2 = new Intent(PitScouting.this, CrowdScouting.class);
                                startActivity(intent2);
                                break;
                            case R.id.nav_master:
                                Intent intent3 = new Intent(PitScouting.this, MasterDevice.class);
                                startActivity(intent3);
                                break;
                            case R.id.nav_settings:
                                Intent intent4 = new Intent(PitScouting.this, Settings.class);
                                startActivity(intent4);
                                break;
                            default:
                                return onNavigationItemSelected(menuItem);
                        }

                        return true;
                    }
                });



    }

    public void onPause(){
        super.onPause();
        // Insert current values into Shared Preferences so they can be saved if app is closed
        SharedPreferences.Editor editor = getSharedPreferences("CurrentUser", MODE_PRIVATE).edit();
        editor.putString("name_pit", name());
        editor.apply();
    }

    public void onResume() {
        super.onResume();
        // Restore values that were saved when the app is opened again
        SharedPreferences prefs = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        String name = prefs.getString("name_pit", null);
        TextView Person_text = findViewById(R.id.Person_text);
        Person_text.setText(name);
    }

    // Function to take a picture and name it based on the entered team number
    public void take_picture(View view){
        String team_num = team();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File out = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "FRC" + File.separator + "Robots");
        out = new File(out, team_num + ".png");
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        startActivityForResult(i,1);
    }

    // Export entered pit data to a CSV file and reset the fields
    public void savebuttonClick(View view){
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_export_dialog_message)
                .setTitle(R.string.confirm_export_dialog_title)
                .setPositiveButton(R.string.confirm_export, (dialog, id) -> {
                    write_data_pit();
                    reset_info_pit();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // CANCEL
                })
                .show();
    }

    // Write the pit data to a CSV file
    public void write_data_pit(){
        String results = team() + "," + name() + "," + drive_chain() + "," + wheel_style() + "," + ground_hatch_check() + "," + station_hatch_check() + "," + ground_cargo_check() + "," + station_cargo_check() + "," + Slider_hatch() + "," + Slider_cargo() + "," + launch_counter() + "," + vision() + "," + sandstorm() + "," + rocket_counter()  + "," + climb_counter()  + "," + comments();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "FRC"+ File.separator + "pit_data.csv");
        try {
            FileWriter outputfile = new FileWriter(file, true);

            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    "\r\n");
            List<String[]> data = new ArrayList<>();
            if (file.length() == 0) {
                data.add(new String[] {"Team," + "Data entered by," + "Drive Chain," + "Wheel Style," + "Hatches from ground," + "Hatches from station," + "Cargo from ground," + "Cargo from station," + "Total number of Hatches," + "Total number of cargo," + "Launch Position," + "Type of Vision," + "What the robot does in sandstorm," + "Max level on the rocket," + "Max climb level" + "comments," });
            }
            data.add(new String[] {results});
            writer.writeAll(data);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        reset_info_pit();
        rescan(file.getAbsolutePath());
    }

    // Function to scan the edited file so it shows up right away in MTP/OTG
    public void rescan(String file){
        MediaScannerConnection.scanFile(this,
                new String[] {file}, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });
    }

    // Resets all the fields on the page
    public void reset_info_pit(){
        TextView team_text = findViewById(R.id.team_text);
        TextView Drive_text = findViewById(R.id.Drive_text);
        TextView Wheel_text = findViewById(R.id.Wheel_text);
        TextView Vision_text = findViewById(R.id.Vision_text);
        TextView Sandstorm_text = findViewById(R.id.Sandstorm_text);
        TextView Comments_text = findViewById(R.id.Comments_text);
        QuantityPicker Launch_Counter_pit = findViewById(R.id.Launch_Counter_pit);
        QuantityPicker Level_rocket = findViewById(R.id.Level_rocket);
        CheckBox Station_hatch = findViewById(R.id.Station_hatch);
        CheckBox Station_cargo = findViewById(R.id.Station_cargo);
        CheckBox Ground_cargo = findViewById(R.id.Ground_cargo);
        CheckBox Ground_hatch = findViewById(R.id.Ground_hatch);
        IndicatorSeekBar seek_hatch = findViewById(R.id.indicatorSeekBar2);
        IndicatorSeekBar seek_cargo = findViewById(R.id.indicatorSeekBar);
        seek_hatch.setProgress(0);
        seek_cargo.setProgress(0);
        Station_cargo.setChecked(false);
        Station_hatch.setChecked(false);
        Ground_cargo.setChecked(false);
        Ground_hatch.setChecked(false);
        team_text.setText("");
        Drive_text.setText("");
        Wheel_text.setText("");
        Vision_text.setText("");
        Sandstorm_text.setText("");
        Comments_text.setText("");
        Launch_Counter_pit.setQuantitySelected(1);
        Level_rocket.setQuantitySelected(0);
    }

    // The following functions initialize the counters in the UI
    public void Launch_Counter_pit(){
        QuantityPicker quantityPicker = findViewById(R.id.Launch_Counter_pit);
        quantityPicker.setMinQuantity(1);
        quantityPicker.setMaxQuantity(200);
        quantityPicker.setQuantityPicker(true);
        quantityPicker.setTextStyle(QuantityPicker.BOLD);
        quantityPicker.setQuantityTextColor(R.color.colorPrimaryDark);
        quantityPicker.setTextSize(30);
        quantityPicker.setQuantityTextColor(R.color.counter_color);
        quantityPicker.setTextStyle(QuantityPicker.NORMAL);
        quantityPicker.setQuantityButtonColor(R.color.black);
    }

    public void Level_rocket(){
        QuantityPicker quantityPicker = findViewById(R.id.Level_rocket);
        quantityPicker.setMinQuantity(1);
        quantityPicker.setMaxQuantity(200);
        quantityPicker.setQuantityPicker(true);
        quantityPicker.setTextStyle(QuantityPicker.BOLD);
        quantityPicker.setQuantityTextColor(R.color.colorPrimaryDark);
        quantityPicker.setTextSize(30);
        quantityPicker.setQuantityTextColor(R.color.counter_color);
        quantityPicker.setTextStyle(QuantityPicker.NORMAL);
        quantityPicker.setQuantityButtonColor(R.color.black);
    }

    public void Level_climb(){
        QuantityPicker quantityPicker = findViewById(R.id.Level_climb);
        quantityPicker.setMinQuantity(1);
        quantityPicker.setMaxQuantity(200);
        quantityPicker.setQuantityPicker(true);
        quantityPicker.setTextStyle(QuantityPicker.BOLD);
        quantityPicker.setQuantityTextColor(R.color.colorPrimaryDark);
        quantityPicker.setTextSize(30);
        quantityPicker.setQuantityTextColor(R.color.counter_color);
        quantityPicker.setTextStyle(QuantityPicker.NORMAL);
        quantityPicker.setQuantityButtonColor(R.color.black);
    }

    // The following functions get the current data from the UI and return them as a string
    public String team() {
        TextView team = findViewById(R.id.team_text);
        return team.getText().toString();
    }

    public String name() {
        TextView name = findViewById(R.id.Person_text);
        return name.getText().toString();
    }

    public String drive_chain() {
        TextView drive_chain = findViewById(R.id.Drive_text);
        return drive_chain.getText().toString();
    }

    public String wheel_style() {
        TextView wheel_style = findViewById(R.id.Wheel_text);
        return wheel_style.getText().toString();
    }

    public String ground_hatch_check() {
        CheckBox ground_hatch_check = findViewById(R.id.Ground_hatch);
        if (ground_hatch_check.isChecked()){
            return "1";
        } else {
            return "0";
        }
    }

    public String station_hatch_check() {
        CheckBox station_hatch_check = findViewById(R.id.Station_hatch);
        if (station_hatch_check.isChecked()){
            return "1";
        } else {
            return "0";
        }
    }

    public String ground_cargo_check() {
        CheckBox ground_hatch_check = findViewById(R.id.Ground_cargo);
        if (ground_hatch_check.isChecked()){
            return "1";
        } else {
            return "0";
        }
    }

    public String station_cargo_check() {
        CheckBox station_hatch_check = findViewById(R.id.Station_cargo);
        if (station_hatch_check.isChecked()){
            return "1";
        } else {
            return "0";
        }
    }

    public String Slider_hatch() {
        IndicatorSeekBar seekBar = findViewById(R.id.indicatorSeekBar2);
        return Integer.toString(seekBar.getProgress());
    }

    public String Slider_cargo() {
        IndicatorSeekBar seekBar = findViewById(R.id.indicatorSeekBar);
        return Integer.toString(seekBar.getProgress());
}

    public String launch_counter() {
        QuantityPicker picker = findViewById(R.id.Launch_Counter_pit);
        return String.valueOf(picker.getQuantity());
    }

    public String vision() {
        TextView vision = findViewById(R.id.Vision_text);
        return vision.getText().toString();
    }

    public String sandstorm() {
        TextView sandstorm = findViewById(R.id.Sandstorm_text);
        return sandstorm.getText().toString();
    }

    public String rocket_counter() {
        QuantityPicker picker = findViewById(R.id.Level_rocket);
        return String.valueOf(picker.getQuantity());
    }

    public String climb_counter() {
        QuantityPicker picker = findViewById(R.id.Level_climb);
        return String.valueOf(picker.getQuantity());
    }

    // Take the entered string and remove commas then return it
    public String comments() {
        TextView Comments = findViewById(R.id.Comments_text);
        String comment_string = Comments.getText().toString();
        return comment_string.replaceAll(",", " ");
    }

    // Disable the back button as to not force the memory to be cleared for the activity
    @Override
    public void onBackPressed() {}
}
