package com.example.universalconvertor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerFrom, spinnerTo;
    private EditText inputValue;
    private Button convertButton;
    private TextView resultView;

    private final String[] lengthUnits = {"Inch", "Foot", "Yard", "Mile", "Centimeter", "Meter", "Kilometer"};
    private final String[] weightUnits = {"Pound", "Ounce", "Ton", "Kilogram", "Gram"};
    private final String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    private Spinner categorySpinner;
    private final String[] categories = {"Length", "Weight", "Temperature"};

    private String selectedFromUnit = "";
    private String selectedToUnit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        inputValue = findViewById(R.id.inputValue);
        convertButton = findViewById(R.id.convertButton);
        resultView = findViewById(R.id.resultView);

        // Initialize spinners with default category (Length)
        setupSpinners(lengthUnits);

        convertButton.setOnClickListener(v -> performConversion());

        categorySpinner = findViewById(R.id.categorySpinner);

        // category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Listen for category changes and update units
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = categories[position];
                switch (category) {
                    case "Length":
                        setupSpinners(lengthUnits);
                        break;
                    case "Weight":
                        setupSpinners(weightUnits);
                        break;
                    case "Temperature":
                        setupSpinners(temperatureUnits);
                        break;
                }
                // Clear result when category changes
                resultView.setText("");
                inputValue.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // Handle  unit category selection
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFromUnit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedToUnit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSpinners(String[] unitList) {
        setupSpinners(unitList, "");
    }

    private void setupSpinners(String[] unitList, String previousToUnit) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Restore previous selection
        int previousPosition = adapter.getPosition(previousToUnit);
        if (previousPosition >= 0) {
            spinnerTo.setSelection(previousPosition);
        }
    }

    private void performConversion() {
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();
        String inputText = inputValue.getText().toString().trim();

        // Validate input is not empty
        if (inputText.isEmpty()) {
            resultView.setText("Please enter a value.");
            return;
        }

        // Validate input is a valid number
        double input;
        try {
            input = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            resultView.setText("Invalid number entered.");
            return;
        }

        // Validate source and destination units
        if (fromUnit.equals(toUnit)) {
            resultView.setText("Source and target units are the same.");
            return;
        }

        // Perform conversion
        try {
            double convertedValue = convertUnits(fromUnit, toUnit, input);
            resultView.setText("Result: " + convertedValue);
        } catch (Exception e) {
            resultView.setText("Conversion not supported.");
        }
    }


    private double convertUnits(String from, String to, double value) throws Exception {
        if (from.equals(to)) return value;

        // Length Conversion
        if (isLengthUnit(from) && isLengthUnit(to)) {
            double cmValue = convertToCm(from, value);
            return convertFromCm(to, cmValue);
        }

        // Weight Conversion
        if (isWeightUnit(from) && isWeightUnit(to)) {
            double kgValue = convertToKg(from, value);
            return convertFromKg(to, kgValue);
        }

        // Temperature Conversion
        if (isTemperatureUnit(from) && isTemperatureUnit(to)) {
            return convertTemperature(from, to, value);
        }

        throw new Exception("Invalid conversion");
    }

    // Length Conversions
    private double convertFromCm(String to, double value) {
        switch (to) {
            case "Inch": return value / 2.54;
            case "Foot": return value / 30.48;
            case "Yard": return value / 91.44;
            case "Mile": return value / 160934;
            case "Meter": return value / 100;
            case "Kilometer": return value / 100000;
            default: return value;
        }
    }

    private double convertToCm(String from, double value) {
        switch (from) {
            case "Meter": return value * 100;
            case "Kilometer": return value * 100000;
            case "Inch": return value * 2.54;
            case "Foot": return value * 30.48;
            case "Yard": return value * 91.44;
            case "Mile": return value * 160934;
            default: return value;
        }
    }

    // Weight Conversions
    private double convertToKg(String from, double value) {
        switch (from) {
            case "Pound": return value * 0.453592;
            case "Ounce": return value * 0.0283495;
            case "Ton": return value * 907.185;
            case "Gram": return value / 1000;
            default: return value;
        }
    }

    private double convertFromKg(String to, double value) {
        switch (to) {
            case "Pound": return value / 0.453592;
            case "Ounce": return value / 0.0283495;
            case "Ton": return value / 907.185;
            case "Gram": return value * 1000;
            default: return value;
        }
    }

    // Temperature Conversions
    private double convertTemperature(String from, String to, double value) {
        if (from.equals("Celsius") && to.equals("Fahrenheit")) return (value * 1.8) + 32;
        if (from.equals("Fahrenheit") && to.equals("Celsius")) return (value - 32) / 1.8;
        if (from.equals("Celsius") && to.equals("Kelvin")) return value + 273.15;
        if (from.equals("Kelvin") && to.equals("Celsius")) return value - 273.15;
        return value;
    }

    private boolean isLengthUnit(String unit) {
        for (String u : lengthUnits) if (u.equals(unit)) return true;
        return false;
    }

    private boolean isWeightUnit(String unit) {
        for (String u : weightUnits) if (u.equals(unit)) return true;
        return false;
    }

    private boolean isTemperatureUnit(String unit) {
        for (String u : temperatureUnits) if (u.equals(unit)) return true;
        return false;
    }
}
