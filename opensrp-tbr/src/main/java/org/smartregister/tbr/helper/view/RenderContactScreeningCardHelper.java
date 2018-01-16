package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.model.ScreenContact;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderContactScreeningCardHelper extends BaseRenderHelper {

    private static String TAG = RenderContactScreeningCardHelper.class.getCanonicalName();

    public RenderContactScreeningCardHelper(Context context, ResultsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> metadata) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {

                FrameLayout contactViewTemplate = (FrameLayout) view.findViewById(R.id.clientContactFrameLayout);
                if (contactViewTemplate != null) {
                    ViewGroup contactsHolderView = (ViewGroup) view.findViewById(R.id.contactScreeningViewContactsHolder);
                    contactViewTemplate.setVisibility(View.GONE);
                    contactsHolderView.removeAllViews();
                    contactsHolderView.addView(contactViewTemplate);//Reinstate default guy for next reuse

                    List<ScreenContact> contacts = new ArrayList<>();
                    contacts.add(new ScreenContact("1", "MN", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("2", "EK", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("3", "LL", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("4", "JZ", Constants.GENDER.MALE, Constants.SCREEN_STAGE.INTREATMENT, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("5", "ES", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.SCREENED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("6", "ZP", Constants.GENDER.TRANSGENDER, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("7", "MB", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("8", "PL", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("9", "MT", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("10", "NI", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("11", "MZ", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("12", "TO", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.SCREENED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("13", "KK", Constants.GENDER.TRANSGENDER, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("14", "OB", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED, !isPrimeNumber(contacts.size())));
                    contacts.add(new ScreenContact("15", "SG", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT, !isPrimeNumber(contacts.size())));

                    FrameLayout contactView;

                    for (int i = 0; i < contacts.size(); i++) {
                        ScreenContactViewHelper screenContactViewHelper = new ScreenContactViewHelper(context, contactViewTemplate, contacts.get(i));
                        contactView = screenContactViewHelper.getScreenContactView();
                        contactView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ((BasePatientDetailActivity) context).startFormActivity(Constants.FORM.CONTACT_SCREENING, view.getTag(R.id.CONTACT_ID).toString(), null);
                            }
                        });

                        contactsHolderView.addView(contactView);

                    }
                } else {
                    Log.e(TAG, "No Frame Layout contactViewTemplate found");
                }
            }

        });

    }

    //Demo only
    public boolean isPrimeNumber(int number) {

        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
