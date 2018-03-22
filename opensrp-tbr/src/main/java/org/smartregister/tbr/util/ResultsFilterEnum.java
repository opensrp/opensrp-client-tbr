package org.smartregister.tbr.util;

/**
 * Created by Imran-PC on 12-Mar-18.
 */

public enum ResultsFilterEnum implements FilterEnum{
    SMEAR_POSITIVE("Smear +ve", "results.result1='test_result' AND value1 like '%plus%'"),
    XPERT_POSITIVE("Xpert +ve", "results.result1='mtb_result' and value1 = 'detected'"),
    RIF_POSITIVE("RIF Resistant +ve", "results.result2='rif_result' and value2='detected'"),
    CULTURE_POSITIVE("Culture +ve", "result1='culture_result' and value1='positive'"),
    XRAY_INDICATIVE("Xray Indicative", "result1='xray_result' and value1='indicative'");

    private String filterString;
    private String condition;

    public String getFilterString(){
        return this.filterString;
    }

    @Override
    public String getCondition(){
        return this.condition;
    }

    ResultsFilterEnum(String filterString, String condition){
        this.filterString = filterString;
        this.condition = condition;
    }
}
