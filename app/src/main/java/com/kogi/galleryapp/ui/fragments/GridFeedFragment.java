package com.kogi.galleryapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogi.galleryapp.R;
import com.kogi.galleryapp.Utils;
import com.kogi.galleryapp.domain.entities.Feed;
import com.kogi.galleryapp.ui.fragments.adapters.GridRecyclerViewAdapter;
import com.kogi.galleryapp.ui.listeners.OnFragmentInteractionListener;
import com.kogi.galleryapp.ui.listeners.OnGridFragmentInteractionListener;

import java.util.List;

/**
 * Fragment para la visualizacion del listado de feed. Implementa SwipeRefreshLayout.OnRefreshListener
 * que notifica a la actividad del evento de refrescar.
 *
 * Como vista de item se tiene implementado CardView, por lo que el RecyclerView solo acepta
 * StaggeredGridLayoutManager y LinearLayoutManager para su visualizacion. En caso de que se desee
 * ver como GridLayoutManager se debe cambiar esta vista.
 *
 */
public class GridFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OnGridFragmentInteractionListener mGridListener;
    private OnFragmentInteractionListener mInteractionListener;
    private RecyclerView mRecyclerView;
    private GridRecyclerViewAdapter mGridRecyclerViewAdapter;
    private StaggeredGridLayoutManager mGridLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Feed> mFeed;

    public GridFeedFragment() {
    }

    public void notifyDataSetChanged(int newDataLength) {
        mGridRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Transporta la lista a la posicion deseada.
     */
    public void showFeed(int position) {
        if (mGridLayoutManager != null) {
            int positionTemp = mGridRecyclerViewAdapter.selectedPosition;
            mGridRecyclerViewAdapter.selectedPosition = position;
            mGridRecyclerViewAdapter.notifyItemChanged(positionTemp);
            mGridRecyclerViewAdapter.notifyItemChanged(position);

            mGridLayoutManager.scrollToPosition(position);
            mGridLayoutManager.smoothScrollToPosition(mRecyclerView,
                    new RecyclerView.State(), position);
        }
    }

    public static GridFeedFragment newInstance(List<Feed> feed) {
        GridFeedFragment fragment = new GridFeedFragment();
        fragment.setArguments(Utils.getListFeedBundle(feed, 0));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mFeed = getArguments().getParcelableArrayList(Utils.FEED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_grid, container, false);

        if (view instanceof SwipeRefreshLayout) {
            mGridRecyclerViewAdapter = new GridRecyclerViewAdapter(mFeed, mInteractionListener);
            mGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.setAdapter(mGridRecyclerViewAdapter);
            mRecyclerView.setHasFixedSize(true);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view;
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGridFragmentInteractionListener && context instanceof OnFragmentInteractionListener) {
            mGridListener = (OnGridFragmentInteractionListener) context;
            mInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGridFragmentInteractionListener & OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGridListener = null;
    }

    @Override
    public void onRefresh() {
        mGridListener.onRefreshGrid();
    }

    public void setRefreshLayout(boolean refresh) {
        mSwipeRefreshLayout.setRefreshing(refresh);
    }

}
