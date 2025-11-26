package com.ifpr.androidapptemplate.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Item
import com.ifpr.androidapptemplate.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private lateinit var enderecoEditText: EditText
    private lateinit var itemImageView: ImageView
    private var imageUri: Uri? = null


    //TODO("Declare aqui as outras variaveis do tipo EditText que foram inseridas no layout")
    private lateinit var salvarButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var tituloEditText: EditText
    private lateinit var descricaoEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var quantidadeEditText: EditText
    private lateinit var autorEditText: EditText
    private lateinit var editoraEditText: EditText
    private lateinit var precoEditText: EditText



    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        itemImageView = view.findViewById(R.id.image_item)
        salvarButton = view.findViewById(R.id.salvarItemButton)
        selectImageButton = view.findViewById(R.id.button_select_image)
        enderecoEditText = view.findViewById(R.id.enderecoItemEditText)
        tituloEditText = view.findViewById(R.id.tituloItemEditText)
        descricaoEditText = view.findViewById(R.id.descricaoItemEditText)
        dataEditText = view.findViewById(R.id.dataItemEditText)
        categoriaEditText = view.findViewById(R.id.categoriaItemEditText)
        quantidadeEditText = view.findViewById(R.id.quantidadeItemEditText)
        autorEditText = view.findViewById(R.id.autorItemEditText)
        editoraEditText = view.findViewById(R.id.editoraItemEditText)
        precoEditText = view.findViewById(R.id.precoItemEditText)


        //TODO("Capture aqui os outro campos que foram inseridos no layout. Por exemplo, ate
        // o momento so foi capturado o endereco (EditText)")

        auth = FirebaseAuth.getInstance()

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        salvarButton.setOnClickListener {
            salvarItem()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun salvarItem() {
        //TODO("Capture aqui o conteudo que esta nos outros editTexts que foram criados")
        val endereco = enderecoEditText.text.toString().trim()
        val titulo = tituloEditText.text.toString().trim()
        val descricao = descricaoEditText.text.toString().trim()
        val data = dataEditText.text.toString().trim()
        val categoria = categoriaEditText.text.toString().trim()
        val quantidade = quantidadeEditText.text.toString().trim().toIntOrNull()
        val autor = autorEditText.text.toString().trim()
        val editora = editoraEditText.text.toString().trim()
        val preco = precoEditText.text.toString().trim().toDoubleOrNull()



        if (endereco.isEmpty() || imageUri == null) {
            Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT)
                .show()
            return
        }
        uploadImageToFirestore()
    }


    private fun uploadImageToFirestore() {
        if (imageUri != null) {
            val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
                val endereco = enderecoEditText.text.toString().trim()
                val titulo = tituloEditText.text.toString().trim()
                val descricao = descricaoEditText.text.toString().trim()
                val data = dataEditText.text.toString().trim()
                val categoria = categoriaEditText.text.toString().trim()
                val quantidade = quantidadeEditText.text.toString().trim().toIntOrNull()
                val autor = autorEditText.text.toString().trim()
                val editora = editoraEditText.text.toString().trim()
                val preco = precoEditText.text.toString().trim().toDoubleOrNull()


                //TODO("Capture aqui o conteudo que esta nos outros editTexts que foram criados")

                val item = Item(endereco, titulo, descricao, data, categoria, quantidade, autor, editora, preco, base64Image)

                saveItemIntoDatabase(item)
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(itemImageView)
        }
    }

    private fun saveItemIntoDatabase(item: Item) {
        //TODO("Altere a raiz que sera criada no seu banco de dados do realtime database.
        // Renomeie a raiz itens")
        databaseReference = FirebaseDatabase.getInstance().getReference("itens")

        // Cria uma chave unica para o novo item
        val itemId = databaseReference.push().key
        if (itemId != null) {
            databaseReference.child(auth.uid.toString()).child(itemId).setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Item cadastrado com sucesso!", Toast.LENGTH_SHORT)
                        .show()
                    requireActivity().supportFragmentManager.popBackStack()
                }.addOnFailureListener {
                    Toast.makeText(context, "Falha ao cadastrar o item", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Erro ao gerar o ID do item", Toast.LENGTH_SHORT).show()
        }
    }
}