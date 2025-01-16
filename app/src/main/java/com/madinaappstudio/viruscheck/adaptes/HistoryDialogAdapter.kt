package com.madinaappstudio.viruscheck.adaptes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.utils.showToast

class HistoryDialogAdapter(private val historyList: List<String>) :
    RecyclerView.Adapter<HistoryDialogAdapter.ViewModel>() {

    inner class ViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val ivFileType: ImageView = view.findViewById(R.id.ivHistoryScanType)
        val tvTitle: MaterialTextView = view.findViewById(R.id.tvHistoryTitle)
        val ivMore: ImageView = view.findViewById(R.id.ivHistoryMore)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryDialogAdapter.ViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_history_item, parent, false)
        return ViewModel(view)
    }

    override fun onBindViewHolder(holder: HistoryDialogAdapter.ViewModel, position: Int) {
        holder.tvTitle.text = historyList[position]
        holder.ivMore.setOnClickListener {
            showToast(holder.itemView.context, "More")
        }
    }

    override fun getItemCount(): Int = historyList.size

}