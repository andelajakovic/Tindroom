package com.example.tindroom.ui.prelogin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.tindroom.R;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.ImageHandler;
import com.example.tindroom.utils.InputValidator;
import com.example.tindroom.utils.LoadingDialogBar;
import com.example.tindroom.utils.NetworkChangeListener;
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

    private User user;

    private TextInputLayout nameInput, dateOfBirthInput, genderInput, facultyInput;
    private TextInputEditText nameEditText, dateOfBirthEditText, descriptionEditText;
    private AutoCompleteTextView genderDropdown, facultyDropdown;
    private List<Faculty> facultyList;
    private TindroomApiService tindroomApiService;
    private View rootView;
    private Calendar dateCalendar;
    private ImageView avatarImageView;
    private ImageButton editImageButton;
    private Button nextButton;
    private Uri imageUri;
    LoadingDialogBar loadingDialogBar;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about_you, container, false);

        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        loadingDialogBar = new LoadingDialogBar(getActivity());

        initViews();
        initListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initViews() {
        nameInput = rootView.findViewById(R.id.nameInput);
        nameEditText = rootView.findViewById(R.id.nameEditText);

        dateOfBirthInput = rootView.findViewById(R.id.dateOfBirthInput);
        dateOfBirthEditText = rootView.findViewById(R.id.dateOfBirthEditText);

        facultyInput = rootView.findViewById(R.id.facultyInput);
        facultyDropdown = rootView.findViewById(R.id.facultyDropdown);

        genderInput = rootView.findViewById(R.id.genderInput);
        genderDropdown = rootView.findViewById(R.id.genderDropdown);

        editImageButton = rootView.findViewById(R.id.editImageButton);

        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        descriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        nextButton = rootView.findViewById(R.id.nextButton);

        setGenderMenuItems();
        setFacultyMenuItems();

    }

    private void initListeners() {
        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Calendar newCalendar = Calendar.getInstance();  // current date
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.MySpinnerDatePickerStyle,
                                                                         (v, year, monthOfYear, dayOfMonth) -> {
                                                                             dateCalendar = Calendar.getInstance(); // picked date
                                                                             dateCalendar.set(year, monthOfYear, dayOfMonth);
                                                                             dateOfBirthEditText.setText(dateFormatter.format(dateCalendar.getTime()));
                                                                         },
                                                                         newCalendar.get(Calendar.YEAR),
                                                                         newCalendar.get(Calendar.MONTH),
                                                                         newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(newCalendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

        editImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                chooseImage();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                if (checkUserInput()) {
                    updateUserInfo();
                    navigateToRoommateFormFragment(view);
                }
            }
        });
    }

    private void setGenderMenuItems() {
        String[] items = getResources().getStringArray(R.array.your_gender_items);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        genderDropdown.setAdapter(arrayAdapter);
    }

    private void setFacultyMenuItems() {
        facultyList = new ArrayList<>();
        loadingDialogBar.startLoadingDialog();

        Call<List<Faculty>> facultiesCall = tindroomApiService.getFaculties();

        facultiesCall.enqueue(new Callback<List<Faculty>>() {
            // TODO (Andrea: napraviti loading popup dialog i obavijestiti korisnika ako nema internetske veze)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Faculty>> call, @NonNull final Response<List<Faculty>> response) {
                loadingDialogBar.dismissDialog();
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), 1);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                imageUri = data.getData();
                Bitmap bitmap = ImageHandler.handleSamplingAndRotationBitmap(requireContext(), data.getData());
                Glide.with(rootView)
                     .asBitmap()
                     .load(bitmap)
                     .into(avatarImageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkUserInput() {
        InputValidator inputValidator = new InputValidator(getContext());

        boolean nameNotEmptyFlag = inputValidator.isInputEditTextFilled(nameEditText, nameInput);
        boolean dateOfBirthNotEmptyFlag = inputValidator.isInputEditTextFilled(dateOfBirthEditText, dateOfBirthInput);
        boolean genderNotEmptyFlag = inputValidator.isInputEditTextFilled(genderDropdown, genderInput);
        boolean facultyNotEmptyFlag = inputValidator.isInputEditTextFilled(facultyDropdown, facultyInput);

        return nameNotEmptyFlag && dateOfBirthNotEmptyFlag && genderNotEmptyFlag && facultyNotEmptyFlag;
    }

    private void updateUserInfo() {
        if(imageUri != null) {
            user.setImageUri(imageUri.toString());
        }
        user.setName(String.valueOf(nameEditText.getText()));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        user.setDateOfBirth(dateFormatter.format(dateCalendar.getTime()));
        if (String.valueOf(genderDropdown.getText()).equals(getString(R.string.male))) {
            user.setGender('M');
        } else if (String.valueOf(genderDropdown.getText()).equals(getString(R.string.female))) {
            user.setGender('F');
        }
        for (Faculty faculty : facultyList) {
            if (faculty.getName().equals(String.valueOf(facultyDropdown.getText()))) {
                user.setIdFaculty(faculty.getFacultyId());
                break;
            }
        }
        user.setDescription(String.valueOf(descriptionEditText.getText()));
    }

    public void navigateToRoommateFormFragment(View view) {
        NavDirections action = AboutYouFragmentDirections.actionAboutYouFragmentToRoommateFormFragment(user);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeListener,intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}