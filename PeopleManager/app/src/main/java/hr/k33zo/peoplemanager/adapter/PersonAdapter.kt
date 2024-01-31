package hr.k33zo.peoplemanager.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.k33zo.peoplemanager.App
import hr.k33zo.peoplemanager.Navigable
import hr.k33zo.peoplemanager.PERSON_ID
import hr.k33zo.peoplemanager.R
import hr.k33zo.peoplemanager.dao.Person
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PersonAdapter(
    private val context: Context,
    private val people: MutableList<Person>,
    private val navigable: Navigable
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvRole = itemView.findViewById<TextView>(R.id.tvRole)
        fun bind(person: Person){
            tvTitle.text = person.toString()
            tvRole.text = person.role ?: ""
            Picasso.get()
                .load(File(person.picturePath?:""))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50,5))
                .into(ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.person, parent, false)
        )
    }

    override fun getItemCount()= people.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val person = people[position]
        holder.ivDelete.setOnLongClickListener{
            //delete
            GlobalScope.launch (Dispatchers.Main) {
                //main thread
                withContext(Dispatchers.IO) {
                    //bg thread
                    (context?.applicationContext as App)
                        .getPersonDao()
                        .delete(person)
                    File(person.picturePath).delete()
                }
                people.removeAt(position)
                notifyDataSetChanged()
            }
            true
        }

        holder.itemView.setOnClickListener {
            //edit
            navigable.navigate(
                Bundle().apply {
                    putLong(PERSON_ID, person._id!!)
                }
            )

        }

        holder.bind(person)
    }
}