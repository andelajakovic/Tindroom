package com.example.tindroom.ui.prelogin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tindroom.R;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

public class AboutYouFragment extends Fragment {

    private TextInputLayout nameInput, dateOfBirthInput;
    private TextInputEditText dateOfBirthEditText;
    private AutoCompleteTextView genderDropdown, facultyDropdown;
    private List<Faculty> facultyList;
    private Retrofit retrofit;
    private TindroomApiService tindroomApiService;
    private View rootView;
    private Calendar dateCalendar;
    private ImageView avatarImageView;
    private ImageButton editImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_about_you, container, false);

        retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);

        initViews();
        initListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initViews() {
        nameInput = (TextInputLayout) rootView.findViewById(R.id.nameInput);
        dateOfBirthInput = (TextInputLayout) rootView.findViewById(R.id.dateOfBirthInput);
        facultyDropdown = (AutoCompleteTextView) rootView.findViewById(R.id.facultyDropdown);
        genderDropdown = (AutoCompleteTextView) rootView.findViewById(R.id.genderDropdown);
        dateOfBirthEditText = (TextInputEditText) rootView.findViewById(R.id.dateOfBirthEditText);
        avatarImageView = (ImageView) rootView.findViewById(R.id.avatarImageView);
        editImageButton = (ImageButton) rootView.findViewById(R.id.editImageButton);
        setGenderMenuItems();
        setFacultyMenuItems();
    }

    private void initListeners() {
        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Calendar newCalendar = Calendar.getInstance();  // current date
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                                                         (v, year, monthOfYear, dayOfMonth) -> {
                                                                             dateCalendar = Calendar.getInstance(); // picked date
                                                                             dateCalendar.set(year, monthOfYear, dayOfMonth);
                                                                             dateOfBirthEditText.setText(dateFormatter.format(dateCalendar.getTime()));
                                                                         },
                                                                         newCalendar.get(Calendar.YEAR),
                                                                         newCalendar.get(Calendar.MONTH),
                                                                         newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                Bitmap avatar = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                avatarImageView.setImageBitmap(avatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
            editImageButton.setImageDrawable(getResources().getDrawable(R.drawable.edit_icon));
        }
    }

    private void setGenderMenuItems() {
        String[] items = {"Muško", "Žensko"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        genderDropdown.setAdapter(arrayAdapter);
    }

    private void setFacultyMenuItems() {
        facultyList = new ArrayList<>();

        Call<List<Faculty>> facultiesCall = tindroomApiService.getFaculties();

        facultiesCall.enqueue(new Callback<List<Faculty>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Faculty>> call, @NonNull final Response<List<Faculty>> response) {
                assert response.body() != null;
                facultyList.addAll(response.body());
                String[] items = facultyList.stream().map(Faculty::getName).toArray(String[]::new);

                ArrayAdapter<String> spinnerArrayAdapter;
                spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                                                         android.R.layout.simple_spinner_dropdown_item,
                                                         items);
                facultyDropdown.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(@NonNull final Call<List<Faculty>> call, @NonNull final Throwable t) {
                Log.d("faculties FAILURE", t.toString());
            }
        });
    }

    public void navigateToHomeActivity(View view) {
        NavDirections action = AboutYouFragmentDirections.actionAboutYouFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}