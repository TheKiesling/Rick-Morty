package com.example.frontendavanzado

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Result
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharactersFragment : Fragment(R.layout.fragment_characters), CharactersAdapter.CharacterItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var charactersList: MutableList<Result>
    private lateinit var toolbar: MaterialToolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView_charactersFragment)
        toolbar = requireActivity().findViewById(R.id.toolbar_mainActivity)

        setupRecycler()
        setListeners()
    }

    override fun onCharacterItemClicked(character: Result, position: Int) {
        val action = CharactersFragmentDirections.actionCharactersFragmentToDetailsFragment(
            character.id
        )
        requireView().findNavController().navigate(action)
    }

    private fun setupRecycler(){
        RetrofitInstance.api.getCharacters().enqueue(object: Callback<AllAssetsResponse>{
            override fun onResponse(
                call: Call<AllAssetsResponse>,
                response: Response<AllAssetsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    charactersList = response.body()!!.results.toMutableList()
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = CharactersAdapter(charactersList, this@CharactersFragment)
                }
            }

            override fun onFailure(call: Call<AllAssetsResponse>, t: Throwable) {
                println("Error")
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setListeners(){
        toolbar.setOnMenuItemClickListener {menuItem ->

            when(menuItem.itemId){
                R.id.menu_item_sortAZ -> {
                    charactersList.sortBy { elem -> elem.name }
                    recyclerView.adapter!!.notifyDataSetChanged()
                    true
                }
                R.id.menu_item_sortZA -> {
                    charactersList.sortByDescending { elem -> elem.name }
                    recyclerView.adapter!!.notifyDataSetChanged()
                    true

                }
                else -> true
            }
        }
    }
}