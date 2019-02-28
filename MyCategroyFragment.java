package com.snxy.app.merchant_manager.module.view.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snxy.app.merchant_manager.AppConstant;
import com.snxy.app.merchant_manager.R;
import com.snxy.app.merchant_manager.base.BaseFragment;
import com.snxy.app.merchant_manager.module.adapter.goods.LeftAdapter;
import com.snxy.app.merchant_manager.module.bean.goods.RespBusinessCategoryList;
import com.snxy.app.merchant_manager.module.bean.goods.RespBusinessTwoList;
import com.snxy.app.merchant_manager.module.bean.goods.RespGoodsSuccess;
import com.snxy.app.merchant_manager.module.bean.goods.RespSearchGoodsList;
import com.snxy.app.merchant_manager.module.modle.goods.GoodsModel;
import com.snxy.app.merchant_manager.module.presenter.goods.GoodsPresenter;
import com.snxy.app.merchant_manager.module.view.goods.add.AddCategroyActivity;
import com.snxy.app.merchant_manager.net.error.ErrorBody;
import com.snxy.app.merchant_manager.utils.SharedUtils;
import com.snxy.app.merchant_manager.utils.TransformUtils;
import com.snxy.app.merchant_manager.widget.MyCommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import retrofit2.http.HEAD;

/**
 * @author: Coco
 * @date: On 2019/1/10
 * @desc: 经营品类界面
 */
public class MyCategroyFragment extends BaseFragment implements /*AdapterView.OnItemClickListener,*/ GoodView {

