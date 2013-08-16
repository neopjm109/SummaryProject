
package com.wscompany.summary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wscompany.summary.MyHorizontalScrollView.SizeCallback;

public class MainActivity extends Activity {
	
	// Layout Variables //
    MyHorizontalScrollView scrollView;
    View menuView, contentsView;
    ViewGroup indexView, pasteView, fileLoaderView, webPageView, recentView, storageView, settingView;
    TextView pasteContents, webTitle, webContents;
    ListView menuListView;
    LayoutInflater inflater;
    
    String [] menu_items = {"붙여넣기","파일탐색기","웹페이지","최근기록","보관함","설정"};
    ImageView btnSlide;
    
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
    
    Context context;
    
    // Summary Variables //
    private String beforeSummary = "", afterSummary ="", titleSummary = "";
    
    // Paste Variables//    
	DownThread mThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	context = getApplicationContext();

    	inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.activity_main, null);
        setContentView(scrollView);

        // ViewGroup initialize
        menuView= inflater.inflate(R.layout.left_menu, null);
        contentsView = inflater.inflate(R.layout.right_contents, null);
        
        indexView = (ViewGroup) contentsView.findViewById(R.id.index);
        pasteView = (ViewGroup) contentsView.findViewById(R.id.paste);
        fileLoaderView = (ViewGroup) contentsView.findViewById(R.id.fileLoader);
        webPageView = (ViewGroup) contentsView.findViewById(R.id.webPage);
        recentView = (ViewGroup) contentsView.findViewById(R.id.recent);
        storageView = (ViewGroup) contentsView.findViewById(R.id.storage);
        settingView = (ViewGroup) contentsView.findViewById(R.id.setting);
        
        // ViewGroup initialize
        pasteContents = (TextView) pasteView.findViewById(R.id.pasteContents);
        
        webTitle = (TextView) webPageView.findViewById(R.id.webTitle);
        webContents = (TextView) webPageView.findViewById(R.id.webContents);
        
        pasteView.setVisibility(View.INVISIBLE);
        fileLoaderView.setVisibility(View.INVISIBLE);
        webPageView.setVisibility(View.INVISIBLE);
        recentView.setVisibility(View.INVISIBLE);
        storageView.setVisibility(View.INVISIBLE);
        settingView.setVisibility(View.INVISIBLE);
        
        // Menu
        menuListView = (ListView) menuView.findViewById(R.id.menuList);
        menuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu_items));
        menuListView.setOnItemClickListener(itemListener);
                
        btnSlide = (ImageView) contentsView.findViewById(R.id.BtnSlide);
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menuView));

        final View[] children = new View[] { menuView, contentsView };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));
    }
    
    // Menu Listener 메뉴 리스너
    OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			// TODO Auto-generated method stub
			indexView.setVisibility(View.INVISIBLE);
			pasteView.setVisibility(View.INVISIBLE);
			fileLoaderView.setVisibility(View.INVISIBLE);
			webPageView.setVisibility(View.INVISIBLE);
			recentView.setVisibility(View.INVISIBLE);
			storageView.setVisibility(View.INVISIBLE);
			settingView.setVisibility(View.INVISIBLE);
			
			switch(pos){
			case 0:	// 붙여넣기
				pasteView.setVisibility(View.VISIBLE);
				
				ClipboardManager clipboard = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = clipboard.getPrimaryClip();
				
				beforeSummary = clip.getItemAt(0).coerceToText(getApplicationContext()).toString();
				pasteContents.setText(beforeSummary);
				
				break;
			case 1:	// 파일탐색기
				fileLoaderView.setVisibility(View.VISIBLE);
				break;
			case 2:	// 웹페이지
				View layout = inflater.inflate(R.layout.html_dialog, null);
				final EditText htmlEdit = (EditText)layout.findViewById(R.id.htmlEdit);
				webPageView.setVisibility(View.VISIBLE);
				
				new AlertDialog.Builder(MainActivity.this).setTitle("웹페이지")
				.setView(layout).setPositiveButton("확인", new DialogInterface.OnClickListener(){
					
					public void onClick(DialogInterface dialog, int which) {
						mThread = new DownThread(htmlEdit.getText().toString());
						mThread.start();
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				
				break;
			case 3:	// 최근기록
				recentView.setVisibility(View.VISIBLE);
				break;
			case 4:	// 보관함
				storageView.setVisibility(View.VISIBLE);
				break;
			case 5:	// 설정
				settingView.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};

    // Sliding Listener 슬라이딩 리스너
    static class ClickListenerForScrolling implements OnClickListener {
        HorizontalScrollView scrollView;
        View menuView;

        boolean menuOut = false;

        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menuView) {
            super();
            this.scrollView = scrollView;
            this.menuView = menuView;
        }

        @Override
        public void onClick(View v) {
            int menuWidth = menuView.getMeasuredWidth();

            // Ensure menu is visible
            menuView.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
        }
    }

    // Callback Menu 메뉴 콜백 클래스
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
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
                dims[0] = w - btnWidth;
            }
        }
    }
    
    // 웹페이지 불러오기 쓰레드
    class DownThread extends Thread{
		String mAddr;
		String mResult;
		
		DownThread(String addr){
			mAddr = addr;
			mResult = "";
		}
		
		public void run(){
			mResult = WHtmlParser.DownloadHtml(mAddr);
			mAfterDown.sendEmptyMessage(0);
		}
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg) {
				beforeSummary = mThread.mResult;
				titleSummary = WHtmlParser.GetTitle(beforeSummary);
				afterSummary = WHtmlParser.RemoveTag(beforeSummary);
				webTitle.setText(titleSummary);
				webContents.setText(beforeSummary);
	    	}
		};
	}
}
