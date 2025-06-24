package edu.uph.m23si3.glucotrack;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Kosongkan
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Kosongkan
    }
}
