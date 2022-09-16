package com.example.frontendavanzado

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.frontendavanzado.datasource.api.RetrofitInstance
import com.example.frontendavanzado.datasource.model.AllAssetsResponse
import com.example.frontendavanzado.datasource.model.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharactersAdapter(
    private val dataSet: MutableList<Result>,
    private  val characterItemListener: CharacterItemListener
    ): RecyclerView.Adapter<CharactersAdapter.ViewHolder>() {

    interface CharacterItemListener {
        fun onCharacterItemClicked(character: Result, position: Int)
    }

    class ViewHolder(
        private val view: View,
        private val listener: CharacterItemListener
        ): RecyclerView.ViewHolder(view){

        private val imageCharacter: ImageView = view.findViewById(R.id.imageView_itemCharacter_characterImage)
        private val txtName: TextView = view.findViewById(R.id.textView_itemCharacter_name)
        private val txtDescription: TextView = view.findViewById(R.id.textView_itemCharacter_description)
        private val layout: ConstraintLayout = view.findViewById(R.id.layout_itemCharacters)
        private lateinit var character: Result

        fun setData(character: Result){
            this.character = character

            imageCharacter.load(character.image){
                transformations(CircleCropTransformation())
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            }
            txtName.text = character.name
            txtDescription.text = "${character.species} - ${character.status}"

            layout.setOnClickListener{
                listener.onCharacterItemClicked(character, this.adapterPosition)
            }
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_characters, parent, false)

        return ViewHolder(view, characterItemListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataSet[position])
    }
}