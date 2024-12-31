package com.lollipop.applist.helper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class LoadMoreHelper(
    private val keepCount: Int = 4,
    private val onLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    var enable = true

    fun bind(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!enable) {
            return
        }
        val layoutManager = recyclerView.layoutManager ?: return
        val adapter = recyclerView.adapter ?: return
        val lastVisibleItemPosition = when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPositions.maxOrNull() ?: return
            }

            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
            else -> return
        }
        if (lastVisibleItemPosition >= adapter.itemCount - keepCount) {
            enable = false
            onLoadMore()
        }
    }

}