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
import com.hofit.hofitclient.databinding.FragmentDocumentUploadBinding


class DocumentUpload : Fragment() {

    private lateinit var binding: FragmentDocumentUploadBinding
    private lateinit var currentUser: String
    private lateinit var fireBase: DocumentReference
    private lateinit var mStorageReference: StorageReference

    //PDF Names
    private lateinit var panCardName: String
    private lateinit var gstCertificateName: String
    private lateinit var aadhaarName: String
    private lateinit var cancelChequeName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentUploadBinding.inflate(inflater, container, false)
        currentUser = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(currentUser).collection("outlet_document")
            .document("document")

        fireBase.get()
            .addOnSuccessListener { data ->
                val outID = data.getString("outlet_id").toString()
                if (outID != "") {
                    binding.btnSaveOutletDocument.isEnabled = false
                    binding.btnEditOutletDocument.isEnabled = true
                }
            }

        mStorageReference =
            Firebase.storage.reference.child("outlet/").child(currentUser)
                .child("outlet_documents/")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromFireStore()
        saveOutletDocument()

    }

    private fun saveOutletDocument() {

        var panCardUri: Uri? = null
        val mGetPanCardUri = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                panCardUri = it
                panCardName = getFileName(requireContext(), panCardUri!!)
                binding.edUploadBPanCard.editText?.setText(panCardName)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadPanCard.setOnClickListener {
            mGetPanCardUri.launch("application/pdf")
        }

        var gstCertificateUri: Uri? = null
        val mGetGstCertificateUri =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                try {
                    gstCertificateUri = it
                    gstCertificateName = getFileName(requireContext(), gstCertificateUri!!)
                    binding.edUploadGSTCertificate.editText?.setText(gstCertificateName)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.btnUploadGSTCertificate.setOnClickListener {
            mGetGstCertificateUri.launch("application/pdf")
        }

        var aadhaarUri: Uri? = null
        val mGetAadhaarUri = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                aadhaarUri = it
                aadhaarName = getFileName(requireContext(), aadhaarUri!!)
                binding.edUploadAadhaarCopy.editText?.setText(aadhaarName)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadAadhaarCopy.setOnClickListener {
            mGetAadhaarUri.launch("application/pdf")
        }

        var cancelChequeUri: Uri? = null
        val mGetCancelChequeUri = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                cancelChequeUri = it
                cancelChequeName = getFileName(requireContext(), cancelChequeUri!!)
                binding.edUploadCancelCheque.editText?.setText(cancelChequeName)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnUploadCancelCheque.setOnClickListener {
            mGetCancelChequeUri.launch("application/pdf")
        }

        binding.btnSaveOutletDocument.setOnClickListener {

            binding.btnSaveOutletDocument.visibility = View.INVISIBLE
            binding.btnEditOutletDocument.visibility = View.INVISIBLE
            binding.progressOutletDocument.visibility = View.VISIBLE

            val outletBusinessPanCard = binding.edUploadBPanCard1.text.toString().trim()
            val outletBusinessGST = binding.edUploadGSTCertificate1.text.toString().trim()
            val outletBusinessAadhaar = binding.edUploadAadhaarCopy1.text.toString().trim()
            val outletBusinessCheque = binding.edUploadCancelCheque1.text.toString().trim()

            val outletBusinessAccNumber =
                binding.edBusinessAccountNumber1.text.toString().trim()
            val outletBusinessAccIFSC =
                binding.edBusinessAccIFSCCode1.text.toString().trim()
            val outletBusinessAccHolderName =
                binding.edBusinessAccHolderName1.text.toString().trim()

            if (outletBusinessPanCard.isNotEmpty() && outletBusinessGST.isNotEmpty() && outletBusinessAadhaar.isNotEmpty() && outletBusinessCheque.isNotEmpty()) {
                if (outletBusinessAccNumber.isNotEmpty() && outletBusinessAccIFSC.isNotEmpty() && outletBusinessAccHolderName.isNotEmpty()) {


                    val outletRestDetails = hashMapOf(
                        "outlet_business_panCard" to "",
                        "outlet_business_panCard_name" to outletBusinessPanCard,
                        "outlet_business_gst" to "",
                        "outlet_business_gst_name" to outletBusinessGST,
                        "outlet_business_aadhaar" to "",
                        "outlet_business_aadhaar_name" to outletBusinessAadhaar,
                        "outlet_business_cheque" to "",
                        "outlet_business_cheque_name" to outletBusinessCheque,
                        "outlet_business_acc_number" to outletBusinessAccNumber,
                        "outlet_business_acc_IFSC" to outletBusinessAccIFSC,
                        "outlet_business_acc_holderName" to outletBusinessAccHolderName
                    )

                    fireBase
                        .set(outletRestDetails, SetOptions.merge())
                        .addOnSuccessListener {
                            clearED()
                            binding.btnSaveOutletDocument.visibility = View.VISIBLE
                            binding.btnEditOutletDocument.visibility = View.VISIBLE
                            binding.progressOutletDocument.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT)
                                .show()
                            binding.btnSaveOutletDocument.isEnabled = true
                            binding.btnEditOutletDocument.isEnabled = false
                        }

                    uploadPanCardFile(panCardUri!!)
                    uploadGSTFile(gstCertificateUri!!)
                    uploadAadhaarFile(aadhaarUri!!)
                    uploadChequeFile(cancelChequeUri!!)


                } else {
                    binding.btnSaveOutletDocument.visibility = View.VISIBLE
                    binding.btnEditOutletDocument.visibility = View.VISIBLE
                    binding.progressOutletDocument.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Fill required details", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.btnSaveOutletDocument.visibility = View.VISIBLE
                binding.btnEditOutletDocument.visibility = View.VISIBLE
                binding.progressOutletDocument.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Upload required document", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.btnEditOutletDocument.setOnClickListener {

            binding.btnSaveOutletDocument.visibility = View.INVISIBLE
            binding.btnEditOutletDocument.visibility = View.INVISIBLE
            binding.progressOutletDocument.visibility = View.VISIBLE

            val outletBusinessPanCard = binding.edUploadBPanCard1.text.toString().trim()
            val outletBusinessGST = binding.edUploadGSTCertificate1.text.toString().trim()
            val outletBusinessAadhaar = binding.edUploadAadhaarCopy1.text.toString().trim()
            val outletBusinessCheque = binding.edUploadCancelCheque1.text.toString().trim()

            val outletBusinessAccNumber =
                binding.edBusinessAccountNumber1.text.toString().trim()
            val outletBusinessAccIFSC =
                binding.edBusinessAccIFSCCode1.text.toString().trim()
            val outletBusinessAccHolderName =
                binding.edBusinessAccHolderName1.text.toString().trim()

            if (outletBusinessPanCard.isNotEmpty() && outletBusinessGST.isNotEmpty() && outletBusinessAadhaar.isNotEmpty() && outletBusinessCheque.isNotEmpty()) {
                if (outletBusinessAccNumber.isNotEmpty() && outletBusinessAccIFSC.isNotEmpty() && outletBusinessAccHolderName.isNotEmpty()) {


                    val outletRestDetails = hashMapOf(
                        "outlet_business_panCard" to "",
                        "outlet_business_panCard_name" to outletBusinessPanCard,
                        "outlet_business_gst" to "",
                        "outlet_business_gst_name" to outletBusinessGST,
                        "outlet_business_aadhaar" to "",
                        "outlet_business_aadhaar_name" to outletBusinessAadhaar,
                        "outlet_business_cheque" to "",
                        "outlet_business_cheque_name" to outletBusinessCheque,
                        "outlet_business_acc_number" to outletBusinessAccNumber,
                        "outlet_business_acc_IFSC" to outletBusinessAccIFSC,
                        "outlet_business_acc_holderName" to outletBusinessAccHolderName
                    )

                    fireBase
                        .set(outletRestDetails, SetOptions.merge())
                        .addOnSuccessListener {
                            clearED()
                            binding.btnSaveOutletDocument.visibility = View.VISIBLE
                            binding.btnEditOutletDocument.visibility = View.VISIBLE
                            binding.progressOutletDocument.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT)
                                .show()
                        }

                    editUploadPanCardFile(panCardUri!!)
                    editUploadGSTFile(gstCertificateUri!!)
                    editUploadAadhaarFile(aadhaarUri!!)
                    editUploadChequeFile(cancelChequeUri!!)


                } else {
                    binding.btnSaveOutletDocument.visibility = View.VISIBLE
                    binding.btnEditOutletDocument.visibility = View.VISIBLE
                    binding.progressOutletDocument.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Fill required details", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.btnSaveOutletDocument.visibility = View.VISIBLE
                binding.btnEditOutletDocument.visibility = View.VISIBLE
                binding.progressOutletDocument.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Upload required document", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun clearED() {
        binding.edUploadBPanCard.editText?.setText("")
        binding.edUploadGSTCertificate.editText?.setText("")
        binding.edUploadAadhaarCopy.editText?.setText("")
        binding.edUploadCancelCheque.editText?.setText("")
    }


    private fun uploadPanCardFile(data: Uri?) {
        val mStorageReference =
            mStorageReference.child("outlet_business_panCard" + "." + "pdf")
        mStorageReference.putFile(data!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val outletPanCardUrl = hashMapOf("outlet_business_panCard" to uri)
                        fireBase.set(outletPanCardUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadGSTFile(data: Uri?) {
        val mStorageReference =
            mStorageReference.child("outlet_business_gst" + "." + "pdf")
        mStorageReference.putFile(data!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val outletGSTUrl = hashMapOf("outlet_business_gst" to uri)
                        fireBase.set(outletGSTUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadAadhaarFile(data: Uri?) {
        val mStorageReference =
            mStorageReference.child("outlet_business_aadhaar" + "." + "pdf")
        mStorageReference.putFile(data!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val outletAadhaarUrl = hashMapOf("outlet_business_aadhaar" to uri)
                        fireBase.set(outletAadhaarUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun uploadChequeFile(data: Uri?) {
        val mStorageReference =
            mStorageReference.child("outlet_business_cheque" + "." + "pdf")
        mStorageReference.putFile(data!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val outletChequeUrl = hashMapOf("outlet_business_cheque" to uri)
                        fireBase.set(outletChequeUrl, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun editUploadPanCardFile(data: Uri?) {
        val docPanRef: StorageReference =
            Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_panCard.pdf")
        docPanRef.delete()
            .addOnSuccessListener {
                val docPanRef1: StorageReference =
                    Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_panCard.pdf")
                docPanRef1.putFile(data!!)
                    .addOnCompleteListener { taskSnapshot ->
                        if (taskSnapshot.isSuccessful) {
                            docPanRef1.downloadUrl.addOnSuccessListener { uri ->
                                val outletPanCardUrl = hashMapOf("outlet_business_panCard" to uri)
                                fireBase.set(outletPanCardUrl, SetOptions.merge())
                            }
                        }
                    }
                    .addOnFailureListener {}
            }
    }

    private fun editUploadGSTFile(data: Uri?) {
        val docGstRef: StorageReference =
            Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_gst.pdf")
        docGstRef.delete()
            .addOnSuccessListener {
                val docGstRef1: StorageReference =
                    Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_gst.pdf")
                docGstRef1.putFile(data!!)
                    .addOnCompleteListener { taskSnapshot ->
                        if (taskSnapshot.isSuccessful) {
                            docGstRef1.downloadUrl.addOnSuccessListener { uri ->
                                val outletGSTUrl = hashMapOf("outlet_business_gst" to uri)
                                fireBase.set(outletGSTUrl, SetOptions.merge())
                            }
                        }
                    }
                    .addOnFailureListener {}
            }
    }

    private fun editUploadAadhaarFile(data: Uri?) {
        val docAadhaarRef: StorageReference =
            Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_aadhaar.pdf")
        docAadhaarRef.delete()
            .addOnSuccessListener {
                val docAadhaarRef1: StorageReference =
                    Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_aadhaar.pdf")
                docAadhaarRef1.putFile(data!!)
                    .addOnCompleteListener { taskSnapshot ->
                        if (taskSnapshot.isSuccessful) {
                            docAadhaarRef1.downloadUrl.addOnSuccessListener { uri ->
                                val outletAadhaarUrl = hashMapOf("outlet_business_aadhaar" to uri)
                                fireBase.set(outletAadhaarUrl, SetOptions.merge())
                            }
                        }
                    }
                    .addOnFailureListener {}
            }

    }

    private fun editUploadChequeFile(data: Uri?) {
        val docChequeRef: StorageReference =
            Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_cheque.pdf")
        docChequeRef.delete()
            .addOnSuccessListener {
                val docChequeRef1: StorageReference =
                    Firebase.storage.reference.child("outlet/$currentUser/outlet_documents/outlet_business_cheque.pdf")
                docChequeRef1.putFile(data!!)
                    .addOnCompleteListener { taskSnapshot ->
                        if (taskSnapshot.isSuccessful) {
                            docChequeRef1.downloadUrl.addOnSuccessListener { uri ->
                                val outletChequeUrl = hashMapOf("outlet_business_cheque" to uri)
                                fireBase.set(outletChequeUrl, SetOptions.merge())
                            }
                        }
                    }
                    .addOnFailureListener {}
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

    private fun getDataFromFireStore() {
        fireBase.get()
            .addOnSuccessListener { data ->
                val outBusinessAccNumber = data.getString("outlet_business_acc_number").toString()
                binding.edBusinessAccountNumber1.setText(outBusinessAccNumber)
                val outBusinessAccNumberIFSC = data.getString("outlet_business_acc_IFSC").toString()
                binding.edBusinessAccIFSCCode1.setText(outBusinessAccNumberIFSC)
                val outBusinessAccNumberHolderName =
                    data.getString("outlet_business_acc_holderName").toString()
                binding.edBusinessAccHolderName1.setText(outBusinessAccNumberHolderName)
            }
    }

}