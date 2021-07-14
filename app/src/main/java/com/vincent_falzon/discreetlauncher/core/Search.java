package com.vincent_falzon.discreetlauncher.core ;

// License
/*

	This file is part of Discreet Launcher.

	Copyright (C) 2019-2021 Vincent Falzon

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

// Imports
import android.app.Activity ;
import android.content.Context ;
import android.graphics.Color ;
import android.graphics.drawable.ColorDrawable ;
import android.graphics.drawable.Drawable ;
import android.text.Editable ;
import android.text.TextWatcher ;
import android.view.Gravity ;
import android.view.KeyEvent ;
import android.view.LayoutInflater ;
import android.view.MotionEvent ;
import android.view.View ;
import android.view.ViewGroup ;
import android.view.inputmethod.EditorInfo ;
import android.view.inputmethod.InputMethod ;
import android.view.inputmethod.InputMethodManager ;
import android.widget.EditText ;
import android.widget.LinearLayout ;
import android.widget.PopupWindow ;
import android.widget.TextView ;
import androidx.recyclerview.widget.RecyclerView ;
import com.vincent_falzon.discreetlauncher.ActivityMain ;
import com.vincent_falzon.discreetlauncher.Constants ;
import com.vincent_falzon.discreetlauncher.FlexibleGridLayout ;
import com.vincent_falzon.discreetlauncher.R ;
import com.vincent_falzon.discreetlauncher.SearchAdapter ;
import java.util.ArrayList ;

/**
 * Represent the search application.
 */
public class Search extends Application
{
	// Attributes
	private PopupWindow popup ;
	private SearchAdapter adapter ;


	/**
	 * Constructor to represent the search application.
	 * @param display_name Displayed to the user
	 * @param icon Displayed to the user
	 */
	public Search(String display_name, Drawable icon)
	{
		super(display_name, Constants.APK_SEARCH, Constants.APK_SEARCH, icon) ;
		popup = null ;
	}


	/**
	 * Display the search application as a popup.
	 * @param parent Element from which the event originates
	 * @return Always <code>true</code>
	 */
	public boolean start(View parent)
	{
		// Initializations
		Context context = parent.getContext() ;
		LayoutInflater inflater = LayoutInflater.from(context) ;

		// Prepare the popup view
		View popupView = inflater.inflate(R.layout.popup, (ViewGroup)null) ;
		popupView.findViewById(R.id.popup_title).setVisibility(View.GONE) ;
		popupView.findViewById(R.id.close_popup).setOnClickListener(new PopupClickListener()) ;

		// Prepare the search bar
		EditText searchBar = popupView.findViewById(R.id.search_bar) ;
		searchBar.setVisibility(View.VISIBLE) ;
		searchBar.addTextChangedListener(new TextChangeListener()) ;
		searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener()
			{
				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
				{
					// Perform an action when the user presses "Enter"
					if(actionId == EditorInfo.IME_ACTION_DONE)
						{
							// If there is only one application remaining, start it
							if(adapter.getItemCount() == 1)
								{
									adapter.getFirstItem().start(view) ;
									return true ;
								}
						}
					return false ;
				}
			}) ;

		// Retrieve all the applications without folders and the search
		ArrayList<Application> applications = ActivityMain.getApplicationsList().getApplications(false) ;
		Application search = null ;
		for(Application application : applications)
			if(application instanceof Search) search = application ;
		if(search != null) applications.remove(search) ;

		// Prepare the popup content
		RecyclerView recycler = popupView.findViewById(R.id.popup_recycler) ;
		adapter = new SearchAdapter(context, applications) ;
		recycler.setAdapter(adapter) ;
		recycler.setLayoutManager(new FlexibleGridLayout(context, ActivityMain.getApplicationWidth())) ;

		// Create the popup representing the search application
		int popup_height = Math.min(context.getResources().getDisplayMetrics().heightPixels / 2, parent.getRootView().getHeight()) ;
		popup = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, popup_height, true) ;
		popupView.setOnTouchListener(new PopupTouchListener()) ;

		// Fix popup not closing on press back with API 21
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)) ;

		// Display the popup and the keyboard
		popup.showAtLocation(parent, Gravity.CENTER, 0, 0) ;
		((InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInputFromWindow(parent.getWindowToken(), InputMethod.SHOW_EXPLICIT, 0) ;
		return true ;
	}


	/**
	 * Dismiss the popup if it is currently displayed.
	 */
	public void closePopup()
	{
		if(popup != null) popup.dismiss() ;
	}


	/**
	 * Listen for a click on the popup.
	 */
	private class PopupClickListener implements View.OnClickListener
	{
		/**
		 * Detect a click on a view.
		 * @param view Target element
		 */
		@Override
		public void onClick(View view)
		{
			// Close the popup
			closePopup() ;
		}
	}


	/**
	 * Dismiss the popup when the user touchs outside of it (needs <code>focusable = true</code>).
	 */
	private class PopupTouchListener implements View.OnTouchListener
	{
		/**
		 * Detect a gesture on a view.
		 * @param view Target element
		 * @param event Details about the gesture
		 * @return <code>true</code> if the event is consumed, <code>false</code> otherwise
		 */
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			view.performClick() ;
			closePopup() ;
			return true ;
		}
	}


	/**
	 * Called when the text of an EditText is changed by the user.
	 */
	private class TextChangeListener implements TextWatcher
	{
		// Needed to implement TextWatcher
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after){ }


		/**
		 * Called when the text has just been changed.
		 * @param text New text
		 * @param start Place of the replacement
		 * @param before Previous text length
		 * @param count Number of new characters
		 */
		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count)
		{
			// Update the display of the RecyclerView
			adapter.getFilter().filter(text) ;
		}

		// Needed to implement TextWatcher
		@Override
		public void afterTextChanged(Editable s){ }
	}
}
