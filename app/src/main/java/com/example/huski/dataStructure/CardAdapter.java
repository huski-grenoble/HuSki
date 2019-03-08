package com.example.huski.dataStructure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
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
    ImageButton localiseBtn,deleteBtn,renameBtn;
    TextView cardName,chipId;

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
        chipId = convertView.findViewById(R.id.chipId);
        deleteBtn = convertView.findViewById(R.id.deleteButton);
        localiseBtn =  convertView.findViewById(R.id.localiseButton);
        renameBtn =  convertView.findViewById(R.id.renameButton);
        String lvl = "battery" + card.getBatteryLvl();
        imBatterySki.setImageResource(getContext().getResources().getIdentifier(lvl, "drawable", "com.example.huski"));
        if(imBatterySki.getDrawable().getConstantState() == getContext().getResources().getDrawable(R.drawable.battery0).getConstantState()){
            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(500); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            imBatterySki.startAnimation(animation); //to start animation
        }
        else{
            imBatterySki.clearAnimation();
        }

        // set cardName
        cardName.setText(card.getName());
        chipId.setText(card.getChipId());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment.arrayOfCards.remove(card);
                ListFragment.adapter.notifyDataSetChanged();
                ListFragment.adapter.notifyDataSetInvalidated();
                try {
                    deleteData(card);
                    if(ListFragment.periph != null) {
                        ListFragment.periph.envoyer(card.getChipId() + "3");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup, null);
                final EditText renameInput = (EditText) view.findViewById(R.id.rename);
                dialog = new AlertDialog.Builder(getContext());
                dialog.create();
                dialog.setTitle("Edit the name");

                dialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteData(card);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        card.setName(renameInput.getText().toString());
                        Toast.makeText(activity, "change"+card.getName(), Toast.LENGTH_SHORT).show();
                        cardName.setText(card.getName());
                        writeData(card);
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
            }
        });

        localiseBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(ListFragment.periph != null) {
                    ListFragment.periph.envoyer(card.getChipId() + "1");
                }
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
                        try {
                            deleteData(card);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        card.setName(renameInput.getText().toString());
                        Toast.makeText(activity, "change"+card.getName(), Toast.LENGTH_SHORT).show();
                        cardName.setText(card.getName());
                        writeData(card);
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

    /**
     * Deletes card and update cardList according so
     * @param card card to delete
     * @throws IOException
     */
    public void deleteData(cardStruct card) throws IOException {
        int i = 0;
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
        if (testFile != null) {
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile = line;
                    String arr[] = textFromFile.split("♥", 2);
                    Log.d("SavedData", arr[1] + " " + card.getChipId().toString());
                    if(arr[1].equals(card.getChipId().toString())){
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

    /**
     * Deletes the specified line of the specified file
     * @param file file you update
     * @param lineIndex index of the line you want to remove
     * @throws IOException
     */
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

    public void writeData(cardStruct card){
        try {
            if(ListFragment.isConnectedToGW && ListFragment.periph != null) {
                ListFragment.periph.envoyer(card.getChipId() + "2");
            }
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true /*append*/));
            writer.write(card.getName() + "♥" + card.getChipId().toString());
            writer.newLine();
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{testFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the CardsSaved.txt file.");
        }
    }

}