    @BindView(R.id.mEd_search)
    EditText mEdSearch;
    @BindView(R.id.mTv_search)
    TextView mTvSearch;
    @BindView(R.id.mRl_search_tv)
    RelativeLayout mRlSearchTv;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.mLinear)
    RelativeLayout mLinear;
    @BindView(R.id.mBtn_add)
    RelativeLayout mBtnAdd;
    @BindView(R.id.mBtn_delete)
    RelativeLayout mBtnDelete;
    @BindView(R.id.mBtn_delete_finish)
    RelativeLayout mBtnDeleteFinish;
    @BindView(R.id.mBtn_manager)
    RelativeLayout mBtnManager;
    @BindView(R.id.mBtn_finish)
    Button mBtnFinish;
    @BindView(R.id.mLinear_bottom)
    RelativeLayout mLinearBottom;
    Unbinder unbinder;
    @BindView(R.id.mRl)
    RelativeLayout mRl;
    @BindView(R.id.addGroup)
    TextView addGroup;
    @BindView(R.id.llBottomContainer)
    LinearLayout llBottomContainer;
    @BindView(R.id.rlMask)
    RelativeLayout rlMask;
    @BindView(R.id.leftRl)
    RelativeLayout leftRl;
    @BindView(R.id.mTv_line_bottom)
    TextView mTvLineBottom;
    private LeftAdapter adapter;
    private VegetableFragment myFragment;
    public static int mPosition;
    private String token;
    private List<RespBusinessCategoryList.DataBeanX.CateListBean> cateList;
    private GoodsPresenter presenter;
    private Map<String, RequestBody> map;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_categroy2;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners(Bundle savedInstanceState) {
        mBtnManager.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnDeleteFinish.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
        mRlSearchTv.setOnClickListener(this);
        addGroup.setOnClickListener(this);
        mEdSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    EventBus.getDefault().post("");
                }
            }
        });
    }

    @Override
    protected void initView(View view) {
        presenter = new GoodsPresenter(new GoodsModel(), this);
        token = SharedUtils.getString(getActivity().getApplicationContext(), AppConstant.TOKEN, "");
        map = new HashMap<>();
        map.put("token", TransformUtils.convertToRequestBody(token));
        map.put("pageNumber", TransformUtils.convertToRequestBody(1 + ""));
        map.put("pageSize", TransformUtils.convertToRequestBody(200 + ""));
        presenter.getCategoryList(map);
        initDate();

    }

    private void initDate() {
        //创建MyFragment对象
        myFragment = new VegetableFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        if (null != cateList) {
            bundle.putString(VegetableFragment.TAG, cateList.get(mPosition).getCateId());
            bundle.putString(VegetableFragment.CATENAME, cateList.get(mPosition).getCateName());
        } else {
            bundle.putString(VegetableFragment.TAG, "");
            bundle.putString(VegetableFragment.CATENAME, "全部");
        }
        myFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        cateList.clear();
        mPosition = 0;
    }

    /**
     * {@link VegetableFragment#onDeleteGoodsEvent(DeleteEvent)}
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtn_add:
                startActivity(AddCategroyActivity.class);
                break;
            case R.id.mBtn_delete:
                EventBus.getDefault().post(new DeleteEvent(true));
                llBottomContainer.setVisibility(View.GONE);
                mBtnDeleteFinish.setVisibility(View.VISIBLE);
                rlMask.setVisibility(View.VISIBLE);
                rlMask.setFocusable(true);
                listView.setFocusable(false);
                rlMask.setOnClickListener(v1 -> {
                });
                break;
            case R.id.mBtn_manager:
                Log.e("mmmmm", "onClick: " + mPosition);
                startActivity(ManagerGroupActivity.class);
                break;
            case R.id.mBtn_finish:
                break;
            case R.id.mBtn_delete_finish:
                EventBus.getDefault().post(new DeleteEvent(false));
                llBottomContainer.setVisibility(View.VISIBLE);
                mBtnDeleteFinish.setVisibility(View.GONE);
                rlMask.setVisibility(View.GONE);
                rlMask.setFocusable(false);
                listView.setFocusable(true);
                break;
            case R.id.mRl_search_tv:
                String searchName = mEdSearch.getText().toString().trim();
                if (null != searchName) {
                    if (!"".equals(searchName)) {
                        EventBus.getDefault().post(searchName);
                    }
                } else {
                    EventBus.getDefault().post("");
                }
                break;
            case R.id.addGroup:
                break;
            default:
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        Log.e("------", "onCreateView: ");
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("-------", "onViewCreated: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("-------", "onDestroyView: ");
        unbinder.unbind();
    }

    @Override
    public void getCategoryListSuccess(RespBusinessCategoryList respBusinessCategoryList) {

        if (respBusinessCategoryList.isResult()) {
            cateList = respBusinessCategoryList.getData().getCateList();
            if (null != cateList) {
                listView.setAdapter(null);
                adapter = new LeftAdapter(getActivity(), cateList, false);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    //拿到当前位置
                    mPosition = position;
                    //即使刷新adapter
                    adapter.notifyDataSetChanged();
                    if (null != cateList) {
                        for (int i = 0; i < cateList.size(); i++) {
                            myFragment = new VegetableFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager()
                                    .beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, myFragment);
                            Bundle bundle = new Bundle();
                            bundle.putString(VegetableFragment.TAG, cateList.get(position).getCateId());
                            bundle.putString(VegetableFragment.CATENAME, cateList.get(position).getCateName());
                            myFragment.setArguments(bundle);
                            fragmentTransaction.commit();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void getVegetableListSuccess(RespBusinessTwoList respBusinessTwoList) {

    }

    @Override
    public void getSearchVegetableListSuccess(RespSearchGoodsList respSearchGoodsList) {
    }

    @Override
    public void addGroupSuccess(RespGoodsSuccess respGoodsSuccess) {
    }

    @Override
    public void deleteGroupSuccess(RespGoodsSuccess respGoodsSuccess) {
    }

    @Override
    public void addGoodSuccess(RespGoodsSuccess respGoodsSuccess) {

    }

    @Override
    public void deleteGoodSuccess(RespGoodsSuccess respGoodsSuccess) {

    }

    @Override
    public void updateGoodSuccess(RespGoodsSuccess respGoodsSuccess) {

    }

    @Override
    public void showError(ErrorBody errorBody) {

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getCategoryList(map);
        //创建MyFragment对象
        myFragment = new VegetableFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        bundle.putString(VegetableFragment.TAG, "");
        bundle.putString(VegetableFragment.CATENAME, "全部");
        myFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }

    public void update() {
        String searchName = mEdSearch.getText().toString().trim();
        EventBus.getDefault().post(searchName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
