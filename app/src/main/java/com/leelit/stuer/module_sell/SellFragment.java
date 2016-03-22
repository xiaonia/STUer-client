package com.leelit.stuer.module_sell;

import android.view.View;

import com.leelit.stuer.R;
import com.leelit.stuer.base_adapters.BaseListAdapter;
import com.leelit.stuer.base_fragments.BaseListFragment;
import com.leelit.stuer.bean.SellInfo;
import com.leelit.stuer.module_sell.presenter.SellPresenter;
import com.leelit.stuer.module_sell.viewinterface.ISellView;
import com.leelit.stuer.utils.ContactUtils;
import com.leelit.stuer.utils.ProgressDialogUtils;
import com.leelit.stuer.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Leelit on 2016/3/16.
 */
public class SellFragment extends BaseListFragment implements ISellView {

    private SellPresenter mSellPresenter = new SellPresenter(this);
    private List<SellInfo> mList = new ArrayList<>();
    private SellAdapter mSellAdapter;


    @Override
    protected BaseListAdapter bindAdapter() {
        mSellAdapter = new SellAdapter(mList);
        return mSellAdapter;
    }

    @Override
    public void taskAfterLoaded() {
        loadDataFromDb();
    }

    public void loadDataFromDb() {
        mSellPresenter.doLoadDataFromDb();
    }

    @Override
    public void showNoDataFromNet() {
        toast("没有新的数据，请稍后再来...");
    }

    @Override
    public void showNoDataInDb() {
        toast("没有缓存数据，请刷新...");
    }

    @Override
    protected void refreshTask() {
        mSellPresenter.doLoadDataFromNet();
    }

    @Override
    protected void onItemClickEvent(View view, int position) {
        mSellPresenter.doContactSeller(mList.get(position), position);
    }

    @Override
    public void netError() {
        toast(getActivity().getString(R.string.net_error));
    }

    @Override
    public void showDataFromDb(List<SellInfo> sellInfos) {
        mList.clear();
        mList.addAll(sellInfos);
        // 如果不展示下架商品
        checkIfNoShowOfflineSell();
        mSellAdapter.notifyDataSetChanged();
    }

    private void checkIfNoShowOfflineSell() {
        List<SellInfo> offInfos = new ArrayList<>();
        if (SettingUtils.noOfflineSell()) {
            for (SellInfo info : mList) {
                if (info.getStatus().equals("off")) {
                    offInfos.add(info);
                }
            }
        }
        for (SellInfo info : offInfos) {
            mList.remove(info);
        }
    }


    @Override
    public void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void showLoadingDbProgressDialog() {
        ProgressDialogUtils.show(getActivity(), "加载中...");
    }

    @Override
    public void dismissLoadingDbProgressDialog() {
        ProgressDialogUtils.dismiss();
    }

    @Override
    public void showDataFromNet(List<SellInfo> sellInfos) {
        Collections.reverse(mList); // loadFromDb展示后的时间顺序是 4 3 2 1， reverse后 1 2 3 4
        mList.addAll(sellInfos);    // 加入5 6 7 8后变成 1 2 3 4 5 6 7 8
        Collections.reverse(mList); // reverse后 8 7 6 5 4 3 2 1
        checkIfNoShowOfflineSell();
        mAdapter.notifyDataSetChanged();  // 正确的时间顺序
    }


    @Override
    public void showGoodsOffline(int position) {
        toast("该商品已售出...");
        mList.get(position).setStatus("off");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showContactDialog(String tel, String shortel, String wechat) {
        ContactUtils.createContactDialog(getContext(), tel, shortel, wechat).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSellPresenter.doClear();
    }


}
