package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONObject;
import org.smartregister.tbr.R;
import org.smartregister.tbr.activity.BasePatientDetailActivity;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.model.Contact;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

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

                    List<Contact> contacts = getDummyData();
                    FrameLayout contactView;

                    for (int i = 0; i < contacts.size(); i++) {
                        ScreenContactViewHelper screenContactViewHelper = new ScreenContactViewHelper(context, contactViewTemplate, contacts.get(i));
                        contactView = screenContactViewHelper.getScreenContactView();
                        contactView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Contact screenContactData = (Contact) view.getTag(R.id.CONTACT);
                                if (screenContactData != null) {

                                    if (screenContactData.getStage() == null) {
                                        ((BasePatientDetailActivity) context).startFormActivity(Constants.FORM.CONTACT_SCREENING, view.getTag(R.id.CONTACT_ID).toString(), null);
                                    } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.DIAGNOSED) && screenContactData.isNegative() == null) {
                                        showNegativeContactPopUp();

                                    } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.INTREATMENT)) {
                                        screenContactData.setContactId("1f1af838-d36c-4f84-aae0-49110fb7be80");
                                        JSONObject jsonObject = TbrApplication.getInstance().getEventClientRepository().getClientByBaseEntityId(screenContactData.getContactId());
                                        //  ((BasePatientDetailActivity) context).goToPatientDetailActivity(Constants.SCREEN_STAGE.INTREATMENT,patientDetails);

                                    } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.DIAGNOSED)) {
                                        screenContactData.setContactId("1f1af838-d36c-4f84-aae0-49110fb7be80");
                                        JSONObject jsonObject = TbrApplication.getInstance().getEventClientRepository().getClientByBaseEntityId(screenContactData.getContactId());
                                        //  ((BasePatientDetailActivity) context).goToPatientDetailActivity(Constants.SCREEN_STAGE.DIAGNOSED,patientDetails);

                                    } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.SCREENED) && screenContactData.isNegative()) {

                                        screenContactData.setContactId("1f1af838-d36c-4f84-aae0-49110fb7be80");
                                        JSONObject jsonObject = TbrApplication.getInstance().getEventClientRepository().getClientByBaseEntityId(screenContactData.getContactId());
                                        // ((BasePatientDetailActivity) context).goToPatientDetailActivity(Constants.SCREEN_STAGE.SCREENED,patientDetails);
                                    }

                                }
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

    private void showNegativeContactPopUp() {

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

    private List<Contact> getDummyData() {
        List<Contact> contacts = new ArrayList<>();
        Contact a;

        a = new Contact();
        a.setFirstName("Ezekiel");
        a.setLastName("Kashoosha");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Laban");
        a.setLastName("Lego");
        a.setGender(Constants.GENDER.TRANSGENDER);
        a.setStage(Constants.SCREEN_STAGE.DIAGNOSED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Joyce");
        a.setLastName("Zabina");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.INTREATMENT);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Elenor");
        a.setLastName("Swila");
        a.setGender(Constants.GENDER.FEMALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Zipporah");
        a.setLastName("Paliba");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Mickal");
        a.setLastName("Bati");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Peter");
        a.setLastName("Leddy");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Mimi");
        a.setLastName("Taurus");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Nicole");
        a.setLastName("Isipi");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Malengo");
        a.setLastName("Zumba");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.DIAGNOSED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);


        a = new Contact();
        a.setFirstName("Teanaa");
        a.setLastName("Orembe");
        a.setGender(Constants.GENDER.FEMALE);
        a.setStage(Constants.SCREEN_STAGE.SCREENED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Kingsom");
        a.setLastName("Kilele");
        a.setGender(Constants.GENDER.TRANSGENDER);
        a.setStage(Constants.SCREEN_STAGE.DIAGNOSED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Oboro");
        a.setLastName("Bogoti");
        a.setGender(Constants.GENDER.MALE);
        a.setStage(Constants.SCREEN_STAGE.DIAGNOSED);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        a = new Contact();
        a.setFirstName("Supagirl");
        a.setLastName("Galeli");
        a.setGender(Constants.GENDER.FEMALE);
        a.setStage(Constants.SCREEN_STAGE.INTREATMENT);
        a.setNegative(!isPrimeNumber(contacts.size()));
        a.setContactId(generateRandomUUIDString());
        contacts.add(a);

        return contacts;
    }
}
