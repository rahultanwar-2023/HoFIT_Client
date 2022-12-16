package com.hofit.hofitclient.ui.fragment.registration_outlet_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hofit.hofitclient.databinding.FragmentOutletImagesBinding

class OutletImages : Fragment() {

    private lateinit var binding: FragmentOutletImagesBinding
    private lateinit var auth: String
    private lateinit var fireBase: DocumentReference
    private lateinit var mStorageReference: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutletImagesBinding.inflate(inflater, container, false)
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)
        mStorageReference =
            Firebase.storage.reference.child("outlet/").child(auth).child("outlet_images/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var imageUri: Uri? = null
        val mGetImageUri = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageUri = it
                val imageUriName = getFileName(requireContext(), imageUri!!)
                binding.edUploadOutletImage.editText?.setText(imageUriName)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadOutletImage.setOnClickListener {
            mGetImageUri.launch("image/*")
        }

        var imageUri1: Uri? = null
        val mGetImageUri1 = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageUri1 = it
                val imageUriName1 = getFileName(requireContext(), imageUri1!!)
                binding.edUploadOutletImage1.editText?.setText(imageUriName1)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadOutletImage1.setOnClickListener {
            mGetImageUri1.launch("image/*")
        }

        var imageUri2: Uri? = null
        val mGetImageUri2 = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageUri2 = it
                val imageUriName2 = getFileName(requireContext(), imageUri2!!)
                binding.edUploadOutletImage2.editText?.setText(imageUriName2)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadOutletImage2.setOnClickListener {
            mGetImageUri2.launch("image/*")
        }

        var imageUri3: Uri? = null
        val mGetImageUri3 = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageUri3 = it
                val imageUriName3 = getFileName(requireContext(), imageUri3!!)
                binding.edUploadOutletImage3.editText?.setText(imageUriName3)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadOutletImage3.setOnClickListener {
            mGetImageUri3.launch("image/*")
        }

        var imageUri4: Uri? = null
        val mGetImageUri4 = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageUri4 = it
                val imageUriName4 = getFileName(requireContext(), imageUri4!!)
                binding.edUploadOutletImage4.editText?.setText(imageUriName4)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadOutletImage4.setOnClickListener {
            mGetImageUri4.launch("image/*")
        }

        binding.btnSaveOutletImages.setOnClickListener {

            binding.btnSaveOutletImages.visibility = View.INVISIBLE
            binding.progressOutletImages.visibility = View.VISIBLE

            val outletImage = binding.edUploadOutlet.text.toString().trim()
            val outletImage1 = binding.edUploadOutlet1.text.toString().trim()
            val outletImage2 = binding.edUploadOutlet2.text.toString().trim()
            val outletImage3 = binding.edUploadOutlet3.text.toString().trim()
            val outletImage4 = binding.edUploadOutlet4.text.toString().trim()

            if (outletImage.isNotEmpty() && outletImage1.isNotEmpty() && outletImage2.isNotEmpty() && outletImage3.isNotEmpty() && outletImage4.isNotEmpty()) {
                uploadImage(imageUri)
                uploadImage1(imageUri1)
                uploadImage2(imageUri2)
                uploadImage3(imageUri3)
                uploadImage4(imageUri4)

                Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT).show()

                binding.progressOutletImages.visibility = View.INVISIBLE
                binding.btnSaveOutletImages.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Upload at-least 5 Images", Toast.LENGTH_SHORT).show()
                binding.progressOutletImages.visibility = View.INVISIBLE
                binding.btnSaveOutletImages.visibility = View.VISIBLE
            }

        }

    }

    private fun uploadImage(imageUri: Uri?) {
        val sRef =
            mStorageReference.child(System.currentTimeMillis().toString())
        sRef.putFile(imageUri!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    sRef.downloadUrl.addOnSuccessListener { uri ->

                        val outletImageUrl = hashMapOf(
                            "outlet_image" to uri.toString()
                        )
                        fireBase.collection("outlet_images").document()
                            .set(outletImageUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadImage1(imageUri1: Uri?) {
        val sRef =
            mStorageReference.child(System.currentTimeMillis().toString())
        sRef.putFile(imageUri1!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    sRef.downloadUrl.addOnSuccessListener { uri ->

                        val outletImageUrl = hashMapOf(
                            "outlet_image" to uri.toString()
                        )
                        fireBase.collection("outlet_images").document()
                            .set(outletImageUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadImage2(imageUri2: Uri?) {
        val sRef =
            mStorageReference.child(System.currentTimeMillis().toString())
        sRef.putFile(imageUri2!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    sRef.downloadUrl.addOnSuccessListener { uri ->

                        val outletImageUrl = hashMapOf(
                            "outlet_image" to uri.toString()
                        )
                        fireBase.collection("outlet_images").document()
                            .set(outletImageUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadImage3(imageUri3: Uri?) {
        val sRef =
            mStorageReference.child(System.currentTimeMillis().toString())
        sRef.putFile(imageUri3!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    sRef.downloadUrl.addOnSuccessListener { uri ->

                        val outletImageUrl = hashMapOf(
                            "outlet_image" to uri.toString()
                        )
                        fireBase.collection("outlet_images").document()
                            .set(outletImageUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadImage4(imageUri4: Uri?) {
        val sRef =
            mStorageReference.child(System.currentTimeMillis().toString())
        sRef.putFile(imageUri4!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    sRef.downloadUrl.addOnSuccessListener { uri ->

                        val outletImageUrl = hashMapOf(
                            "outlet_image" to uri.toString()
                        )
                        fireBase.collection("outlet_images").document()
                            .set(outletImageUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }

        return uri.path!!.substring(uri.path!!.lastIndexOf('/') + 1)
    }

}