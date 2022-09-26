package com.example.frontendavanzado.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontendavanzado.adapters.CharactersAdapter
import com.example.frontendavanzado.R
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.localStorage.DataStorage
import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Result
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharactersFragment : Fragment(R.layout.fragment_characters),
    CharactersAdapter.CharacterItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var charactersList: MutableList<Result>
    private lateinit var toolbar: MaterialToolbar
    private lateinit var dataStore: DataStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView_charactersFragment)
        toolbar = requireActivity().findViewById(R.id.toolbar_mainActivity)

        dataStore = DataStorage(requireContext())

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
        val context: CharactersAdapter.CharacterItemListener = this

        RetrofitInstance.api.getCharacters().enqueue(object: Callback<AllAssetsResponse>{
            override fun onResponse(
                call: Call<AllAssetsResponse>,
                response: Response<AllAssetsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    charactersList = response.body()!!.results.toMutableList()
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = CharactersAdapter(charactersList, context)
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
                R.id.menu_item_logout -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.removeKey("email")

                        CoroutineScope(Dispatchers.Main).launch {
                            requireView().findNavController().navigate(R.id.action_charactersFragment_to_loginFragment)
                        }
                    }
                    true
                }
                else -> true
            }
        }
    }
}