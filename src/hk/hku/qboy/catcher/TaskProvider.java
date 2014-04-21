package hk.hku.qboy.catcher;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TaskProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "hk.hku.qboy.catcher.tasks";
	public static final String PROVIDER_PATH_NAME = "tasks";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/" + PROVIDER_PATH_NAME);

	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String DDL = "ddl";
	public static final String COLOR = "color";
	public static final String URGENT = "urgent";
	public static final String STATUS = "status";

	private static final int TASKS = 1;
	private static final int TASK_ID = 2;

	private static final UriMatcher uri_matcher;
	static {
		uri_matcher = new UriMatcher(UriMatcher.NO_MATCH);
		uri_matcher.addURI(PROVIDER_NAME, PROVIDER_PATH_NAME, TASKS);
		uri_matcher.addURI(PROVIDER_NAME, PROVIDER_PATH_NAME + "/#", TASK_ID);
	}

	private static String LOG_TAG = "catcher";

	// database object
	private SQLiteDatabase tasks_db;

	private static final String DATABASE_NAME = "tasks";
	private static final String DATABASE_TABLE = "titles";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + _ID
			+ " integer primary key autoincrement, " + TITLE
			+ " text not null, " + DDL + " text not null, " + URGENT
			+ " int not null, " + COLOR + " text not null);";

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		int count = 0;
		switch (uri_matcher.match(arg0)) {
		case TASKS:
			count = tasks_db.delete(DATABASE_TABLE, arg1, arg2);
			break;

		case TASK_ID:
			String id = arg0.getPathSegments().get(1);
			count = tasks_db.delete(DATABASE_TABLE, _ID + " = " + id
					+ (!TextUtils.isEmpty(arg1) ? " AND (" + arg1 + ")" : ""),
					arg2);
			break;

		default:
			throw new IllegalArgumentException("unknown uri" + arg0);
		}
		getContext().getContentResolver().notifyChange(arg0, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uri_matcher.match(uri)) {
		case TASKS:
			return "vnd.android.cursor.dir/" + PROVIDER_NAME;

		case TASK_ID:
			return "vnd.android.cursor.item/" + PROVIDER_NAME;

		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// add a new book
		long row_id = tasks_db.insert(DATABASE_TABLE, "", values);

		if (row_id > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, row_id);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		} else {
			throw new SQLException("failed to insert row into " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper db_helper = new DatabaseHelper(context);
		tasks_db = db_helper.getWritableDatabase();

		return (tasks_db == null ? false : true);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sql_builder = new SQLiteQueryBuilder();
		sql_builder.setTables(DATABASE_TABLE);

		if (uri_matcher.match(uri) == TASK_ID) {
			sql_builder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));
		}

		if (sortOrder == null || sortOrder == "") {
			sortOrder = TITLE;
		}

		Cursor cursor = sql_builder.query(tasks_db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;

		switch (uri_matcher.match(uri)) {
		case TASKS:
			count = tasks_db.update(DATABASE_TABLE, values, selection,
					selectionArgs);
			break;

		case TASK_ID:
			count = tasks_db.update(
					DATABASE_TABLE,
					values,
					_ID
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs);

		default:
			throw new IllegalArgumentException("unknown uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
