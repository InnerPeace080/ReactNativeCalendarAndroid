package com.chymtt.reactnativecalendar;

import android.graphics.Color;
import android.os.SystemClock;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Chym on 29/11/15.
 */
public class CalendarManager extends SimpleViewManager<Calendar> {
    public static final String REACT_CLASS = "CalendarAndroid";

    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final DateFormat dateFormat;
    static {
        dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }

    private static final String COLOR_REGEX = "^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})$";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected Calendar createViewInstance(ThemedReactContext context) {
        return new Calendar(context);
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(
                        "topDateChange",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of(
                                        "bubbled", "onDateChange", "captured", "onDateChangeCapture")))
                .build();
    }

    @Override
    protected void addEventEmitters(final ThemedReactContext reactContext, final Calendar view) {
        view.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher()
                        .dispatchEvent(new CalendarEvent(
                                view.getId(),
                                SystemClock.uptimeMillis(),
                                date,
                                selected));
            }
        });
    }

    @ReactProp(name = "topbarVisible")
    public void setTopbarVisible(Calendar view, boolean topbarVisible) {
        view.setTopbarVisible(topbarVisible);
    }

    @ReactProp(name = "arrowColor")
    public void setArrowColor(Calendar view, String color) {
        if (color != null) {
            if (color.matches(COLOR_REGEX)) {
                view.setArrowColor(Color.parseColor(color));
            } else {
                throw new JSApplicationIllegalArgumentException("Invalid arrowColor property: " + color);
            }
        }
    }

    @ReactProp(name = "firstDayOfWeek")
    public void setFirstDayOfWeek(Calendar view, String firstDayOfWeek) {
        if (firstDayOfWeek != null) {
            view.setFirstDayOfWeek(getFirstDayOfWeekFromString(firstDayOfWeek));
        }
    }

    @ReactProp(name = "showDate")
    public void setShowDate(Calendar view, String showDate) {
        if (showDate != null) {
            if (showDate.equals("all")) {
                view.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);
            } else if (showDate.equals("current")) {
                view.setShowOtherDates(MaterialCalendarView.SHOW_DEFAULTS);
            } else {
                throw new JSApplicationIllegalArgumentException("Unknown showDate property: " + showDate);
            }
        }
    }

    @ReactProp(name = "currentDate")
    public void setCurrentDate(Calendar view, ReadableArray data) {
        String type = data.getType(0).name();
        if ("String".equals(type)) {
            try {
                Date date = dateFormat.parse(data.getString(0));
                view.setCurrentDate(date);
            } catch (ParseException e) {
                throw new JSApplicationIllegalArgumentException("Invalid date format: " + data.getString(0));
            }
        } else if ("Number".equals(type)) {
            Double value = data.getDouble(0);
            Date date = new Date(value.longValue());
            view.setCurrentDate(date);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid date format: " + data.getString(0));
        }
    }

    @ReactProp(name = "selectionMode")
    public void setSelectionMode(Calendar view, String mode) {
        if (mode != null) {
            if (mode.equals("none")) {
                view.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
            } else if (mode.equals("single")) {
                view.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            } else if (mode.equals("multiple")) {
                view.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
            } else {
                throw new JSApplicationIllegalArgumentException("Unknown selectionMode property: " + mode);
            }
        }
    }

    @ReactProp(name = "selectionColor")
    public void setSelectionColor(Calendar view, String color) {
        if (color != null) {
            if (color.matches(COLOR_REGEX)) {
                view.setSelectionColor(Color.parseColor(color));
            } else {
                throw new JSApplicationIllegalArgumentException("Invalid selectionColor property: " + color);
            }
        }
    }

    @ReactProp(name = "selectedDates")
    public void setSelectedDates(Calendar view, ReadableArray dates) {
        ArrayList<Date> selectedDates = new ArrayList<Date>();
        for (int i = 0; i < dates.size(); i++) {
            String type = dates.getType(i).name();
            if ("String".equals(type)) {
                try {
                    Date date = dateFormat.parse(dates.getString(i));
                    selectedDates.add(date);
                } catch (ParseException e) {
                    throw new JSApplicationIllegalArgumentException("Invalid date format: " + dates.getString(i));
                }
            } else if ("Number".equals(type)) {
                Double value = dates.getDouble(i);
                Date date = new Date(value.longValue());
                selectedDates.add(date);
            } else {
                throw new JSApplicationIllegalArgumentException("Invalid date format: " + dates.getString(i));
            }
        }
        for (Date date : selectedDates) {
            view.setDateSelected(date, true);
        }
    }

    @ReactProp(name = "titleMonths")
    public void setTitleMonths(Calendar view, ReadableArray titleMonths) {
      Log.v("setTitleMonths","setTitleMonths");
      if(titleMonths.size()<12){
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

        Log.w(className + "." + methodName + "():" + lineNumber, "titleMonths array not enough size, size is " + titleMonths.size() );

      }

      CharSequence[] monthLabels = new CharSequence[12];
      for(int i = 0; i<12; i++){
        if(i<titleMonths.size()){
          monthLabels[i] = titleMonths.getString(i);
        }
        else{
          monthLabels[i] = "unknown";
        }
      }

      view.setTitleMonths(monthLabels);

      // DateFormatTitleFormatter dateFormat = new DateFormatTitleFormatter(
      //   new SimpleDateFormat(
      //           "MMMM - yyyy", Locale.getDefault()
      //   )
      // );
      // view.setTitleFormatter(dateFormat);


    }


    // @ReactProp(name = "titleDateFormat")
    // public void setTitleDateFormat(Calendar view, String titleDateFormat) {
    //   Log.v("setTitleDateFormat","setTitleDateFormat");
    //   Log.d("setTitleDateFormat",titleDateFormat);
    //   String titleDateFormatTemp = titleDateFormat;
    //   DateFormatTitleFormatter dateFormat = new DateFormatTitleFormatter(
    //     new SimpleDateFormat(
    //             "MMMM - yyyy", Locale.getDefault()
    //     )
    //   );
    //   view.setTitleFormatter(dateFormat);
    // }


    private int getFirstDayOfWeekFromString(String firstDayOfWeek) {
        if (firstDayOfWeek.equals("monday")) {
            return java.util.Calendar.MONDAY;
        } else if (firstDayOfWeek.equals("tuesday")) {
            return java.util.Calendar.TUESDAY;
        } else if (firstDayOfWeek.equals("wednesday")) {
            return java.util.Calendar.WEDNESDAY;
        } else if (firstDayOfWeek.equals("thursday")) {
            return java.util.Calendar.THURSDAY;
        } else if (firstDayOfWeek.equals("friday")) {
            return java.util.Calendar.FRIDAY;
        } else if (firstDayOfWeek.equals("saturday")) {
            return java.util.Calendar.SATURDAY;
        } else if (firstDayOfWeek.equals("sunday")) {
            return java.util.Calendar.SUNDAY;
        } else {
            throw new JSApplicationIllegalArgumentException("Unknown firstDayOfWeek property: " + firstDayOfWeek);
        }
    }
}
