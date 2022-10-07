package com.example.frontendavanzado.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.frontendavanzado.datasource.localSource.Database
import com.example.frontendavanzado.adapters.CharactersAdapter
import com.example.frontendavanzado.R
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.localStorage.DataStorage
import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Character
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
    private lateinit var resultsList: MutableList<Result>
    private val charactersList: MutableList<Character> = mutableListOf()
    private lateinit var toolbar: MaterialToolbar
    private lateinit var dataStore: DataStorage
    private lateinit var database: Database

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView_charactersFragment)
        toolbar = requireActivity().findViewById(R.id.toolbar_mainActivity)
        dataStore = DataStorage(requireContext())
        database = Room.databaseBuilder(
            requireContext(),
            Database::class.java,
            "dbname"
        ).build()

        getCharacters()
        setListeners()
    }

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
                        database.characterDao().deleteAll()
                        CoroutineScope(Dispatchers.Main).launch {
                            requireView().findNavController().navigate(R.id.action_charactersFragment_to_loginFragment)
                        }
                    }
                    true
                }
                R.id.menu_item_sync ->{
                    CoroutineScope(Dispatchers.IO).launch {
                        database.characterDao().deleteAll()
                    }
                    getDataApi()
                    true
                }

                else -> true
            }
        }
    }

    private fun getCharacters(){
        CoroutineScope(Dispatchers.IO).launch {
            val characters = database.characterDao().getCharacters()
            if(characters.isEmpty())
                getDataApi()
            else {
                charactersList.clear()
                charactersList.addAll(characters)
                CoroutineScope(Dispatchers.Main).launch {
                    setupRecycler()
                }
            }
        }
    }

    private fun getDataApi() {
        charactersList.clear()
        RetrofitInstance.api.getCharacters().enqueue(object : Callback<AllAssetsResponse> {
            override fun onResponse(
                call: Call<AllAssetsResponse>,
                response: Response<AllAssetsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    resultsList = response.body()!!.results.toMutableList()
                    setDataRoom()
                }
            }

            override fun onFailure(call: Call<AllAssetsResponse>, t: Throwable) {
                println("Error")
            }
        })
    }

    private fun setDataRoom() {
        for (result in resultsList)
            charactersList.add (
                Character(
                id = result.id,
                image = result.image,
                name = result.name,
                gender = result.gender,
                origin = result.origin.name,
                species = result.species,
                status = result.status,
                episodesAppearance = result.episode.size
                )
            )
        for (character in charactersList)
            CoroutineScope(Dispatchers.IO).launch {
                database.characterDao().insert(character)
        }
    }

    override fun onCharacterItemClicked(character: Character, position: Int) {
        val action = CharactersFragmentDirections.actionCharactersFragmentToDetailsFragment(
            character.id
        )
        requireView().findNavController().navigate(action)
    }

    private fun setupRecycler(){
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = CharactersAdapter(charactersList, this@CharactersFragment)
    }
}