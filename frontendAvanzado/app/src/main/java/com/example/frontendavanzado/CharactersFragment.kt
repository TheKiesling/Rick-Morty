package com.example.frontendavanzado

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class CharactersFragment : Fragment(R.layout.fragment_characters), CharactersAdapter.CharacterItemListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var charactersList: MutableList<Character>
    private lateinit var toolbar: MaterialToolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView_charactersFragment)
        toolbar = requireActivity().findViewById(R.id.toolbar_mainActivity)

        setupRecycler()
        setListeners()
    }

    override fun onCharacterItemClicked(character: Character, position: Int) {
        val action = CharactersFragmentDirections.actionCharactersFragmentToDetailsFragment(
            character.name,
            character.species,
            character.status,
            character.image,
            character.gender
        )
        requireView().findNavController().navigate(action)
    }

    private fun setupRecycler(){
        charactersList = RickAndMortyDB.getCharacters()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = CharactersAdapter(charactersList, this)
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