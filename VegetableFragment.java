package com.snxy.app.merchant_manager.module.view.goods.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.snxy.app.merchant_manager.AppConstant;
import com.snxy.app.merchant_manager.R;
import com.snxy.app.merchant_manager.base.BaseFragment;
import com.snxy.app.merchant_manager.module.adapter.goods.RightRecyclerAdapter;
import com.snxy.app.merchant_manager.module.adapter.goods.SearchAdapter;
import com.snxy.app.merchant_manager.module.bean.goods.CateListPojo;
import com.snxy.app.merchant_manager.module.bean.goods.RespBusinessCategoryList;
import com.snxy.app.merchant_manager.module.bean.goods.RespBusinessTwoList;
import com.snxy.app.merchant_manager.module.bean.goods.RespGoodsSuccess;
import com.snxy.app.merchant_manager.module.bean.goods.RespSearchGoodsList;
import com.snxy.app.merchant_manager.module.modle.goods.GoodsModel;
import com.snxy.app.merchant_manager.module.modle.goods.GroupModel;
import com.snxy.app.merchant_manager.module.presenter.goods.GoodsPresenter;
import com.snxy.app.merchant_manager.module.presenter.goods.GroupPresenter;
import com.snxy.app.merchant_manager.net.OnNetworkStatus;
import com.snxy.app.merchant_manager.net.error.ErrorBody;
import com.snxy.app.merchant_manager.utils.RecyclerManagerUtils;
import com.snxy.app.merchant_manager.utils.RefreshUtils;
import com.snxy.app.merchant_manager.utils.SharedUtils;
import com.snxy.app.merchant_manager.utils.TransformUtils;
import com.snxy.app.merchant_manager.widget.MyCommonDialog;
import com.snxy.app.merchant_manager.widget.SpinerPopWindow;
import com.snxy.app.merchant_manager.widget.SpinerPopWindoww;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * @author: Coco
 * @date: On 2019/1/12
 * @desc: merchant_manager2
 */
public class VegetableFragment extends BaseFragment implements GoodView {
    public static final String TAG = "MyFragment";
    public static final String CATENAME = "CATENAME";
    private String cateId;
    private GoodsPresenter presenter;
    private String token;
    private RecyclerView recyclerView;
    private RightRecyclerAdapter adapter;
    private MyCommonDialog dialog;
    private List<RespBusinessTwoList.DataBeanX.DataBean> dataBeanList;
    private MyCommonDialog deleteDialog;
    List<RespSearchGoodsList.DataBeanX.DataBean> beanList;
    private SmartRefreshLayout smartRefresh;
    private Map<String, RequestBody> vegetableMap;
    private Map<String, RequestBody> searchMap;
    private SearchAdapter searchAdapter;
    /**
     * 是否是搜索状态，默认为非搜索
     */
    private boolean isSearch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_vegetables;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners(Bundle savedInstanceState) {

    }

    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
        smartRefresh = view.findViewById(R.id.smartRefresh);
        recyclerView.setLayoutManager(RecyclerManagerUtils.gridLayoutManager(getActivity(), 2));
        token = SharedUtils.getString(context.getApplicationContext(), AppConstant.TOKEN, "");
        initPresenter();

