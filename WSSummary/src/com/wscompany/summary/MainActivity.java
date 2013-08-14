
package com.wscompany.summary;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wscompany.summary.MyHorizontalScrollView.SizeCallback;

/**
 * This demo uses a custom HorizontalScrollView that ignores tosuch events, and therefore does NOT allow manual scrolling.
 * 
 * The only scrolling allowed is scrolling in code triggered by the menu button.
 * 
 * When the button is pressed, both the menu and the app will scroll. So the menu isn't revealed from beneath the app, it
 * adjoins the app and moves with the app.
 */
public class MainActivity extends Activity {
	
	// Layout Variables //
    MyHorizontalScrollView scrollView;
    View menuView, contentsView;
    ListView menuListView;
    
    String [] menu_items = {"붙여넣기","파일탐색기","웹페이지","최근기록","보관함","설정"};
    ImageView btnSlide;
    
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;
    
    // Summary Variables //
    private String beforeSummary = "", afterSummary ="", titleSummary = "";
    
    // Webpage Variables //
	DownThread mThread;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.activity_main, null);
        setContentView(scrollView);

        menuView= inflater.inflate(R.layout.left_menu, null);
        contentsView = inflater.inflate(R.layout.right_contents, null);
        ViewGroup tabBar = (ViewGroup) contentsView.findViewById(R.id.tabBar);
        
        // Menu
        menuListView = (ListView) menuView.findViewById(R.id.menuList);
        menuListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu_items));
        menuListView.setOnItemClickListener(itemListener);
                
        btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
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
			switch(pos){
			case 0:	// 붙여넣기
				
				break;
			case 1:	// 파일탐색기
				break;
			case 2:	// 웹페이지
				break;
			case 3:	// 최근기록
				break;
			case 4:	// 보관함
				break;
			case 5:	// 설정
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
//				text.setText(titleSummary+"\n\n"+afterSummary);
//		    		mProgress.dismiss();
	    	}
		};
	}
}
