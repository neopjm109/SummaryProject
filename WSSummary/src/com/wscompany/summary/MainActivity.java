package com.wscompany.summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wscompany.summary.MyHorizontalScrollView.SizeCallback;

public class MainActivity extends Activity {

	// Constants
	static final int INDEX_GO_ROOT = 0;
	static final int INDEX_UPPON_LEVEL = 1;
	static final int MENU_DELETE = 0;
	static final int MENU_CANCEL = 1;
	static final int ACT_LOADPOPUP = 0;
	static final int INDEX_DIRECTRY_START = 2;
	static int INDEX_DIRECTORY_END = 2;

	// Layout Variables //
	MyHorizontalScrollView scrollView;
	View menuView, contentsView;
	LinearLayout.LayoutParams params;

	ViewGroup indexView, pasteView, fileLoaderView, fileView, webPageView,
			recentView, storageView, settingView, upLayer;
	TextView pasteContents, webTitle, webContents, fileTitle, fileContents;

	ListView menuListView;
	LayoutInflater inflater;

	Button summary, sizeUp, sizeDown;
	int[] textSizeCount = { 0, 0, 0, 0, 0 }; // Paste, Wile, Web, Recent,
												// Storage 순

	// Menu Layout Variables //
	String[] menu_items = { "붙여넣기", "파일탐색기", "웹페이지", "최근기록", "보관함", "설정" };
	ImageView btnSlide;

	boolean menuOut, pasteOn, fileViewOn, webPageOn, recentOn, storageOn,
			settingOn, upLayerOn;
	Handler handler = new Handler();
	int btnWidth;

	Context context;
	DisplayMetrics metrics;
	int ScreenWidth;

	// File Loader
	File mCurrentDirectory;
	ListView mFileListView;
	IconfiedTextListAdapter mFileListAdapter;
	int mPosition;
	List<IconfiedText> mDirectoryEntries = new ArrayList<IconfiedText>();
	List<IconfiedText> mFileEntries = new ArrayList<IconfiedText>();

	// Summary Variables //
	private String beforeSummary = "", afterSummary = "", titleSummary = "";

	// ETC Variables//
	DownThread mThread;
	boolean SummaryOn = false;
	
	//GestureDetector
	GestureDetector mDetector;
	final static int DISTANCE = 200;
	final static int VELOCITY = 300;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ScreenWidth = metrics.widthPixels / 2;

		inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		scrollView = (MyHorizontalScrollView) inflater.inflate(
				R.layout.activity_main, null);
		setContentView(scrollView);

