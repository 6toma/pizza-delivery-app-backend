package nl.tudelft.sem.template.coupon.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import lombok.Data;

@Data
public class Date {

    private int day;
    private int month;
    private int year;

    /**
     * Constructor for Date class.
     * Also checks whether passed day, month and year is a valid existing date.
     *
     * @param day day of month
     * @param month month of year
     * @param year year
     */
    public Date(int day, int month, int year) throws ParseException {
        SimpleDateFormat testDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        testDate.setLenient(false);
        testDate.parse(day + "/" + month + "/" + year);
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
