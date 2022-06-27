package com.example.tindroom.ui.postlogin;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tindroom.R;
import com.example.tindroom.data.local.SharedPreferencesStorage;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Neighborhood;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.InputValidator;
import com.example.tindroom.utils.LoadingDialogBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    View rootView;
    TindroomApiService tindroomApiService;
    private User user;

    private TextInputLayout nameInput, dateOfBirthInput, genderInput, facultyInput, priceInput, neighbourhoodInput;
    private TextInputEditText nameEditText, dateOfBirthEditText, descriptionEditText, priceEditTex;
    private AutoCompleteTextView roommateGenderDropdown, facultyDropdown, neighbourhoodDropdown;
    private TextInputLayout roommateGenderInput;
    private LinearLayout needApartmentLayout, haveApartmentLayout;
    private List<Faculty> facultyList;
    private Button updateInfo;
    private TextView deleteAccount, roommateAgeLabel, apartmentPriceLabel, roommateLimit;
    private Calendar dateCalendar, defaultDateCalendar;
    private RangeSlider roommateAgeSlider, apartmentPriceSlider;
    private boolean hasApartment = false;
    private SwitchMaterial haveApartmentSwitch;
    LoadingDialogBar loadingDialogBar;

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    FirebaseAuth mAuth;
    private List<Neighborhood> neighborhoodList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = SharedPreferencesStorage.getSessionUser(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Retrofit retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);
        loadingDialogBar = new LoadingDialogBar(getActivity());

        initViews();
        initData();
        initListeners();

        return rootView;
    }

    private void initViews() {
        needApartmentLayout = rootView.findViewById(R.id.needApartmentLayout);
        haveApartmentLayout = rootView.findViewById(R.id.have_apartment_layout);

        deleteAccount = rootView.findViewById(R.id.deleteAccount);
        updateInfo = rootView.findViewById(R.id.save);

        nameInput = rootView.findViewById(R.id.usernameInput);
        nameEditText = rootView.findViewById(R.id.usernameEditText);

        dateOfBirthInput = rootView.findViewById(R.id.dateOfBirthInput);
        dateOfBirthEditText = rootView.findViewById(R.id.dateOfBirthEditText);

        neighbourhoodInput = rootView.findViewById(R.id.neighbourhoodInput);
        neighbourhoodDropdown = rootView.findViewById(R.id.neighborhoodDropdown);

        priceInput = rootView.findViewById(R.id.priceInput);
        priceEditTex = rootView.findViewById(R.id.priceEditText);

        facultyInput = rootView.findViewById(R.id.facultyInput);
        facultyDropdown = rootView.findViewById(R.id.facultyDropdown);

        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        descriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        roommateGenderInput = rootView.findViewById(R.id.roommateGenderInput);
        roommateGenderDropdown = rootView.findViewById(R.id.roommateGenderDropdown);

        roommateAgeSlider = rootView.findViewById(R.id.slider);
        roommateAgeLabel = rootView.findViewById(R.id.roommateLimit);

        apartmentPriceLabel = rootView.findViewById(R.id.apartmentPriceLabel);
        apartmentPriceSlider = rootView.findViewById(R.id.apartmentPriceSlider);

        haveApartmentSwitch = rootView.findViewById(R.id.haveApartmentSwitch);

        userHasApartment();
        setGenderMenuItems();
        setFacultyMenuItems();
    }

    private void userHasApartment(){
        if(user.isHasApartment()){
            haveApartmentSwitch.setChecked(true);
            haveApartmentLayout.setVisibility(View.VISIBLE);
            needApartmentLayout.setVisibility(View.GONE);
            hasApartment = true;
            userApartmentPrice();
        }else{
            haveApartmentSwitch.setChecked(false);
            haveApartmentLayout.setVisibility(View.GONE);
            needApartmentLayout.setVisibility(View.VISIBLE);
            hasApartment = false;
            apartmentPriceSlider.setValues(500f, 1500f);
            userHasApartmentPrice();
        }
    }

    private void initData(){
        nameEditText.setText(user.getName());
        descriptionEditText.setText(user.getDescription());
        String gender = String.valueOf(user.getRoommateGender());
        roommateGenderDropdown.setText(gender,false);
        String price = String.valueOf(Math.round(user.getPriceFrom()));
        priceEditTex.setText(price);

        userBirthDate();
        userRoommateAge();
        userApartmentPrice();
        userFaculty();
        userNeighborhood();
    }

    private void userBirthDate(){
        String month = user.getDateOfBirth().substring(5,7);
        String day = user.getDateOfBirth().substring(9,10);
        String year = user.getDateOfBirth().substring(0,4);

        dateCalendar = Calendar.getInstance();
        dateCalendar.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        dateOfBirthEditText.setText(dateFormatter.format(dateCalendar.getTime()));
    }

    private void userRoommateAge(){
        String ageFrom = String.valueOf(user.getRoommateAgeFrom());
        String ageTo = String.valueOf(user.getRoommateAgeTo());
        roommateAgeSlider.setValues((float)(user.getRoommateAgeFrom()),(float)(user.getRoommateAgeTo()));
        roommateAgeLabel.setText(getResources().getString(R.string.roommate_age_limit, ageFrom, ageTo));
    }

    private void userApartmentPrice(){
        String priceFrom = String.valueOf(Math.round(user.getPriceFrom()));
        String priceTo = String.valueOf(Math.round(user.getPriceTo()));
        apartmentPriceSlider.setValues((float)(user.getPriceFrom()),(float)(user.getPriceTo()));
        apartmentPriceLabel.setText(getResources().getString(R.string.apartment_price_range, priceFrom,priceTo));
    }

    private void userHasApartmentPrice(){
        priceEditTex.setText(String.valueOf(user.getPriceFrom()));
    }

    private void userFaculty(){
        /*Long faculty = user.getIdFaculty();
        final String[] userFaculty = {""};

        Call<Faculty> facultyCall = tindroomApiService.getFacultyById(faculty);
        facultyCall.enqueue(new Callback<Faculty>() {
            @Override
            public void onResponse(Call<Faculty> call, Response<Faculty> response) {
                assert response.body() != null;
                userFaculty[0] = response.body().getName();
                facultyDropdown.setText(userFaculty[0], false);
            }
            @Override
            public void onFailure(Call<Faculty> call, Throwable t) {
            }
        });*/
        facultyDropdown.setText(user.getFaculty().getName(), false);
    }

    private void userNeighborhood(){
        /*Long neighborId = user.getIdNeighborhood();
        final String[] neighborhood = {""};
        Call<Neighborhood> neighborhoodCall = tindroomApiService.getNeighborhoodById(neighborId);
        neighborhoodCall.enqueue(new Callback<Neighborhood>() {
            @Override
            public void onResponse(Call<Neighborhood> call, Response<Neighborhood> response) {
                assert response.body() != null;
                neighborhood[0] = response.body().getName();
                neighbourhoodDropdown.setText(neighborhood[0], false);
            }
            @Override
            public void onFailure(Call<Neighborhood> call, Throwable t) {
            }
        });*/
        neighbourhoodDropdown.setText(user.getNeighborhood().getName(), false);
    }

    private void setGenderMenuItems(){
        String[] items = getResources().getStringArray(R.array.roommates_gender_items);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        roommateGenderDropdown.setAdapter(arrayAdapter);
    }

    private void setRoommateAge() {
        roommateAgeLabel.setText(getResources().getString(R.string.roommate_age_limit, String.valueOf(roommateAgeSlider.getValues().get(0).intValue()), String.valueOf(roommateAgeSlider.getValues().get(1).intValue())));
    }

    private void setApartmentPriceLimitValue() {
        apartmentPriceLabel.setText(getResources().getString(R.string.roommate_form_fragment_price_range_title, String.valueOf(apartmentPriceSlider.getValues().get(0).intValue()), String.valueOf(apartmentPriceSlider.getValues().get(1).intValue())));
    }

    private void setFacultyMenuItems() {
        facultyList = new ArrayList<>();
        loadingDialogBar.startLoadingDialog();
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
                setNeighborhoodMenuItems();
            }

            @Override
            public void onFailure(@NonNull final Call<List<Faculty>> call, @NonNull final Throwable t) {
                Log.d("faculties FAILURE", t.toString());
                loadingDialogBar.dismissDialog();
            }
        });
    }

    private void setNeighborhoodMenuItems() {
        neighborhoodList = new ArrayList<>();

        Call<List<Neighborhood>> neighborhoodsCall = tindroomApiService.getNeighborhoods();

        neighborhoodsCall.enqueue(new Callback<List<Neighborhood>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Neighborhood>> call, @NonNull final Response<List<Neighborhood>> response) {
                loadingDialogBar.dismissDialog();
                assert response.body() != null;
                neighborhoodList.addAll(response.body());
                String[] items = neighborhoodList.stream().map(Neighborhood::getName).toArray(String[]::new);

                ArrayAdapter<String> spinnerArrayAdapter;
                spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        items);
                neighbourhoodDropdown.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(@NonNull final Call<List<Neighborhood>> call, @NonNull final Throwable t) {
                Log.d("neighborhoods FAILURE", t.toString());
                loadingDialogBar.dismissDialog();
            }
        });
    }

    private void initListeners() {
        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar newCalendar = Calendar.getInstance();  // current date
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
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
        roommateAgeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull final RangeSlider slider, final float value, final boolean fromUser) {
                setRoommateAge();
            }
        });

        apartmentPriceSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull final RangeSlider slider, final float value, final boolean fromUser) {
                setApartmentPriceLimitValue();
            }
        });
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });
        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(checkUserInput()){
                    updateUserInfo();
                    navigateToMainActivity(view);
                //}
            }
        });
        haveApartmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(haveApartmentSwitch.isChecked()){
                    user.setHasApartment(true);
                }else{
                    user.setHasApartment(false);
                }
                userHasApartment();

            }
        });
    }

    private void updateUserInfo() {
        user.setName(String.valueOf(nameEditText.getText()));
        user.setDescription(String.valueOf(descriptionEditText.getText()));
        user.setDateOfBirth(dateFormatter.format(dateCalendar.getTime()));
        user.setRoommateAgeFrom(roommateAgeSlider.getValues().get(0).intValue());
        user.setRoommateAgeTo(roommateAgeSlider.getValues().get(1).intValue());

        if (String.valueOf(roommateGenderDropdown.getText()).equals(getString(R.string.male))) {
            user.setRoommateGender('M');
        } else if (String.valueOf(roommateGenderDropdown.getText()).equals(getString(R.string.female))) {
            user.setRoommateGender('F');
        }else{
            user.setRoommateGender('A');
        }
        for (Faculty faculty : facultyList) {
            if (faculty.getName().equals(String.valueOf(facultyDropdown.getText()))) {
                user.setIdFaculty(faculty.getFacultyId());
                break;
            }
        }

        if(hasApartment){
            for (Neighborhood neighborhood :  neighborhoodList){
                if(neighborhood.getName().equals(String.valueOf(neighbourhoodDropdown.getText()))) {
                    user.setIdNeighborhood(neighborhood.getNeighborhoodId());
                    break;

                }
            }
            user.setPriceFrom(Double.parseDouble(String.valueOf(priceEditTex.getText())));
        }else{
            user.setPriceFrom(apartmentPriceSlider.getValues().get(0).intValue());
            user.setPriceTo(apartmentPriceSlider.getValues().get(1).intValue());
        }

        SharedPreferencesStorage.setSessionUser(getContext(),user);

        Call<User> userUpdateCall = tindroomApiService.updateUserById(user.getUserId(), user);
        userUpdateCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("updated", response.toString());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    /*private boolean checkUserInput() {
        InputValidator inputValidator = new InputValidator(getContext());

        boolean nameNotEmptyFlag = inputValidator.isInputEditTextFilled(nameEditText, nameInput);
        boolean dateOfBirthNotEmptyFlag = inputValidator.isInputEditTextFilled(dateOfBirthEditText, dateOfBirthInput);
        boolean genderNotEmptyFlag = inputValidator.isInputEditTextFilled(roommateGenderDropdown, roommateGenderInput);
        boolean facultyNotEmptyFlag = inputValidator.isInputEditTextFilled(facultyDropdown, facultyInput);

        boolean roommateGenderFlag = inputValidator.isInputEditTextFilled(roommateGenderDropdown, roommateGenderInput);
        boolean priceFlag = true, neighborhoodFlag = true;

        if (hasApartment) {
            priceFlag = inputValidator.isInputEditTextFilled(priceEditTex, priceInput);
            neighborhoodFlag = inputValidator.isInputEditTextFilled(neighbourhoodDropdown, neighbourhoodInput);
        }

        return nameNotEmptyFlag && dateOfBirthNotEmptyFlag && genderNotEmptyFlag && facultyNotEmptyFlag && roommateGenderFlag && priceFlag && neighborhoodFlag;
    }*/

    private void deleteUser(){
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    navigateToMainActivity(rootView);
                }
            }
        });

        // TODO (treba izbrisati korisnika i s apija, ali za to jos nemamo poziv)
    }

    public void navigateToMainActivity(View view) {
        NavDirections action = SettingsFragmentDirections.actionSettingsFragmentToMainActivity();
        Navigation.findNavController(view).navigate(action);
    }}