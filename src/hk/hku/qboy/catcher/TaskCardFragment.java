/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package hk.hku.qboy.catcher;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardCursorAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.UndoBarController;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * List of Google Play cards Example with Undo Controller
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class TaskCardFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	// private CardArrayAdapter mCardArrayAdapter;
	TaskCardAdapter mAdapter;
	private CardListView mListView;

	@Override
	public int getTitleResourceId() {
		return R.string.app_name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.cursor_list_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {

		mAdapter = new TaskCardAdapter(getActivity());
		mListView = (CardListView) getActivity().findViewById(
				R.id.cursor_list_layout);
		if (mListView != null) {
			mListView.setAdapter(mAdapter);
		}

		// Force start background query to load sessions
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader = null;
		loader = new CursorLoader(getActivity(), TaskProvider.CONTENT_URI,
				TaskProvider.ALL_PROJECTION, TaskProvider.COMPLETED + " = 0",
				null, TaskProvider.DEFAULT_SORT);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (getActivity() == null) {
			return;
		}
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);

	}

	public class TaskCardAdapter extends CardCursorAdapter {

		public TaskCardAdapter(Context context) {
			super(context);
		}

		@Override
		protected Card getCardFromCursor(Cursor cursor) {
			TaskCard card = new TaskCard(super.getContext());
			setCardFromCursor(card, cursor);

			// Create a CardHeader
			CardHeader header = new CardHeader(getActivity());
			// Set the header title

			header.setTitle(card.title);
			header.setPopupMenu(R.menu.popupmain,
					new CardHeader.OnClickCardHeaderPopupMenuListener() {
						@Override
						public void onMenuItemClick(BaseCard card, MenuItem item) {
							// Toast.makeText(
							// getContext(),
							// "Click on card=" + card.getId() + " item="
							// + item.getOrder(),
							// Toast.LENGTH_SHORT).show();
							switch (item.getOrder()) {
							case (100):
								ContentValues values = new ContentValues();
								values.put(TaskProvider.COMPLETED, "1");
								getContext().getContentResolver().update(
										TaskProvider.CONTENT_URI, values, TaskProvider._ID + " = "+card.getId(),null);
								break;
							case (101):
								Intent intent = new Intent(getContext(),
										TaskDetail.class);
								intent.putExtra("title_key", card.getTitle());
								intent.putExtra("id_key", Integer.parseInt(card.getId()));
								startActivity(intent);
								break;
							}
						}
					});

			// Add Header to card
			card.addCardHeader(header);

			CardThumbnail thumb = new CardThumbnail(getActivity());
			thumb.setDrawableResource(card.resourceIdThumb);
			card.addCardThumbnail(thumb);

			card.setOnClickListener(new Card.OnCardClickListener() {
				@Override
				public void onClick(Card card, View view) {
					// Toast.makeText(
					// getContext(),
					// "Card id=" + card.getId() + " Title="
					// + card.getTitle(), Toast.LENGTH_SHORT)
					// .show();
					Intent timerIntent = new Intent(getContext(),
							TimerActivity.class);
					timerIntent.putExtra("title", card.getTitle());
					timerIntent.putExtra("id_key",
							Integer.parseInt(card.getId()));
					startActivity(timerIntent);
				}
			});
			// card.init();
			return card;
		}

		private void setCardFromCursor(TaskCard card, Cursor cursor) {
			card.id = cursor.getInt(getIndex(TaskProvider._ID));
			card.title = cursor.getString(getIndex(TaskProvider.TITLE));
			card.deadline = cursor.getString(getIndex(TaskProvider.DDL));
			int urgent = cursor.getInt(getIndex(TaskProvider.URGENT));
			int color = cursor.getInt(getIndex(TaskProvider.COLOR));
			if (urgent == 1) {
				switch (color) {
				case Color.BLUE:
					card.resourceIdThumb = R.drawable.blue_u;
					break;
				case Color.GREEN:
					card.resourceIdThumb = R.drawable.green_u;
					break;
				case Color.GRAY:
					card.resourceIdThumb = R.drawable.grey_u;
					break;
				case Color.PINK:
					card.resourceIdThumb = R.drawable.pink_u;
					break;
				case Color.RED:
					card.resourceIdThumb = R.drawable.red_u;
					break;
				case Color.YELLOW:
					card.resourceIdThumb = R.drawable.yellow_u;
					break;
				}
			} else {
				switch (color) {
				case Color.BLUE:
					card.resourceIdThumb = R.drawable.blue_n;
					break;
				case Color.GREEN:
					card.resourceIdThumb = R.drawable.green_n;
					break;
				case Color.GRAY:
					card.resourceIdThumb = R.drawable.grey_n;
					break;
				case Color.PINK:
					card.resourceIdThumb = R.drawable.pink_n;
					break;
				case Color.RED:
					card.resourceIdThumb = R.drawable.red_n;
					break;
				case Color.YELLOW:
					card.resourceIdThumb = R.drawable.yellow_n;
					break;
				}
			}

		}
	}

	private void removeCard(Card card) {
		// TODO
		// Use this code to delete items on DB
		// ContentResolver resolver = getActivity().getContentResolver();
		// long noDeleted = resolver.delete
		// (CardCursorContract.CardCursor.CONTENT_URI,
		// CardCursorContract.CardCursor.KeyColumns.KEY_ID + " = ? ",
		// new String[]{card.getId()});

		// mAdapter.notifyDataSetChanged();

	}

	private int getIndex(String s) {
		return Arrays.asList(TaskProvider.ALL_PROJECTION).indexOf(s);
	}

	public class TaskCard extends Card {
		int id;
		String title;

		public String getId() {
			return "" + id;
		}

		public String getTitle() {
			return title;
		}

		String deadline;
		int resourceIdThumb;

		public TaskCard(Context context) {
			super(context, R.layout.card_inner_content);
		}

		@Override
		public void setupInnerViewElements(ViewGroup parent, View view) {
			// Retrieve elements
			TextView mTitleTextView = (TextView) parent
					.findViewById(R.id.inner_title);
			// TextView mSecondaryTitleTextView = (TextView)
			// parent.findViewById(R.id.secondary_inner_title);

			if (mTitleTextView != null)
				mTitleTextView.setText(deadline);

		}

		// private void init(){
		// setSwipeable(true);
		//
		// setOnSwipeListener(new OnSwipeListener() {
		// @Override
		// public void onSwipe(Card card) {
		// Toast.makeText(getContext(), "Removed card=" + title,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		//
		//
		// setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
		// @Override
		// public void onUndoSwipe(Card card) {
		// Toast.makeText(getContext(), "Undo card=" + title,
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// }
	}

}
