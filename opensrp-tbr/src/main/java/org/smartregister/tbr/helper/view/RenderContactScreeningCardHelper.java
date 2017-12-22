package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.model.ScreenContact;
import org.smartregister.tbr.repository.ResultsRepository;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 23/11/2017.
 */

public class RenderContactScreeningCardHelper extends BaseRenderHelper {

    public RenderContactScreeningCardHelper(Context context, ResultsRepository detailsRepository) {
        super(context, detailsRepository);
    }

    @Override
    public void renderView(final View view, final Map<String, String> metadata) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {

                LinearLayout contactScreeningView = (LinearLayout) view.findViewById(R.id.clientContactScreeningCardView);
                ViewGroup contactsHolderView = (ViewGroup) contactScreeningView.findViewById(R.id.contactScreeningViewContactsHolder);
                TextView contactViewTemplate = (TextView) contactsHolderView.findViewById(R.id.clientContactTextView);
                contactsHolderView.removeView(contactViewTemplate);

                List<ScreenContact> contacts = new ArrayList<>();
                contacts.add(new ScreenContact("1", "MN", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED));
                contacts.add(new ScreenContact("2", "EK", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("3", "LL", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("4", "JZ", Constants.GENDER.MALE, Constants.SCREEN_STAGE.INTREATMENT));
                contacts.add(new ScreenContact("5", "ES", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.SCREENED));
                contacts.add(new ScreenContact("6", "ZP", Constants.GENDER.TRANSGENDER, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("7", "MB", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED));
                contacts.add(new ScreenContact("8", "PL", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT));
                contacts.add(new ScreenContact("9", "MT", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT));
                contacts.add(new ScreenContact("10", "NI", Constants.GENDER.MALE, Constants.SCREEN_STAGE.SCREENED));
                contacts.add(new ScreenContact("11", "MZ", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("12", "TO", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.SCREENED));
                contacts.add(new ScreenContact("13", "KK", Constants.GENDER.TRANSGENDER, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("14", "OB", Constants.GENDER.MALE, Constants.SCREEN_STAGE.DIAGNOSED));
                contacts.add(new ScreenContact("15", "SG", Constants.GENDER.FEMALE, Constants.SCREEN_STAGE.INTREATMENT));

                TextView contactView;

                for (int i = 0; i < contacts.size(); i++) {
                    contactView = new TextView(context);
                    contactView.setLayoutParams(contactViewTemplate.getLayoutParams());
                    contactView.setPadding(contactViewTemplate.getPaddingLeft(), contactViewTemplate.getPaddingTop(), contactViewTemplate.getPaddingRight(), contactViewTemplate.getPaddingBottom());
                    contactView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contactViewTemplate.getTextSize() / 2);
                    contactView.setGravity(contactViewTemplate.getGravity());
                    contactView.setText(contacts.get(i).getName());
                    contactView.setId(View.generateViewId());
                    contactView.setTag(R.id.CONTACT_ID, contacts.get(i));


                    if (contacts.get(i).getGender().equals(Constants.GENDER.FEMALE)) {
                        contactView.setBackground(context.getResources().getDrawable(R.color.female_light_pink));
                        contactView.setTextColor(context.getResources().getColor(R.color.female_pink));
                    } else if (contacts.get(i).getGender().equals(Constants.GENDER.MALE)) {
                        contactView.setBackground(context.getResources().getDrawable(R.color.male_light_blue));
                        contactView.setTextColor(context.getResources().getColor(R.color.male_blue));
                    } else if (contacts.get(i).getGender().equals(Constants.GENDER.TRANSGENDER)) {
                        contactView.setBackground(context.getResources().getDrawable(R.color.gender_neutral_light_green));
                        contactView.setTextColor(context.getResources().getColor(R.color.gender_neutral_green));
                    }

                    if (contacts.get(i).isNegative()) {
                        contactView.findViewById(R.id.clientContactIndicatorImageView).setVisibility(View.GONE);
                        contactView.setBackground(context.getResources().getDrawable(R.color.disabled_light_gray));
                        contactView.setTextColor(context.getResources().getColor(R.color.disabled_gray));
                    } else {

                        ImageView indicatorImageView = (ImageView) contactView.findViewById(R.id.clientContactIndicatorImageView);
                        if (contacts.get(i).getStage().equals(Constants.SCREEN_STAGE.SCREENED)) {
                            indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_screened));
                        } else if (contacts.get(i).getStage().equals(Constants.SCREEN_STAGE.DIAGNOSED)) {
                            indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_diagnosed));
                        } else if (contacts.get(i).getStage().equals(Constants.SCREEN_STAGE.INTREATMENT)) {
                            indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_intreatment));
                        }
                    }

                    contactView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Utils.showToast(context, "Clicked on Contact " + view.getTag(R.id.CONTACT_ID));
                        }
                    });

                    contactsHolderView.addView(contactView);

                }
            }

        });

    }
}
