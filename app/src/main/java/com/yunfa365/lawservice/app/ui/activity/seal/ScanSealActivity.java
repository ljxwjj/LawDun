package com.yunfa365.lawservice.app.ui.activity.seal;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.android.agnetty.utils.LogUtil;
import com.baihe.bhsdk.util.BleHelper;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.yunfa365.lawservice.app.R;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpFormFuture;
import com.yunfa365.lawservice.app.pojo.BhSeal;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.ui.activity.base.BaseUserActivity;
import com.yunfa365.lawservice.app.ui.view.holder.EmptyViewHolder;
import com.yunfa365.lawservice.app.utils.ScreenUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_scan_seal)
public class ScanSealActivity extends BaseUserActivity {
    private final int REQUEST_CODE_ENABLE_BLE = 2;

    @ViewById(R.id.base_id_back)
    View mBackView;

    @ViewById(R.id.base_id_title)
    TextView mTitleTxt;

    @ViewById(R.id.base_right_btn)
    ImageView mRightImage;

    @ViewById(R.id.base_right_txt)
    TextView mRightTxt;

    @ViewById
    RecyclerView listView;
    MyAdapter mAdapter;
    List<ScanResult> mData = new ArrayList<>();

    @Extra
    int action;  // 1: 添加印章  2: 启动印章

