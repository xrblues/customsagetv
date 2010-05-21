package org.jdna.sage.io;

import sagex.api.Configuration;
import sagex.api.Utility;


public abstract class SageXML {

    public static final String SAGEXML_CURRENT_VERSION="1.3";
    public static final String[] SAGEXML_SUPPORTED_VERSIONS={SAGEXML_CURRENT_VERSION,"1.1","1.2"};
    public static final String SAGEXML_DTD_PUBLIC_BASE="-//NIELM//DTD SAGESHOWINFO XML ";
    public static final String SAGEXML_DTD_PUBLIC=SAGEXML_DTD_PUBLIC_BASE+SAGEXML_CURRENT_VERSION+"//EN";
    public static final String SAGEXML_DTD_URI="http://sageplugins.sourceforge.net/nielm_modules/sagexmlinfo/sageshowinfo_1_3.dtd";
    public static final String SAGEXML_DTD_LOCAL="sageshowinfo.dtd";


    static private final String[][] AllRoles=new String[][]{
        {"Actor",null},
        {"Lead_Actor",null},
        {"Supporting_Actor",null},
        {"Actress",null},
        {"Lead_Actress",null},
        {"Supporting_Actress",null},
        {"Guest",null},
        {"Guest_Star",null},
        {"Director",null},
        {"Producer",null},
        {"Writer",null},
        {"Choreographer",null},
        {"Sports_Figure",null},
        {"Coach",null},
        {"Host",null},
        {"Executive_Producer",null},
        {"Artist",null}
    };
    static private final String[][] AllPremiereFinale=new String[][]{
        {"Premiere",null},
        {"Season_Premiere",null},
        {"Series_Premiere",null},
        {"Channel_Premiere",null},
        {"Season_Finale",null},
        {"Series_Finale",null}
    };
    static private void TranslateArray(String[][] array){
        for ( int i = 0 ; i < array.length; i ++ ){
            String translation=Utility.LocalizeString(array[i][0]);
            if ( translation.equals(array[i][0])) {
                // no translation
                translation=array[i][0].replace('_', ' ');
            }
            array[i][1]=translation;
        }
    }

    static String GetUntranslatedRole(String translatedRole) {
        return GetUntranslated(AllRoles, translatedRole);
    }
    static String GetUntranslated(String[][] array,String translated){
        initialize();
        for (int i=0;i<array.length;i++){
            if ( array[i][1].equals(translated))
                return array[i][0];
        }
        return translated;
    }

    static private double SAGE_MAJOR_VERSION;
    static private boolean initialized=false;
    
    static private void initialize(){
        synchronized(SageXML.class) {
            if ( initialized )
                return;
            TranslateArray(AllRoles);
            TranslateArray(AllPremiereFinale);
    
            try {
                // version=SageTV V6.2.1.141
                String versionString=Configuration.GetProperty("version", "");
                versionString.replaceAll(".*(V[1-9]+)\\.([0-9]+).*","$1_$2");
                SageXML.SAGE_MAJOR_VERSION=Double.parseDouble(versionString);
            }catch (NumberFormatException e){
                SageXML.SAGE_MAJOR_VERSION=6.1;
            }catch (Exception e) {
                SageXML.SAGE_MAJOR_VERSION=6.1;
            }
        }
    }

    static double getSageMajorVersion() {
        initialize();
        return SAGE_MAJOR_VERSION;
    }

    static String[][] getAllRoles() {
        initialize();
        return AllRoles;
    }

    static String[][] getAllPremiereFinale() {
        initialize();
        return AllPremiereFinale;
    }
}
