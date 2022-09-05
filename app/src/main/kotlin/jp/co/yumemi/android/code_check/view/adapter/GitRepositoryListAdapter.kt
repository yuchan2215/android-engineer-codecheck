package jp.co.yumemi.android.code_check.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository

/**
 * GitHubリポジトリをリスト表示する時のアダプタ。
 */
abstract class GitRepositoryListAdapter :
    ListAdapter<GitRepository, GitRepositoryListAdapter.ViewHolder>(DIFF_UTIL) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    abstract fun itemClick(item: GitRepository)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.git_repository_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.repositoryNameView).text =
            item.name

        holder.itemView.setOnClickListener {
            itemClick(item)
        }
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GitRepository>() {
            override fun areItemsTheSame(oldItem: GitRepository, newItem: GitRepository): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: GitRepository,
                newItem: GitRepository
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
