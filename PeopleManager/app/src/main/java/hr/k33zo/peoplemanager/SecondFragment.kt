package hr.k33zo.peoplemanager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hr.k33zo.peoplemanager.dao.Person
import hr.k33zo.peoplemanager.dao.Role
import hr.k33zo.peoplemanager.databinding.FragmentSecondBinding
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val PERSON_ID = "hr.k33zo.person_id"
private const val MIME = "image/*"
private const val JPG_EXTENSION = ".jpg"

class SecondFragment : Fragment() {

    private  lateinit var person: Person
    private var _binding: FragmentSecondBinding? = null
    private lateinit var roleSpinner: Spinner

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roleSpinner = binding.spinnerRole


        fetchPerson()
        setupListeners()

    }

    private fun fetchPerson() {
        val id = arguments?.getLong(PERSON_ID)
        if (id!=null){
            GlobalScope.launch (Dispatchers.Main) {
                //main thread
                person = withContext(Dispatchers.IO) {
                    //bg thread
                    (context?.applicationContext as App)
                        .getPersonDao()
                        .getPerson(id) ?: Person()
                }
                bindPerson()
            }
        }else {
            person = Person()
            bindPerson()
            }
    }

    private fun bindPerson() {
        Picasso.get()
            .load(File(person.picturePath?:""))
            .error(R.mipmap.ic_launcher)
            .transform(RoundedCornersTransformation(50,5))
            .into(binding.ivImage)
        binding.tvDate.text = person.birthDate.format(DateTimeFormatter.ISO_DATE)
        binding.etFirst.setText(person.firstName ?:"")
        binding.etLast.setText(person.lastName ?:"")

        val roleValues = Role.getValuesAsArray()
        val roleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roleValues)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = roleAdapter

        val rolePosition = person.role?.let { roleValues.indexOf(it) } ?: 0
        roleSpinner.setSelection(rolePosition)


    }

    private fun setupListeners() {
        binding.etFirst.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                person.firstName=text?.toString()?.trim()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.etLast.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                person.lastName=text?.toString()?.trim()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.tvDate.setOnClickListener{
            handleDate()
        }
        binding.ivImage.setOnLongClickListener{
            handleImage()
            true
        }
        binding.btnCommit.setOnClickListener {
            if (formValid()) commit()
        }

    }

    private fun handleDate() {
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                person.birthDate= LocalDate.of(y,m+1,d)
                bindPerson()
            },
            person.birthDate.year,
            person.birthDate.monthValue -1,
            person.birthDate.dayOfMonth
        ).show()
    }

    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME
            callback.launch(this)
        }
    }

    private val callback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK && it.data != null ){
            val dir = context?.applicationContext?.getExternalFilesDir(null)
            val file = File(dir, File.separator.toString()+UUID.randomUUID().toString()+JPG_EXTENSION)

            context?.contentResolver?.openInputStream(it.data?.data as Uri).use {inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    outputStream.write(bos.toByteArray())
                    person.picturePath = file.absolutePath
                    bindPerson()
                }

            }
        }
    }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etFirst, binding.etLast).forEach {
            if (it.text.trim().isNullOrEmpty()){
                ok = false
                it.error = getString(R.string.please_insert)
            }
        }
        return ok && person.picturePath != null
    }

    private fun commit() {
        GlobalScope.launch(Dispatchers.Main) {
            //main thread
            withContext(Dispatchers.IO) {
                //bg thread
                person.role = roleSpinner.selectedItem.toString()
                if (person._id == null)
                    (context?.applicationContext as App)
                        .getPersonDao()
                        .insert(person)
                else
                    (context?.applicationContext as App)
                        .getPersonDao()
                        .update(person)
            }
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}