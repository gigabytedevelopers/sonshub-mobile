package com.gigabytedevelopersinc.apps.sonshub.utils;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Tuesday, 02
 * Month: June
 * Year: 2020
 * Date: 02 Jun, 2020
 * Time: 8:39 AM
 * Desc: RecyclerViewEmptyObserver
 **/
public class RecyclerViewEmptyObserver extends RecyclerView.AdapterDataObserver {
    private View emptyView;
    private RecyclerView recyclerView;

    public RecyclerViewEmptyObserver(RecyclerView rv, View ev) {
        this.recyclerView = rv;
        this.emptyView    = ev;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    public void onChanged() { checkIfEmpty(); }
    public void onItemRangeInserted(int positionStart, int itemCount) { checkIfEmpty(); }
    public void onItemRangeRemoved(int positionStart, int itemCount) { checkIfEmpty(); }
}