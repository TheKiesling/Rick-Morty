package com.example.frontendavanzado

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation

class DetailsFragment : Fragment(R.layout.fragment_details) {
    val args: DetailsFragmentArgs by navArgs()

    private lateinit var image: ImageView
    private lateinit var txtName:TextView
    private lateinit var txtSpecie:TextView
    private lateinit var txtStatus:TextView
    private lateinit var txtGender: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image = view.findViewById(R.id.imageView_detailsFragment_character)
        txtName = view.findViewById(R.id.textView_detailsFragment_name)
        txtSpecie = view.findViewById(R.id.textView_detailsFragment_speciesInfo)
        txtStatus = view.findViewById(R.id.textView_detailsFragment_statusInfo)
        txtGender = view.findViewById(R.id.textView_detailsFragment_genderInfo)


        image.load(args.urlImage){
            transformations(CircleCropTransformation())
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
        }

        txtName.text = args.name.toString()
        txtSpecie.text = args.specie.toString()
        txtStatus.text = args.status.toString()
        txtGender.text = args.gender.toString()
    }
}