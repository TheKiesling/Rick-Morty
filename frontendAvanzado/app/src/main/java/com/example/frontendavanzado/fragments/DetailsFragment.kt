package com.example.frontendavanzado.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.frontendavanzado.R
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.model.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment : Fragment(R.layout.fragment_details) {
    val args: DetailsFragmentArgs by navArgs()

    private lateinit var image: ImageView
    private lateinit var txtName:TextView
    private lateinit var txtSpecie:TextView
    private lateinit var txtStatus:TextView
    private lateinit var txtGender: TextView
    private lateinit var txtOrigin: TextView
    private lateinit var txtEpisodeAppearances: TextView
    private lateinit var character: Result

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = view.findViewById(R.id.imageView_detailsFragment_character)
        txtName = view.findViewById(R.id.textView_detailsFragment_name)
        txtSpecie = view.findViewById(R.id.textView_detailsFragment_speciesInfo)
        txtStatus = view.findViewById(R.id.textView_detailsFragment_statusInfo)
        txtGender = view.findViewById(R.id.textView_detailsFragment_genderInfo)
        txtOrigin = view.findViewById(R.id.textView_detailsFragment_originInfo)
        txtEpisodeAppearances = view.findViewById(R.id.textView_detailsFragment_episodeAppearancesInfo)


        RetrofitInstance.api.getCharacter(args.id).enqueue(object: Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful && response.body() != null) {
                    character = response.body()!!
                    image.load(character.image){
                        transformations(CircleCropTransformation())
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }

                    txtName.text = character.name.toString()
                    txtSpecie.text = character.species.toString()
                    txtStatus.text = character.status.toString()
                    txtGender.text = character.gender.toString()
                    txtOrigin.text = character.origin.name.toString()
                    txtEpisodeAppearances.text = character.episode.size.toString()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                println("Error")
            }

        })



    }
}