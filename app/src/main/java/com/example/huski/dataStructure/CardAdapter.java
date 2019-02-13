package com.example.huski.dataStructure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.FindFragment;
import com.example.huski.ListFragment;
import com.example.huski.MainActivity;
import com.example.huski.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CardAdapter extends ArrayAdapter<cardStruct> {
    //Variable definition
    Activity activity;
    AlertDialog.Builder dialog;
    ImageView imBatterySki;
    ImageButton localiseBtn,deleteBtn;
    TextView cardName,uuid;

    public CardAdapter(Activity activity, ArrayList<cardStruct> cards){
        super(activity,0,cards);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final cardStruct card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }
        // Link to XML
        imBatterySki = convertView.findViewById(R.id.batterySkiLvl);
        cardName = convertView.findViewById(R.id.cardName);
        uuid = convertView.findViewById(R.id.uuid);
        deleteBtn = convertView.findViewById(R.id.deleteButton);
        localiseBtn =  convertView.findViewById(R.id.localiseButton);
        // à bouger dans le truc qui recevra les données des Skis
        if(imBatterySki.getDrawable().getConstantState() == getContext().getResources().getDrawable(R.drawable.battery0).getConstantState()){
            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(500); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            imBatterySki.startAnimation(animation); //to start animation
        }

        // set cardName
        cardName.setText(card.getName());
        uuid.setText(card.getUuid().toString());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment.arrayOfCards.remove(card);
                ListFragment.adapter.notifyDataSetChanged();
                ListFragment.adapter.notifyDataSetInvalidated();
                try {
                    deleteData(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        localiseBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).startTransactionFragment(new FindFragment(card));
            }
        });

        //Alert to edit text

        cardName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup, null);
                final EditText renameInput = (EditText) view.findViewById(R.id.rename);
                dialog = new AlertDialog.Builder(getContext());
                dialog.create();
                dialog.setTitle("Edit the name");

                dialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        card.setName(renameInput.getText().toString());
                        Toast.makeText(activity, "change"+card.getName(), Toast.LENGTH_SHORT).show();
                        cardName.setText(card.getName());
                        ListFragment.adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(view);
                dialog.show();
                return false;
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public void deleteData(cardStruct card) throws IOException {
        int i = 0;
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
        if (testFile != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile = line.toString();
                    String arr[] = textFromFile.split("♥", 2);
                    Log.d("SavedData", arr[1] + " " + card.getUuid().toString());
                    if(arr[1].equals(card.getUuid().toString())){
                        removeLine(testFile, i);
                        Toast.makeText(getContext(), "Card deleted: " + arr[0], Toast.LENGTH_LONG).show();
                    }
                    i++;
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the CardsSaved.txt file.");
            }
        }
    }

    public void removeLine(final File file, final int lineIndex) throws IOException {
        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
        while(reader.hasNextLine())
            lines.add(reader.nextLine());
        reader.close();
        assert lineIndex >= 0 && lineIndex <= lines.size() - 1;
        lines.remove(lineIndex);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for(final String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
