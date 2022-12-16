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
import com.hofit.hofitclient.databinding.FragmentTrainerCoachDetailsBinding


class TrainerCoachDetails : Fragment() {

    private lateinit var binding: FragmentTrainerCoachDetailsBinding
    private lateinit var auth: String
    private lateinit var fireBase: DocumentReference
    private lateinit var mStorageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainerCoachDetailsBinding.inflate(inflater, container, false)
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)
        mStorageReference =
            Firebase.storage.reference.child("outlet/").child(auth).child("outlet_coach_document/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var coachCertificateUri: Uri? = null

        val mGetCoachCertificateUri =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                try {
                    coachCertificateUri = it
                    val coachCertificateUriName =
                        getFileName(requireContext(), coachCertificateUri!!)
                    binding.edUploadCoachCertificate.editText?.setText(coachCertificateUriName)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.btnUploadCoachCertificate.setOnClickListener {
            mGetCoachCertificateUri.launch("application/pdf")
        }

        binding.btnSaveOutletCoach.setOnClickListener {

            binding.btnSaveOutletCoach.visibility = View.INVISIBLE
            binding.progressOutletCoach.visibility = View.VISIBLE

            saveCoachDetails(coachCertificateUri)
        }

    }

    private fun saveCoachDetails(coachCertificateUri: Uri?) {
        val coachName = binding.edCoachName.editText.toString().trim()
        val coachCertificate = binding.edUploadCoachCertificate.editText.toString().trim()

        if (coachName.isNotEmpty() && coachCertificate.isNotEmpty()) {
            val sRef =
                mStorageReference.child(System.currentTimeMillis().toString() + "." + "pdf")
            sRef.putFile(coachCertificateUri!!)
                .addOnCompleteListener { taskSnapshot ->
                    if (taskSnapshot.isSuccessful) {
                        sRef.downloadUrl.addOnSuccessListener { uri ->
                            val outletPanCardUrl = hashMapOf(
                                "outlet_coach_name" to coachName,
                                "outlet_coach_certificate" to uri
                            )
                            fireBase.collection("outlet_coach_details").document()
                                .set(outletPanCardUrl, SetOptions.merge())
                            Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT)
                                .show()
                            binding.progressOutletCoach.visibility = View.INVISIBLE
                            binding.btnSaveOutletCoach.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener {}

        } else {
            Toast.makeText(context, "Fill required details", Toast.LENGTH_SHORT).show()
        }
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