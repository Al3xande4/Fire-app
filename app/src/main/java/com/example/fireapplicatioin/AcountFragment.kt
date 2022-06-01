package com.example.fireapplicatioin

import android.Manifest;
import android.app.Activity.RESULT_OK
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide.with
import com.bumptech.glide.disklrucache.DiskLruCache
import com.example.fireapplicatioin.databinding.FragmentAcountBinding
import com.firebase.ui.database.paging.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView


class AcountFragment : Fragment() {

    lateinit var userpic: ImageView

    private val GalleryPick = 1
    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 200
    private val IMAGEPICK_GALLERY_REQUEST = 300
    private val IMAGE_PICKCAMERA_REQUEST = 400
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>

    var imageuri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding = DataBindingUtil.inflate<FragmentAcountBinding>(inflater, R.layout.fragment_acount, container, false)

        userpic = binding.imgCamera

        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.imgCamera.setOnClickListener{ showImagePicDialog()}

        val user = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        val userId = user!!.uid

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(User::class.java)
                if(userProfile != null){
                    binding.txtEmail.text = userProfile.email
                    binding.txtUsername.text = userProfile.name
                }
            }
            override fun onCancelled(error: DatabaseError){

            }
        }
        reference.child(userId).addListenerForSingleValueEvent(listener)

//        binding.imgCamera.setOnClickListener { camera ->
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                pickImage()
//            } else {
//                requestPermissions(
//                    arrayOf(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ), 1000
//                )
//            }
//        }
        return binding.root
    }

    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }

    private fun showImagePicDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pick Image From")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                if (!checkCameraPermission()!!) {
                    requestCameraPermission()
                } else {
                    pickFromGallery()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        })
        builder.create().show()
    }

    // checking storage permissions
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Requesting  gallery permission
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    // checking camera permissions
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    // Requesting camera permission
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    // Requesting camera and gallery
    // permission if not given
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please Enable Camera and Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.size > 0) {
                    val writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeStorageaccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(requireContext(), "Please Enable Storage Permissions", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    // Here we will pick image from gallery or camera
    private fun pickFromGallery() {
        CropImage.activity().start(requireActivity())
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                with(requireContext()).load(resultUri).into(userpic)
            }
        }
    }
}