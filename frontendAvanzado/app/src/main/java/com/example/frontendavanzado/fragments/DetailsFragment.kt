package com.example.frontendavanzado.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.frontendavanzado.R
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.localSource.Database
import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Character
import com.example.frontendavanzado.datasource.model.Origin
import com.example.frontendavanzado.datasource.model.Result
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment : Fragment(R.layout.fragment_details) {
    private val args: DetailsFragmentArgs by navArgs()

    private lateinit var image: ImageView
    private lateinit var inputName:TextInputLayout
    private lateinit var inputSpecie:TextInputLayout
    private lateinit var inputStatus:TextInputLayout
    private lateinit var inputGender: TextInputLayout
    private lateinit var inputOrigin: TextInputLayout
    private lateinit var inputEpisodeAppearances: TextInputLayout
    private lateinit var character: Character
    private lateinit var result: Result
    private lateinit var database: Database
    private lateinit var buttonSave: Button
    private lateinit var toolbar: MaterialToolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = view.findViewById(R.id.imageView_detailsFragment_character)
        inputName = view.findViewById(R.id.inputLayout_detailsFragment_name)
        inputSpecie = view.findViewById(R.id.inputLayout_detailsFragment_species)
        inputStatus = view.findViewById(R.id.inputLayout_detailsFragment_status)
        inputGender = view.findViewById(R.id.inputLayout_detailsFragment_gender)
        inputOrigin = view.findViewById(R.id.inputLayout_detailsFragment_origin)
        inputEpisodeAppearances = view.findViewById(R.id.inputLayout_detailsFragment_episodeAppearances)
        buttonSave = view.findViewById(R.id.button_detailsFragment_save)
        toolbar = requireActivity().findViewById(R.id.toolbar_mainActivity)

        database = Room.databaseBuilder(
            requireContext(),
            Database::class.java,
            "dbname"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            character = database.characterDao().getCharacterById(args.id)
            CoroutineScope(Dispatchers.Main).launch {
                image.load(character!!.image) {
                    transformations(CircleCropTransformation())
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
                inputName.editText!!.setText(character.name)
                inputSpecie.editText!!.setText(character.species)
                inputStatus.editText!!.setText(character.status)
                inputGender.editText!!.setText(character.gender)
                inputOrigin.editText!!.setText(character.origin)
                inputEpisodeAppearances.editText!!.setText(character.episodesAppearance.toString())
            }
        }

        setListeners()

    }

    private fun setListeners() {
        buttonSave.setOnClickListener {
            val character = character.copy(
                name = inputName.editText!!.text.toString(),
                species = inputSpecie.editText!!.text.toString(),
                status = inputStatus.editText!!.text.toString(),
                gender = inputGender.editText!!.text.toString(),
                origin = inputOrigin.editText!!.text.toString(),
                episodesAppearance = Integer.parseInt(inputEpisodeAppearances.editText!!.text.toString())
            )

            CoroutineScope(Dispatchers.IO).launch {
                database.characterDao().update(character)
            }
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.menu_item_delete -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        database.characterDao().delete(character)
                        CoroutineScope(Dispatchers.Main).launch {
                            requireView().findNavController().navigate(R.id.action_detailsFragment_to_charactersFragment)
                        }
                    }
                    true
                }
                R.id.menu_item_sync -> {
                    getDataApi()
                    CoroutineScope(Dispatchers.IO).launch {
                        database.characterDao().update(character)
                    }
                    true
                }
                else -> true
            }

        }
    }

    private fun getDataApi(){
        RetrofitInstance.api.getCharacter(args.id).enqueue(object: Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful && response.body() != null) {
                    result = response.body()!!
                    image.load(result.image){
                        transformations(CircleCropTransformation())
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }

                    inputName.editText!!.setText(result.name)
                    inputSpecie.editText!!.setText(result.species)
                    inputStatus.editText!!.setText(result.status)
                    inputGender.editText!!.setText(result.gender)
                    inputOrigin.editText!!.setText(result.origin.name)
                    inputEpisodeAppearances.editText!!.setText(result.episode.size.toString())
                    setDataRoom()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                println("Error")
            }

        })
    }

    private fun setDataRoom() {
        var apiCharacter = Character(
            id = result.id,
            image = result.image,
            name = result.name,
            gender = result.gender,
            origin = result.origin.name,
            species = result.species,
            status = result.status,
            episodesAppearance = result.episode.size
        )
        CoroutineScope(Dispatchers.IO).launch {
            database.characterDao().update(apiCharacter)
        }
    }

}