/**
 * @author Bikas Vaibhav (http://bikasv.com) 2013
 * Rewrote the plug-in at https://github.com/phonegap/phonegap-plugins/tree/master/Android/DatePicker
 * It can now accept `min` and `max` dates for DatePicker.
 */

package com.plugin.datepicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

import android.app.Activity;
import android.content.res.Resources;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.os.Build;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class DatePickerPlugin extends CordovaPlugin implements OnDateChangedListener, TimePicker.OnTimeChangedListener {

	private static final String ACTION_DATE = "date";
	private static final String ACTION_TIME = "time";
	private final String pluginName = "DatePickerPlugin";

	private int pickedYear = -1;
  private int pickedMonth = -1;
  private int pickedDay = -1;
  private int pickedHour = -1;
  private int pickedMinute = -1;

	private Date maxDate = null;
	private Calendar maxDateCal = null;
	private Date minDate = null;
	private Calendar minDateCal = null;

	private TimePicker timePickerView = null;
	private DatePicker datePickerView = null;

	private void pickersToMax() {
		int pHour = this.maxDateCal.get(Calendar.HOUR_OF_DAY);
		int pMinute = this.maxDateCal.get(Calendar.MINUTE);
		int pDay = this.maxDateCal.get(Calendar.DAY_OF_MONTH);
		int pYear = this.maxDateCal.get(Calendar.YEAR);
		int pMonth = this.maxDateCal.get(Calendar.MONTH);

		this.datePickerView.init(pYear, pMonth, pDay, this);
		this.timePickerView.setOnTimeChangedListener(null);
		this.timePickerView.setCurrentHour(pHour);
		this.timePickerView.setCurrentMinute(pMinute);
		this.timePickerView.setOnTimeChangedListener(this);

		this.pickedYear = pYear;
		this.pickedMonth = pMonth;
		this.pickedDay = pDay;
		this.pickedHour = pHour;
		this.pickedMinute = pMinute;
	}

	private void pickersToMin() {
		int pHour = this.minDateCal.get(Calendar.HOUR_OF_DAY);
		int pMinute = this.minDateCal.get(Calendar.MINUTE);
		int pDay = this.minDateCal.get(Calendar.DAY_OF_MONTH);
		int pYear = this.minDateCal.get(Calendar.YEAR);
		int pMonth = this.minDateCal.get(Calendar.MONTH);

		this.datePickerView.init(pYear, pMonth, pDay, this);
		this.timePickerView.setOnTimeChangedListener(null);
		this.timePickerView.setCurrentHour(pHour);
		this.timePickerView.setCurrentMinute(pMinute);
		this.timePickerView.setOnTimeChangedListener(this);

		this.pickedYear = pYear;
		this.pickedMonth = pMonth;
		this.pickedDay = pDay;
		this.pickedHour = pHour;
		this.pickedMinute = pMinute;
	}

	private Date pickedToDate() {
		Calendar calDate = Calendar.getInstance(TimeZone.getDefault());
		calDate.set(Calendar.HOUR_OF_DAY, this.pickedHour);
		calDate.set(Calendar.MINUTE, this.pickedMinute);
		calDate.set(Calendar.DAY_OF_MONTH, this.pickedDay);
		calDate.set(Calendar.YEAR, this.pickedYear);
		calDate.set(Calendar.MONTH, this.pickedMonth);

		return calDate.getTime();
	}

	private void dateToPicked(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(date);
		this.pickedHour = cal.get(Calendar.HOUR_OF_DAY);
		this.pickedMinute = cal.get(Calendar.MINUTE);
		this.pickedDay = cal.get(Calendar.DAY_OF_MONTH);
		this.pickedYear = cal.get(Calendar.YEAR);
		this.pickedMonth = cal.get(Calendar.MONTH);
	}

	@Override
	public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) {
		Log.d(pluginName, "DatePicker called with options: " + data);
		boolean result = false;

		this.show(data, callbackContext);
		result = true;

		return result;
	}

	@Override
  public void onDateChanged(android.widget.DatePicker datePicker, int y, int m, int d) {
		Calendar calDate = Calendar.getInstance(TimeZone.getDefault());
		calDate.set(Calendar.HOUR_OF_DAY, this.pickedHour);
		calDate.set(Calendar.MINUTE, this.pickedMinute);
		calDate.set(Calendar.DAY_OF_MONTH, d);
		calDate.set(Calendar.YEAR, y);
		calDate.set(Calendar.MONTH, m);

		if(this.maxDate != null && calDate.getTime().after(this.maxDate)) {
			// datePicker.updateDate(this.pickedYear, this.pickedMonth, this.pickedDay);
			// datePicker.updateDate(this.pickedYear, this.pickedMonth, this.pickedDay);
			this.pickersToMax();
			return;
		}

		if(this.minDate != null && calDate.getTime().before(this.minDate)) {
			// datePicker.updateDate(this.pickedYear, this.pickedMonth, this.pickedDay);
			// datePicker.updateDate(this.pickedYear, this.pickedMonth, this.pickedDay);
			this.pickersToMin();
			return;
		}

    this.pickedYear = y;
    this.pickedMonth = m;
    this.pickedDay = d;
  }

  @Override
  public void onTimeChanged(TimePicker timePicker, int h, int m) {
		Calendar calDate = Calendar.getInstance(TimeZone.getDefault());
		calDate.set(Calendar.HOUR_OF_DAY, h);
		calDate.set(Calendar.MINUTE, m);
		calDate.set(Calendar.DAY_OF_MONTH, this.pickedDay);
		calDate.set(Calendar.YEAR, this.pickedYear);
		calDate.set(Calendar.MONTH, this.pickedMonth);

		if(this.maxDate != null && calDate.getTime().after(this.maxDate)) {
			// timePicker.setCurrentHour(this.pickedHour);
			// timePicker.setCurrentMinute(this.pickedMinute);
			this.pickersToMax();
			return;
		}

		if(this.minDate != null && calDate.getTime().before(this.minDate)) {
			// timePicker.setCurrentHour(this.pickedHour);
			// timePicker.setCurrentMinute(this.pickedMinute);
			this.pickersToMin();
			return;
		}

		this.pickedHour = h;
		this.pickedMinute = m;
  }

	public synchronized void show(final JSONArray data, final CallbackContext callbackContext) {
		final DatePickerPlugin datePickerPlugin = this;
		final Activity currentCtx = cordova.getActivity();
		final Calendar c = Calendar.getInstance();
		final Runnable runnable;

		String action = "date";
		long minDateLong = 0, maxDateLong = 0;

		int month = -1, day = -1, year = -1, hour = -1, min = -1;
		boolean hideNowButton = false;

		try {
			JSONObject obj = data.getJSONObject(0);
			action = obj.getString("mode");

			String optionDate = obj.getString("date");

			String[] datePart = optionDate.split("/");
			month = Integer.parseInt(datePart[0]);
			day = Integer.parseInt(datePart[1]);
			year = Integer.parseInt(datePart[2]);
			hour = Integer.parseInt(datePart[3]);
			min = Integer.parseInt(datePart[4]);

			minDateLong = obj.getLong("minDate");
			maxDateLong = obj.getLong("maxDate");

			hideNowButton = obj.getBoolean("hideNowButton");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// By default initalize these fields to 'now'
		final int mYear = year == -1 ? c.get(Calendar.YEAR) : year;
		final int mMonth = month == -1 ? c.get(Calendar.MONTH) : month - 1;
		final int mDay = day == -1 ? c.get(Calendar.DAY_OF_MONTH) : day;
		final int mHour = hour == -1 ? c.get(Calendar.HOUR_OF_DAY) : hour;
		final int mMinutes = min == -1 ? c.get(Calendar.MINUTE) : min;
		final boolean nowButtonHidden = hideNowButton;

		this.pickedDay = mDay;
		this.pickedHour = mHour;
		this.pickedMinute = mMinutes;
		this.pickedMonth = mMonth;
		this.pickedYear = mYear;

		final long minDate = minDateLong;
		final long maxDate = maxDateLong;

		if(minDate != 0) {
			this.minDate = new Date(minDate);
			this.minDateCal = Calendar.getInstance(TimeZone.getDefault());
			this.minDateCal.setTime(this.minDate);
			Log.v("MIN DATE", this.minDate.toString());

			if(this.pickedToDate().before(this.minDate)) {
				this.dateToPicked(this.minDate);
			}
		}

		if(maxDate != 0) {
			this.maxDate = new Date(maxDate);
			this.maxDateCal = Calendar.getInstance(TimeZone.getDefault());
			this.maxDateCal.setTime(this.maxDate);
			Log.v("MAX DATE", this.maxDate.toString());
		}

		Log.v("PICKED", "" + this.pickedHour + ":" + this.pickedMinute);

		if (ACTION_TIME.equalsIgnoreCase(action)) {
			runnable = new Runnable() {
				@Override
				public void run() {
					final TimeSetListener timeSetListener = new TimeSetListener(datePickerPlugin, callbackContext);
					final TimePickerDialog timeDialog = new TimePickerDialog(currentCtx, timeSetListener, mHour,
							mMinutes, true);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						timeDialog.setCancelable(true);
						timeDialog.setCanceledOnTouchOutside(false);
						timeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								callbackContext.success("cancel");
							}
						});
						timeDialog.setOnKeyListener(new Dialog.OnKeyListener() {
							@Override
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
								// TODO Auto-generated method stub
								//callbackContext.success("");
								return false;
							}
						});
					}
					timeDialog.show();
				}
			};

		} else if (ACTION_DATE.equalsIgnoreCase(action)) {
			runnable = new Runnable() {
				@Override
				public void run() {
					final DateSetListener dateSetListener = new DateSetListener(datePickerPlugin, callbackContext);
					final DatePickerDialog dateDialog = new DatePickerDialog(currentCtx, dateSetListener, mYear,
							mMonth, mDay);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						DatePicker dp = dateDialog.getDatePicker();
						if(minDate > 0) {
							dp.setMinDate(minDate);
						}
						if(maxDate > 0 && maxDate > minDate) {
							dp.setMaxDate(maxDate);
						}

						dateDialog.setCancelable(true);
						dateDialog.setCanceledOnTouchOutside(false);
						dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				            @Override
				            public void onClick(DialogInterface dialog, int which) {
								callbackContext.success("cancel");
				            }
				        });
						dateDialog.setOnKeyListener(new Dialog.OnKeyListener() {
							@Override
							public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
								// TODO Auto-generated method stub
								//callbackContext.success("");
								return false;
							}
						});
					}
					else {
						java.lang.reflect.Field mDatePickerField = null;
						try {
							mDatePickerField = dateDialog.getClass().getDeclaredField("mDatePicker");
						} catch (NoSuchFieldException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mDatePickerField.setAccessible(true);
						DatePicker pickerView = null;
						try {
							pickerView = (DatePicker) mDatePickerField.get(dateDialog);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

						final Calendar startDate = Calendar.getInstance();
						startDate.setTimeInMillis(minDate);
						final Calendar endDate = Calendar.getInstance();
						endDate.setTimeInMillis(maxDate);

						final int minYear = startDate.get(Calendar.YEAR);
					    final int minMonth = startDate.get(Calendar.MONTH);
					    final int minDay = startDate.get(Calendar.DAY_OF_MONTH);
					    final int maxYear = endDate.get(Calendar.YEAR);
					    final int maxMonth = endDate.get(Calendar.MONTH);
					    final int maxDay = endDate.get(Calendar.DAY_OF_MONTH);

						if(startDate !=null || endDate != null) {
							pickerView.init(mYear, mMonth, mDay, new OnDateChangedListener() {
				                @Override
								public void onDateChanged(DatePicker view, int year, int month, int day) {
				                	if(maxDate > 0 && maxDate > minDate) {
					                	if(year > maxYear || month > maxMonth && year == maxYear || day > maxDay && year == maxYear && month == maxMonth){
					                		view.updateDate(maxYear, maxMonth, maxDay);
					                	}
				                	}
				                	if(minDate > 0) {
					                	if(year < minYear || month < minMonth && year == minYear || day < minDay && year == minYear && month == minMonth) {
					                		view.updateDate(minYear, minMonth, minDay);
					                	}
				                	}
			                	}
				            });
						}
					}
					dateDialog.show();
				}
			};

		} else {
			runnable = new Runnable() {
				@Override
				public void run() {
					Resources activityRes = currentCtx.getResources();
			    String pkgName = currentCtx.getPackageName();

			    LayoutInflater inflater = (LayoutInflater)
			            currentCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			    View layout = inflater.inflate(activityRes.getIdentifier("datetime", "layout", pkgName),
			            (ViewGroup) currentCtx.findViewById(activityRes.getIdentifier("linearLayout2", "id", pkgName)));

			    final AlertDialog.Builder builder = new AlertDialog.Builder(currentCtx);
			    builder.setView(layout);

			    final AlertDialog dialog = builder.create();
					dialog.setCancelable(true);
          dialog.setCanceledOnTouchOutside(false);

			    Button nowButton =(Button)layout.findViewById(activityRes.getIdentifier("nowButton", "id", pkgName));
			    if (nowButtonHidden) {
			    	nowButton.setVisibility(View.GONE);
			    }

			    Button okButton = (Button)layout.findViewById(activityRes.getIdentifier("okButton", "id", pkgName));
			    Button cancelButton = (Button) layout.findViewById(activityRes.getIdentifier("cancelButton", "id", pkgName));

			    DatePicker datePick = (DatePicker) layout.findViewById(activityRes.getIdentifier("datePicker", "id", pkgName));
					datePick.init(datePickerPlugin.pickedYear, datePickerPlugin.pickedMonth, datePickerPlugin.pickedDay, datePickerPlugin);
					datePickerPlugin.datePickerView = datePick;

			    TimePicker timePick = (TimePicker) layout.findViewById(activityRes.getIdentifier("timePicker", "id", pkgName));
					datePickerPlugin.timePickerView = timePick;

					timePick.setIs24HourView(true);
					timePick.setCurrentHour(datePickerPlugin.pickedHour);
          timePick.setCurrentMinute(datePickerPlugin.pickedMinute);
					timePick.setOnTimeChangedListener(datePickerPlugin);

			    nowButton.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
		            dialog.dismiss();
								callbackContext.success("now");
			        }
			    });

			    okButton.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
								Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
								calendar.set(Calendar.HOUR_OF_DAY, datePickerPlugin.pickedHour);
								calendar.set(Calendar.MINUTE, datePickerPlugin.pickedMinute);
								calendar.set(Calendar.DAY_OF_MONTH, datePickerPlugin.pickedDay);
            		calendar.set(Calendar.YEAR, datePickerPlugin.pickedYear);
            		calendar.set(Calendar.MONTH, datePickerPlugin.pickedMonth);

								Log.v("DATETIME", "" + pickedYear + "/" + pickedMonth + "/" + pickedDay + " " + pickedHour + ":" + pickedMinute);

		            dialog.dismiss();

								callbackContext.success("" + calendar.getTime().getTime());
			        }
			    });

			    cancelButton.setOnClickListener(new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
		            dialog.dismiss();
								callbackContext.success("cancel");
			        }
			    });

			    dialog.show();
				}
			};
		}

		cordova.getActivity().runOnUiThread(runnable);
	}

	private final class DateSetListener implements OnDateSetListener {
		private final DatePickerPlugin datePickerPlugin;
		private final CallbackContext callbackContext;

		private DateSetListener(DatePickerPlugin datePickerPlugin, CallbackContext callbackContext) {
			this.datePickerPlugin = datePickerPlugin;
			this.callbackContext = callbackContext;
		}

		/**
		 * Return a string containing the date in the format YYYY/MM/DD
		 */
		@Override
		public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
			String returnDate = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
			callbackContext.success(returnDate);
		}
	}

	private final class TimeSetListener implements OnTimeSetListener {
		private final DatePickerPlugin datePickerPlugin;
		private final CallbackContext callbackContext;

		private TimeSetListener(DatePickerPlugin datePickerPlugin, CallbackContext callbackContext) {
			this.datePickerPlugin = datePickerPlugin;
			this.callbackContext = callbackContext;
		}

		/**
		 * Return the current date with the time modified as it was set in the
		 * time picker.
		 */
		@Override
		public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String toReturn = sdf.format(calendar.getTime());

			callbackContext.success(toReturn);
		}
	}

}
