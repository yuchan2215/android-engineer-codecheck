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

class SearchFragment: Fragment(R.layout.search_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val binding= SearchFragmentBinding.bind(view)

        val viewModel= SearchFragmentViewModel()

        val layoutManager= LinearLayoutManager(requireContext())
        val dividerItemDecoration=
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        val adapter= GitRepositoryListAdapter(object : GitRepositoryListAdapter.OnItemClickListener{
            override fun itemClick(item: GitRepository){
                gotoRepositoryFragment(item)
            }
        })

        binding.searchInputText
            .setOnEditorActionListener{ editText, action, _ ->
                if (action== EditorInfo.IME_ACTION_SEARCH){
                    editText.text.toString().let {
                        viewModel.searchResults(it).apply{
                            adapter.submitList(this)
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        binding.recyclerView.also{
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

val diff_util= object: DiffUtil.ItemCallback<GitRepository>(){
    override fun areItemsTheSame(oldItem: GitRepository, newItem: GitRepository): Boolean
    {
        return oldItem.name== newItem.name
    }

    override fun areContentsTheSame(oldItem: GitRepository, newItem: GitRepository): Boolean
    {
        return oldItem== newItem
    }

}

class GitRepositoryListAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<GitRepository, GitRepositoryListAdapter.ViewHolder>(diff_util){

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
}