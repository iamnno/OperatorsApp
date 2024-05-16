package com.nkt.operatorsapp.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nkt.operatorsapp.databinding.QueryItemBinding

class QueriesAdapter(
    private val deleteQuery: (String) -> Unit
) : RecyclerView.Adapter<QueriesAdapter.QueriesVH>() {

    var queries = emptyList<String>()

    inner class QueriesVH(
        private val binding: QueryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(query: String) {
            with(binding) {
                queryText.text = query
                deleteQueryButton.setOnClickListener {
                    deleteQuery(query)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueriesVH {
        val binding = QueryItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return QueriesVH(binding)
    }

    override fun getItemCount(): Int {
        return queries.size
    }

    override fun onBindViewHolder(holder: QueriesVH, position: Int) {
        holder.bind(query = queries[position])
    }
}