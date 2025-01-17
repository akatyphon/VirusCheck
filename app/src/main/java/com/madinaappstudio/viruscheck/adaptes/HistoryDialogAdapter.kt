package com.madinaappstudio.viruscheck.adaptes

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.database.HistoryEntity
import com.madinaappstudio.viruscheck.databinding.DialogHistoryDetailsBinding
import com.madinaappstudio.viruscheck.models.HistoryViewModel
import com.madinaappstudio.viruscheck.utils.showToast
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistoryDialogAdapter(
    private var historyList: List<HistoryEntity>,
    private var viewModel: HistoryViewModel,
    private val listener: OnHistoryClickListener
) :
    RecyclerView.Adapter<HistoryDialogAdapter.ViewModel>() {

    inner class ViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val cvRvHistoryItem: MaterialCardView = view.findViewById(R.id.cvRvHistoryItem)
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
        holder.tvTitle.text = historyList[position].getName()
        val type = historyList[position].getType()
        if (type == "file") {
            holder.ivFileType.setImageResource(R.drawable.ic_file)
        } else {
            holder.ivFileType.setImageResource(R.drawable.ic_url)
        }
        holder.ivMore.setOnClickListener {
            showMenu(holder.itemView.context, it, historyList[position], position)
        }

        holder.cvRvHistoryItem.setOnClickListener {
            if (type == "file") {
                listener.onHistoryClicked(
                    arrayOf(type, historyList[position].getFileSha256()!!)
                )
            } else {
                listener.onHistoryClicked(
                    arrayOf(type, historyList[position].getUrlBase64()!!)
                )
            }
        }

    }

    override fun getItemCount(): Int = historyList.size

    fun updateData(newHistoryList: List<HistoryEntity>) {
        (historyList as MutableList).clear()
        (historyList as MutableList).addAll(newHistoryList)
        notifyDataSetChanged()
    }


    private fun showMenu(context: Context, view: View, historyEntity: HistoryEntity, position: Int) {
        val popup = PopupMenu(context, view)
        popup.menuInflater.inflate(R.menu.history_more_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.btnHistoryMenuDetails -> {
                    showDetailsDialog(context, historyEntity)
                    true
                }
                R.id.btnHistoryMenuDelete -> {
                    (historyList as MutableList).removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, historyList.size)
                    viewModel.deleteHistory(historyEntity)
                    showToast(context, "Delete")
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDetailsDialog(context: Context, entity: HistoryEntity){
        val binding = DialogHistoryDetailsBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context).create().apply {
            setView(binding.root)
            window?.setDimAmount(0.5f)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        if (entity.getType() == "file"){
            binding.ivHistoryInfoIcon.setImageResource(R.drawable.ic_file)
            binding.tvHistoryInfoType.text = "File"
            binding.llHistoryInfoSize.visibility = View.VISIBLE
            binding.tvHistoryInfoFileSize.text = entity.getFileSize()
            binding.tvHistoryInfoUIdLabel.text = "SHA256"
            binding.tvHistoryInfoUId.text = entity.getFileSha256()
        } else {
            binding.ivHistoryInfoIcon.setImageResource(R.drawable.ic_url)
            binding.tvHistoryInfoType.text = "Url"
            binding.tvHistoryInfoUIdLabel.text = "UrlBase64"
            binding.tvHistoryInfoUId.text = entity.getUrlBase64()
        }

        binding.tvHistoryInfoName.text = entity.getName()
        binding.tvHistoryInfoDate.text = formatDate(entity.getDate())
        builder.show()
    }

    private fun formatDate(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss").withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochMilli(millis))
    }

    interface OnHistoryClickListener {
        fun onHistoryClicked(array: Array<String>)
    }

}