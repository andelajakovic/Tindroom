package com.example.tindroom.ui.prelogin;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tindroom.R;
import com.example.tindroom.data.model.Faculty;
import com.example.tindroom.data.model.Neighborhood;
import com.example.tindroom.data.model.User;
import com.example.tindroom.network.RetrofitService;
import com.example.tindroom.network.TindroomApiService;
import com.example.tindroom.utils.InputValidator;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.material.slider.LabelFormatter.LABEL_GONE;

public class RoommateFormFragment extends Fragment {

    private User user;

    private Retrofit retrofit;
    private TindroomApiService tindroomApiService;
    private View rootView;

    private List<Neighborhood> neighborhoodList;

    private TextInputLayout roommateGenderInput, neighborhoodInput, priceInput;
    private AutoCompleteTextView roommateGenderDropdown, neighborhoodDropdown;
    private RangeSlider roommateAgeSlider, apartmentPriceSlider;
    private SwitchMaterial haveApartmentSwitch;
    private LinearLayout haveApartmentLayout, needApartmentLayout;
    private TextInputEditText priceEditText;
    private Button navigateToHomeActivityButton;
    private TextView roommateAgeLabel, apartmentPriceLabel;

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
        rootView = inflater.inflate(R.layout.fragment_roommate_form, container, false);

        retrofit = RetrofitService.getRetrofit();
        tindroomApiService = retrofit.create(TindroomApiService.class);

        initViews();
        initListeners();