    private List<BhSeal> allData;
    private List<ScanResult> scanResultsList = new ArrayList<>();
    private List<String> scanMacList = new ArrayList<>();
    private boolean bleCanCan = false;
    private boolean isBleOnScan = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (bleCanCan) {
            if (!isBleOnScan) startScan();
        } else {
            //获取定位权限
            initCheck();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBleOnScan) stopScan();
    }

    @AfterViews
    void init() {
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTxt.setText("添加印章");

        mData = new ArrayList();
        mAdapter = new MyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.addItemDecoration(new DividerItemDecoration(1));
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(mAdapter);

        loadData();
    }

    /**
     * 动态获取定位权限，Android6.0以上操作蓝牙需要动态获取系统定位权限
     */
    private void initCheck() {
        /*if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }*/
        //请求打开蓝牙
        Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //请求开启蓝牙
        this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_ENABLE_BLE);
    }

    @OnActivityResult(REQUEST_CODE_ENABLE_BLE)
    void enableBleOnResult(int result, Intent data) {
        if (result == RESULT_OK) {
            bleCanCan = true;
            startScan();
        }
    }

    private void startScan() {
        LogUtil.d("startScan ---------------------------");
        isBleOnScan = true;
        //搜索印章设备，参数值传递你的app key和密钥
        BleHelper.getBleHelper(this).startScan(AppCst.BH_SDK_APP_KEY,AppCst.BH_SDK_SECRET).subscribe(
                scanResult -> {
                    String name = scanResult.getBleDevice().getName();
                    String mac = scanResult.getBleDevice().getMacAddress();
                    LogUtil.d("onScanResult =======================\n "+ "name:" + scanResult.getBleDevice().getName() + "   mac:" + scanResult.getBleDevice().getMacAddress());
//                        mList.add("name:" + scanResult.getBleDevice().getName() + "   mac:" + scanResult.getBleDevice().getMacAddress());
                    if (!scanMacList.contains(mac)) {
                        scanMacList.add(mac);
                        scanResultsList.add(scanResult);
                        filterData();
                    }
                }
        );
    }

    private void stopScan() {
        isBleOnScan = false;
        BleHelper.getBleHelper(this).stopScan();
    }

    private void loadData() {
        AppRequest request = new AppRequest.Build("api/WebSet/Zhang_list")
                .create();
        new HttpFormFuture.Builder(this)
                .setData(request)
                .setListener(new AgnettyFutureListener(){
                    @Override
                    public void onStart(AgnettyResult result) {
                        showLoading();
                    }

                    @Override
                    public void onComplete(AgnettyResult result) {
                        hideLoading();
                        AppResponse resp = (AppResponse)result.getAttach();
                        if (resp.flag) {
                            List<BhSeal> list = resp.resultsToList(BhSeal.class);
                            if (list == null) {
                                allData = new ArrayList<>();
                            } else {
                                allData = list;
                            }
                            filterData();
                        } else {
                            showToast(resp.Message);
                        }
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        hideLoading();
                        result.getException().printStackTrace();
                    }
                })
                .execute();
    }

    private void filterData() {
        mData.clear();
        if (allData == null) {

        } else if (action == 1) {
            for (ScanResult item : scanResultsList) {
                String itemMac = item.getBleDevice().getMacAddress();
                boolean addItem = true;
                for (BhSeal seal : allData) {
                    if (seal.mac.equals(itemMac)) {
                        addItem = false;
                        break;
                    }
                }
                if (addItem) mData.add(item);
            }
        } else if (action == 2){
            if (allData == null || allData.isEmpty() || scanResultsList.isEmpty()) {

            } else {
                for (BhSeal item : allData) {

                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int ITEM_TYPE_HEADER = 0;
        static final int ITEM_TYPE_CONTENT = 1;
        static final int ITEM_TYPE_FOOTER = 2;
        static final int ITEM_TYPE_EMPTY = 3;

        private int mHeaderCount = 0;
        private int mFooterCount = 0;

        public int getContentItemCount() {
            return mData.size();
        }

        public int getFooterCount() {
            return mFooterCount;
        }

        public boolean isHeaderView(int position) {
            return mHeaderCount > 0 && position < mHeaderCount;
        }

        public boolean isFooterView(int position) {
            return mFooterCount > 0 && position >= (mHeaderCount + getContentItemCount());
        }

        public boolean isLastContentView(int position) {
            return position + 1 == mHeaderCount + getContentItemCount();
        }

        public boolean isEmptyView(int position) {
            return isEmpty() && position == 0;
        }

        public boolean isEmpty() {
            return mData == null || mData.isEmpty();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return null;
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return null;
            } else if (viewType == ITEM_TYPE_CONTENT){
                View view = View.inflate(ScanSealActivity.this, R.layout.item_scan_seal_list, null);
                ContentViewHolder viewHolder = new ContentViewHolder(view);
                return viewHolder;
            } else if (viewType == ITEM_TYPE_EMPTY) {
                View view = View.inflate(ScanSealActivity.this, R.layout.common_list_item_empty, null);
                return new EmptyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContentViewHolder) {
                ScanResult item = getItem(position);
                String name = item.getBleDevice().getName();
                String mac = item.getBleDevice().getMacAddress();
                ContentViewHolder itemViewHolder = (ContentViewHolder) holder;
                itemViewHolder.text.setText(String.format("%s->%s", name, mac));
                itemViewHolder.item = item;
            }
        }

        @Override
        public int getItemCount() {
            return mHeaderCount + getContentItemCount() + getFooterCount();
        }

        private ScanResult getItem(int position) {
            return mData.get(position - mHeaderCount);
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeaderView(position)) {
                return ITEM_TYPE_HEADER;
            } else if (isFooterView(position)) {
                return ITEM_TYPE_FOOTER;
            } else {
                return ITEM_TYPE_CONTENT;
            }
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ScanResult item;
        TextView text;

        public ContentViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SealAddActivity_.intent(ScanSealActivity.this).startForResult(0);
        }
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int dividerHeight;
        private Drawable mDivider;

        public DividerItemDecoration(float dividerHeight) {
            this.dividerHeight = ScreenUtil.dip2px(dividerHeight);
            mDivider = getResources().getDrawable(R.color.status_color);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (mAdapter.isHeaderView(position)) {

            } else if (mAdapter.isFooterView(position)) {

            } else {
                outRect.bottom = dividerHeight;//类似加了一个bottom padding
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            final int left = 0;
            final int right = parent.getWidth() - left;

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int top = child.getBottom();

                if (mAdapter.isHeaderView(i)) {

                } else if (mAdapter.isFooterView(i)) {

                } else {
                    mDivider.setBounds(left, top, right, top+dividerHeight);
                    mDivider.draw(c);
                }
            }
        }
    }
}
