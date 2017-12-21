package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.repository.ResultsRepository;
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

                List<String> contacts = new ArrayList<>();
                contacts.add("MN");
                contacts.add("EW");
                contacts.add("WB");
                contacts.add("MB");
                contacts.add("CE");
                contacts.add("JN");
                contacts.add("QB");
                contacts.add("LL");
                contacts.add("VK");
                contacts.add("SE");
                contacts.add("TT");
                contacts.add("PM");
                contacts.add("LO");
                contacts.add("KK");
                contacts.add("MG");
                contacts.add("PL");
                contacts.add("MA");

                TextView contactView;

                for (int i = 0; i < contacts.size(); i++) {
                    contactView = new TextView(context);
                    contactView.setLayoutParams(contactViewTemplate.getLayoutParams());
                    contactView.setPadding(contactViewTemplate.getPaddingLeft(), contactViewTemplate.getPaddingTop(), contactViewTemplate.getPaddingRight(), contactViewTemplate.getPaddingBottom());
                    contactView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contactViewTemplate.getTextSize() / 2);
                    contactView.setGravity(contactViewTemplate.getGravity());
                    contactView.setText(contacts.get(i));
                    contactView.setId(View.generateViewId());
                    contactView.setTag(R.id.CONTACT_ID, contacts.get(i));

                    if (!isPrimeNumber(i)) {
                        contactView.setBackground(context.getResources().getDrawable(R.color.female_light_pink));
                        contactView.setTextColor(context.getResources().getColor(R.color.female_pink));
                    } else {

                        contactView.setBackground(context.getResources().getDrawable(R.color.male_light_blue));
                        contactView.setTextColor(context.getResources().getColor(R.color.male_blue));
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

    public boolean isPrimeNumber(int number) {

        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