		// ViewGroup initialize
		ViewGroupInit();
		VisibilityInvisible();
		menuBooleanFalse();
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		// Menu Layout
		menuListView = (ListView) menuView.findViewById(R.id.menuList);
		menuListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menu_items));
		menuListView.setOnItemClickListener(itemListener);

		btnSlide = (ImageView) contentsView.findViewById(R.id.BtnSlide);
		btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView,
				menuView, btnSlide));
		
		mDetector = new GestureDetector(this, mGestureListener);

		final View[] children = new View[] { menuView, contentsView };

		// Scroll to app (view[1]) when layout finished.
		int scrollToViewIdx = 1;
		scrollView.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(btnSlide, ScreenWidth));
	}

	public void ViewGroupInit() {
		menuView = inflater.inflate(R.layout.left_menu, null);
		contentsView = inflater.inflate(R.layout.right_contents, null);

		indexView = (ViewGroup) contentsView.findViewById(R.id.index);
		pasteView = (ViewGroup) contentsView.findViewById(R.id.paste);
		fileLoaderView = (ViewGroup) contentsView.findViewById(R.id.fileLoader);
		fileView = (ViewGroup) contentsView.findViewById(R.id.fileRead);
		webPageView = (ViewGroup) contentsView.findViewById(R.id.webPage);
		recentView = (ViewGroup) contentsView.findViewById(R.id.recent);
		storageView = (ViewGroup) contentsView.findViewById(R.id.storage);
		settingView = (ViewGroup) contentsView.findViewById(R.id.setting);
		upLayer = (ViewGroup) contentsView.findViewById(R.id.upLayer);

		pasteContents = (TextView) pasteView.findViewById(R.id.pasteContents);

		webTitle = (TextView) webPageView.findViewById(R.id.webTitle);
		webContents = (TextView) webPageView.findViewById(R.id.webContents);

		fileTitle = (TextView) fileView.findViewById(R.id.fileTitle);
		fileContents = (TextView) fileView.findViewById(R.id.fileContents);

		summary = (Button) contentsView.findViewById(R.id.btnSummary);
		summary.setOnClickListener(sumListener);
		sizeUp = (Button) contentsView.findViewById(R.id.btnTextSizeUp);
		sizeUp.setOnClickListener(sumListener);
		sizeDown = (Button) contentsView.findViewById(R.id.btnTextSizeDown);
		sizeDown.setOnClickListener(sumListener);
	}

	public void VisibilityInvisible() {
		pasteView.setVisibility(View.INVISIBLE);
		fileLoaderView.setVisibility(View.INVISIBLE);
		fileView.setVisibility(View.INVISIBLE);
		webPageView.setVisibility(View.INVISIBLE);
		recentView.setVisibility(View.INVISIBLE);
		storageView.setVisibility(View.INVISIBLE);
		settingView.setVisibility(View.INVISIBLE);
		upLayer.setVisibility(View.INVISIBLE);
	}

	public void menuBooleanFalse() {
		menuOut = pasteOn = fileViewOn = webPageOn = recentOn = storageOn = settingOn = upLayerOn = false;
	}

	// UpLayer 클릭 리스너

	View.OnClickListener sumListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnSummary:
				if (SummaryOn) {
					summary.setText("요약하기");

					if (pasteOn)
						pasteContents.setText(beforeSummary);
					else if (fileViewOn)
						fileContents.setText(beforeSummary);
					else if (webPageOn)
						webContents.setText(beforeSummary);

				} else {
					summary.setText("원문보기");

					if (pasteOn)
						pasteContents.setText(afterSummary);
					else if (fileViewOn)
						fileContents.setText(afterSummary);
					else if (webPageOn)
						webContents.setText(afterSummary);
				}

				SummaryOn = !SummaryOn;
				break;
			case R.id.btnTextSizeUp:
				if (pasteOn) {
					if (textSizeCount[0] < 3) {
						switch (textSizeCount[0]) {
						case 0:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[0]++;
					}
				} else if (fileViewOn) {
					if (textSizeCount[1] < 3) {
						switch (textSizeCount[1]) {
						case 0:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[1]++;
					}
				} else if (webPageOn) {
					if (textSizeCount[2] < 3) {
						switch (textSizeCount[2]) {
						case 0:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[2]++;
					}
				}
				break;
			case R.id.btnTextSizeDown:
				if (pasteOn) {
					if (textSizeCount[0] > 0) {
						switch (textSizeCount[0]) {
						case 1:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 15);
							break;
						case 2:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 3:
							pasteContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 줄일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[0]--;
					}
				} else if (fileViewOn) {
					if (textSizeCount[1] > 0) {
						switch (textSizeCount[1]) {
						case 1:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 15);
							break;
						case 2:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 3:
							fileContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[1]--;
					}
				} else if (webPageOn) {
					if (textSizeCount[2] > 0) {
						switch (textSizeCount[2]) {
						case 1:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 15);
							break;
						case 2:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 3:
							webContents.setTextSize(
									TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[2]--;
					}
				}
				break;
			default:
				break;
			}
		}
	};

	// Menu Listener 메뉴 리스너
	OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			// TODO Auto-generated method stub
			indexView.setVisibility(View.VISIBLE);
			VisibilityInvisible();
			menuBooleanFalse();

			switch (pos) {
			case 0: // 붙여넣기
				indexView.setVisibility(View.INVISIBLE);
				pasteView.setVisibility(View.VISIBLE);
				upLayer.setVisibility(View.VISIBLE);
				pasteOn = upLayerOn = true;

				ClipboardManager clipboard = (ClipboardManager) MainActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = clipboard.getPrimaryClip();

				beforeSummary = clip.getItemAt(0)
						.coerceToText(getApplicationContext()).toString();
				pasteContents.setText(beforeSummary);

				break;
			case 1: // 파일탐색기
				indexView.setVisibility(View.INVISIBLE);
				fileLoaderView.setVisibility(View.VISIBLE);
				fileViewOn = true;

				if (mCurrentDirectory == null) {
					mCurrentDirectory = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath());
				}

				mFileListView = (ListView) findViewById(R.id.load_file_listview);
				updateFileList();
				mFileListView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						switch (position) {
						case INDEX_GO_ROOT:
							String ext = Environment.getExternalStorageState();
							if (ext.equals(Environment.MEDIA_MOUNTED)) {
								mCurrentDirectory = new File(Environment
										.getExternalStorageDirectory()
										.getAbsolutePath());
							} else {
								String path = Environment.MEDIA_UNMOUNTED;
								mCurrentDirectory = new File(path);
							}
							updateFileList();
							break;
						case INDEX_UPPON_LEVEL:
							if (mCurrentDirectory.getParent() != null)
								mCurrentDirectory = mCurrentDirectory
										.getParentFile();
							updateFileList();
							break;
						default:
							// 폴더이면
							if (position >= INDEX_DIRECTRY_START
									&& position <= INDEX_DIRECTORY_END) {
								String subPath = "/"
										+ ((IconfiedText) mFileListAdapter
												.getItem(position)).getText();
								mCurrentDirectory = new File(mCurrentDirectory
										.getPath() + subPath);
								updateFileList();
							} else {
								String fileName = ((IconfiedText) mFileListAdapter
										.getItem(position)).getText();
								Intent intent = new Intent();
								intent.putExtra("file_path", mCurrentDirectory.getPath() + "/" + fileName);
								setResult(RESULT_OK, intent);
								String s = mCurrentDirectory.getPath() + "/" + fileName;
								int c = 0;
								for (int i = 1; i < s.length() - 1; i++) {
									if (s.substring(i).contains("/") == true) {
										c = i;
									}
								}

								File dir = makeDirectory(s.substring(0, c));
								File file = makeFile(dir, s);
								String txt = "";
								fileLoaderView.setVisibility(View.INVISIBLE);
								if (file.getName().contains(".txt") == true) {
									txt = readFile(file);
									fileView.setVisibility(View.VISIBLE);
									upLayer.setVisibility(View.VISIBLE);
									fileViewOn = upLayerOn = true;
									fileContents.setText(txt);
								}

								else if (file.getName().contains(".doc") == true
										|| file.getName().contains(".docx") == true) {

									File file2 = null;
									WordExtractor extractor = null;
									try {

										file2 = new File(s);
										FileInputStream fis = new FileInputStream(
												file2.getAbsolutePath());
										HWPFDocument document = new HWPFDocument(
												fis);
										extractor = new WordExtractor(document);
										String[] fileData = extractor
												.getParagraphText();
										for (int i = 0; i < fileData.length; i++) {
											if (fileData[i] != null)
												txt += (fileData[i]);
										}
										fileView.setVisibility(View.VISIBLE);
										upLayer.setVisibility(View.VISIBLE);
										fileViewOn = upLayerOn = true;
										fileContents.setText(txt);
									} catch (Exception exep) {
									}
								}
							}
						}
					}
				});
				break;
			case 2: // 웹페이지
				View layout = inflater.inflate(R.layout.html_dialog, null);
				final EditText htmlEdit = (EditText) layout
						.findViewById(R.id.htmlEdit);

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("웹페이지")
						.setView(layout)
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										mThread = new DownThread(htmlEdit
												.getText().toString());
										mThread.start();
									}
								})
						.setNegativeButton("취소",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();

				break;
			case 3: // 최근기록
				indexView.setVisibility(View.INVISIBLE);
				recentView.setVisibility(View.VISIBLE);
				recentOn = true;
				break;
			case 4: // 보관함
				indexView.setVisibility(View.INVISIBLE);
				storageView.setVisibility(View.VISIBLE);
				storageOn = true;
				break;
			case 5: // 설정
				indexView.setVisibility(View.INVISIBLE);
				settingView.setVisibility(View.VISIBLE);
				settingOn = true;
				break;
			default:
				break;
			}
		}
	};

	// Sliding Listener 슬라이딩 클릭리스너
	static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView scrollView;
		View menuView;
		ImageView btnSlide;

		boolean menuOut = false;

		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View menuView, ImageView btnSlide) {
			super();
			this.scrollView = scrollView;
			this.menuView = menuView;
			this.btnSlide = btnSlide;
		}

		@Override
		public void onClick(View v) {
			int menuWidth = menuView.getMeasuredWidth();

			// Ensure menu is visible
			menuView.setVisibility(View.VISIBLE);

			if (!menuOut) {
				// Scroll to 0 to reveal menu
				int left = 0;
				btnSlide.setImageResource(R.drawable.menu0);
				scrollView.smoothScrollTo(left, 0);
			} else {
				// Scroll to menuWidth so menu isn't on screen.
				int left = menuWidth;
				btnSlide.setImageResource(R.drawable.menu1);
				scrollView.smoothScrollTo(left, 0);
			}
			menuOut = !menuOut;
		}
	}

	// Callback Menu 메뉴 콜백 클래스
	static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;
		int ScreenWidth;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		public SizeCallbackForMenu(View btnSlide, int width) {
			super();
			this.btnSlide = btnSlide;
			this.ScreenWidth = width;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;

			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - (btnWidth + ScreenWidth);
			}
		}
	}

	// 웹페이지 불러오기 쓰레드
	class DownThread extends Thread {
		String mAddr;
		String mResult;

		DownThread(String addr) {
			mAddr = addr;
			mResult = "";
		}

		public void run() {
			mResult = WHtmlParser.DownloadHtml(mAddr);
			mAfterDown.sendEmptyMessage(0);
		}

		Handler mAfterDown = new Handler() {
			public void handleMessage(Message msg) {
				beforeSummary = mThread.mResult;
				titleSummary = WHtmlParser.GetTitle(beforeSummary);
				beforeSummary = WHtmlParser.RemoveTag(beforeSummary);
				webTitle.setText(titleSummary);
				webContents.setText(beforeSummary);
				indexView.setVisibility(View.INVISIBLE);
				webPageView.setVisibility(View.VISIBLE);
				upLayer.setVisibility(View.VISIBLE);
				webPageOn = upLayerOn = true;
			}
		};
	}

	// 파일 읽기
	public File makeDirectory(String dir_path) {
		File dir = new File(dir_path);
		if (!dir.exists()) {
			dir.mkdirs();
		} else {
		}
		return dir;
	}

	// 파일 만들기
	private File makeFile(File dir, String file_path) {
		File file = null;
		boolean isSuccess = false;
		if (dir.isDirectory()) {
			file = new File(file_path);
			if (file != null && !file.exists()) {
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
			} else {
			}
		}
		return file;
	}

	// 파일 읽기
	private String readFile(File file) {
		int readcount = 0;
		String s = "";
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				fileTitle.setText(file.getName().toString());
				Reader in = new InputStreamReader(fis, "KSC5601");
				int i;
				while ((i = in.read()) != -1) {
					s += ((char) i);
				}
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	// 파일목록 업데이트
	public void updateFileList() {
		String ext = Environment.getExternalStorageState();
		// String path = null;
		if (ext.equals(Environment.MEDIA_MOUNTED)) {

		} else {
			String path = Environment.MEDIA_UNMOUNTED;
			// File files = new File(path);
			mCurrentDirectory = new File(path);
		}

		// File files = new File(FILE_PATH);
		mFileListAdapter = new IconfiedTextListAdapter(context);

		if (mDirectoryEntries != null) {
			mDirectoryEntries = new ArrayList<IconfiedText>();
		}
		if (mFileEntries != null) {
			mFileEntries = new ArrayList<IconfiedText>();
		}

		mDirectoryEntries.add(new IconfiedText(".", context.getResources()
				.getDrawable(R.drawable.goroot)));
		mDirectoryEntries.add(new IconfiedText("..", context.getResources()
				.getDrawable(R.drawable.uponelevel)));
		for (File file : mCurrentDirectory.listFiles()) {
			if (file.isDirectory())
				mDirectoryEntries.add(new IconfiedText(file.getName(), context
						.getResources().getDrawable(R.drawable.folder)));
		}
		INDEX_DIRECTORY_END = mDirectoryEntries.size() - 1;
		Collections.sort(mDirectoryEntries);
		for (File file : mCurrentDirectory.listFiles()) {
			if (file.getName().endsWith(".txt")
					| file.getName().endsWith(".java")
					| file.getName().endsWith(".c")
					| file.getName().endsWith(".cpp")
					| file.getName().endsWith(".doc")
					| file.getName().endsWith(".docx")) {
				mFileEntries.add(new IconfiedText(file.getName(), context
						.getResources().getDrawable(R.drawable.text)));
			}
		}
		Collections.sort(mFileEntries);
		mDirectoryEntries.addAll(mFileEntries);
		// }
		mFileListAdapter.setListItems(mDirectoryEntries);
		mFileListView.setAdapter(mFileListAdapter);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return mDetector.onTouchEvent(event);
	}
	
	OnGestureListener mGestureListener = new OnGestureListener(){
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int menuWidth = 0;
			int left = 0;
			if(Math.abs(velocityX) > VELOCITY){
				if(e1.getX() - e2.getX() > DISTANCE){
					menuWidth = menuView.getMeasuredWidth();
					left = menuWidth;
					btnSlide.setImageResource(R.drawable.menu1);
					scrollView.smoothScrollTo(left, 0);
				}
				if(e2.getX() - e1.getX() > DISTANCE){
					// Ensure menu is visible
					menuView.setVisibility(View.VISIBLE);

					if (!menuOut) {
						// Scroll to 0 to reveal menu
						
						btnSlide.setImageResource(R.drawable.menu0);
						scrollView.smoothScrollTo(left, 0);
					} else {
						// Scroll to menuWidth so menu isn't on screen.
						left = menuWidth;
						btnSlide.setImageResource(R.drawable.menu1);
						scrollView.smoothScrollTo(left, 0);
					}
					menuOut = !menuOut;
				}
			}	
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		
	};
}
