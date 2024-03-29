package com.example.fireapplicatioin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide.with
import androidx.core.app.ActivityCompat
import com.example.fireapplicatioin.databinding.FragmentAccountBinding
import com.google.android.gms.tasks.Task
import androidx.annotation.Nullable






class AccountFragment : Fragment() {

    private lateinit var userpic: ImageView

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    private var imageUri: Uri? = null

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentAccountBinding

    private lateinit var dialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        userpic = binding.imgCamera

        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        dialog = LoadingDialog(requireActivity())
        dialog.startLoading()

        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        val userId = user?.uid

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(User::class.java)
                if(userProfile != null){
                    binding.txtEmail.text = userProfile.email
                    binding.txtUsername.text = userProfile.name
                    dialog.stopLoading()
                }
            }
            override fun onCancelled(error: DatabaseError){

            }
        }
        reference.child(userId!!).addListenerForSingleValueEvent(listener)

        with(requireContext()).load(firebaseAuth.currentUser!!.photoUrl)
            .error(R.drawable.ic_acount).into(binding.imgProfile)

        binding.imgCamera.setOnClickListener{
            if (isStorageOk(requireContext())) {
                pickImage()
            } else {
                requestStoragePermission(requireActivity())
            }
        }


        return binding.root
    }


    private fun isStorageOk(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission(activity: Activity?) {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1000
        )
    }

    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                uploadImage(imageUri ?: return)
                with(this).load(imageUri).into(binding.imgProfile)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val exception = result.error
                Log.d("TAG", "onActivityResult: $exception")
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        storageReference.child(firebaseAuth.uid + "/Profile/image_profile.jpg").putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val image: Task<Uri> = taskSnapshot.storage.downloadUrl
                image.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result.toString()
                        val profileChangeRequest = UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(url))
                            .build()
                        firebaseAuth.currentUser?.updateProfile(profileChangeRequest)
                            ?.addOnCompleteListener { profile ->
                                if (profile.isSuccessful) {
                                    val databaseReference =
                                        FirebaseDatabase.getInstance().getReference("Users")
                                    val map: MutableMap<String, Any> =
                                        HashMap()
                                    map["image"] = url
                                    firebaseAuth.uid?.let {
                                        databaseReference.child(it)
                                            .updateChildren(map)
                                    }
                                    Log.d("URL", url)
                                    with(requireContext()).load(url)
                                        .into(
                                            binding.imgProfile
                                        )
                                    Toast.makeText(
                                        requireContext(),
                                        "Image Updated",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Log.d("TAG", "Profile : " + profile.exception)
                                    Toast.makeText(
                                        context,
                                        "Profile : " + profile.exception,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "" + task.exception, Toast.LENGTH_SHORT).show()
                        Log.d("TAG", "onComplete: image url  " + task.exception)
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(requireContext(), "Storage permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}