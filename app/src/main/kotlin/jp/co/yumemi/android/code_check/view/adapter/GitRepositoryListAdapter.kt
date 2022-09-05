package jp.co.yumemi.android.code_check.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.code_check.databinding.GitRepositoryListItemLayoutBinding
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository

/**
 * GitHubリポジトリをリスト表示する時のアダプタ。
 */
abstract class GitRepositoryListAdapter :
    ListAdapter<GitRepository, GitRepositoryListAdapter.ViewHolder>(DIFF_UTIL) {
    abstract fun itemClick(item: GitRepository)

    class ViewHolder(private val binding: GitRepositoryListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repository: GitRepository) {
            binding.repositoryNameView.text = repository.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = GitRepositoryListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.setOnClickListener {
            itemClick(item)
        }
        holder.bind(item)
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