        //得到数据
        cateId = getArguments().getString(TAG);
        vegetableMap = new HashMap<>();
        vegetableMap.put("token", TransformUtils.convertToRequestBody(token));
        if (null != cateId) {
            vegetableMap.put("Id", TransformUtils.convertToRequestBody(cateId));
        } else {
            vegetableMap.put("Id", TransformUtils.convertToRequestBody(""));
        }
        vegetableMap.put("pageNumber", TransformUtils.convertToRequestBody("" + currentPage));
        vegetableMap.put("pageSize", TransformUtils.convertToRequestBody("" + 10));
        //刷新数据
        initRefresh();
    }

    int currentPage = 1;

    private void initRefresh() {
        //非搜索列表刷新，搜索状态为false
        presenter.getVegetableList(vegetableMap);
        smartRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isSearch = false;
                ++currentPage;
                presenter.getVegetableList(vegetableMap);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isSearch = false;
                currentPage = 1;
                presenter.getVegetableList(vegetableMap);
            }
        });
    }

    private void initPresenter() {
        presenter = new GoodsPresenter(new GoodsModel(), this);
    }

    @Override
    public void getCategoryListSuccess(RespBusinessCategoryList respBusinessCategoryList) {

    }

    ImageView deleteImg;

    @Override
    public void getVegetableListSuccess(RespBusinessTwoList respBusinessTwoList) {
        //判断是否时删除状态，如果是，删除显示，反之则不显示，需要判断搜索状态？
        finishRefresh();
        if (respBusinessTwoList.isResult()) {
            if (isDelete) {
                dataBeanList = respBusinessTwoList.getData().getData();
                if (null != dataBeanList && dataBeanList.size() != 0) {
                    adapter = new RightRecyclerAdapter(getActivity(), R.layout.item_right, dataBeanList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.VISIBLE);
                    });
                    initItemListener();
                }
            } else {
                dataBeanList = respBusinessTwoList.getData().getData();
                if (null != dataBeanList && dataBeanList.size() != 0) {
                    adapter = new RightRecyclerAdapter(getActivity(), R.layout.item_right, dataBeanList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.GONE);
                    });
                    initItemListener();
                }
            }

        } else {
            showShortToast(respBusinessTwoList.getMsg());
        }
    }

    private void finishRefresh() {
        smartRefresh.finishRefresh();
        smartRefresh.finishLoadmore();
    }

    private void initItemListener() {
        adapter.setOnItemClickListener((position, vegeId) -> {
            String vegeName = dataBeanList.get(position).getVegeName();
            String privace = dataBeanList.get(position).getPrivace();
            String vegeId1 = dataBeanList.get(position).getVegeId();
            String unit = dataBeanList.get(position).getUnit();
            String cateName = dataBeanList.get(position).getCateName();
            if (isDelete) {
                initDeleteGoodsDialog(vegeId1, vegeName);
            } else {
                initGoodInfoDialog(vegeName, privace, unit, vegeId1, cateName);
            }

        });

    }

    private void initSearchListener() {
        searchAdapter.setOnItemClickListener((position, vegeId) -> {
            String vegeName = beanList.get(position).getVegeName();
            String privace = beanList.get(position).getPrivace();
            String vegeId1 = beanList.get(position).getVegeId();
            String unit = beanList.get(position).getUnit();
            String cateName = beanList.get(position).getCateName();
            if (isDelete) {
                initDeleteGoodsDialog(vegeId1, vegeName);
            } else {
                initGoodInfoDialog(vegeName, privace, unit, vegeId1, cateName);
            }

        });

    }

    private void initDeleteGoodsDialog(String vegeId, String vegeName) {
        deleteDialog = new MyCommonDialog(getActivity(), AppConstant.DELETEGOODS, vegeName);
        deleteDialog.setPositive("确定")
                .setTitle("")
                .setMessage(vegeName + "”")
                .setOnClickBottomListener(new MyCommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(String goodName, String price, String unit, String groupName, String id) {

                    }

                    @Override
                    public void onPositiveClick(String name) {

                    }

                    @Override
                    public void onPositiveClick() {
                        Map<String, RequestBody> deleteGoodMap = new HashMap<>();
                        deleteGoodMap.put("token", TransformUtils.convertToRequestBody(token));
                        deleteGoodMap.put("merchantVegetableId", TransformUtils.convertToRequestBody(vegeId));
                        presenter.deleteGood(deleteGoodMap);
                        adapter.showDelete();
                    }

                    @Override
                    public void onNegtiveClick() {
                        deleteDialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void getSearchVegetableListSuccess(RespSearchGoodsList respSearchGoodsList) {
        //搜索列表数据展示，如果是删除状态，如果是显示删除，反之不显示，需要双重判断？
        finishRefresh();
        if (respSearchGoodsList.isResult()) {
            if (null != respSearchGoodsList.getData()) {
                beanList = respSearchGoodsList.getData().getData();
                if (null != beanList) {
                    if (isDelete) {
                        searchAdapter = new SearchAdapter(getActivity(), R.layout.item_right, beanList);
                        recyclerView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        searchAdapter.setAdapterView((position, view) -> {
                            deleteImg = (ImageView) view;
                            deleteImg.setVisibility(View.VISIBLE);
                        });
                    } else {
                        searchAdapter = new SearchAdapter(getActivity(), R.layout.item_right, beanList);
                        recyclerView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                        searchAdapter.setAdapterView((position, view) -> {
                            deleteImg = (ImageView) view;
                            deleteImg.setVisibility(View.GONE);
                        });
                    }

                    initSearchListener();
                }
            }
        } else {
            showShortToast(respSearchGoodsList.getMsg());
        }
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
        if (respGoodsSuccess.isResult()) {
            deleteDialog.dismiss();
            if (isDelete) {
                if (isSearch) {
                    initSearchRefresh();
                } else {
                    initRefresh();
                }
            } else {
                if (isSearch) {
                    initSearchRefresh();
                } else {
                    initRefresh();
                }
            }
        } else {
            showShortToast(respGoodsSuccess.getMsg());
        }
    }

    @Override
    public void updateGoodSuccess(RespGoodsSuccess respGoodsSuccess) {
        if (respGoodsSuccess.isResult()) {
            dialog.dismiss();
            initRefresh();
        } else {
            showShortToast(respGoodsSuccess.getMsg());
            dialog.dismiss();
        }
    }

    @Override
    public void showError(ErrorBody errorBody) {
        finishRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchGoodsEvent(String searchName) {
        isSearch = true;
        searchMap = new HashMap<>();
        searchMap.put("token", TransformUtils.convertToRequestBody(token));
        searchMap.put("pageNumber", TransformUtils.convertToRequestBody("" + currentPage));
        searchMap.put("pageSize", TransformUtils.convertToRequestBody("" + 10));
        searchMap.put("searchName", TransformUtils.convertToRequestBody(searchName));
        initSearchRefresh();

    }

    private void initSearchRefresh() {
        //搜索时列表刷新，搜索状态为true
        presenter.getSearchVegetableList(searchMap);
        smartRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isSearch = true;
                ++currentPage;
                presenter.getSearchVegetableList(searchMap);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isSearch = true;
                currentPage = 1;
                presenter.getSearchVegetableList(searchMap);

            }
        });
    }

    /**
     * {@link MyCategroyFragment#onClick(View)}
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteGoodsEvent(DeleteEvent event) {
        this.isDelete = event.isDelete;
        if (isDelete) {
            if (isSearch) {
                //搜索时的删除，删除显示
                if (null != beanList && beanList.size() != 0) {
                    searchAdapter = new SearchAdapter(getActivity(), R.layout.item_right, beanList);
                    recyclerView.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.VISIBLE);
                    });
                    initSearchListener();
                }

            } else {
                //非搜索时的删除，删除显示
                if (null != dataBeanList && dataBeanList.size() != 0) {
                    adapter = new RightRecyclerAdapter(getActivity(), R.layout.item_right, dataBeanList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.VISIBLE);
                    });
                    initItemListener();
                }
            }

        } else {
            if (isSearch) {
                //非删除时的搜索，删除不显示
                if (null != beanList && beanList.size() != 0) {
                    searchAdapter = new SearchAdapter(getActivity(), R.layout.item_right, beanList);
                    recyclerView.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.GONE);
                    });
                    initSearchListener();
                }
            } else {
                //非删除时的非搜索，删除不显示
                if (null != dataBeanList && dataBeanList.size() != 0) {
                    adapter = new RightRecyclerAdapter(getActivity(), R.layout.item_right, dataBeanList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter.setAdapterView((position, view) -> {
                        deleteImg = (ImageView) view;
                        deleteImg.setVisibility(View.GONE);
                    });
                    initItemListener();
                }
            }
        }
    }

    boolean isDelete = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

    }


    private void initGoodInfoDialog(String goodName, String price, String unit, String
            vegeId, String cateName) {
        dialog = new MyCommonDialog(getActivity(), AppConstant.GOODSINFO, goodName, price, unit, cateName);
        dialog.setPositive("确定")
                .setTitle("商品详情")
                .setOnClickBottomListener(new MyCommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick(String goodName, String price, String unit, String groupName, String id) {
                        Map<String, RequestBody> addGoodMap = new HashMap<>();
                        addGoodMap.put("token", TransformUtils.convertToRequestBody(token));
                        addGoodMap.put("vegetableName", TransformUtils.convertToRequestBody(goodName));
                        addGoodMap.put("merchantVegetableCategoryId", TransformUtils.convertToRequestBody(id));
                        addGoodMap.put("id", TransformUtils.convertToRequestBody(vegeId));
                        addGoodMap.put("price", TransformUtils.convertToRequestBody(price));
                        addGoodMap.put("unit", TransformUtils.convertToRequestBody(unit));
                        presenter.updateGood(addGoodMap);
                    }

                    @Override
                    public void onPositiveClick(String name) {
                    }

                    @Override
                    public void onPositiveClick() {

                    }

                    @Override
                    public void onNegtiveClick() {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
