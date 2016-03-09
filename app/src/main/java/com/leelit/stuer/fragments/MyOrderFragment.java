package com.leelit.stuer.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leelit.stuer.bean.BaseInfo;
import com.leelit.stuer.utils.PhoneInfoUtils;
import com.leelit.stuer.viewinterface.IMyOrderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leelit on 2016/3/2.
 */
public abstract class MyOrderFragment extends BaseListFragment implements IMyOrderView {

    protected List<List<? extends BaseInfo>> mList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    protected abstract void finishThisOrder(BaseInfo rightInfo, int position);

    protected abstract void quitThisOrder(BaseInfo rightInfo, int position);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onItemClickEvent(View view, int position) {
        List<? extends BaseInfo> relativeInfos = mList.get(position);
        BaseInfo rightInfo = null;
        for (int i = 0; i < relativeInfos.size(); i++) {
            BaseInfo current = relativeInfos.get(i);
            if (current.getImei().equals(PhoneInfoUtils.getImei())) {
                rightInfo = current;
            }
        }
        if (rightInfo != null) {
            if (rightInfo.getFlag().equals("host")) {
                finishThisOrder(rightInfo, position);
            } else {
                quitThisOrder(rightInfo, position);
            }
        }
    }


    @Override
    public void notRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showInfos(List<List<? extends BaseInfo>> lists) {
        mList.clear();
        mList.addAll(lists);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void noInfos() {
        toast("没有数据...");
    }

    @Override
    public void netError() {
        toast("网络异常，请稍后再试...");
    }

    @Override
    public void showDeleteProgressDialog(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void dismissDeleteProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void deleteOrder(int position) {
        mAdapter.removeData(position);
    }
}
