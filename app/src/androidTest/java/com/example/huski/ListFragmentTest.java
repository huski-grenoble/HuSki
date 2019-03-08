package com.example.huski;

import android.app.Activity;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListFragmentTest {
    private CardAdapter cardAdapter;

    private cardStruct card0,card1,card2;

    public ListFragmentTest() throws Exception {
        this.setUp();
    }

    protected void setUp() throws Exception {
        ArrayList<cardStruct> data = new ArrayList<cardStruct>();

        card0 = new cardStruct("card0", "+34123456789", 2);
        card1 = new cardStruct("card1", "+34123456789", 3);
        card2 = new cardStruct("card2", "+34123456789", 4);
        data.add(card0);
        data.add(card1);
        data.add(card2);
        cardAdapter = new CardAdapter( ListFragment.newInstance().getActivity(),data);

    }

    @Test
    public void newCard() {

    }

    @Test
    public void bluetoothOn() {
    }

    @Test
    public void getList() {
        assertEquals("card0",card0.getName());
    }

    @Test
    public void writeData() {
    }

    @Test
    public void readData() {
    }

    @Test
    public void parseData() {
    }

    @Test
    public void sendFromList() {
    }
}