package com.saqibdb.YahrtzeitsOfGedolim.helper;

public class HebrewUtil {


    /**
     * Formats a number >=1 and <=999 as a Hebrew-format number
     *
     * @param num       the number to format
     * @param gershayim if true, a geresh will be inserted after a single-digit number,
     *                  and a gershayim will be inserted before the last digit
     *                  of a multiple-digit number.
     * @return A string
     * @throws IllegalArgumentException if the number < 1 or > 999.
     */
    public static String formatHebrewNumber(int num, boolean gershayim) {
        if (num < 1 || num > 999) //FIX handle thousands later.
            throw new IllegalArgumentException("can't format numbers > 999 or <1");

        String[] units = new String[]{"", "א",
                "ב",
                "ג",
                "ד",
                "ה",
                "ו",
                "ז",
                "ח",
                "ט"};
        String[] tens = new String[]{"",
                "י",
                "כ",
                "ל",
                "מ",
                "נ",
                "ס",
                "ע",
                "פ",
                "צ",
        };
        String[] hundredsText = new String[]{"",
                "ק",
                "ר",
                "ש",
                "ת",
        };


        StringBuffer buf = new StringBuffer();
/*		final int thousands= (num/1000);
		if( thousands > 0 )
		{
			buf.append(units[thousands]);
			buf.append( " " );
		}
		*/
        int hundreds = (num / 100) % 10;
        while (hundreds > 4) {
            hundreds -= 4;
            buf.append(hundredsText[4]);
        }
        buf.append(hundredsText[hundreds]);


        final int doubleDigits = (num % 100);
        if (15 == doubleDigits) {
            buf.append("טו");
        } else if (16 == doubleDigits) {
            buf.append("טז");
        } else {
            buf.append(tens[doubleDigits / 10]);
            buf.append(units[doubleDigits % 10]);
        }

        if (gershayim) {
            if (buf.length() > 1) {
                buf.insert(buf.length() - 1, "\u05f4");//gershayim
            } else
                buf.append("\u05f3"); //geresh
        }

        return buf.toString();
    }
}