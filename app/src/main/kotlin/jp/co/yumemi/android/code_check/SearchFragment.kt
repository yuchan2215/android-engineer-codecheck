/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.SearchFragmentBinding

/**
 * GitHubリポジトリを検索するフラグメント。
 * 検索結果をリストに表示する。
 * リストのアイテムがタップされたら[GitRepositoryDetailFragment]へ推移する。
 */
class SearchFragment: Fragment(R.layout.search_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val binding= SearchFragmentBinding.bind(view)

        val viewModel= SearchFragmentViewModel()

        val adapter= GitRepositoryListAdapter(object : GitRepositoryListAdapter.OnItemClickListener{
            override fun itemClick(item: GitRepository){
                gotoRepositoryFragment(item)
            }
        })

        binding.searchInputText
            .setOnEditorActionListener{ editText, action, _ ->
                if (action != EditorInfo.IME_ACTION_SEARCH)
                    return@setOnEditorActionListener false

                val text = editText.text.toString()
                val searchResults = viewModel.searchResults(text)
                adapter.submitList(searchResults)

                return@setOnEditorActionListener true
            }

        binding.recyclerView.also{

            //項目を線で区切る
            val layoutManager= LinearLayoutManager(requireContext())
            val dividerItemDecoration=
                DividerItemDecoration(requireContext(), layoutManager.orientation)
            it.layoutManager= layoutManager
            it.addItemDecoration(dividerItemDecoration)
            
            it.adapter= adapter
        }
    }

    fun gotoRepositoryFragment(item: GitRepository)
    {
        val action= SearchFragmentDirections
            .openRepositoryDetail(repository = item)
        findNavController().navigate(action)
    }
}

/**
 * GitHubリポジトリをリスト表示する時のアダプタ。
 */
class GitRepositoryListAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<GitRepository, GitRepositoryListAdapter.ViewHolder>(DIFF_UTIL){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener{
    	fun itemClick(item: GitRepository)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
    	val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.git_repository_list_item_layout, parent, false)
    	return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
    	val item= getItem(position)
        holder.itemView.findViewById<TextView>(R.id.repositoryNameView).text=
            item.name

    	holder.itemView.setOnClickListener{
     		itemClickListener.itemClick(item)
    	}
    }

    companion object {
        private val DIFF_UTIL = object: DiffUtil.ItemCallback<GitRepository>(){
            override fun areItemsTheSame(oldItem: GitRepository, newItem: GitRepository): Boolean
            {
                return oldItem.name== newItem.name
            }

            override fun areContentsTheSame(oldItem: GitRepository, newItem: GitRepository): Boolean
            {
                return oldItem== newItem
            }
        }
    }
}
