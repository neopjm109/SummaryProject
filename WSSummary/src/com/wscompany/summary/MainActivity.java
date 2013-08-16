
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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
	
	// Layout Variables //
    MyHorizontalScrollView scrollView;
    View menuView, contentsView;
    LinearLayout.LayoutParams params;
    
    ViewGroup indexView, pasteView, fileLoaderView, webPageView, recentView, storageView, settingView, upLayer;
    TextView pasteContents, webTitle, webContents;
    
    ListView menuListView;
    LayoutInflater inflater;
    
    Button summary, sizeUp, sizeDown;
    int[] textSizeCount = {0, 0, 0, 0, 0};	// Paste, Wile, Web, Recent, Storage 순
    
    // Menu Layout Variables //
    String [] menu_items = {"붙여넣기","파일탐색기","웹페이지","최근기록","보관함","설정"};
    ImageView btnSlide;
    
    boolean menuOut, pasteOn, fileLoaderOn, webPageOn, recentOn, storageOn, settingOn, upLayerOn;
    Handler handler = new Handler();
    int btnWidth;
    
    Context context;
    DisplayMetrics metrics;
	int ScreenWidth;
    
    // Summary Variables //
    private String beforeSummary = "", afterSummary ="", titleSummary = "";
    
    // ETC Variables//
	DownThread mThread;
    boolean SummaryOn = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	context = getApplicationContext();
    	metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	ScreenWidth = metrics.widthPixels/2;

    	inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.activity_main, null);
        setContentView(scrollView);

        // ViewGroup initialize
        ViewGroupInit();
        VisibilityInvisible();
		menuBooleanFalse();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        		LinearLayout.LayoutParams.MATCH_PARENT);
                        
        // Menu Layout
        menuListView = (ListView) menuView.findViewById(R.id.menuList);
        menuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu_items));
        menuListView.setOnItemClickListener(itemListener);
                
        btnSlide = (ImageView) contentsView.findViewById(R.id.BtnSlide);
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menuView, btnSlide));
        
        final View[] children = new View[] { menuView, contentsView };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide, ScreenWidth));
    }
    
    public void ViewGroupInit(){
        menuView= inflater.inflate(R.layout.left_menu, null);
        contentsView = inflater.inflate(R.layout.right_contents, null);
        
        indexView = (ViewGroup) contentsView.findViewById(R.id.index);
        pasteView = (ViewGroup) contentsView.findViewById(R.id.paste);
        fileLoaderView = (ViewGroup) contentsView.findViewById(R.id.fileLoader);
        webPageView = (ViewGroup) contentsView.findViewById(R.id.webPage);
        recentView = (ViewGroup) contentsView.findViewById(R.id.recent);
        storageView = (ViewGroup) contentsView.findViewById(R.id.storage);
        settingView = (ViewGroup) contentsView.findViewById(R.id.setting);
        upLayer = (ViewGroup) contentsView.findViewById(R.id.upLayer);
        
        pasteContents = (TextView) pasteView.findViewById(R.id.pasteContents);
        
        webTitle = (TextView) webPageView.findViewById(R.id.webTitle);
        webContents = (TextView) webPageView.findViewById(R.id.webContents);
        
        summary = (Button) contentsView.findViewById(R.id.btnSummary);
        summary.setOnClickListener(sumListener);
        sizeUp = (Button) contentsView.findViewById(R.id.btnTextSizeUp);
        sizeUp.setOnClickListener(sumListener);
        sizeDown = (Button) contentsView.findViewById(R.id.btnTextSizeDown);
        sizeDown.setOnClickListener(sumListener);
    }
    
    public void VisibilityInvisible(){
        pasteView.setVisibility(View.INVISIBLE);
        fileLoaderView.setVisibility(View.INVISIBLE);
        webPageView.setVisibility(View.INVISIBLE);
        recentView.setVisibility(View.INVISIBLE);
        storageView.setVisibility(View.INVISIBLE);
        settingView.setVisibility(View.INVISIBLE);
        upLayer.setVisibility(View.INVISIBLE);
    }
    
    public void menuBooleanFalse(){
        menuOut = pasteOn = fileLoaderOn = webPageOn = recentOn = storageOn = settingOn = upLayerOn = false;
    }
    
    // UpLayer 클릭 리스너
    
    View.OnClickListener sumListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btnSummary:
				if(SummaryOn){
					summary.setText("요약하기");
					
					if(pasteOn)
						pasteContents.setText(beforeSummary);
					else if(webPageOn)
						pasteContents.setText(beforeSummary);
					
				}
				else{
					summary.setText("원문보기");
					
					if(pasteOn)
						pasteContents.setText(afterSummary);
					else if(webPageOn)
						pasteContents.setText(afterSummary);
				}
				
				SummaryOn = !SummaryOn;
				break;
			case R.id.btnTextSizeUp:
				if(pasteOn){
					if(textSizeCount[0] < 3){	
						switch(textSizeCount[0]){
						case 0:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(), "더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[0]++;
					}
				}
				else if(webPageOn){
					if(textSizeCount[2] < 3){	
						switch(textSizeCount[2]){
						case 0:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(), "더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[2]++;
					}
				}
				break;
			case R.id.btnTextSizeDown:
				if(pasteOn){
					if(textSizeCount[0] > 0){	
						switch(textSizeCount[0]){
						case 1:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
							break;
						case 2:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 3:
							pasteContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						default:
							Toast.makeText(getApplicationContext(), "더이상 줄일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[0]--;
					}
				}
				else if(webPageOn){
					if(textSizeCount[2] < 3){	
						switch(textSizeCount[2]){
						case 0:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
							break;
						case 1:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
							break;
						case 2:
							webContents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
							break;
						default:
							Toast.makeText(getApplicationContext(), "더이상 늘일 수 없습니다.", Toast.LENGTH_SHORT);
							break;
						}
						textSizeCount[2]++;
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
		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			// TODO Auto-generated method stub
			indexView.setVisibility(View.VISIBLE);
			VisibilityInvisible();
			menuBooleanFalse();
			
			switch(pos){
			case 0:	// 붙여넣기
				indexView.setVisibility(View.INVISIBLE);
				pasteView.setVisibility(View.VISIBLE);
		        upLayer.setVisibility(View.VISIBLE);
				pasteOn = upLayerOn = true;
				
				ClipboardManager clipboard = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = clipboard.getPrimaryClip();
				
				beforeSummary = clip.getItemAt(0).coerceToText(getApplicationContext()).toString();
				pasteContents.setText(beforeSummary);
								
				break;
			case 1:	// 파일탐색기
				indexView.setVisibility(View.INVISIBLE);
				fileLoaderView.setVisibility(View.VISIBLE);
				fileLoaderOn = true;
				break;
			case 2:	// 웹페이지
				View layout = inflater.inflate(R.layout.html_dialog, null);
				final EditText htmlEdit = (EditText)layout.findViewById(R.id.htmlEdit);
				
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
				indexView.setVisibility(View.INVISIBLE);
				recentView.setVisibility(View.VISIBLE);
				recentOn = true;
				break;
			case 4:	// 보관함
				indexView.setVisibility(View.INVISIBLE);
				storageView.setVisibility(View.VISIBLE);
				storageOn = true;
				break;
			case 5:	// 설정
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

        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menuView, ImageView btnSlide) {
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
}