        return rootView;
    }

    private void initViews() {
        roommateGenderInput = rootView.findViewById(R.id.roommateGenderInput);
        roommateGenderDropdown = rootView.findViewById(R.id.roommateGenderDropdown);

        neighborhoodInput = rootView.findViewById(R.id.neighbourhoodInput);
        neighborhoodDropdown = rootView.findViewById(R.id.neighborhoodDropdown);

        roommateAgeSlider = rootView.findViewById(R.id.roommateAgeSlider);
        roommateAgeLabel = rootView.findViewById(R.id.roommateAgeLabel);
        roommateAgeSlider.setLabelBehavior(LABEL_GONE);

        haveApartmentSwitch = rootView.findViewById(R.id.haveApartmentSwitch);

        haveApartmentLayout = rootView.findViewById(R.id.haveApartmentLayout);
        needApartmentLayout = rootView.findViewById(R.id.needApartmentLayout);

        apartmentPriceSlider = rootView.findViewById(R.id.apartmentPriceSlider);
        apartmentPriceLabel = rootView.findViewById(R.id.apartmentPriceLabel);
        apartmentPriceSlider.setLabelBehavior(LABEL_GONE);

        priceInput = rootView.findViewById(R.id.priceInput);
        priceEditText = rootView.findViewById(R.id.priceEditText);

        apartmentPriceSlider = rootView.findViewById(R.id.apartmentPriceSlider);

        navigateToHomeActivityButton = rootView.findViewById(R.id.navigateToHomeActivityButton);

        setGenderMenuItems();
        setNeighborhoodMenuItems();
        setRoommateAgeLimitValues();
        setApartmentPriceLimitValue();
    }

    private void initListeners() {
        haveApartmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked) {
                if (checked) {
                    haveApartmentLayout.setVisibility(View.VISIBLE);
                    needApartmentLayout.setVisibility(View.GONE);
                } else {
                    haveApartmentLayout.setVisibility(View.GONE);
                    needApartmentLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        roommateAgeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull final RangeSlider slider, final float value, final boolean fromUser) {
                setRoommateAgeLimitValues();
            }
        });

        apartmentPriceSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull final RangeSlider slider, final float value, final boolean fromUser) {
                setApartmentPriceLimitValue();
            }
        });

        navigateToHomeActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(checkUserInput()) {
                    if (String.valueOf(roommateGenderDropdown.getText()).equals(getString(R.string.male))) {
                        user.setRoommateSex('M');
                    } else if (String.valueOf(roommateGenderDropdown.getText()).equals(getString(R.string.female))) {
                        user.setRoommateSex('F');
                    } else {
                        user.setRoommateSex('A');
                    }
                    user.setRoommateAgeFrom(roommateAgeSlider.getValues().get(0).intValue());
                    user.setRoommateAgeTo(roommateAgeSlider.getValues().get(1).intValue());
                    if (haveApartmentSwitch.isChecked()) {
                        user.setHasApartment(true);
                        user.setPriceFrom(Double.parseDouble(String.valueOf((priceEditText.getText()))));
                        for (Neighborhood neighborhood : neighborhoodList) {
                            if (neighborhood.getName().equals(String.valueOf(neighborhoodDropdown.getText()))) {
                                user.setIdNeighborhood(neighborhood.getNeighborhoodId());
                                break;
                            }
                        }
                    } else {
                        user.setHasApartment(false);
                        user.setPriceFrom(apartmentPriceSlider.getValues().get(0));
                        user.setPriceTo(apartmentPriceSlider.getValues().get(1));
                    }
                    user.setRegistered(true);
                    updateUser();
                }
            }
        });
    }

    private void updateUser() {
        Call<User> userCall = tindroomApiService.updateUserById(user.getUserId(), user);
        userCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(final Call<User> call, final Response<User> response) {
                navigateToHomeActivity(rootView);
            }

            @Override
            public void onFailure(final Call<User> call, final Throwable t) {
                Toast.makeText(getContext(), getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRoommateAgeLimitValues() {
        roommateAgeLabel.setText(getResources().getString(R.string.roommate_form_fragment_roommate_age_title, String.valueOf(roommateAgeSlider.getValues().get(0).intValue()), String.valueOf(roommateAgeSlider.getValues().get(1).intValue())));
    }

    private void setApartmentPriceLimitValue() {
        apartmentPriceLabel.setText(getResources().getString(R.string.roommate_form_fragment_price_range_title, String.valueOf(apartmentPriceSlider.getValues().get(0).intValue()), String.valueOf(apartmentPriceSlider.getValues().get(1).intValue())));
    }

    private void setGenderMenuItems() {
        String[] items =  getResources().getStringArray(R.array.roommates_gender_items);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        roommateGenderDropdown.setAdapter(arrayAdapter);
    }

    private void setNeighborhoodMenuItems() {
        neighborhoodList = new ArrayList<>();

        Call<List<Neighborhood>> neighborhoodsCall = tindroomApiService.getNeighborhoods();

        neighborhoodsCall.enqueue(new Callback<List<Neighborhood>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull final Call<List<Neighborhood>> call, @NonNull final Response<List<Neighborhood>> response) {
                assert response.body() != null;
                neighborhoodList.addAll(response.body());
                String[] items = neighborhoodList.stream().map(Neighborhood::getName).toArray(String[]::new);

                ArrayAdapter<String> spinnerArrayAdapter;
                spinnerArrayAdapter = new ArrayAdapter<>(getContext(),
                                                         android.R.layout.simple_spinner_dropdown_item,
                                                         items);
                neighborhoodDropdown.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(@NonNull final Call<List<Neighborhood>> call, @NonNull final Throwable t) {
                Log.d("neighborhoods FAILURE", t.toString());
            }
        });
    }

    private boolean checkUserInput() {
        InputValidator inputValidator = new InputValidator(getContext());

        boolean roommateGenderFlag = inputValidator.isInputEditTextFilled(roommateGenderDropdown, roommateGenderInput);
        boolean priceFlag = true, neighborhoodFlag = true;
        if (haveApartmentSwitch.isChecked()) {
            priceFlag = inputValidator.isInputEditTextFilled(priceEditText, priceInput);
            neighborhoodFlag = inputValidator.isInputEditTextFilled(neighborhoodDropdown, neighborhoodInput);
        }

        return roommateGenderFlag && priceFlag && neighborhoodFlag;
    }

    public void navigateToHomeActivity(View view) {
        NavDirections action = RoommateFormFragmentDirections.actionRoommateFormFragmentToHomeActivity();
        Navigation.findNavController(view).navigate(action);
    }

}