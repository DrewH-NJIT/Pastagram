package com.example.pastagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var etDescription: EditText
    lateinit var buttonTakePicture: Button
    lateinit var buttonSubmitImage: Button
    lateinit var ivPicture: ImageView
    lateinit var pbLoading : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etDescription = findViewById(R.id.etDescription)
        ivPicture = findViewById(R.id.ivPicture)
        pbLoading = findViewById(R.id.pbLoading)

        findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Toast.makeText(this, "how is this photofile null?", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.buttonTakePicture).setOnClickListener {
            onLaunchCamera()
        }

        findViewById<Button>(R.id.buttonLogout).setOnClickListener {
            ParseUser.logOut()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

//        queryPosts()
    }

    private fun submitPost(description: String, user: ParseUser, file: File) {
        val post = Post()

        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))

        pbLoading.visibility = ProgressBar.VISIBLE
        post.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving post")
                e.printStackTrace()
            } else {
                Log.i(TAG, "Post Submitted :-)")
                etDescription.text.clear()
                Toast.makeText(this, "Post Submitted!! :-)", Toast.LENGTH_SHORT).show()
                pbLoading.visibility = ProgressBar.INVISIBLE
            }
        }


    }

    private fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

                val ivPreview: ImageView = findViewById(R.id.ivPicture)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching posts ")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(
                                TAG, "Post: " + post.getDescription() + " , username: " +
                                        post.getUser()?.username
                            )
                        }
                    }
                }
            }
        })
    }

    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    companion object {
        const val TAG = "MainActivity"
    }

}